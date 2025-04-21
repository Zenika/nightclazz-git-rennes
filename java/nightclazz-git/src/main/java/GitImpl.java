import java.io.*;
import java.nio.charset.StandardCharsets;
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

        var objectsFolder = new File(gitRoot, GIT_FOLDER + "/objects");
        var intermediateFolder = new File(objectsFolder, sha1.substring(0, 2));

        return new File(intermediateFolder, sha1.substring(2));
    }

    public static byte[] uncompressObject(String sha1) throws IOException, DataFormatException {
        File gitFile = gitObjectPath(sha1);
        var compressedByte = fileToByte(gitFile);
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

    private static File findGitRootFolder(File git) {
        System.out.println("recherche du dossier .git dans : " + git);
        var gitFile = new File(git, GIT_FOLDER);
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

    public static GitFile readContent(String sha1) throws DataFormatException, IOException {
        byte[] fileData = uncompressObject(sha1);
        var content = ByteArrayHelper.splitByteArray(fileData, (byte) 0, 1);
        var header = ByteArrayHelper.splitByteArray(content.getFirst(), (byte) ' ', 1);
        int dataLen = Integer.parseInt(new String(header.getLast()));
        if(dataLen != content.getLast().length){
            throw new RuntimeException("Size not matching, got " + content.getLast().length + ", expected ");
        }
        return new GitFile(GitFile.Type.parse(header.getFirst()), dataLen, content.getLast());
    }

    public static List<GitTreeEntry> parseTree(GitFile file){
        List<GitTreeEntry> result = new ArrayList<>();
        int start = 0;
        while (start < file.content().length){
            var separatorIndex = ByteArrayHelper.findIndex(file.content(), (byte) 0, start);
            var entryEnd = separatorIndex + 21;
            result.add(parseTreeEntry(Arrays.copyOfRange(file.content(), start, entryEnd)));
            start = entryEnd;
        }
        return result;
    }

    public static GitCommit parseCommit(GitFile file){
        var content = ByteArrayHelper.splitByteArray(file.content(), new byte[]{(byte) '\n', (byte) '\n'}, 1);
        var header = ByteArrayHelper.splitByteArray(content.getFirst(), (byte) '\n', Integer.MAX_VALUE);
        return new GitCommit( header.stream().map(e -> ByteArrayHelper.splitByteArray(e, (byte) ' ', 1))
                .collect(Collectors.toMap(e -> stringFromByte(e.getFirst()), e -> stringFromByte(e.getLast()))), stringFromByte(content.getLast()));
    }

    private static GitTreeEntry parseTreeEntry(byte[] entry){
        var content = ByteArrayHelper.splitByteArray(entry, (byte) 0, 1);
        var header = ByteArrayHelper.splitByteArray(content.getFirst(), (byte) ' ', 1);
        return new GitTreeEntry(stringFromByte(header.getFirst()), stringFromByte(header.getLast()), hexFromByte(content.getLast()));
    }

    private static String stringFromByte(byte[] bytes){
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String hexFromByte(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(String.format("%02x", aByte));
            // upper case
            // result.append(String.format("%02X", aByte));
        }
        return result.toString();
    }
}
