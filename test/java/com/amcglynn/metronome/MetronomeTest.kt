package com.amcglynn.metronome

import com.nhaarman.mockito_kotlin.*
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
class MetronomeTest {
    lateinit var metronome: Metronome
    lateinit var trackMock: Track
    lateinit var sequenceMock: Sequence
    lateinit var sequencerMock: Sequencer

    @Before
    fun setUp() {
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
    fun testTempoChangeResultsInChangeInTempoToSequencer() {
        metronome.setNewTempo(60f, sequenceMock)
        verify(sequencerMock).tempoInBPM = 60f

        inOrder(sequencerMock) {
            verify(sequencerMock).stop()
            verify(sequencerMock).start()
        }

        assertEquals(60f, metronome.tempo)
    }
}
