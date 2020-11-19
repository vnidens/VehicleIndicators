package com.vnidens.vehicle.data.service.provider

import java.util.concurrent.TimeUnit

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 15.11.2020
 */
internal data class DataGeneratorParams(
    val maxValue: Float,
    val sinMultiplier1: Float,
    val sinMultiplier2: Float,
    val delay: Long = 100,
    val delayUnit: TimeUnit = TimeUnit.MILLISECONDS
)