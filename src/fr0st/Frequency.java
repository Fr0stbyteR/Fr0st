package fr0st;

public class Frequency {
    public static final double A440 = 440;

    public static final double SEMITONE = Math.pow(2D, 1D / 12D);

    public static final double THRES_AUDIT = Math.pow(2D, 1D / 36D);

    public static double getFrequency(Pitch pitch) {
        return A440 * Math.pow(2D, (pitch.getIndex() - 69) / 12D);
    }

    public static double getNoteValue(double f) {
        return 69 + 12 * (Math.log(f / A440) / Math.log(2D));
    }

    public static Pitch getPitch(double f) {
        return new Pitch((int) Math.round(getNoteValue(f)));
    }
}
