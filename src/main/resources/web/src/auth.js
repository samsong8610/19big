import Vue from 'vue'

export default {
  pretendRequest (callback) {
    Vue.http.get('/userinfo').then(resp => {
      console.log('GET /userinfo success')
      resp.json().then(json => {
        console.log(JSON.stringify(json))
        // eslint-disable-next-line
        callback({
          authenticated: true,
          openid: json.openid,
          nickname: json.nickname
        })
      })
    }).catch(err => {
      console.log('GET /userinfo failed' + JSON.stringify(err))
      // eslint-disable-next-line
      callback({
        authenticated: false
      })
    })
  },
  login (callback) {
    if (localStorage.openid) {
      if (callback) {
        // eslint-disable-next-line
        callback(true)
      }
      return
    }
    this.pretendRequest((res) => {
      if (res.authenticated) {
        localStorage.openid = res.openid
        localStorage.nickname = res.nickname
        if (callback) {
          // eslint-disable-next-line
          callback(true)
        }
      } else {
        if (callback) {
          // eslint-disable-next-line
          callback(false)
        }
      }
    })
  },

  getUserInfo () {
    return {openid: localStorage.openid, nickname: localStorage.nickname}
  },

  logout (callback) {
    delete localStorage.openid
    delete localStorage.nickname
    if (callback) {
      // eslint-disable-next-line
      callback(false)
    }
  },

  isAuthenticated () {
    return !!localStorage.openid
  }
}
