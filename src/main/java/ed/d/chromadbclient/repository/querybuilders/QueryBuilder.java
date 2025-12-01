package ed.d.chromadbclient.repository.querybuilders;

import ed.d.chromadbclient.repository.querybuilders.conditions.Condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryBuilder extends GetBuilder {

    private final float[] embedding;
    private Integer nResults;

    private QueryBuilder(float[] embedding) {
        this.embedding = embedding;
    }

    public static QueryBuilder queryBuilder(float[] embedding) {
        return new QueryBuilder(embedding);
    }

    public static GetBuilder getBuilder() {
        return new GetBuilder();
    }

    public QueryBuilder nResults(int nResults) {
        this.nResults = nResults;
        return this;
    }

    @Override
    public QueryBuilder docFilter(Condition docConditions) {
        return (QueryBuilder) super.docFilter(docConditions);
    }

    @Override
    public QueryBuilder metadataFilter(Condition metadataConditions) {
        return (QueryBuilder) super.metadataFilter(metadataConditions);
    }

    @Override
    public QueryBuilder include(Inclusions inclusions) {
        return (QueryBuilder) super.include(inclusions);
    }

    @Override
    public QueryBuilder ids(List<String> ids) {
        return (QueryBuilder) super.ids(ids);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        List<Float> embeddingList = new ArrayList<>(embedding.length);
        for (int i = 0; i < embedding.length; i++) {
            embeddingList.add(i, embedding[i]);
        }
        map.put("embeddings", List.of(embeddingList));
        if (nResults != null) {
            map.put("n_results", nResults);
        }
        return map;
    }
}
