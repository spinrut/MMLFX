package mml;

import javax.sound.midi.*;

public class MMLPlayer {
    private Sequencer sequencer;

    public MMLPlayer() throws MidiUnavailableException {
        sequencer = MidiSystem.getSequencer();
        if(sequencer != null) {
            sequencer.open();
        } else {
            throw new MidiUnavailableException("Failed to get sequencer");
        }
    }

    public void play(String mml[]) {
        try {
            Sequence sequence = MMLParser.parseMML(mml);
            sequencer.setSequence(sequence);
            sequencer.start();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        sequencer.close();
    }
}
