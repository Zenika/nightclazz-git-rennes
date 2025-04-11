import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;
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

    public static byte[] contentSha1(File sha1) throws IOException, DataFormatException {
        var bytesGit = fileToByte(sha1);

        return decompress(bytesGit);
    }

    private static byte[] fileToByte(File git) throws IOException {
        byte[] byteArray = new byte[(int) git.length()];
        try (FileInputStream inputStream = new FileInputStream(git)) {
            inputStream.read(byteArray);
        }
        return byteArray;
    }

    private static byte[] decompress(byte[] input) throws IOException {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
        InflaterInputStream inflater = new InflaterInputStream(inputStream);
        return inflater.readAllBytes();
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

    public static GitFile readContent(byte[] file) throws DataFormatException, IOException {
        var content = splitByteArray(file, (byte) 0, 1);
        var header = splitByteArray(content.getFirst(), (byte) ' ', 1);
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
            var separatorIndex = findIndex(file.content(), (byte) 0, start);
            var entryEnd = separatorIndex + 21;
            result.add(parseTreeEntry(Arrays.copyOfRange(file.content(), start, entryEnd)));
            start = entryEnd;
        }
        return result;
    }

    private static GitTreeEntry parseTreeEntry(byte[] entry){
        var content = splitByteArray(entry, (byte) 0, 1);
        var header = splitByteArray(content.getFirst(), (byte) ' ', 1);
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

    private static List<byte[]> splitByteArray(byte[] array, byte delimiter, int limit){
        ArrayList<byte[]> result = new ArrayList<>();
        int lastSplit = 0;
        for(int i = 0; i < array.length; i++){
            if(array[i] == delimiter){
                result.add(Arrays.copyOfRange(array, lastSplit, i));
                lastSplit = i+1;
                if(result.size() == limit){
                    break;
                }
            }
        }
        result.add(Arrays.copyOfRange(array, lastSplit, array.length));
        return result;
    }

    private static int findIndex(byte[] array, byte element, int start){
        for(int i = start; i < array.length; i++){
            if(array[i] == element){
                return i;
            }
        }
        return -1;
    }
}
