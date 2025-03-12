import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

public class GitImpl {

    private final static String GIT_FOLDER = ".git";

    public static File sha1(String sha1) throws IOException, DataFormatException {

        File gitTrouve = findGit(new File(".").getAbsoluteFile());
        System.out.println("dossier .git trouvé : "+ gitTrouve);

        var objectsFolder = new File(gitTrouve,  GIT_FOLDER + "/objects");
        var intermediateFolder = new File(objectsFolder, sha1.substring(0, 2));

        return new File(intermediateFolder, sha1.substring(2));
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
