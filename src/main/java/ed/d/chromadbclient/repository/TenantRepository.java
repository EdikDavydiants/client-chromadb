package ed.d.chromadbclient.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ed.d.chromadbclient.config.chroma.ChromaUrlProvider;
import ed.d.chromadbclient.exceptions.ChromaConflictException;
import ed.d.chromadbclient.repository.dto.entities.TenantDto;
import ed.d.chromadbclient.wrapper.HttpClientWrapper;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TenantRepository extends ChromaRepository {

    private final ChromaUrlProvider urlProvider;
    private final HttpClientWrapper httpClient;

    public void createTenant(String tenantName) {
        try {
            httpClient.post(
                    urlProvider.createTenantUrl(),
                    Map.of("name", tenantName));
        }
        catch (ChromaConflictException ignore) {}
    }

    public Optional<TenantDto> getTenant(String tenantName) {
        return httpClient.get(
                urlProvider.getTenantUrl(tenantName),
                TenantDto.class
        );
    }
}
