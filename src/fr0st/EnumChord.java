package fr0st;

import java.util.Arrays;

public enum EnumChord {
    MAJ("M3", "M5"),
    MIN("m3", "M5"),
    AUG("M3", "A5"),
    DIM("m3", "d5"),
    DOM7("M3", "M5", "m7"),
    MAJ7("M3", "M5", "M7"),
    MINMAJ7("m3", "M5", "M7"),
    MIN7("m3", "M5", "m7"),
    AUGMAJ7("M3", "A5", "M7"),
    AUG7("M3", "A5", "m7"),
    DIMMIN7("m3", "d5", "m7"),
    DIM7("m3", "d5", "d7"),
    DOM7DIM5("M3","d5", "m7");
    private Interval[] intervals;

    EnumChord(String... intervalsIn) {
        this.intervals = Interval.getIntervals(intervalsIn);
    }

    public Interval[] getIntervals() {
        return intervals;
    }

    public static EnumChord getEnumChord(Chord chord) {
        for (EnumChord enumChord : values()) if (Arrays.equals(chord.getIntervals(), enumChord.getIntervals())) return enumChord;
        return null;
    }

    public static EnumChord getEnumChord(String nameIn) {
        for (EnumChord enumChord : values()) if (nameIn.equalsIgnoreCase(enumChord.name())) return enumChord;
        return null;
    }

    public Chord getChord(Note base) {
        return new Chord(base, intervals);
    }
}
