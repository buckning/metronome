package metronome

import javax.sound.midi.*

/**
 * Created by amcglynn on 30/12/2017.
 */
class Metronome(var tempo: Float, val sequencer: Sequencer, val sequence: Sequence) {
    private var track: Track
    private var metronomeObserver: MetronomeObserver

    val BASS_DRUM = 35
    val SNARE_DRUM = 38

    init {
        sequencer.open()

        track = sequence.createTrack()

        createTrack(track)

        sequencer.setSequence(sequence)
        sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY)
        sequencer.tempoInBPM = tempo

        metronomeObserver = MetronomeObserver(sequencer)
    }

    private fun resetTrack(sequence: Sequence) {
        track = sequence.createTrack()
        createTrack(track)

        sequencer.setSequence(sequence)
        sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY)
    }

    private fun createTrack(track: Track) {
        track.add(makeEvent(ShortMessage.NOTE_ON, 9, BASS_DRUM, 0))
        track.add(makeEvent(ShortMessage.NOTE_ON, 9, BASS_DRUM, 8))
        track.add(makeEvent(ShortMessage.NOTE_ON, 9, BASS_DRUM, 10))

        track.add(makeEvent(ShortMessage.NOTE_ON, 9, SNARE_DRUM, 4))
        track.add(makeEvent(ShortMessage.NOTE_ON, 9, SNARE_DRUM, 12))

        track.add(makeEvent(ShortMessage.CONTROL_CHANGE, 1, 127, 16))
        // this is to make sure that it always plays the full bar
        track.add(makeEvent(ShortMessage.PROGRAM_CHANGE, 9, 1, 15))
    }

    fun stop() {
        sequencer.stop();
    }

    fun close() {
        sequencer.close()
    }

    fun setNewTempo(newTempo: Float, sequence: Sequence) {
        sequencer.stop()
        resetTrack(sequence)
        sequencer.tempoInBPM = newTempo
        sequencer.start()
        tempo = newTempo
    }

    fun start() {
        // need to delete and reset track due to the way that the midi sequencer sounds when starting again
        sequence.deleteTrack(track)
        resetTrack(sequence)
        sequencer.tempoInBPM = tempo
        sequencer.start()
    }

    private fun makeEvent(cmd: Int, channel: Int, sfx: Int, tick: Long) : MidiEvent {
        val message = ShortMessage()
        message.setMessage(cmd, channel, sfx, 100)
        return MidiEvent(message, tick)
    }

    fun addClickListener(clickListener: ClickListener) {
        metronomeObserver.addClickListener(clickListener)
    }

    class MetronomeObserver(sequencer: Sequencer) {
        private var tickPosition: Long
        private var clickListener: ClickListener? = null

        init {
            tickPosition = sequencer.tickPosition
            Thread(Runnable {
                while(true) {
                    if(tickPosition != sequencer.tickPosition) {
                        tickPosition = sequencer.tickPosition
                        clickListener?.clickCountChanged(tickPosition)
                    }
                    Thread.sleep(10)
                }
            }).start()
        }

        fun addClickListener(clickListener: ClickListener) {
            this.clickListener = clickListener
        }
    }
}