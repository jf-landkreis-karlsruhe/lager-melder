const isValidJuleikaExpireDate = (juleikaExpireDate: string | null, registrationEnd?: Date | null) => {
  if (!juleikaExpireDate) return false
  if (!registrationEnd) return true // if no registration end is set, no validation is needed
  return new Date(Date.parse(juleikaExpireDate)) >= registrationEnd
}

const isValidJuleikaNumber = (juleikaNumber: string) => {
  return juleikaNumber.length > 0
}

export { isValidJuleikaExpireDate, isValidJuleikaNumber }
