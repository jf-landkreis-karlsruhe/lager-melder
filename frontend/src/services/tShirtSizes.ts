import { getData, postData, deleteData } from '../helper/fetch'
import { withAuthenticationHeader } from './authentication'

export const getTShirtSizes = () => getData<string[]>('tShirtSizes', withAuthenticationHeader())

export const createTShirtSize = (tShirtSize: TShirtSize) =>
  postData<TShirtSize>(`tShirtSizes`, withAuthenticationHeader(), tShirtSize)

export const deleteTShirtSize = (replace: TShirtSizeToReplace) =>
  deleteData(`tShirtSizes/${replace.oldTShirtSize}`, withAuthenticationHeader(), {
    size: replace.newTShirtSize
  })

export interface TShirtSize {
  size: string
}

export interface TShirtSizeToReplace {
  oldTShirtSize: string
  newTShirtSize: string
}
