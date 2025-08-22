package com.foodanalyzer.backend.service;

import com.foodanalyzer.backend.entity.Food;
import com.foodanalyzer.backend.entity.User;
import com.foodanalyzer.backend.entity.UserUsage;
import com.foodanalyzer.backend.model.FoodResponse;
import com.foodanalyzer.backend.repository.FoodRepository;
import com.foodanalyzer.backend.repository.UserRepository;
import com.foodanalyzer.backend.repository.UserUsageRepository;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class FoodService {
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserUsageRepository userUsageRepository;
    @Value("${gemini.api.key}")
    private String geminiKey;
    @Value("${pixabay.api.key}")
    private String pixabayKey;

    @Value("${unlimited.emails}")
    private String unlimitedEmailsRaw;

    private Set<String> getUnlimitedEmails() {
        if (unlimitedEmailsRaw == null || unlimitedEmailsRaw.isBlank()) return Set.of();
        return Arrays.stream(unlimitedEmailsRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    public Food searchFood(String name, String username) {
        Optional<Food> cached = foodRepository.findByNameIgnoreCase(name);
        if (cached.isPresent()) {
            Food food = cached.get();
            if (food.getImageUrl() == null || food.getImageUrlExpiresAt() == null || food.getImageUrlExpiresAt().isBefore(LocalDateTime.now())) {
                refreshImage(food);
                foodRepository.save(food);
            }
            return food;
        }

        // Check limits
        User user = username != null ? userRepository.findByEmail(username).orElse(null) : null;
        if (user == null) {
            throw new RuntimeException("Please login in to use the tool.");
        }
        if (!checkDailyLimit(user)) {
            throw new RuntimeException("Daily limit exceeded. This is a demo for portfolio purposes.");
        }

        // Gemini API
        Food food = new Food();
        food.setName(name);
        try {
            Client client = Client.builder().apiKey(geminiKey).build();
            String prompt = "Is \"" + name + "\" a food dish or edible? Answer only in JSON with keys: isFood (true/false), info (short description), ingredients (list), benefits (list). Keep response concise.";
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    prompt,
                    null
            );
            String content = response.text();
            FoodResponse foodResponse;
            ObjectMapper mapper = new ObjectMapper();
            try {
                String jsonText = content.replace("```json\n", "").replace("\n```", "");
                foodResponse = mapper.readValue(jsonText, FoodResponse.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse Gemini response: " + e.getMessage());
            }

            if (!foodResponse.isFood()) {
                throw new RuntimeException(name + " is not a recognized food dish.");
            }
            food.setIsFood(foodResponse.isFood());
            food.setDescription(foodResponse.getInfo());
            food.setIngredients(String.join(", ", foodResponse.getIngredients()));
            food.setBenefits(String.join(", ", foodResponse.getBenefits()));

            refreshImage(food);
            foodRepository.save(food);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("API failure. Demo limit exhausted.");
        }

        // User history
        updateUsage(user);

        return food;
    }

    private void refreshImage(Food food) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String pixabayUrl = "https://pixabay.com/api/?key=" + pixabayKey + "&q=" + food.getName() + "&lang=en&safesearch=true&per_page=3&category=food";
        String imageResponse = rest.exchange(pixabayUrl, HttpMethod.GET, entity, String.class).getBody();
        try {
            ObjectMapper mapper1 = new ObjectMapper();
            JsonNode json = mapper1.readTree(imageResponse);
            JsonNode hits = json.get("hits");
            if (hits != null && hits.size() > 0) {
                food.setImageUrl(hits.get(0).get("webformatURL").asText());
            } else {
                food.setImageUrl(null);
            }
            // set expiration to 24 hours from now
            food.setImageUrlExpiresAt(LocalDateTime.now().plusHours(24));
        } catch (Exception e) {
            food.setImageUrl(null);
            food.setImageUrlExpiresAt(null);
        }
    }

    private boolean checkDailyLimit(User user) {
        // Bypass limit for whitelisted emails from env
        String email = user.getEmail();
        if (email != null && getUnlimitedEmails().contains(email.toLowerCase())) {
            return true;
        }

        LocalDate today = LocalDate.now();
        UserUsage usage = userUsageRepository.findByUserAndDate(user, today)
                .orElse(null);

        if (usage == null) {
            usage = new UserUsage();
            usage.setUser(user);
            usage.setDate(today);
            usage.setCount(0);
            userUsageRepository.save(usage);
            return true;
        }

        return usage.getCount() < 10;
    }

    private void updateUsage(User user) {
        if (user == null) return;

        LocalDate today = LocalDate.now();
        UserUsage usage = userUsageRepository.findByUserAndDate(user, today)
                .orElse(null);

        if (usage == null) {
            usage = new UserUsage();
            usage.setUser(user);
            usage.setDate(today);
            usage.setCount(1);
        } else {
            usage.setCount(usage.getCount() + 1);
        }

        userUsageRepository.save(usage);
    }
}