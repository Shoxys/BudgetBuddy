package com.shoxys.budgetbuddy_backend.DTOs.Gemini;

import java.util.List;

/**
 * Request object for sending content to the Gemini API.
 */
public record GeminiRequest(List<Content> contents) {
    /**
     * Represents the content structure containing a list of parts for the Gemini API request.
     */
    public static record Content(List<Part> parts) {
    }


    /**
     * Represents a single part of content with text data for the Gemini API request.
     */
    public static record Part(String text) {
    }
}