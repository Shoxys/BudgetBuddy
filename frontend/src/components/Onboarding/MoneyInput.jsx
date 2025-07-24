/**
 * MoneyInput component for entering monetary values.
 * Renders an input field with a dollar sign prefix.
 */

export default function MoneyInput({ id, label, placeholder, value, onChange, onBlur }) {
  // Layout
  return (
    <div className="flex flex-col mt-2 mb-5">
      {/* Label */}
      <label htmlFor={id} className="font-body font-semibold text-lg mb-1">
        {label}
      </label>
      <div className="relative">
        <span className="absolute left-3 top-1/2 -translate-y-1/2 text-lg text-gray-500 pointer-events-none">
          $
        </span>
        {/* Input */}
        <input
          id={id}
          className="pl-7 w-1/2 rounded-md border border-border_gray py-2.5 px-2 text-base font-body shadow-sm shadow-gray-300 outline-primary_blue focus:shadow-sm focus:shadow-blue-300"
          type="number"
          placeholder={placeholder}
          min="0"
          step="5"
          value={value}
          onChange={onChange}
          onBlur={onBlur} 
        />
      </div>
    </div>
  );
}