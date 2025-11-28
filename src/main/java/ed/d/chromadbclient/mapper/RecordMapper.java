package ed.d.chromadbclient.mapper;

import org.springframework.stereotype.Component;
import ed.d.chromadbclient.repository.dto.entities.RecordsDto;

import java.util.HashMap;
import java.util.Map;

@Component
public class RecordMapper {

    public Map<String, Object> mapRecordsDtoToMap(RecordsDto records) {
        Map<String, Object> map = new HashMap<>();
        map.put("ids", records.ids());
        map.put("documents", records.documents());
        if (records.embeddings() != null) { map.put("embeddings", records.embeddings()); }
        if (records.metadatas() != null) { map.put("metadatas", records.metadatas()); }
        if (records.uris() != null) { map.put("uris", records.uris()); }
        return map;
    }
}
