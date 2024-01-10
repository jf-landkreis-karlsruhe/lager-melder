import { describe, it, expect } from 'vitest'

import { mount } from '@vue/test-utils'
import LmFooter from '../LmFooter.vue'

describe('LmFooter', () => {
  it('renders properly', () => {
    const wrapper = mount(LmFooter)
    expect(wrapper.text()).toContain('Impressum')
    expect(wrapper.text()).toContain('Jugendfeuerwehr Landkreis Karlsruhe. All rights reserved')
  })
})
