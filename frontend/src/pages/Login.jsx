import AuthForm from '../components/AuthForm';

export default function Login() {
  const loginFields = [
    { id: 'email', label: 'Email', type: 'email', placeholder: 'example.email@gmail.com', required: true },
    { id: 'password', label: 'Password', type: 'password', placeholder: 'Enter at least 8+ characters ', required: true },
  ];

  const handleSubmit = () => {
      // Handle login logic
      alert('Logging in!');
  }

  return (
    <div className="min-h-screen flex flex-row">
      <div className="w-1/2 min-h-screen flex flex-col">
        <div className="p-6 absolute">
          <img src="/src/assets/bb-logo-text.png" alt="Budget Buddy Logo with text" width="180" />
        </div>
        <div className="flex-1 flex items-center justify-center px-12 ">
          <div className="text-center w-1/2">
              <h1 className="font-header text-gray-800 text-5xl 3xl:text-6xl font-medium">Login</h1>
              <AuthForm
                fields={loginFields}
                onSubmit={handleSubmit}
                buttonLabel="Login"
                className="w-full mt-9"
              />
              <p className="mt-5 font-body text-md"> Don't have an account? 
                <a className="text-primary_blue underline text-lg font-medium" href="#">Sign Up</a>
              </p>
          </div>
        </div>
      </div>
      <div className="w-1/2 min-h-screen bg-[url('/src/assets/login-bg.png')] bg-cover bg-no-repeat order-2"></div>
    </div>
  );
}