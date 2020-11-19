package com.vnidens.vehicleindicators.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View.*
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.vnidens.vehicleindicators.databinding.ActivityMainBinding
import com.vnidens.vehicleindicators.ui.adapter.CircularFragmentPagerAdapter
import com.vnidens.vehicleindicators.ui.fragment.SpeedometerFragment
import com.vnidens.vehicleindicators.ui.fragment.TachometerFragment
import com.vnidens.vehicleindicators.ui.view.CircularPageChangeListener
import com.vnidens.vehicleindicators.viewpager.transformer.DepthPageTransformer

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        val pagerAdapter = CircularFragmentPagerAdapter(
            listOf(SpeedometerFragment::class.java, TachometerFragment::class.java),
            supportFragmentManager,
            lifecycle
        )

        binding.pager.adapter = pagerAdapter
        binding.pager.registerOnPageChangeCallback(CircularPageChangeListener(binding.pager))
    }

}