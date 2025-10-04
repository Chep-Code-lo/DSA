package dsa.tree;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;

public class FamilyIO {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static void save(PersonNode root, File file) throws IOException {
        try(Writer w = new OutputStreamWriter(new FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8)){
            GSON.toJson(root, w);
        }
    }
    public static PersonNode load(File file) throws IOException {
        try(Reader r = new InputStreamReader(new FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8)){
            return GSON.fromJson(r, PersonNode.class);
        }
    }
}
