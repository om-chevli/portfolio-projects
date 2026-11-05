package com.omchevli.docengine.service;

import com.omchevli.docengine.model.Document;
import com.omchevli.docengine.repository.DocumentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository repository;
    private final SummaryService summaryService;

    public DocumentService(DocumentRepository repository, SummaryService summaryService) {
        this.repository = repository;
        this.summaryService = summaryService;
    }

    public Document create(String title, String content, String owner) {
        return repository.save(new Document(title, content, owner));
    }

    public Document get(String id, String username) {
        Document doc = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        if (!doc.canAccess(username)) {
            // 404 rather than 403: do not leak that the document exists.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found");
        }
        return doc;
    }

    public List<Document> listAccessible(String username) {
        return repository.findAccessibleBy(username);
    }

    public List<Document> search(String username, String keyword) {
        String needle = keyword.toLowerCase();
        return repository.findAccessibleBy(username).stream()
                .filter(d -> d.getTitle().toLowerCase().contains(needle)
                        || d.getContent().toLowerCase().contains(needle)
                        || d.getTags().stream().anyMatch(t -> t.toLowerCase().contains(needle)))
                .toList();
    }

    public Document update(String id, String title, String content, String username) {
        Document doc = get(id, username);
        if (!doc.canEdit(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No edit permission");
        }
        if (title != null) doc.setTitle(title);
        if (content != null) doc.setContent(content);
        doc.setUpdatedAt(Instant.now());
        doc.setVersion(doc.getVersion() + 1);
        return repository.save(doc);
    }

    public Document share(String id, String shareWith, String username) {
        Document doc = get(id, username);
        if (!doc.getOwner().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the owner can share");
        }
        doc.getSharedWith().add(shareWith);
        doc.setUpdatedAt(Instant.now());
        return repository.save(doc);
    }

    public void delete(String id, String username) {
        Document doc = get(id, username);
        if (!doc.getOwner().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the owner can delete");
        }
        repository.deleteById(id);
    }

    public String summarize(String id, String username, int maxSentences) {
        Document doc = get(id, username);
        return summaryService.summarize(doc.getContent(), maxSentences);
    }
}
