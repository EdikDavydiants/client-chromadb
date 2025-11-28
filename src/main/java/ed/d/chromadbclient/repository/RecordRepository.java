package ed.d.chromadbclient.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ed.d.chromadbclient.config.chroma.ChromaUrlProvider;
import ed.d.chromadbclient.mapper.RecordMapper;
import ed.d.chromadbclient.repository.dto.entities.RecordsDto;
import ed.d.chromadbclient.wrapper.HttpClientWrapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RecordRepository extends ChromaRepository {

    private final ChromaUrlProvider urlProvider;
    private final HttpClientWrapper httpClient;
    private final RecordMapper recordMapper;
    private final CollectionRepository collectionRepository;

    public void addRecords(String tenantName, String databaseName, String collectionName, RecordsDto records) {
        String collectionId = collectionRepository.takeCollectionIdByName(tenantName, databaseName, collectionName);
        httpClient.post(
                urlProvider.addRecordsUrl(tenantName, databaseName, collectionId),
                recordMapper.mapRecordsDtoToMap(records)
        );
    }

    public RecordsDto getRecords(String tenantName, String databaseName, String collectionName, GetBuilder getBuilder) {
        String collectionId = collectionRepository.takeCollectionIdByName(tenantName, databaseName, collectionName);
        return httpClient.postWithResponse(
                urlProvider.getRecordsUrl(tenantName, databaseName, collectionId),
                getBuilder.toMap(),
                RecordsDto.class
        );
    }

    public static class GetBuilder {

        private String containedSubstr;
        private String excludedSubstr;
        private Condition metadataCondition;

        public static abstract class Condition {
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

            public static FieldCondition fieldCond(String name) {
                return new FieldCondition(name);
            }

            public Map<String, Object> toMap() {
                return Map.of(type, content());
            }

            public abstract Object content();
        }

        public static class FieldCondition extends Condition {

            private final Map<String, Object> subConditions = new HashMap<>();

            protected FieldCondition(String fieldName) {
                super(fieldName);
            }

            @Override
            public Object content() {
                return subConditions;
            }

            public FieldCondition gt(Object value) {
                subConditions.put("$gt", value);
                return this;
            }

            public FieldCondition gte(Object value) {
                subConditions.put("$gte", value);
                return this;
            }

            public FieldCondition lt(Object value) {
                subConditions.put("$lt", value);
                return this;
            }

            public FieldCondition lte(Object value) {
                subConditions.put("$lte", value);
                return this;
            }

            public FieldCondition eq(Object value) {
                subConditions.put("$eq", value);
                return this;
            }

            public FieldCondition ne(Object value) {
                subConditions.put("$ne", value);
                return this;
            }

            public FieldCondition in(Object... values) {
                subConditions.put("$in", List.of(values));
                return this;
            }
        }

        public static class AndCondition extends Condition {
            private final List<Condition> conditions;

            protected AndCondition(List<Condition> conditions) {
                super("$and");
                this.conditions = conditions;
            }

            @Override
            public Object content() {
                return conditions.stream()
                        .map(Condition::toMap)
                        .toList();
            }
        }

        public static class OrCondition extends Condition {
            private final List<Condition> conditions;

            protected OrCondition(List<Condition> conditions) {
                super("$or");
                this.conditions = conditions;
            }

            @Override
            public Object content() {
                return conditions.stream()
                        .map(Condition::toMap)
                        .toList();
            }
        }

        public static class NotCondition extends Condition {
            private final Condition condition;

            protected NotCondition(Condition condition) {
                super("$not");
                this.condition = condition;
            }

            @Override
            public Object content() {
                return condition.toMap();
            }
        }


        private GetBuilder() {}

        public static GetBuilder builder() {
            return new GetBuilder();
        }

        public GetBuilder contains(String substring) {
            this.containedSubstr = substring;
            return this;
        }

        public GetBuilder excludes(String substring) {
            this.excludedSubstr = substring;
            return this;
        }

        public GetBuilder metadataFilter(Condition metadataCondition) {
            this.metadataCondition = metadataCondition;
            return this;
        }

        public Map<String, Object> toMap() {
            Map<String, Object> body = new HashMap<>();
            if (containedSubstr != null || excludedSubstr != null) {
                Map<String, Object> documentFilter = new HashMap<>();
                if (containedSubstr != null) {
                    documentFilter.put("$contains", containedSubstr);
                }
                if (excludedSubstr != null) {
                    documentFilter.put("$not_contains", excludedSubstr);
                }
                body.put("where_document", documentFilter);
            }
            if (metadataCondition != null) {
                body.put("where", metadataCondition.toMap());
            }
            return body;
        }
    }
}
