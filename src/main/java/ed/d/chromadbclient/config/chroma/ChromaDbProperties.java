package ed.d.chromadbclient.config.chroma;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.chroma-db")
public class ChromaDbProperties {

    private boolean enabled = false;
    private String address = "localhost";
    private String port = "8000";
    private String apiPrefix = "/api/v2";
    private String configFile = "chroma-db/structure.yaml";
}
