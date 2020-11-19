package com.vnidens.vehicleindicators.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vnidens.vehicleindicators.data.VehicleDataProvider
import com.vnidens.vehicleindicators.viewmodel.SpeedometerViewModel
import java.lang.IllegalArgumentException

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 18.11.2020
 */
class SpeedometerVmFactory(
    private val dataProvider: VehicleDataProvider
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        when(modelClass) {
            SpeedometerViewModel::class.java -> SpeedometerViewModel(dataProvider)
            else -> throw IllegalArgumentException("Factory does not support ViewModel class $modelClass")
        } as T
}