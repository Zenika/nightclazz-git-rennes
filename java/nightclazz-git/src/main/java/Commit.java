import java.util.Map;

public record Commit(Map<String, String> headers, String message) {
}
