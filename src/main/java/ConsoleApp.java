import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleApp {

    private final Noodle noodle;
    private final String savePath;
    private final Scanner sc;

    // Contexte de navigation
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

            try {
                executeLine(line);
            } catch (ExitSignal ex) {
                System.out.println("Au revoir.");
                return;
            } catch (Exception e) {
                System.out.println("Erreur: " + e.getMessage());
            }
        }
    }


    private String prompt() {
        String deg = (selectedDiplome == null) ? "Aucun diplome selectionne" : selectedDiplome.nomDiplome;
        return "[" + deg + " / annee " + selectedYear + "] > ";
    }

    /**
     * @return true si la commande modifie l'etat => sauvegarde
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

            case DELETE_UE:
                handleDeleteUE(args);
                return true;

            case DISPLAY_GRAPH:
                handleDisplayGraph(args);
                return false;

            case GET_TOTAL:
                handleGetTotal(args);
                return false;

            case CREATE_TEACHER:
                handleCreateTeacher(args);
                return true;

            case ASSIGN:
                handleAssign(args);
                return true;

            case EXIT:
                // sauvegarde finale par securite
                JsonStorage.save(savePath, noodle);
                System.out.println("Sauvegarde ecrite: " + savePath);
                return false;

            case EDIT_UE:
                handleEditUE(args);
                return true;

            case ASSIGN_UE:
                handleAssignUE(args);
                return true;

            case GET_COVER:
                handleGetCover(args);
                return false;
            case GET_SEANCE:
                handleGetSeance(args);
                return false;
            case TRACE_GRAPH:
                handleTraceGraph(args);
                return false;
            case RUN:
                handleRun(args);
                return false;







            default:
                System.out.println("Commande non geree (HELP pour l'aide)");
                return false;
        }
    }

    private void printHelp() {
        System.out.println("Commandes disponibles :");
        System.out.println("  CREATE DEGREE <nomDiplome> <typeDiplome> <annee> <maxEtu> <ects>");
        System.out.println("  SELECT DEGREE <nomDiplome>");
        System.out.println("  SELECT YEAR <annee>");
        System.out.println("  CREATE UE <nomUE> <ects> <cm> <td> <tp>");
        System.out.println("  DELETE UE <nomUE>");
        System.out.println("  DISPLAY GRAPH <nomDiplome>");
        System.out.println("  LIST DEGREES");
        System.out.println("  GET TOTAL <name>   (name = ALL | nomDiplome | nomUE | nomEnseignant)");
        System.out.println("  CREATE TEACHER <nom> <prenom>");
        System.out.println("  ASSIGN <nomUE> <nomEnseignant> <nbHeures>");
        System.out.println("  EDIT UE <name> <ects> <cm> <td> <tp>");
        System.out.println("  ASSIGN UE <name> <diplome> <year>");
        System.out.println("  GET SEANCE <name>   (name = ALL | nomDiplome | nomUE)");
        System.out.println("  GET COVER <name>   (name = ALL | nomDiplome | nomUE)");
        System.out.println("  TRACE GRAPH <diplome> <filename.png>");

        System.out.println("  HELP");
        System.out.println("  EXIT");
        System.out.println();
        System.out.println("Regle sujet: CREATE UE necessite d'abord SELECT DEGREE puis SELECT YEAR.");
    }

    // ===== Handlers =====


    private void handleGetSeance(String[] args) {


        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: GET SEANCE <name>");
        }
        String name = args[0];
        int nbSeances = noodle.getSeance(name);
        System.out.println(nbSeances +" séances sont nécessaires");
    }


    private void handleCreateDegree(String[] args) {
        if (args.length != 5) {
            throw new IllegalArgumentException(
                    "Usage: CREATE DEGREE <nomDiplome> <typeDiplome> <annee> <maxEtu> <ects>"
            );
        }

        String nom = args[0];
        TypeDiplome type = TypeDiplome.valueOf(args[1].toUpperCase());
        int annee = Integer.parseInt(args[2]);
        int maxEtu = Integer.parseInt(args[3]);
        int ects = Integer.parseInt(args[4]);

        noodle.creerDiplome(nom, type, annee, maxEtu, ects);
        System.out.println("Diplome cree: " + nom);
    }

    private void handleSelectDegree(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: SELECT DEGREE <nomDiplome>");
        }

        String nom = args[0];
        Diplome dip = noodle.getDiplome(nom);
        if (dip == null) {
            throw new IllegalArgumentException("Diplome inconnu: " + nom);
        }

        selectedDiplome = dip;
        selectedYear = 1; // conforme au sujet
        System.out.println("Diplome selectionne: " + nom + " (annee courante = 1)");
    }

    private void handleSelectYear(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: SELECT YEAR <annee>");
        }
        requireSelectedDegree();

        int y = Integer.parseInt(args[0]);
        int max = selectedDiplome.annee;
        if (y < 1 || y > max) {
            throw new IllegalArgumentException("Annee invalide: " + y + " (1.." + max + ")");
        }

        selectedYear = y;
        System.out.println("Annee selectionnee: " + y);
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
        System.out.println("UE creee: " + nomUE + " (annee " + selectedYear + ")");
    }

    private void handleDeleteUE(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: DELETE UE <nomUE>");
        }
        requireSelectedDegree();

        String nomUE = args[0];
        selectedDiplome.supprimerUE(nomUE, selectedYear);

        System.out.println("UE supprimee: " + nomUE + " (annee " + selectedYear + ")");
        afficherUEAnneeCourante();
    }

    private void handleDisplayGraph(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: DISPLAY GRAPH <nomDiplome>");
        }

        String nom = args[0];
        Diplome dip = noodle.getDiplome(nom);
        if (dip == null) throw new IllegalArgumentException("Diplome inconnu: " + nom);

        noodle.displayGraphDiplome(dip);
    }


    private void handleGetTotal(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: GET TOTAL <name>");
        }

        String name = args[0];
        int total = noodle.getTotal(name);
        TotalTargetType type = noodle.getTotalTargetType(name);

        switch (type) {
            case ALL:
                System.out.println("L'offre de formation represente actuellement " + total + " heures (CM, TD, TP)");
                break;
            case ENSEIGNANT:
                System.out.println("Le nombre d'heures de " + name + " est de " + total + " heures");
                break;
            case DIPLOME:
            case UE:
                System.out.println(name + " represente actuellement " + total + " heures (CM, TD, TP)");
                break;
        }
    }

    private void handleCreateTeacher(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: CREATE TEACHER <nom> <prenom>");
        }

        String nom = args[0];
        String prenom = args[1];

        noodle.enregistrerEnseignant(nom, prenom);
        System.out.println("Enseignant(e) " + prenom + " " + nom + " cree(e)");
    }

    /**
     * ITE-3 - Commande conforme:
     * ASSIGN <nameUE> <nameTeacher> <hours>
     */
    private void handleAssign(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Usage: ASSIGN <nomUE> <nomEnseignant> <nbHeures>");
        }

        String nomUE = args[0];
        String nomEnseignant = args[1];
        int heures = Integer.parseInt(args[2]);

        // Ajustement: la console ne manipule plus UE directement
        noodle.assignerEnseignant(nomUE, nomEnseignant, heures);

        System.out.println("L'enseignant(e) " + nomEnseignant
                + " affecte(e) a l'UE " + nomUE + " pour " + heures + " heures");
    }

    private void handleEditUE(String[] args) {
        if (args.length != 5) {
            throw new IllegalArgumentException("Usage: EDIT UE <name> <ects> <cm> <td> <tp>");
        }
        requireSelectedDegree();

        String nomUE = args[0];
        int ects = Integer.parseInt(args[1]);
        int cm = Integer.parseInt(args[2]);
        int td = Integer.parseInt(args[3]);
        int tp = Integer.parseInt(args[4]);

        // Appelle ici la méthode que TU as ajoutée dans Diplome
        // Ex: selectedDiplome.editerUE(...) ou selectedDiplome.modifierUE(...)
        selectedDiplome.editerUE(nomUE, ects, cm, td, tp, selectedYear);

        System.out.println("UE modifiee: " + nomUE + " (annee " + selectedYear + ")");
        afficherUEAnneeCourante();
    }


    private void requireSelectedDegree() {
        if (selectedDiplome == null) {
            throw new IllegalStateException("Aucun diplome selectionne. Utilise: SELECT DEGREE <nomDiplome>");
        }
    }

    private void afficherUEAnneeCourante() {
        System.out.println("UE de l'annee " + selectedYear + " :");

        ArrayList<UE> ues = selectedDiplome.UEHashMap.get(selectedYear);
        if (ues == null || ues.isEmpty()) {
            System.out.println("  (Aucune UE)");
            return;
        }

        for (int i = 0; i < ues.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + ues.get(i));
        }
    }

    private void handleAssignUE(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Usage: ASSIGN UE <name> <diplome> <year>");
        }

        String nomUE = args[0];
        String nomDiplome = args[1];
        int year = Integer.parseInt(args[2]);

        // Appelle ici la méthode que TU as ajoutée dans Noodle
        // Ex: noodle.assignerUEMutualisee(nomUE, nomDiplome, year);
        noodle.assignerUEMutualisee(nomUE, nomDiplome, year);

        System.out.println("UE " + nomUE + " liee au diplome " + nomDiplome + " (annee " + year + ")");
    }

    private void handleGetCover(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: GET COVER <name>");
        }
        System.out.println(noodle.getCover(args[0]));
    }

    private void handleTraceGraph(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: TRACE GRAPH <diplome> <filename.png>");
        }

        String diplomeName = args[0];
        String fileName = args[1];

        noodle.traceGraph(diplomeName, fileName);

        System.out.println("Graphe genere: " + fileName);
    }


    private void executeLine(String line) throws Exception {
        if (line == null) return;

        line = line.trim();
        if (line.isEmpty()) return;

        // commentaires
        if (line.startsWith("#") || line.startsWith("//")) return;

        Commande cmd;
        try {
            cmd = Commande.fromInput(line);
        } catch (Exception e) {
            throw new IllegalArgumentException("Commande inconnue (HELP pour l'aide)");
        }

        String argsLine = cmd.getArguments(line);
        String[] args = argsLine.isEmpty() ? new String[0] : argsLine.split("\\s+");

        boolean mutated = dispatch(cmd, args);

        if (mutated) {
            JsonStorage.save(savePath, noodle);
            System.out.println("Sauvegarde ecrite: " + savePath);
        }

        if (cmd == Commande.EXIT) {
            // On sort proprement si on exécute en interactif.
            // En script, ça arrêtera aussi le script (ce qui est généralement souhaité).
            throw new ExitSignal();
        }
    }


    public void runScript(String filePath) {
        System.out.println("Working dir = " + System.getProperty("user.dir"));

        java.nio.file.Path path = java.nio.file.Paths.get(filePath);
        if (!java.nio.file.Files.exists(path)) {
            throw new IllegalArgumentException("Fichier introuvable: " + filePath);
        }

        try (java.io.BufferedReader br = java.nio.file.Files.newBufferedReader(path)) {
            String line;
            int lineNo = 0;

            while ((line = br.readLine()) != null) {
                lineNo++;
                String trimmed = line.trim();

                // ignorer vides / commentaires
                if (trimmed.isEmpty()) continue;
                if (trimmed.startsWith("#") || trimmed.startsWith("//")) continue;

                try {
                    System.out.println(">> " + trimmed);
                    executeLine(trimmed);
                } catch (ExitSignal ex) {
                    System.out.println("Script interrompu par EXIT.");
                    return;
                } catch (Exception e) {
                    System.out.println("Erreur script ligne " + lineNo + " : " + trimmed);
                    System.out.println("  -> " + e.getMessage());
                }

            }
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Erreur lecture fichier: " + filePath, e);
        }
    }
    private static class ExitSignal extends RuntimeException {
        // Exception "technique" pour sortir de la boucle sans dupliquer la logique
    }
    private void handleRun(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: RUN <script.txt>");
        }
        runScript(args[0]);
    }




}