package com.vnidens.vehicleindicators.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.vnidens.vehicleindicators.R
import com.vnidens.vehicleindicators.databinding.FragmentSpeedometerBinding
import com.vnidens.vehicleindicators.databinding.FragmentTachometerBinding
import com.vnidens.vehicleindicators.service.SpeedometerServiceProxy
import com.vnidens.vehicleindicators.service.TachometerServiceProxy
import com.vnidens.vehicleindicators.viewmodel.SpeedometerViewModel
import com.vnidens.vehicleindicators.viewmodel.TachometerViewModel
import com.vnidens.vehicleindicators.viewmodel.factory.SpeedometerVmFactory
import com.vnidens.vehicleindicators.viewmodel.factory.TachometerVmFactory
import org.slf4j.LoggerFactory

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 17.11.2020
 */
class TachometerFragment : Fragment(R.layout.fragment_tachometer) {

    private val vm by activityViewModels<TachometerViewModel> { vmFactory }

    //@Inject
    private lateinit var vmFactory: TachometerVmFactory

    private lateinit var binding: FragmentTachometerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTachometerBinding.bind(view)

        vm.indicatorValue.observe(viewLifecycleOwner) {
            binding.givTachometer.currentValue = it
        }
    }

    private fun inject() {
        //TODO: perform dependency injection here
        val dataProvider = TachometerServiceProxy(requireContext())
        vmFactory = TachometerVmFactory(dataProvider)
    }
}