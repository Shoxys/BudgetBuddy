/**
 * Dashboard component displaying account overview and financial insights.
 */
import { useState, useEffect } from 'react';
import Sidebar from '../components/Sidebar';
import Notification from '../components/Notification';
import { AccountDisplayNames } from '../constants/AccountConstants';
import {
  ValueCard,
  InsightsCard,
  NetworthCard,
  TotalBalanceCard,
  SavingGoalsCard,
  IncomeExpenseCard,
  IncomeCard,
  ExpenseAnalysisCard,
  TransactionsCard,
  UpdateAccount,
} from '../components/Dashboard';
import {
  useTotalBalance,
  useAccountsSummary,
  useNetworth,
  useSpendingInsights,
  useSavingGoals,
  useIncomeExpenseSummary,
  useIncomeTrend,
  useExpenseAnalysis,
  useRecentTransactions,
} from '../api';

/**
 * Main dashboard page for displaying financial data and managing accounts.
 * @returns {JSX.Element} Dashboard component.
 */
export default function Dashboard() {
  const [update, setUpdate] = useState(null);
  const [notification, setNotification] = useState({ isOpen: false, type: '', message: '' });

  // Data fetching hooks
  const { data: totalBalanceData, isLoading: isLoadingTotalBalance, error: errorTotalBalance } = useTotalBalance();
  const { data: accountsSummaryData, isLoading: isLoadingAccountsSummary, error: errorAccountsSummary } = useAccountsSummary();
  const { data: networthData, isLoading: isLoadingNetworth, error: errorNetworth } = useNetworth();
  const { data: spendingInsightsData, isLoading: isLoadingSpendingInsights, error: errorSpendingInsights } = useSpendingInsights();
  const { data: savingGoalsData, isLoading: isLoadingSavingGoals, error: errorSavingGoals } = useSavingGoals();
  const { data: incomeExpenseSummaryData, isLoading: isLoadingIncomeExpenseSummary, error: errorIncomeExpenseSummary } = useIncomeExpenseSummary();
  const { data: incomeTrendData, isLoading: isLoadingIncomeTrend, error: errorIncomeTrend } = useIncomeTrend();
  const { data: expenseAnalysisData, isLoading: isLoadingExpenseAnalysis, error: errorExpenseAnalysis } = useExpenseAnalysis();
  const { data: recentTransactionsData, isLoading: isLoadingRecentTransactions, error: errorRecentTransactions } = useRecentTransactions();

  // Loading state
  const isLoadingAny =
    isLoadingTotalBalance ||
    isLoadingAccountsSummary ||
    isLoadingNetworth ||
    isLoadingSpendingInsights ||
    isLoadingSavingGoals ||
    isLoadingIncomeExpenseSummary ||
    isLoadingIncomeTrend ||
    isLoadingExpenseAnalysis ||
    isLoadingRecentTransactions;

  // Handle errors
  useEffect(() => {
    const errors = [
      errorTotalBalance,
      errorAccountsSummary,
      errorNetworth,
      errorSpendingInsights,
      errorSavingGoals,
      errorIncomeExpenseSummary,
      errorIncomeTrend,
      errorExpenseAnalysis,
      errorRecentTransactions,
    ].filter(Boolean);

    if (errors.length > 0) {
      const message = errors
        .map((err) => err.response?.data?.message || err.message || 'Unknown error')
        .join('; ');
      setNotification({ isOpen: true, type: 'error', message: `Data loading failed: ${message}` });
    }
  }, [
    errorTotalBalance,
    errorAccountsSummary,
    errorNetworth,
    errorSpendingInsights,
    errorSavingGoals,
    errorIncomeExpenseSummary,
    errorIncomeTrend,
    errorExpenseAnalysis,
    errorRecentTransactions,
  ]);

  // Handlers
  const handleCloseNotification = () => setNotification({ ...notification, isOpen: false });

  const handleUpdateModalClose = (result) => {
    setUpdate(null);
    if (result?.success) {
      setNotification({ isOpen: true, type: 'success', message: result.message });
    } else if (result?.error) {
      setNotification({ isOpen: true, type: 'error', message: result.error.message || 'Update failed' });
    }
  };

  const handleUpdateActioned = (type, name, id) => {
    setUpdate({ type, name: name || AccountDisplayNames[type], id });
  };

  // Main layout
  return (
    <div className="relative min-h-screen bg-slate-100 pl-16">
      {/* Sidebar and notification */}
      <Sidebar selectedNav="Dashboard" />
      <Notification
        isOpen={notification.isOpen}
        message={notification.message}
        type={notification.type}
        onClose={handleCloseNotification}
      />
      {/* Content container */}
      <div className="flex flex-col px-5 pb-4 w-full h-full gap-4">
        {/* Header */}
        <div className="flex flex-row justify-between items-center">
          <h2 className="text-3xl font-bold ml-4 mt-3">
            <span className="text-blue-600">Budget</span>
            <span className="text-pink-500">Buddy</span>
            <span className="text-gray-800"> Dashboard</span>
          </h2>
        </div>
        {/* Loading state */}
        {isLoadingAny ? (
          <div className="flex-grow flex justify-center items-center h-[calc(100vh-150px)]">
            <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-blue-600"></div>
          </div>
        ) : (
          <div className="w-full h-full">
            {/* Account overview section */}
            <div className="flex flex-col bg-white/50 backdrop-blur-sm p-4 w-full text-black rounded-xl shadow-md">
              <h1 className="text-lg font-semibold text-gray-700 mb-4">Account Overview</h1>
              <div className="flex flex-wrap flex-row gap-6">
                <TotalBalanceCard totalBalance={totalBalanceData} />
                <ValueCard accounts={accountsSummaryData} setUpdateActioned={handleUpdateActioned} />
              </div>
            </div>
            {/* Insights and transactions section */}
            <div className="flex flex-col lg:flex-row gap-4 mt-4 h-full">
              <div className="w-full lg:w-1/2 grid grid-cols-1 gap-4 auto-rows-min">
                <InsightsCard data={spendingInsightsData} />
                <NetworthCard data={networthData} />
                <SavingGoalsCard data={savingGoalsData} />
                <IncomeExpenseCard data={incomeExpenseSummaryData} />
              </div>
              <div className="w-full lg:w-1/2 grid grid-cols-1 md:grid-cols-2 gap-4 auto-rows-min">
                <IncomeCard data={incomeTrendData} />
                <ExpenseAnalysisCard expenses={expenseAnalysisData} />
                <TransactionsCard data={recentTransactionsData} />
              </div>
            </div>
          </div>
        )}
      </div>
      {/* Update account modal */}
      {update && (
        <UpdateAccount
          isOpen={!!update}
          onClose={handleUpdateModalClose}
          accountType={update.type}
          accountName={update.name}
          accountId={update.id}
        />
      )}
    </div>
  );
}