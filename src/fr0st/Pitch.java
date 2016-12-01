package fr0st;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pitch extends Note implements Cloneable, Comparable<Pitch>, Tonal {
    private static final Pattern REGEX = Pattern.compile("^([a-gA-G][bs#]*)(-?\\d)$");
    private int octave;

    public Pitch(String nameIn) throws IllegalArgumentException {
        Matcher matcher = REGEX.matcher(nameIn);
        if (matcher.find()) {
            super.setNote(matcher.group(1));
            this.octave = Integer.valueOf(matcher.group(2));
        } else throw new IllegalArgumentException("No such pitch.");
    }

    public Pitch(Note noteIn, int octaveIn) {
        this.enumNote = noteIn.enumNote;
        this.alteration = noteIn.alteration;
        this.octave = octaveIn;
    }

    public Pitch(int indexIn) {
        super(indexIn % 12);
        this.octave = indexIn / 12;
    }

    public Pitch(EnumNote enumNoteIn, int alterationIn, int octaveIn) {
        super(enumNoteIn, alterationIn);
        this.octave = octaveIn;
    }

    public Pitch(Pitch that) {
        this.enumNote = that.enumNote;
        this.alteration = that.alteration;
        this.octave = that.getOctave();
    }

    @Override
    public Note ascend(Interval intervalIn) {
        this.octave += (this.enumNote.getIndex() + intervalIn.getDegree() - 1) / 7 + intervalIn.getOctave();
        return super.ascend(intervalIn);
    }

    @Override
    public Note descend(Interval intervalIn) {
        this.octave += (int) Math.floor((this.enumNote.getIndex() - intervalIn.getDegree() + 1) / 7D) - intervalIn.getOctave();
        return super.descend(intervalIn);
    }

    public Pitch getNearest(Note noteIn) {
        Pitch pitch = new Pitch(noteIn, this.octave);
        int diff = this.getIndex() - pitch.getIndex();
        if (diff > 6) pitch.nextOctave();
        else if (diff < -6) pitch.prevOctave();
        return pitch;
    }

    public Pitch getNext(Note noteIn) {
        Pitch pitch = new Pitch(noteIn, this.octave);
        int diff = this.getIndex() - pitch.getIndex();
        if (diff >= 0) pitch.nextOctave();
        return pitch;
    }

    public Pitch getPrev(Note noteIn) {
        Pitch pitch = new Pitch(noteIn, this.octave);
        int diff = this.getIndex() - pitch.getIndex();
        if (diff <= 0) pitch.prevOctave();
        return pitch;
    }

    public int getIndex() {
        return this.getOffset() + 12 * (this.octave + 1);
    }

    public int getMidiNote() {
        return getIndex();
    }

    public double getFrequency() {
        return Frequency.getFrequency(this);
    }

    public boolean equalsMidi(Pitch pitchIn) {
        return this.getMidiNote() == pitchIn.getMidiNote();
    }

    public Note getNote() {
        return new Note(this.enumNote, this.alteration);
    }

    public void setNote(Note noteIn) {
        this.enumNote = noteIn.getEnumNote();
        this.alteration = noteIn.getAlteration();
    }

    public int getOctave() {
        return octave;
    }

    public void setOctave(int octaveIn) {
        this.octave = octaveIn;
    }

    public void nextOctave() {
        this.octave++;
    }

    public void prevOctave() {
        this.octave--;
    }

    public int getDistance(Pitch pitchIn) {
        return Math.abs(this.getIndex() - pitchIn.getIndex());
    }

    @Override
    public String toString() {
        return super.toString() + String.valueOf(octave);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Pitch pitch = (Pitch) o;

        if (alteration != pitch.alteration) return false;
        if (octave != pitch.octave) return false;
        return enumNote == pitch.enumNote;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (enumNote != null ? enumNote.hashCode() : 0);
        result = 31 * result + alteration;
        result = 31 * result + octave;
        return result;
    }

    @Override
    public Pitch clone(){
        return new Pitch(this);
    }

    @Override
    public int compareTo(Pitch pitchIn) {
        return this.getIndex() - pitchIn.getIndex();
    }

    @Override
    public double getStability(Tonality tonalityIn) {
        for (Note note :tonalityIn.getNotes()) {
            if (this.getNote().equals(note)) return 1D;
        }
        return 0D;
    }

    @Override
    public double getTendency(Note noteIn) {
        int distance = noteIn.getClass() == Pitch.class
                ? this.getDistance((Pitch) noteIn)
                : this.getDistance(this.getNearest(noteIn));
        return distance == 0 ? 1D : 1D / distance;
    }

    @Override
    public double getTendency(Tonality tonalityIn) {
        int distance = this.getDistance(this.getNearest(tonalityIn.note));
        return distance == 0 ? 1D : 1D / distance;
    }

    @Override
    public Color getColor(Tonality tonalityIn) {
        return this.getNote().getColor(tonalityIn);
    }
}
