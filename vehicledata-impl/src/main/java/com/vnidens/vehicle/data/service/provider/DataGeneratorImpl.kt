package com.vnidens.vehicle.data.service.provider

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.sin

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 15.11.2020
 */
internal class DataGeneratorImpl : DataGenerator {

    override fun provideData(params: DataGeneratorParams): Flow<Float> = flow {

        val delayValue = params.delayUnit.toMillis(params.delay)

        generateSequence(0f) { it + 0.1f }
            .forEach { currentTicker ->
                val value = abs(
                    (sin(currentTicker * params.sinMultiplier1) + sin(currentTicker * params.sinMultiplier2)) / 2
                )

                emit(params.maxValue * value)

                delay(delayValue)
            }
    }

}