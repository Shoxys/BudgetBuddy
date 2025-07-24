/**
 * Constants for Transactions
 */

export const TransactionType = {
    CREDIT: 'CREDIT',
    DEBIT: 'DEBIT'
};

export const CategoryColors = {
  // Essentials
  'Groceries': 'bg-green-100 text-green-600',
  'Fuel': 'bg-purple-100 text-purple-600',
  'Medical': 'bg-teal-100 text-teal-600',
  'Utilities': 'bg-blue-100 text-blue-600',
  'Rent': 'bg-gray-200 text-gray-600',
  'Insurance': 'bg-sky-100 text-sky-600',
  'Accommodation': 'bg-stone-100 text-stone-600',
  'Homeware': 'bg-gray-100 text-gray-600',

  // Personal
  'Personal care': 'bg-pink-100 text-pink-500',
  'Clothing & accessories': 'bg-rose-100 text-rose-500',
  'Health & Fitness': 'bg-lime-100 text-lime-600',

  // Eating Out / Fun
  'Restaurants & takeaway': 'bg-yellow-100 text-yellow-600',
  'Entertainment': 'bg-orange-100 text-orange-600',
  'Attractions & Events': 'bg-indigo-100 text-indigo-500',
  'Subscriptions': 'bg-amber-100 text-amber-600',
  'Media': 'bg-fuchsia-100 text-fuchsia-600',
  'Gambling': 'bg-red-200 text-red-600',

  // Travel
  'Travel expenses': 'bg-cyan-100 text-cyan-600',
  'Public transport': 'bg-violet-100 text-violet-600',
  'Taxis & ride shares': 'bg-slate-100 text-slate-600',
  'Parking': 'bg-slate-100 text-slate-600',

  // Transfers / Banking
  'Transfers out': 'bg-slate-200 text-slate-600',
  'Transfers in': 'bg-slate-100 text-slate-500',
  'Internal transfers': 'bg-slate-100 text-slate-500',
  'Savings': 'bg-emerald-100 text-emerald-600',
  'Investments': 'bg-indigo-100 text-indigo-600',
  'Loan Repayment': 'bg-red-100 text-red-500',

  // Income
  'Salary': 'bg-emerald-100 text-emerald-600',
  'Refund': 'bg-lime-100 text-lime-600',
  'Government Payment': 'bg-green-100 text-green-700',
  'Cash Deposit': 'bg-zinc-100 text-zinc-600',
  'Investment income': 'bg-indigo-100 text-indigo-600',

  // Other
  'Uncategorised': 'bg-gray-100 text-gray-500',
  'Other shopping': 'bg-amber-200 text-amber-700',
  'Fees': 'bg-red-100 text-red-600',
  'Charity': 'bg-fuchsia-100 text-fuchsia-600',
  'Education': 'bg-cyan-100 text-cyan-700',
  'Services': 'bg-teal-200 text-teal-700',
  'Electronics & technology': 'bg-purple-200 text-purple-700',

  // Misc
  'Other': 'bg-gray-100 text-gray-500',
};

export const Categories = Object.keys(CategoryColors);