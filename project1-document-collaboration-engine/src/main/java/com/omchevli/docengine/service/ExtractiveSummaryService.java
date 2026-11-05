package com.omchevli.docengine.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Offline extractive summarizer: scores each sentence by the frequency of the
 * words it contains (classic Luhn-style scoring) and returns the top-scoring
 * sentences in their original order.
 */
@Service
public class ExtractiveSummaryService implements SummaryService {

    @Override
    public String summarize(String content, int maxSentences) {
        if (content == null || content.isBlank()) {
            return "";
        }
        List<String> sentences = Arrays.stream(content.split("(?<=[.!?])\\s+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        if (sentences.size() <= maxSentences) {
            return String.join(" ", sentences);
        }

        Map<String, Integer> wordFreq = new HashMap<>();
        for (String sentence : sentences) {
            for (String word : words(sentence)) {
                wordFreq.merge(word, 1, Integer::sum);
            }
        }

        List<Integer> topIndexes = IntStream.range(0, sentences.size())
                .boxed()
                .sorted(Comparator.comparingDouble(
                        (Integer i) -> score(sentences.get(i), wordFreq)).reversed())
                .limit(maxSentences)
                .sorted()
                .toList();

        return topIndexes.stream()
                .map(sentences::get)
                .collect(Collectors.joining(" "));
    }

    private double score(String sentence, Map<String, Integer> wordFreq) {
        List<String> ws = words(sentence);
        if (ws.isEmpty()) {
            return 0;
        }
        return ws.stream().mapToInt(w -> wordFreq.getOrDefault(w, 0)).sum() / (double) ws.size();
    }

    private List<String> words(String text) {
        return Arrays.stream(text.toLowerCase().split("[^a-z0-9]+"))
                .filter(w -> w.length() > 2)
                .toList();
    }
}
