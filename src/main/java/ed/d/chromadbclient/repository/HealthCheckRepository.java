package ed.d.chromadbclient.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ed.d.chromadbclient.config.chroma.ChromaUrlProvider;
import ed.d.chromadbclient.exceptions.ChromaException;
import ed.d.chromadbclient.repository.dto.responses.HealthcheckResponseDto;
import ed.d.chromadbclient.wrapper.HttpClientWrapper;

import static ed.d.chromadbclient.util.ExceptionMessages.CHROMA_UNKNOWN_ERROR;

@Repository
@RequiredArgsConstructor
public class HealthCheckRepository extends ChromaRepository{

    private final ChromaUrlProvider urlProvider;
    private final HttpClientWrapper httpClient;

    public HealthcheckResponseDto checkHealth() {
        return httpClient.get(
                urlProvider.healthCheckUrl(),
                HealthcheckResponseDto.class
        ).orElseThrow(() -> new ChromaException(CHROMA_UNKNOWN_ERROR));
    }
}
