import { useState, useRef, useEffect } from "react";

export default function ActionsDropdown({ onEdit, onDelete }) {
  const [open, setOpen] = useState(false);
  const dropdownRef = useRef(null);

  // Close dropdown on outside click
  useEffect(() => {
    const handleClickOutside = (event) => {
        if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
            setOpen(false)
        }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div className="relative" ref={dropdownRef}>
        <button 
            onClick={() => setOpen(!open)}
            className="text-2xl text-gray-600 hover:text-black"
        >
            ⋮
        </button>
        {open && (
            <div className="absolute right-0 z-10 mt-2 w-36 bg-white rounded-md shadow-md border">
                <button
                    className="flex items-center gap-2 px-3 py-2 w-full h-full hover:bg-gray-100"
                    onClick={() => {
                        setOpen(false);
                        onEdit();
                    }}>
                    <img className="h-8" src="src/assets/edit-icon.png" alt="edit icon" />
                    Edit
                </button>
                <button
                    className="flex items-center gap-1 px-3 py-2 text-pink-600 hover:bg-gray-100 w-full text-left"
                    onClick={() => {
                    setOpen(false);
                    onDelete();
                    }}
                >
                <img className="w-8" src="src/assets/trash.png" alt="trash icon" />
                Delete
                </button>
            </div>
        )}
    </div>
  );
}
