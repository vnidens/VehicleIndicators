package com.vnidens.vehicleindicators.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.vnidens.vehicle.data.api.FloatValueListener
import com.vnidens.vehicleindicators.data.VehicleDataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 18.11.2020
 */
abstract class AbstractVehicleServiceProxy<T>(
    private val context: Context
) : VehicleDataProvider {

    private val serviceConnection = ServiceConnectionImpl()

    protected abstract val serviceIntent: Intent

    protected val serviceRef = AtomicReference<T>()
    protected val floatValueListener = FloatValueListenerImpl()

    private val _currentValue = MutableStateFlow(0f)
    override val currentValue: StateFlow<Float>
        get() = _currentValue

    protected abstract fun getServiceInstance(binder: IBinder): T

    protected abstract fun onConnected()

    override fun connect() {
        if(serviceRef.get() == null) {
            context.bindService(serviceIntent, serviceConnection, Service.BIND_AUTO_CREATE)
        }
    }

    override fun disconnect() {
        if(serviceRef.get() != null) {
            context.unbindService(serviceConnection)
        }
    }

    private inner class ServiceConnectionImpl : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            service?.also {
                serviceRef.set(getServiceInstance(it))
                onConnected()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceRef.set(null)
        }

    }

    protected inner class FloatValueListenerImpl : FloatValueListener.Stub() {
        override fun onValueUpdate(value: Float) {
            _currentValue.tryEmit(value)
        }

    }
}