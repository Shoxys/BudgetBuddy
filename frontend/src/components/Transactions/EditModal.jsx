/**
 * EditModal component for updating existing transactions.
 * Handles form input and transaction updates with validation.
 */

import { useState, useEffect } from 'react';
import { Dialog } from '@headlessui/react';
import Notification from '../../components/Notification';
import { useUpdateTransaction } from '../../api/TransactionHooks';
import { TransactionType, CategoryColors, Categories } from '../../constants/TransactionConstants';
import { formatMoney } from '../../Utils/helpers';

export default function EditModal({ actionedTransaction = {}, isOpen = false, onClose }) {
  const [formData, setFormData] = useState({});
  const [notification, setNotification] = useState({ isOpen: false, type: '', message: '' });

  const { mutate: updateTransaction, isPending: isUpdatingTransaction } = useUpdateTransaction();

  // Handlers
  const handleChange = (field) => (e) => {
    setFormData((prev) => ({ ...prev, [field]: e.target.value }));
    if (notification.isOpen) {
      setNotification({ isOpen: false, type: '', message: '' });
    }
  };

  const handleSave = () => {
    // Validate form fields
    const amount = parseFloat(formData.amount);
    if (!formData.description || !formData.category || !formData.type || !formData.date || isNaN(amount) || amount <= 0) {
      setNotification({
        isOpen: true,
        type: 'error',
        message: 'Please ensure all fields are filled and amount is positive.',
      });
      return;
    }

    const dataToSend = {
      id: formData.id,
      date: formData.date,
      amount: amount,
      description: formData.description,
      category: formData.category,
      merchant: formData.merchant || '',
      type: formData.type,
    };

    updateTransaction(dataToSend, {
      onSuccess: () => {
        setNotification({
          isOpen: true,
          type: 'success',
          message: 'Transaction updated successfully!',
        });
        setTimeout(() => onClose(), 1000);
      },
      onError: (error) => {
        const errorMessage =
          error.response?.data?.message ||
          error.response?.data?.errors?.map((err) => err.defaultMessage || err.message).join('; ') ||
          error.message ||
          'Failed to update transaction.';
        setNotification({ isOpen: true, type: 'error', message: errorMessage });
      },
    });
  };

  // Sync form with transaction data
  useEffect(() => {
    if (isOpen && actionedTransaction) {
      const formattedDate = actionedTransaction.date
        ? new Date(actionedTransaction.date).toISOString().split('T')[0]
        : '';
      const displayAmount = Math.abs(actionedTransaction.amount || 0);
      const derivedType =
        actionedTransaction.type && typeof actionedTransaction.type === 'string'
          ? actionedTransaction.type
          : actionedTransaction.amount < 0
          ? TransactionType.DEBIT
          : TransactionType.CREDIT;
     setFormData({
      ...actionedTransaction, 
      date: formattedDate,
      amount: displayAmount,
      description: actionedTransaction.description || actionedTransaction.merchant || '',
      type: derivedType,
    });
      setNotification({ isOpen: false, type: '', message: '' });
    } else {
      setFormData({});
      setNotification({ isOpen: false, type: '', message: '' });
    }
  }, [actionedTransaction, isOpen]);

  const isFormValid =
    formData.description &&
    formData.category &&
    formData.type &&
    formData.date &&
    !isNaN(parseFloat(formData.amount)) &&
    parseFloat(formData.amount) > 0;

  // Live balance calculation logic
  const originalAmount = actionedTransaction?.amount || 0;
  const originalBalanceAtTransaction = actionedTransaction?.balanceAtTransaction || 0;

  const newAmountFromForm = parseFloat(formData.amount) || 0;
  const newAmountAdjustedForType =
    formData.type === TransactionType.DEBIT ? -Math.abs(newAmountFromForm) : Math.abs(newAmountFromForm);

  // Calculate the projected balance by applying the delta to the original balance
  const projectedBalance = originalBalanceAtTransaction + (newAmountAdjustedForType - originalAmount);


  // Layout
  return (
    <Dialog open={isOpen} onClose={onClose} className="relative z-50">
      {/* Notification */}
      {notification.isOpen && (
        <Notification
          isOpen={notification.isOpen}
          type={notification.type}
          message={notification.message}
          onClose={() => setNotification({ isOpen: false, type: '', message: '' })}
        />
      )}
      <div className="fixed inset-0 bg-gray-600/60" aria-hidden="true" />
      <div className="fixed inset-0 flex items-center justify-center p-4 font-body">
        <Dialog.Panel className="w-full max-w-md rounded-lg bg-white p-6 shadow-bb-general">
          {/* Header */}
          <Dialog.Title className="text-lg font-semibold font-header mb-4">Edit Transaction</Dialog.Title>
          {/* Form */}
          {actionedTransaction && (
            <div className="space-y-4">
              {/* Details */}
              <div>
                <label className="block text-sm font-medium text-gray-700 font-header">Details</label>
                <input
                  type="text"
                  value={formData.description || ''}
                  onChange={handleChange('description')}
                  className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue"
                  required
                />
              </div>
              {/* Category & Type */}
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 font-header">Category</label>
                  <select
                    value={formData.category || 'Other'}
                    onChange={handleChange('category')}
                    className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue"
                  >
                    {Categories.map((cat) => (
                      <option key={cat} value={cat}>
                        {cat}
                      </option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 font-header">Type</label>
                  <select
                    value={formData.type || TransactionType.DEBIT}
                    onChange={handleChange('type')}
                    className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue"
                  >
                    <option value={TransactionType.CREDIT}>Credit</option>
                    <option value={TransactionType.DEBIT}>Debit</option>
                  </select>
                </div>
              </div>
              {/* Amount */}
              <div className="relative">
                <label className="block text-sm font-medium text-gray-700 font-header">Amount</label>
                <span className="absolute ml-3 top-[1.9rem] font-header text-lg">$</span>
                <input
                  type="number"
                  value={formData.amount || ''}
                  onChange={handleChange('amount')}
                  className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue pl-7"
                  step="0.01"
                  min="0"
                  required
                />
              </div>
              {/* Balance */}
              <div>
                <label className="block text-sm font-medium text-gray-700 font-header">Balance After Transaction</label>
                <p
                  className={`mt-1 text-lg font-semibold ${
                    projectedBalance < 0 ? 'text-secondary_red' : 'text-[#2CBD00]'
                  }`}
                >
                  {/* Show the live projected balance, fallback to 0 if inputs are invalid */}
                  {!isNaN(projectedBalance) ? formatMoney(projectedBalance) : formatMoney(0)}
                </p>
              </div>
              {/* Date */}
              <div>
                <label className="block text-sm font-medium text-gray-700 font-header">Date</label>
                <input
                  type="date"
                  value={formData.date || ''}
                  onChange={handleChange('date')}
                  className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue"
                  required
                />
              </div>
            </div>
          )}
          {/* Actions */}
          <div className="mt-6 flex justify-end gap-2">
            <button
              onClick={onClose}
              className="rounded-md border border-gray-300 px-4 py-2 text-gray-700 hover:bg-gray-100 transition-colors"
              disabled={isUpdatingTransaction}
            >
              Cancel
            </button>
            <button
              onClick={handleSave}
              className="rounded-md bg-primary_blue px-5 py-2 text-white hover:bg-btn_hover transition-colors"
              disabled={isUpdatingTransaction || !isFormValid}
            >
              {isUpdatingTransaction ? 'Saving Changes...' : 'Save Changes'}
            </button>
          </div>
        </Dialog.Panel>
      </div>
    </Dialog>
  );
}