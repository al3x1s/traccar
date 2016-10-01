/*
 * Copyright 2016 alexis.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar;

import java.sql.SQLException;
import org.traccar.helper.Log;
import org.traccar.model.Position;
import org.traccar.model.VehicleAlert;
import java.util.Collection;
import org.traccar.database.GeofenceManager;
import org.traccar.model.AlertLog;
import org.traccar.model.User;
import org.traccar.model.Vehicle;
import org.traccar.notification.NotificationMail;

/**
 *
 * @author alexis
 */
public class VehicleAlertHandler extends BaseDataHandler {

    public VehicleAlertHandler() {

    }

    private void sendNotification(Collection<User> users, AlertLog alertLog, Position position) {
        for (User user : users) {
            NotificationMail.sendMailAsync(user.getId(), alertLog, position);
        }
    }

    private void sendNotification(long userId, AlertLog alertLog, Position position) {
        NotificationMail.sendMailAsync(userId, alertLog, position);
    }

    private Position getLastPosition(long deviceId) {
        if (Context.getIdentityManager() != null) {
            return Context.getIdentityManager().getLastPosition(deviceId);
        }
        return null;
    }

    private void handleGeofenceAlert(VehicleAlert va, Position position, Collection<User> users) {
        Position oldPosition = getLastPosition(position.getDeviceId());

        GeofenceManager geofenceManager = Context.getGeofenceManager();
        boolean isIn = geofenceManager.isDeviceInGeofence(va.getGeofenceid(), position);
        boolean wasIn = geofenceManager.isDeviceInGeofence(va.getGeofenceid(), oldPosition);

        if (isIn && !wasIn
                && (va.getTypeId() == VehicleAlert.TYPE_GEOFENCE_ENTER
                || va.getTypeId() == VehicleAlert.TYPE_GEOFENCE_ENTER_EXIT)) {
            AlertLog al = new AlertLog(va.getVehicleId(), va.getAlertId(),
                    position.getId(), VehicleAlert.TYPE_GEOFENCE_ENTER, va.getGeofenceid());
            sendNotification(users, al, position);
        } else if (!isIn && wasIn
                && (va.getTypeId() == VehicleAlert.TYPE_GEOFENCE_EXIT
                || va.getTypeId() == VehicleAlert.TYPE_GEOFENCE_ENTER_EXIT)) {
            AlertLog al = new AlertLog(va.getVehicleId(), va.getAlertId(),
                    position.getId(), VehicleAlert.TYPE_GEOFENCE_EXIT, va.getGeofenceid());
            sendNotification(users, al, position);
        }
    }

    private void handlePanicAlert(VehicleAlert va, Position position, Collection<User> users) {
        Object alarm = position.getAttributes().get(Position.KEY_ALARM);
        if (alarm != null && alarm.equals(Position.ALARM_SOS)) {
            AlertLog al = new AlertLog(va.getVehicleId(), va.getAlertId(),
                position.getId(), VehicleAlert.TYPE_PANIC);
            sendNotification(users, al, position);
        }
    }

    private void handlePanicAlertAdmin(Position position) {
        try {
            Object alarm = position.getAttributes().get(Position.KEY_ALARM);
            if (alarm != null && alarm.equals(Position.ALARM_SOS)) {
                Vehicle v = Context.getDataManager().getVehicle(position.getDeviceId());
                AlertLog al = new AlertLog(v.getId(), 0, position.getId(), VehicleAlert.TYPE_PANIC);
                sendNotification(2, al, position);
            }
        } catch (SQLException error) {
            Log.warning(error);
        }
    }

    private void handleSpeedAlert(VehicleAlert va, Position position, Collection<User> users) {
        boolean notRepeat = true;
        double speed = position.getSpeed();
        double speedLimit = va.getMaxSpeed();
        if (speedLimit == 0) {
            return;
        }

        double oldSpeed = 0;
        if (notRepeat) {
            Position lastPosition = getLastPosition(position.getDeviceId());
            if (lastPosition != null) {
                oldSpeed = lastPosition.getSpeed();
            }
        }

        if (speed > speedLimit && oldSpeed <= speedLimit) {
            Log.info("alerta de velocidad aqui");
            AlertLog al = new AlertLog(va.getVehicleId(), va.getAlertId(),
                    position.getId(), VehicleAlert.TYPE_SPEED);
            sendNotification(users, al, position);
        }
    }

    private void handleStopAlert(VehicleAlert va, Position position, Collection<User> users) {

    }

    private void handleOdometerAlert(VehicleAlert va, Position position, Collection<User> users) {
        double totalDistance = 0.0;
        double initialOdometer = va.getInitialOdometer();
        double odometerAlert = va.getOdometro();
        if (position.getAttributes().containsKey(Position.KEY_TOTAL_DISTANCE)) {
            totalDistance = ((Number) position.getAttributes().get(Position.KEY_TOTAL_DISTANCE)).doubleValue();
            if ((totalDistance - initialOdometer) >= odometerAlert) {
                Log.info("alerta de odometro aqui");
                AlertLog al = new AlertLog(va.getVehicleId(), va.getAlertId(),
                        position.getId(), VehicleAlert.TYPE_ODOMETER, totalDistance);
                sendNotification(users, al, position);
            }
        }
    }

    @Override
    protected Position handlePosition(Position position) {
        Collection<VehicleAlert> vehicleAlert = Context.getVehicleAlertManager()
                .getVehicleAlertByDeviceId(position.getDeviceId());
        handlePanicAlertAdmin(position);
        for (VehicleAlert va : vehicleAlert) {
            try {
                Collection<User> users = Context.getDataManager().getUserAlert(va.getAlertId());
                switch (va.getTypeId()) {
                    case VehicleAlert.TYPE_PANIC:
                        handlePanicAlert(va, position, users);
                        break;
                    case VehicleAlert.TYPE_SPEED:
                        handleSpeedAlert(va, position, users);
                        break;
                    case VehicleAlert.TYPE_STOP:
                        handleStopAlert(va, position, users);
                        break;
                    case VehicleAlert.TYPE_ODOMETER:
                        handleOdometerAlert(va, position, users);
                        break;
                    case VehicleAlert.TYPE_GEOFENCE_EXIT:
                    case VehicleAlert.TYPE_GEOFENCE_ENTER:
                    case VehicleAlert.TYPE_GEOFENCE_ENTER_EXIT:
                        handleGeofenceAlert(va, position, users);
                        break;
                    default:
                        break;
                }
            } catch (SQLException error) {
                Log.warning(error);
            }
        }

        return position;
    }
}
