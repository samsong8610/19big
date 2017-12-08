<template>
  <div class="hello">
    <p v-if="authenticated">Welcome!</p>
    <div v-if="!authenticated">
      <p><a href="/auth/wechat?scope=snsapi_userinfo">使用微信登录</a></p>
      <p v-if="error">登录失败，请重试。</p>
    </div>
  </div>
</template>

<script>
import Auth from '@/auth'

export default {
  name: 'Home',
  data () {
    return {
      authenticated: false,
      error: false
    }
  },
  mounted () {
    Auth.login(authenticated => {
      this.authenticated = authenticated
      if (authenticated) {
        console.log('login success: ' + JSON.stringify(Auth.getUserInfo()))
        this.$router.replace(this.$route.query.redirect || '/')
      } else {
        this.error = true
      }
    })
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1, h2 {
  font-weight: normal;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
