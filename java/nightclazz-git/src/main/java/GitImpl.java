import java.io.*;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class GitImpl {

    private final static String GIT_FOLDER = ".git";

    public static File sha1(String sha1) throws IOException, DataFormatException {

        File gitTrouve = findGit(new File(".").getAbsoluteFile());
        System.out.println("dossier .git trouvé : " + gitTrouve);

        var objectsFolder = new File(gitTrouve, GIT_FOLDER + "/objects");
        var intermediateFolder = new File(objectsFolder, sha1.substring(0, 2));

        return new File(intermediateFolder, sha1.substring(2));
    }

    public static String contentSha1(File sha1) throws IOException, DataFormatException {
        var bytesGit = fileToByte(sha1);

        String decompress = decompress(bytesGit);

        System.out.println("fichier décompressé : " + decompress);
        return decompress;
    }

    private static byte[] fileToByte(File git) throws IOException {
        byte[] byteArray = new byte[(int) git.length()];
        try (FileInputStream inputStream = new FileInputStream(git)) {
            inputStream.read(byteArray);
        }
        return byteArray;
    }

    private static String decompress(byte[] input) throws IOException {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
        InflaterInputStream inflater = new InflaterInputStream(inputStream);
        return new String(inflater.readAllBytes());
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

    public static GitFile readContent(String content) {
        String[] split = content.split("\\x00");
        String[] header = split[0].split(" ");
        int size = Integer.parseInt(header[1]);
        String contentFile = split[1];
        if(size != contentFile.length()) {
            throw new IllegalArgumentException("Size not matching");
        }
        return new GitFile(GitFile.Type.parse(header[0]), size, contentFile);
    }
}
