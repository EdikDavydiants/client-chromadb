package ed.d.chromadbclient.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan("ed.d.chromadbclient")
@ConditionalOnProperty(name = "spring.chroma-db.enabled", havingValue = "true")
public class ChromaDbAutoConfiguration {
}
