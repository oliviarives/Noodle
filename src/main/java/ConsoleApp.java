import java.util.Scanner;

public class ConsoleApp {

    private final Noodle noodle;
    private final String savePath;
    private final Scanner sc;

    // === Contexte (exigé par le sujet) ===
    private Diplome selectedDiplome = null;
    private int selectedYear = 1;

    public ConsoleApp(Noodle noodle, String savePath) {
        this.noodle = noodle;
        this.savePath = savePath;
        this.sc = new Scanner(System.in);
    }

    public void run() {
        System.out.println("Tape HELP pour la liste des commandes.");

        while (true) {
            System.out.print(prompt());
            String line = sc.nextLine();
            if (line == null) return;

            line = line.trim();
            if (line.isEmpty()) continue;

            Commande cmd;
            try {
                cmd = Commande.fromInput(line);
            } catch (Exception e) {
                System.out.println("Commande inconnue (HELP pour l'aide)");
                continue;
            }

            String argsLine = cmd.getArguments(line);
            String[] args = argsLine.isEmpty() ? new String[0] : argsLine.split("\\s+");

            try {
                boolean mutated = dispatch(cmd, args);

                if (mutated) {
                    JsonStorage.save(savePath, noodle);
                    System.out.println("Sauvegarde écrite: " + savePath);
                }

                if (cmd == Commande.EXIT) {
                    System.out.println("Au revoir.");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Erreur: " + e.getMessage());
            }
        }
    }

    private String prompt() {
        String deg = (selectedDiplome == null) ? "Aucun diplôme sélectionné" : selectedDiplome.nomDiplome;
        return "[" + deg + " / annee " + selectedYear + "] > ";
    }

    /**
     * @return true si la commande modifie l'état => sauvegarde
     */
    private boolean dispatch(Commande cmd, String[] args) throws Exception {
        switch (cmd) {
            case HELP:
                printHelp();
                return false;

            case LIST_DEGREES:
                noodle.afficherDiplomes();
                return false;

            case CREATE_DEGREE:
                handleCreateDegree(args);
                return true;

            case SELECT_DEGREE:
                handleSelectDegree(args);
                return false;

            case SELECT_YEAR:
                handleSelectYear(args);
                return false;

            case CREATE_UE:
                handleCreateUE(args);
                return true;

            case DISPLAY_GRAPH:
                handleDisplayGraph(args);
                return false;



            case EXIT:
                // Sauvegarde finale par sécurité
                JsonStorage.save(savePath, noodle);
                System.out.println("Sauvegarde écrite: " + savePath);
                return false;

            default:
                System.out.println("Commande non gérée (HELP pour l'aide)");
                return false;
        }
    }

    private void printHelp() {
        System.out.println("Commandes disponibles :");
        System.out.println("  CREATE DEGREE <nomDiplome> <typeDiplome> <annee> <ects> <ectsEntreprise>");
        System.out.println("  SELECT DEGREE <nomDiplome>");
        System.out.println("  SELECT YEAR <annee>");
        System.out.println("  CREATE UE <nomUE> <ects> <cm> <td> <tp>");
        System.out.println("  DISPLAY GRAPH <nomDiplome>");
        System.out.println("  LIST DEGREES");
        System.out.println("  HELP");
        System.out.println("  EXIT");
        System.out.println();
        System.out.println("Règle sujet: CREATE UE nécessite d'abord SELECT DEGREE puis SELECT YEAR.");
    }

    // ===== Handlers =====

    private void handleCreateDegree(String[] args) {
        if (args.length != 5) {
            throw new IllegalArgumentException(
                    "Usage: CREATE DEGREE <nomDiplome> <typeDiplome> <annee> <ects> <ectsEntreprise>"
            );
        }

        String nom = args[0];
        TypeDiplome type = TypeDiplome.valueOf(args[1].toUpperCase());
        int annee = Integer.parseInt(args[2]);
        int ects = Integer.parseInt(args[3]);
        int ectsEntreprise = Integer.parseInt(args[4]);

        noodle.creerDiplome(nom, type, annee, ects, ectsEntreprise);
        System.out.println("Diplôme créé: " + nom);
    }

    private void handleSelectDegree(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: SELECT DEGREE <nomDiplome>");
        }

        String nom = args[0];
        Diplome dip = noodle.getDiplome(nom);
        if (dip == null) {
            throw new IllegalArgumentException("Diplôme inconnu: " + nom);
        }

        selectedDiplome = dip;
        selectedYear = 1; // conformément au sujet
        System.out.println("Diplôme sélectionné: " + nom + " (année courante = 1)");
    }

    private void handleSelectYear(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: SELECT YEAR <annee>");
        }
        requireSelectedDegree();

        int y = Integer.parseInt(args[0]);

        int max = selectedDiplome.annee;
        if (y < 1 || y > max) {
            throw new IllegalArgumentException("Année invalide: " + y + " (1.." + max + ")");
        }

        selectedYear = y;
        System.out.println("Année sélectionnée: " + y);

        // Afficher les UE de l'année sélectionnée
        afficherUEAnneeCourante();
    }



    private void handleCreateUE(String[] args) {
        if (args.length != 5) {
            throw new IllegalArgumentException("Usage: CREATE UE <nomUE> <ects> <cm> <td> <tp>");
        }
        requireSelectedDegree();

        String nomUE = args[0];
        int ects = Integer.parseInt(args[1]);
        int cm = Integer.parseInt(args[2]);
        int td = Integer.parseInt(args[3]);
        int tp = Integer.parseInt(args[4]);

        selectedDiplome.creerUE(nomUE, ects, cm, td, tp, selectedYear);
        System.out.println("UE créée: " + nomUE + " (année " + selectedYear + ")");
    }

    private void handleDisplayGraph(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: DISPLAY GRAPH <nomDiplome>");
        }

        String nom = args[0];
        Diplome dip = noodle.getDiplome(nom);
        if (dip == null) throw new IllegalArgumentException("Diplôme inconnu: " + nom);

        // Affichage textuel de l'arbre (conforme énoncé)
        // Ici tu appelles ta méthode existante qui affiche le diplôme.
        // Si tu veux être strict, renomme consulterDiplome -> displayGraphText.
        noodle.consulterDiplome(dip);
    }


    private void requireSelectedDegree() {
        if (selectedDiplome == null) {
            throw new IllegalStateException("Aucun diplôme sélectionné. Utilise: SELECT DEGREE <nomDiplome>");
        }
    }

    private void afficherUEAnneeCourante() {
        System.out.println("UE de l’année " + selectedYear + " :");

        var ues = selectedDiplome.UEHashMap.get(selectedYear);
        if (ues == null || ues.isEmpty()) {
            System.out.println("  (Aucune UE)");
            return;
        }

        for (int i = 0; i < ues.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + ues.get(i));
        }
    }


}
