package ed.d.chromadbclient.repository.querybuilders;

import ed.d.chromadbclient.repository.querybuilders.conditions.Condition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetBuilder {

    private Condition docConditions;
    private Condition metadataConditions;
    private Inclusions inclusions;
    private List<String> ids;

    protected GetBuilder() {}

    public GetBuilder docFilter(Condition docConditions) {
        this.docConditions = docConditions;
        return this;
    }

    public GetBuilder metadataFilter(Condition metadataConditions) {
        this.metadataConditions = metadataConditions;
        return this;
    }

    public GetBuilder include(Inclusions inclusions) {
        this.inclusions = inclusions;
        return this;
    }

    public GetBuilder ids(List<String> ids) {
        this.ids = ids;
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> body = new HashMap<>();
        if (docConditions != null) {
            body.put("where_document", docConditions.toMap());
        }
        if (metadataConditions != null) {
            body.put("where", metadataConditions.toMap());
        }
        if (inclusions != null) {
            body.put("include", inclusions.getInclusions());
        }
        if (ids != null) {
            body.put("ids", ids);
        }
        return body;
    }
}
