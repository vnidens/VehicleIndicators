package com.vnidens.vehicleindicators.ui.view

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * VehicleIndicators
 *
 *
 * Created by Victor Nidens
 * Date: 20.11.2020
 */

@ExtendWith(MockKExtension::class)
internal class CircularPageChangeListenerTest {

    @MockK
    private lateinit var viewPager: ViewPager2

    @MockK
    lateinit var adapter: RecyclerView.Adapter<*>

    @BeforeEach
    fun beforeEach() {
        every { viewPager.adapter } returns adapter

        every { viewPager.setCurrentItem(allAny(), allAny()) } returns Unit
    }

    @Test
    fun `switch to first item should be moved to the last but one`() {
        every { viewPager.currentItem } returns 0
        every { adapter.itemCount } returns 6

        val listener = CircularPageChangeListener(viewPager)

        listener.onPageScrollStateChanged(ViewPager2.SCROLL_STATE_IDLE)

        verify { viewPager.setCurrentItem(4, allAny()) }
    }

    @Test
    fun `switch to last item should be moved to the second one`() {
        every { viewPager.currentItem } returns 5
        every { adapter.itemCount } returns 6

        val listener = CircularPageChangeListener(viewPager)

        listener.onPageScrollStateChanged(ViewPager2.SCROLL_STATE_IDLE)

        verify { viewPager.setCurrentItem(1, allAny()) }
    }

    @Test
    fun `switch to any item in the middle of list is not altered`() {
        every { viewPager.currentItem } returns 3
        every { adapter.itemCount } returns 6

        val listener = CircularPageChangeListener(viewPager)

        listener.onPageScrollStateChanged(ViewPager2.SCROLL_STATE_IDLE)

        verify(inverse = true) { viewPager.setCurrentItem(allAny(), allAny()) }
    }

}
