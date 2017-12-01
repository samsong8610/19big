import moment from 'moment'

const levelLabel = function (level) {
  switch (level) {
    case 1:
      return '学民'
    case 2:
      return '学霸'
    case 3:
      return '学神'
    default:
      console.log('unknown level: ' + level)
      return '未知'
  }
}

const percentage = function (value) {
  return (Math.round(value * 10000) / 100) + '%'
}

const safeMoment = function (value, format) {
  if (value === null) {
    return ''
  }
  try {
    return moment(value, format)
  } catch (err) {
    console.log(`convert ${value} as ${format} failed: ${err}`)
    return ''
  }
}

export default {
  install: function (Vue, options) {
    Vue.filter('levelLabel', levelLabel)
    Vue.filter('percentage', percentage)
    Vue.filter('safeMoment', safeMoment)
  }
}
