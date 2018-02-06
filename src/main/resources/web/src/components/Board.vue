<template>
  <el-row>
    <el-col :xs="{span: 22, offset: 1}" :lg="{span: 20, offset: 2}">
      <h2>组织参与率</h2>
      <el-row class="ops">
        <el-col :xs="{span:5,offset:18}" :lg="{span:6,offset:17}">
          <el-button type="primary" size="small" @click="onRefresh"><i class="el-icon-refresh"></i>刷新</el-button>
          <el-button type="primary" size="small" @click="onReset"><i class="el-icon-refresh"></i>重置</el-button>
        </el-col>
      </el-row>
      <el-table :data="orgs" stripe border show-summary :summary-method="calculateSummary">
        <el-table-column type="index" width="50"></el-table-column>
        <el-table-column prop="name" label="组织名称"></el-table-column>
        <el-table-column prop="submittedMembers" label="签到党员人数"></el-table-column>
        <el-table-column prop="totalMembers" label="总党员人数"></el-table-column>
        <el-table-column prop="percent" label="签到率">
          <template slot-scope="scope">
            {{scope.row.percent | percentage}}
          </template>
        </el-table-column>
      </el-table>
      <h2>个人榜单</h2>
      <el-row class="ops">
        <el-col :xs="{span:5,offset:18}" :lg="{span:6,offset:17}">
          <el-button type="primary" size="small" @click="onRefreshBoards"><i class="el-icon-refresh"></i>刷新</el-button>
        </el-col>
      </el-row>
      <el-row>
        <el-col class="personal-board" v-for="(board,idx) in boards" :key="idx" :xs="7" :lg="7">
          <h3 v-if="idx === 0">学民</h3>
          <h3 v-else-if="idx === 1">学霸</h3>
          <h3 v-else>学神</h3>
          <el-table :data="board" stripe border>
            <el-table-column type="index" width="50"></el-table-column>
            <el-table-column prop="user.nickname" label="昵称"></el-table-column>
            <el-table-column prop="score" label="成绩"></el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-col>
  </el-row>
</template>
<script>
import Api from '@/api'

export default {
  name: 'Board',
  data () {
    return {
      orgs: [],
      boards: []
    }
  },
  methods: {
    load () {
      Api.board.get().then(resp => {
        resp.json().then(json => {
          this.orgs = json
          this.$message({type: 'success', message: '获取成功。'})
        })
      }).catch(err => {
        let reason = {
          status: err.status,
          error: err.error,
          message: err.message
        }
        this.$message({type: 'warning', message: `获取组织排名失败，请稍后重试。原因：${JSON.stringify(reason)}。`})
      })
      // this.orgs = [
      //   {name: '金江镇党委', submittedMembers: 28, totalMembers: 3788, percent: 0.99992},
      //   {name: '老城镇党委', submittedMembers: 27, totalMembers: 3788, percent: 0.88888},
      //   {name: '大丰镇党委', submittedMembers: 26, totalMembers: 3788, percent: 0.77777},
      //   {name: '瑞溪镇党委', submittedMembers: 25, totalMembers: 3788, percent: 0.66666}
      // ]
      // this.boards = [
      //   [{id: 1, level: 1, user: {username: 'User1', nickname: 'Sam'}, score: 32}],
      //   [{id: 2, level: 2, user: {username: 'User2', nickname: 'Song'}, score: 32}, {id: 3, level: 2, user: {username: 'User1', nickname: 'Sam'}, score: 31}],
      //   [{id: 4, level: 3, user: {username: 'User2', nickname: 'Song'}, score: 30}, {id: 5, level: 3, user: {username: 'User1', nickname: 'Sam'}, score: 1}]
      // ]
    },
    loadBoards () {
      Api.board.getBoards({ext: true}).then(resp => {
        resp.json().then(json => {
          this.boards = json
          this.$message({type: 'success', message: '获取个人榜单成功'})
        })
      }).catch(err => {
        let reason = {
          status: err.status,
          error: err.error,
          message: err.message
        }
        this.$message({type: 'warning', message: `获取个人排名失败，请稍后再试。原因：${JSON.stringify(reason)}。`})
      })
    },
    onRefresh () {
      this.load()
    },
    onReset () {
      this.$confirm('重置将清零当前各个组织参与率，您确定要重置吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(resp => {
        Api.board.reset().then(resp => {
          this.$message({type: 'info', message: '重置成功'})
          this.load()
        }).catch(err => {
          let reason = {
            status: err.status,
            error: err.error,
            message: err.message
          }
          this.$message({type: 'warning', message: `重置组织参与率失败，请稍后再试。原因：${JSON.stringify(reason)}。`})
        })
      }).catch(() => {
        this.$message({type: 'info', message: '操作取消。'})
      })
    },
    calculateSummary (param) {
      const {columns, data} = param
      const sum = []
      columns.forEach((col, index) => {
        if (index === 0) {
          sum[index] = '合计'
          return
        }
        const values = data.map(item => Number(item[col.property]))
        if (!values.every(value => isNaN(value))) {
          sum[index] = values.reduce((prev, curr) => {
            if (!isNaN(curr)) {
              return prev + curr
            } else {
              return prev
            }
          }, 0)
        }
        if (col.property === 'percent') {
          sum[index] = (Math.round(sum[index - 2] * 10000 / sum[index - 1]) / 100) + '%'
        }
      })
      return sum
    },
    onRefreshBoards () {
      this.loadBoards()
    }
  },
  created: function () {
    this.load()
    this.loadBoards()
  }
}
</script>
<style>
.el-row .ops {
  margin-bottom: 1em;
}
.el-row .personal-board {
  margin: 0 5px;
}
.el-row .personal-board h3 {
  text-align: center;
}
</style>
