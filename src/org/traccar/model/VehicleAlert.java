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
public class VehicleAlert {
    public static final int TYPE_SPEED = 1;
    public static final int TYPE_STOP = 2;
    public static final int TYPE_GEOFENCE_EXIT = 3;
    public static final int TYPE_GEOFENCE_ENTER = 4;
    public static final int TYPE_GEOFENCE_ENTER_EXIT = 5;
    public static final int TYPE_ODOMETER = 6;
    public static final int TYPE_PANIC = 7;

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private long deviceId;

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
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

    private double initialOdometer;

    public double getInitialOdometer() {
        return initialOdometer;
    }

    public void setInitialOdometer(double initialOdometer) {
        this.initialOdometer = initialOdometer;
    }

    private long geofenceid;
    private double maxSpeed;
    private int stopTime;
    private double odometro;
    private int typeId;

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public long getGeofenceid() {
        return geofenceid;
    }

    public void setGeofenceid(long geofenceid) {
        this.geofenceid = geofenceid;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getStopTime() {
        return stopTime;
    }

    public void setStopTime(int stopTime) {
        this.stopTime = stopTime;
    }

    public double getOdometro() {
        return odometro;
    }

    public void setOdometro(double odometro) {
        this.odometro = odometro;
    }

}
