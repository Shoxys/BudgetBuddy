import { useState } from "react";
import { Dialog } from "@headlessui/react";
import { formatMoney } from "../../Utils/helpers";

export default function UpdateSavings({ isOpen, onClose }) {
  const [savingsBalance, setSavingsBalance] = useState(2000);

  return (
    <Dialog open={isOpen} onClose={onClose} className="relative z-50">
      <div className="fixed inset-0 bg-gray-600/60" aria-hidden="true" />
      <div className="fixed inset-0 flex items-center justify-center p-4 font-body">
        <Dialog.Panel className="w-full max-w-md rounded-lg bg-white p-6 shadow-lg">
          <Dialog.Title className="text-lg font-semibold font-header mb-2">Update Savings</Dialog.Title>

          <div className="space-y-4">
            {/* Savings Balance */}
            <div className="relative">
              <span className="absolute ml-3 top-[2.1rem] font-header text-lg">$</span>
              <label className="font-body text-sm">Amount</label>
              <input
                  type="number"
                  value={savingsBalance}
                  onBlur={(e) => setSavingsBalance(formatMoney(e.target.value))}
                  className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue pl-7"
                />
              </div>
            </div>


          {/* Action Buttons */}
          <div className="mt-6 flex justify-end gap-2">
            <button
              onClick={onClose}
              className="rounded-md border border-gray-300 px-4 py-2 text-gray-700 hover-effect hover:bg-gray-100"
            >
              Cancel
            </button>
            <button
              onClick={() => {
                // Handle save 
                onClose();
              }}
              className="rounded-md bg-primary_blue px-5 py-2 text-white hover-effect hover:bg-btn_hover"
            >
              Save
            </button>
          </div>
        </Dialog.Panel>
      </div>
    </Dialog>
  );
};


