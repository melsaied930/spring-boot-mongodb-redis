package com.example.spring_boot_mongodb_redis.config;

import com.example.spring_boot_mongodb_redis.model.DatabaseSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SequenceGeneratorService {

    private final MongoOperations mongoOperations;

    @Autowired
    public SequenceGeneratorService(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public long generateSequence(String seqName) {
        DatabaseSequence counter = mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(seqName)),
                // If this is a new document, initialize seq to 100, then increment by 1 → first value is 101
                new Update()
                        .setOnInsert("seq", 100)
                        .inc("seq", 1),
                FindAndModifyOptions.options()
                        .returnNew(true)
                        .upsert(true),
                DatabaseSequence.class
        );
        // Fallback just in case (though with upsert(true) counter should never be null)
        return !Objects.isNull(counter) ? counter.getSeq() : 101;
    }
}
