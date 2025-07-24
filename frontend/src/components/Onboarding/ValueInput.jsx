/**
 * ValueInput component for entering account balances during onboarding.
 * Renders MoneyInput components for selected account types with async updates.
 */

import { useState } from 'react';
import { AccountTypes, AccountDisplayNames } from '../../constants/AccountConstants';
import MoneyInput from './MoneyInput';
import Notification from '../../components/Notification';
import LoadingSpinner from '../../components/LoadingSpinner';
import { useUpdateAccountBalance } from '../../api/AccountHooks';

export default function ValueInput({ selections = {}, balances = {}, setBalances }) {
  const [notification, setNotification] = useState({ message: '', type: '', isVisible: false });
  const { mutateAsync: updateBalance, isLoading: isUpdating } = useUpdateAccountBalance();

  // Handlers
  const handleChange = (type, value) => {
    setBalances((prev) => ({ ...prev, [type]: value }));
  };

  const handleBlur = async (type, value) => {
    const balanceValue = parseFloat(value);
    if (isNaN(balanceValue)) {
      setNotification({
        message: `Please enter a valid number for ${AccountDisplayNames[type].toLowerCase()}.`,
        type: 'error',
        isVisible: true,
      });
      return;
    }

    try {
      await updateBalance({
        id: null,
        name: AccountDisplayNames[type],
        type,
        balance: balanceValue,
      });
      setNotification({
        message: `${AccountDisplayNames[type]} updated successfully!`,
        type: 'success',
        isVisible: true,
      });
    } catch (error) {
      setNotification({
        message: `Failed to update ${AccountDisplayNames[type].toLowerCase()}: ${error.response?.data || error.message}`,
        type: 'error',
        isVisible: true,
      });
    }
  };

  const clearNotification = () => setNotification({ message: '', type: '', isVisible: false });

  // Layout
  return (
    <div className="relative">
      {/* Notification */}
      {notification.isVisible && (
        <Notification
          message={notification.message}
          type={notification.type}
          onClose={clearNotification}
        />
      )}
      {/* Loading */}
      {isUpdating && (
        <div className="flex justify-center py-4">
          <LoadingSpinner />
        </div>
      )}
      {/* Inputs */}
      {!isUpdating && (
        <>
          {selections[AccountTypes.INVESTMENTS] && (
            <MoneyInput
              id="investments"
              label={AccountDisplayNames[AccountTypes.INVESTMENTS]}
              placeholder="Enter value of total investments"
              value={balances[AccountTypes.INVESTMENTS] || ''}
              onChange={(e) => handleChange(AccountTypes.INVESTMENTS, e.target.value)}
              onBlur={(e) => handleBlur(AccountTypes.INVESTMENTS, e.target.value)}
            />
          )}
          {selections[AccountTypes.SAVINGS] && (
            <MoneyInput
              id="savings"
              label={AccountDisplayNames[AccountTypes.SAVINGS]}
              placeholder="Enter value of total savings"
              value={balances[AccountTypes.SAVINGS] || ''}
              onChange={(e) => handleChange(AccountTypes.SAVINGS, e.target.value)}
              onBlur={(e) => handleBlur(AccountTypes.SAVINGS, e.target.value)}
            />
          )}
        </>
      )}
    </div>
  );
}