public enum Commande {

    CREATE_DEGREE("CREATE DEGREE"),
    SELECT_DEGREE("SELECT DEGREE"),
    SELECT_YEAR("SELECT YEAR"),
    CREATE_UE("CREATE UE"),

    DISPLAY_GRAPH("DISPLAY GRAPH"),

    LIST_DEGREES("LIST DEGREES"),
    HELP("HELP"),
    EXIT("EXIT");

    private final String label;

    Commande(String label) {
        this.label = label;
    }

    public static Commande fromInput(String input) {
        String normalized = input.trim().toUpperCase();

        Commande best = null;
        for (Commande c : values()) {
            if (normalized.startsWith(c.label)) {
                if (best == null || c.label.length() > best.label.length()) {
                    best = c;
                }
            }
        }
        if (best == null) throw new IllegalArgumentException("Commande inconnue");
        return best;
    }

    public String getArguments(String input) {
        return input.substring(label.length()).trim();
    }

    public String getLabel() {
        return label;
    }
}
