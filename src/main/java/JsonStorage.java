import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

public class JsonStorage {
    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static void save(String path, Noodle noodle) throws IOException {
        mapper.writeValue(new File(path), noodle);
    }

    public static Noodle load(String path) throws IOException {
        return mapper.readValue(new File(path), Noodle.class);
    }
}
