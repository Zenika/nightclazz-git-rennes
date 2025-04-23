import org.bouncycastle.util.encoders.Hex;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
        var content = splitByteArrayForCommit(file, (byte) 0, 1);
        var header = splitByteArrayForCommit(content.getFirst(), (byte) ' ', 1);
        int dataLen = Integer.parseInt(new String(header.getLast()));
        if(dataLen != content.getLast().length){
            throw new RuntimeException("Size not matching, got " + content.getLast().length + ", expected ");
        }
        return new GitFile(GitFile.Type.parse(header.getFirst()), dataLen, content.getLast());
    }

    private static List<byte[]> splitByteArrayForCommit(byte[] array, byte delimiter, int limit){
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

    // on cherche le premier x00 et après on cherche 20 caractères et on coupe
    public static GitTree parseTree(byte[] content){

        List<byte[]> entriesByte = buildEntries(content, 0);
        return new GitTree(entriesByte.stream()
                .map(e -> {
                    List<byte[]> headersAndSha1 = splitByteArrayForCommit(e, (byte) 0, 1);
                    List<byte[]> modeAndNom = splitByteArrayForCommit(headersAndSha1.getFirst(), (byte) ' ', 1);
                    return new GitTreeEntry(new String(modeAndNom.getFirst()), new String(modeAndNom.getLast()), Hex.toHexString(headersAndSha1.getLast()));

                })
                .toList());
    }

    private static List<byte[]> buildEntries(byte[] content, int separator) {
        List<byte[]> entriesByte = new ArrayList<>();

        int startEntryIndex = 0;
        int endEntryIndex;
        for (int i = 0; i < content.length -1; i++) {
            byte aByte = content[i];
            if (aByte == (byte) separator) {
                endEntryIndex = i + 1 + 20; // tkt frère
                entriesByte.add(Arrays.copyOfRange(content, startEntryIndex, endEntryIndex));
                startEntryIndex = endEntryIndex;
            }
        }
        return entriesByte;
    }

    public static Commit parseCommit(byte[] content){
        List<byte[]> bytes = splitByteArrayForCommit(content, new byte[]{(byte) '\n', (byte) '\n'}, 1);
        if(bytes.size() != 2){
            throw new IllegalArgumentException("Commit file has not the correct format");
        }
        byte[] headers = bytes.getFirst();
        Map<String, String> headersCommit = splitByteArrayForCommit(headers, (byte) '\n', 10).stream()
                .collect(
                        Collectors.toMap(
                                b -> new String(splitByteArrayForCommit(b, (byte) ' ', 1).getFirst()),
                                b -> new String(splitByteArrayForCommit(b, (byte) ' ', 1).getLast())));


        return new Commit(headersCommit, new String(bytes.getLast()));

    }

    private static List<byte[]> splitByteArrayForCommit(byte[] array, byte[] delimiter, int limit){
        ArrayList<byte[]> result = new ArrayList<>();
        int lastSplit = 0;
        for(int i = 0; i < array.length -1; i++){
            if(array[i] == delimiter[0] && array[i+1] == delimiter[1]){
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
}
