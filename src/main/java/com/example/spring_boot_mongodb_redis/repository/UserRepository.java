package com.example.spring_boot_mongodb_redis.repository;

import com.example.spring_boot_mongodb_redis.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, Long> {
}
