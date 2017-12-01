import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/components/Home'
import Question from '@/components/Question'
import Board from '@/components/Board'
import Award from '@/components/Award'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/question',
      name: 'question',
      component: Question
    },
    {
      path: '/board',
      name: 'board',
      component: Board
    },
    {
      path: '/award',
      name: 'award',
      component: Award
    }
  ]
})
