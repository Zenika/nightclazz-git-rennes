import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ByteArrayHelper {
    public static List<byte[]> splitByteArray(byte[] array, byte delimiter, int limit){
        return splitByteArray(array, new byte[]{delimiter}, limit);
    }

    public static List<byte[]> splitByteArray(byte[] array, byte[] delimiter, int limit){
        ArrayList<byte[]> result = new ArrayList<>();
        int delimiterLength = delimiter.length;
        int lastSplit = 0;
        for(int i = 0; i < array.length - delimiterLength; i++){
            boolean found = true;
            for(int j = 0; j < delimiterLength; j++){
                found &= array[i + j] == delimiter[j];
            }
            if(found){
                result.add(Arrays.copyOfRange(array, lastSplit, i));
                lastSplit = i + delimiterLength;
                if(result.size() == limit){
                    break;
                }
            }
        }
        result.add(Arrays.copyOfRange(array, lastSplit, array.length));
        return result;
    }

    public static int findIndex(byte[] array, byte element, int start){
        for(int i = start; i < array.length; i++){
            if(array[i] == element){
                return i;
            }
        }
        return -1;
    }

    static String stringFromByte(byte[] bytes){
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String hexFromByte(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(String.format("%02x", aByte));
        }
        return result.toString();
    }
}
