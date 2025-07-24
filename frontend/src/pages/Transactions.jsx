/**
 * Transactions component for displaying and managing transaction history.
 * Supports pagination, sorting, importing, and deleting transactions.
 */

// Component Imports
import Sidebar from '../components/Sidebar';
import Table from '../components/Transactions/Table';
import AddModal from '../components/Transactions/AddModal';
import Notification from '../components/Notification';
import LoadingSpinner from '../components/LoadingSpinner';

// Utility Imports
import { formatDate } from '../Utils/helpers';

// Hook Imports
import { useState, useRef, useEffect } from 'react';
import { useTransactions, useDeleteMultipleTransactions, useImportTransactions } from '../api/TransactionHooks';


// Constants
const ITEMS_PER_PAGE = 20;
const DEFAULT_SORT = 'date,desc';
const NOTIFICATION_DEFAULT = { isOpen: false, message: '', type: 'success' };

// Main Component
export default function Transactions() {
  // State Management
  const [page, setPage] = useState(0);
  const [sort, setSort] = useState(DEFAULT_SORT);
  const [openAddModal, setOpenAddModal] = useState(false);
  const [selectedIds, setSelectedIds] = useState([]);
  const [notification, setNotification] = useState(NOTIFICATION_DEFAULT);
  const fileInputRef = useRef(null);

  // Data Fetching
  const { data: transactionData, isLoading, isError, error } = useTransactions(page, ITEMS_PER_PAGE, sort);
  const { mutate: deleteMultiple } = useDeleteMultipleTransactions();
  const { mutate: importTransactions, isPending: isImporting } = useImportTransactions();

  // Derived Data
  const transactions = transactionData?.content || [];
  const totalPages = transactionData?.totalPages || 0;
  const totalTransactions = transactionData?.totalElements || 0;
  const dateRange = {
    start: transactions.length ? transactions[transactions.length - 1].date : null,
    end: transactions.length ? transactions[0].date : null,
  };

  // Effects
  useEffect(() => {
    if (isError) {
      setNotification({
        isOpen: true,
        message: `Failed to fetch transactions: ${error.message || 'Please try again.'}`,
        type: 'error',
      });
    }
  }, [isError, error]);

  // Handlers
  const handleSortChange = (event) => {
    setSort(event.target.value);
    setPage(0);
  };

  const handleDeleteSelected = () => {
    if (selectedIds.length === 0) return;

    deleteMultiple(selectedIds, {
      onSuccess: () => {
        setSelectedIds([]);
        setNotification({
          isOpen: true,
          message: 'Transactions deleted successfully.',
          type: 'success',
        });
      },
      onError: (err) => {
        setNotification({
          isOpen: true,
          message: `Failed to delete transactions: ${err.message || 'Please try again.'}`,
          type: 'error',
        });
      },
    });
  };

  const handleImportClick = () => {
    fileInputRef.current.click();
  };

  const handleFileSelect = (event) => {
    const files = Array.from(event.target.files);
    if (files.length === 0) return;

    importTransactions(files, {
      onSuccess: (data) => {
        setNotification({
          isOpen: true,
          message: data.message || 'Transactions imported successfully.',
          type: 'success',
        });
      },
      onError: (err) => {
        setNotification({
          isOpen: true,
          message: `Failed to import transactions: ${err.message || 'Please try again.'}`,
          type: 'error',
        });
      },
    });
    event.target.value = null;
  };

  const handleCloseNotification = () => {
    setNotification({ ...notification, isOpen: false });
  };

  // Render
  return (
    <div className="relative min-h-screen bg-bb_slate pl-20">
      {/* File Input for Imports */}
      <input
        type="file"
        ref={fileInputRef}
        onChange={handleFileSelect}
        style={{ display: 'none' }}
        multiple
        accept=".csv"
      />

      {/* Notification Display */}
      <Notification
        isOpen={notification.isOpen}
        message={notification.message}
        type={notification.type}
        onClose={handleCloseNotification}
      />

      {/* Sidebar Navigation */}
      <Sidebar selectedNav="Dashboard" />

      {/* Main Content */}
      <div className="flex flex-col w-full h-full pb-4">
        {/* Header Section */}
        <div className="flex flex-col bg-white py-1 mt-3 px-10">
          <div className="flex flex-row justify-between items-center">
            <div className="flex flex-row items-center gap-2">
              <img className="w-11" src="/assets/transaction-icon-selected.png" alt="Transaction icon" />
              <h2 className="text-2xl font-header font-bold">Transaction History</h2>
            </div>
          </div>
          <h2 className="ml-11 mt-[-0.5rem] text-lg">
            <span className="font-header font-semibold text-gray-700">Date range - </span>
            <span className="font-header font-medium text-gray-600">
              {dateRange.start ? `${formatDate(dateRange.start)} - ${formatDate(dateRange.end)}` : 'N/A'}
            </span>
          </h2>
        </div>

        {/* Transactions Table */}
        <div className="flex flex-col min-h-[63vh] 3xl:min-h-[75vh] gap-5 bg-white py-4 px-4 ml-8 mr-8 text-black rounded-sm shadow-bb-general">
          <div className="flex flex-row justify-between items-center">
            <h1 className="font-header text-xl font-bold text-gray-800">{totalTransactions} Transactions</h1>
            <div className="flex flex-row gap-5 items-center">
              <button
                onClick={handleImportClick}
                disabled={isImporting}
                className="bg-transparent border border-secondary_red text-secondary_red font-body font-semibold underline flex items-center gap-2 px-8 py-3 rounded-md hover:bg-red-50 outline-none disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <img className="w-6" src="/assets/import-icon.png" alt="Import icon" />
                {isImporting ? 'Importing...' : 'Import'}
              </button>
              <select
                onChange={handleSortChange}
                value={sort}
                className="text-gray-500 font-header font-semibold shadow-bb-general text-md rounded-[0.2rem] bg-bb_slate py-2 px-3 outline-none cursor-pointer hover:bg-slate-50"
              >
                <option value="date,desc">Sort Latest</option>
                <option value="date,asc">Sort Oldest</option>
              </select>
              {selectedIds.length < 2 ? (
                <button
                  onClick={() => setOpenAddModal(true)}
                  className="bg-primary_blue flex items-center text-white text-md font-header font-semibold gap-2 py-2 px-4 rounded-md hover:bg-btn_hover"
                >
                  <img className="w-5" src="/assets/plus-icon.png" alt="Plus icon" />
                  Add New
                </button>
              ) : (
                <button
                  onClick={handleDeleteSelected}
                  className="bg-[#F44336] flex items-center text-white text-md font-header font-semibold gap-2 py-2 px-3 rounded-md hover:bg-red-600"
                >
                  <img className="w-5" src="/assets/trash-white.png" alt="Trash icon" />
                  Delete Selected
                </button>
              )}
            </div>
          </div>
          <span className="absolute text-sm font-semibold font-header text-gray-500 top-[9.6rem]">Select All</span>

          {/* Table Content */}
         {isLoading ? (
            <LoadingSpinner message="Loading transactions..." />
            ) : transactions.length > 0 ? (
            <Table
                transactions={transactions}
                selectedIds={selectedIds}
                setSelectedIds={setSelectedIds}
            />
            ) : (
            <div className="flex-grow flex justify-center items-center text-gray-500">
                <p className="text-xl">No transactions found.</p>
            </div>
            )}
        </div>

        {/* Pagination */}
        <div className="flex flex-row justify-center gap-4 pb-4 bg-white ml-8 mr-8">
          <button
            onClick={() => setPage((prev) => Math.max(0, prev - 1))}
            disabled={page === 0 || isLoading}
            className="bg-bb_neutral py-2 px-3 text-md text-gray-500 font-body font-semibold rounded-lg hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            ‹ Previous
          </button>
          <span className="py-2 px-3 text-md text-gray-500 font-body font-semibold">
            Page {page + 1} of {totalPages || 1}
          </span>
          <button
            onClick={() => setPage((prev) => Math.min(totalPages - 1, prev + 1))}
            disabled={page >= totalPages - 1 || isLoading}
            className="bg-bb_neutral py-2 px-5 text-md text-gray-500 font-body font-semibold rounded-lg hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Next ›
          </button>
        </div>
      </div>

      {/* Add Transaction Modal */}
      <AddModal isOpen={openAddModal} onClose={() => setOpenAddModal(false)} />
    </div>
  );
}