package com.shoxys.budgetbuddy_backend.Services;

import com.shoxys.budgetbuddy_backend.DTOs.SavingGoal.GoalContributionRequest;
import com.shoxys.budgetbuddy_backend.DTOs.SavingGoal.GoalStat;
import com.shoxys.budgetbuddy_backend.DTOs.SavingGoal.GoalStatsResponse;
import com.shoxys.budgetbuddy_backend.DTOs.SavingGoal.SavingGoalRequest;
import com.shoxys.budgetbuddy_backend.Entities.Account;
import com.shoxys.budgetbuddy_backend.Entities.SavingGoal;
import com.shoxys.budgetbuddy_backend.Entities.User;
import com.shoxys.budgetbuddy_backend.Enums.AccountType;
import com.shoxys.budgetbuddy_backend.Enums.GoalType;
import com.shoxys.budgetbuddy_backend.Exceptions.SavingGoalNotFoundException;
import com.shoxys.budgetbuddy_backend.Exceptions.UserNotFoundException;
import com.shoxys.budgetbuddy_backend.Repo.AccountRepo;
import com.shoxys.budgetbuddy_backend.Repo.SavingGoalsRepo;
import com.shoxys.budgetbuddy_backend.Repo.UserRepo;
import com.shoxys.budgetbuddy_backend.Utils.Utils;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for managing saving goals, including creation, updates, contributions, and statistics.
 */
@Service
public class SavingGoalService {
  private static final Logger logger = LoggerFactory.getLogger(SavingGoalService.class);
  private final SavingGoalsRepo savingGoalsRepo;
  private final AccountRepo accountRepo;
  private final UserRepo userRepo;
  private final AccountService accountService;

  /**
   * Constructs a SavingGoalService with required dependencies.
   *
   * @param savingGoalsRepo Repository for saving goal data access
   * @param accountRepo Repository for account data access
   * @param userRepo Repository for user data access
   * @param accountService Service for account-related operations
   */
  public SavingGoalService(
      SavingGoalsRepo savingGoalsRepo,
      AccountRepo accountRepo,
      UserRepo userRepo,
      AccountService accountService) {
    logger.debug("Initializing SavingGoalService");
    if (savingGoalsRepo == null
        || accountRepo == null
        || userRepo == null
        || accountService == null) {
      logger.error("One or more dependencies are null");
      throw new IllegalArgumentException("All dependencies must be non-null");
    }
    this.savingGoalsRepo = savingGoalsRepo;
    this.accountRepo = accountRepo;
    this.userRepo = userRepo;
    this.accountService = accountService;
    logger.info("SavingGoalService initialized successfully");
  }

  /**
   * Retrieves the title of a saving goal by ID for a user.
   *
   * @param email the user's email
   * @param id the saving goal ID
   * @return the title of the saving goal
   * @throws IllegalArgumentException if email is invalid or ID is not positive
   * @throws UserNotFoundException if user is not found
   * @throws SavingGoalNotFoundException if saving goal is not found
   */
  public String getSavingGoalTitleById(String email, long id) {
    logger.debug("Fetching saving goal title for user: {}, ID: {}", email, id);
    validateEmailAndId(email, id);
    User user =
        userRepo
            .getUserByEmail(email)
            .orElseThrow(
                () -> {
                  logger.error("User not found: {}", email);
                  return new UserNotFoundException("User not found: " + email);
                });
    String title = savingGoalsRepo.findTitleForSavingGoalByIdAndUser(id, user);
    if (Utils.nullOrEmpty(title)) {
      logger.error("Saving goal not found for ID: {}", id);
      throw new SavingGoalNotFoundException("Goal not found with ID: " + id);
    }
    logger.info("Retrieved saving goal title: {} for user: {}", title, email);
    return title;
  }

  /**
   * Retrieves a saving goal by ID for a user.
   *
   * @param email the user's email
   * @param id the saving goal ID
   * @return the saving goal object
   * @throws IllegalArgumentException if email is invalid or ID is not positive
   * @throws UserNotFoundException if user is not found
   * @throws SavingGoalNotFoundException if saving goal is not found
   */
  public SavingGoal getSavingGoalById(String email, long id) {
    logger.debug("Fetching saving goal for user: {}, ID: {}", email, id);
    validateEmailAndId(email, id);
    User user =
        userRepo
            .getUserByEmail(email)
            .orElseThrow(
                () -> {
                  logger.error("User not found: {}", email);
                  return new UserNotFoundException("User not found: " + email);
                });
    SavingGoal goal =
        savingGoalsRepo
            .findSavingGoalByIdAndUser(id, user)
            .orElseThrow(
                () -> {
                  logger.error("Saving goal not found for ID: {}", id);
                  return new SavingGoalNotFoundException("Goal not found with ID: " + id);
                });
    logger.info("Retrieved saving goal ID: {} for user: {}", id, email);
    return goal;
  }

