package com.omchevli.docengine.controller;

import com.omchevli.docengine.model.Document;
import com.omchevli.docengine.service.DocumentService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    public record CreateRequest(@NotBlank String title, @NotBlank String content) {}
    public record UpdateRequest(String title, String content) {}
    public record ShareRequest(@NotBlank String username) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Document create(@RequestBody CreateRequest req, Authentication auth) {
        return service.create(req.title(), req.content(), auth.getName());
    }

    @GetMapping
    public List<Document> list(Authentication auth) {
        return service.listAccessible(auth.getName());
    }

    @GetMapping("/search")
    public List<Document> search(@RequestParam String q, Authentication auth) {
        return service.search(auth.getName(), q);
    }

    @GetMapping("/{id}")
    public Document get(@PathVariable String id, Authentication auth) {
        return service.get(id, auth.getName());
    }

    @PutMapping("/{id}")
    public Document update(@PathVariable String id, @RequestBody UpdateRequest req, Authentication auth) {
        return service.update(id, req.title(), req.content(), auth.getName());
    }

    @PostMapping("/{id}/share")
    public Document share(@PathVariable String id, @RequestBody ShareRequest req, Authentication auth) {
        return service.share(id, req.username(), auth.getName());
    }

    @GetMapping("/{id}/summary")
    public Map<String, String> summary(@PathVariable String id,
                                       @RequestParam(defaultValue = "3") int sentences,
                                       Authentication auth) {
        return Map.of("summary", service.summarize(id, auth.getName(), sentences));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id, Authentication auth) {
        service.delete(id, auth.getName());
    }
}
