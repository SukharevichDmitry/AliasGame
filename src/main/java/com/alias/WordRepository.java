package com.alias;

import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class WordRepository {
    private List<String> words;

    public WordRepository() {
        loadWords();
    }

    private void loadWords() {
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream("/words.json"), StandardCharsets.UTF_8)) {
            words = new Gson().fromJson(reader, List.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load words!", e);
        }
    }

    public String getRandomWord() {
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }
}
