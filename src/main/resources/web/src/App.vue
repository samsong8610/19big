<template>
  <div id="app">
    <el-container>
      <el-header>
        <el-menu :default-active="$route.path" router mode="horizontal">
          <el-menu-item index="/">
            <i class="el-icon-menu"></i>
            <router-link to="home">Home</router-link>
          </el-menu-item>
          <el-menu-item index="/question">
            <i class="el-icon-tickets"></i>
            <router-link to="question">题库</router-link>
          </el-menu-item>
          <el-menu-item index="/board">
            <i class="el-icon-upload2"></i>
            <router-link to="board">排名</router-link>
          </el-menu-item>
          <el-menu-item index="/award">
            <i class="el-icon-goods"></i>
            <router-link to="award">奖品</router-link>
          </el-menu-item>
        </el-menu>
      </el-header>
      <el-main>
        <router-view/>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'app'
}
</script>

<style>
</style>
