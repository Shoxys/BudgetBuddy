export default function StepsProgress ({step}) {
    return (
        <>
            {[0, 1, 2].map((index) => {
            let icon = "";
            let alt = "";

            if (index < step) {
                icon = "step-passed.png";
                alt = "Step indicator passed";
            } else if (index === step){
                icon = "step-active.png";
                alt = "Step indicator active";
            } else {
                icon = "step-inactive.png";
                alt = "Step indicator inactive"
            }
            return (
                <img key={index} src={`src/assets/${icon}`} alt={alt} />
             );
            })}
    </>
  );
}