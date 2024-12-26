import type {Attendee} from "@/services/attendee";

export interface AttendeeWithSelected extends Attendee {
    selected: boolean
}

export const selectAllNext = (attendees: AttendeeWithSelected[]) => {
    return attendees.some((youth) => !youth.selected);
}
