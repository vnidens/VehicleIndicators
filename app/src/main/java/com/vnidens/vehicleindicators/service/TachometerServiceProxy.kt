package com.vnidens.vehicleindicators.service

import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.vnidens.vehicle.data.api.SpeedometerDataService
import com.vnidens.vehicle.data.api.TachometerDataService
import com.vnidens.vehicle.data.api.VehicleDataApi
import java.lang.IllegalStateException

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 18.11.2020
 */
class TachometerServiceProxy(
    context: Context,
) : AbstractVehicleServiceProxy<TachometerDataService>(context) {

    override val serviceIntent: Intent =
        VehicleDataApi.getIntent(context, VehicleDataApi.VEHICLE_TACHOMETER_SERVICE)
            ?: throw IllegalStateException("Tachometer service not available")

    override fun getServiceInstance(binder: IBinder): TachometerDataService =
        TachometerDataService.Stub.asInterface(binder)

    override fun onConnected() {
        serviceRef.get()?.setFloatValueListener(floatValueListener)
    }
}