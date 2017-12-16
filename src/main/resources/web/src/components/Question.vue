<template>
  <el-row>
    <el-row>
      <el-col :xs="{span:22,offset:1}" :lg="{span:20,offset:2}">
        <h2>题库管理</h2>
        <el-row class="ops">
          <el-col :xs="{span:4,offset:18}" :lg="{span:4,offset:17}">
            <el-button @click="newLevel" type="success" size="small"><i class="el-icon-plus"></i>新建</el-button>
            <el-button type="primary" size="small" @click="onRefresh"><i class="el-icon-refresh"></i>刷新</el-button>
          </el-col>
        </el-row>
        <el-table ref="levelTable" :data="questions" style="width: 100%" border :row-class-name="rowClassName">
          <el-table-column type="index" width="50"></el-table-column>
          <el-table-column prop="title" label="主题"></el-table-column>
          <el-table-column prop="level" label="级别">
            <template slot-scope="scope">
              {{scope.row.level | levelLabel}}
            </template>
          </el-table-column>
          <el-table-column prop="active" label="状态">
            <template slot-scope="scope">
              <i :class="scope.row.active ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
            </template>
          </el-table-column>
          <el-table-column prop="created" label="创建时间">
            <template slot-scope="scope">
              {{scope.row.created | moment('YYYY-MM-DD HH:mm:ss')}}
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button @click="editLevel(scope.row, scope.$index)" type="primary" plain size="small"><i class="el-icon-edit"></i>编辑</el-button>
              <el-button @click="deleteLevel(scope.row, scope.$index)" type="danger" plain size="small"><i class="el-icon-delete"></i>删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <div class="hr"></div>
    <el-row>
      <el-col :xs="{span:22,offset:1}" :lg="{span:20,offset:2}">
        <div class="detail-panel">
          <div class="panel-header">
            <span>题库详情</span>
          </div>
          <div class="panel-content">
            <el-form id="levelForm" ref="levelForm" :model="currentLevel" :rules="levelRules">
              <el-form-item label="主题" prop="title">
                <el-input ref="titleInput" v-model="currentLevel.title" placeholder="主题"></el-input>
              </el-form-item>
              <el-form-item label="级别" prop="level">
                <el-select v-model="currentLevel.level" placeholder="级别">
                  <el-option v-for="each in levelOptions" :key="each.value" :label="each.label" :value="each.value"></el-option>
                  <!-- <el-option label="学民" value="1"></el-option>
                  <el-option label="学霸" value="2"></el-option>
                  <el-option label="学神" value="3"></el-option> -->
                </el-select>
              </el-form-item>
              <el-form-item label="状态" prop="active">
                <el-switch v-model="currentLevel.active" active-color="#67c23a" inactive-color="#e6ebf5"></el-switch>
              </el-form-item>
              <el-table :data="currentLevel.questions" stripe>
                <el-table-column prop="question" label="题干" min-width="38">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.question" placeholder="题干"></el-input>
                  </template>
                </el-table-column>
                <el-table-column prop="answers[0]" label="选项一" min-width="13">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.answers[0]" placeholder="选项一"></el-input>
                  </template>
                </el-table-column>
                <el-table-column prop="answers[1]" label="选项二" min-width="13">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.answers[1]" placeholder="选项二"></el-input>
                  </template>
                </el-table-column>
                <el-table-column prop="answers[2]" label="选项三" min-width="13">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.answers[2]" placeholder="选项三"></el-input>
                  </template>
                </el-table-column>
                <el-table-column prop="answer" label="答案" min-width="13">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.answer" placeholder="答案"></el-input>
                  </template>
                </el-table-column>
                <el-table-column min-width="10">
                  <template slot-scope="scope">
                    <el-button type="text" size="small" @click="addQuestion"><i class="el-icon-circle-plus"></i></el-button>
                    <el-button type="text" size="small" @click="deleteQuestion(scope.row, scope.$index)"><i class="el-icon-remove"></i></el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-form-item>
                <el-button type="primary" size="small" @click="saveLevel">保存</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </el-col>
    </el-row>
  </el-row>
</template>
<script>
import '@/utils.js'
import Api from '@/api'

