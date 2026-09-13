import { postData } from '../helper/fetch'
import { withAuthenticationHeader } from './authentication'

interface JuleikaValidationRequest {
  cardNumber: string
  lastName: string
}

export interface JuleikaValidationResult {
  valid: boolean
  expireDate?: string
}

export const validateJuleika = (cardNumber: string, lastName: string): Promise<JuleikaValidationResult> =>
  postData<JuleikaValidationResult>('juleika/validate', withAuthenticationHeader(), {
    cardNumber,
    lastName
  } as JuleikaValidationRequest)
