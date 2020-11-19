package com.vnidens.vehicleindicators.data

import kotlinx.coroutines.flow.StateFlow

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 18.11.2020
 */
interface VehicleDataProvider {
    fun connect()
    fun disconnect()

    val currentValue: StateFlow<Float>
}