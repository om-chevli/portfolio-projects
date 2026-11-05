package com.omchevli.docengine;

import com.omchevli.docengine.model.Document;
import com.omchevli.docengine.repository.InMemoryDocumentRepository;
import com.omchevli.docengine.service.DocumentService;
import com.omchevli.docengine.service.ExtractiveSummaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentServiceTest {

    private DocumentService service;

    @BeforeEach
    void setUp() {
        service = new DocumentService(new InMemoryDocumentRepository(), new ExtractiveSummaryService());
    }

    @Test
    void ownerCanReadOwnDocument() {
        Document doc = service.create("Plan", "Some content here.", "om");
        assertEquals("Plan", service.get(doc.getId(), "om").getTitle());
    }

    @Test
    void strangerCannotReadUnsharedDocument() {
        Document doc = service.create("Secret", "Hidden content.", "om");
        assertThrows(ResponseStatusException.class, () -> service.get(doc.getId(), "guest"));
    }

    @Test
    void sharedUserCanReadButNotReshare() {
        Document doc = service.create("Shared", "Team content.", "om");
        service.share(doc.getId(), "guest", "om");
        assertEquals("Shared", service.get(doc.getId(), "guest").getTitle());
        assertThrows(ResponseStatusException.class, () -> service.share(doc.getId(), "mallory", "guest"));
    }

    @Test
    void updateBumpsVersion() {
        Document doc = service.create("Draft", "v1 content.", "om");
        Document updated = service.update(doc.getId(), "Draft 2", "v2 content.", "om");
        assertEquals(2, updated.getVersion());
        assertEquals("Draft 2", updated.getTitle());
    }

    @Test
    void searchFindsByKeyword() {
        service.create("Release notes", "The websocket sync shipped.", "om");
        service.create("Shopping", "Eggs and milk.", "om");
        assertEquals(1, service.search("om", "websocket").size());
        assertTrue(service.search("guest", "websocket").isEmpty());
    }

    @Test
    void summaryIsShorterThanContent() {
        String content = "Sync keeps every client up to date. The server broadcasts each change. "
                + "Clients apply updates in order. Conflicts are resolved by version number. "
                + "Sync is the core of the product. Offline edits are queued and replayed.";
        Document doc = service.create("Sync design", content, "om");
        String summary = service.summarize(doc.getId(), "om", 2);
        assertTrue(summary.length() < content.length());
        assertTrue(summary.toLowerCase().contains("sync"));
    }
}
