package ed.d.chromadbclient.wrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import ed.d.chromadbclient.exceptions.ChromaConflictException;
import ed.d.chromadbclient.exceptions.ChromaException;
import ed.d.chromadbclient.exceptions.ChromaYamlParsingException;
import ed.d.chromadbclient.exceptions.HttpClientException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import static ed.d.chromadbclient.util.ExceptionMessages.CHROMA_CONTENT_ALREADY_EXIST;
import static ed.d.chromadbclient.util.ExceptionMessages.CHROMA_INTERNAL_ERROR;
import static ed.d.chromadbclient.util.ExceptionMessages.CHROMA_NOT_FOUND_CONTENT;
import static ed.d.chromadbclient.util.ExceptionMessages.CHROMA_UNAUTHORIZED_REQUEST;
import static ed.d.chromadbclient.util.ExceptionMessages.CHROMA_UNKNOWN_ERROR;
import static ed.d.chromadbclient.util.ExceptionMessages.HTTP_REQUEST_FAILED;
import static ed.d.chromadbclient.util.ExceptionMessages.JSON_TO_OBJECT_FAILED;
import static ed.d.chromadbclient.util.ExceptionMessages.OBJECT_TO_JSON_FAILED;

@Service
public class HttpClientWrapper {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpClientWrapper() {
        this.httpClient = HttpClient
                .newBuilder()
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public HttpResponse<String> send(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new HttpClientException(HTTP_REQUEST_FAILED);
        }
    }

    public void post(String url, Object body) {
        String jsonBody = toJson(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = send(request);

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return;
        } else if(response.statusCode() == HttpURLConnection.HTTP_CONFLICT) {
            throw new ChromaConflictException(CHROMA_CONTENT_ALREADY_EXIST);
        } else {
            processCommonProblems(response);
        }
    }

    public <T> T postWithResponse(String url, Object body, Class<T> responseType) {
        String jsonBody = toJson(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = send(request);

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return toObject(response.body(), responseType);
        } else if(response.statusCode() == HttpURLConnection.HTTP_CONFLICT) {
            throw new ChromaConflictException(CHROMA_CONTENT_ALREADY_EXIST);
        } else {
            return processCommonProblems(response);
        }
    }

    public <T> Optional<T> get(String url, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = send(request);

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return Optional.ofNullable(toObject(response.body(), responseType));
        } else if(response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            return Optional.empty();
        } else {
            return processCommonProblems(response);
        }
    }

    public <T> List<T> getList(String url, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = send(request);

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return toObjectList(response.body(), responseType);
        } else {
            return processCommonProblems(response);
        }
    }

    public void delete(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> response = send(request);

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return;
        } else if(response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new ChromaConflictException(CHROMA_NOT_FOUND_CONTENT);
        } else {
            processCommonProblems(response);
        }
    }

    private <T> T processCommonProblems(HttpResponse<String> response) {
        if(response.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new ChromaException(CHROMA_UNAUTHORIZED_REQUEST);
        } else if (response.statusCode() >= 500 && response.statusCode() < 600) {
            throw new ChromaException(CHROMA_INTERNAL_ERROR);
        } else {
            throw new ChromaException(CHROMA_UNKNOWN_ERROR);
        }
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ChromaYamlParsingException(OBJECT_TO_JSON_FAILED);
        }
    }

    private <T> T toObject(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        }
        catch (JsonProcessingException e) {
            throw new ChromaYamlParsingException(JSON_TO_OBJECT_FAILED);
        }
    }

    private <T> List<T> toObjectList(String json, Class<T> elementType) {
        try {
            return objectMapper.readValue(
                    json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementType));
        }
        catch (JsonProcessingException e) {
            throw new ChromaYamlParsingException(JSON_TO_OBJECT_FAILED);
        }
    }
}
