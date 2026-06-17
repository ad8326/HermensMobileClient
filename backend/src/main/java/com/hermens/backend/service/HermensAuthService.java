package com.hermens.backend.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HermensAuthService {

    private static class Session {
        final String username;
        final String hermensToken;

        Session(String username, String hermensToken) {
            this.username = username;
            this.hermensToken = hermensToken;
        }
    }

    private final Map<String, Session> sessionStore = new ConcurrentHashMap<>();

    public String createSession(String username, String hermensToken) {
        String token = UUID.randomUUID().toString();
        sessionStore.put(token, new Session(username, hermensToken));
        return token;
    }

    public boolean validateToken(String token) {
        return token != null && sessionStore.containsKey(token);
    }

    public String getHermensToken(String token) {
        Session session = sessionStore.get(token);
        return session == null ? null : session.hermensToken;
    }

    public String getUsername(String token) {
        Session session = sessionStore.get(token);
        return session == null ? null : session.username;
    }
}
