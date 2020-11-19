package com.vnidens.vehicle.data.service

import com.vnidens.vehicle.data.api.FloatValueListener
import com.vnidens.vehicle.data.api.TachometerDataService
import com.vnidens.vehicle.data.service.provider.DataGenerator
import com.vnidens.vehicle.data.service.provider.DataGeneratorParams
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 15.11.2020
 */
internal class TachometerDataServiceImpl(
    dataGenerator: DataGenerator,
    parentJob: Job
) : TachometerDataService.Stub(), CoroutineScope {

    private val supervisorJob = SupervisorJob(parentJob)

    override val coroutineContext: CoroutineContext = Dispatchers.Default + supervisorJob

    private val listenerRef = AtomicReference<FloatValueListener>()

    init {
        dataGenerator.provideData(
            DataGeneratorParams(
                MAX_VALUE,
                sinMultiplier1 = SIN_MULTIPLIER_1_DEFAULT,
                sinMultiplier2 = SIN_MULTIPLIER_2_DEFAULT
            )
        )
            .onEach {
                listenerRef.get()?.onValueUpdate(it)
            }
            .launchIn(this)
    }

    override fun setFloatValueListener(listener: FloatValueListener?) {
        listenerRef.set(listener)
    }

    companion object {

        private const val MAX_VALUE = 10f

        private const val SIN_MULTIPLIER_1_DEFAULT = 100f

        private const val SIN_MULTIPLIER_2_DEFAULT = 150f
    }
}