package com.example.spring_boot_mongodb_redis.service;

import com.example.spring_boot_mongodb_redis.config.SequenceGeneratorService;
import com.example.spring_boot_mongodb_redis.exception.UserNotFoundException;
import com.example.spring_boot_mongodb_redis.model.User;
import com.example.spring_boot_mongodb_redis.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository repository;
    private final SequenceGeneratorService sequenceGenerator;

    public UserService(UserRepository repository, SequenceGeneratorService sequenceGenerator) {
        this.repository = repository;
        this.sequenceGenerator = sequenceGenerator;
    }

    public List<User> getAll() {
        log.info("Fetching all users from database");
        return repository.findAll();
    }

    @Cacheable(value = "users", key = "#id", unless = "#result == null")
    public User getById(Long id) {
        log.info("Attempting to fetch user with ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    String message = "User not found with ID: " + id;
                    log.warn(message);
                    return new UserNotFoundException(message);
                });
    }

    public User create(User user) {
        log.info("Creating new user with data: {}", user);
        user.setId(sequenceGenerator.generateSequence("user_sequence"));
        return repository.save(user);
    }

    @CachePut(value = "users", key = "#id")
    public User update(Long id, User userDetails) {
        log.info("Updating user with ID: {}", id);
        User user = repository.findById(id)
                .orElseThrow(() -> {
                    String message = "User not found with ID: " + id;
                    log.warn(message);
                    return new UserNotFoundException(message);
                });

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setMaidenName(userDetails.getMaidenName());
        user.setGender(userDetails.getGender());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        user.setUsername(userDetails.getUsername());
        user.setPassword(userDetails.getPassword());
        user.setBirthDate(userDetails.getBirthDate());

        return repository.save(user);
    }

    @CacheEvict(value = "users", key = "#id")
    public void delete(Long id) {
        log.info("Deleting user with ID: {}", id);
        if (repository.existsById(id)) {
            repository.deleteById(id);
            log.info("User with ID {} deleted successfully", id);
        } else {
            String message = "User not found with ID: " + id;
            log.warn(message);
            throw new UserNotFoundException(message);
        }
    }
}
