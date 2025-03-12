import java.io.IOException;
import java.util.zip.DataFormatException;

public class Main {

    private final static String SHA_1 = "95d09f2b10159347eece71399a7e2e907ea3df4f";


    public static void main(String[] args) {
        try {
            System.out.println("Récupération du path d'un fichier via son sha1 :");
            System.out.println(GitImpl.sha1(SHA_1));
        } catch (IOException | DataFormatException e) {
            // oh snap !
            throw new RuntimeException(e);
        }
    }
}
