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
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UserDataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ResourceLoader resourceLoader;
    private final RestTemplate restTemplate;
    private final AppConfig appConfig;

    public UserDataInitializer(UserRepository userRepository,
                               ResourceLoader resourceLoader,
                               RestTemplate restTemplate,
                               AppConfig appConfig) {
        this.userRepository = userRepository;
        this.resourceLoader = resourceLoader;
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("📦 Starting user data initialization...");
            List<User> users = loadUsersFromFile();
            if (users.isEmpty()) {
                users = fetchUsersFromApi();
            }
            if (!users.isEmpty()) {
                userRepository.saveAll(users);
                log.info("✅ Successfully initialized MongoDB with {} users", users.size());
            } else {
                log.warn("⚠️ No users were loaded from either source");
            }
        } else {
            log.info("✅ User data already present. Skipping initialization.");
        }
    }

    private List<User> loadUsersFromFile() {
        List<User> users = new ArrayList<>();
        try {
            Resource resource = resourceLoader.getResource(appConfig.getUserDataFile());
            if (!resource.exists()) {
                log.info("ℹ️ {} not found in classpath", appConfig.getUserDataFile());
                return users;
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        JsonNode node = mapper.readTree(line);
                        User user = parseUserFromJsonNode(node);
                        users.add(user);
                    } catch (Exception e) {
                        log.warn("⚠️ Failed to parse user: {}", line, e);
                    }
                }
            }
            log.info("ℹ️ Loaded {} users from {}", users.size(), appConfig.getUserDataFile());
        } catch (Exception e) {
            log.warn("⚠️ Failed to load users from file", e);
        }
        return users;
    }

    private List<User> fetchUsersFromApi() {
        List<User> users = new ArrayList<>();
        try {
            log.info("ℹ️ Fetching users from API: {}", appConfig.getApiUrl());
            String response = restTemplate.getForObject(appConfig.getApiUrl(), String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode usersNode = root.path("users");

            for (JsonNode node : usersNode) {
                try {
                    User user = parseUserFromApiNode(node);
                    users.add(user);
                } catch (Exception e) {
                    log.warn("⚠️ Failed to parse user from API: {}", node, e);
                }
            }
            log.info("ℹ️ Fetched {} users from API", users.size());
        } catch (Exception e) {
            log.error("❌ Failed to fetch users from API", e);
        }
        return users;
    }

    private User parseUserFromJsonNode(JsonNode node) {
        Long id = node.path("id").asLong();
        LocalDate birthDate = null;
        JsonNode dateNode = node.path("birthDate");
        if (!dateNode.isMissingNode()) {
            if (dateNode.has("$date")) {
                String dateStr = dateNode.path("$date").asText();
                birthDate = LocalDate.parse(dateStr.substring(0, 10));
            } else {
                birthDate = LocalDate.parse(dateNode.asText());
            }
        }

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

    private User parseUserFromApiNode(JsonNode node) {
        Long id = node.path("id").asLong();
        String rawDate = node.path("birthDate").asText();
        LocalDate birthDate = null;

        try {
            // Handle both formats: "1996-5-30" and "1996-05-30"
            String[] dateParts = rawDate.split("-");
            if (dateParts.length == 3) {
                String year = dateParts[0];
                String month = dateParts[1].length() == 1 ? "0" + dateParts[1] : dateParts[1];
                String day = dateParts[2].length() == 1 ? "0" + dateParts[2] : dateParts[2];
                birthDate = LocalDate.parse(String.format("%s-%s-%s", year, month, day));
            }
        } catch (DateTimeParseException e) {
            log.warn("⚠️ Could not parse birthDate: {}", rawDate);
        }

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
}