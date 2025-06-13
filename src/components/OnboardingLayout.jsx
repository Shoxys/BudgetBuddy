import Button from './Button';
import Logo from './Logo';

export default function OnboardingLayout ({ children }) {
 return (
    <div className="flex justify-center min-h-screen min-w-screen bg-[url('src/assets/onboard-bg.png')] bg-cover bg-center">
        <div className="w-6/13 bg-white rounded-xl opacity-80 py-3 px-12">
           <Logo size="text-2xl" width="30"/> 
           <div className="flex gap-0.5 mt-7">
                <img src="src/assets/step-active.png" alt="Step indicator active" />
                <img src="src/assets/step-inactive.png" alt="Step indicator inactive" />
                <img src="src/assets/step-inactive.png" alt="Step indicator inactive" />
           </div> 
           <p className="font-body text-gray-500 text-sm mt-2">1 of 3</p>
           <h1 className="font-header text-3xl font-bold mt-5">Welcome to Budget Buddy! 👋 <br/> Help us personalise your experience better!</h1>
           {children} 
           <div className="float-right mt-16">
            <button className="text-md px-7 py-2 font-body text-gray-700" >Skip for now</button>
            <Button label="Continue⠀→" className="shadow-custom-blue" padding="px-7 py-2" size="text-md" weight="font-normal" rounding="rounded-lg"></Button>
           </div>
        </div>    
    </div>

 )
}

