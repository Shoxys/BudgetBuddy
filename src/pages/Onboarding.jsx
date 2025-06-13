import OnboardingLayout from '../components/OnBoardingLayout';
import MoneyInput from '../components/MoneyInput';

export default function Onboarding() {
    return(
        <>
        <OnboardingLayout>
            <p className="text-gray-700 mt-3">Select either or both options if the any following are owned:</p>
            <div className="flex gap-4 mt-8">
                 <button className="rounded-2xl relative overflow-hidden outline-6 outline-primary_blue focus:shadow-custom-blue">
                    <img class="rounded-2xl" src="src/assets/investments.png" alt="Investment options you may have"/>
                </button>
                <button className="rounded-2xl relative outline-6 outline-primary_blue focus:shadow-custom-blue">
                    <img className="rounded-2xl" src="src/assets/savings.png" alt="Saving options you may have"/>
                </button>
            </div>
        </OnboardingLayout>
        <OnboardingLayout>
            <p className="text-gray-700 mt-3 mb-8">Keep track of your total networth balance by providing us with the following:</p>
                <MoneyInput id="investments" label="Total Investments" placeholder="Enter value of total investments"/>   
                <MoneyInput id="savings" label="Total Savings" placeholder="Enter value of total savings"/>  
        </OnboardingLayout>
        </>
    )
}