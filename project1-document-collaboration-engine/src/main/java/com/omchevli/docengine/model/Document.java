package com.omchevli.docengine.model;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A document owned by one user and optionally shared with others.
 * Plain class (no persistence annotations) so the same model works with the
 * in-memory store and the MongoDB-backed repository.
 */
public class Document {

    @org.springframework.data.annotation.Id
    private String id;
    private String title;
    private String content;
    private String owner;
    private Set<String> sharedWith = new LinkedHashSet<>();
    private Set<String> tags = new LinkedHashSet<>();
    private Instant createdAt;
    private Instant updatedAt;
    private long version;

    public Document() {
    }

    public Document(String title, String content, String owner) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.owner = owner;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.version = 1;
    }

    public boolean canAccess(String username) {
        return owner.equals(username) || sharedWith.contains(username);
    }

    public boolean canEdit(String username) {
        return owner.equals(username) || sharedWith.contains(username);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public Set<String> getSharedWith() { return sharedWith; }
    public void setSharedWith(Set<String> sharedWith) { this.sharedWith = sharedWith; }
    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public long getVersion() { return version; }
    public void setVersion(long version) { this.version = version; }
}
