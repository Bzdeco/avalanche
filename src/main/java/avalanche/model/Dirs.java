package avalanche.model;

public enum Dirs {
    NW("płn. zach."),
    N("płn."),
    NE("płn. wsch."),
    E("wsch."),
    SE("płd. wsch."),
    S("płd."),
    SW("płd. zach."),
    W("zach.");

    private String text;

    Dirs(String text) {
        this.text = text;
    }

    public static Dirs fromString(String text) {
        if (text != null)
            for (Dirs d : Dirs.values())
                if (text.equalsIgnoreCase(d.text))
                    return d;
        throw new IllegalArgumentException("No constant with text " + text + " found.");
    }
}
