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

}
