package com.vnidens.vehicleindicators.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.vnidens.vehicleindicators.data.VehicleDataProvider
import kotlinx.coroutines.Dispatchers

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 18.11.2020
 */
abstract class AbstractIndicatorViewModel(
    protected val dataProvider: VehicleDataProvider
) : ViewModel() {

    val indicatorValue: LiveData<Float> = dataProvider.currentValue.asLiveData()

    init {
        dataProvider.connect()
    }

    override fun onCleared() {
        dataProvider.disconnect()
    }
}