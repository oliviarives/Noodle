import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

public class JsonStorage {

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            // Autorise Jackson à sérialiser directement les champs (même sans getters)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    /**
     * Sauvegarde l'état complet du système dans un fichier JSON
     */
    public static void save(String path, Noodle noodle) throws IOException {
        mapper.writeValue(new File(path), noodle);
    }

    /**
     * Charge l'état complet du système depuis un fichier JSON
     */
    public static Noodle load(String path) throws IOException {
        return mapper.readValue(new File(path), Noodle.class);
    }
}
