export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary_blue: '#008CFF',
        secondary_red: '#E92C81',
        bb_purple: '#853FFF',
        bb_green: '#64E69C',
        bb_yellow: '#FFBD51',
        btn_hover: '#2180F0FF',
        bb_salmon: '#FFF8F2FF',
        bb_darkgrey: '#17191FFF',
        bb_neutral: '#F3F4F6FF',
        bb_aqua: '#F0F8FF80',
      },
      fontFamily: {
        'header': ['Archivo', 'sans-serif'],
        'body': ['Inter', 'sans-serif'],
      },
      screens: {
        '3xl': '1921px', // Custom breakpoint for my 1440p display
      },
      boxShadow: {
        'custom-blue': '0px 8px 17px #008cff26, 0px 0px 2px #008cff1F',
      },
      width: {
        '6/13': '47%',
      },
      border: {
        'border_gray': '#008CFFFF',
      },
    },
  },
  plugins: [],
};