package com.vnidens.vehicle.data.api

import android.content.Context
import android.content.Intent

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 15.11.2020
 */
object VehicleDataApi {

    /**
     * Intent Action for Speedometer data service.
     */
    const val VEHICLE_SPEEDOMETER_SERVICE = "com.vnidens.vehicle.data.service.SPEEDOMETER"

    /**
     * Intent Action for Tachometer data service.
     */
    const val VEHICLE_TACHOMETER_SERVICE = "com.vnidens.vehicle.data.service.TACHOMETER"

    /**
     * Returns [Intent] for the given Service Intent Action if the corresponding service
     * is provided by some application.
     *
     * @param context [Context] instance
     * @param serviceAction requested Service Intent Action.
     *                      Can be one of [VEHICLE_SPEEDOMETER_SERVICE] or [VEHICLE_TACHOMETER_SERVICE].
     *
     * @return [Intent] if the Service with given Intent Action provided by tome application,
     *          <code>null</code> otherwise.
     */
    fun getIntent(context: Context, serviceAction: String): Intent? {
        require(
            serviceAction == VEHICLE_SPEEDOMETER_SERVICE
                    || serviceAction == VEHICLE_TACHOMETER_SERVICE
        ) {
            "Service $serviceAction is not supported."
        }

        val intent = Intent(serviceAction)

        return context.packageManager.resolveService(intent, 0)
            ?.serviceInfo
            ?.let { serviceInfo ->
                intent.apply {
                    setPackage(serviceInfo.packageName)
                }
            }
    }

}