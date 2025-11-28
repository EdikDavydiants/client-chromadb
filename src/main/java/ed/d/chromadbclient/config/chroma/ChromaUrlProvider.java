package ed.d.chromadbclient.config.chroma;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChromaUrlProvider {

    private final ChromaDbProperties dbProperties;

    private String baseUrl() {
        return "http://" + dbProperties.getAddress() + ":" + dbProperties.getPort() + dbProperties.getApiPrefix();
    }

    public String healthCheckUrl() {
        return baseUrl() + "/healthcheck";
    }

    public String createTenantUrl() {
        return baseUrl() + "/tenants";
    }

    public String getTenantUrl(String tenantName) {
        return baseUrl() + "/tenants/" + tenantName;
    }

    private String databasesUrlPart(String tenantName) {
        return baseUrl() + "/tenants/" + tenantName + "/databases";
    }

    public String getDatabasesUrl(String tenantName) {
        return databasesUrlPart(tenantName);
    }

    public String createDatabaseUrl(String tenantName) {
        return databasesUrlPart(tenantName);
    }

    public String getDatabaseUrl(String tenantName, String databaseName) {
        return databasesUrlPart(tenantName) + "/" + databaseName;
    }

    public String deleteDatabaseUrl(String tenantName, String databaseName) {
        return databasesUrlPart(tenantName) + "/" + databaseName;
    }

    private String collectionsUrlPart(String tenantName, String databaseName) {
        return databasesUrlPart(tenantName) + "/" + databaseName + "/collections";
    }

    public String getCollectionsUrl(String tenantName, String databaseName) {
        return collectionsUrlPart(tenantName, databaseName);
    }

    public String createCollectionUrl(String tenantName, String databaseName) {
        return collectionsUrlPart(tenantName, databaseName);
    }

    private String collectionUrlPart(String tenantName, String databaseName, String collectionId) {
        return collectionsUrlPart(tenantName, databaseName) + "/" + collectionId;
    }

    public String getCollectionUrl(String tenantName, String databaseName, String collectionId) {
        return collectionUrlPart(tenantName, databaseName, collectionId);
    }

    public String updateCollectionUrl(String tenantName, String databaseName, String collectionId) {
        return collectionUrlPart(tenantName, databaseName, collectionId);
    }

    public String deleteCollectionUrl(String tenantName, String databaseName, String collectionId) {
        return collectionUrlPart(tenantName, databaseName, collectionId);
    }

    public String addRecordsUrl(String tenantName, String databaseName, String collectionId) {
        return collectionUrlPart(tenantName, databaseName, collectionId) + "/add";
    }

    public String countRecordsUrl(String tenantName, String databaseName, String collectionId) {
        return collectionUrlPart(tenantName, databaseName, collectionId) + "/count";
    }

    public String deleteRecordsUrl(String tenantName, String databaseName, String collectionId) {
        return collectionUrlPart(tenantName, databaseName, collectionId) + "/delete";
    }

    public String getRecordsUrl(String tenantName, String databaseName, String collectionId) {
        return collectionUrlPart(tenantName, databaseName, collectionId) + "/get";
    }

    public String queryRecordsUrl(String tenantName, String databaseName, String collectionId) {
        return collectionUrlPart(tenantName, databaseName, collectionId) + "/query";
    }
}
