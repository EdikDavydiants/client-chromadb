package ed.d.chromadbclient.repository;

import ed.d.chromadbclient.repository.querybuilders.GetBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ed.d.chromadbclient.config.chroma.ChromaUrlProvider;
import ed.d.chromadbclient.mapper.RecordMapper;
import ed.d.chromadbclient.repository.dto.entities.RecordsDto;
import ed.d.chromadbclient.wrapper.HttpClientWrapper;

@Repository
@RequiredArgsConstructor
public class RecordRepository extends ChromaRepository {

    private final ChromaUrlProvider urlProvider;
    private final HttpClientWrapper httpClient;
    private final RecordMapper recordMapper;
    private final CollectionRepository collectionRepository;

    public void addRecords(String tenantName, String databaseName, String collectionName, RecordsDto records) {
        String collectionId = collectionRepository.takeCollectionIdByName(tenantName, databaseName, collectionName);
        httpClient.post(
                urlProvider.addRecordsUrl(tenantName, databaseName, collectionId),
                recordMapper.mapRecordsDtoToMap(records)
        );
    }

    public RecordsDto getRecords(String tenantName, String databaseName, String collectionName, GetBuilder getBuilder) {
        String collectionId = collectionRepository.takeCollectionIdByName(tenantName, databaseName, collectionName);
        return httpClient.postWithResponse(
                urlProvider.getRecordsUrl(tenantName, databaseName, collectionId),
                getBuilder.toMap(),
                RecordsDto.class
        );
    }
}
