package fr0st;

public class Color implements Cloneable {
    protected double t, s, d;

    public Color(double t, double s, double d) {
        this.t = t;
        this.s = s;
        this.d = d;
    }

    public Color() {
        this.t = 0D;
        this.s = 0D;
        this.d = 0D;
    }

    public Color(double... color) {
        this.t = color[0];
        this.s = color[1];
        this.d = color[2];
    }

    public Color(Color that) {
        this.t = that.t;
        this.s = that.s;
        this.d = that.d;
    }

    public double getT() {
        return t;
    }

    public void setT(double t) {
        this.t = t;
    }

    public double getS() {
        return s;
    }

    public void setS(double s) {
        this.s = s;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public double[] toArray() {
        return new double[]{this.t, this.s, this.d};
    }

    public Color fromArray(double... color) {
        set(color);
        return this;
    }

    public void set(double... color) {
        this.t = color[0];
        this.s = color[1];
        this.d = color[2];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Color color = (Color) o;

        if (Double.compare(color.t, t) != 0) return false;
        if (Double.compare(color.s, s) != 0) return false;
        return Double.compare(color.d, d) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(t);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(s);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(d);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Color{" +
                "t=" + t +
                ", s=" + s +
                ", d=" + d +
                '}';
    }

    @Override
    public Color clone() {
        return new Color(this);
    }
}
