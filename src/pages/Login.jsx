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
  <div className="min-h-screen">
    <div className="w-1/2 min-h-screen float-right bg-[url('src/assets/login-bg.png')] bg-cover bg-no-repeat">
  </div>
  <div className="w-1/2 py-8 px-12 3xl:py-10 3xl:px-14" >
    <div className="">
      <img src="/src/assets/bb-logo-text.png" alt="Budget Buddy Logo with text" width="180" />
    </div>
    <div className="text-center pt-24 justify-items-center">
      <h1 className="font-header text-gray-800 text-5xl font-bold">Login</h1>
      <AuthForm 
            fields={loginFields}
            onSubmit={handleSubmit}
            buttonLabel="Login"
            className="w-1/2 mt-9"
          />
    </div>
  </div>
</div>
  );
}