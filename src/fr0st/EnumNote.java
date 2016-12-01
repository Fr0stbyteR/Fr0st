package fr0st;

public enum EnumNote {
    C(0),
    D(2),
    E(4),
    F(5),
    G(7),
    A(9),
    B(11);

    private int offset;

    EnumNote(int offsetIn) {
        this.offset = offsetIn;
    }

    public int getIndex() {
        return this.ordinal();
    }

    public int getOffset() {
        return offset;
    }

    public static EnumNote getEnumNoteByOffset(int offsetIn) {
        for (EnumNote enumNote : values()) if (enumNote.getOffset() == Math.floorMod(offsetIn, 12)) return enumNote;
        return null;
    }

    public static EnumNote getEnumNoteByIndex(int indexIn) {
        return values()[Math.floorMod(indexIn, 7)];
    }

    public static EnumNote getEnumNote(String stringIn) {
        for (EnumNote enumNote : values()) if (enumNote.name().equalsIgnoreCase(stringIn)) return enumNote;
        return null;
    }

    @Override
    public String toString() {
        return name();
    }
}
