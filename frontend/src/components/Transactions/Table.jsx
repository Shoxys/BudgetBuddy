/**
 * Table component for displaying transactions with selection and action options.
 * Supports row selection, edit, and delete actions via modals.
 */

import { useState } from 'react';
import Notification from '../../components/Notification';
import ActionsDropdown from '../../components/ActionsDropdown';
import DeletionPopup from '../../components/DeletionPopup';
import Tag from '../../components/Tag';
import EditModal from './EditModal';
import { useDeleteTransaction } from '../../api/TransactionHooks';
import { formatMoney, formatDate } from '../../Utils/helpers';

export default function Table({ transactions = [], selectedIds = [], setSelectedIds }) {
  const [lastCheckedIndex, setLastCheckedIndex] = useState(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [currentActionedId, setCurrentActionedId] = useState(null);
  const [notification, setNotification] = useState({ isOpen: false, type: '', message: '' });

  const { mutate: deleteTransaction, isPending: isDeleting } = useDeleteTransaction();

  // Handlers
  const handleSelect = (event, id, index) => {
    if (!Array.isArray(transactions) || transactions.length === 0) return;

    // Handle shift-click for range selection
    if (event.shiftKey && lastCheckedIndex !== null) {
      const start = Math.min(index, lastCheckedIndex);
      const end = Math.max(index, lastCheckedIndex);
      const rangeIds = transactions.slice(start, end + 1).map((t) => t.id);
      const wasLastCheckedSelected = selectedIds.includes(transactions[lastCheckedIndex]?.id);

      setSelectedIds((prev) => {
        const newSelected = new Set(prev);
        if (wasLastCheckedSelected) {
          rangeIds.forEach((rangeId) => newSelected.add(rangeId));
        } else {
          rangeIds.forEach((rangeId) => newSelected.delete(rangeId));
        }
        return Array.from(newSelected);
      });
    } else {
      // Toggle single item selection
      setSelectedIds((prev) => {
        const newSelected = new Set(prev);
        if (newSelected.has(id)) {
          newSelected.delete(id);
        } else {
          newSelected.add(id);
        }
        return Array.from(newSelected);
      });
    }
    setLastCheckedIndex(index);
  };

  const handleSelectAll = () => {
    if (!Array.isArray(transactions) || transactions.length === 0) return;

    const currentPageIds = transactions.map((t) => t.id);
    const isAllSelectedOnPage = currentPageIds.every((id) => selectedIds.includes(id));

    setSelectedIds((prev) =>
      isAllSelectedOnPage
        ? prev.filter((id) => !currentPageIds.includes(id))
        : Array.from(new Set([...prev, ...currentPageIds]))
    );
  };

  const handleDelete = () => {
    if (currentActionedId) {
      deleteTransaction(currentActionedId, {
        onSuccess: () => {
          setNotification({
            isOpen: true,
            type: 'success',
            message: 'Transaction deleted successfully!',
          });
          setShowDeleteModal(false);
          setCurrentActionedId(null);
          setTimeout(() => setNotification({ isOpen: false, type: '', message: '' }), 2000);
        },
        onError: (error) => {
          setNotification({
            isOpen: true,
            type: 'error',
            message:
              error.response?.data?.message ||
              error.response?.data?.errors?.map((err) => err.defaultMessage || err.message).join('; ') ||
              error.message ||
              'Failed to delete transaction.',
          });
        },
      });
    }
  };

  const selectedTransaction = transactions.find((t) => t.id === currentActionedId);
  const isAllSelected = Array.isArray(transactions) && transactions.length > 0 && transactions.every((t) => selectedIds.includes(t.id));

  // Layout
  return (
    <div className="relative">
      {/* Notification */}
      {notification.isOpen && (
        <Notification
          isOpen={notification.isOpen}
          type={notification.type}
          message={notification.message}
          onClose={() => setNotification({ isOpen: false, type: '', message: '' })}
        />
      )}
      {/* Table */}
      <table className="table-fixed w-full">
        <thead>
          <tr className="font-header font-semibold text-md text-gray-800">
            <th className="w-1 text-center py-2">
              <input
                className="h-5 w-5 outline-none"
                name="selectAll"
                type="checkbox"
                onChange={handleSelectAll}
                checked={isAllSelected}
              />
            </th>
            <th className="w-5 text-center">Date</th>
            <th className="w-5">Merchant</th>
            <th className="w-7">Details</th>
            <th className="w-4">Category</th>
            <th className="w-3.5">Debit</th>
            <th className="w-3.5">Credit</th>
            <th className="w-3.5">Balance</th>
            <th className="w-0.5"></th>
          </tr>
        </thead>
        <tbody>
          {Array.isArray(transactions) && transactions.length > 0 ? (
            transactions.map(({ id, date, amount, description, balanceAtTransaction, category, merchant }, index) => (
              <tr
                key={id}
                className={`border-t border-b ${
                  selectedIds.includes(id) ? 'bg-[#008CFF66] opacity-85 border-blue-400' : 'border-gray-200'
                } text-md text-gray-700 font-body font-semibold`}
              >
                <td className="text-center py-3.5">
                  <input
                    className="h-4 w-4 outline-none"
                    type="checkbox"
                    onClick={(event) => handleSelect(event, id, index)}
                    checked={selectedIds.includes(id)}
                    readOnly
                  />
                </td>
                <td className="px-2 text-center">{formatDate(date)}</td>
                <td className="break-words whitespace-normal max-w-[250px]">{merchant}</td>
                <td className="break-words whitespace-normal max-w-[250px]">{description}</td>
                <td className="text-sm">
                  <Tag category={category} />
                </td>
                <td className="text-center text-gray-800">
                  {amount < 0 && (
                    <>
                      <span className="text-[#E11A0CFF]">-</span>
                      {formatMoney(Math.abs(amount))}
                    </>
                  )}
                </td>
                <td className="text-gray-800 text-center">
                  {amount >= 0 && (
                    <>
                      <span className="text-[#2CBD00FF]">+</span>
                      {formatMoney(amount)}
                    </>
                  )}
                </td>
                <td className="text-gray-800 text-center items-center">
                  {balanceAtTransaction < 0 ? (
                    <span className="text-[#E11A0CFF]">-</span>
                  ) : (
                    <span className="text-[#2CBD00FF]">+</span>
                  )}
                  {formatMoney(Math.abs(balanceAtTransaction))}
                </td>
                <td className="text-left">
                  <ActionsDropdown
                    onEdit={() => {
                      setShowEditModal(true);
                      setCurrentActionedId(id);
                    }}
                    onDelete={() => {
                      setShowDeleteModal(true);
                      setCurrentActionedId(id);
                    }}
                    desc="Are you sure you want to delete this transaction?"
                    disabled={selectedIds.length > 1}
                  >
                    ⋮
                  </ActionsDropdown>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="8" className="text-center py-4 text-gray-500">
                No transactions available.
              </td>
            </tr>
          )}
        </tbody>
      </table>
      {/* Modals */}
      {showDeleteModal && selectedTransaction && (
        <DeletionPopup
          actionedTransaction={selectedTransaction}
          isOpen={showDeleteModal}
          onClose={() => {
            setShowDeleteModal(false);
            setCurrentActionedId(null);
          }}
          onConfirm={handleDelete}
          isLoading={isDeleting}
        />
      )}
      {showEditModal && selectedTransaction && (
        <EditModal
          actionedTransaction={selectedTransaction}
          isOpen={showEditModal}
          onClose={() => {
            setShowEditModal(false);
            setCurrentActionedId(null);
          }}
        />
      )}
    </div>
  );
}