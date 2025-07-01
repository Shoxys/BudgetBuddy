import { useState } from "react";
import { Dialog } from "@headlessui/react";
import { formatMoney } from "../../Utils/helpers";

const categories = [
  "Transfers out",
  "Personal Care",
  "Restaurants & takeaway",
  "Attractions & events",
];

export default function EditModal({ isOpen, onClose }) {
  const [details, setDetails] = useState("Emergency Fund");
  const [category, setCategory] = useState("Personal Care");
  const [type, setType] = useState("Debit");
  const [date, setDate] = useState("2022-04-27");
  const [amount, setAmount] = useState(68.96);
  const [balance, setBalance] = useState(200.21);

  return (
    <Dialog open={isOpen} onClose={onClose} className="relative z-50">
      <div className="fixed inset-0 bg-gray-600/60" aria-hidden="true" />
      <div className="fixed inset-0 flex items-center justify-center p-4 font-body">
        <Dialog.Panel className="w-full max-w-md rounded-lg bg-white p-6 shadow-lg">
          <Dialog.Title className="text-lg font-semibold font-header mb-4">Edit Transaction</Dialog.Title>

          <div className="space-y-4">
            {/* Details Input */}
            <div>
              <label className="block text-sm font-medium text-gray-700 font-header">Details</label>
              <input
                type="text"
                value={details}
                onChange={(e) => setDetails(e.target.value)}
                className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue"
              />
            </div>

            {/* Category & Type */}
            <div className="grid grid-cols-2 gap-4">
              {/* Category Dropdown */}
              <div>
                <label className="block text-sm font-medium text-gray-700 font-header">Category</label>
                <select
                  value={category}
                  onChange={(e) => setCategory(e.target.value)}
                  className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue"
                >
                  {categories.map((cat) => (
                    <option key={cat} value={cat}>
                      {cat}
                    </option>
                  ))}
                </select>
              </div>

              {/* Type Dropdown */}
              <div>
                <label className="block text-sm font-medium text-gray-700 font-header">Type</label>
                <select
                  value={type}
                  onChange={(e) => setType(e.target.value)}
                  className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue"
                >
                  <option value="Credit">Credit</option>
                  <option value="Debit">Debit</option>
                </select>
              </div>
            </div>

            
            {/* Transaction Amount */}
            <div>
            <label className="block text-sm font-medium text-gray-700 font-header">Amount</label>
            <span className="absolute ml-3 mt-2.5 font-header text-lg">$</span>
            <input
                type="number"
                value={amount}
                onChange={(e) => setAmount(formatMoney(e.target.value))}
                className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue pl-7"
              />
            </div>

            {/* Account Balance */}
            <div>
            <label className="block text-sm font-medium text-gray-700 font-header">Balance</label>
            <span className="absolute ml-3 mt-2.5 font-header text-lg">$</span>
            <input
                type="number"
                value={balance}
                onChange={(e) => setBalance(formatMoney(e.target.value))}
                className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue pl-7"
              />
            </div>

            {/* Date Picker */}
            <div>
              <label className="block text-sm font-medium text-gray-700 font-header">Date</label>
              <div className="relative mt-1">
                <input
                  type="date"
                  value={date}
                  onChange={(e) => setDate(e.target.value)}
                  className="w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue"
                />
              </div>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="mt-6 flex justify-end gap-2">
            <button
              onClick={onClose}
              className="rounded-md border border-gray-300 px-4 py-2 text-gray-700 hover:bg-gray-100"
            >
              Cancel
            </button>
            <button
              onClick={() => {
                // Handle save 
                onClose();
              }}
              className="rounded-md bg-primary_blue px-5 py-2 text-white hover:bg-btn_hover"
            >
              Save
            </button>
          </div>
        </Dialog.Panel>
      </div>
    </Dialog>
  );
};


