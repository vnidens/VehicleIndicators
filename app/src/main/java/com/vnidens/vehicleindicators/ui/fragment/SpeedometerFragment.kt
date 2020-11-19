package com.vnidens.vehicleindicators.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.vnidens.vehicleindicators.R
import com.vnidens.vehicleindicators.databinding.FragmentSpeedometerBinding
import com.vnidens.vehicleindicators.service.SpeedometerServiceProxy
import com.vnidens.vehicleindicators.viewmodel.SpeedometerViewModel
import com.vnidens.vehicleindicators.viewmodel.factory.SpeedometerVmFactory
import org.slf4j.LoggerFactory

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 17.11.2020
 */
class SpeedometerFragment : Fragment(R.layout.fragment_speedometer) {

    private val vm by activityViewModels<SpeedometerViewModel> { vmFactory }

    //@Inject
    private lateinit var vmFactory: SpeedometerVmFactory

    private lateinit var binding: FragmentSpeedometerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSpeedometerBinding.bind(view)

        vm.indicatorValue.observe(viewLifecycleOwner) {
            binding.givSpeedometer.currentValue = it
        }
    }

    private fun inject() {
        //TODO: perform dependency injection here
        val dataProvider = SpeedometerServiceProxy(requireContext())
        vmFactory = SpeedometerVmFactory(dataProvider)
    }

}