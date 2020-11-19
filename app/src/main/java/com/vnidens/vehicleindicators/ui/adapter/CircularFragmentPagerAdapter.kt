package com.vnidens.vehicleindicators.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vnidens.vehicleindicators.ui.fragment.SpeedometerFragment
import com.vnidens.vehicleindicators.ui.fragment.TachometerFragment
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import kotlin.math.log
import kotlin.reflect.KClass

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 17.11.2020
 */
class CircularFragmentPagerAdapter(
    fragmentClasses: List<Class<out Fragment>>,
    manager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(manager, lifecycle) {

    private val items = if(fragmentClasses.size > 1) {
        listOf(fragmentClasses.last(), *fragmentClasses.toTypedArray(), fragmentClasses.first())
    } else {
        fragmentClasses
    }

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment {
        return items[position].newInstance()
    }

}