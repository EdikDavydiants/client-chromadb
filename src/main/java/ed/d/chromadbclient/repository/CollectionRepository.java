package ed.d.chromadbclient.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ed.d.chromadbclient.config.chroma.ChromaUrlProvider;
import ed.d.chromadbclient.exceptions.ChromaConflictException;
import ed.d.chromadbclient.exceptions.ChromaNotFoundException;
import ed.d.chromadbclient.repository.dto.entities.CollectionDto;
import ed.d.chromadbclient.wrapper.HttpClientWrapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static ed.d.chromadbclient.util.ExceptionMessages.CHROMA_NOT_FOUND_CONTENT;

@Repository
@RequiredArgsConstructor
public class CollectionRepository extends ChromaRepository{

    private final ChromaUrlProvider urlProvider;
    private final HttpClientWrapper httpClient;
    private final Map<String, String> collectionsNameToIdMap = new ConcurrentHashMap<>();

    public void createCollection(String tenantName, String databaseName, String collectionName, boolean getOrCreate) {
        httpClient.post(
                urlProvider.createCollectionUrl(tenantName, databaseName),
                Map.of("get_or_create", getOrCreate, "name", collectionName)
        );
    }

    public void createCollection(String tenantName, String databaseName, Map<String, Object> content) {
        try {
            httpClient.post(
                    urlProvider.createCollectionUrl(tenantName, databaseName),
                    content
            );
        } catch (ChromaConflictException ignore) {}
    }

    public Optional<CollectionDto> getCollection(String tenantName, String databaseName, String collectionIdOrName) {
        return httpClient.get(
                urlProvider.getCollectionUrl(tenantName, databaseName, collectionIdOrName),
                CollectionDto.class
        );
    }

    public List<CollectionDto> getCollections(String tenantName, String databaseName) {
        return httpClient.getList(
                urlProvider.getCollectionsUrl(tenantName, databaseName),
                CollectionDto.class
        );
    }

    public void deleteCollection(String tenantName, String databaseName, String collectionIdOrName) {
        httpClient.delete(
                urlProvider.deleteCollectionUrl(tenantName, databaseName, collectionIdOrName)
        );
    }

    public String takeCollectionIdByName(String tenantName, String databaseName, String collectionName) {
        String key = tenantName + ":" + databaseName + ":" + collectionName;
        String collectionId = collectionsNameToIdMap.get(key);
        if(collectionId == null) {
            collectionId = getCollection(tenantName, databaseName, collectionName)
                    .orElseThrow(() -> new ChromaNotFoundException(CHROMA_NOT_FOUND_CONTENT))
                    .id();
            collectionsNameToIdMap.put(key, collectionId);
        }
        return collectionId;
    }
}
