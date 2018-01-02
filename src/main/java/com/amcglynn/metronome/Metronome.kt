package metronome

import javax.sound.midi.*

val BASS_DRUM = 35
val SNARE_DRUM = 38

/**
 * Created by amcglynn on 30/12/2017.
 */
class Metronome(var tempo: Float) {
    private var sequencer: Sequencer
    private var track: Track
    private var sequence: Sequence
    private var metronomeObserver: MetronomeObserver

    init {
        sequencer = MidiSystem.getSequencer()

        sequencer.open()

        sequence = Sequence(Sequence.PPQ, 4)
        track = sequence.createTrack()

        track.add(makeEvent(ShortMessage.NOTE_ON, 9, BASS_DRUM, 0))
        track.add(makeEvent(ShortMessage.NOTE_ON, 9, BASS_DRUM, 8))
        track.add(makeEvent(ShortMessage.NOTE_ON, 9, BASS_DRUM, 10))

        track.add(makeEvent(ShortMessage.NOTE_ON, 9, SNARE_DRUM, 4))
        track.add(makeEvent(ShortMessage.NOTE_ON, 9, SNARE_DRUM, 12))

        track.add(makeEvent(ShortMessage.CONTROL_CHANGE, 1, 127, 16))
        // this is to make sure that it always plays the full bar
        track.add(makeEvent(ShortMessage.PROGRAM_CHANGE, 9, 1, 15))

        sequencer.setSequence(sequence)
        sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY)
        sequencer.tempoInBPM = tempo

        metronomeObserver = MetronomeObserver(sequencer)
    }

    private fun resetTrack() {
        sequence = Sequence(Sequence.PPQ, 4)
        track = sequence.createTrack()

        track.add(makeEvent(ShortMessage.NOTE_ON, 9, BASS_DRUM, 0))
        track.add(makeEvent(ShortMessage.NOTE_ON, 9, BASS_DRUM, 8))
        track.add(makeEvent(ShortMessage.NOTE_ON, 9, BASS_DRUM, 10))

        track.add(makeEvent(ShortMessage.NOTE_ON, 9, SNARE_DRUM, 4))
        track.add(makeEvent(ShortMessage.NOTE_ON, 9, SNARE_DRUM, 12))

        track.add(makeEvent(ShortMessage.CONTROL_CHANGE, 1, 127, 16))
        // this is to make sure that it always plays the full bar
        track.add(makeEvent(ShortMessage.PROGRAM_CHANGE, 9, 1, 15))

        sequencer.setSequence(sequence)
        sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY)
        sequencer.tempoInBPM = tempo
    }

    fun stop() {
        sequencer.stop();
    }

    fun close() {
        sequencer.close()
    }

    fun setNewTempo(newTempo: Float) {
        println("setting tempo to ${newTempo}")
        sequencer.stop()
        resetTrack()
        sequencer.tempoInBPM = newTempo
        sequencer.start()
        tempo = newTempo
    }

    fun start() {
        sequence.deleteTrack(track)
        resetTrack()
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
                        clickListener?.clickCountChanged()
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