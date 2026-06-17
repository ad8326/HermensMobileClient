package com.hermens.backend.controller;

import com.hermens.backend.model.HermensProxyRequest;
import com.hermens.backend.model.HermensStatusResponse;
import com.hermens.backend.service.HermensAuthService;
import com.hermens.backend.service.HermensClientService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/hermens")
public class HermensProxyController {

    private final HermensClientService hermensClientService;
    private final HermensAuthService authService;

    public HermensProxyController(HermensClientService hermensClientService, HermensAuthService authService) {
        this.hermensClientService = hermensClientService;
        this.authService = authService;
    }

    @GetMapping("/status")
    public ResponseEntity<HermensStatusResponse> status(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        String localToken = extractToken(authorization);
        if (!authService.validateToken(localToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new HermensStatusResponse("error", "Unauthorized"));
        }

        String hermensToken = authService.getHermensToken(localToken);
        if (hermensToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new HermensStatusResponse("error", "Unauthorized"));
        }

        return ResponseEntity.ok(hermensClientService.requestStatus(hermensToken));
    }

    @PostMapping("/proxy")
    public ResponseEntity<Object> proxy(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                        @RequestBody HermensProxyRequest request) {
        String localToken = extractToken(authorization);
        if (!authService.validateToken(localToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "error", "message", "Unauthorized"));
        }

        String hermensToken = authService.getHermensToken(localToken);
        if (hermensToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "error", "message", "Unauthorized"));
        }

        Object result = hermensClientService.proxyRequest(request, hermensToken);
        return ResponseEntity.ok(result);
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null) {
            return null;
        }
        return authorizationHeader.replace("Bearer ", "").trim();
    }
}
