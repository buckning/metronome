package com.amcglynn.metronome

import metronome.CommandLineMetronome
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by amcglynn on 05/01/2018.
 */
class CommandLineMetronomeTest {

    @Test
    fun testClampReturnsMinNumberWhenNumberBelowMinimumIsPassedIn() {
        val cliMetronome = CommandLineMetronome()
        assertEquals(25f, cliMetronome.clamp(10f, 25f, 100f))
    }

    @Test
    fun testClampReturnsMaxNumberWhenNumberAboveTheMaximumIsPassedIn() {
        val cliMetronome = CommandLineMetronome()
        assertEquals(250f, cliMetronome.clamp(1000f, 25f, 250f))
    }

    @Test
    fun testClampReturnsTheNumberWhenNumberIsWithinTheLimits() {
        val cliMetronome = CommandLineMetronome()
        assertEquals(50f, cliMetronome.clamp(50f, 25f, 250f))
    }
}
