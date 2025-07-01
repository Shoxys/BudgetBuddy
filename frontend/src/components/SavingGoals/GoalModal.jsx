import { useState, useEffect } from "react";
import { Dialog } from "@headlessui/react";
import ImageUpload from "./ImageUpload";

export default function GoalModal({ isOpen, onClose, data }) {
  const [title, setName] = useState(data?.title || "");
  const [endDate, setEndDate] = useState(data?.endDate || "");
  const [target, setTarget] = useState(data?.target || "");
  const [image, setImage] = useState("");

  const DEFAULT_IMAGE = "src/assets/default.png"
  
  function resolveImage(data) {
    if (!data) return "";
    return data.image || DEFAULT_IMAGE;
  }

  useEffect(() => {
    setName(data?.title || "");
    setTarget(data?.target || "");
    setEndDate(data?.endDate || "");
    setImage(resolveImage(data));
  }, [data]);

  return (
    <Dialog open={isOpen} onClose={onClose} className="relative z-50">
      <div className="fixed inset-0 bg-gray-600/60" aria-hidden="true" />
      <div className="fixed inset-0 flex items-center justify-center p-4 font-body">
        <Dialog.Panel className="w-full max-w-md rounded-lg bg-white p-6 shadow-lg">
          <Dialog.Title className="text-lg font-semibold font-header mb-4">{data ? "Edit Goal" : "Create New Goal"}</Dialog.Title>

          <div className="space-y-4">
            {/* Details Input */}
            <div>
              <label className="block text-sm font-medium text-gray-700 font-header">Name</label>
              <input
                type="text"
                placeholder="Enter goal title"
                value={title}
                onChange={(e) => setName(e.target.value)}
                className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue"
              />
            </div>

            {/* Goal Target */}
            <div>
              <label className="block text-sm font-medium text-gray-700 font-header">Goal Target</label>
              <span className="absolute ml-3 mt-2.5 font-header text-lg">$</span>
              <input
                  type="number"
                  placeholder="Enter transaction balance"
                  value={target}
                  onChange={(e) => setTarget(e.target.value)}
                  className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue pl-7"
                />
            </div>

            {/* Image Upload */}
            <div className="space-y-2">
              <label className="font-header text-sm"> Goal Image </label>
              <ImageUpload newGoal src={image}/>
            </div>

            {/* Date Picker */}
            <div>
              <label className="block text-sm font-medium text-gray-700 font-header">Goal end date</label>
              <div className="relative mt-1">
                <input
                  type="date"
                  value={endDate}
                  onChange={(e) => setEndDate(e.target.value)}
                  className="w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue"
                />
              </div>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="mt-6 flex justify-end gap-2">
            <button
              onClick={onClose}
              className="rounded-md border border-gray-300 px-4 py-2 text-gray-700 hover:bg-gray-100 outline-none"
            >
              Cancel
            </button>
            <button
              onClick={() => {
                // Handle save 
                onClose();
              }}
              className="rounded-md bg-primary_blue px-5 py-2 text-white outline-none hover:bg-btn_hover"
            >
              {data ? "Save": "Create"}
            </button>
          </div>
        </Dialog.Panel>
      </div>
    </Dialog>
  );
};


