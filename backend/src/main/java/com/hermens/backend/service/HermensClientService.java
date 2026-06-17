package com.hermens.backend.service;

import com.hermens.backend.model.HermensLoginRequest;
import com.hermens.backend.model.HermensProxyRequest;
import com.hermens.backend.model.HermensRemoteLoginResponse;
import com.hermens.backend.model.HermensStatusResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
public class HermensClientService {

    private final RestTemplate restTemplate;
    private final String authPath;

    public HermensClientService(@Value("${hermens.api.url}") String baseUrl,
                                @Value("${hermens.auth.path}") String authPath) {
        this.restTemplate = new RestTemplate();
        this.restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));
        this.authPath = authPath;
    }

    public HermensRemoteLoginResponse login(HermensLoginRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<HermensLoginRequest> entity = new HttpEntity<>(request, headers);
            return restTemplate.postForObject(authPath, entity, HermensRemoteLoginResponse.class);
        } catch (Exception ex) {
            return new HermensRemoteLoginResponse("error", ex.getMessage(), null);
        }
    }

    public HermensStatusResponse requestStatus(String hermensToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(hermensToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<HermensStatusResponse> response = restTemplate.exchange("/status", HttpMethod.GET, entity, HermensStatusResponse.class);
            return response.getBody();
        } catch (Exception ex) {
            return new HermensStatusResponse("error", ex.getMessage());
        }
    }

    public Object proxyRequest(HermensProxyRequest request, String hermensToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(hermensToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<>(request.getPayload(), headers);
            return restTemplate.postForObject(request.getPath(), entity, Object.class);
        } catch (Exception ex) {
            return new HermensStatusResponse("error", ex.getMessage());
        }
    }
}