  /**
   * Retrieves the total contribution for all saving goals of a user.
   *
   * @param email the user's email
   * @return the total contribution amount
   * @throws IllegalArgumentException if email is invalid
   * @throws UserNotFoundException if user is not found
   */
  public BigDecimal getTotalContributionForUser(String email) {
    logger.debug("Fetching total contribution for user: {}", email);
    validateEmail(email);
    User user =
        userRepo
            .getUserByEmail(email)
            .orElseThrow(
                () -> {
                  logger.error("User not found: {}", email);
                  return new UserNotFoundException("User not found: " + email);
                });
    BigDecimal total =
        Optional.ofNullable(savingGoalsRepo.sumContributionsByUser(user)).orElse(BigDecimal.ZERO);
    logger.info("Total contribution: {} for user: {}", total, email);
    return total;
  }

  /**
   * Retrieves all pending saving goals for a user.
   *
   * @param email the user's email
   * @return a list of pending saving goals
   * @throws IllegalArgumentException if email is invalid
   * @throws UserNotFoundException if user is not found
   */
  public List<SavingGoal> getPendingSavingGoalsForUser(String email) {
    logger.debug("Fetching pending saving goals for user: {}", email);
    validateEmail(email);
    User user =
        userRepo
            .getUserByEmail(email)
            .orElseThrow(
                () -> {
                  logger.error("User not found: {}", email);
                  return new UserNotFoundException("User not found: " + email);
                });
    List<SavingGoal> goals = savingGoalsRepo.findPendingSavingGoalsForUser(user);
    logger.info("Retrieved {} pending saving goals for user: {}", goals.size(), email);
    return goals == null ? List.of() : goals;
  }

  /**
   * Retrieves all completed saving goals for a user.
   *
   * @param email the user's email
   * @return a list of completed saving goals
   * @throws IllegalArgumentException if email is invalid
   * @throws UserNotFoundException if user is not found
   */
  public List<SavingGoal> getCompleteSavingGoalsForUser(String email) {
    logger.debug("Fetching completed saving goals for user: {}", email);
    validateEmail(email);
    User user =
        userRepo
            .getUserByEmail(email)
            .orElseThrow(
                () -> {
                  logger.error("User not found: {}", email);
                  return new UserNotFoundException("User not found: " + email);
                });
    List<SavingGoal> goals = savingGoalsRepo.findCompletedSavingGoalsForUser(user);
    logger.info("Retrieved {} completed saving goals for user: {}", goals.size(), email);
    return goals == null ? List.of() : goals;
  }

  /**
   * Retrieves statistics for a user's saving goals, including completed, in-progress, overdue, and
   * total counts.
   *
   * @param email the user's email
   * @return a GoalStatsResponse containing goal statistics
   * @throws IllegalArgumentException if email is invalid
   * @throws UserNotFoundException if user is not found
   */
  public GoalStatsResponse getGoalStatsForUser(String email) {
    logger.debug("Fetching goal stats for user: {}", email);
    validateEmail(email);
    User user =
        userRepo
            .getUserByEmail(email)
            .orElseThrow(
                () -> {
                  logger.error("User not found: {}", email);
                  return new UserNotFoundException("User not found: " + email);
                });
    List<GoalStat> goalStatList =
        Arrays.asList(
            getCompletedGoalStat(user),
            getInProgressGoalStat(user),
            getOverdueGoalStat(user),
            getTotalGoalStat(user));
    logger.info("Retrieved goal stats for user: {}", email);
    return new GoalStatsResponse(goalStatList);
  }

