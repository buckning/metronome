package com.amcglynn.metronome

import com.nhaarman.mockito_kotlin.*
import metronome.ClickListener
import metronome.Metronome
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import javax.sound.midi.Sequence
import javax.sound.midi.Sequencer
import javax.sound.midi.Track

/**
 * Created by amcglynn on 05/01/2018.
 */
class MetronomeTest : ClickListener {
    lateinit var metronome: Metronome
    lateinit var trackMock: Track
    lateinit var sequenceMock: Sequence
    lateinit var sequencerMock: Sequencer

    var clickListenerUpdated = false

    @Before
    fun setUp() {
        clickListenerUpdated = false
        trackMock = mock<Track>()

        sequenceMock = mock<Sequence> {
            on { createTrack() } doReturn trackMock
        }

        sequencerMock = mock<Sequencer>()

        metronome = Metronome(120f, sequencerMock, sequenceMock)
    }

    @Test
    fun testTempoIsSetInMetronomeInConstructor() {
        assertEquals(120f, metronome.tempo)
    }

    @Test
    fun testSequencerIsStoppedWhenStopIsCalled() {
        metronome.stop()
        verify(sequencerMock).stop()
        assertEquals(120f, metronome.tempo)
    }

    @Test
    fun testSequencerIsStartedWhenStartIsCalled() {
        metronome.start()
        verify(sequencerMock).start()
        assertEquals(120f, metronome.tempo)
    }

    @Test
    fun testClickListenerUpdatesTheListenerWhenTheSequncerHasUpdated() {
        metronome.addClickListener(this)
        whenever(sequencerMock.tickPosition).thenReturn(0)

        assertFalse(clickListenerUpdated)

        whenever(sequencerMock.tickPosition).thenReturn(1)
        Thread.sleep(15)    // allow enough time (150% of max time) for the listener to update

        // if this check fails, there could be a delay in the callback of the listener
        // greater than 5ms to update the listener would result in a failure
        assertTrue(clickListenerUpdated)
    }

    @Test
    fun testClickListenerDoesNotUpdateTheListenerWhenTheSequncerHasNotUpdated() {
        metronome.addClickListener(this)

        whenever(sequencerMock.tickPosition).thenReturn(0)

        Thread.sleep(15)    // allow enough time (150% of max time) for the listener to update

        // if this check fails, there could be a delay in the callback of the listener
        // greater than 5ms to update the listener would result in a failure
        assertFalse(clickListenerUpdated)
    }

    @Test
    fun testSetNewTempoResultsInChangeInTempoToSequencer() {
        metronome.setNewTempo(60f, sequenceMock)
        verify(sequencerMock).tempoInBPM = 60f

        inOrder(sequencerMock) {
            verify(sequencerMock).stop()
            verify(sequencerMock).start()
        }

        assertEquals(60f, metronome.tempo)
    }

    override fun clickCountChanged() {
        clickListenerUpdated = true
    }
}
