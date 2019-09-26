package mml;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.sound.midi.*;
import java.util.ArrayList;

public class MMLParser {
    private static final int RESOLUTION = 192;
    private Sequence sequence;
    private Track track;
    private ArrayList<String> errors;

    public static Sequence parseMML(String mml[]) throws InvalidMidiDataException {
        return new MMLParser(mml).get();
    }

    private MMLParser(@NotNull String mml[]) throws InvalidMidiDataException {
        sequence = new Sequence(Sequence.PPQ, RESOLUTION);
        errors = new ArrayList<>();

        int trackNum = 1;
        for(String trackMML : mml) {
            track = sequence.createTrack();
            // We can only create Tracks from Sequences, so pass the track in and mutate it
            TrackParser parser = new TrackParser(track, trackMML.toLowerCase(), RESOLUTION);
            for(String err : parser.getErrors()) {
                errors.add(trackNum + ":" + err);
            }
            trackNum++;
        }
    }

    @Contract(pure = true)
    private Sequence get() throws InvalidMidiDataException {
        if(errors.isEmpty()) {
            return sequence;
        } else {
            for(String err : errors) {
                System.out.println(err);
            }
            throw new InvalidMidiDataException("Found errors.");
        }
    }
}
