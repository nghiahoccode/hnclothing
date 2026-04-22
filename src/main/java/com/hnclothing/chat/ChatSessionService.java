package com.hnclothing.chat;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatSessionService {

    private final ConcurrentHashMap<String, List<String>> sessions = new ConcurrentHashMap<>();

    public void addMessage(String sessionId, String message) {
        sessions.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(message);
    }

    public List<String> getHistory(String sessionId) {
        return sessions.getOrDefault(sessionId, new ArrayList<>());
    }

    public String buildContext(String sessionId) {
        List<String> history = getHistory(sessionId);
        return String.join("\n", history);
    }
}