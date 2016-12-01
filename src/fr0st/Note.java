package fr0st;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Note implements Cloneable, Tonal {
    private static final Pattern REGEX = Pattern.compile("^([a-gA-G])([bs#]*)$");
    protected EnumNote enumNote;
    protected int alteration;

    public Note() {
        this.setNote("C");
    }

    public Note(String nameIn) {
        this.setNote(nameIn);
    }

    public Note(EnumNote enumNoteIn, int alterationIn) {
        this.enumNote = enumNoteIn;
        this.alteration = alterationIn;
    }

    public Note(int offsetIn) {
        this.setOffset(offsetIn);
    }

    protected Note(Note that) {
        this.enumNote = that.getEnumNote();
        this.alteration = that.getAlteration();
    }

    public Note ascend(Interval intervalIn) {
        EnumNote newEnumNote = EnumNote.getEnumNoteByIndex(this.enumNote.getIndex() + intervalIn.getDegree() - 1);
        if (newEnumNote == null) return this;
        this.alteration += (intervalIn.getDifference() - 12 * intervalIn.getOctave()) - Math.floorMod(newEnumNote.getOffset() - this.enumNote.getOffset(), 12);
        this.enumNote = newEnumNote;
        return this;
    }

    public Note descend(Interval intervalIn) {
        EnumNote newEnumNote = EnumNote.getEnumNoteByIndex(this.enumNote.getIndex() - intervalIn.getDegree() + 1);
        if (newEnumNote == null) return this;
        this.alteration -= (intervalIn.getDifference() - 12 * intervalIn.getOctave()) - Math.floorMod(this.enumNote.getOffset() - newEnumNote.getOffset(), 12);
        this.enumNote = newEnumNote;
        return this;
    }

    public Note getRelative(Scale scaleIn) throws IllegalArgumentException {
        Note relative = this.clone();
        if (scaleIn == Scale.MAJOR) return relative.ascend(new Interval("m3"));
        if (scaleIn == Scale.MAJOR) return relative.descend(new Interval("m3"));
        throw new IllegalArgumentException("Scale not Supported");
    }

    public void setOffset(int offsetIn) {
        int[] interval = Interval.getIntervalFromOffset(offsetIn);
        this.enumNote = EnumNote.getEnumNoteByOffset(Interval.getOffsetFromDegree(interval[0]));
        this.alteration = interval[1];
    }

    public void setNote(String nameIn) throws IllegalArgumentException {
        Matcher matcher = REGEX.matcher(nameIn);
        if (matcher.find()) {
            this.enumNote = EnumNote.getEnumNote(matcher.group(1));
            this.alteration = 0;
            String stringAlteration = matcher.group(2);
            while (stringAlteration.length() > 0) {
                String s = stringAlteration.substring(0, 1);
                if (s.equals("s") || s.equals("#")) this.alteration++;
                else alteration--;
                stringAlteration = stringAlteration.substring(1);
            }
        } else throw new IllegalArgumentException("No such note.");
    }

    public Pitch getPitch(int octaveIn) {
        return new Pitch(this, octaveIn);
    }

    public EnumNote getEnumNote() {
        return enumNote;
    }

    public void setEnumNote(EnumNote enumNoteIn) {
        this.enumNote = enumNoteIn;
    }

    public int getAlteration() {
        return alteration;
    }

    public void setAlteration(int alterationIn) {
        this.alteration = alterationIn;
    }

    public int getOffset() {
        return this.enumNote.getOffset() + alteration;
    }

    public int getDistance(Note noteIn) {
        int distance = Math.abs(this.enumNote.getOffset() + this.alteration) - (noteIn.enumNote.getOffset() + noteIn.alteration);
        return distance > 6 ? 12 - distance : distance;
    }

    @Override
    public String toString() {
        String string = this.enumNote.name();
        int alter = this.alteration;
        while (alter != 0) {
            if (alter > 0) {
                string += "s";
                alter--;
            } else {
                string += "b";
                alter++;
            }
        }
        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Note note1 = (Note) o;

        if (alteration != note1.alteration) return false;
        return enumNote == note1.enumNote;

    }

    @Override
    public int hashCode() {
        int result = enumNote != null ? enumNote.hashCode() : 0;
        result = 31 * result + alteration;
        return result;
    }

    @Override
    public Note clone() {
        return new Note(this);
    }

    @Override
    public double getStability(Tonality tonalityIn) {
        for (Note note :tonalityIn.getNotes()) {
            if (this.equals(note)) return 1D;
        }
        return 0D;
    }

    @Override
    public double getTendency(Note noteIn) {
        int distance = this.getDistance(noteIn);
        return distance == 0 ? 1D : 1D / distance;
    }

    @Override
    public double getTendency(Tonality tonalityIn) {
        int distance = this.getDistance(tonalityIn.note);
        return distance == 0 ? 0D : 1D / distance;
    }

    @Override
    public Color getColor(Tonality tonalityIn) {
        Note base = tonalityIn.getNote().clone();
        Color color = new Color();
        if (tonalityIn.getScale() == Scale.MAJOR) {
            Chord chord = new Chord(base.descend(new Interval("m3")), EnumChord.MIN7);
            if (this.equals(chord.getNotes()[0])) color.setT(0.5D);
            if (this.equals(chord.getNotes()[1])) color.setT(1D);
            if (this.equals(chord.getNotes()[2])) color.setT(0.75D);
            if (this.equals(chord.getNotes()[3])) color.setT(0.5D);
            chord.ascend(new Interval("P4"));
            if (this.equals(chord.getNotes()[0])) color.setS(0.5D);
            if (this.equals(chord.getNotes()[1])) color.setS(1D);
            if (this.equals(chord.getNotes()[2])) color.setS(0.75D);
            if (this.equals(chord.getNotes()[3])) color.setS(0.5D);
            chord.ascend(new Interval("M2"));
            if (this.equals(chord.getNotes()[0])) color.setD(0.5D);
            if (this.equals(chord.getNotes()[1])) color.setD(1D);
            if (this.equals(chord.getNotes()[2])) color.setD(0.75D);
            if (this.equals(chord.getNotes()[3])) color.setD(0.5D);
        }
        if (tonalityIn.getScale() == Scale.MINOR) {
            Chord chord = new Chord(base, EnumChord.MIN);
            if (this.equals(chord.getNotes()[0])) color.setT(1D);
            if (this.equals(chord.getNotes()[1])) color.setT(1D);
            if (this.equals(chord.getNotes()[2])) color.setT(0.75D);
            chord.ascend(new Interval("P4"));
            if (this.equals(chord.getNotes()[0])) color.setS(1D);
            if (this.equals(chord.getNotes()[1])) color.setS(1D);
            if (this.equals(chord.getNotes()[2])) color.setS(0.75D);
            chord.ascend(new Interval("M2")).contains(this);
            if (this.equals(chord.getNotes()[0])) color.setD(1D);
            if (this.equals(chord.getNotes()[1])) color.setD(1D);
            if (this.equals(chord.getNotes()[2])) color.setD(0.75D);
            chord.setIntervals(EnumChord.MAJ).contains(this);
            if (this.equals(chord.getNotes()[1])) color.setD(1D);
        }
        return color;
    }
}
