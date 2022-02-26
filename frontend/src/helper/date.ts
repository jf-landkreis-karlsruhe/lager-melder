export function dateAndTimeAsIsoString(date: string, time: string): string {
  const d = new Date(date)
  const [startHours, startMinutes] = time.split(":")
  d.setHours(Number(startHours), Number(startMinutes)) // startTime is e.g. 12:00
  return d.toISOString()
}

export function dateIsoString(date: Date): string {
  return new Date(
    date.getMilliseconds() - new Date().getTimezoneOffset() * 60000
  )
    .toISOString()
    .substr(0, 10)
}

export function dateFromIsoString(dateTime: string): string {
  return new Date(
    new Date(dateTime).getMilliseconds() -
      new Date().getTimezoneOffset() * 60000
  )
    .toISOString()
    .substr(11, 16)
}

export function timeFromIsoString(date: Date): string {
  return new Date(
    date.getMilliseconds() - new Date().getTimezoneOffset() * 60000
  )
    .toISOString()
    .substr(11, 16)
}
