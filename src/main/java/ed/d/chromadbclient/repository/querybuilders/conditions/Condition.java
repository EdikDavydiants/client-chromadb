package ed.d.chromadbclient.repository.querybuilders.conditions;

import java.util.Arrays;
import java.util.Map;

public abstract class Condition {
    private final String type;

    public Condition(String type) {
        this.type = type;
    }

    public static Condition not(Condition condition) {
        return new NotCondition(condition);
    }

    public static Condition or(Condition... conditions) {
        return new OrCondition(
                Arrays.stream(conditions)
                        .toList());
    }

    public static Condition and(Condition... conditions) {
        return new AndCondition(
                Arrays.stream(conditions)
                        .toList());
    }

    public static MetadataCondition metaCond(String name) {
        return new MetadataCondition(name);
    }

    public static DocCondition contains(String substring) {
        return new DocCondition("$contains", substring);
    }

    public static DocCondition notContains(String substring) {
        return new DocCondition("$not_contains", substring);
    }

    public Map<String, Object> toMap() {
        return Map.of(type, content());
    }

    public abstract Object content();
}
