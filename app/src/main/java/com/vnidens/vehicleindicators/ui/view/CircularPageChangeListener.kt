package com.vnidens.vehicleindicators.ui.view

import androidx.viewpager2.widget.ViewPager2
import com.vnidens.vehicleindicators.ui.util.lastIndex

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 17.11.2020
 */
class CircularPageChangeListener(
    private val pager: ViewPager2
) : ViewPager2.OnPageChangeCallback() {

    private var currentPosition = pager.currentItem

    override fun onPageSelected(position: Int) {
        currentPosition = position
    }

    override fun onPageScrollStateChanged(state: Int) {
        if(state != ViewPager2.SCROLL_STATE_IDLE) {
            return
        }

        val lastIndex = pager.adapter?.lastIndex()
            ?: return

        if(currentPosition == 0) {
            pager.setCurrentItem(lastIndex - 1, false)
        } else if(currentPosition == lastIndex) {
            pager.setCurrentItem(1, false)
        }
    }
}