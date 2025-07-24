/**
 * DeletionPopup component for confirming deletion actions.
 * Displays a modal with a description and confirm/cancel buttons.
 */

import { Dialog } from '@headlessui/react';
import LoadingSpinner from './LoadingSpinner';

export default function DeletionPopup({ isOpen = false, onClose, onConfirm, desc, isLoading = false }) {
  // Layout
  return (
    <Dialog open={isOpen} onClose={onClose} className="relative z-50">
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
        <Dialog.Panel className="flex flex-col items-center bg-white py-6 px-6 rounded-lg shadow-bb-general w-96">
          <img src="/assets/trash-icon.png" alt="Delete icon" className="h-12 mb-2" />
          <Dialog.Title className="text-lg font-bold font-header mb-2">Confirm Deletion</Dialog.Title>
          <p className="text-sm text-gray-600 font-body">
            {desc || 'Are you sure you want to delete this item?'}
          </p>
          <p className="text-sm text-gray-600 font-body mb-4">This action cannot be undone</p>
          <div className="flex justify-center gap-4">
            <button
              onClick={onClose}
              className="text-gray-500 rounded-lg border border-gray-300 shadow-bb-general px-7 py-1.5 hover:bg-gray-50 transition-colors"
              disabled={isLoading}
            >
              Cancel
            </button>
            <button
              onClick={onConfirm}
              className="text-white font-semibold bg-secondary_red px-7 py-1.5 rounded-lg hover:bg-red-600 transition-colors"
              disabled={isLoading}
            >
              {isLoading ? <LoadingSpinner size="small" /> : 'Delete'}
            </button>
          </div>
        </Dialog.Panel>
      </div>
    </Dialog>
  );
}