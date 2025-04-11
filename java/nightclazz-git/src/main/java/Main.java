import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

public class Main {

    private final static String SHA_1 = "95d09f2b10159347eece71399a7e2e907ea3df4f";

    public static void main(String[] args) {
        try {
            System.out.println("Récupération du path d'un fichier via son sha1 :"); // 30 - 45min
            File objectPath = GitImpl.sha1(SHA_1);
            System.out.println(objectPath);

            System.out.println();
            System.out.println("Récupération du contenu d'un fichier via son sha1"); // 30 - 45min
            String content = GitImpl.contentSha1(objectPath);
            System.out.println(content);

            System.out.println();
            System.out.println("Contenu des fichirs");
            System.out.println(GitImpl.readContent(content)); // ~10min - 15min

            System.out.println();
        } catch (IOException | DataFormatException e) {
            // oh snap !
            throw new RuntimeException(e);
        }
    }
}
