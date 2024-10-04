import { Food } from '../services/attendee'

export const dateAsText = (stringDate: string) => {
  if (!stringDate) {
    return "";
  }
  const date = new Date(stringDate);
  const day = date.getDate().toString().padStart(2, "0");
  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const year = date.getFullYear();
  return `${day}.${month}.${year}`;
};

export const foodText = (food: Food) => {
  switch (food) {
    case Food.MEAT:
      return "Fleisch";
    case Food.NONE:
      return "Nichts";
    case Food.SPECIAL:
      return "Sonderessen";
    case Food.VEGETARIAN:
      return "Vegetarisch";
    case Food.MUSLIM:
      return "Muslimisch";
    default:
      return food;
  }
};
