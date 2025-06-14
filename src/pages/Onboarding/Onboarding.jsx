
import OnboardingLayout from '../../components/OnBoardingLayout';
import Investments from './Investments'
import ValueInput from './ValueInput'
import UploadSection from './UploadSection'
import { useState } from 'react';

const stepMap = {
    0: {component: Investments,
        title: "Welcome to Budget Buddy! 👋 \nHelp us personalise your experience better!",
        subtitle:"Select either or both options if the any following are owned:",
    },
    1: {component: ValueInput,
        title: "Welcome to Budget Buddy! 👋 \nHelp us personalise your experience better!",
        subtitle:"Keep track of your total networth balance by providing us with the following:",
    },
    2: {component: UploadSection,
        title: "Upload Your Transactions",
        subtitle:"Gain valuable insights, visualisation and control over your spending by uploading your bank transactions",
    }
};

export default function Onboarding() {
    const [step, setStep] = useState(0);
     const { title, subtitle, component: StepComponent } = stepMap[step];
    return(
        <>
            <OnboardingLayout
                title={title}
                subtitle={subtitle}
                step={step}
                setStep={setStep}
                >
                <StepComponent/>
            </OnboardingLayout>
        </>
    )
}