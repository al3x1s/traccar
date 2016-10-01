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
package org.traccar.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.traccar.Config;
import org.traccar.Context;
import org.traccar.helper.Log;
import org.traccar.model.VehicleAlert;

/**
 *
 * @author alexis
 */
public class VehicleAlertManager {

    public static final long DEFAULT_REFRESH_DELAY = 60;

    private final Config config;
    private final DataManager dataManager;
    private final long dataRefreshDelay;

    private Map<Long, VehicleAlert> vehicleAlert = new ConcurrentHashMap<>();

    private AtomicLong vehicleAlertLastUpdate = new AtomicLong();

    public VehicleAlertManager(DataManager dataManager) {
        this.dataManager = dataManager;
        this.config = Context.getConfig();
        dataRefreshDelay = config.getLong("database.refreshDelay", DEFAULT_REFRESH_DELAY) * 1000;
        if (dataManager != null) {
            try {
                updateVehicleAlertCache(true);
            } catch (SQLException error) {
                Log.warning(error);
            }
        }
    }

    private void updateVehicleAlertCache(boolean force) throws SQLException {
        long lastUpdate = vehicleAlertLastUpdate.get();
        if ((force || System.currentTimeMillis() - lastUpdate > dataRefreshDelay)
                && vehicleAlertLastUpdate.compareAndSet(lastUpdate, System.currentTimeMillis())) {
            vehicleAlert.clear();
            for (VehicleAlert va : dataManager.getAllVehicleAlert()) {
                vehicleAlert.put(va.getId(), va);
            }
        }
    }

    public Collection<VehicleAlert> getVehicleAlertByDeviceId(long deviceId) {
        boolean forceUpdate = vehicleAlert.isEmpty();

        try {
            updateVehicleAlertCache(forceUpdate);
        } catch (SQLException e) {
            Log.warning(e);
        }

        Collection<VehicleAlert> vehicleAlertResult = new ArrayList<>();
        for (VehicleAlert va : vehicleAlert.values()) {
            if (va.getDeviceId() == deviceId) {
                vehicleAlertResult.add(va);
            }
        }
        return vehicleAlertResult;
    }

}
