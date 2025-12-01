package ed.d.chromadbclient.repository.querybuilders;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Inclusions {
    @Getter
    private final List<String> inclusions = new ArrayList<>();

    private Inclusions() {}

    public static Inclusions list() {
        return new Inclusions();
    }

    public Inclusions distances() {
        inclusions.add("distances");
        return this;
    }

    public Inclusions documents() {
        inclusions.add("documents");
        return this;
    }

    public Inclusions embeddings() {
        inclusions.add("embeddings");
        return this;
    }

    public Inclusions metadatas() {
        inclusions.add("metadatas");
        return this;
    }

    public Inclusions uris() {
        inclusions.add("uris");
        return this;
    }
}
