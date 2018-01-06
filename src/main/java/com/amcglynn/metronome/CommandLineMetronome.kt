package metronome

import com.amcglynn.metronome.CommandLineInput
import java.lang.NumberFormatException
import java.util.*
import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequence
import kotlin.system.exitProcess

val DEFAULT_TEMPO = 60f
val MIN_TEMPO = 30f
val MAX_TEMPO = 300f

class CommandLineMetronome(val metronome: Metronome, val commandLineInput: CommandLineInput,
                           val testMode: Boolean = false) {
    fun start() {
        val instructions = "available commands are: \n\tstart \n\tstop \n\ttempo <new tempo> " +
                "\n\texit\n\nNote: tempo must be between 30 and 300"

        println(instructions)

        do {
            print("Enter command: ")
            val fullCommand = commandLineInput.readLine()
            var command = fullCommand.split(" ").get(0)

            when(command) {
                "start" -> {
                    doStart()
                }
                "stop" -> {
                    doStop()
                }
                "tempo" -> {
                    doTempoChange(fullCommand)
                }
                "exit" -> {
                    doExit()
                }
                else -> {
                    println("Command not found, ${instructions}")
                }
            }
        } while (command != "exit")

        // this check is here so we don't stop junit from running during a test suite run
        if(!testMode) {
            exitProcess(0)
        }
    }

    fun doStart() {
        println("Starting...")
        metronome.start()
    }

    fun doStop() {
        println("Stopping...")
        metronome.stop()
    }

    fun doTempoChange(fullCommand: String) {
        val tempoChangeInstructions = "You must enter tempo followed by the desired tempo.\n " +
                "e.g.\ntempo 120"

        if(fullCommand.split(" ").size == 2) {
            val tempo = fullCommand.split(" ").get(1)
            try {
                val tempoFloat = tempo.toFloat()
                metronome.setNewTempo(clamp(tempoFloat, MIN_TEMPO, MAX_TEMPO), Sequence(Sequence.PPQ, 4))
            } catch (e: NumberFormatException) {
                println(tempoChangeInstructions)
            }
        } else {
            println(tempoChangeInstructions)
        }
    }

    fun doExit() {
        println("Exiting...")
        metronome.stop()
        metronome.close()
    }

    fun clamp(num: Float, min: Float, max: Float) : Float {
        var clampedValue = num
        if(num < min) {
            clampedValue = min
        } else if (num > max) {
            clampedValue = max
        }
        return clampedValue
    }
}

fun main(args: Array<String>) {
    val metronome = Metronome(DEFAULT_TEMPO, MidiSystem.getSequencer(), Sequence(Sequence.PPQ, 4))
    val cliInput = CommandLineInput(Scanner(System.`in`))
    CommandLineMetronome(metronome, cliInput).start()
}