  /**
   * Generates statistics for completed saving goals.
   *
   * @param user the user to analyze
   * @return a GoalStat for completed goals
   * @throws IllegalArgumentException if user is null
   */
  public GoalStat getCompletedGoalStat(User user) {
    logger.debug("Generating completed goal stats for user ID: {}", user.getId());
    validateUser(user);
    int completedCount = savingGoalsRepo.findCountCompletedSavingGoalsByUser(user);
    String plurality = completedCount > 1 ? "goals" : "goal";
    String completedGoalInsight =
        String.format("You have completed %d %s", completedCount, plurality);
    logger.debug("Completed goal stat: {}", completedGoalInsight);
    return new GoalStat(completedGoalInsight, GoalType.COMPLETED, completedCount);
  }

  /**
   * Generates statistics for in-progress saving goals.
   *
   * @param user the user to analyze
   * @return a GoalStat for in-progress goals
   * @throws IllegalArgumentException if user is null
   */
  public GoalStat getInProgressGoalStat(User user) {
    logger.debug("Generating in-progress goal stats for user ID: {}", user.getId());
    validateUser(user);
    int inProgressCount = savingGoalsRepo.findCountInProgressSavingGoalsByUser(user);
    int totalGoals = savingGoalsRepo.findCountSavingGoalsByUser(user);
    double inProgressPercent = totalGoals > 0 ? ((double) inProgressCount / totalGoals) * 100 : 0;
    String inProgressGoalInsight =
        String.format(
            "You're making progress — %d goals (%d%%) are still in progress",
            inProgressCount, Math.round(inProgressPercent));
    logger.debug("In-progress goal stat: {}", inProgressGoalInsight);
    return new GoalStat(inProgressGoalInsight, GoalType.IN_PROGRESS, inProgressCount);
  }

  /**
   * Generates statistics for overdue saving goals.
   *
   * @param user the user to analyze
   * @return a GoalStat for overdue goals
   * @throws IllegalArgumentException if user is null
   */
  public GoalStat getOverdueGoalStat(User user) {
    logger.debug("Generating overdue goal stats for user ID: {}", user.getId());
    validateUser(user);
    int overdueCount = savingGoalsRepo.findCountOverdueSavingGoalsByUser(user);
    int pendingCount = savingGoalsRepo.findCountInProgressSavingGoalsByUser(user);
    double overduePercent = pendingCount > 0 ? ((double) overdueCount / pendingCount) * 100 : 0;
    String overdueGoalInsight =
        String.format(
            "About %.0f%% of your pending goals are overdue. Time to catch up!", overduePercent);
    logger.debug("Overdue goal stat: {}", overdueGoalInsight);
    return new GoalStat(overdueGoalInsight, GoalType.OVERDUE, overdueCount);
  }

  /**
   * Generates statistics for total saving goals.
   *
   * @param user the user to analyze
   * @return a GoalStat for total goals
   * @throws IllegalArgumentException if user is null
   */
  public GoalStat getTotalGoalStat(User user) {
    logger.debug("Generating total goal stats for user ID: {}", user.getId());
    validateUser(user);
    int totalCount = savingGoalsRepo.findCountSavingGoalsByUser(user);
    int completedCount = savingGoalsRepo.findCountCompletedSavingGoalsByUser(user);
    int completionPercent = totalCount > 0 ? (int) ((double) completedCount / totalCount * 100) : 0;
    String completionInsight =
        String.format("You have completed %d%% of total goals", completionPercent);
    logger.debug("Total goal stat: {}", completionInsight);
    return new GoalStat(completionInsight, GoalType.TOTAL, totalCount);
  }

  /**
   * Updates the contribution amount for a saving goal and recalculates account balance.
   *
   * @param email the user's email
   * @param id the saving goal ID
   * @param request the contribution request
   * @throws IllegalArgumentException if email, ID, or contribution is invalid
   * @throws UserNotFoundException if user is not found
   * @throws SavingGoalNotFoundException if saving goal is not found
   */
  @Transactional
  public void updateContributionForSavingGoal(
      String email, long id, GoalContributionRequest request) {
    logger.debug("Updating contribution for saving goal ID: {}, user: {}", id, email);
    validateEmailAndId(email, id);
    if (request == null) {
      logger.error("Contribution request is null");
      throw new IllegalArgumentException("Contribution request must not be null");
    }
    if (Utils.nullOrNegative(request.getContribution())) {
      logger.error("Invalid contribution amount: {}", request.getContribution());
      throw new IllegalArgumentException("Contribution amount must not be null or negative");
    }
    User user =
        userRepo
            .getUserByEmail(email)
            .orElseThrow(
                () -> {
                  logger.error("User not found: {}", email);
                  return new UserNotFoundException("User not found: " + email);
                });
    if (!savingGoalsRepo.existsByIdAndUser(id, user)) {
      logger.error("Saving goal not found for ID: {}", id);
      throw new SavingGoalNotFoundException("Goal not found with ID: " + id);
    }
    savingGoalsRepo.updateSavingGoalContribution(user, id, request.getContribution());
    accountService.recalculateGoalSavingsBalance(user);
    logger.info(
        "Updated contribution: {} for saving goal ID: {}, user: {}",
        request.getContribution(),
        id,
        email);
  }

