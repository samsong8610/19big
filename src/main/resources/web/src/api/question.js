import Vue from 'vue'
import VueResource from 'vue-resource'

if (Vue.http === undefined) {
  Vue.use(VueResource)
}

export default Vue.resource('/questionlevels/{id}')
