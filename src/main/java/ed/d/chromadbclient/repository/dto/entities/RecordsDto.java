package ed.d.chromadbclient.repository.dto.entities;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record RecordsDto(
        List<String> ids,
        List<String> documents,
        List<List<Float>> embeddings,
        List<Map<String, Object>> metadatas,
        List<String> uris,
        List<String> include
) {
}
