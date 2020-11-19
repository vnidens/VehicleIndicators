package com.vnidens.vehicle.data.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.vnidens.vehicle.data.api.SpeedometerDataService
import com.vnidens.vehicle.data.api.TachometerDataService
import com.vnidens.vehicle.data.api.VehicleDataApi
import com.vnidens.vehicle.data.service.provider.DataGeneratorImpl
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 15.11.2020
 */
class VehicleDataService : Service(), CoroutineScope {

    private lateinit var speedometerBinder: SpeedometerDataService.Stub
    private lateinit var tachometerBinder: TachometerDataService.Stub

    override val coroutineContext: CoroutineContext = Dispatchers.Default + SupervisorJob()

    override fun onCreate() {
        super.onCreate()

        inject()
    }

    override fun onBind(intent: Intent?): IBinder? =
        when (intent?.action) {
            VehicleDataApi.VEHICLE_SPEEDOMETER_SERVICE -> speedometerBinder
            VehicleDataApi.VEHICLE_TACHOMETER_SERVICE -> tachometerBinder
            else -> null
        }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    private fun inject() {
        //TODO: do some real DI here

        val dataProvider = DataGeneratorImpl()

        speedometerBinder = SpeedometerDataServiceImpl(dataProvider, coroutineContext.job)
        tachometerBinder = TachometerDataServiceImpl(dataProvider, coroutineContext.job)
    }
}