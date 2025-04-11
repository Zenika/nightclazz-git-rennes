import java.nio.charset.StandardCharsets;

public record GitFile(Type type, int size, byte[] content) {

    public enum Type{
        BLOB("blob"),
        TREE("tree"),
        COMMIT("commit");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public static Type parse(String name){
            for (Type type : Type.values()) {
                if(type.name.equals(name)){
                    return type;
                }
            }
            throw new IllegalArgumentException("Wrong git file type");
        }

        public static Type parse(byte[] name){
            return parse(new String(name, StandardCharsets.UTF_8));
        }
    }
}


