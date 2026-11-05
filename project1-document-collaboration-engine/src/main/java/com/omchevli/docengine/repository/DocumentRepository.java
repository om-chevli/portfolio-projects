package com.omchevli.docengine.repository;

import com.omchevli.docengine.model.Document;

import java.util.List;
import java.util.Optional;

/** Storage abstraction for documents. Two implementations ship with the project:
 *  an in-memory store (default, zero setup) and a MongoDB store (profile "mongo"). */
public interface DocumentRepository {
    Document save(Document document);
    Optional<Document> findById(String id);
    List<Document> findAccessibleBy(String username);
    void deleteById(String id);
}
