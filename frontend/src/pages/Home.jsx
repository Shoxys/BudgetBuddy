import { useNavigate  } from 'react-router-dom';
import Logo from '../components/Logo';

export default function Home() {
  const navigate = useNavigate();
  return (
   <div
  className="min-h-screen pl-40 3xl:pl-72 pr-9 bg-[url('src/assets/home-bg.png')] bg-cover bg-no-repeat"
  style={{ 
    backgroundSize: 'cover', 
    backgroundPosition: 'center',
    width: '100vw', 
    height: '100vh' 
  }}
>
  <div className="pt-6 3xl:pt-10 flex items-center justify-between">
    <Logo/>
    <div className="flex items-center">
      <button
        className="text-bb_darkgrey bg-bb_salmon font-body text-2xl 3xl:text-4xl rounded-xl font-bold px-8 py-5 3xl:px-10 3xl:py-7 mr-5 3xl:mr-8 hover-effect hover:bg-neutral-50"
        onClick={() => {navigate("/signup")}}
      >
        Sign Up
      </button>
      <button onClick={() => navigate("/login")} className="btn-primary font-header font-bold rounded-xl px-10 py-5 3xl:px-14 3xl:py-7 text-2xl 3xl:text-4xl">Login</button>
    </div>
  </div>
  <div className="w-2/5 pt-11 3xl:pt-36">
    <h1 className="font-header text-6xl 3xl:text-8xl font-bold leading-tight 3xl:leading-tight">Gain <br /> Personalised <br /> Financial <br /> Insights</h1>
    <h2 className="mt-6 3xl:mt-12 mb-6 3xl:mb-12 font-header text-2xl 3xl:text-4xl">
      Built for users seeking to manage their finances effectively, it offers transaction logging, spending categorisation, goal tracking, and visualisations.
    </h2>
    <button onClick={() => navigate("/signup")} className="btn-primary text-3xl 3xl:text-4xl px-10 py-3 3xl:px-14 3xl:py-5 rounded-xl">Get Started</button>
  </div>
</div>
  );
}