  /**
   * Creates a new saving goal for a user and recalculates account balance.
   *
   * @param email the user's email
   * @param request the saving goal request
   * @return the created saving goal
   * @throws IllegalArgumentException if email or request fields are invalid
   * @throws UserNotFoundException if user is not found
   */
  @Transactional
  public SavingGoal createSavingGoal(String email, SavingGoalRequest request) {
    logger.debug("Creating saving goal for user: {}", email);
    validateEmail(email);
    if (request == null) {
      logger.error("Saving goal request is null");
      throw new IllegalArgumentException("Saving goal request must not be null");
    }
    if (Utils.nullOrEmpty(request.getTitle())) {
      logger.error("Saving goal title is null or empty");
      throw new IllegalArgumentException("Title must not be null or empty");
    }
    if (Utils.nullOrNegative(request.getTarget())) {
      logger.error("Invalid target amount: {}", request.getTarget());
      throw new IllegalArgumentException("Target amount must not be null or negative");
    }
    if (Utils.nullOrNegative(request.getContributed())) {
      logger.error("Invalid contributed amount: {}", request.getContributed());
      throw new IllegalArgumentException("Contributed amount must not be null or negative");
    }
    if (request.getDate() == null) {
      logger.error("Saving goal date is null");
      throw new IllegalArgumentException("Date must not be null");
    }
    User user =
        userRepo
            .getUserByEmail(email)
            .orElseThrow(
                () -> {
                  logger.error("User not found: {}", email);
                  return new UserNotFoundException("User not found: " + email);
                });
    Account savingGoalsAccount =
        accountRepo
            .findAccountByUserAndType(user, AccountType.GOALSAVINGS)
            .orElseGet(
                () -> {
                  logger.debug("No goal savings account found, creating new for user: {}", email);
                  return accountService.createGoalSavingsAccount(user, request.getContributed());
                });
    SavingGoal newSavingGoal =
        new SavingGoal(
            request.getTitle(),
            request.getTarget(),
            request.getContributed(),
            request.getDate(),
            request.getImageRef(),
            savingGoalsAccount,
            user);
    SavingGoal savedGoal = savingGoalsRepo.save(newSavingGoal);
    accountService.recalculateGoalSavingsBalance(user);
    logger.info("Created saving goal ID: {} for user: {}", savedGoal.getId(), email);
    return savedGoal;
  }

  /**
   * Updates an existing saving goal and recalculates account balance.
   *
   * @param email the user's email
   * @param id the saving goal ID
   * @param request the saving goal request
   * @return the updated saving goal
   * @throws IllegalArgumentException if email, ID, or request fields are invalid
   * @throws UserNotFoundException if user is not found
   * @throws SavingGoalNotFoundException if saving goal is not found
   */
  @Transactional
  public SavingGoal updateSavingGoal(String email, Long id, SavingGoalRequest request) {
    logger.debug("Updating saving goal ID: {} for user: {}", id, email);
    validateEmailAndId(email, id);
    if (request == null) {
      logger.error("Saving goal request is null");
      throw new IllegalArgumentException("Saving goal request must not be null");
    }
    if (Utils.nullOrEmpty(request.getTitle())) {
      logger.error("Saving goal title is null or empty");
      throw new IllegalArgumentException("Title must not be null or empty");
    }
    if (Utils.nullOrNegative(request.getTarget())) {
      logger.error("Invalid target amount: {}", request.getTarget());
      throw new IllegalArgumentException("Target amount must not be null or negative");
    }
    if (request.getContributed() == null) {
      logger.warn("Contributed amount is null, setting to zero");
    }
    if (request.getDate() == null) {
      logger.error("Saving goal date is null");
      throw new IllegalArgumentException("Date must not be null");
    }
    User user =
        userRepo
            .getUserByEmail(email)
            .orElseThrow(
                () -> {
                  logger.error("User not found: {}", email);
                  return new UserNotFoundException("User not found: " + email);
                });
    SavingGoal savingGoal =
        savingGoalsRepo
            .findSavingGoalByIdAndUser(id, user)
            .orElseThrow(
                () -> {
                  logger.error("Saving goal not found for ID: {}", id);
                  return new SavingGoalNotFoundException("Goal not found with ID: " + id);
                });
    if (savingGoal.getContributed().compareTo(savingGoal.getTarget()) >= 0) {
      savingGoal.setContributed(BigDecimal.ZERO);
    }
    savingGoal.setId(id);
    savingGoal.setTitle(request.getTitle());
    savingGoal.setTarget(request.getTarget());
    savingGoal.setDate(request.getDate());
    savingGoal.setImageRef(request.getImageRef());
    SavingGoal updated = savingGoalsRepo.save(savingGoal);
    accountService.recalculateGoalSavingsBalance(user);
    logger.info("Updated saving goal ID: {} for user: {}", id, email);
    return updated;
  }

