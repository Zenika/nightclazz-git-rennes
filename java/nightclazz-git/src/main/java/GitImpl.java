import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class GitImpl {

    private final static String SHA_1 = "95d09f2b10159347eece71399a7e2e907ea3df4f";
    private final static String GIT_FOLDER = ".git";

    public static void sha1() {

        File gitTrouve = findGit(new File(".").getAbsoluteFile());
        System.out.println("dossier .git trouvé : "+ gitTrouve);
    }

    private static File findGit(File git) {
        System.out.println("recherche du dossier .git dans : " + git);
        var gitFile = new File(git, GIT_FOLDER);
        if (gitFile.exists()) {
            return git;
        } else {
            return findGit(git.getParentFile());
        }
    }
}
