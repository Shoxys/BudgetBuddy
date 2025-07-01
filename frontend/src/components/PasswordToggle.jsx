export default function PasswordToggle({ showPassword, setShowPassword, name }) {
    const togglePasswordVisibility = (inputId) => {
        setShowPassword((prev) => ({
        ...prev,
        [inputId] : !prev[inputId],
        }));
    };
    return(
        <button type="button" onClick={() => togglePasswordVisibility(name)} className="absolute right-3 top-[2.2rem] text-gray-400">
            <img
            src={showPassword[name] ? '/src/assets/eye-open.png' : '/src/assets/eye-closed.png'}
            alt={showPassword[name] ? 'Hide password' : 'Show password'}
            className="w-[1.6rem] h-full object-contain"
            />
        </button>
    )
}