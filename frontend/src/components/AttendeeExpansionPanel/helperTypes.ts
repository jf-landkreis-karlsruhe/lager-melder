import type { EventDays } from '@/services/eventDays'

export interface TShirtSizeSelect {
  title: string
  props: Object
}
export interface HelperDaySelect {
    title: string
    value: string
}

export interface DepartmentSelect {
    title: string
    value: number
}

export const getHelperDaySelect = (eventDays: EventDays[]): HelperDaySelect[] => {
    return eventDays.map((day) => ({
        title: day.name,
        value: day.id
    }))
}
