/**
 * Localizes date
 * @param date as Date or string
 * @param locale default is de-DE
 * @returns localized date in string format, e.g. "Samstag, 04.03.2022"
 */
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

/**
 *
 * @param date as Date
 * @param locale default is de-DE
 * @returns localized date in string format with time, e.g. "Samstag, 04.03.2022 10:00"
 */
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

/**
 * Returns time in string format
 * @param date as Date
 * @returns string, e.g. "18:24"
 */
export const getTimeFromDate = (date: Date): string => {
  const hours = date.getHours();
  const minutes = date.getMinutes();
  return `${hours}:${minutes}`;
};

/**
 * Adds the time in string format to a date and returns date as Date
 * @param date current date, without time
 * @param time time as string, e.g. "18:24"
 * @returns date with hours and minutes set from time
 */
export const getDateFromDateWithTimeString = (
  date: Date | string,
  time: string
): Date => {
  const dateWithTime = new Date(date);
  const [hours, minutes] = time.split(":").map((n) => Number(n));
  dateWithTime.setHours(hours);
  dateWithTime.setMinutes(minutes);
  return dateWithTime;
};
