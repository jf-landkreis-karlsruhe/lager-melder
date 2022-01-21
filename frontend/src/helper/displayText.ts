import { Food, TShirtSize } from "../services/attendee";

export const tShirtSizeText = (tShirtSize: TShirtSize) => {
  switch (tShirtSize) {
    case TShirtSize.ONE_HUNDRED_TWENTY_EIGHT:
      return "128";
    case TShirtSize.ONE_HUNDRED_FORTY:
      return "140";
    case TShirtSize.ONE_HUNDRED_FIFTY_TWO:
      return "152";
    case TShirtSize.ONE_HUNDRED_FIFTY_EIGHT:
      return "158";
    case TShirtSize.ONE_HUNDRED_SIXTY_FOUR:
      return "164";
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
    case TShirtSize.XXXL:
      return "XXXL";
    case TShirtSize.XXXXL:
      return "XXXXL";
    case TShirtSize.XXXXXL:
      return "XXXXXL";
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
