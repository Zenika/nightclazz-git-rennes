import java.util.Map;

public record GitCommit(Map<String, String> header, String commentaire) {
}