  /**
   * Deletes a saving goal and recalculates account balance.
   *
   * @param email the user's email
   * @param id the saving goal ID
   * @throws IllegalArgumentException if email is invalid or ID is not positive
   * @throws UserNotFoundException if user is not found
   * @throws SavingGoalNotFoundException if saving goal is not found
   */
  @Transactional
  public void deleteSavingGoal(String email, long id) {
    logger.debug("Deleting saving goal ID: {} for user: {}", id, email);
    validateEmailAndId(email, id);
    User user =
        userRepo
            .getUserByEmail(email)
            .orElseThrow(
                () -> {
                  logger.error("User not found: {}", email);
                  return new UserNotFoundException("User not found: " + email);
                });
    if (!savingGoalsRepo.existsByIdAndUser(id, user)) {
      logger.error("Saving goal not found for ID: {}", id);
      throw new SavingGoalNotFoundException("Saving Goal not found with ID: " + id);
    }
    savingGoalsRepo.deleteSavingGoalByIdAndUser(id, user);
    accountService.recalculateGoalSavingsBalance(user);
    logger.info("Deleted saving goal ID: {} for user: {}", id, email);
  }

  /**
   * Uploads an image for a saving goal and returns the file path.
   *
   * @param username the user's username
   * @param file the image file to upload
   * @return the relative file path of the uploaded image
   * @throws IllegalArgumentException if username or file is invalid
   * @throws RuntimeException if file upload fails
   */
  public String uploadImage(String username, MultipartFile file) {
    logger.debug("Uploading image for user: {}", username);
    if (Utils.nullOrEmpty(username)) {
      logger.error("Username is null or empty");
      throw new IllegalArgumentException("Username must not be null or empty");
    }
    if (file == null || file.isEmpty()) {
      logger.error("Invalid file: null or empty");
      throw new IllegalArgumentException("File must not be null or empty");
    }
    try {
      Path uploadPath = Paths.get("uploads");
      Files.createDirectories(uploadPath);
      String fileName =
          username + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
      Path filePath = uploadPath.resolve(fileName);
      Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
      String relativePath = "/uploads/" + fileName;
      logger.info("Uploaded image to: {}", relativePath);
      return relativePath;
    } catch (Exception e) {
      logger.error("Failed to upload image for user: {}: {}", username, e.getMessage(), e);
      throw new RuntimeException("Failed to upload image: " + file.getOriginalFilename(), e);
    }
  }

  private void validateEmail(String email) {
    if (Utils.nullOrEmpty(email)) {
      logger.error("Email is null or empty");
      throw new IllegalArgumentException("Email must not be null or empty");
    }
    if (!Utils.isEmail(email)) {
      logger.error("Invalid email format: {}", email);
      throw new IllegalArgumentException("Invalid email format");
    }
  }

  private void validateEmailAndId(String email, long id) {
    validateEmail(email);
    Utils.validatePositiveId(id, "Saving goal ID must be positive");
  }

  private void validateUser(User user) {
    if (user == null) {
      logger.error("User is null");
      throw new IllegalArgumentException("User must not be null");
    }
    Utils.validatePositiveId(user.getId(), "User ID must be positive");
  }
}
