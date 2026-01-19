public enum Commande {

    // ITE-1
    CREATE_DEGREE("CREATE DEGREE"),
    SELECT_DEGREE("SELECT DEGREE"),
    SELECT_YEAR("SELECT YEAR"),
    CREATE_UE("CREATE UE"),
    DISPLAY_GRAPH("DISPLAY GRAPH"),
    LIST_DEGREES("LIST DEGREES"),

    // ITE-2
    DELETE_UE("DELETE UE"),
    GET_TOTAL("GET TOTAL"),

    // ITE-3
    CREATE_TEACHER("CREATE TEACHER"),
    ASSIGN("ASSIGN"),

    // ITE-4
    EDIT_UE("EDIT UE"),
    ASSIGN_UE("ASSIGN UE"),
    GET_COVER("GET COVER"),
    GET_SEANCE("GET SEANCE"),

    TRACE_GRAPH("TRACE GRAPH"),

    RUN("RUN"),






    HELP("HELP"),
    EXIT("EXIT");

    private final String label;

    Commande(String label) {
        this.label = label;
    }

    /**
     * Detecte la commande en prefixe. Si plusieurs commandes matchent, on garde le label le plus long.
     * Exemple: "GET TOTAL ALL" doit matcher GET_TOTAL.
     */
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
