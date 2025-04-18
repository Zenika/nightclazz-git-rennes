import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.DataFormatException;

public class Main {

    private final static String SHA_1_TREE = "8a3e2535ec71c2e30e2a33e0d16ba95507fd9276";
    private final static String SHA_1_BLOB = "95d09f2b10159347eece71399a7e2e907ea3df4f";
    private final static String SHA_1_COMMIT = "81f46abdf6b06d8d9b4c6785fff98e540c567326";

    public static void main(String[] args) {
        try {
            System.out.println("Récupération du path d'un fichier via son sha1 :"); // 30 - 45min
            File objectPath = GitImpl.sha1(SHA_1_BLOB);
            System.out.println(objectPath);

            System.out.println();
            System.out.println("Récupération du contenu d'un fichier via son sha1"); // 30 - 45min
            var content = GitImpl.contentSha1(objectPath);
            System.out.println(new String(content, StandardCharsets.UTF_8));

            System.out.println();
            System.out.println("Contenu des fichiers");
            System.out.println(GitImpl.readContent(content)); // ~10min - 15min

            System.out.println("Parsing de tree");
            List<GitTreeEntry> gitTreeEntries = GitImpl.parseTree(GitImpl.readContent(GitImpl.contentSha1(GitImpl.sha1(SHA_1_TREE))));
            System.out.println(gitTreeEntries);

            System.out.println("Parsing de commit");
            GitCommit gitCommit = GitImpl.parseCommit(GitImpl.readContent(GitImpl.contentSha1(GitImpl.sha1(SHA_1_COMMIT))));
            System.out.println(gitCommit);
        } catch (IOException | DataFormatException e) {
            // oh snap !
            throw new RuntimeException(e);
        }
    }
}
