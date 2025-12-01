package ed.d.chromadbclient.repository.querybuilders.conditions;

import ed.d.chromadbclient.repository.querybuilders.GetBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetadataCondition extends Condition {
    private final Map<String, Object> subConditions = new HashMap<>();

    protected MetadataCondition(String fieldName) {
        super(fieldName);
    }

    @Override
    public Object content() {
        return subConditions;
    }

    public MetadataCondition gt(Object value) {
        subConditions.put("$gt", value);
        return this;
    }

    public MetadataCondition gte(Object value) {
        subConditions.put("$gte", value);
        return this;
    }

    public MetadataCondition lt(Object value) {
        subConditions.put("$lt", value);
        return this;
    }

    public MetadataCondition lte(Object value) {
        subConditions.put("$lte", value);
        return this;
    }

    public MetadataCondition eq(Object value) {
        subConditions.put("$eq", value);
        return this;
    }

    public MetadataCondition ne(Object value) {
        subConditions.put("$ne", value);
        return this;
    }

    public MetadataCondition in(Object... values) {
        subConditions.put("$in", List.of(values));
        return this;
    }
}
