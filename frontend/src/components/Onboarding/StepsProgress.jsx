/**
 * StepsProgress component for displaying onboarding step progress.
 * Renders icons indicating completed, active, or inactive steps.
 */

export default function StepsProgress({ step = 0 }) {
  const steps = Array.from({ length: 3 }, (_, index) => {
    let status = 'inactive';
    if (index < step) {
      status = 'passed';
    } else if (index === step) {
      status = 'active';
    }
    return {
      index,
      icon: `step-${status}.png`,
      alt: `Step ${status}`,
    };
  });

  // Layout
  return (
    <div className="flex gap-2">
      {steps.map(({ index, icon, alt }) => (
        <img key={index} src={`/assets/${icon}`} alt={alt} className="w-11 h-4" />
      ))}
    </div>
  );
}