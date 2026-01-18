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

        new ConsoleApp(noodle, savePath).run();
    }
}
