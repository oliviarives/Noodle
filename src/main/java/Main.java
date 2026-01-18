import java.nio.file.Files;
import java.nio.file.Path;


public class Main {
    public static void main(String[] args) {

        String savePath = "noodle.json";
        Noodle noodle;

        try {
            if (Files.exists(Path.of(savePath))) {
                noodle = JsonStorage.load(savePath);
                System.out.println("Sauvegarde chargée: " + savePath);
            } else {
                noodle = new Noodle();
                System.out.println("Aucune sauvegarde trouvée, nouvelle instance.");
            }
        } catch (Exception e) {
            noodle = new Noodle();
            System.out.println("Erreur chargement JSON, nouvelle instance. Détail: " + e.getMessage());
        }

        Diplome dip;

        // Creation d'un diplome
        dip = noodle.creerDiplome("LicenceInfo", TypeDiplome.LICENCE, 3, 180, 180);

        // Tentative de doublon levé de l'exception
        //noodle.creerDiplome("LicenceInfo", TypeDiplome.LICENCE, 3, 180, 180);

        UE ueTest = dip.creerUE("Maths",6,10,10,10,2);
        dip.creerUE("Français",6,10,10,10,2);
        dip.creerUE("Philosophie",6,10,10,10,1);

        noodle.consulterDiplome(dip);
        noodle.consulterAnneeDiplome(dip,2);

        Enseignant enseignant1 = new Enseignant("Damien", "Gouteux");
        noodle.ajouterEnseigantUE(ueTest,enseignant1,10);

        Diplome dip2 = noodle.creerDiplome("LicenceMIASHS", TypeDiplome.LICENCE, 3, 180, 180);
        noodle.assignerUE(dip2,ueTest,2);

        noodle.afficherDiplomes();

        try {
            JsonStorage.save(savePath, noodle);
            System.out.println("Sauvegarde écrite: " + savePath);
        } catch (Exception e) {
            System.out.println("Erreur sauvegarde JSON: " + e.getMessage());
        }




    }
}