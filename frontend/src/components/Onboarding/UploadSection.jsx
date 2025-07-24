/**
 * UploadSection component for uploading CSV files during onboarding.
 * Supports drag-and-drop and file browsing with file list display.
 */

import { useCallback } from 'react';
import { useDropzone } from 'react-dropzone';

export default function UploadSection({ files = [], setFiles }) {
  // Handle file drop with CSV filtering
  const onDrop = useCallback(
    (acceptedFiles) => {
      const validFiles = acceptedFiles.filter((file) => file.type === 'text/csv');
      setFiles((prevFiles) => [...prevFiles, ...validFiles]);
    },
    [setFiles]
  );

  const { getRootProps, getInputProps, open, isDragActive } = useDropzone({
    onDrop,
    noClick: true,
    noKeyboard: true,
    accept: { 'text/csv': ['.csv'] },
  });

  // Handlers
  const handleRemoveFile = (fileToRemove) => {
    setFiles((prevFiles) => prevFiles.filter((file) => file !== fileToRemove));
  };

  // Layout
  return (
    <div className="flex flex-col items-center w-full">
      {/* Dropzone */}
      <div
        {...getRootProps({
          className: `flex w-4/5 h-auto py-10 flex-col justify-center items-center gap-4 bg-bb_aqua rounded-3xl border-dashed border-2 ${
            isDragActive ? 'border-blue-500 bg-blue-50' : 'border-primary_blue'
          } font-body text-gray-800 text-center text-sm cursor-pointer transition-all`,
        })}
      >
        <input {...getInputProps()} />
        <img src="/assets/upload.png" alt="Upload icon" className="w-20" />
        <h3 className="text-lg font-semibold">
          {isDragActive ? 'Drop your files here' : 'Drag & drop your files here'}
        </h3>
        <p className="text-gray-500">Supported format: .csv</p>
        <h3 className="text-md">OR</h3>
        <button
          type="button"
          onClick={open}
          className="btn-primary font-normal text-base px-6 py-2 rounded-lg hover:bg-blue-600 transition-colors"
        >
          Browse files
        </button>
      </div>
      {/* File List */}
      {files.length > 0 ? (
        <aside className="mt-6 text-sm w-4/5">
          <h4 className="text-lg font-semibold mb-2">Selected Files:</h4>
          <ul className="grid gap-3">
            {files.map((file) => (
              <li
                key={file.path}
                className="flex justify-between items-center p-3 bg-gray-100 rounded-lg shadow-bb-general"
              >
                <div className="flex items-center gap-2">
                  <img src="/assets/csv.png" alt="File icon" className="w-6 h-6" />
                  <span>
                    {file.path} - {Math.round(file.size / 1024)} KB
                  </span>
                </div>
                <button
                  onClick={() => handleRemoveFile(file)}
                  className="text-red-500 hover:text-red-700 font-medium"
                >
                  Remove
                </button>
              </li>
            ))}
          </ul>
        </aside>
      ) : (
        <aside className="mt-6 text-sm w-4/5 text-gray-500">
          <p>No files selected. Upload a .csv file or skip to continue.</p>
        </aside>
      )}
    </div>
  );
}