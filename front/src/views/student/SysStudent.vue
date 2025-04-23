<template>
  <div class="container">
    <el-select v-model="testInfoSelector" style="width: '200px'" placeholder="请选择" @change="handleStuTestInfoChange">
      <el-option
        v-for="item in testInfoSelectorList"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      />
    </el-select>

    <el-table
      :data="singleTestTableData"
      style="width: 100%"
      :cell-style="addClass"
    >
      <el-table-column :class-name="'okTable'" type="index" width="50" />
      <el-table-column :label-class-name="'okTable'" prop="username" label="学号" />
      <el-table-column :label-class-name="'okTable'" prop="userFullName" label="姓名" />
      <el-table-column :label-class-name="'okTable'" prop="testName" label="考试名称" />
      <el-table-column :label-class-name="'okTable'" prop="testTime" label="考试日期" />
      <el-table-column :label-class-name="'okTable'" prop="chinese" label="语文成绩" />
      <el-table-column :label-class-name="'okTable'" prop="math" label="数学成绩" />
      <el-table-column :label-class-name="'okTable'" prop="english" label="英语成绩" />
      <el-table-column :label-class-name="'okTable'" prop="politics" label="政治成绩" />
      <el-table-column :label-class-name="'okTable'" prop="sport" label="体育成绩" />
      <el-table-column :label-class-name="'okTable'" prop="allScore" label="总成绩" />

    </el-table>

    <div style="margin-top: 120px">
      <el-select v-model="classNameSelector" style="width: '200px'" placeholder="请选择" @change="handelClassNameChange">
        <el-option
          v-for="item in classNameSelectorList"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>

      <el-table
        :data="curr_stu_score_list"
        style="width: 100%"
        :cell-style="addClass"
      >
        <el-table-column :class-name="'okTable'" type="index" width="50" />
        <el-table-column :label-class-name="'okTable'" prop="username" label="学号" />
        <el-table-column :label-class-name="'okTable'" prop="userFullName" label="姓名" />
        <el-table-column :label-class-name="'okTable'" prop="testName" label="考试名称" />
        <el-table-column :label-class-name="'okTable'" prop="testTime" label="考试日期" />
        <el-table-column :label-class-name="'okTable'" prop="score" :label=" this.curr_stu_score_name + '成绩'" />

      </el-table>
    </div>
  </div>
</template>

<script>
import {getAllTestInfo, getOneScoreByTestInfoId, getStudentScores} from '@/api/teacher'

export default {
  name: 'SysStudent',
  data() {
    return {
      teacherBaseUrl: '/teacher',
      testInfoSelectorList: [],
      testInfoSelector: '',
      singleTestTableData: [],
      singleClassTableData: [],
      classNameSelectorList: [
        { value: 'chinese', label: '语文' },
        { value: 'math', label: '数学' },
        { value: 'english', label: '英语' },
        { value: 'politics', label: '政治' },
        { value: 'sport', label: '体育' }
      ],
      classNameSelector: '',
      stu_score_list: [],
      stu_chinese_score_list: [],
      stu_math_score_list: [],
      stu_english_score_list: [],
      stu_politics_score_list: [],
      stu_sport_score_list: [],
      curr_stu_score_list: [],
      curr_stu_score_name: ''
    }
  },
  mounted() {
    getAllTestInfo(this.teacherBaseUrl).then(res => {
      res.data.forEach(item => {
        this.testInfoSelectorList.push({
          value: item.id,
          label: item.testName
        })
      })
      this.getCurrStudentScore()
    })
    this.getCurrStudentSingleClassScore()
  },
  methods: {
    handelClassNameChange(classNameValue) {
      this.curr_stu_score_list = []
      this[`stu_${this.classNameSelector}_score_list`].forEach(item => {
        this.curr_stu_score_list.push({
          score: item.score,
          testName: item.testName,
          testTime: item.testTime.split('T')[0],
          username: this.$store.getters.user.username,
          userFullName: this.$store.getters.user.fullName
        })
      })
      this.classNameSelectorList.forEach(item => {
        if (classNameValue === item.value) {
          this.curr_stu_score_name = item.label
          return
        }
      })
    },

    addClass({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 5) {
        if (row.chinese > 60 || row.score > 60) {
          return 'color:green; font-weight: bold; font-size: 20px'
        } else {
          return 'color:red; font-weight: bold; font-size: 20px'
        }
      }
      if (columnIndex === 6) {
        if (row.math > 60) {
          return 'color:green; font-weight: bold; font-size: 20px'
        } else {
          return 'color:red; font-weight: bold; font-size: 20px'
        }
      }
      if (columnIndex === 7) {
        if (row.english > 60) {
          return 'color:green; font-weight: bold; font-size: 20px'
        } else {
          return 'color:red; font-weight: bold; font-size: 20px'
        }
      }
      if (columnIndex === 8) {
        if (row.politics > 60) {
          return 'color:green; font-weight: bold; font-size: 20px'
        } else {
          return 'color:red; font-weight: bold; font-size: 20px'
        }
      }
      if (columnIndex === 9) {
        if (row.sport > 60) {
          return 'color:green; font-weight: bold; font-size: 20px'
        } else {
          return 'color:red; font-weight: bold; font-size: 20px'
        }
      }
      if (columnIndex === 10) {
        return 'font-weight: bold; font-size: 20px'
      }
    },

    handleStuTestInfoChange() {
      this.getCurrStudentScore(this.testInfoSelector)
    },
    getCurrStudentScore(testInfoId) {
      getOneScoreByTestInfoId(this.teacherBaseUrl, this.$store.getters.user.id, testInfoId).then(res => {
        this.singleTestTableData = []
        const scoreInfo = { ...res.data }
        scoreInfo.testTime = res.data.testTime.split('T')[0]
        scoreInfo.username = this.$store.getters.user.username
        scoreInfo.userFullName = this.$store.getters.user.fullName
        scoreInfo.allScore = res.data.chinese + res.data.math + res.data.english + res.data.politics + res.data.sport
        this.singleTestTableData.push(scoreInfo)
        this.testInfoSelector = res.data.testInfoId
      })
    },
    getCurrStudentSingleClassScore() {
      getStudentScores(this.teacherBaseUrl, this.$store.getters.user.id).then(res => {
        this.stu_score_list = res.data
        this.stu_chinese_score_list = res.data.map(item => { return { 'testName': item.testName, 'score': item.chinese, 'testTime': item.testTime } })
        this.stu_math_score_list = res.data.map(item => { return { 'testName': item.testName, 'score': item.math, 'testTime': item.testTime } })
        this.stu_english_score_list = res.data.map(item => { return { 'testName': item.testName, 'score': item.english, 'testTime': item.testTime } })
        this.stu_politics_score_list = res.data.map(item => { return { 'testName': item.testName, 'score': item.politics, 'testTime': item.testTime } })
        this.stu_sport_score_list = res.data.map(item => { return { 'testName': item.testName, 'score': item.sport, 'testTime': item.testTime } })
        this.classNameSelector = 'chinese'
        this.handelClassNameChange(this.classNameSelector)
      })
    }
  }
}
</script>

<style lang="scss">
.container{
  margin-top: 20px;
  margin-left: 20px;
}
.okTable{
  font-size: 18px;
}
</style>
