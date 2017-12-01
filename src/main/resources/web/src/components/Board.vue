<template>
  <el-row>
    <el-col :xs="{span: 22, offset: 1}" :lg="{span: 20, offset: 2}">
      <h2>组织排名</h2>
      <el-row class="ops">
        <el-col :xs="{span:4,offset:18}" :lg="{span:4,offset:17}">
          <el-button type="primary" size="small" @click="onRefresh"><i class="el-icon-refresh"></i>刷新</el-button>
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
    </el-col>
  </el-row>
</template>
<<script>
import Api from '@/api'

export default {
  name: 'Board',
  data () {
    return {
      orgs: []
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
    },
    onRefresh () {
      this.load()
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
    }
  },
  created: function () {
    this.load()
  }
}
</script>
<style>
.el-row .ops {
  margin-bottom: 1em;
}
</style>
