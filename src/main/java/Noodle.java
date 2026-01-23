import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class Noodle implements Serializable {
    private static final long serialVersionUID = 1L;

    HashMap<String, Diplome> diplomeHashMap = new HashMap<>();

    // cle = NOM de l'enseignant (identifiant)
    HashMap<String, Enseignant> enseignantsByNom = new HashMap<>();

    public Noodle() {
        this.diplomeHashMap = new HashMap<>();
        this.enseignantsByNom = new HashMap<>();
    }

    // =========================
    // Diplomes
    // =========================

    public void afficherDiplomes() {
        if (diplomeHashMap.isEmpty()) {
            System.out.println("(Aucun diplome)");
            return;
        }
        diplomeHashMap.forEach((nomDiplome, d) -> System.out.println(nomDiplome + " : " + d));
    }

    public Diplome creerDiplome(String nomDiplome, TypeDiplome type, int annee, int maxEtu, int ects) {
        if (diplomeHashMap.containsKey(nomDiplome)) {
            throw new IllegalArgumentException("Un diplome nomme '" + nomDiplome + "' existe deja.");
        }
        Diplome dip = new Diplome(nomDiplome, type, annee, maxEtu, ects);
        diplomeHashMap.put(nomDiplome, dip);
        System.out.println("Diplome " + nomDiplome + " cree");
        return dip;
    }

    public Diplome getDiplome(String nomDiplome) {
        return diplomeHashMap.get(nomDiplome);
    }

    public void consulterAnneeDiplome(Diplome dip, int annee) {
        System.out.println("Liste des UE enseignees en annee " + annee + " de " + dip.nomDiplome);
        ArrayList<UE> ues = dip.UEHashMap.get(annee);
        if (ues == null || ues.isEmpty()) {
            System.out.println("  (Aucune UE)");
            return;
        }
        for (int i = 0; i < ues.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + ues.get(i));
        }
    }

    public void consulterDiplome(Diplome dip) {
        System.out.println("Voici la liste des UE par annees pour " + dip.nomDiplome + " :");

        dip.UEHashMap.forEach((Integer annee, ArrayList<UE> listUE) -> {
            System.out.println("Annee " + annee + " :");

            if (listUE.isEmpty()) {
                System.out.println("  (Aucune UE)");
            } else {
                for (int i = 0; i < listUE.size(); i++) {
                    System.out.println("  " + (i + 1) + ") " + listUE.get(i));
                }
            }
        });
    }

    // =========================
    // Enseignants (ITE-3)
    // =========================

    // ES-07
    public Enseignant enregistrerEnseignant(String nom, String prenom) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Nom enseignant vide");
        }
        if (prenom == null || prenom.isBlank()) {
            throw new IllegalArgumentException("Prenom enseignant vide");
        }
        if (enseignantsByNom.containsKey(nom)) {
            throw new IllegalArgumentException("Enseignant deja existant: " + nom);
        }
        Enseignant e = new Enseignant(nom, prenom);
        enseignantsByNom.put(nom, e);
        return e;
    }

    public Enseignant getEnseignant(String nom) {
        return enseignantsByNom.get(nom);
    }

    // ES-08 (commande ASSIGN -> doit passer par Noodle)
    public void assignerEnseignant(String nomUE, String nomEnseignant, int nbHeures) {
        if (nomUE == null || nomUE.isBlank()) {
            throw new IllegalArgumentException("Nom UE vide");
        }
        if (nomEnseignant == null || nomEnseignant.isBlank()) {
            throw new IllegalArgumentException("Nom enseignant vide");
        }
        if (nbHeures <= 0) {
            throw new IllegalArgumentException("Nombre d'heures invalide");
        }

        Enseignant e = enseignantsByNom.get(nomEnseignant);
        if (e == null) {
            throw new IllegalArgumentException("Enseignant inconnu : " + nomEnseignant);
        }

        UE ue = trouverUEUnique(nomUE); // lève une exception si introuvable / ambigu

        ue.affecterEnseignant(nomEnseignant, nbHeures);

        e.ajouterAffectation(nomUE, nbHeures);
    }

    // =========================
    // Recherche UE (sans diplome) - utile pour ASSIGN
    // =========================

    public UE trouverUEUnique(String nomUE) {
        UE found = null;

        for (Diplome d : diplomeHashMap.values()) {
            for (UE ue : d.UEList) {
                if (ue.nomUE.equals(nomUE)) {
                    if (found != null) {
                        throw new IllegalArgumentException(
                                "UE ambigue: '" + nomUE + "' existe dans plusieurs diplomes"
                        );
                    }
                    found = ue;
                }
            }
        }

        if (found == null) {
            throw new IllegalArgumentException("UE introuvable: " + nomUE);
        }

        return found;
    }

    // =========================
    // GET TOTAL
    // =========================

    public TotalTargetType getTotalTargetType(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nom vide");
        }

        if (name.equalsIgnoreCase("ALL")) return TotalTargetType.ALL;
        if (diplomeHashMap.containsKey(name)) return TotalTargetType.DIPLOME;
        if (enseignantsByNom.containsKey(name)) return TotalTargetType.ENSEIGNANT;

        // Tentative: UE unique
        try {
            trouverUEUnique(name);
            return TotalTargetType.UE;
        } catch (Exception ignored) {
            // ignored
        }

        throw new IllegalArgumentException(
                "Aucun diplome, UE ou enseignant ne correspond a: " + name
        );
    }

    public int getTotal(String name) {
        TotalTargetType type = getTotalTargetType(name);

        switch (type) {
            case ALL:
                int totalAll = 0;
                for (Diplome d : diplomeHashMap.values()) {
                    totalAll += totalHeuresDiplome(d);
                }
                return totalAll;

            case DIPLOME:
                return totalHeuresDiplome(diplomeHashMap.get(name));

            case UE:
                UE ue = trouverUEUnique(name);
                return ue.cm + ue.td + ue.tp;

            case ENSEIGNANT:
                // Ajustement ITE-3: on prend la source de verite cote Enseignant
                return enseignantsByNom.get(name).getTotalHeures();

            default:
                throw new IllegalStateException("Type non gere");
        }
    }

    private int totalHeuresDiplome(Diplome d) {
        int total = 0;
        for (UE ue : d.UEList) {
            total += ue.cm + ue.td + ue.tp;
        }
        return total;
    }


    public void assignerUEMutualisee(String ueName, String diplomeName, int year) {
        Diplome d = diplomeHashMap.get(diplomeName);
        if (d == null) throw new IllegalArgumentException("Diplome inconnu: " + diplomeName);

        UE ue = trouverUEParNomGlobal(ueName);
        if (ue == null) throw new IllegalArgumentException("UE introuvable: " + ueName);

        d.lierUEExistante(ue, year);
    }

    private UE trouverUEParNomGlobal(String nomUE) {
        UE found = null;
        for (Diplome d : diplomeHashMap.values()) {
            for (UE ue : d.UEList) {
                if (ue.nomUE.equals(nomUE)) {
                    if (found == null) found = ue;
                    else if (found != ue) {
                        // deux instances différentes avec le même nom => ambigu
                        throw new IllegalArgumentException("UE ambigue: '" + nomUE + "' existe sous plusieurs formes");
                    }
                }
            }
        }
        return found;
    }

    public int getCover(String name) {


        if (name == null || name.isBlank()) throw new IllegalArgumentException("Nom vide");

        if (name.equalsIgnoreCase("ALL")) {
            return coverAll();
        }

        // Diplôme ?
        Diplome d = diplomeHashMap.get(name);
        if (d != null) {
            return coverDiplome(d);
        }

        // UE ?
        UE ue = trouverUEParNomGlobal(name); // ou trouverUEUnique si tu gères bien la mutualisation
        if (ue != null) {
            return ue.getCover();
        }

        throw new IllegalArgumentException("Cible inconnue pour GET COVER: " + name);
    }

    private int coverDiplome(Diplome d) {
        int assigned = 0;
        int total = 0;

        for (UE ue : d.UEList) {
            assigned += ue.getHeuresAffectees();
            total += ue.getTotalHeures();
        }
        return percentRounded(assigned, total);
    }

    private int coverAll() {
        // Pour éviter de compter 2 fois une UE mutualisée, on déduplique par instance
        HashSet<UE> uniqueUEs = new HashSet<>();
        for (Diplome d : diplomeHashMap.values()) {
            uniqueUEs.addAll(d.UEList);
        }

        int assigned = 0;
        int total = 0;
        for (UE ue : uniqueUEs) {
            assigned += ue.getHeuresAffectees();
            total += ue.getTotalHeures();
        }
        return percentRounded(assigned, total);
    }

    private int percentRounded(int assigned, int total) {
        if (total <= 0) return 0;
        double p = (assigned * 100.0) / total;
        return (int) Math.round(p);
    }

    // =========================
    // GET SEANCE (ITE-4)
    // =========================

    /**
     * ITE-4 - Calcul du nombre de seances (1 seance = 2h), arrondi a l'entier superieur.
     * <name> peut etre ALL, un nom de diplome, ou un nom d'UE.
     */

    private int ceilDiv2(int heures) {
        if (heures <= 0) return 0;
        return (int) Math.ceil(heures / 2.0);
    }

    public int getSeance(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nom vide");
        }

        //Offre de  globale de formation
        if (name.equalsIgnoreCase("ALL")) {
            int heures = totalHeuresOffreSansDoubleCompte();
            return ceilDiv2(heures);
        }

        // Diplome
        Diplome d = diplomeHashMap.get(name);
        if (d != null) {
            int heures = totalHeuresDiplome(d);
            return ceilDiv2(heures);
        }

        // UE (recherche globale compatible mutualisation)
        UE ue = trouverUEParNomGlobal(name);
        if (ue != null) {
            int heures = ue.cm + ue.td + ue.tp;
            return ceilDiv2(heures);
        }

        throw new IllegalArgumentException("Cible inconnue pour GET SEANCE: " + name);
    }

    /**
     * ceil(h/2) en entier (1 seance = 2h).
     * Ex: 30 -> 15 ; 31 -> 16 ; 0 -> 0
     */


    /**
     * Total d'heures de l'offre (ALL), sans double-compter les UE mutualisees.
     */
    private int totalHeuresOffreSansDoubleCompte() {
        HashSet<UE> uniques = new HashSet<>();
        for (Diplome d : diplomeHashMap.values()) {
            uniques.addAll(d.UEList);
        }

        int total = 0;
        for (UE ue : uniques) {
            total += ue.cm + ue.td + ue.tp;
        }
        return total;
    }



    // =========================
    // ITE-5 - DISPLAY GRAPH (format sujet)
    // =========================
    public void displayGraphDiplome(Diplome dip) {
        if (dip == null) {
            throw new IllegalArgumentException("Diplome null");
        }

        // Ligne racine
        System.out.println(dip.nomDiplome + " (" + dip.type + ")");

        // Années 1..n
        for (int year = 1; year <= dip.annee; year++) {
            System.out.println("  Annee " + year);

            ArrayList<UE> ues = dip.UEHashMap.get(year);
            if (ues == null || ues.isEmpty()) {
                System.out.println("    (Aucune UE)");
                continue;
            }

            for (UE ue : ues) {
                System.out.println("    " + ue.nomUE + " (" + ue.ects + " ECTS) "
                        + ue.cm + " CM " + ue.td + " TD " + ue.tp + " TP");

                int cover = ue.getCover();
                System.out.println("    Enseignants (couverte a " + cover + "%) :");

                HashMap<String, Integer> map = ue.getHeuresParEnseignant();
                if (map == null || map.isEmpty()) {
                    System.out.println("      (Aucun enseignant)");
                } else {
                    ArrayList<String> noms = new ArrayList<>(map.keySet());
                    noms.sort(String::compareToIgnoreCase);

                    for (String nomEns : noms) {
                        int h = map.getOrDefault(nomEns, 0);
                        System.out.println("      " + nomEns + " (" + h + "h)");
                    }
                }
            }
        }
    }

    // =========================
    // ITE-5 - TRACE GRAPH (Graphviz DOT -> PNG)
    // =========================


    public void traceGraphAll(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Nom de fichier vide");
        }

        if (diplomeHashMap.isEmpty()) {
            throw new IllegalStateException("Aucun diplome a tracer (ALL).");
        }

        // Normaliser extension
        if (!fileName.toLowerCase().endsWith(".png")) {
            fileName = fileName + ".png";
        }

        String dot = toDotAll();

        // Fichier DOT temporaire
        String dotFile = "ALL.dot";

        writeTextFile(dotFile, dot);
        runDot(dotFile, fileName);
    }





    public void traceGraph(String diplomeName, String fileName) {
        if (diplomeName == null || diplomeName.isBlank()) {
            throw new IllegalArgumentException("Nom diplome vide");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Nom de fichier vide");
        }

        Diplome dip = diplomeHashMap.get(diplomeName);
        if (dip == null) {
            throw new IllegalArgumentException("Diplome inconnu: " + diplomeName);
        }

        if (!fileName.toLowerCase().endsWith(".png")) {
            fileName = fileName + ".png";
        }

        String dot = toDot(dip);
        String dotFile = diplomeName + ".dot";

        writeTextFile(dotFile, dot);
        runDot(dotFile, fileName);
    }


    private String toDotAll() {
        StringBuilder sb = new StringBuilder();

        sb.append("digraph G {\n");

        // ---------- Graph (global) ----------
        sb.append("  graph [\n");
        sb.append("    rankdir=TB,\n");
        sb.append("    splines=polyline,\n");
        sb.append("    compound=true,\n");
        sb.append("    nodesep=0.45,\n");
        sb.append("    ranksep=0.85,\n");
        sb.append("    pad=0.25,\n");
        sb.append("    bgcolor=\"white\",\n");
        sb.append("    fontname=\"Helvetica\"\n");
        sb.append("  ];\n");

        // ---------- Edges (global) ----------
        sb.append("  edge [\n");
        sb.append("    color=\"#64748B\",\n");
        sb.append("    penwidth=1.2,\n");
        sb.append("    arrowsize=0.8\n");
        sb.append("  ];\n");

        // ---------- Nodes (global) ----------
        sb.append("  node [\n");
        sb.append("    fontname=\"Helvetica\",\n");
        sb.append("    fontsize=11,\n");
        sb.append("    shape=box,\n");
        sb.append("    style=\"rounded,filled\",\n");
        sb.append("    color=\"#CBD5E1\",\n");
        sb.append("    penwidth=1.2,\n");
        sb.append("    fillcolor=\"#F8FAFC\"\n");
        sb.append("  ];\n\n");

        // ---------- Root "ALL" ----------
        String rootId = "ROOT_ALL";
        sb.append("  ").append(rootId).append(" [");
        sb.append("shape=box, style=\"rounded,filled\", penwidth=1.8, ");
        sb.append("color=\"#0F172A\", fillcolor=\"#DBEAFE\", ");
        sb.append("label=<\n");
        sb.append("    <TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLPADDING=\"8\">\n");
        sb.append("      <TR><TD ALIGN=\"CENTER\"><B><FONT POINT-SIZE=\"16\">OFFRE DE FORMATION</FONT></B></TD></TR>\n");
        sb.append("      <TR><TD ALIGN=\"CENTER\"><FONT POINT-SIZE=\"11\" COLOR=\"#0F172A\">ALL</FONT></TD></TR>\n");
        sb.append("    </TABLE>\n");
        sb.append("  >];\n\n");

        sb.append("  { rank=source; ").append(rootId).append("; }\n\n");

        // Trier les diplômes par nom pour un rendu stable
        ArrayList<Diplome> dips = new ArrayList<>(diplomeHashMap.values());
        dips.sort((a, b) -> a.nomDiplome.compareToIgnoreCase(b.nomDiplome));

        // On mémorise les noeuds diplômes pour les aligner en ligne sous ROOT
        ArrayList<String> dipNodeIds = new ArrayList<>();

        // ---------- Diplomas blocks ----------
        for (Diplome dip : dips) {
            String dipId = "DIP_" + safeId(dip.nomDiplome);
            dipNodeIds.add(dipId);

            // Noeud diplome
            sb.append("  ").append(dipId).append(" [");
            sb.append("shape=box, style=\"rounded,filled\", penwidth=1.6, ");
            sb.append("color=\"#0F172A\", fillcolor=\"#E0F2FE\", ");
            sb.append("label=<\n");
            sb.append("    <TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLPADDING=\"8\">\n");
            sb.append("      <TR><TD ALIGN=\"CENTER\"><B><FONT POINT-SIZE=\"14\">")
                    .append(escapeHtml(dip.nomDiplome))
                    .append("</FONT></B></TD></TR>\n");
            sb.append("      <TR><TD ALIGN=\"CENTER\"><FONT POINT-SIZE=\"10\" COLOR=\"#0F172A\">")
                    .append(escapeHtml(String.valueOf(dip.type)))
                    .append("</FONT></TD></TR>\n");
            sb.append("    </TABLE>\n");
            sb.append("  >];\n\n");

            // Flèche ALL -> Diplôme
            sb.append("  ").append(rootId).append(" -> ").append(dipId).append(" [weight=40, minlen=2];\n\n");

            // Sous-graphe du diplôme : années + UE (comme ton rendu actuel)
            appendDiplomeGraph(sb, dip, dipId);
        }

        // Aligner les diplômes sur une même ligne
        if (!dipNodeIds.isEmpty()) {
            sb.append("  { rank=same; ");
            for (String id : dipNodeIds) sb.append(id).append("; ");
            sb.append("}\n");

            // Stabiliser gauche->droite
            sb.append("  ");
            for (int i = 0; i < dipNodeIds.size() - 1; i++) {
                sb.append(dipNodeIds.get(i)).append(" -> ");
            }
            sb.append(dipNodeIds.get(dipNodeIds.size() - 1))
                    .append(" [style=invis, constraint=false];\n\n");
        }

        // ---------- Legend ----------
        sb.append("  subgraph cluster_legend {\n");
        sb.append("    label=\"Légende\";\n");
        sb.append("    labelloc=t;\n");
        sb.append("    labeljust=l;\n");
        sb.append("    fontsize=12;\n");
        sb.append("    style=\"rounded\";\n");
        sb.append("    color=\"#CBD5E1\";\n");
        sb.append("    bgcolor=\"#FFFFFF\";\n");
        sb.append("    penwidth=1.0;\n");
        sb.append("    Legend1 [shape=box, style=\"rounded,filled\", color=\"#DC2626\", fillcolor=\"#FEE2E2\", label=\"UE : couverture < 50%\"];\n");
        sb.append("    Legend2 [shape=box, style=\"rounded,filled\", color=\"#D97706\", fillcolor=\"#FEF3C7\", label=\"UE : 50% à 79%\"];\n");
        sb.append("    Legend3 [shape=box, style=\"rounded,filled\", color=\"#16A34A\", fillcolor=\"#DCFCE7\", label=\"UE : >= 80%\"];\n");
        sb.append("  }\n");

        sb.append("}\n");
        return sb.toString();
    }

    /**
     * Ajoute pour un diplôme : conteneurs Année X avec les UE dedans,
     * et une flèche qui touche le bord du conteneur (via port invisible + lhead).
     *
     * IMPORTANT : clusterId doit être unique à l'échelle du graphe ALL,
     * donc on le préfixe avec dipId.
     */
    private void appendDiplomeGraph(StringBuilder sb, Diplome dip, String dipId) {
        for (int year = 1; year <= dip.annee; year++) {
            String clusterId = "cluster_" + dipId + "_Y" + year;

            sb.append("  subgraph ").append(clusterId).append(" {\n");
            sb.append("    label=\"Année ").append(year).append("\";\n");
            sb.append("    labelloc=t;\n");
            sb.append("    labeljust=l;\n");
            sb.append("    fontsize=13;\n");
            sb.append("    fontname=\"Helvetica\";\n");
            sb.append("    style=\"rounded\";\n");
            sb.append("    color=\"#CBD5E1\";\n");
            sb.append("    bgcolor=\"#F8FAFC\";\n");
            sb.append("    penwidth=1.2;\n\n");

            // Port au bord haut du conteneur (pour que la flèche ne "rentre" pas)
            String portId = dipId + "_Y" + year + "_PORT";
            sb.append("    ").append(portId).append(" [shape=point, width=0.01, label=\"\", style=invis];\n");
            sb.append("    { rank=min; ").append(portId).append("; }\n\n");

            ArrayList<UE> ues = dip.UEHashMap.get(year);
            if (ues != null) {
                ues.sort((a, b) -> a.nomUE.compareToIgnoreCase(b.nomUE));
            }

            ArrayList<String> contentIds = new ArrayList<>();

            if (ues != null && !ues.isEmpty()) {
                for (UE ue : ues) {
                    String ueId = dipId + "_Y" + year + "_UE_" + safeId(ue.nomUE);
                    contentIds.add(ueId);

                    int cover = ue.getCover();
                    String ueFill = coverFillColor(cover);
                    String ueBorder = coverBorderColor(cover);

                    // enseignants + reste à couvrir (comme tu as maintenant)
                    String teachersHtml = teachersHtml(ue);
                    int remaining = remainingHours(ue);
                    String remainingHtml = remainingHtml(remaining);

                    String ueLabelHtml =
                            "<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLPADDING=\"6\">" +
                                    "<TR><TD ALIGN=\"LEFT\"><B><FONT POINT-SIZE=\"13\">" + escapeHtml(ue.nomUE) + "</FONT></B></TD></TR>" +
                                    "<TR><TD ALIGN=\"LEFT\"><FONT POINT-SIZE=\"10\" COLOR=\"#334155\">" + ue.ects + " ECTS</FONT></TD></TR>" +
                                    "<TR><TD ALIGN=\"LEFT\"><FONT POINT-SIZE=\"10\" COLOR=\"#334155\">" +
                                    ue.cm + " CM  •  " + ue.td + " TD  •  " + ue.tp + " TP" +
                                    "</FONT></TD></TR>" +
                                    teachersHtml +
                                    remainingHtml +
                                    "</TABLE>";

                    sb.append("    ").append(ueId).append(" [");
                    sb.append("shape=box, style=\"rounded,filled\", penwidth=1.4, ");
                    sb.append("color=\"").append(ueBorder).append("\", ");
                    sb.append("fillcolor=\"").append(ueFill).append("\", ");
                    sb.append("label=<").append(ueLabelHtml).append(">");
                    sb.append("];\n");
                }
            } else {
                String emptyId = dipId + "_Y" + year + "_EMPTY";
                contentIds.add(emptyId);

                sb.append("    ").append(emptyId).append(" [");
                sb.append("shape=box, style=\"rounded,filled\", penwidth=1.0, ");
                sb.append("color=\"#CBD5E1\", fillcolor=\"#FFFFFF\", ");
                sb.append("fontcolor=\"#64748B\", label=\"(Aucune UE)\"");
                sb.append("];\n");
            }

            // Placement interne : empilement vertical
            if (!contentIds.isEmpty()) {
                sb.append("\n");
                sb.append("    ").append(portId).append(" -> ").append(contentIds.get(0))
                        .append(" [style=invis, weight=50, minlen=1];\n");
                for (int i = 0; i < contentIds.size() - 1; i++) {
                    sb.append("    ").append(contentIds.get(i)).append(" -> ").append(contentIds.get(i + 1))
                            .append(" [style=invis, weight=40, minlen=1];\n");
                }
            }

            sb.append("  }\n\n");

            // Flèche visible : diplôme -> bord du conteneur année
            sb.append("  ").append(dipId).append(" -> ").append(portId)
                    .append(" [weight=30, minlen=2, lhead=").append(clusterId).append("];\n\n");
        }
    }





    private String toDot(Diplome dip) {
        StringBuilder sb = new StringBuilder();

        sb.append("digraph G {\n");

        // ---------- Graph (global) ----------
        sb.append("  graph [\n");
        sb.append("    rankdir=TB,\n");
        sb.append("    splines=polyline,\n");
        sb.append("    compound=true,\n");
        sb.append("    nodesep=0.45,\n");
        sb.append("    ranksep=0.85,\n");
        sb.append("    pad=0.25,\n");
        sb.append("    bgcolor=\"white\",\n");
        sb.append("    fontname=\"Helvetica\"\n");
        sb.append("  ];\n");

        // ---------- Edges (global) ----------
        sb.append("  edge [\n");
        sb.append("    color=\"#64748B\",\n");
        sb.append("    penwidth=1.2,\n");
        sb.append("    arrowsize=0.8\n");
        sb.append("  ];\n");

        // ---------- Nodes (global) ----------
        sb.append("  node [\n");
        sb.append("    fontname=\"Helvetica\",\n");
        sb.append("    fontsize=11,\n");
        sb.append("    shape=box,\n");
        sb.append("    style=\"rounded,filled\",\n");
        sb.append("    color=\"#CBD5E1\",\n");
        sb.append("    penwidth=1.2,\n");
        sb.append("    fillcolor=\"#F8FAFC\"\n");
        sb.append("  ];\n\n");

        // ---------- Diplôme ----------
        String dipId = "DIP_" + safeId(dip.nomDiplome);

        sb.append("  ").append(dipId).append(" [");
        sb.append("shape=box, style=\"rounded,filled\", penwidth=1.6, ");
        sb.append("color=\"#0F172A\", fillcolor=\"#E0F2FE\", ");
        sb.append("label=<\n");
        sb.append("    <TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLPADDING=\"8\">\n");
        sb.append("      <TR><TD ALIGN=\"CENTER\"><B><FONT POINT-SIZE=\"16\">")
                .append(escapeHtml(dip.nomDiplome))
                .append("</FONT></B></TD></TR>\n");
        sb.append("      <TR><TD ALIGN=\"CENTER\"><FONT POINT-SIZE=\"11\" COLOR=\"#0F172A\">")
                .append(escapeHtml(String.valueOf(dip.type)))
                .append("</FONT></TD></TR>\n");
        sb.append("    </TABLE>\n");
        sb.append("  >];\n\n");

        sb.append("  { rank=source; ").append(dipId).append("; }\n\n");

        // ---------- Years containers ----------
        for (int year = 1; year <= dip.annee; year++) {
            String clusterId = "cluster_" + dipId + "_Y" + year;

            sb.append("  subgraph ").append(clusterId).append(" {\n");
            sb.append("    label=\"Année ").append(year).append("\";\n");
            sb.append("    labelloc=t;\n");
            sb.append("    labeljust=l;\n");
            sb.append("    fontsize=13;\n");
            sb.append("    fontname=\"Helvetica\";\n");
            sb.append("    style=\"rounded\";\n");
            sb.append("    color=\"#CBD5E1\";\n");
            sb.append("    bgcolor=\"#F8FAFC\";\n");
            sb.append("    penwidth=1.2;\n\n");

            // Port au bord haut du conteneur
            String portId = dipId + "_Y" + year + "_PORT";
            sb.append("    ").append(portId).append(" [shape=point, width=0.01, label=\"\", style=invis];\n");
            sb.append("    { rank=min; ").append(portId).append("; }\n\n");

            ArrayList<UE> ues = dip.UEHashMap.get(year);
            if (ues != null) {
                ues.sort((a, b) -> a.nomUE.compareToIgnoreCase(b.nomUE));
            }

            ArrayList<String> contentIds = new ArrayList<>();

            if (ues != null && !ues.isEmpty()) {
                for (UE ue : ues) {
                    String ueId = dipId + "_Y" + year + "_UE_" + safeId(ue.nomUE);
                    contentIds.add(ueId);

                    int cover = ue.getCover();
                    String ueFill = coverFillColor(cover);
                    String ueBorder = coverBorderColor(cover);

                    // ----- enseignants + reste à couvrir -----
                    String teachersHtml = teachersHtml(ue);
                    int remaining = remainingHours(ue);
                    String remainingHtml = remainingHtml(remaining);

                    String ueLabelHtml =
                            "<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLPADDING=\"6\">" +
                                    "<TR><TD ALIGN=\"LEFT\"><B><FONT POINT-SIZE=\"13\">" + escapeHtml(ue.nomUE) + "</FONT></B></TD></TR>" +
                                    "<TR><TD ALIGN=\"LEFT\"><FONT POINT-SIZE=\"10\" COLOR=\"#334155\">" + ue.ects + " ECTS</FONT></TD></TR>" +
                                    "<TR><TD ALIGN=\"LEFT\"><FONT POINT-SIZE=\"10\" COLOR=\"#334155\">" +
                                    ue.cm + " CM  •  " + ue.td + " TD  •  " + ue.tp + " TP" +
                                    "</FONT></TD></TR>" +
                                    teachersHtml +
                                    remainingHtml +
                                    "</TABLE>";

                    sb.append("    ").append(ueId).append(" [");
                    sb.append("shape=box, style=\"rounded,filled\", penwidth=1.4, ");
                    sb.append("color=\"").append(ueBorder).append("\", ");
                    sb.append("fillcolor=\"").append(ueFill).append("\", ");
                    sb.append("label=<").append(ueLabelHtml).append(">");
                    sb.append("];\n");
                }
            } else {
                String emptyId = dipId + "_Y" + year + "_EMPTY";
                contentIds.add(emptyId);

                sb.append("    ").append(emptyId).append(" [");
                sb.append("shape=box, style=\"rounded,filled\", penwidth=1.0, ");
                sb.append("color=\"#CBD5E1\", fillcolor=\"#FFFFFF\", ");
                sb.append("fontcolor=\"#64748B\", label=\"(Aucune UE)\"");
                sb.append("];\n");
            }

            // Placement interne : empilement vertical
            if (!contentIds.isEmpty()) {
                sb.append("\n");
                sb.append("    ").append(portId).append(" -> ").append(contentIds.get(0))
                        .append(" [style=invis, weight=50, minlen=1];\n");
                for (int i = 0; i < contentIds.size() - 1; i++) {
                    sb.append("    ").append(contentIds.get(i)).append(" -> ").append(contentIds.get(i + 1))
                            .append(" [style=invis, weight=40, minlen=1];\n");
                }
            }

            sb.append("  }\n\n");

            // Flèche diplôme -> bord du conteneur
            sb.append("  ").append(dipId).append(" -> ").append(portId)
                    .append(" [weight=30, minlen=2, lhead=").append(clusterId).append("];\n\n");
        }

        // ---------- Légende ----------
        sb.append("  subgraph cluster_legend {\n");
        sb.append("    label=\"Légende\";\n");
        sb.append("    labelloc=t;\n");
        sb.append("    labeljust=l;\n");
        sb.append("    fontsize=12;\n");
        sb.append("    style=\"rounded\";\n");
        sb.append("    color=\"#CBD5E1\";\n");
        sb.append("    bgcolor=\"#FFFFFF\";\n");
        sb.append("    penwidth=1.0;\n");
        sb.append("    Legend1 [shape=box, style=\"rounded,filled\", color=\"#DC2626\", fillcolor=\"#FEE2E2\", label=\"UE : couverture < 50%\"];\n");
        sb.append("    Legend2 [shape=box, style=\"rounded,filled\", color=\"#D97706\", fillcolor=\"#FEF3C7\", label=\"UE : 50% à 79%\"];\n");
        sb.append("    Legend3 [shape=box, style=\"rounded,filled\", color=\"#16A34A\", fillcolor=\"#DCFCE7\", label=\"UE : >= 80%\"];\n");
        sb.append("  }\n");

        sb.append("}\n");
        return sb.toString();
    }

    // =========================
    // Helpers GRAPH: enseignants + reste
    // =========================

    private int remainingHours(UE ue) {
        int total = ue.getTotalHeures();        // idéal si tu as cette méthode
        int assigned = ue.getHeuresAffectees(); // idéal si tu as cette méthode
        int remaining = total - assigned;
        return Math.max(0, remaining);
    }

    private String teachersHtml(UE ue) {
        HashMap<String, Integer> map = ue.getHeuresParEnseignant();
        if (map == null || map.isEmpty()) {
            return "<TR><TD ALIGN=\"LEFT\"><FONT POINT-SIZE=\"9\" COLOR=\"#64748B\">(Aucun enseignant)</FONT></TD></TR>";
        }

        ArrayList<String> noms = new ArrayList<>(map.keySet());
        noms.sort(String::compareToIgnoreCase);

        StringBuilder sb = new StringBuilder();
        sb.append("<TR><TD ALIGN=\"LEFT\"><FONT POINT-SIZE=\"9\" COLOR=\"#334155\"><B>Enseignants :</B></FONT></TD></TR>");

        for (String nom : noms) {
            int h = map.getOrDefault(nom, 0);
            sb.append("<TR><TD ALIGN=\"LEFT\"><FONT POINT-SIZE=\"9\" COLOR=\"#334155\">")
                    .append(escapeHtml(nom)).append(" (").append(h).append("h)")
                    .append("</FONT></TD></TR>");
        }

        return sb.toString();
    }

    private String remainingHtml(int remaining) {
        if (remaining <= 0) {
            return ""; // rien si tout est couvert
        }
        return "<TR><TD ALIGN=\"LEFT\"><FONT POINT-SIZE=\"9\" COLOR=\"#B91C1C\"><B>Reste à couvrir : "
                + remaining + "h</B></FONT></TD></TR>";
    }

    // ---------- Couleurs couverture ----------
    private String coverFillColor(int cover) {
        if (cover < 50) return "#FEE2E2";
        if (cover < 80) return "#FEF3C7";
        return "#DCFCE7";
    }

    private String coverBorderColor(int cover) {
        if (cover < 50) return "#DC2626";
        if (cover < 80) return "#D97706";
        return "#16A34A";
    }

    // ---------- HTML escaping (DOT labels) ----------
    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    // ---------- Barre de progression (pas utilisée ici, mais conservée) ----------
    private String coverBarHtml(int cover) {
        int w = 120;
        int filled = (int) Math.round(w * (cover / 100.0));
        int empty = w - filled;

        String fill = coverBorderColor(cover);

        return "<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLPADDING=\"0\" CELLSPACING=\"0\">" +
                "<TR>" +
                "<TD WIDTH=\"" + filled + "\" HEIGHT=\"10\" BGCOLOR=\"" + fill + "\"></TD>" +
                "<TD WIDTH=\"" + empty + "\" HEIGHT=\"10\" BGCOLOR=\"#E2E8F0\"></TD>" +
                "</TR>" +
                "</TABLE>";
    }

    // ---------- Exécution Graphviz ----------
    private void runDot(String dotFile, String pngFile) {
        try {
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", dotFile, "-o", pngFile);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            String output = new String(p.getInputStream().readAllBytes());
            int code = p.waitFor();

            if (code != 0) {
                throw new IllegalStateException("Graphviz dot a echoue (code " + code + "): " + output);
            }
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Impossible d'executer 'dot' (Graphviz). Est-il installe ?", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Execution de dot interrompue", e);
        }
    }

    // ---------- I/O fichiers ----------
    private void writeTextFile(String path, String content) {
        try (java.io.FileWriter fw = new java.io.FileWriter(path)) {
            fw.write(content);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Erreur ecriture fichier: " + path, e);
        }
    }

    // ---------- IDs DOT ----------
    private String safeId(String s) {
        return s.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}