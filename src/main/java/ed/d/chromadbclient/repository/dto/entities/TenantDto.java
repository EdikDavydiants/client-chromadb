package ed.d.chromadbclient.repository.dto.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TenantDto(
        String name,
        @JsonProperty("resource_name") String resourceName
) {
}
