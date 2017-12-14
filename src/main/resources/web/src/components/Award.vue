<template>
  <el-row>
    <el-col :xs="{span: 22, offset: 1}" :lg="{span: 20, offset: 2}">
      <h2>奖品发放</h2>
      <el-row class="ops">
        <el-col :xs="{span: 9,offset: 9}" :lg="{span:9,offset:9}">
          <el-form :inline="true">
            <el-form-item label="搜索">
              <el-input type="text" v-model="phone" prefix-icon="el-icon-search" placeholder="电话" @change="searchRemotely"></el-input>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :xs="{span:4}" :lg="{span:4}">
          <el-button type="primary" size="small" @click="onRefresh"><i class="el-icon-refresh"></i>刷新</el-button>
        </el-col>
      </el-row>
      <el-table :data="displayedAwards" stripe border>
        <el-table-column type="index" width="50"></el-table-column>
        <el-table-column prop="nickname" label="微信昵称"></el-table-column>
        <el-table-column prop="phone" label="电话"></el-table-column>
        <el-table-column prop="gift" label="奖品"></el-table-column>
        <el-table-column prop="created" label="获奖日期">
          <template slot-scope="scope">
            <!-- {{scope.row.created | safeMoment('YYYY-MM-DD HH:mm:ss')}} -->
            {{scope.row.created | moment('YYYY-MM-DD HH:mm:ss')}}
          </template>
        </el-table-column>
        <el-table-column prop="notified" label="是否通知">
          <template slot-scope="scope">
            {{scope.row.notified ? '是' : '否'}}
          </template>
        </el-table-column>
        <el-table-column prop="claimed" label="是否领奖">
          <template slot-scope="scope">
            {{scope.row.claimed ? '是' : '否'}}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button v-show="!scope.row.notified" @click="notify(scope.row, scope.$index)" type="text" size="small"><i class="el-icon-bell"></i>已通知</el-button>
            <el-button v-show="!scope.row.claimed" @click="claim(scope.row, scope.$index)" type="text" size="small"><i class="el-icon-circle-check"></i>已领取</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-row>
        <el-col v-show="!pagination.first"><el-button type="text" @click="previousPage"><i class="el-icon-"></i>上一页</el-button></el-col>
        <el-col v-show="!pagination.last"><el-button type="text" @click="nextPage"><i class="el-icon-"></i>下一页</el-button></el-col>
      </el-row>
    </el-col>
  </el-row>
</template>
<script>
import Api from '@/api'

export default {
  name: 'Award',
  data () {
    return {
      awards: [],
      pagination: {
        first: true,
        last: true,
        number: 0
      },
      phone: '',
      matched: null
    }
  },
  computed: {
    displayedAwards () {
      if (this.phone) {
        let found = []
        let prefix = null
        let suffix = null
        if (this.phone.length <= 3) {
          prefix = this.phone
        } else {
          prefix = this.phone.substring(0, 3)
          if (this.phone.length > 7) {
            suffix = this.phone.substring(7)
          }
        }
        this.awards.forEach((each, index) => {
          if (each.phone.startsWith(prefix) &&
            (suffix === null || each.phone.substring(7).startsWith(suffix))) {
            found.push(each)
          }
        })
        if (this.matched) {
          found.push(this.matched)
        }
        if (found.length === 0 && this.phone.length === 11) {
          this.$message({type: 'info', message: '回车可以搜索全部得奖情况呦。'})
        }
        return found
      } else {
        this.matched = null
        return this.awards
      }
    }
  },
  methods: {
    load (page) {
      // this.awards = [
      //   {id: 1, nickname: 'Sam', phone: '158****6899', gift: '学农奖品', created: 1511620429863, claimed: 1511620439863},
      //   {id: 2, nickname: 'Sam', phone: '152****6899', gift: '学霸奖品', created: 1511620429863, claimed: 1511620439863},
      //   {id: 3, nickname: 'Sam', phone: '158****6898', gift: '学神奖品', created: 1511620429863, claimed: undefined}
      // ]
      let p = page || 0
      Api.award.get({}, {page: p, size: 50}).then(resp => {
        resp.json().then(json => {
          this.awards = json.content
          this.pagination.first = json.first
          this.pagination.last = json.last
          this.pagination.number = json.number
          this.$message({type: 'success', message: '获取成功。'})
        })
      }).catch(err => {
        let reason = {
          status: err.status,
          error: err.error,
          message: err.message
        }
        this.$message({type: 'warning', message: `获取奖品发放失败，请稍后重试。原因：${JSON.stringify(reason)}。`})
      })
    },
    onRefresh () {
      this.load()
    },
    notify (award, index) {
      Api.award.update({id: award.id}, award).then(resp => {
        this.$message('成功标记已经通知领奖。')
      }).catch(err => {
        let reason = {
          status: err.status,
          error: err.error,
          message: err.message
        }
        this.$message({type: 'warning', message: `标记已经通知领奖失败，请稍后重试。原因：‘${JSON.stringify(reason)}’。`})
      })
    },
    claim (award, index) {
      Api.award.save({id: award.id}).then(resp => {
        resp.json().then(json => {
          this.$message({type: 'success', message: '成功标记奖品已领取。'})
          this.awards[index] = json
        })
      }).catch(err => {
        let reason = {
          status: err.status,
          error: err.error,
          message: err.message
        }
        this.$message({type: 'warning', message: `标记奖品已领取失败，请重试。原因：${JSON.stringify(reason)}`})
      })
    },
    searchRemotely () {
      console.log('search by phone remotely')
      if (this.phone.length === 11) {
        Api.award.findByPhone({phone: this.phone}).then(resp => {
          resp.json().then(json => {
            if (json && json.length > 0) {
              this.matched = json
            }
          })
        })
      }
    },
    previousPage () {
      if (this.pagination.number > 0) {
        this.load(this.pagination.number - 1)
      }
    },
    nextPage () {
      this.load(this.pagination.number + 1)
    }
  },
  created: function () {
    this.load()
  }
}
</script>
<style>

</style>
