# Kotlin MIDI Metronome
Simple Metronome project which was primarily used to learn the syntax of the Kotlin programming language.

The metronome uses the javax.sound.midi APIs to generate the MIDI sounds

# How to use

While the metronome is running, available commands are:
* start
* stop
* tempo <new tempo>
* exit

# Potential improvements

* Use Dagger 2, Guice or Spring framework for dependency injection
* There MIDI library used in this project does not work perfectly at all times.
 This could be some quirks with how this project is integrating with it or configured. There is room for improvement
* There could be a better mechanism to listen for changes on the sequencer than polling it in a loop
