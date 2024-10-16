import { getData } from '@/helper/fetch'
import { withAuthenticationHeader } from './authentication'

export interface EventDays {
  id: string
  name: string
  dayOfEvent: number
}

export const getEventDays = (): Promise<EventDays[]> =>
  getData<EventDays[]>('event-days', withAuthenticationHeader()).then((eventDays) =>
    eventDays.sort((a, b) => a.dayOfEvent - b.dayOfEvent)
  )
