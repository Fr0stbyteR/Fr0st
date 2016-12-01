package fr0st;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        //System.out.println(Math.pow(Frequency.SEMITONE, 7));
        Pitch pitch1 = new Pitch("E2");
        Pitch pitch2 = new Pitch("G2");
        Pitch pitch3 = new Pitch("C2");
        Pitch pitch4 = new Pitch("C3");
        Note note1 = new Note("Bb");
        Note note2 = new Note("G");/*
        Note note3 = new Note("G");
        Note note4 = new Note("G");*/
        Chord chord0 = new Chord("C2", "E2", "G2", "Bb2");
        Chord chord1 = new Chord("F1", EnumChord.MAJ);
        Chord chord2 = new Chord("G2", EnumChord.MAJ);
        Interval interval = new Interval("P4");
        Interval interval1 = new Interval("M9");
        Interval interval2 = new Interval("d1");

/*
        HashMap<Chord, Integer> chords = new HashMap<>();
        for (int i = 1; i < 12; i++) {
            for (int j = 1; j < 12; j++) {
                if (j == i) break;
                for (int k = 1; k < 12; k++) {
                    if (k == i || k == j) break;
                    Note note0 = new Note("C");
                    Note note1 = new Note(i);
                    Note note2 = new Note(j);
                    Note note3 = new Note(k);
                    Chord chord = new Chord(note0, note1, note2, note3);
                    System.out.println(chord.getHarmonic() + " C" + note1.toString() + note2.toString() + note3.toString());
                }
            }
        }
*/

        System.out.println(chord2.getColor(new Tonality("c")));
        System.out.println(chord1.getTendency(chord2));
        System.out.println(Arrays.toString(interval2.getSubHarmonicCount()));
        //System.out.println(chord1.getHarmonic());
        //System.out.println(chord2.getHarmonic());
        //Interval interval = new Interval(note1, note2);
        //System.out.println(new Interval(pitch1, pitch2));
        System.out.println(Arrays.toString(chord2.getSubHarmonicCount()));
        System.out.println(Arrays.toString(chord2.getSupHarmonicCount()));
        //System.out.println(interval.toString());
        //System.out.println(note1.ascend(new Interval("m7")));
        Tonality tonality = new Tonality("f");
        Interval octave = new Interval("P8");
        //System.out.println(Arrays.toString(Tonality.Scale.MINOR.getScale()));
        //System.out.println(noteT1.getDistance());
        //System.out.println(octave.minus(interval));
        //System.out.println(interval.minusByOctave());
        //System.out.println(interval1.plus(interval2));
        //System.out.println(chord1.inverse(3));
    }

}
