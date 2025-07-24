/**
 * ContributionModal component for adding contributions to a savings goal.
 * Handles input validation and async submission with notifications.
 */

import { useState } from 'react';
import { Dialog } from '@headlessui/react';
import Notification from '../../components/Notification';
import LoadingSpinner from '../../components/LoadingSpinner';
import { useContributeToGoal } from '../../api/SavingGoalsHooks';


export default function ContributionModal({ isOpen = false, onClose, data = {} }) {
  const [amount, setAmount] = useState('');
  const [notification, setNotification] = useState({ message: '', type: '', isVisible: false });
  const { mutateAsync: contributeToGoal, isPending: isContributing } = useContributeToGoal();

  // Handlers
  const clearNotification = () => setNotification({ message: '', type: '', isVisible: false });

  const handleSubmit = async () => {
    const contribution = parseFloat(amount);
    try {
      await contributeToGoal({ id: data?.id, amount: contribution });
      setNotification({
        message: `Contribution to ${data?.title || 'goal'} added successfully!`,
        type: 'success',
        isVisible: true,
      });
      setAmount('');
      setTimeout(() => {
        onClose(); 
      }, 200);
    } catch (error) {
      const errorMessage =
        error.response?.data?.errors?.map((err) => err.defaultMessage).join('; ') ||
        error.response?.data?.message ||
        error.message ||
        'Failed to add contribution.';
      setNotification({ message: errorMessage, type: 'error', isVisible: true });
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
            {/* Header */}
            <Dialog.Title className="text-lg font-semibold font-header mb-4">
              Goal Contribution
            </Dialog.Title>

            {/* Content */}
            {isContributing ? (
              <div className="flex justify-center py-4">
                <LoadingSpinner />
              </div>
            ) : (
              <div className="space-y-4">
                {/* Goal Title */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 font-header">Goal</label>
                  <input
                    type="text"
                    value={data?.title || 'Untitled Goal'}
                    disabled
                    className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue"
                  />
                </div>
                {/* Contribution Amount */}
                <div className="relative">
                  <label className="block text-sm font-medium text-gray-700 font-header">
                    Amount
                  </label>
                  <span className="absolute ml-3 top-[1.9rem] font-header text-lg">$</span>
                  <input
                    type="number"
                    placeholder="Enter contribution amount"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue pl-7"
                    min="0"
                    step="0.01"
                  />
                </div>
              </div>
            )}

            {/* Actions */}
            <div className="mt-5 flex justify-end gap-2">
              <button
                onClick={() => {
                  setAmount('');
                  onClose();
                }}
                className="rounded-md border border-gray-300 px-4 py-2 text-gray-700 hover:bg-gray-100 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleSubmit}
                className="rounded-md bg-primary_blue px-5 py-2 text-white hover:bg-btn_hover transition-colors"
                disabled={isContributing}
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