package fr0st;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interval implements Cloneable {
    private static final Pattern REGEX = Pattern.compile("^([PMmAd])([0-9]+)((\\+|-)?\\d*)$");
    public static final int[] DEGREE_TO_OFFSET = {0, 2, 4, 5, 7, 9, 11};
    private int degree, offset, octave;

    public static int[] getIntervalFromString(String nameIn) throws IllegalArgumentException {
        int degree = 0, offset = 0, octave = 0;
        Matcher matcher = REGEX.matcher(nameIn);
        if (matcher.find()) {
            degree = Integer.valueOf(matcher.group(2));
            offset = getOffsetFromProperty(Property.getProperty(matcher.group(1)), degree);
            if (matcher.group(3).length() != 0) octave = Integer.valueOf(matcher.group(3));
        } else throw new IllegalArgumentException("No such interval");
        return new int[]{degree, offset, octave};
    }

    public static int getOffsetFromDegree(int degreeIn) {
        return DEGREE_TO_OFFSET[Math.floorMod(degreeIn - 1, 7)] + 12 * (int) Math.floor((degreeIn - 1) / 7D);
    }

    public static int getOffsetFromInterval(int degreeIn, int offsetIn, int octaveIn) {
        return DEGREE_TO_OFFSET[Math.floorMod(degreeIn - 1, 7)] + 12 * (int) Math.floor((degreeIn - 1) / 7D) + offsetIn + 12 * octaveIn;
    }

    public static int[] getIntervalFromOffset(int offsetIn) {
        int degree = 0, offset = 0, octave;
        octave = (int) Math.floor(offsetIn / 12D);
        for (int i = 0; i < DEGREE_TO_OFFSET.length; i++) {
            if (DEGREE_TO_OFFSET[i] == Math.floorMod(offsetIn, 12)) {
                degree = i + 1;
                offset = 0;
                break;
            } else if (DEGREE_TO_OFFSET[i] == Math.floorMod(offsetIn, 12) + 1) {
                degree = i + 1;
                offset = -1;
                break;
            }
        }
        return new int[]{degree, offset, octave};
    }

    public Interval(int degreeIn, int offsetIn) {
        this.degree = Math.floorMod(degreeIn - 1, 7) + 1;
        this.offset = offsetIn;
        this.octave = (int) Math.floor((degreeIn - 1) / 7D);
    }

    public Interval(double ratioIn) {
        int difference = getOffsetFromRatio(ratioIn);
        int[] interval = getIntervalFromOffset(difference);
        this.degree = interval[0];
        this.offset = interval[1];
        this.octave = interval[2];
    }

    public Interval(int differenceIn) {
        int[] interval = getIntervalFromOffset(differenceIn);
        this.degree = interval[0];
        this.offset = interval[1];
        this.octave = interval[2];
    }

    public Interval(int degreeIn, Property propertyIn) {
        this.degree = Math.floorMod(degreeIn - 1, 7) + 1;
        this.offset = this.getOffsetFromProperty(propertyIn);
        this.octave = (int) Math.floor((degreeIn - 1) / 7D);
    }

    public Interval(int degreeIn, int offsetIn, int octaveIn) {
        this.degree = Math.floorMod(degreeIn - 1, 7) + 1;
        this.offset = offsetIn;
        this.octave = (int) Math.floor((degreeIn - 1) / 7D) + octaveIn;
    }

    public Interval(Note note1, Note note2) {
        this.degree = Math.abs(note1.getEnumNote().getIndex() - note2.getEnumNote().getIndex()) % 7 + 1;
        this.offset = Math.abs(note1.getOffset() - note2.getOffset()) % 12 - getOffsetFromDegree(this.degree);
        this.octave = 0;
    }

    public Interval(String nameIn) {
        int[] interval = getIntervalFromString(nameIn);
        this.degree = Math.floorMod(interval[0] - 1, 7) + 1;
        this.offset = interval[1];
        this.octave = (int) Math.floor((interval[0] - 1) / 7D) + interval[2];
    }

    public Interval(Pitch pitch1, Pitch pitch2) {
        Note noteLow, noteHigh;
        if (pitch1.getIndex() < pitch2.getIndex()) {
            noteLow = pitch1.getNote();
            noteHigh = pitch2.getNote();
        } else {
            noteLow = pitch2.getNote();
            noteHigh = pitch1.getNote();
        }
        this.degree = Math.floorMod(noteHigh.getEnumNote().getIndex() - noteLow.getEnumNote().getIndex(), 7) + 1;
        this.offset = Math.abs(pitch1.getIndex() - pitch2.getIndex()) % 12 - getOffsetFromDegree(this.degree);
        this.octave = Math.abs(pitch1.getIndex() - pitch2.getIndex()) / 12;
    }

    protected Interval(Interval that) {
        this.degree = that.getDegree();
        this.offset = that.getOffset();
        this.octave = that.getOctave();
    }

    public Interval plus(Interval intervalIn) {
        int[] interval = new int[3];
        interval[0] = Math.floorMod(this.degree + intervalIn.getDegree() - 1 - 1, 7) + 1;
        interval[1] = this.getDifference() - 12 * this.octave + intervalIn.getDifference() - 12 * intervalIn.getOctave() - getOffsetFromDegree(this.degree + intervalIn.getDegree() - 1);
        interval[2] = this.octave + intervalIn.getOctave() + (this.degree + intervalIn.getDegree() - 1 - 1) / 7;
        this.degree = interval[0];
        this.offset = interval[1];
        this.octave = interval[2];
        return this;
    }

    public Interval minus(Interval intervalIn) {
        int[] interval = new int[3];
        interval[0] = Math.floorMod(this.degree - intervalIn.getDegree() + 1 - 1, 7) + 1;
        interval[1] = (this.getDifference() - 12 * this.octave) - (intervalIn.getDifference() - 12 * intervalIn.getOctave()) - getOffsetFromDegree(this.degree - intervalIn.getDegree() + 1);
        interval[2] = this.octave - intervalIn.getOctave() + (int) Math.floor((this.degree - intervalIn.getDegree() + 1 - 1) / 7D);
        this.degree = interval[0];
        this.offset = interval[1];
        this.octave = interval[2];
        return this;
    }

    public Interval minusByOctave() {
        int[] interval = new int[3];
        interval[0] = Math.floorMod(1 - this.degree, 7) + 1;
        interval[1] = 0 - (this.getDifference() - 12 * this.octave) - getOffsetFromDegree(1 - this.degree + 1);
        interval[2] = 1 - this.octave + (int) Math.floor((1 - this.degree + 1 - 1) / 7D);
        this.degree = interval[0];
        this.offset = interval[1];
        this.octave = interval[2];
        return this;
    }

    public static Interval sum(Interval interval1, Interval interval2) {
        return interval1.clone().plus(interval2);
    }

    public static Interval diff(Interval interval1, Interval interval2) {
        return interval1.clone().minus(interval2);
    }

    public static Interval[] getIntervals(int... offsets) {
        Interval[] intervals = new Interval[offsets.length];
        for (int i = 0; i < intervals.length; i++) {
            intervals[i] = new Interval(offsets[i]);
        }
        return intervals;
    }

    public static Interval[] getIntervals(String... names) {
        Interval[] intervals = new Interval[names.length];
        for (int i = 0; i < intervals.length; i++) {
            intervals[i] = new Interval(names[i]);
        }
        return intervals;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degreeIn) {
        this.degree = degreeIn;
    }

    public Property getProperty() {
        return getPropertyFromOffset(this.offset, this.degree);
    }

    public static Property getPropertyFromOffset(int offsetIn, int degreeIn) {
        if (degreeIn == 1 || degreeIn == 4 || degreeIn == 5) {
            if (offsetIn == 0) return Property.PERFECT;
            if (offsetIn == 1) return Property.AUGMENTED;
            if (offsetIn == -1) return Property.DIMINISHED;
        } else {
            if (offsetIn == 0) return Property.MAJOR;
            if (offsetIn == -1) return Property.MINOR;
            if (offsetIn == 1) return Property.AUGMENTED;
            if (offsetIn == -2) return Property.DIMINISHED;
        }
        return null;
    }

    public int getOffsetFromProperty(Property propertyIn) {
        return getOffsetFromProperty(propertyIn, this.degree);
    }

    public static int getOffsetFromProperty(Property propertyIn, int degreeIn) {
        if (degreeIn == 1 || degreeIn == 4 || degreeIn == 5) {
            if (propertyIn == Property.PERFECT) return 0;
            if (propertyIn == Property.AUGMENTED) return 1;
            if (propertyIn == Property.DIMINISHED) return -1;
        } else {
            if (propertyIn == Property.MAJOR) return 0;
            if (propertyIn == Property.MINOR) return -1;
            if (propertyIn == Property.AUGMENTED) return 1;
            if (propertyIn == Property.DIMINISHED) return -2;
        }
        return 0;
    }

    public int getDifference() {
        return getOffsetFromInterval(this.degree, this.offset, this.octave);
    }

    public double getRatio() {
        return Math.pow(Frequency.SEMITONE, this.getDifference());
    }

    public static double getRatioFromOffset(int offsetIn) {
        return Math.pow(Frequency.SEMITONE, offsetIn);
    }

    public static int getOffsetFromRatio(double ratioIn) {
        return (int) Math.round(Math.log(ratioIn) / Math.log(Frequency.SEMITONE));
    }

    public int[] getSubHarmonicCount() {
        return gcdCount(1D, this.getRatio(), Frequency.THRES_AUDIT);
    }

    public static int[] gcdCount(double double1, double double2, double accuracyIn) {
        double high = Math.max(double1, double2) / Math.min(double1, double2);
        int lastJ = 1;
        for (int i = 1; i < 100; i++) {
            for (int j = lastJ; j < 100; j++) {
                if ((1D / i) / (high / j) > accuracyIn) break;
                else if ((1D / i) / (high / j) < accuracyIn && (1D / i) / (high / j) > 1 / accuracyIn) return new int[]{i, j};
                lastJ = j;
            }
        }
        return null;
    }

    public int[] getSupHarmonicCount() {
        return lcmCount(1D, this.getRatio(), Frequency.THRES_AUDIT);
    }

    public static int[] lcmCount(double double1, double double2, double accuracyIn) {
        double high = Math.max(double1, double2) / Math.min(double1, double2);
        int lastJ = 1;
        for (int i = 1; i < 100; i++) {
            for (int j = lastJ; j < 100; j++) {
                if (high * j / i > accuracyIn) break;
                else if (high * j / i < accuracyIn && high * j / i > 1 / accuracyIn) {
                    return new int[]{i, j};
                }
                lastJ = j;
            }
        }
        return null;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offsetIn) {
        this.offset = offsetIn;
    }

    public int getOctave() {
        return octave;
    }

    public void setOctave(int octaveIn) {
        this.octave = octaveIn;
    }

    @Override
    public String toString() {
        return (getProperty() == null ? String.valueOf(this.offset) : getProperty().getAbb()) + this.degree + (this.octave > 0 ? ("+" + this.octave) : this.octave < 0 ? this.octave : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Interval interval = (Interval) o;

        if (degree != interval.degree) return false;
        if (offset != interval.offset) return false;
        return octave == interval.octave;

    }

    @Override
    public int hashCode() {
        int result = degree;
        result = 31 * result + offset;
        result = 31 * result + octave;
        return result;
    }

    @Override
    public Interval clone() {
        return new Interval(this);
    }

    public enum Property {
        PERFECT(0, "P"), MAJOR(1, "M"), MINOR(-1, "m"), AUGMENTED(2, "A"), DIMINISHED(-2, "d");
        private int index;
        private String abb;

        Property(int indexIn, String abbIn) {
            this.index = indexIn;
            this.abb = abbIn;
        }

        public int getIndex() {
            return index;
        }

        public String getAbb() {
            return abb;
        }

        public static Property getProperty(int indexIn) {
            for (Property property : values()) if (property.getIndex() == indexIn) return property;
            return null;
        }

        public static Property getProperty(String stringIn) {
            for (Property property : values()) {
                if (property.name().equalsIgnoreCase(stringIn)) return property;
                else if (property.getAbb().equals(stringIn)) return property;
            }
            return null;
        }

        public static Property getPropertyFromAbb(String stringIn) {
            for (Property property : values()) if (property.getAbb().equals(stringIn)) return property;
            return null;
        }
    }
}
