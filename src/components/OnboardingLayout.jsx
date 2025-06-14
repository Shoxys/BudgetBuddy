import Button from './Button';
import Logo from './Logo';
import StepsProgress from './StepsProgress'

export default function OnboardingLayout ({ children, step, setStep, title, subtitle} ) {
   const MAX_STEPS = 3
   const nextStep = () => {
        if (step < MAX_STEPS - 1) setStep(prevStep => prevStep + 1);
    };

    const prevStep = () => {
        if (step > 0) setStep(prevStep => prevStep - 1);
    };

   return (
      <div className="flex justify-center min-h-screen min-w-screen bg-[url('src/assets/onboard-bg.png')] bg-cover bg-center">
         <div className="w-6/13 bg-white rounded-xl opacity-80 py-3 px-12">
            <Logo size="text-2xl" width="30"/> 
            <div className="flex gap-0.5 mt-7">
               <StepsProgress step={step}/>
            </div> 
            <p className="font-body text-gray-500 text-sm mt-2">{step + 1} of {MAX_STEPS}</p>
            <h1 className="font-header text-3xl font-bold mt-5 whitespace-pre-wrap">{title}</h1>
            <p className="text-gray-700 mt-3 mb-8">{subtitle}</p>
               {children} 
            <div className="mt-14 w-full float-left">
                  {step != 0 && (
                     <button onClick={prevStep} className={`font-body text-md bg-gray-100 py-2 px-5 rounded-lg text-gray-600 hover:bg-slate-200 outline-none`}>←⠀Back</button>
                  )}
            <div className="float-right">
               <button onClick={nextStep} className="text-md px-5 py-2 font-body text-gray-700 outline-none hover:bg-slate-100 rounded-lg mr-4" >Skip for now</button>
               <Button onClick={nextStep} label="Continue⠀→" className="shadow-custom-blue" padding="px-7 py-2" size="text-md" weight="font-normal" rounding="rounded-lg"></Button>
            </div>
            </div>
         </div>    
      </div>
      
   )
}

