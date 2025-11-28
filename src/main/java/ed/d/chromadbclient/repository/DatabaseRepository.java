package ed.d.chromadbclient.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ed.d.chromadbclient.config.chroma.ChromaUrlProvider;
import ed.d.chromadbclient.exceptions.ChromaConflictException;
import ed.d.chromadbclient.repository.dto.entities.DatabaseDto;
import ed.d.chromadbclient.wrapper.HttpClientWrapper;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class DatabaseRepository extends ChromaRepository {

    private final ChromaUrlProvider urlProvider;
    private final HttpClientWrapper httpClient;

    public List<DatabaseDto> getDatabases(String tenantName) {
        return httpClient.getList(
                urlProvider.getDatabasesUrl(tenantName),
                DatabaseDto.class
        );
    }

    public void createDatabase(String tenantName, String databaseName) {
        try {
            httpClient.post(
                    urlProvider.createDatabaseUrl(tenantName),
                    Map.of("name", databaseName)
            );
        } catch (ChromaConflictException ignore) {}
    }

    public void deleteDatabase(String tenantName, String databaseName) {
        httpClient.delete(
                urlProvider.deleteDatabaseUrl(tenantName, databaseName)
        );
    }
}
