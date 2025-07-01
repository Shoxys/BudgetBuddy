import { Dialog } from "@headlessui/react";

export default function DeletionPopup({ isOpen, onClose, handleDelete, desc }) {
    return(
        <Dialog open={isOpen} onClose={onClose} className="relative z-50">
            <div className="fixed inset-0 bg-black bg-opacity-50 z-30 flex items-center text-center justify-center">
                <Dialog.Panel>
                    <div className="flex flex-col z-31 items-center bg-white py-6 px-6 rounded-lg shadow-xl w-96 z-50 ">
                        <img src="src/assets/trash-icon.png" alt="trash icon" />
                         <Dialog.Title className="text-lg font-bold mb-2">Confirm Deletion</Dialog.Title>
                        <p className="text-sm text-gray-600">{desc ? desc : "Are you sure you want to delete this item?"}</p>
                        <p className="text-sm text-gray-600 mb-4">This action cannot be undone</p>
                        <div className="flex justify-center gap-4">
                            <button onClick={onClose} className="text-gray-500 rounded-lg border border-gray-300 shadow-bb-general px-7 py-1.5 outline-none hover:bg-gray-50">Cancel</button>
                            <button onClick={handleDelete} className="text-white font-semibold bg-[#F44336FF] px-7 py-1.5 rounded-lg hover:bg-red-600 outline-none">Delete</button>
                        </div>
                    </div>
                </Dialog.Panel>
            </div>
        </Dialog>
    )
}