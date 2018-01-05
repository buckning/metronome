package com.amcglynn.metronome

import java.util.Scanner

/**
 * Created by amcglynn on 05/01/2018.
 */
class CommandLineInput(val stdin: Scanner) {
    fun readLine() : String {
        return stdin.nextLine()
    }
}
