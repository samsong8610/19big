import Vue from 'vue'
import VueResource from 'vue-resource'

if (Vue.http === undefined) {
  Vue.use(VueResource)
}

const customActions = {
  findByPhone: {method: 'GET', url: '/awards/queries/phone/{phone}'},
  getPage: {method: 'GET', url: '/awards?page={page}&size={size}'}
}
export default Vue.resource('/awards/{id}', {}, customActions)
