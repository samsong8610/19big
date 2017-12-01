import Vue from 'vue'
import VueResource from 'vue-resource'

if (Vue.http === undefined) {
  Vue.use(VueResource)
}

const customActions = {
  findByPhone: {method: 'GET', url: '/awards/queries/phone/{phone}'}
}
export default Vue.resource('/awards/{id}', {}, customActions)
