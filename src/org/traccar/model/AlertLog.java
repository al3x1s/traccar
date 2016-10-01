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
package org.traccar.model;

/**
 *
 * @author alexis
 */
public class AlertLog {

    public AlertLog(long vehicleId, long alertId, long positionId, int typeId) {
        this.vehicleId = vehicleId;
        this.alertId = alertId;
        this.positionId = positionId;
        this.typeId = typeId;
    }

    public AlertLog(long vehicleId, long alertId, long positionId, int typeId, long geofenceId) {
        this.vehicleId = vehicleId;
        this.alertId = alertId;
        this.positionId = positionId;
        this.typeId = typeId;
        this.geofenceId = geofenceId;
    }

    public AlertLog(long vehicleId, long alertId, long positionId, int typeId, double odometer) {
        this.vehicleId = vehicleId;
        this.alertId = alertId;
        this.positionId = positionId;
        this.typeId = typeId;
        this.odometer = odometer;
    }

    public AlertLog() {

    }

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private long vehicleId;

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        this.vehicleId = vehicleId;
    }

    private long alertId;

    public long getAlertId() {
        return alertId;
    }

    public void setAlertId(long alertId) {
        this.alertId = alertId;
    }

    private long positionId;

    public long getPositionId() {
        return positionId;
    }

    public void setPositionId(long positionId) {
        this.positionId = positionId;
    }

    private int typeId;

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    private long geofenceId;

    public long getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(long geofenceId) {
        this.geofenceId = geofenceId;
    }

    private double odometer;

    public double getOdometer() {
        return odometer;
    }

    public void setOdometer(double odometer) {
        this.odometer = odometer;
    }

}
