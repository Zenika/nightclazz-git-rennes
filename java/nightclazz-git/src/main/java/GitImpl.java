import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.InflaterInputStream;

public class GitImpl {

    private final static String GIT_FOLDER = ".git";

    public static File gitObjectPath(String sha1) throws IOException, DataFormatException {

        File gitRoot = findGitRootFolder(new File(".").getAbsoluteFile());
        System.out.println("dossier .git trouvé : " + gitRoot);

        File objectsFolder = new File(gitRoot, GIT_FOLDER + "/objects");
        File intermediateFolder = new File(objectsFolder, sha1.substring(0, 2));

        return new File(intermediateFolder, sha1.substring(2));
    }

    private static File findGitRootFolder(File git) {
        System.out.println("recherche du dossier .git dans : " + git);
        File gitFile = new File(git, GIT_FOLDER);
        if (gitFile.exists()) {
            return git;
        } else if(git.getParentFile() == null) {
            // On est à la racine
            throw new RuntimeException("Not a git directory");
        }
        else {
            return findGitRootFolder(git.getParentFile());
        }
    }

}
