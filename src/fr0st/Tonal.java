package fr0st;

public interface Tonal {
    double getStability(Tonality tonalityIn); //is the object in tonality
    double getTendency(Note noteIn);
    double getTendency(Tonality tonalityIn);
    Color getColor(Tonality tonalityIn);
}