export default {
  name: 'Question',
  data () {
    return {
      questions: [],
      currentLevel: {
        id: null,
        title: '',
        level: '',
        active: false,
        questions: [],
        created: new Date()
      },
      isNewLevel: false,
      isDirtyLevel: false,
      levelRules: {
        title: [
          {required: true, message: '请输入题库主题', trigger: 'blur'}
        ],
        level: [
          {required: true, type: 'number', min: 1, max: 3, message: '请选择题库难度级别', trigger: 'change'}
        ]
      },
      levelOptions: [
        {label: '学民', value: 1},
        {label: '学霸', value: 2},
        {label: '学神', value: 3}
      ]
    }
  },
  methods: {
    rowClassName ({row, rowIndex}) {
      return row.active ? 'active-row' : ''
    },
    newLevel () {
      console.log('newLevel')
      let aLevel = this.createLevel()
      this.currentLevel = aLevel
      this.isNewLevel = true
      this.$refs.titleInput.focus()
    },
    createLevel () {
      return (function () {
        return {
          id: null,
          title: '',
          level: '',
          active: false,
          questions: [
            {question: '', answers: ['', '', ''], answer: ''}
          ],
          created: new Date()
        }
      })()
    },
    editLevel (row, rowIndex) {
      if (row.questions.length === 0) {
        row.questions.push({question: '', answers: ['', '', ''], answer: ''})
      }
      this.currentLevel = row
      this.isNewLevel = false
      this.$refs.titleInput.focus()
    },
    deleteLevel (row, rowIndex) {
      this.$confirm(`此操作将删除题库’${row.title}‘，您确定删除吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        Api.question.delete({id: row.id}).then(resp => {
          this.questions.splice(rowIndex, 1)
          this.isDirtyLevel = true
          this.$message({type: 'success', message: '题库删除成功。'})
        }).catch(err => {
          let reason = {
            status: err.status,
            error: err.error,
            message: err.message
          }
          this.$message({type: 'warning', message: `题库删除失败，请稍后重试。原因：${JSON.stringify(reason)}`})
        })
      }).catch(() => {
        this.$message({type: 'info', message: '操作取消。'})
      })
    },
    saveLevel () {
      this.$refs.levelForm.validate((valid) => {
        if (valid) {
          let questionsValid = this.validQuestions(this.currentLevel)
          if (questionsValid.valid) {
            if (this.currentLevel.active) {
              // check no more than one active in the same level
              let activeLevel = null
              this.questions.forEach(each => {
                if (each.level === this.currentLevel.level && each.active) {
                  activeLevel = each
                  return false
                }
              })
              if (activeLevel !== null && activeLevel.id !== this.currentLevel.id) {
                this.$notify.error({
                  title: '错误',
                  message: `每个级别只能启用一个题库，当前启用的题库是${activeLevel.title}。`,
                  duration: 0
                })
                console.log('more than one active in level ' + this.currentLevel.level)
                return false
              }
            }
            if (this.isNewLevel) {
              // create a new level
              Api.question.save({}, this.currentLevel).then(resp => {
                console.log('question.post success')
                resp.json().then(q => {
                  this.questions.push(q)
                  this.currentLevel = this.createLevel()
                  this.isNewLevel = false
                  this.isDirtyLevel = false
                  this.$message({type: 'success', message: '新题库保存成功。'})
                }).catch(err => {
                  console.log(`question.post return non-JSON body: ${resp.body}, error: ${JSON.stringify(err)}`)
                })
              })
            } else {
              // update current level
              Api.question.update({id: this.currentLevel.id}, this.currentLevel).then(resp => {
                console.log('question.put success')
                resp.json().then(q => {
                  this.currentLevel = this.createLevel()
                  this.isDirtyLevel = false
                  this.$message({type: 'success', message: '题库更新成功。'})
                }).catch(err => {
                  console.log(`question.put return non-JSON body: ${resp.body}, error: ${JSON.stringify(err)}`)
                })
              }).catch(err => {
                let reason = {
                  status: err.status,
                  error: err.error,
                  message: err.message
                }
                this.$message({type: 'warning', message: `删除题库失败，请稍后重试。原因：${JSON.stringify(reason)}`})
              })
            }
            // this.$refs.levelForm.resetFields()
          } else {
            console.log('question fields required')
            this.$notify.error({title: '错误', message: questionsValid.message, duration: 0})
            return false
          }
        } else {
          console.log('level form invalid')
          return false
        }
      })
    },
    validQuestions (level) {
      if (level.questions && level.questions.length > 0) {
        let valid = true
        let message = ''
        level.questions.forEach(element => {
          if (element.question === '' || element.answer === '' ||
            element.answers === null || element.answers.length !== 3 ||
            element.answers[0] === '' || element.answers[1] === '' || element.answers[2] === '') {
            message = '题目列表以及每个题目的题干、选项、答案都不能为空。题目标题：' + element.question
            valid = false
          }
          if (element.answers.indexOf(element.answer) === -1) {
            message = `题目‘${element.question}’的答案‘${element.answer}’不在选项范围内。`
            valid = false
          }
          return valid
        })
        return {valid: valid, message: message}
      }
      return {valid: false, message: '题库必须有至少一个题目。'}
    },
    addQuestion () {
      this.currentLevel.questions.push({question: '', answers: ['', '', ''], answer: ''})
    },
    deleteQuestion (question, index) {
      this.$confirm('您确定删除当前题目吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.currentLevel.questions.splice(index, 1)
      }).catch(() => {
        this.$message({type: 'info', message: '操作取消。'})
      })
    },
    onRefresh () {
      this.load()
    },
    load () {
      // this.questions = [
      //   {title: '19大学民题目', level: 1, active: true, questions: [{title: '红色的英文单词是？', answers: ['Red', 'Green', 'Blue'], answer: 'Red'}], created: 1511620409863},
      //   {title: '19大学霸题目', level: 2, active: true, questions: [], created: 1511620419863},
      //   {title: '19大学神题目', level: 3, active: false, questions: [], created: 1511620429863}
      // ]
      Api.question.get().then(resp => {
        console.log('question.get success')
        resp.json().then(json => {
          this.questions = json
          this.$message({type: 'success', message: '获取成功。'})
        })
      }).catch(err => {
        let reason = {
          status: err.status,
          error: err.error,
          message: err.message
        }
        this.$message({type: 'warning', message: `获取题库失败，请稍后重试（${JSON.stringify(reason)}）。`})
      })
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
.el-table .active-row {
  background: #e0ffe0;
}
.hr {
  height: 0;
  border: 1px solid #ebebeb;
  margin: 15px 0;
}
.detail-panel {
  border: 1px solid #ebebeb;
  border-radius: 3px;
}
.detail-panel .panel-header {
  background-color: #ebebeb;
  padding: 15px;
}
.detail-panel .panel-content {
  padding: 15px;
}
#levelForm {
  width: 80%;
  margin: 0 auto;
}
</style>
