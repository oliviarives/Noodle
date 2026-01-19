import java.io.*;

/**
 * Stockage local sans dependance externe.
 * On utilise la serialisation Java (ObjectOutputStream/ObjectInputStream).
 */
public class JsonStorage {

    public static void save(String path, Noodle noodle) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(noodle);
        }
    }

    public static Noodle load(String path) throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(path);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Object obj = ois.readObject();
            return (Noodle) obj;
        }
    }
}
