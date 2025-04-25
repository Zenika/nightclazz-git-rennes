import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.DataFormatException;

public class Main {

    private final static String SHA_1_BLOB = "95d09f2b10159347eece71399a7e2e907ea3df4f";

    public static void main(String[] args) {
        try {
            System.out.println("Récupération du path d'un fichier via son sha1 :");
            File objectPath = GitImpl.gitObjectPath(SHA_1_BLOB);
            System.out.println(objectPath);

        } catch (IOException | DataFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
