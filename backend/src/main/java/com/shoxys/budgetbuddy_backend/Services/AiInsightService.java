package com.shoxys.budgetbuddy_backend.Services;

import com.shoxys.budgetbuddy_backend.DTOs.Dashboard.SpendingInsight;
import com.shoxys.budgetbuddy_backend.DTOs.Gemini.GeminiRequest;
import com.shoxys.budgetbuddy_backend.DTOs.Gemini.GeminiResponse;
import com.shoxys.budgetbuddy_backend.Utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Service for generating AI-based spending insights using the Gemini API.
 */
@Service
public class AiInsightService {
    private static final Logger logger = LoggerFactory.getLogger(AiInsightService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.model:gemini-2.0-flash}")
    private String model;

    /**
     * Fetches insights from the Gemini API based on a provided prompt.
     *
     * @param promptText the prompt text to send to the Gemini API
     * @return the extracted text response from the API
     * @throws IllegalArgumentException if promptText is null or empty
     * @throws IllegalStateException    if the API key is not configured
     * @throws RuntimeException         if the API call fails or returns null
     */
    public String getInsightsFromText(String promptText) {
        logger.debug("Fetching insights from Gemini API for prompt");
        if (Utils.nullOrEmpty(promptText)) {
            logger.error("Prompt text is null or empty");
            throw new IllegalArgumentException("Prompt text cannot be null or empty");
        }
        if (Utils.nullOrEmpty(apiKey)) {
            logger.error("Gemini API key is missing");
            throw new IllegalStateException("API key must be configured");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        GeminiRequest.Part part = new GeminiRequest.Part(promptText);
        GeminiRequest.Content content = new GeminiRequest.Content(List.of(part));
        GeminiRequest requestBody = new GeminiRequest(List.of(content));

        HttpEntity<GeminiRequest> requestEntity = new HttpEntity<>(requestBody, headers);
        logger.debug("Sending request to Gemini API: url={}, model={}", apiUrl, model);

        try {
            GeminiResponse response = restTemplate.postForObject(
                    apiUrl,
                    requestEntity,
                    GeminiResponse.class,
                    model
            );
            if (response != null) {
                String result = response.extractText();
                if (Utils.nullOrEmpty(result)) {
                    logger.warn("Received empty response from Gemini API");
                    throw new RuntimeException("Empty response from Gemini API");
                }
                logger.info("Received response from Gemini API: {}", result);
                return result;
            }
            logger.error("Received null response from Gemini API");
            throw new RuntimeException("No response from Gemini API");
        } catch (Exception e) {
            logger.error("Failed to call Gemini API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch insights from Gemini API", e);
        }
    }

    /**
     * Builds a strict prompt for the Gemini API based on transaction statistics.
     *
     * @param transactionStats the transaction statistics to include in the prompt
     * @return the formatted prompt string
     * @throws IllegalArgumentException if transactionStats is null or empty
     */
    public String buildStrictPrompt(String transactionStats) {
        logger.debug("Building strict prompt for transaction stats");
        if (Utils.nullOrEmpty(transactionStats)) {
            logger.error("Transaction stats is null or empty");
            throw new IllegalArgumentException("Transaction stats cannot be null or empty");
        }
        String prompt = """
                You are a financial analyst who provides very brief, actionable advice. \
                Based on the following spending summary, generate exactly TWO short, encouraging, and distinct insights. \
                Each insight should aim to be 2 sentences and MUST not exceed 3 sentences. \
                DO NOT use any introductory text, numbering, or bullet points. \
                Each insight must be on its own line.

                --- SUMMARY ---
                %s
                --- END SUMMARY ---
                """.formatted(transactionStats);
        logger.info("Built prompt for transaction stats");
        return prompt;
    }

    /**
     * Parses raw insights from the Gemini API into a list of SpendingInsight objects.
     *
     * @param rawInsights the raw insight text to parse
     * @return a list of SpendingInsight objects
     */
    public List<SpendingInsight> parseInsightsToList(String rawInsights) {
        logger.debug("Parsing insights: {}", rawInsights);
        if (Utils.nullOrEmpty(rawInsights)) {
            logger.warn("Raw insights are null or empty, returning static insights");
            return getStaticInsights();
        }
        List<SpendingInsight> insights = Arrays.stream(rawInsights.split("\n"))
                .filter(line -> !line.trim().isEmpty())
                .map(String::trim)
                .map(SpendingInsight::new)
                .toList();
        logger.info("Parsed {} insights", insights.size());
        return insights;
    }

    /**
     * Returns a list of static spending insights when no valid insights are available.
     *
     * @return a list of default SpendingInsight objects
     */
    private List<SpendingInsight> getStaticInsights() {
        logger.debug("Returning static insights");
        return List.of(
                new SpendingInsight("Regularly reviewing your spending is a great first step to finding savings!"),
                new SpendingInsight("Try setting a small, achievable savings goal to build momentum.")
        );
    }
}
