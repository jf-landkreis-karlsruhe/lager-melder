export const dateLocalized = (
  date: Date | string,
  locale = "de-DE"
): string => {
  return new Date(date).toLocaleString(locale, {
    weekday: "long",
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
};

export const dateTimeLocalized = (
  date: Date | string,
  locale = "de-DE"
): string => {
  return new Date(date).toLocaleString(locale, {
    weekday: "long",
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};
