package com.omchevli.docengine.repository;

import com.omchevli.docengine.model.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/** Default store: keeps documents in memory so the app runs with no external services. */
@Repository
@Profile("!mongo")
public class InMemoryDocumentRepository implements DocumentRepository {

    private final Map<String, Document> store = new ConcurrentHashMap<>();

    @Override
    public Document save(Document document) {
        store.put(document.getId(), document);
        return document;
    }

    @Override
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Document> findAccessibleBy(String username) {
        return store.values().stream()
                .filter(d -> d.canAccess(username))
                .toList();
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }
}
