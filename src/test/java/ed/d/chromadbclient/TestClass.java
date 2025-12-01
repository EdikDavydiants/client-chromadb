package ed.d.chromadbclient;

import ed.d.chromadbclient.repository.querybuilders.GetBuilder;
import ed.d.chromadbclient.repository.querybuilders.Inclusions;
import ed.d.chromadbclient.repository.querybuilders.QueryBuilder;
import ed.d.chromadbclient.repository.querybuilders.conditions.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ed.d.chromadbclient.config.ChromaDbAutoConfiguration;
import ed.d.chromadbclient.repository.RecordRepository;
import ed.d.chromadbclient.repository.dto.entities.RecordsDto;

import java.util.List;

@SpringBootTest(classes = {ChromaDbAutoConfiguration.class })
public class TestClass {

    @Autowired
    RecordRepository recordRepository;

    @BeforeEach
    void init() {
    }

    @Test
    void test() {
        GetBuilder getBuilder = QueryBuilder.getBuilder()
                .docFilter(
                        Condition.and(
                                Condition.contains("doc")
                                //RecordRepository.GetBuilder.Condition.notContains("cum")
                        )
                )
                .metadataFilter(
                        Condition.and(
                                Condition.metaCond("meta2")
                                        .in("data1", "data2"),
                                Condition.metaCond("meta3")
                                        .in("data1", "data3")
                        )
                )
                .include(
                        Inclusions.list()
                                .distances()
                )
                .ids(
                        List.of("record_id91")
                );

        RecordsDto response = recordRepository.getRecords(
                "tenant91", "database91", "collection91", getBuilder);
        System.out.println(response);
    }
}
