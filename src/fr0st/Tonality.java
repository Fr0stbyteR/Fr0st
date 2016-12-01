package fr0st;

public class Tonality implements Cloneable {
    protected Note note;
    protected Scale scale;

    public Tonality(Note noteIn, Scale scaleIn) {
        if (noteIn.getClass() == Pitch.class) this.note = ((Pitch) noteIn).getNote();
        else this.note = noteIn;
        this.scale = scaleIn;
    }

    public Tonality(String tonalityIn) throws IllegalArgumentException {
        try {
            this.note = new Note(tonalityIn);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No such tonality.");
        }
        this.scale = tonalityIn.substring(0, 1).matches("[A-G]") ? Scale.MAJOR : Scale.MINOR;
    }

    public Tonality(Tonality that) {
        this.note = that.note;
        this.scale = that.scale;
    }

    public Note[] getNotes() {
        Note[] notes = new Note[this.scale.size()];
        for (int i = 0; i < notes.length; i++) {
            notes[i] = this.note.clone().ascend(this.scale.getInterval()[i]);
        }
        return notes;
    }

    public void toRelative() throws IllegalArgumentException {
        if (this.getScale() == Scale.MAJOR) {
            this.note.descend(new Interval("m3"));
            this.setScale(Scale.MINOR);
        }
        if (this.getScale() == Scale.MAJOR) {
            this.note.ascend(new Interval("m3"));
            this.setScale(Scale.MAJOR);
        }
        throw new IllegalArgumentException("Relative of found");
    }

    public void ascend(Interval intervalIn) {
        this.note.ascend(intervalIn);
    }

    public void descend(Interval intervalIn) {
        this.note.descend(intervalIn);
    }

    public void toNext() {
        this.note.ascend(new Interval("P5"));
    }

    public void toPrev() {
        this.note.descend(new Interval("P5"));
    }


    public Note getNoteFromDegree(Scale.Degree degreeIn) {
        return this.note.clone().ascend(this.scale.getIntervalFromDegree(degreeIn));
    }

    public Note getNoteFromDegree(int degreeIn) {
        return this.note.clone().ascend(this.scale.getIntervalFromDegree(degreeIn));
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note noteIn) {
        this.note = noteIn;
    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scaleIn) {
        this.scale = scaleIn;
    }

    @Override
    public String toString() {
        return note.toString() + scale.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tonality tonality = (Tonality) o;

        if (note != null ? !note.equals(tonality.note) : tonality.note != null) return false;
        return scale == tonality.scale;

    }

    @Override
    public int hashCode() {
        int result = note != null ? note.hashCode() : 0;
        result = 31 * result + (scale != null ? scale.hashCode() : 0);
        return result;
    }

    @Override
    protected Tonality clone() {
        return new Tonality(this);
    }
}
