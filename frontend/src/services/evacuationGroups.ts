import { getData } from '@/helper/fetch'
import { withAuthenticationHeader } from './authentication'

export interface EvacuationGroup {
  id: string
  name: string
  color: string
}

export const getEvacuationGroup = (): Promise<EvacuationGroup[]> =>
  getData<EvacuationGroup[]>('evacuation-groups', withAuthenticationHeader())
