package com.vnidens.vehicle.data.service.provider

import kotlinx.coroutines.flow.Flow

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 15.11.2020
 */
internal interface DataGenerator {

    fun provideData(params: DataGeneratorParams): Flow<Float>

}