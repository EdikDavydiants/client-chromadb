package ed.d.chromadbclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ed.d.chromadbclient.config.ChromaDbAutoConfiguration;
import ed.d.chromadbclient.repository.RecordRepository;
import ed.d.chromadbclient.repository.dto.entities.RecordsDto;

//@SpringBootTest(classes = {ChromaDbAutoConfiguration.class })
public class TestClass {

    //@Autowired
    RecordRepository recordRepository;

    @BeforeEach
    void init() {
    }

    @Test
    void test() {
//        var getBuilder = RecordRepository.GetBuilder.builder()
//                .contains("doc")
//                .metadataFilter(
//                        RecordRepository.GetBuilder.Condition.and(
//                                RecordRepository.GetBuilder.Condition.fieldCond("meta2")
//                                        .in("data1", "data2"),
//                                RecordRepository.GetBuilder.Condition.fieldCond("meta3")
//                                        .in("data1", "data3")
//                        )
//                );
//
//        RecordsDto response = recordRepository.getRecords(
//                "tenant91", "database91", "collection91", getBuilder);
//        System.out.println(response);
    }
}
