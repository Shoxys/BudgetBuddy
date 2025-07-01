import { Link, useNavigate } from 'react-router-dom';
import AuthForm from '../components/Authentication/AuthForm';

export default function Signup() {
  const signupFields = [
    { id: 'email', label: 'Email', type: 'email', placeholder: 'example.email@gmail.com', required: true },
    { id: 'password', label: 'Password', type: 'password', placeholder: 'Enter at least 8+ characters ', required: true },
    { id: 'confirmPassword', label: 'Confirm Password', type: 'password', placeholder: 'Enter password again ', required: true },
  ];

  const navigate = useNavigate()
  const handleSubmit = () => {
      // Handle login logic
      navigate("/onboarding")
      alert('Sign up!');
  }

  return (
    <div className="min-h-screen flex flex-row">
      <div className="w-1/2 min-h-screen flex flex-col">
        <div className="py-5 px-10 absolute">
          <img src="/src/assets/bb-logo-text.png" alt="Budget Buddy Logo with text" width="160" />
        </div>
        <div className="flex-1 flex items-center justify-center px-12 ">
          <div className="text-center w-1/2">
            <div className=""> 
              <h1 className="font-header text-gray-800 text-5xl 3xl:text-6xl font-semibold">Sign Up</h1>
              <AuthForm
                fields={signupFields}
                onSubmit={handleSubmit}
                buttonLabel="Sign Up"
                className="w-full mt-9 3xl:mt-14"
              />
              <p className="mt-5 font-body text-md">
                <span> Already have an account? </span> 
                <Link to="/login" className="text-primary_blue underline text-lg font-medium">Login</Link>
              </p>
            </div>
          </div>
        </div>
      </div>
      <div className="w-1/2 min-h-screen bg-[url('/src/assets/signup-bg.png')] bg-cover bg-no-repeat order-2"></div>
    </div>
  );
}