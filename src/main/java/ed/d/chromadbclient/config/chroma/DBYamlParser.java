package ed.d.chromadbclient.config.chroma;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import ed.d.chromadbclient.exceptions.ChromaException;
import ed.d.chromadbclient.exceptions.ChromaYamlParsingException;
import ed.d.chromadbclient.repository.CollectionRepository;
import ed.d.chromadbclient.repository.DatabaseRepository;
import ed.d.chromadbclient.repository.TenantRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ed.d.chromadbclient.util.ExceptionMessages.CHROMA_CONTENT_ALREADY_EXIST;
import static ed.d.chromadbclient.util.ExceptionMessages.STRUCTURE_FILE_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class DBYamlParser {

    private final List<Tenant> tenants = new ArrayList<>();
    private final List<Database> databases = new ArrayList<>();
    private final List<Collection> collections = new ArrayList<>();
    private final TenantRepository tenantRepository;
    private final DatabaseRepository databaseRepository;
    private final CollectionRepository collectionRepository;
    private final DBYamlValidator validator;
    private final ChromaDbProperties dbProperties;
    private String tenant;
    private String database;

    @PostConstruct
    void init() {
        Map<String, Object> yamlMap;
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(dbProperties.getConfigFile())) {
            yamlMap = new Yaml().load(inputStream);
        } catch (Exception e) {
            throw new ChromaYamlParsingException(STRUCTURE_FILE_NOT_FOUND);
        }
        parse(yamlMap);
    }

    @RequiredArgsConstructor
    @Getter
    public static class Tenant {
        private final String name;
        private final String resourceName;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Database {
        private final String tenant;
        private final String name;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Collection {
        private final String tenant;
        private final String database;
        private final Map<String, Object> content;
    }

    public void parse(Object dbConfig) {
        validator.validStructure(dbConfig);
        iterate(dbConfig);
        validator.validNames(tenants, databases, collections);
        createEntitiesInChroma();
    }

    private void iterate(Object content) {
        if(content instanceof Map<?,?> contentMap) {
            iterateMap(contentMap);
        }
        if(content instanceof List<?> contentList) {
            iterateList(contentList);
        }
    }

    private void iterateMap(Map<?, ?> contentMap) {
        Set<? extends Map.Entry<?, ?>> entries = contentMap.entrySet();
        for(Map.Entry<?, ?> entry: entries) {
            if (entry.getKey().equals("tenants")) {
                iterateTenantList((List<?>) entry.getValue());
            } else if(entry.getKey().equals("databases")) {
                iterateDatabaseList((List<?>) entry.getValue());
            } else if(entry.getKey().equals("collections")) {
                iterateCollectionList((List<?>) entry.getValue());
            } else {
                iterate(entry.getValue());
            }
        }
    }

    private void iterateCollectionList(List<?> collectionList) {
        for (Object collection: collectionList) {
            addCollection(this.tenant, this.database, collection);
        }
    }

    private void iterateDatabaseList(List<?> databaseList) {
        for (Object database: databaseList) {
            String databaseName = (String) ((Map<?,?>) database).get("name");
            addDatabase(databaseName, this.tenant);
            this.database = databaseName;
            iterate(database);
        }
    }

    private void iterateTenantList(List<?> tenantList) {
        for (Object tenant: tenantList) {
            String tenantName = (String) ((Map<?,?>) tenant).get("name");
            String resourceName = (String) ((Map<?,?>) tenant).get("resourceName");
            addTenant(tenantName, resourceName);
            this.tenant = tenantName;
            iterate(tenant);
        }
    }

    private void iterateList(List<?> contentList) {
        for(Object obj: contentList) {
            iterate(obj);
        }
    }

    public void addTenant(String name, String resourceName) {
        tenants.add(new Tenant(name, resourceName));
    }

    public void addDatabase(String name, String tenant) {
        databases.add(new Database(tenant, name));
    }

    public void addCollection(String tenant, String database, Object content) {
        Map<String, Object> mapContent = (Map<String, Object>) content;
        mapContent.put("get_or_create", true);
        collections.add(new Collection(tenant, database, mapContent));
    }

    public void createEntitiesInChroma() {
        try {
            tenants.forEach(tenant ->
                    tenantRepository.createTenant(tenant.getName()));
            databases.forEach(database ->
                    databaseRepository.createDatabase(database.getTenant(), database.getName()));
            collections.forEach(collection ->
                    collectionRepository.createCollection(collection.getTenant(), collection.getDatabase(), collection.getContent()));
        }
        catch (ChromaException e) {
            if(!e.getMessage().equals(CHROMA_CONTENT_ALREADY_EXIST)) {
                throw new ChromaYamlParsingException("Entities creation error.");
            }
        }
    }
}
