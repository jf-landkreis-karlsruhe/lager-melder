const isValidJuleikaExpireDate = (juleikaExpireDate: string | null, eventEnd?: Date | null) => {
  if (!juleikaExpireDate) return false
  if (!eventEnd) return true // if no event end is set, no validation is needed
  return new Date(Date.parse(juleikaExpireDate)) >= eventEnd
}

const isValidJuleikaNumber = (juleikaNumber: string) => {
  return juleikaNumber.length > 0
}

export { isValidJuleikaExpireDate, isValidJuleikaNumber }
