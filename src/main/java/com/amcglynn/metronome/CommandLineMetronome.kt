package metronome

import java.util.*
import kotlin.system.exitProcess

val DEFAULT_TEMPO = 60f
val MIN_TEMPO = 30f
val MAX_TEMPO = 300f

class CommandLineMetronome {
    fun start() {
        val metronome = Metronome(DEFAULT_TEMPO)

        val stdin = Scanner(System.`in`)

        var command: String

        println("available commands are start, stop and exit")

        do {
            print("Enter command: ")
            val fullCommand = stdin.nextLine()
            command = fullCommand.split(" ").get(0)

            when(command) {
                "start" -> {
                    println("Starting...")
                    metronome.start()
                }
                "stop" -> {
                    println("Stopping...")
                    metronome.stop()
                }
                "tempo" -> {
                    if(fullCommand.split(" ").size == 2) {
                        val tempo = fullCommand.split(" ").get(1)
                        try {
                            val tempoFloat = tempo.toFloat()
                            metronome.setNewTempo(clamp(tempoFloat, MIN_TEMPO, MAX_TEMPO))
                        } catch (e: NumberFormatException) {
                            println("You must enter tempo followed by the desired tempo.\n " +
                                    "e.g.\ntempo 120")
                        }
                    } else {
                        println("You must enter tempo followed by the desired tempo.\n " +
                                "e.g.\ntempo 120")
                    }
                }
                "exit" -> {
                    println("Exiting...")
                    metronome.stop()
                    metronome.close()
                }
                else -> {
                    println("Command not found, available commands are start, stop and exit")
                }
            }
        } while (command != "exit")

        exitProcess(0)
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
    CommandLineMetronome().start()
}

