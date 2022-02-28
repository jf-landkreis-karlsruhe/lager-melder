import { Food, TShirtSize } from "../services/attendee";

export const tShirtSizeText = (tShirtSize: TShirtSize) => {
  switch (tShirtSize) {
    case TShirtSize.S104110:
      return "104/110";
    case TShirtSize.S116128:
      return "116/128";
    case TShirtSize.S140152:
      return "140/152";
    case TShirtSize.S164:
      return "164";
    case TShirtSize.S176:
      return "176";
    case TShirtSize.S:
      return "S";
    case TShirtSize.M:
      return "M";
    case TShirtSize.L:
      return "L";
    case TShirtSize.XL:
      return "XL";
    case TShirtSize.XXL:
      return "XXL";
    case TShirtSize.X3L:
      return "3XL";
    case TShirtSize.X4L:
      return "4XL";
    case TShirtSize.X5L:
      return "5XL";
    default:
      return tShirtSize;
  }
};

export const birthdayText = (birthday: string) => {
  const date = new Date(birthday);
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
    case Food.ALLERGY:
      return "Allergie";
    case Food.VEGETARIAN:
      return "Vegetarisch";
    case Food.VEGAN:
      return "Vegan";
    case Food.MUSLIM:
      return "Muslimisch";
    default:
      return food;
  }
};
