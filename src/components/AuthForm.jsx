import Button from '../components/Button';

const AuthForm = ({ fields, onSubmit, buttonLabel, className = '' }) => {
  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit();
  };

  return (
    <form onSubmit={handleSubmit} className={`${className}`}>
      {fields.map((field, index) => (
        <div key={index} className="flex flex-col px-3 py-2 text-left bg-bb_neutral rounded-xl mb-4">
          <label htmlFor={field.id} className="text-lg text-gray-700 font-body font-bold">
            {field.label}
          </label>
          <input
            id={field.id}
            type={field.type || 'text'}
            placeholder={field.placeholder}
            className="rounded-sm text-left bg-bb_neutral text-md focus:outline-none focus:ring-primary_blue"
            />
        </div>
      ))}
      <Button 
        label={buttonLabel}
        size="text-lg"
        className="mt-6 shadow-custom-blue w-full py-2"
        onClick={onSubmit}
      />
    </form>
  )
}

export default AuthForm;