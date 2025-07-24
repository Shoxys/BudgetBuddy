package com.shoxys.budgetbuddy_backend.DTOs.Gemini;

import java.util.List;

/**
 * Response object mapping the JSON structure returned by the Gemini API.
 */
public record GeminiResponse(List<Candidate> candidates) {
    /**
     * Represents a candidate response from the Gemini API containing content.
     */
    public static record Candidate(Content content) {
    }

    /**
     * Represents the content structure in a Gemini API response, including parts and role.
     */
    public static record Content(List<Part> parts, String role) {
    }

    /**
     * Represents a single part of content with text data in the Gemini API response.
     */
    public static record Part(String text) {}

    /**
     * Extracts the text from the first part of the first candidate's content.
     *
     * @return the text string if available, or null if no candidates or parts exist
     */
    public String extractText() {
        if (candidates != null && !candidates.isEmpty()) {
            Candidate firstCandidate = candidates.getFirst();
            if (firstCandidate.content() != null && firstCandidate.content().parts() != null && !firstCandidate.content().parts().isEmpty()) {
                return firstCandidate.content().parts().getFirst().text();
            }
        }
        return null;
    }
}
