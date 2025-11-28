package ed.d.chromadbclient.repository.dto.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public record CollectionDto(

        String id,

        String name,

        String tenant,

        String database,

        Integer dimension,

        @JsonProperty("log_position")
        Long logPosition,

        Integer version,

        @JsonProperty("configuration_json")
        Configuration configuration,

        JsonNode metadata
) {

        public record Configuration(
                Hnsw hnsw,
                Spann spann,
                @JsonProperty("embedding_function") EmbeddingFunction embeddingFunction
        ) {
                public record Hnsw(
                        String space,
                        @JsonProperty("ef_construction") Integer efConstruction,
                        @JsonProperty("ef_search") Integer efSearch,
                        @JsonProperty("max_neighbors") Long maxNeighbors,
                        @JsonProperty("resize_factor") Double resizeFactor,
                        @JsonProperty("sync_threshold") Integer syncThreshold
                ) {}

                public record Spann(
                        @JsonProperty("num_candidates") Integer numCandidates,
                        @JsonProperty("search_range") Integer searchRange
                ) {}

                public record EmbeddingFunction(
                        String type,
                        String name,
                        Config config
                ) {
                        public record Config(
                                @JsonProperty("batch_size") Integer batchSize,
                                String model
                        ) {}
                }
        }
}
