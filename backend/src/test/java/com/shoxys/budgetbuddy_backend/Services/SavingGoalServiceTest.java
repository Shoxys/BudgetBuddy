package com.shoxys.budgetbuddy_backend.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.shoxys.budgetbuddy_backend.DTOs.SavingGoal.GoalContributionRequest;
import com.shoxys.budgetbuddy_backend.DTOs.SavingGoal.GoalStat;
import com.shoxys.budgetbuddy_backend.DTOs.SavingGoal.GoalStatsResponse;
import com.shoxys.budgetbuddy_backend.DTOs.SavingGoal.SavingGoalRequest;
import com.shoxys.budgetbuddy_backend.Entities.*;
import com.shoxys.budgetbuddy_backend.Enums.*;
import com.shoxys.budgetbuddy_backend.Exceptions.UserNotFoundException;
import com.shoxys.budgetbuddy_backend.Repo.*;
import com.shoxys.budgetbuddy_backend.TestUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SavingGoalServiceTest {

  @Mock private SavingGoalsRepo savingGoalsRepo;
  @Mock private AccountRepo accountRepo;
  @Mock private UserRepo userRepo;
  @Mock private AccountService accountService;

  @InjectMocks private SavingGoalService savingGoalService;

  private final String VALID_EMAIL = "valid@example.com";
  private final String INVALID_EMAIL = "invalid@example.com";
  private final User mockUser = new User(VALID_EMAIL, "hashedpass");
  private final Account goalAccount =
      new Account("Goals", AccountType.GOALSAVINGS, null, BigDecimal.TEN, true, mockUser);
  private final SavingGoal savingGoal =
      new SavingGoal(
          "Save for Car",
          BigDecimal.valueOf(1000),
          BigDecimal.valueOf(200),
          LocalDate.now(),
          "img.jpg",
          goalAccount,
          mockUser);
  private final SavingGoal savingGoal2 =
      new SavingGoal(
          "Vacation",
          BigDecimal.valueOf(100),
          BigDecimal.valueOf(20),
          LocalDate.now(),
          "img.jpg",
          goalAccount,
          mockUser);
  private static final long GOAL_ID = 10L;
  private static final long USER_ID = 1L;

  @BeforeEach
  void setup() {
    mockUser.setId(USER_ID);
    savingGoal.setId(GOAL_ID);
  }

  @Test
  void getSavingGoalTitleById_shouldReturnTitle() {
    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
    when(savingGoalsRepo.findTitleForSavingGoalByIdAndUser(GOAL_ID, mockUser))
        .thenReturn("Save for Car");

    String title = savingGoalService.getSavingGoalTitleById(VALID_EMAIL, GOAL_ID);

    assertEquals("Save for Car", title);
  }

  @Test
  void getSavingGoalTitleById_shouldThrowIfUserNotFound() {
    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.empty());

    assertThrows(
        UserNotFoundException.class,
        () -> savingGoalService.getSavingGoalTitleById(VALID_EMAIL, USER_ID));
  }

  @Test
  void createSavingGoal_shouldCreateAndReturnGoal() {
    SavingGoalRequest req =
        new SavingGoalRequest(
            "Title", BigDecimal.valueOf(1000), BigDecimal.valueOf(0), LocalDate.now(), "img");
    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
    when(accountRepo.findAccountByUserAndType(mockUser, AccountType.GOALSAVINGS))
        .thenReturn(Optional.empty());
    when(accountService.createGoalSavingsAccount(mockUser, req.getContributed()))
        .thenReturn(goalAccount);
    when(savingGoalsRepo.save(any(SavingGoal.class))).thenAnswer(inv -> inv.getArgument(0));

    SavingGoal result = savingGoalService.createSavingGoal(VALID_EMAIL, req);

    assertEquals(req.getTitle(), result.getTitle());
    verify(accountService).recalculateGoalSavingsBalance(mockUser);
  }

  @Test
  void updateSavingGoal_shouldModifyAndReturnGoal() {
    SavingGoalRequest req =
        new SavingGoalRequest(
            "Updated Title",
            BigDecimal.valueOf(800),
            BigDecimal.valueOf(200),
            LocalDate.now(),
            "newimg.jpg");

    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
    when(savingGoalsRepo.findSavingGoalByIdAndUser(GOAL_ID, mockUser))
        .thenReturn(Optional.of(savingGoal));
    when(savingGoalsRepo.save(any(SavingGoal.class))).thenAnswer(inv -> inv.getArgument(0));

    SavingGoal updated = savingGoalService.updateSavingGoal(VALID_EMAIL, GOAL_ID, req);

    assertEquals("Updated Title", updated.getTitle());
    verify(accountService).recalculateGoalSavingsBalance(mockUser);
  }

  @Test
  void deleteSavingGoal_shouldDeleteAndRecalculate() {
    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));

    savingGoalService.deleteSavingGoal(VALID_EMAIL, GOAL_ID);

    verify(savingGoalsRepo).deleteSavingGoalByIdAndUser(GOAL_ID, mockUser);
    verify(accountService).recalculateGoalSavingsBalance(mockUser);
  }

  @Test
  void updateContribution_shouldUpdateAndRecalculate() {
    GoalContributionRequest req = new GoalContributionRequest(BigDecimal.valueOf(50));
    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));

    savingGoalService.updateContributionForSavingGoal(VALID_EMAIL, GOAL_ID, req);

    verify(savingGoalsRepo).updateSavingGoalContribution(mockUser, GOAL_ID, req.getContribution());
    verify(accountService).recalculateGoalSavingsBalance(mockUser);
  }

  // Additional simple tests can be added for getGoalStatsForUser and individual goal stat methods
  @Test
  void getTotalContributionForUser_shouldReturnTotalContribution() {
    BigDecimal expectedTotalContribution = BigDecimal.valueOf(500);

    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
    when(savingGoalsRepo.sumContributionsByUser(mockUser)).thenReturn(expectedTotalContribution);

    BigDecimal actualTotalContribution = savingGoalService.getTotalContributionForUser(VALID_EMAIL);

    assertEquals(expectedTotalContribution, actualTotalContribution);
  }

  @Test
  void getTotalContributionForUser_shouldThrowIfUserNotFound() {
    when(userRepo.getUserByEmail(INVALID_EMAIL)).thenReturn(Optional.empty());

    assertThrows(
        UserNotFoundException.class,
        () -> savingGoalService.getTotalContributionForUser(INVALID_EMAIL));
  }

  @Test
  void getPendingSavingGoalsForUser_shouldReturnSavingGoals() {
    List<SavingGoal> expectedPendingGoals = Arrays.asList(savingGoal, savingGoal2);
    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
    when(savingGoalsRepo.findPendingSavingGoalsForUser(mockUser)).thenReturn(expectedPendingGoals);

    List<SavingGoal> actualPendingGoals =
        savingGoalService.getPendingSavingGoalsForUser(VALID_EMAIL);

    assertEquals(expectedPendingGoals.size(), actualPendingGoals.size());
    TestUtils.assertListElementsMatch(
        expectedPendingGoals,
        actualPendingGoals,
        (expected, actual) -> {
          assertEquals(expected.getTitle(), actual.getTitle());
          assertEquals(expected.getDate(), actual.getDate());
          assertEquals(expected.getContributed(), actual.getContributed());
          assertEquals(expected.getTarget(), actual.getTarget());
          assertEquals(expected.getAccount(), actual.getAccount());
          assertEquals(expected.getUser(), actual.getUser());
        });
  }

  @Test
  void getPendingSavingGoalsForUser_shouldThrowIfUserNotFound() {
    when(userRepo.getUserByEmail(INVALID_EMAIL)).thenReturn(Optional.empty());

    assertThrows(
        UserNotFoundException.class,
        () -> savingGoalService.getPendingSavingGoalsForUser(INVALID_EMAIL));
  }

  @Test
  void getCompleteSavingGoalsForUser_shouldReturnSavingGoals() {
    List<SavingGoal> expectedCompleteGoals = Arrays.asList(savingGoal, savingGoal2);
    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
    when(savingGoalsRepo.findCompletedSavingGoalsForUser(mockUser))
        .thenReturn(expectedCompleteGoals);

    List<SavingGoal> actualCompleteGoals =
        savingGoalService.getCompleteSavingGoalsForUser(VALID_EMAIL);

    assertEquals(expectedCompleteGoals.size(), actualCompleteGoals.size());
    TestUtils.assertListElementsMatch(
        expectedCompleteGoals,
        actualCompleteGoals,
        (expected, actual) -> {
          assertEquals(expected.getTitle(), actual.getTitle());
          assertEquals(expected.getDate(), actual.getDate());
          assertEquals(expected.getContributed(), actual.getContributed());
          assertEquals(expected.getTarget(), actual.getTarget());
          assertEquals(expected.getAccount(), actual.getAccount());
          assertEquals(expected.getUser(), actual.getUser());
        });
  }

  @Test
  void getCompleteSavingGoalsForUser_shouldThrowIfUserNotFound() {
    when(userRepo.getUserByEmail(INVALID_EMAIL)).thenReturn(Optional.empty());

    assertThrows(
        UserNotFoundException.class,
        () -> savingGoalService.getCompleteSavingGoalsForUser(INVALID_EMAIL));
  }

  @Test
  void getGoalStatsForUser_shouldReturnGoalStatResponse() {
    // Arrange
    when(userRepo.getUserByEmail(VALID_EMAIL)).thenReturn(Optional.of(mockUser));
    when(savingGoalsRepo.findCountCompletedSavingGoalsByUser(mockUser)).thenReturn(2);
    when(savingGoalsRepo.findCountInProgressSavingGoalsByUser(mockUser)).thenReturn(3);
    when(savingGoalsRepo.findCountSavingGoalsByUser(eq(mockUser))).thenReturn(6);
    when(savingGoalsRepo.findCountOverdueSavingGoalsByUser(mockUser)).thenReturn(1);
    when(savingGoalsRepo.findCountSavingGoalsByUser(mockUser)).thenReturn(4);
    // called twice due to method reuse
    when(savingGoalsRepo.findCountCompletedSavingGoalsByUser(mockUser)).thenReturn(2);

    // Act
    GoalStatsResponse actual = savingGoalService.getGoalStatsForUser(VALID_EMAIL);

    // Assert

    List<GoalStat> expectedStats =
            List.of(
                    new GoalStat("You have completed 2 goals", GoalType.COMPLETED, 2),
                    new GoalStat("You're making progress — 3 goals (75%) are still in progress", GoalType.IN_PROGRESS, 3),
                    new GoalStat( "About 33% of your pending goals are overdue. Time to catch up!", GoalType.OVERDUE, 1),
                    new GoalStat("You have completed 50% of total goals", GoalType.TOTAL, 4));

    TestUtils.assertListElementsMatch(
            expectedStats,
            actual.getGoalStats(),
            (expected, actualStat) -> {
              assertEquals(expected.getInsight(), actualStat.getInsight());
              assertEquals(expected.getGoalType(), actualStat.getGoalType());
              assertEquals(expected.getAmount(), actualStat.getAmount());
            });
  }


  @Test
  void getGoalStatsForUser_shouldThrowIfUserNotFound() {
    when(userRepo.getUserByEmail(INVALID_EMAIL)).thenReturn(Optional.empty());

    assertThrows(
        UserNotFoundException.class, () -> savingGoalService.getGoalStatsForUser(INVALID_EMAIL));
  }

  @Test
  void getCompletedGoalStat_shouldReturnCorrectStat() {
    when(savingGoalsRepo.findCountCompletedSavingGoalsByUser(mockUser)).thenReturn(5);

    GoalStat stat = savingGoalService.getCompletedGoalStat(mockUser);

    assertEquals(GoalType.COMPLETED, stat.getGoalType());
    assertEquals(5, stat.getAmount());
    assertEquals("You have completed 5 goals", stat.getInsight());
  }

  @Test
  void getInProgressGoalStat_shouldReturnCorrectStat() {
    when(savingGoalsRepo.findCountInProgressSavingGoalsByUser(mockUser)).thenReturn(4);
    when(savingGoalsRepo.findCountSavingGoalsByUser(mockUser)).thenReturn(10);

    GoalStat stat = savingGoalService.getInProgressGoalStat(mockUser);

    assertEquals(GoalType.IN_PROGRESS, stat.getGoalType());
    assertEquals(4, stat.getAmount());
    assertEquals("You're making progress — 4 goals (40%) are still in progress", stat.getInsight());
  }

  @Test
  void getOverdueGoalStat_shouldReturnCorrectStat() {
    when(savingGoalsRepo.findCountOverdueSavingGoalsByUser(mockUser)).thenReturn(2);
    when(savingGoalsRepo.findCountInProgressSavingGoalsByUser(mockUser)).thenReturn(6);

    GoalStat stat = savingGoalService.getOverdueGoalStat(mockUser);

    assertEquals(GoalType.OVERDUE, stat.getGoalType());
    assertEquals(2, stat.getAmount());
    assertEquals("About 33% of your pending goals are overdue. Time to catch up!", stat.getInsight());
  }


  @Test
  void getTotalGoalStat_shouldReturnCorrectStat() {
    when(savingGoalsRepo.findCountSavingGoalsByUser(mockUser)).thenReturn(10);
    when(savingGoalsRepo.findCountCompletedSavingGoalsByUser(mockUser)).thenReturn(2);

    GoalStat stat = savingGoalService.getTotalGoalStat(mockUser);

    assertEquals(GoalType.TOTAL, stat.getGoalType());
    assertEquals(10, stat.getAmount());
    assertEquals("You have completed 20% of total goals", stat.getInsight());
  }
}
