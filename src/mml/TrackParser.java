package mml;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.sound.midi.*;
import java.util.ArrayList;

public class TrackParser {
    private String mml;
    private Track track;
    private long ticks;
    private int octave, noteLen, counter, volume, resolution, prevNote;
    private ArrayList<String> errors;

    private class Note {
        private static final int C = 0;
        private static final int D = 2;
        private static final int E = 4;
        private static final int F = 5;
        private static final int G = 7;
        private static final int A = 9;
        private static final int B = 11;
    }

    public TrackParser(@NotNull Track track, String mml, int resolution) throws InvalidMidiDataException {
        this.track = track;
        this.mml = mml;
        this.resolution = resolution;

        ticks = 0;
        octave = 5;
        noteLen = noteLenToTicks(4);
        counter = 0;
        volume = 93;
        prevNote = -1;

        // Set default tempo and begin parsing
        errors = new ArrayList<>();
        track.add(new MidiEvent(msgSetTempo(120), 0));
        parse();
    }

    private void parse() throws InvalidMidiDataException {
        while(counter < mml.length()) {
            if((mml.charAt(counter) >= 'a' && mml.charAt(counter) <= 'g')
                    || mml.charAt(counter) == 'n'
                    || mml.charAt(counter) == '&') {
                addNote();
            } else {
                switch(mml.charAt(counter)) {
                    case 'r': {
                        counter++;

                        // Clear note
                        if(prevNote != -1) {
                            track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, prevNote, volume), ticks));
                            prevNote = -1;
                        }

                        int restLen = consumeLen();
                        if(restLen == -1) {
                            if(matchChar('.')) {
                                restLen = noteLen + noteLen / 2;
                            } else {
                                restLen = noteLen;
                            }
                        }
                        ticks += restLen;

                        break;
                    }
                    case 'l': {
                        counter++;

                        int newLen = consumeLen();
                        if(newLen != -1) {
                            noteLen = newLen;
                        } else {
                            errors.add(counter + ": Missing length value");
                        }

                        break;
                    }
                    case '<': {
                        counter++;
                        octave--;

                        break;
                    }
                    case '>': {
                        counter++;
                        octave++;

                        break;
                    }
                    case 'o': {
                        counter++;
                        int newOctave = consumeNum();

                        if(newOctave != -1) {
                            if(newOctave >= 0 && newOctave <= 10) {
                                octave = newOctave + 1;
                            } else {
                                errors.add(counter + ": Invalid octave number: " + newOctave);
                            }
                        } else {
                            errors.add(counter + ": Missing octave number");
                        }

                        break;
                    }
                    case 'v': {
                        //TODO Implement
                        counter++;
                        volume = 8 * consumeNum();
                        break;
                    }
                    case 't': {
                        counter++;
                        int newTempo = consumeNum();

                        if(newTempo >= 32 && newTempo <= 255) {
                            track.add(new MidiEvent(msgSetTempo(newTempo), ticks));
                        } else {
                            errors.add(counter + ": Invalid tempo:" + newTempo);
                        }

                        break;
                    }
                    default: {
                        errors.add(counter + ": Invalid char: " + mml.charAt(counter));
                        counter++;
                        break;
                    }
                }
            }
        }

        if(prevNote != -1) {
            track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, prevNote, volume), ticks));
        }
    }

    private void addNote() throws InvalidMidiDataException {
        boolean tie = matchChar('&');

        int note = consumeNoteName();

        if(note < 0 || note > 128) {
            errors.add(counter + ": Invalid note number: " + note);
            return;
        }

        int currNoteLen = consumeLen();
        if(currNoteLen == -1) {
            if(matchChar('.')) {
                currNoteLen = noteLen + noteLen / 2;
            } else {
                currNoteLen = noteLen;
            }
        }

        addNote(note, currNoteLen, tie);
    }

    private void addNote(int note, int numTicks, boolean tie) throws InvalidMidiDataException {
        if(prevNote != note || !tie) {
            if(prevNote != -1) {
                track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, prevNote, volume), ticks));
            }

            track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, note, volume), ticks));
        }

        ticks += numTicks;
        prevNote = note;
    }

    private int consumeNoteName() {
        int note;
        if(mml.charAt(counter) != 'n') {
            int noteName;
            switch(mml.charAt(counter)) {
                case 'c': noteName = Note.C;
                    break;
                case 'd': noteName = Note.D;
                    break;
                case 'e': noteName = Note.E;
                    break;
                case 'f': noteName = Note.F;
                    break;
                case 'g': noteName = Note.G;
                    break;
                case 'a': noteName = Note.A;
                    break;
                case 'b': noteName = Note.B;
                    break;
                default: errors.add(counter + ": Not a note: " + mml.charAt(counter));
                    return -1;
            }

            note = 12 * octave + noteName;
            counter++;
        } else {
            counter++;
            note = consumeNum();
        }

        if(matchChar('+')) {
            note++;
        } else if(matchChar('-')) {
            note--;
        }

        return note;
    }

    private int consumeNum() {
        int begin = counter;

        while(counter < mml.length() && mml.charAt(counter) >= '0' && mml.charAt(counter) <= '9') {
            counter++;
        }

        int len = -1;

        if(counter > begin) {
            len = Integer.parseInt(mml.substring(begin, counter));
        }

        return len;
    }

    private int consumeLen() {
        int len = consumeNum();

        if(len != -1) {
            if(resolution % len == 0) {
                int numTicks = noteLenToTicks(len);
                if(matchChar('.')) {
                    return numTicks + numTicks / 2;
                } else {
                    return numTicks;
                }
            } else {
                errors.add(counter + ": Invalid note length: " + len);
                len = -1;
            }
        }

        return len;
    }

    private boolean matchChar(char c) {
        if(counter < mml.length() && mml.charAt(counter) == c) {
            counter++;
            return true;
        } else {
            return false;
        }
    }

    @Contract(pure = true)
    private int noteLenToTicks(int len) {
        return resolution * 4 / len;
    }

    @NotNull
    @Contract("_ -> new")
    private MetaMessage msgSetTempo(int bpm) throws InvalidMidiDataException {
        byte tempo[] = new byte[3];
        int val = 60000000 / bpm;
        tempo[0] = (byte)((val & 0xFF0000) >> 16);
        tempo[1] = (byte)((val & 0xFF00) >> 8);
        tempo[2] = (byte)(val & 0xFF);

        return new MetaMessage(0x51, tempo, 3);
    }

    public ArrayList<String> getErrors() {
        return errors;
    }
}
