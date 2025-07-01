import { useState, useEffect } from "react";
import { Dialog } from "@headlessui/react";
import { formatMoney } from "../../Utils/helpers";

export default function ContributionModal({ isOpen, onClose, data }) {
  const [name, setName] = useState(data?.name || "");
  const [amount, setAmount] = useState();
  const [addToSavings, setAddToSavings] = useState(true);

  useEffect(() => {
    setName(data?.name || "");
  }, [data]);

  return (
    <Dialog open={isOpen} onClose={onClose} className="relative z-50">
      <div className="fixed inset-0 bg-gray-600/60" aria-hidden="true" />
      <div className="fixed inset-0 flex items-center justify-center p-4 font-body">
        <Dialog.Panel className="w-full max-w-md rounded-lg bg-white p-6 shadow-lg">
          <Dialog.Title className="text-lg font-semibold font-header mb-4">Goal Contribution</Dialog.Title>

          <div className="space-y-4">
            {/* Goal Name */}
            <div>
              <label className="block text-sm font-medium text-gray-700 font-header">Name</label>
              <input
                type="text"
                placeholder="Goal Title"
                value={name}
                disabled
                className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue"
              />
            </div>

            {/* Contribution Amount */}
            <div>
            <label className="block text-sm font-medium text-gray-700 font-header">Amount</label>
            <span className="absolute ml-3 mt-2.5 font-header text-lg">$</span>
            <input
                type="number"
                placeholder="Enter contribution amount"
                value={amount}
                onBlur={(e) => setAmount(formatMoney(e.target.value))}
                className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue pl-7"
              />
            </div>

            {/* Add to Savings Options */}
            <div>
              <label className="block text-gray-700 font-header text-md font-semibold">Would you like to add this contribution amount to your savings total?</label>
              <div className="mt-3 flex flex-row gap-2 font-body">
                <button 
                    className={`px-3 py-1 rounded-xl outline-none ${addToSavings ? "bg-[#B4DDFFFF] text-[#008CFFFF]" : "bg-[#F3F4F6FF] text-gray-700 shadow-bb-general"}`}
                    onClick={() => setAddToSavings(true)}>
                    Yes
                </button>
                <button 
                    className={`px-4 py-1 rounded-xl outline-none  ${addToSavings ? "bg-[#F3F4F6FF] text-gray-700 shadow-bb-general" : "bg-[#B4DDFFFF] text-[#008CFFFF]"}`}
                    onClick={() => setAddToSavings(false)}>
                    No
                </button>
            </div>
         </div>
      </div>

          {/* Action Buttons */}
          <div className="mt-5 flex justify-end gap-2">
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


