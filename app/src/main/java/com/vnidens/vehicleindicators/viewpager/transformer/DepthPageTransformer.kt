package com.vnidens.vehicleindicators.viewpager.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 19.11.2020
 */
class DepthPageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            when {
                position < -1 -> {
                    alpha = 0f
                }
                position <= 0 -> {
                    alpha = 1f
                    translationX = 0f
                    translationZ = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
                position <= 1 -> {
                    alpha = 1 - position

                    translationX = pageWidth * -position
                    translationZ = -1f

                    scaleX = 1f
                    scaleY = 1f
                }
                else -> {
                    alpha = 0f
                }
            }
        }
    }

}
