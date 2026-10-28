import { describe, expect, it } from 'vitest'
import { h } from 'vue'
import { mount } from '@vue/test-utils'
import { VExpansionPanels } from 'vuetify/components'
import LmAttendeeExpansionPanel from '../LmAttendeeExpansionPanel.vue'
import { type Attendee, AttendeeRole, getAttendeeDefault } from '@/services/attendee'

const leader: Attendee = {
  ...getAttendeeDefault(AttendeeRole.YOUTH_LEADER, 1),
  id: 'leader-1',
  firstName: 'Lea',
  lastName: 'Leiter',
  juleikaNumber: '12340',
  juleikaExpireDate: '2030-01-01'
}

// v-expansion-panel only renders inside v-expansion-panels
const mountPanel = (juleikaIsInvalid: boolean, attendee: Attendee = leader) =>
  mount(() =>
    h(VExpansionPanels, () =>
      h(LmAttendeeExpansionPanel, {
        attendee,
        role: AttendeeRole.YOUTH_LEADER,
        roleTitle: 'Jugendleiter',
        departments: [],
        eventDays: [],
        tShirtSizes: [],
        attendeesCanBeEdited: false,
        juleikaIsInvalid
      })
    )
  )

describe('LmAttendeeExpansionPanel', () => {
  it('shows number and expire date of a valid Juleika', () => {
    const wrapper = mountPanel(false)

    expect(wrapper.text()).toContain('12340')
    expect(wrapper.text()).toContain('01.01.2030')
    expect(wrapper.find('.juleika-invalid').exists()).toBe(false)
  })

  it('shows number and expire date of an invalid Juleika marked as invalid', () => {
    const wrapper = mountPanel(true)

    const juleika = wrapper.find('.juleika-invalid')
    expect(juleika.text()).toContain('12340')
    expect(juleika.text()).toContain('01.01.2030')
    expect(juleika.attributes('title')).toBe('Keine gültige Juleika')
  })

  it('shows only the number of an invalid Juleika without expire date', () => {
    const wrapper = mountPanel(true, { ...leader, juleikaExpireDate: undefined })

    expect(wrapper.find('.juleika-invalid').text()).toBe('12340')
    expect(wrapper.text()).not.toContain('NaN')
  })
})
