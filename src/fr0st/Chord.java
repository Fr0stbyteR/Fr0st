package fr0st;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Chord implements Cloneable, Tonal {
    protected Note base;
    protected Interval[] intervals; // Intervals from base

    public Chord(Pitch... pitches) throws IllegalArgumentException {
        if (pitches.length <= 1) throw new IllegalArgumentException("At least 2 notes/pitches");
        ArrayList<Pitch> pitches1 = new ArrayList<>(Arrays.asList(pitches));
        Collections.sort(pitches1);
        this.base = pitches1.get(0);
        this.intervals = new Interval[pitches.length - 1];
        for (int i = 0; i < pitches1.size() - 1; i++) {
            this.intervals[i] = new Interval(this.base, pitches1.get(i + 1));
        }
    }

    public Chord(Note... notes) throws IllegalArgumentException {
        if (notes.length <= 1) throw new IllegalArgumentException("At least 2 notes/pitches");
        this.base = notes[0];
        this.intervals = new Interval[notes.length - 1];
        for (int i = 0; i < notes.length - 1; i++) {
            this.intervals[i] = new Interval(this.base, notes[i + 1]);
        }
    }

    public Chord(String... strings) {
        if (strings.length <= 1) throw new IllegalArgumentException("At least 2 notes/pitches");
        ArrayList<Pitch> pitches = new ArrayList<>();
        boolean isPitch = false;
        try {
            this.base = new Note(strings[0]);
        } catch (IllegalArgumentException e) {
            pitches.add(new Pitch(strings[0]));
            isPitch = true;
        }
        this.intervals = new Interval[strings.length - 1];
        for (int i = 1; i < strings.length; i++) {
            if (isPitch) {
                try {
                    pitches.add(new Pitch(strings[i]));
                } catch (IllegalArgumentException e) {
                    pitches.add(pitches.get(0).getNext(new Note(strings[i])));
                }
            } else {
                this.intervals[i - 1] = new Interval(this.base, new Note(strings[i]));
            }
        }
        if (isPitch) {
            Collections.sort(pitches);
            this.base = pitches.get(0);
            for (int i = 0; i < pitches.size() - 1; i++) {
                this.intervals[i] = new Interval(this.base, pitches.get(i + 1));
            }
        }
    }

    public Chord(Note baseIn, Interval... intervals) {
        this.base = baseIn;
        this.intervals = intervals;
    }

    public Chord(Note baseIn, EnumChord enumChordIn) {
        this.base = baseIn;
        this.intervals = enumChordIn.getIntervals();
    }

    public Chord(Pitch pitchIn, EnumChord enumChordIn) {
        this.base = pitchIn;
        this.intervals = enumChordIn.getIntervals();
    }

    public Chord(String baseIn, EnumChord enumChordIn) {
        try {
            this.base = new Note(baseIn);
        } catch (IllegalArgumentException e) {
            this.base = new Pitch(baseIn);
        }
        this.intervals = enumChordIn.getIntervals();
    }

    protected Chord(Chord that) {
        this.base = that.getBase();
        this.intervals = that.getIntervals();
    }

    public int[] getSubHarmonicCount() {
        int[][] ratios = new int[this.intervals.length][2];
        Interval[] intervals1 = this.intervals;
        for (int i = 0; i < intervals1.length; i++) {
            ratios[i] = intervals1[i].getSubHarmonicCount();
            /*
            ratios[i] = i == 0
                    ? intervals1[i].getSubHarmonicCount()
                    : Interval.diff(intervals1[i], intervals1[i - 1]).getSubHarmonicCount();*/
        }
        return getBasedMultipleRatio(ratios);
    }

    public int[] getSupHarmonicCount() {
        int[][] ratios = new int[this.intervals.length][2];
        Interval[] intervals1 = this.intervals;
        for (int i = 0; i < intervals1.length; i++) {
            ratios[i] = intervals1[i].getSupHarmonicCount();/*
            ratios[i] = i == 0
                    ? intervals1[i].getSupHarmonicCount()
                    : Interval.diff(intervals1[i], intervals1[i - 1]).getSupHarmonicCount();*/
        }
        return getBasedMultipleRatio(ratios);
    }

    public static int[] getConcatMultipleRatio(int[]... ratios) { // [1:2][2:3:4] => [1:2:3:4]
        if (ratios.length == 0) return new int[0];
        if (ratios.length == 1) return ratios[0];
        int length = 0;
        for (int[] ratio1 : ratios) {
            length += ratio1.length;
        }
        length -= ratios.length - 1;
        int[] ratio = new int[length];

        for (int i = 0; i < ratios.length - 1; i++) {
            int[] ratio1 = ratios[i];
            int[] ratio2 = ratios[i + 1];
            int lcm = lcm(ratio1[ratio1.length - 1], ratio2[0]);
            int multiplier1 = lcm / ratio1[ratio1.length - 1];
            int multiplier2 = lcm / ratio2[0];
            if (multiplier1 != 1) {
                for (int j = 0; j <= i; j++) {
                    for (int k = 0; k < ratios[j].length; k++) {
                        ratios[j][k] *= multiplier1;
                    }
                }
            }
            if (multiplier2 != 1) {
                for (int k = 0; k < ratios[i + 1].length; k++) {
                    ratios[i + 1][k] *= multiplier2;
                }
            }
        }

        ratio[0] = ratios[0][0];
        int index = 1;
        for (int i = 0; i < ratios.length; i++) {
            for (int j = 1; j < ratios[i].length; j++) {
                ratio[index] = ratios[i][j];
                index++;
            }
        }
        return ratio;
    }


    public static int[] getBasedMultipleRatio(int[]... ratios) { //[1:2][1:3;4] => [1:2:3:4]
        if (ratios.length == 0) return new int[0];
        if (ratios.length == 1) return ratios[0];
        int length = 0;
        for (int[] ratio1 : ratios) {
            length += ratio1.length;
        }
        length -= ratios.length - 1;
        int[] ratio = new int[length];

        int lcm = ratios[0][0];
        for (int i = 1; i < ratios.length; i++) {
             lcm = lcm(lcm, ratios[i][0]);
        }
        for (int i = 0; i < ratios.length; i++) {
            int[] ratio1 = ratios[i];
            int multiplier = lcm / ratio1[0];
            if (multiplier != 1) {
                for (int j = 0; j < ratios[i].length; j++) {
                    ratios[i][j] *= multiplier;
                }
            }
        }
        ratio[0] = ratios[0][0];
        int index = 1;
        for (int i = 0; i < ratios.length; i++) {
            for (int j = 1; j < ratios[i].length; j++) {
                ratio[index] = ratios[i][j];
                index++;
            }
        }
        return ratio;
    }

    public double getHarmonic() {
        int sumSub = 0, sumSup = 0;
        for (int i : getSubHarmonicCount()) sumSub += i;
        for (int i : getSupHarmonicCount()) sumSup += i;
        return sumSup;
        //return 1D / ((sumSub + sumSup) / (this.intervals.length + 1) / (this.intervals.length + 1));
    }

    public int size() {
        return intervals.length + 1;
    }

    public Chord getInversion(int inversion) {
        Chord newChord = this.clone();
        return newChord.inverse(inversion);
    }

    public Chord inverseUp() {
        Interval interval0 = intervals[0];
        base = base.ascend(intervals[0]);
        for (int i = 0; i < intervals.length - 1; i++) {
            intervals[i] = intervals[i + 1].minus(interval0);
        }
        intervals[intervals.length - 1] = interval0.minusByOctave();
        return this;
    }

    public Chord inverseDown() {
        Interval interval0 = intervals[intervals.length - 1].minusByOctave();
        base = base.descend(interval0);
        for (int i = intervals.length - 1; i > 0; i--) {
            intervals[i] = intervals[i - 1].plus(interval0);
        }
        intervals[0] = interval0;
        return this;
    }

    public Chord inverse(int inversion) {
        if (inversion > 0) {
            for (int i = 0; i < inversion; i++) {
                this.inverseUp();
            }
        }
        if (inversion < 0) {
            for (int i = 0; i > inversion; i--) {
                this.inverseDown();
            }
        }
        return this;
    }

    public Chord ascend(Interval intervalIn) {
        this.base.ascend(intervalIn);
        return this;
    }

    public Chord descend(Interval intervalIn) {
        this.base.descend(intervalIn);
        return this;
    }

    public Note[] getNotes() {
        Note[] notes = new Note[this.intervals.length + 1];
        notes[0] = this.base;
        for (int i = 0; i < this.intervals.length; i++) {
            notes[i + 1] = this.base.clone().ascend(this.intervals[i]);
        }
        return notes;
    }

    public boolean contains(Note noteIn) {
        for (Note note : getNotes()) {
            if (note.equals(noteIn)) return true;
            if (note.getClass() == Pitch.class) {
                if (((Pitch) note).getNote().equals(noteIn)) return true;
            }
        }
        return false;
    }

    public boolean contains(Pitch pitchIn) {
        for (Note note : getNotes()) {
            if (note.equals(pitchIn.getNote())) return true;
            if (note.equals(pitchIn)) return true;
        }
        return false;
    }

    public static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public static int lcm(int a, int b) {
        return a * (b / gcd(a, b));
    }

    public Note getBase() {
        return base;
    }

    public void setBase(Note baseIn) {
        this.base = baseIn;
    }

    public Interval[] getIntervals() {
        return intervals;
    }

    public void setIntervals(Interval[] intervalsIn) {
        this.intervals = intervalsIn;
    }

    public Chord setIntervals(EnumChord enumChord) {
        this.intervals = enumChord.getIntervals();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chord chord = (Chord) o;

        if (base != null ? !base.equals(chord.base) : chord.base != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(intervals, chord.intervals);

    }

    @Override
    public int hashCode() {
        int result = base != null ? base.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(intervals);
        return result;
    }

    @Override
    public String toString() {
        return "Chord{" +
                "base=" + base +
                ", intervals=" + Arrays.toString(intervals) +
                '}';
    }

    @Override
    public Chord clone() {
        return new Chord(this);
    }

    @Override
    public double getStability(Tonality tonalityIn) {
        double stability = 0D;
        for (Note note : this.getNotes()) {
            stability += note.getStability(tonalityIn);
        }
        return stability / (this.intervals.length + 1);
    }

    @Override
    public double getTendency(Note noteIn) {
        double tendency = 0D;
        for (Note note : this.getNotes()) {
            tendency += note.getTendency(noteIn);
        }
        return tendency / (this.intervals.length + 1);
    }

    @Override
    public double getTendency(Tonality tonalityIn) {
        return this.getTendency(new Chord(tonalityIn.getNote(), EnumChord.MAJ));
    }

    @Override
    public Color getColor(Tonality tonalityIn) {
        Color color = new Color(0D, 0D, 0D);
        double[] arrayColor = color.toArray();
        for (Note note : this.getNotes()) {
            double[] color1 = note.getColor(tonalityIn).toArray();
            for (int i = 0; i < color1.length; i++) {
                arrayColor[i] += color1[i];
            }
        }
        for (int i = 0; i < arrayColor.length; i++) {
            arrayColor[i] /= this.size();
        }
        color.set(arrayColor);
        return color;
    }

    public double getTendency(Chord chordIn) {
        double tendency = 0D;
        for (Note note1 : this.getNotes()) {
            double note1Max = 0D;
            for (Note note2 : chordIn.getNotes()) {
                double note2Tendency = note1.getTendency(note2);
                if (note2Tendency > note1Max) note1Max = note2Tendency;
            }
            tendency += note1Max;
        }
        return tendency / (this.intervals.length + 1);
    }
}
