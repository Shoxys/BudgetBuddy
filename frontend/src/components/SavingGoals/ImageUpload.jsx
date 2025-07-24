/**
 * ImageUpload component for uploading goal images.
 * Supports drag-and-drop and file Browse with image preview.
 */

import { useRef } from 'react';
import Notification from '../../components/Notification';
import LoadingSpinner from '../../components/LoadingSpinner';

export default function ImageUpload({ src, onFileChange, isLoading = false, isError = false, error = '' }) {
  const fileInputRef = useRef(null);

  // Handlers
  const handleButtonClick = () => {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  };

  // Layout
  return (
    <div className="flex flex-col justify-center items-center h-2/5">
      {/* Notification */}
      {isError && (
        <Notification
          isOpen={isError}
          type="error"
          message={`Failed to upload image: ${error?.response?.data || error?.message || 'Unknown error'}`}
          onClose={() => {}}
        />
      )}
      {/* Dropzone */}
      <div
        className={`${
          src ? 'bg-cover bg-center w-[80%]' : 'bg-bb_aqua px-5 py-4'
        } flex w-[90%] h-[15rem] flex-col justify-center items-center gap-1 rounded-3xl border-dashed border border-primary_blue font-body text-gray-800 text-center text-sm`}
        style={{ backgroundImage: src ? `url('${src}')` : 'none' }}
      >
        {src ? (
          // Renders when ANY image src is provided (default, preview, or existing)
          <div className="flex h-full w-full items-center justify-center rounded-3xl bg-black/30">
            <input
              ref={fileInputRef}
              type="file"
              id="uploadFile"
              name="goalImage"
              accept=".png,.jpg"
              hidden
              onChange={onFileChange}
              disabled={isLoading}
            />
            <button
              type="button"
              className="btn-primary text-md rounded-lg shadow-bb-general font-semibold hover:bg-btn_hover transition-colors"
              onClick={handleButtonClick}
              disabled={isLoading}
            >
              {isLoading ? <LoadingSpinner size="small" /> : 'Upload New'}
            </button>
          </div>
        ) : (
          // Renders only when `src` is null (for a new goal)
          <>
            <img src="/assets/upload.png" alt="Upload icon" className="w-16 h-16" />
            <h3 className="font-body text-sm">Drag & drop your files here</h3>
            <p className="text-gray-500 font-body text-xs">Supported format: PNG, JPG. Max 10MB.</p>
            <h3 className="font-body text-md">OR</h3>
            <input
              ref={fileInputRef}
              type="file"
              id="uploadFile"
              name="goalImage"
              accept=".png,.jpg"
              hidden
              onChange={onFileChange}
              disabled={isLoading}
            />
            <button
              type="button"
              className="btn-primary font-normal text-sm hover:bg-btn_hover transition-colors"
              onClick={handleButtonClick}
              disabled={isLoading}
            >
              {isLoading ? <LoadingSpinner size="small" /> : 'Browse files'}
            </button>
          </>
        )}
      </div>
    </div>
  );
}