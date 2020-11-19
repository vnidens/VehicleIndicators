package com.vnidens.vehicle.data.api;

import com.vnidens.vehicle.data.api.FloatValueListener;

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 14.11.2020
 */
interface SpeedometerDataService {

    void setFloatValueListener(in FloatValueListener listener);

}