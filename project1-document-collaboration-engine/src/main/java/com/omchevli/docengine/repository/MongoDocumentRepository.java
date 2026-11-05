package com.omchevli.docengine.repository;

import com.omchevli.docengine.model.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB-backed store, active only with the "mongo" profile
 * (spring.data.mongodb.uri / docker-compose service). Not loaded by default,
 * so the app and its tests need no running MongoDB.
 */
@Repository
@Profile("mongo")
public class MongoDocumentRepository implements DocumentRepository {

    private final MongoTemplate mongoTemplate;

    public MongoDocumentRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Document save(Document document) {
        return mongoTemplate.save(document);
    }

    @Override
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, Document.class));
    }

    @Override
    public List<Document> findAccessibleBy(String username) {
        Query query = new Query(new Criteria().orOperator(
                Criteria.where("owner").is(username),
                Criteria.where("sharedWith").is(username)));
        return mongoTemplate.find(query, Document.class);
    }

    @Override
    public void deleteById(String id) {
        mongoTemplate.remove(new Query(Criteria.where("id").is(id)), Document.class);
    }
}
