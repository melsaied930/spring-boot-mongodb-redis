package com.example.spring_boot_mongodb_redis.init;

import com.example.spring_boot_mongodb_redis.config.AppConfig;
import com.example.spring_boot_mongodb_redis.model.User;
import com.example.spring_boot_mongodb_redis.repository.UserRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class UserDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final AppConfig appConfig;
    private final ObjectMapper mapper;

    public UserDataInitializer(UserRepository userRepository, RestTemplate restTemplate, AppConfig appConfig) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("📦 Starting user data initialization...");

            List<User> users = loadUsersFromFile();
            if (users.isEmpty()) {
                users = fetchUsersFromApi();
                if (!users.isEmpty()) {
                    saveUsersToFile(users);
                }
            }

            if (!users.isEmpty()) {
                userRepository.saveAll(users);
                log.info("✅ Successfully initialized MongoDB with {} users", users.size());
            } else {
                log.warn("⚠️ No users were loaded from file or API.");
            }
        } else {
            log.info("✅ User data already present. Skipping initialization.");
        }
    }

    private List<User> loadUsersFromFile() {
        List<User> users = new ArrayList<>();
        try {
            File file = new File(appConfig.getUserDataFile());
            if (!file.exists()) {
                log.info("ℹ️ User data file not found: {}", file.getAbsolutePath());
                return users;
            }

            log.info("ℹ️ Loading users from file: {}", file.getAbsolutePath());
            User[] userArray = mapper.readValue(file, User[].class);
            users.addAll(Arrays.asList(userArray));
            log.info("✅ Loaded {} users from file", users.size());

        } catch (Exception e) {
            log.warn("⚠️ Failed to load users from file", e);
        }
        return users;
    }

    private List<User> fetchUsersFromApi() {
        List<User> users = new ArrayList<>();
        try {
            log.info("🌐 Fetching users from API: {}", appConfig.getApiUrl());
            String response = restTemplate.getForObject(appConfig.getApiUrl(), String.class);

            JsonNode root = mapper.readTree(response);
            JsonNode usersNode = root.path("users");

            for (JsonNode node : usersNode) {
                try {
                    User user = parseUserFromNode(node);
                    users.add(user);
                } catch (Exception e) {
                    log.warn("⚠️ Failed to parse user from API node: {}", node, e);
                }
            }

            log.info("✅ Fetched {} users from API", users.size());

        } catch (Exception e) {
            log.error("❌ Failed to fetch users from API", e);
        }
        return users;
    }

    private void saveUsersToFile(List<User> users) {
        try {
            File file = new File(appConfig.getUserDataFile());
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, users);
            log.info("💾 Saved {} users to file: {}", users.size(), file.getAbsolutePath());
        } catch (Exception e) {
            log.warn("⚠️ Failed to save users to file", e);
        }
    }

    private User parseUserFromNode(JsonNode node) {
        Long id = node.path("id").asLong();
        LocalDate birthDate = parseBirthDate(node.path("birthDate"));

        return User.builder()
                .id(id)
                .firstName(node.path("firstName").asText())
                .lastName(node.path("lastName").asText())
                .maidenName(node.path("maidenName").asText())
                .gender(node.path("gender").asText())
                .email(node.path("email").asText())
                .phone(node.path("phone").asText())
                .username(node.path("username").asText())
                .password(node.path("password").asText())
                .birthDate(birthDate)
                .build();
    }

    private LocalDate parseBirthDate(JsonNode dateNode) {
        try {
            if (dateNode.isTextual()) {
                String[] parts = dateNode.asText().split("-");
                if (parts.length == 3) {
                    String year = parts[0];
                    String month = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
                    String day = parts[2].length() == 1 ? "0" + parts[2] : parts[2];
                    return LocalDate.parse(String.format("%s-%s-%s", year, month, day));
                }
            } else if (dateNode.has("$date")) {
                String rawDate = dateNode.get("$date").asText();
                return LocalDate.parse(rawDate.substring(0, 10));
            }
        } catch (DateTimeParseException e) {
            log.warn("⚠️ Failed to parse birthDate: {}", dateNode);
        }
        return null;
    }
}
