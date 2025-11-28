package ed.d.chromadbclient.repository.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HealthcheckResponseDto(

        @JsonProperty("is_executor_ready") Boolean isExecutorReady,
        @JsonProperty("is_log_client_ready") Boolean isLogClientReady
) {
}
