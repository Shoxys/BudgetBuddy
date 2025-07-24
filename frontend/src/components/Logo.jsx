/**
 * Logo component for displaying the Budget Buddy logo and title.
 */

export default function Logo({ width = '60', size = 'text-4xl', size3xl = '3xl:text-6xl' }) {
  // Layout
  return (
    <div className="flex items-center">
      <img src="/assets/bb_logo.png" alt="Budget Buddy Logo" width={width} />
      <h2 className={`${size} ${size3xl} font-header font-bold ml-4 mt-3`}>
        <span className="text-primary_blue">Budget</span>
        <span className="text-secondary_red">Buddy</span>
      </h2>
    </div>
  );
}