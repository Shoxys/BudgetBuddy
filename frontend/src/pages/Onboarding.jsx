/**
 * Onboarding component for guiding users through account setup and transaction upload.
 * Manages a multi-step flow for selecting account types, entering balances, and uploading transactions.
 */

// Component Imports
import OnboardingLayout from '../components/Onboarding/OnboardingLayout';
import Investments from '../components/Onboarding/Investments';
import ValueInput from '../components/Onboarding/ValueInput';
import UploadSection from '../components/Onboarding/UploadSection';

// Hook and Constant Imports
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useImportTransactions } from '../api/TransactionHooks';
import { useUpdateAccountBalance } from '../api/AccountHooks';
import { AccountTypes, AccountDisplayNames } from '../constants/AccountConstants';
import { ROUTES } from '../constants/AppConstants';

export default function Onboarding() {
  // State for step navigation and form data
  const [currentStep, setCurrentStep] = useState(0);
  const [accountSelections, setAccountSelections] = useState({
    [AccountTypes.INVESTMENTS]: false,
    [AccountTypes.SAVINGS]: false,
  });
  const [accountBalances, setAccountBalances] = useState({
    [AccountTypes.INVESTMENTS]: '',
    [AccountTypes.SAVINGS]: '',
  });
  const [transactionFiles, setTransactionFiles] = useState([]);

  const navigate = useNavigate();
  const { mutate: importTransactions, isPending: isImporting } = useImportTransactions();
  const { mutateAsync: updateAccountBalance, isPending: isUpdatingBalance } = useUpdateAccountBalance();

  const isAnyAccountSelected = accountSelections[AccountTypes.INVESTMENTS] || accountSelections[AccountTypes.SAVINGS];

  // Step configuration
  const stepConfig = {
    0: {
      component: Investments,
      props: { accountSelections, setAccountSelections },
      title: 'Welcome to Budget Buddy! 👋',
      subtitle: 'Select the options you own:',
    },
    1: {
      component: ValueInput,
      props: { selections: accountSelections, balances: accountBalances, setBalances: setAccountBalances },
      title: 'Welcome to Budget Buddy! 👋',
      subtitle: 'Keep track of your total net worth by providing the following:',
    },
    2: {
      component: UploadSection,
      props: { files: transactionFiles, setFiles: setTransactionFiles },
      title: 'Upload Your Transactions',
      subtitle: 'Gain valuable insights by uploading your bank transactions.',
    },
  };

  const { title, subtitle, component: StepComponent, props } = stepConfig[currentStep];

  // Handlers
  const handleNext = async () => {
    if (currentStep === 1 && isAnyAccountSelected) {
      const balanceUpdates = [];
      if (accountSelections[AccountTypes.INVESTMENTS] && accountBalances[AccountTypes.INVESTMENTS]) {
        balanceUpdates.push(
          updateAccountBalance({
            id: null,
            accountType: AccountTypes.INVESTMENTS,
            name: AccountDisplayNames.INVESTMENTS,
            balance: parseFloat(accountBalances[AccountTypes.INVESTMENTS]),
          })
        );
      }
      if (accountSelections[AccountTypes.SAVINGS] && accountBalances[AccountTypes.SAVINGS]) {
        balanceUpdates.push(
          updateAccountBalance({
            id: null,
            accountType: AccountTypes.SAVINGS,
            name: AccountDisplayNames.SAVINGS,
            balance: parseFloat(accountBalances[AccountTypes.SAVINGS]),
          })
        );
      }
      if (balanceUpdates.length > 0) {
        await Promise.all(balanceUpdates);
      }
    }

    if (currentStep === 2) {
      if (transactionFiles.length > 0) {
        importTransactions(transactionFiles, {
          onSuccess: () => navigate(ROUTES.DASHBOARD),
        });
      } else {
        navigate(ROUTES.DASHBOARD);
      }
      return;
    }

    if (currentStep === 0 && !isAnyAccountSelected) {
      setCurrentStep(2); 
    } else {
      setCurrentStep((prev) => prev + 1);
    }
  };
  
  const handleSkip = () => {
    if (currentStep === 2) {
      navigate(ROUTES.DASHBOARD);
    } else {
      setCurrentStep(currentStep === 0 ? 2 : currentStep + 1);
    }
  };

  const handleBack = () => {
    if (currentStep === 2 && !isAnyAccountSelected) {
      setCurrentStep(0);
    } else {
      setCurrentStep((prev) => prev - 1);
    }
  };

  // Layout
  return (
    <OnboardingLayout
      title={title}
      subtitle={subtitle}
      step={currentStep}
      onNext={handleNext}
      onBack={handleBack}
      onSkip={handleSkip}
      isNextLoading={isUpdatingBalance || isImporting}
      files={transactionFiles}
    >
      {/* Step Content */}
      <StepComponent {...props} />
    </OnboardingLayout>
  );
}