package com.example.spring_boot_mongodb_redis.config;

import com.example.spring_boot_mongodb_redis.model.DatabaseSequence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SequenceGeneratorService {

    private final MongoOperations mongoOperations;

    @Autowired
    public SequenceGeneratorService(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public long generateSequence(String seqName) {
        try {
            mongoOperations.insert(new DatabaseSequence(seqName, 100L));
            log.debug("Initialized sequence '{}' to 100", seqName);
        } catch (DuplicateKeyException ignored) {
            log.debug("Sequence '{}' already initialized, skipping seed", seqName);
        }

        DatabaseSequence counter = mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(seqName)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true),
                DatabaseSequence.class
        );

        if (counter == null) {
            log.warn("Counter for '{}' was null after upsert+inc; inserting fallback value 101", seqName);
            mongoOperations.insert(new DatabaseSequence(seqName, 101L));
            return 101L;
        }
        return counter.getSeq();
    }
}
