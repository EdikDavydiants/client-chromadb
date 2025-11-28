package ed.d.chromadbclient.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("ed.d.chromadbclient")
@ConditionalOnProperty(name = "spring.chroma-db.enabled", havingValue = "true")
public class ChromaDbAutoConfiguration {
}
