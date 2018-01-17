import Vue from 'vue'
import VueResource from 'vue-resource'

if (Vue.http === undefined) {
  Vue.use(VueResource)
}

const customActions = {
  getBoards: {method: 'GET', url: '/quizzes/boards'},
  reset: {method: 'POST', url: '/organizations/archives'}
}
export default Vue.resource('/organizations/{id}', {}, customActions)
