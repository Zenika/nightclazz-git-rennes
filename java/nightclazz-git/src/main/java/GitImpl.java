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

    public static byte[] uncompressObject(String sha1) throws IOException, DataFormatException {
        File gitFile = gitObjectPath(sha1);
        byte[] compressedByte = fileToByte(gitFile);
        return uncompressArray(compressedByte);
    }

    private static byte[] fileToByte(File git) throws IOException {
        byte[] byteArray = new byte[(int) git.length()];
        try (FileInputStream inputStream = new FileInputStream(git)) {
            inputStream.read(byteArray);
        }
        return byteArray;
    }

    private static byte[] uncompressArray(byte[] input) throws IOException {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
        InflaterInputStream inflater = new InflaterInputStream(inputStream);
        return inflater.readAllBytes();
    }

    public static GitFile readContent(String sha1) throws DataFormatException, IOException {
        byte[] fileData = uncompressObject(sha1);
        List<byte[]> content = ByteArrayHelper.splitByteArray(fileData, (byte) 0, 1);
        List<byte[]> header = ByteArrayHelper.splitByteArray(content.getFirst(), (byte) ' ', 1);
        int dataLen = Integer.parseInt(new String(header.getLast()));
        if(dataLen != content.getLast().length){
            throw new RuntimeException("Size not matching, got " + content.getLast().length + ", expected ");
        }
        return new GitFile(GitFile.Type.parse(header.getFirst()), dataLen, content.getLast());
    }
}
