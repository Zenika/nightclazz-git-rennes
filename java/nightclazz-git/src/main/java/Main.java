import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;

public class Main {

    private final static String SHA_1_TREE = "8a3e2535ec71c2e30e2a33e0d16ba95507fd9276";
    private final static String SHA_1_BLOB = "95d09f2b10159347eece71399a7e2e907ea3df4f";
    private final static String SHA_1_COMMIT = "9f11a4ecd446039411d5f3afc578a11bde3457fa";

    public static void main(String[] args) {
        try {
            System.out.println("Récupération du path d'un fichier via son sha1 :"); // 30 - 45min
            File objectPath = GitImpl.sha1(SHA_1_COMMIT);
            System.out.println(objectPath);

            System.out.println();
            System.out.println("Récupération du contenu d'un fichier via son sha1"); // 30 - 45min
            var content = GitImpl.contentSha1(objectPath);
            System.out.println(new String(content, StandardCharsets.UTF_8));

            System.out.println();
            System.out.println("Contenu des fichiers");
            GitFile gitContent = GitImpl.readContent(content);
            System.out.println(gitContent); // ~10min - 15min

//            System.out.println();
//            System.out.println("parse tree");
//            System.out.println(GitImpl.parseTree(gitContent.content()));

            System.out.println();
            System.out.println("parse commit");
            System.out.println(GitImpl.parseCommit(gitContent.content()));
        } catch (IOException | DataFormatException e) {
            // oh snap !
            throw new RuntimeException(e);
        }
    }
}
