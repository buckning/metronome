package com.amcglynn.metronome

import com.nhaarman.mockito_kotlin.*
import metronome.CommandLineMetronome
import metronome.Metronome
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by amcglynn on 05/01/2018.
 */
class CommandLineMetronomeTest {

    lateinit var cliMetronome: CommandLineMetronome
    var cliInputRead = false

    @Before
    fun setUp() {
        val cliInputMock = mock<CommandLineInput>()
        val metronomeMock = mock<Metronome>()
        cliInputRead = false
        cliMetronome = CommandLineMetronome(metronomeMock, cliInputMock, true)
    }

    @Test
    fun `test clamp returns the min number when the original number is less than the allowed minimum`() {
        assertEquals(25f, cliMetronome.clamp(10f, 25f, 100f))
    }

    @Test
    fun `test clamp returns the max number when the original number is greater than the allowed maximum`() {
        assertEquals(250f, cliMetronome.clamp(1000f, 25f, 250f))
    }

    @Test
    fun `test clamp returns the original number when it is within the max and min limits`() {
        assertEquals(50f, cliMetronome.clamp(50f, 25f, 250f))
    }

    @Test
    fun `test metronome gets started when user enters start command`() {
        val cliInputMock = mock<CommandLineInput>()
        val metronomeMock = mock<Metronome>()
        setUpCliInput("start", cliInputMock)
        cliMetronome = CommandLineMetronome(metronomeMock, cliInputMock, true)

        cliMetronome.start()

        verify(metronomeMock).start()
    }

    @Test
    fun `test metronome tempo gets changed when user enters a valid tempo command`() {
        val cliInputMock = mock<CommandLineInput>()
        val metronomeMock = mock<Metronome>()
        setUpCliInput("tempo 120", cliInputMock)
        cliMetronome = CommandLineMetronome(metronomeMock, cliInputMock, true)

        cliMetronome.start()

        verify(metronomeMock).setNewTempo(eq(120f), any())
    }

    @Test
    fun `test metronome tempo does not get changed when the user enters an invalid tempo command`() {
        testInvalidTempoCommandDoesNotResultInMetronomeGettingInvoked("tempo junk")
    }

    @Test
    fun `test metronome tempo does not get changed when the user enters an invalid tempo command with two args`() {
        testInvalidTempoCommandDoesNotResultInMetronomeGettingInvoked("tempo 120 junk")
    }

    @Test
    fun `test metronome tempo does not get changed when the user enters an invalid tempo value`() {
        testInvalidTempoCommandDoesNotResultInMetronomeGettingInvoked("tempo 120a")
    }

    @Test
    fun `test the main loop exits when user enters the exit command`() {
        val cliInputMock = mock<CommandLineInput>()
        val metronomeMock = mock<Metronome>()
        setUpCliInput("exit", cliInputMock)
        cliMetronome = CommandLineMetronome(metronomeMock, cliInputMock, true)

        cliMetronome.start()

        verify(metronomeMock, times(0)).setNewTempo(any(), any())
        verify(metronomeMock, times(0)).start()
        verify(metronomeMock, times(1)).stop()
        verify(metronomeMock, times(1)).close()
    }

    @Test
    fun `test the metronome stops when the user enters the stop command`() {
        val cliInputMock = mock<CommandLineInput>()
        val metronomeMock = mock<Metronome>()
        setUpCliInput("stop", cliInputMock)
        cliMetronome = CommandLineMetronome(metronomeMock, cliInputMock, true)

        cliMetronome.start()

        verify(metronomeMock, times(0)).setNewTempo(any(), any())
        verify(metronomeMock, times(0)).start()
        verify(metronomeMock, times(2)).stop()  //there is a second stop here to account for the exit from setUpCliInput
        verify(metronomeMock, times(1)).close()
    }

    @Test
    fun `test metronome gets stopped and closed when user enters exit command`() {
        val cliInputMock = mock<CommandLineInput>()
        val metronomeMock = mock<Metronome>()
        setUpCliInput("exit", cliInputMock)
        cliMetronome = CommandLineMetronome(metronomeMock, cliInputMock, true)

        cliMetronome.start()

        inOrder(metronomeMock) {
            verify(metronomeMock).stop()
            verify(metronomeMock).close()
        }
    }

    fun testInvalidTempoCommandDoesNotResultInMetronomeGettingInvoked(invalidCommand: String) {
        val cliInputMock = mock<CommandLineInput>()
        val metronomeMock = mock<Metronome>()
        setUpCliInput(invalidCommand, cliInputMock)
        cliMetronome = CommandLineMetronome(metronomeMock, cliInputMock, true)

        cliMetronome.start()

        verify(metronomeMock, times(0)).setNewTempo(any(), any())
    }

    /***
     * Set up the cli to mock out the user entering userEnteredString, followed by an "exit" command. The purpose of
     * the exit command is to prevent an endless loop in the test. Each test needs to account for the exit command
     * getting processed during operation
     */
    fun setUpCliInput(userEnteredString: String, cliInputMock: CommandLineInput) {
        doAnswer {
            if(!cliInputRead) {
                cliInputRead = true
                userEnteredString
            } else {
                "exit"
            }
        }.`when`(cliInputMock).readLine()
    }
}
