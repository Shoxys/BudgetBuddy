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
            <Logo size="text-2xl" size3xl="3xl:text-4xl" width="40"/> 
            <div className="flex gap-0.5 mt-7 3xl:mt-10">
               <StepsProgress step={step}/>
            </div> 
            <p className="font-body text-gray-500 text-sm mt-2">{step + 1} of {MAX_STEPS}</p>
            <h1 className="font-header text-3xl 3xl:text-5xl font-bold mt-5 3xl:mt-16 whitespace-pre-wrap">{title}</h1>
            <p className="text-gray-700 font-body 3xl:text-xl mt-3 3xl:mt-6 mb-8 3xl:mb-12">{subtitle}</p>
               {children} 
            <div className="mt-14 3xl:mt-28 w-full float-left">
                  {step != 0 && (
                     <button onClick={prevStep} className={`font-body text-md 3xl:text-lg bg-gray-100 py-2 px-5 rounded-lg text-gray-600 hover:bg-slate-200 outline-none`}>←⠀Back</button>
                  )}
            <div className="float-right">
               <button onClick={nextStep} className="text-md 3xl:text-lg px-5 py-2 font-body text-gray-700 outline-none hover:bg-slate-100 rounded-lg mr-4" >Skip for now</button>
               <Button onClick={nextStep} label="Continue⠀→" className="shadow-custom-blue 3xl:text-lg" padding="px-7 py-2" size="text-md" weight="font-normal" rounding="rounded-lg"></Button>
            </div>
            </div>
         </div>    
      </div>
      
   )
}

