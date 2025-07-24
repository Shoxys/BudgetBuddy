/**
 * UpdateAccount component for updating account balance via a modal.
 * Handles input validation and displays loading or error states.
 */

import { useState, useEffect } from 'react';
import { Dialog } from '@headlessui/react';
import Notification from '../../components/Notification';
import LoadingSpinner from '../../components/LoadingSpinner';
import { useAccountBalance, useUpdateAccountBalance } from '../../api/AccountHooks';

export default function UpdateAccount({ isOpen = false, onClose, accountType, accountName, accountId }) {
  const [newBalance, setNewBalance] = useState('');
  const [notification, setNotification] = useState({ message: '', type: '', isVisible: false });

  const {
    data: currentBalance,
    isLoading: isFetchingBalance,
    isError: isFetchError,
    error: fetchError,
  } = useAccountBalance(accountName, accountType, isOpen);

  const { mutateAsync: updateBalance, isLoading: isUpdating } = useUpdateAccountBalance();

  // Sync balance and handle fetch errors
  useEffect(() => {
    if (isOpen) {
      if (currentBalance != null) {
        setNewBalance(currentBalance);
      } else {
        setNewBalance('');
      }
      if (isFetchError) {
        setNotification({
          message: `Could not load balance for ${accountName}: ${fetchError?.response?.data || fetchError?.message}`,
          type: 'error',
          isVisible: true,
        });
      } else if (notification.isVisible && notification.type === 'error') {
        setNotification({ ...notification, isVisible: false });
      }
    }
  }, [isOpen, currentBalance, isFetchError, fetchError, accountName, notification]);

  // Handlers
  const clearNotification = () => setNotification({ message: '', type: '', isVisible: false });

  const handleSave = async () => {
    const balanceValue = parseFloat(newBalance);
    if (isNaN(balanceValue)) {
      setNotification({
        message: `Please enter a valid number for the ${accountName.toLowerCase()} amount.`,
        type: 'error',
        isVisible: true,
      });
      return;
    }

    try {
      await updateBalance({ id: accountId, name: accountName, accountType, balance: balanceValue });
      setNotification({
        message: `${accountName} updated successfully!`,
        type: 'success',
        isVisible: true,
      });
      setTimeout(() => onClose({ success: true, message: `${accountName} updated successfully!` }), 1000);
    } catch (error) {
      setNotification({
        message: `Failed to update ${accountName}: ${error.response?.data || error.message}`,
        type: 'error',
        isVisible: true,
      });
    }
  };

  // Layout
  return (
    <>
      {/* Notification */}
      {notification.isVisible && (
        <Notification
          message={notification.message}
          type={notification.type}
          onClose={clearNotification}
        />
      )}

      {/* Modal */}
      <Dialog open={isOpen} onClose={() => onClose()} className="relative z-50">
        <div className="fixed inset-0 bg-gray-600/60" aria-hidden="true" />
        <div className="fixed inset-0 flex items-center justify-center p-4 font-body">
          <Dialog.Panel className="w-full max-w-md rounded-lg bg-white p-6 shadow-bb-general">
            <Dialog.Title className="text-lg font-semibold font-header mb-2">
              Update {accountName}
            </Dialog.Title>

            {/* Content */}
            {isFetchingBalance || isUpdating ? (
              <div className="flex justify-center py-4">
                <LoadingSpinner />
              </div>
            ) : (
              <div className="space-y-4 relative">
                <span className="absolute ml-3 top-[2.9rem] font-header text-lg">$</span>
                <label htmlFor="account-input" className="font-body text-sm">
                  Amount
                </label>
                <input
                  id="account-input"
                  type="number"
                  value={newBalance}
                  onChange={(e) => setNewBalance(parseFloat(e.target.value) || '')}
                  onBlur={(e) => setNewBalance(parseFloat(e.target.value) || 0)}
                  className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue pl-7"
                />
              </div>
            )}

            {/* Actions */}
            <div className="mt-6 flex justify-end gap-2">
              <button
                onClick={() => onClose()}
                className="rounded-md border border-gray-300 px-4 py-2 text-gray-700 hover:bg-gray-100 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleSave}
                className="rounded-md bg-primary_blue px-5 py-2 text-white hover:bg-btn_hover transition-colors"
                disabled={isFetchingBalance || isUpdating}
              >
                Save
              </button>
            </div>
          </Dialog.Panel>
        </div>
      </Dialog>
    </>
  );
}