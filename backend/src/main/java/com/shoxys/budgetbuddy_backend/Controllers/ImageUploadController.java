package com.shoxys.budgetbuddy_backend.Controllers;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import com.shoxys.budgetbuddy_backend.Security.AppUserDetails;
import com.shoxys.budgetbuddy_backend.Services.SavingGoalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Handles HTTP requests for uploading images related to saving goals. */
@RestController
@RequestMapping(Constants.UPLOAD_ENDPOINT)
public class ImageUploadController {
  private static final Logger logger = LoggerFactory.getLogger(ImageUploadController.class);
  private final SavingGoalService savingGoalService;

  public ImageUploadController(SavingGoalService savingGoalService) {
    this.savingGoalService = savingGoalService;
  }

  /**
   * Uploads an image for a user's saving goal.
   *
   * @param userDetails the authenticated user's details
   * @param file the image file to upload
   * @return the image reference or an error response
   */
  @PostMapping("/image")
  public ResponseEntity<String> uploadImage(
      @AuthenticationPrincipal AppUserDetails userDetails,
      @RequestParam("file") MultipartFile file) {
    if (userDetails == null) {
      logger.warn("Unauthorized image upload attempt");
      return ResponseEntity.status(401).body("User is not authenticated");
    }
    String username = userDetails.getUsername();
    logger.info("Uploading image for user: {}, file: {}", username, file.getOriginalFilename());
    try {
      String imageRef = savingGoalService.uploadImage(username, file);
      logger.info("Image uploaded for user: {}, ref: {}", username, imageRef);
      return ResponseEntity.ok(imageRef);
    } catch (Exception e) {
      logger.error(
          "Failed to upload image for user: {}, file: {}", username, file.getOriginalFilename(), e);
      return ResponseEntity.badRequest().body("Failed to upload image: " + e.getMessage());
    }
  }
}
