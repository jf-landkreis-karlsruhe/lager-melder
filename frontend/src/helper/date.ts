export function dateAndTimeAsIsoString(date: string, time: string): string {
  const d = new Date(date);
  const [startHours, startMinutes] = time.split(":");
  d.setHours(Number(startHours), Number(startMinutes)); // startTime is e.g. 12:00
  return d.toISOString();
}

export function dateIsoString(date: Date): string {
  return new Date(date.getTime() - new Date().getTimezoneOffset() * 60000)
    .toISOString()
    .substring(0, 10);
}

export function timeIsoString(date: Date): string {
  return new Date(date.getTime() - new Date().getTimezoneOffset() * 60000)
    .toISOString()
    .substring(11, 16);
}
