package fr0st;

import java.util.Arrays;

public class Scale {
    public static final Scale MAJOR = new Scale("Major", "P1:Tonic", "M2:Supertonic", "M3:Mediant", "P4:Subdominant", "P5:Dominant", "M6:Submediant", "M7:Leading");
    public static final Scale MINOR = new Scale("Minor", "P1:Tonic", "M2:Supertonic", "m3:Mediant", "P4:Subdominant", "P5:Dominant", "m6:Submediant", "m7:Subtonic");
    public static final Scale PENTA = new Scale("Penta", "P1:Gong", "M2:Shang", "M3:Jiao", "P5:Zhi", "M6:Yu");
    private String name;
    private Note[] scale;

    public Scale(String nameIn, String... noteIn) {
        this.name = nameIn;
        this.scale = Note.getNotes(noteIn);
    }

    public String getName() {
        return name;
    }

    public Note[] getScale() {
        return scale;
    }

    public int size() {
        return scale.length;
    }

    public Interval[] getInterval() {
        Interval[] intervals = new Interval[this.scale.length];
        for (int i = 0; i < this.scale.length; i++) {
            intervals[i] = this.scale[i].getInterval();
        }
        return intervals;
    }

    public Interval getIntervalFromDegree(int degreeIn) {
        for (Note note : this.scale) {
            if (degreeIn == note.getDegree()) return note.getInterval();
        }
        return null;
    }

    public Interval getIntervalFromDegree(Degree degreeIn) {
        for (Note note : this.scale) {
            if (degreeIn == Degree.getDegree(note.getDegree())) return note.getInterval();
        }
        return null;
    }

    public String[] getNames() {
        String[] names = new String[this.scale.length];
        for (int i = 0; i < this.scale.length; i++) {
            names[i] = this.scale[i].getName();
        }
        return names;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Scale scale1 = (Scale) o;

        if (name != null ? !name.equals(scale1.name) : scale1.name != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(scale, scale1.scale);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(scale);
        return result;
    }

    @Override
    public String toString() {
        return "Scale{" +
                "name='" + name + '\'' +
                ", scale=" + Arrays.toString(scale) +
                '}';
    }

    private static class Note {
        private Interval interval;
        private String name;
        private int degree;

        public Note(Interval interval, String name, int degree) {
            this.interval = interval;
            this.name = name;
            this.degree = degree;
        }

        public Note(Interval interval, String name) {
            this.interval = interval;
            this.name = name;
            this.degree = this.interval.getDegree();
        }

        public Note(String interval, String name) {
            this.interval = new Interval(interval);
            this.name = name;
            this.degree = this.interval.getDegree();
        }

        public Note(String interval_name) {
            this.interval = new Interval(interval_name.split("\\:")[0]);
            this.name = interval_name.split("\\:")[1];
            this.degree = this.interval.getDegree();
        }

        public Interval getInterval() {
            return interval;
        }

        public String getName() {
            return name;
        }

        public int getDegree() {
            return degree;
        }

        public static Note[] getNotes(String... interval_names) {
            Note[] notes = new Note[interval_names.length];
            for (int i = 0; i < interval_names.length; i++) notes[i] = new Note(interval_names[i]);
            return notes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Note note = (Note) o;

            if (degree != note.degree) return false;
            if (interval != null ? !interval.equals(note.interval) : note.interval != null) return false;
            return name != null ? name.equals(note.name) : note.name == null;

        }

        @Override
        public int hashCode() {
            int result = interval != null ? interval.hashCode() : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + degree;
            return result;
        }

        @Override
        public String toString() {
            return interval.toString() + ":" + name;
        }
    }

    public enum Degree {
        I(1),
        II(2),
        III(3),
        IV(4),
        V(5),
        VI(6),
        VII(7);
        int degree;

        Degree(int degreeIn) {
            this.degree = degreeIn;
        }

        public int getDegree() {
            return degree;
        }

        public static Degree getDegree(int degreeIn) {
            for (Degree degree : values()) if (degreeIn == degree.getDegree()) return degree;
            return null;
        }

        public static Degree getDegree(String nameIn) {
            for (Degree degree : values()) if (nameIn.equalsIgnoreCase(degree.name())) return degree;
            return null;
        }
    }
}
