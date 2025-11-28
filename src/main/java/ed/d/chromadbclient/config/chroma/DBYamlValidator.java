package ed.d.chromadbclient.config.chroma;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ed.d.chromadbclient.exceptions.ChromaYamlParsingException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class DBYamlValidator {

    private final Field validStructure;

    public DBYamlValidator() {
        this.validStructure = initStructure();
    }

    public void validStructure(Object dbConfig) {
        validStructure(validStructure, dbConfig);
    }

    private void validStructure(Field structure, Object content) {
        if(structure.contentType != content.getClass()) {
            throw new ChromaYamlParsingException("Wrong structure.");
        }
        if(content instanceof Map<?,?> contentMap) {
            validMap(structure, contentMap);
        }
        if(content instanceof List<?> contentList) {
            validList(structure, contentList);
        }
    }

    private void validList(Field structure, List<?> contentList) {
        Field contentStructure = structure.getFields().get(0);
        for(Object obj: contentList) {
            validStructure(contentStructure, obj);
        }
    }

    private void validMap(Field structure, Map<?, ?> contentMap) {
        List<String> possibleKeys = structure.getFields().stream()
                .map(Field::getName)
                .toList();
        for(Object key: contentMap.keySet()) {
            if(!possibleKeys.contains(key.toString())) {
                throw new ChromaYamlParsingException("Key word '" + key + "' is not valid.");
            }
        }
        for(Field field: structure.getFields()) {
            if (field.getName().equals("metadata")) {
                return;
            }
            Object obj = contentMap.get(field.getName());
            if(obj != null){
                validStructure(field, obj);
            } else if(field.isRequired()) {
                throw new ChromaYamlParsingException("Key word '" + field.getName() + "' is not found.");
            }
        }
    }

    private Field initStructure() {
        Field root = Field.root();
        root
        .next("tenants", true, ArrayList.class)
            .list()
            .next("name", true, String.class)
            .next("resource_name", false, String.class)
            .next("databases", false, ArrayList.class)
                .list()
                .next("name", true, String.class)
                .next("collections", false, ArrayList.class)
                    .list()
                    .next("name", true, String.class)
                    .next("configuration", false, LinkedHashMap.class).down()
                        .next("hnsw", false, LinkedHashMap.class).down()
                            .next("space", false, String.class)
                            .next("ef_construction", false, Integer.class)
                            .next("ef_search", false, Integer.class)
                            .next("max_neighbors", false, Integer.class)
                            .next("resize_factor", false, Double.class)
                            .next("sync_threshold", false, Integer.class).up()
                        .next("embedding_function", false, LinkedHashMap.class).down()
                            .next("type", false, String.class)
                            .next("name", false, String.class)
                            .next("config", false, LinkedHashMap.class).down()
                                .next("model", false, String.class)
                                .next("batch_size", false, Integer.class).up().up().up()
                    .next("metadata", false, LinkedHashMap.class);
        return root;
    }

    public void validNames(List<DBYamlParser.Tenant> tenants, List<DBYamlParser.Database> databases, List<DBYamlParser.Collection> collections) {
        long distCount = tenants.stream()
                .map(DBYamlParser.Tenant::getName)
                .distinct()
                .count();
        if(tenants.size() != distCount) {
            throw new ChromaYamlParsingException("Tenant names must be unique!");
        }
        distCount = databases.stream()
                .map(database -> database.getTenant() + database.getName())
                .distinct()
                .count();
        if(databases.size() != distCount) {
            throw new ChromaYamlParsingException("Database names must be unique in same tenant!");
        }

        distCount = collections.stream()
                .map(collection -> collection.getTenant() + collection.getTenant() + collection.getContent().get("name"))
                .distinct()
                .count();
        if(collections.size() != distCount) {
            throw new ChromaYamlParsingException("Collection names must be unique in same database!");
        }
    }

    @Getter
    @RequiredArgsConstructor
    private static class Field{
        private final String name;
        private final boolean isRequired;
        private final Class<?> contentType;
        private final List<Field> fields = new ArrayList<>();
        private final Field parent;

        public Field next(String name, boolean isRequired, Class<?> contentType) {
            fields.add(new Field(name, isRequired, contentType, this));
            return this;
        }

        public Field up() {
            return parent;
        }

        public Field down() {
            return fields.get(fields.size() - 1);
        }

        public Field list() {
            return down()
                    .next("", false, LinkedHashMap.class)
                    .down();
        }

        public static Field root() {
            return new Field("", true, LinkedHashMap.class, null);
        }
    }
}
