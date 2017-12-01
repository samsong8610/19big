import Vue from 'vue'
import {shallow} from 'vue-test-utils'
import Board from '@/components/Board'

describe('Board.test.js', () => {
  let cmp
  beforeEach(() => {
    cmp = shallow(Board, {
      data: {
        orgs: []
      },
      methods: {
        load () {}
      }
    })
  })

  it('data messages should equal to []', () => {
    expect(cmp.vm.orgs).toEqual([])
  })

  it('should has expected html', () => {
    expect(cmp.element).toMatchSnapshot()
  })
})