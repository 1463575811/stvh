<template>
  <div class="dashboard-container">
    <div class="dashboard-text">亲爱的 {{ this.$store.getters.user.fullName }} , 欢迎您使用本系统!</div>

    <div v-if="this.currRole === 'TEACHER'" class="chart_container">
      <div style="flex: 1">
        <div>
          <el-select v-model="testInfoSelector" style="width: '200px'" placeholder="请选择" @change="handelAvgSelectorChange">
            <el-option
              v-for="item in testInfoSelectorList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>
        <div id="avg_score" class="stu_avg_score" />
      </div>

      <div style="flex: 1">
        <el-select v-model="classNameSelector" style="width: '200px'" placeholder="请选择" @change="handelClassNameChange">
          <el-option
            v-for="item in classNameSelectorList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <div id="avg_score_class" class="stu_avg_score" />
      </div>

    </div>
    <div v-if="this.currRole === 'TEACHER'">
      <el-table
        :data="userScoreTable"
        style="width: 100%"
        stripe
        border
        show-summary
        height="400"
      >
        <el-table-column
          type="index"
          width="50"
        />
        <el-table-column property="username" label="学号" />
        <el-table-column property="userFullName" label="姓名" />
        <el-table-column property="testName" label="考试名称" />
        <el-table-column property="testTime" label="考试时间" />
        <el-table-column property="chinese" sortable label="语文" />
        <el-table-column property="math" sortable label="数学" />
        <el-table-column property="english" sortable label="英语" />
        <el-table-column property="politics" sortable label="政治" />
        <el-table-column property="sport" sortable label="体育" />
        <el-table-column property="allScore" sortable label="总分" />
      </el-table>
      <el-button style="float: right; margin-top: 10px" type="primary" @click="handelExportScore">导出成绩结果</el-button>
    </div>

    <div v-if="this.currRole === 'STUDENT'" class="chart_container">
      <div style="flex: 1">
        <div>
          <el-select v-model="testInfoSelector" style="width: '200px'" placeholder="请选择" @change="handleStuTestInfoChange">
            <el-option
              v-for="item in testInfoSelectorList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>
        <div id="stu_recently_score" class="stu_chart_class" />
      </div>
      <div style="flex: 1">
        <el-select v-model="classNameSelector" style="width: '200px'" placeholder="请选择" @change="handelStuClassNameChange">
          <el-option
            v-for="item in classNameSelectorList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>

        <div id="stu_single_score" style="height: 800px;margin-top: 50px" />
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import * as echarts from 'echarts'
import { getAllTestInfo, getStudentScoresByTestInfoId, getOneScoreByTestInfoId, getStudentScores } from '@/api/teacher'
import { updateUserPassword } from '@/api/admin'
import * as XLSX from 'xlsx'

export default {
  name: 'Dashboard',
  data() {
    return {
      currRole: this.$store.getters.roles[0],
      testInfoSelectorList: [],
      testInfoSelector: '',
      classNameSelectorList: [
        { value: 'chinese', label: '语文' },
        { value: 'math', label: '数学' },
        { value: 'english', label: '英语' },
        { value: 'politics', label: '政治' },
        { value: 'sport', label: '体育' }
      ],
      classNameSelector: '',
      teacherBaseUrl: '/teacher',
      chineseScoreList: [],
      mathScoreList: [],
      englishScoreList: [],
      politicsScoreList: [],
      sportScoreList: [],
      userScoreTable: [],
      stu_chinese_score_list: [],
      stu_math_score_list: [],
      stu_english_score_list: [],
      stu_politics_score_list: [],
      stu_sport_score_list: [],
      currStuScore: {
        chinese: '',
        math: '',
        english: '',
        politics: '',
        sport: ''
      }
    }
  },
  mounted() {
    if (this.$store.getters.roles[0] === 'TEACHER') {
      this.initTeacherScoreCharts()
    }
    if (this.$store.getters.roles[0] === 'TEACHER' || this.$store.getters.roles[0] === 'STUDENT') {
      if (this.$store.getters.user.password === '123456') {
        this.$notify({
          title: '警告',
          message: '您现在使用的是默认密码, 请点击该提示设置新的密码!',
          type: 'warning',
          duration: 4000,
          offset: 100,
          onClick: () => {
            this.$prompt('请输入新的登录密码', '更新密码', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              showClose: false,
              showCancelButton: false,
              closeOnClickModal: false,
              closeOnPressEscape: false,
              inputValidator: (value) => {
                if (!value || value === '') {
                  return '请输入新的登录密码!'
                } else if (value.length <= 4 || value.length >= 30) {
                  return '密码长度必须在5~30位之间!'
                } else if (value === '123456') {
                  return '不可以使用默认密码(123456), 请设置一个新密码!'
                }
                return true
              },
              inputErrorMessage: '密码格式不正确!'
            }).then(({ value }) => {
              updateUserPassword('/admin', { 'id': this.$store.getters.user.id, password: value }).then(res => {
                this.$message.success('密码更新成功!')
              })
            }).catch(() => {
              this.$message({
                type: 'info',
                message: '取消输入'
              })
            })
          }
        })
      }
    }
    if (this.$store.getters.roles[0] === 'STUDENT') {
      this.initStudentScoreCharts()
    }
  },
  methods: {
    handelStuClassNameChange(){
      this.reloadStuSingleScore(this.classNameSelector)
    },
    initStudentScoreCharts() {
      getAllTestInfo(this.teacherBaseUrl).then(res => {
        res.data.forEach(item => {
          this.testInfoSelectorList.push({
            value: item.id,
            label: item.testName
          })
        })
        this.reloadStuRecentlyScore(null)
      })
      getStudentScores(this.teacherBaseUrl, this.$store.getters.user.id).then(res => {
        this.stu_score_list = res.data
        this.stu_chinese_score_list = res.data.map(item => { return { 'testName': item.testName, 'score': item.chinese } })

        this.stu_math_score_list = res.data.map(item => { return { 'testName': item.testName, 'score': item.math } })
        this.stu_english_score_list = res.data.map(item => { return { 'testName': item.testName, 'score': item.english } })
        this.stu_politics_score_list = res.data.map(item => { return { 'testName': item.testName, 'score': item.politics } })
        this.stu_sport_score_list = res.data.map(item => { return { 'testName': item.testName, 'score': item.sport } })
        this.classNameSelector = 'chinese'
        this.reloadStuSingleScore('chinese')
      })
    },

    reloadStuSingleScore(className) {
      const chartDom = document.getElementById('stu_single_score')
      const myChart = echarts.init(chartDom)

      const option = {
        title: {
          text: '考试成绩变化图',
          left: 'center'
        },
        xAxis: {
          type: 'category',
          data: this[`stu_${className}_score_list`].map(item => { return item.testName })
        },
        toolbox: {
          feature: {
            saveAsImage: {}
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985'
            }
          }
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: this[`stu_${className}_score_list`].map(item => { return item.score }),
            type: 'line'
          }
        ]
      }

      myChart.setOption(option)
    },
    handleStuTestInfoChange() {
      this.reloadStuRecentlyScore(this.testInfoSelector)
    },
    reloadStuRecentlyScore(testInfoId) {
      const chartDom = document.getElementById('stu_recently_score')
      const myChart = echarts.init(chartDom)
      getOneScoreByTestInfoId(this.teacherBaseUrl, this.$store.getters.user.id, testInfoId).then(res => {
        this.testInfoSelector = res.data.testInfoId

        const allScore = res.data.chinese + res.data.math + res.data.english + res.data.politics + res.data.sport
        const option = {
          title: {
            text: `考试名称: ${res.data.testName}  总分: ${allScore}`,
            subtext: `考试日期: ${res.data.testTime.split('T')[0]}`,
            left: 'center'
          },
          tooltip: {
            trigger: 'item'
          },
          legend: {
            orient: 'vertical',
            left: 'right'
          },
          series: [
            {
              name: '分数',
              type: 'pie',
              radius: '50%',
              data: [
                { value: res.data.chinese, name: '语文' },
                { value: res.data.math, name: '数学' },
                { value: res.data.english, name: '英语' },
                { value: res.data.politics, name: '政治' },
                { value: res.data.sport, name: '体育' }
              ],
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              }
            }
          ]
        }
        myChart.setOption(option)
      })
    },
    // 导出成绩
    handelExportScore() {
      // console.log(this.userScoreTable)
      const data = [['学号', '姓名', '考试名称', '考试时间', '语文', '数学', '英语', '政治', '体育', '总分']]
      let testName = 'empty'
      this.userScoreTable.forEach(item => {
        testName = item.testName
        data.push([item.username, item.userFullName, item.testName, item.testTime, item.chinese, item.math, item.english, item.politics, item.sport, item.allScore])
      })
      const ws = XLSX.utils.aoa_to_sheet(data)
      const wb = XLSX.utils.book_new()
      XLSX.utils.book_append_sheet(wb, ws, 'Sheet1')
      XLSX.writeFile(wb, `${testName}.xlsx`)
    },
    handelClassNameChange(classNameValue) {
      let className = ''
      this.classNameSelectorList.forEach(item => {
        if (classNameValue === item.value) {
          className = item.label
          return
        }
      })

      this.reloadAvgClassCharts(classNameValue, className)
    },
    handelAvgSelectorChange(testInfoId) {
      let testName
      this.testInfoSelectorList.forEach(item => {
        if (item.value === testInfoId) {
          testName = item.label
          return
        }
      })
      this.reloadAvgCharts(testInfoId, testName)
    },
    getAllTestInfo() {
      getAllTestInfo(this.teacherBaseUrl).then(res => {
        res.data.forEach(item => {
          this.testInfoSelectorList.push({
            value: item.id,
            label: item.testName
          })
        })

        this.testInfoSelector = this.testInfoSelectorList[0].value
        this.reloadAvgCharts(this.testInfoSelector, this.testInfoSelectorList[0].label)
      })
    },
    reloadAvgClassCharts(classNameValue, className) {
      const chartDom = document.getElementById('avg_score_class')
      const myChart = echarts.init(chartDom)
      const classBelong = {
        '1': 0,
        '2': 0,
        '3': 0,
        '4': 0,
        '5': 0
      }

      this[`${classNameValue}ScoreList`].forEach(item => {
        if (item >= 90) {
          classBelong['1'] = classBelong['1'] + 1
        } else if (item >= 80 && item < 90) {
          classBelong['2'] = classBelong['2'] + 1
        } else if (item >= 70 && item < 80) {
          classBelong['3'] = classBelong['3'] + 1
        } else if (item >= 60 && item < 70) {
          classBelong['4'] = classBelong['4'] + 1
        } else {
          classBelong['5'] = classBelong['5'] + 1
        }
      })

      const option = {
        title: {
          text: className + ' - 成绩分布',
          left: '35%'
        },
        tooltip: {
        },
        xAxis: {
          data: ['100-90', '89-80', '79-70', '69-60', '不及格']
        },
        yAxis: {},
        series: [{
          name: '成绩',
          type: 'bar',
          label: {
            show: true,
            position: 'top',
            formatter: function(data) {
              return data.value + '人'
            }
          },
          data: [classBelong['1'], classBelong['2'], classBelong['3'], classBelong['4'], classBelong['5']]
        }]
      }
      myChart.setOption(option)
    },

    reloadAvgCharts(testInfoId, testName) {
      const chartDom = document.getElementById('avg_score')
      const myChart = echarts.init(chartDom)
      getStudentScoresByTestInfoId(this.teacherBaseUrl, testInfoId).then(res => {
        if (res.data.length === 0) {
          this.$message.warning('暂无数据!')
        }

        res.data.forEach(item => {
          item.testTime = item.testTime.split('T')[0]
          item.allScore = item.chinese + item.math + item.english + item.politics + item.sport
        })

        this.userScoreTable = res.data

        const chinese_score_arr = res.data.map(item => item.chinese)
        this.chineseScoreList = chinese_score_arr
        const avg_chinese = Number((chinese_score_arr.reduce((sum, value) => sum + value, 0) / chinese_score_arr.length).toFixed(1))

        const math_score_arr = res.data.map(item => item.math)
        this.mathScoreList = math_score_arr
        const avg_math = Number((math_score_arr.reduce((sum, value) => sum + value, 0) / math_score_arr.length).toFixed(1))

        const english_score_arr = res.data.map(item => item.english)
        this.englishScoreList = english_score_arr
        const avg_english = Number((english_score_arr.reduce((sum, value) => sum + value, 0) / english_score_arr.length).toFixed(1))

        const politics_score_arr = res.data.map(item => item.politics)
        this.politicsScoreList = politics_score_arr
        const avg_politics = Number((politics_score_arr.reduce((sum, value) => sum + value, 0) / politics_score_arr.length).toFixed(1))

        const sport_score_arr = res.data.map(item => item.sport)
        this.sportScoreList = sport_score_arr
        const avg_sport = Number((sport_score_arr.reduce((sum, value) => sum + value, 0) / sport_score_arr.length).toFixed(1))

        const option = {
          title: {
            text: testName + ' - 各科平均分',
            left: '35%'
          },
          tooltip: {
          },
          xAxis: {
            data: ['语文', '数学', '英语', '政治', '体育']
          },
          yAxis: {},
          series: [{
            name: '成绩',
            type: 'bar',
            label: {
              show: true,
              position: 'top',
              formatter: function(data) {
                return data.value + '分'
              }
            },
            data: [avg_chinese, avg_math, avg_english, avg_politics, avg_sport]
          }]
        }
        myChart.setOption(option)
        this.classNameSelector = this.classNameSelectorList[0].value
        this.reloadAvgClassCharts(this.classNameSelectorList[0].value, this.classNameSelectorList[0].label)
      }).catch(err => {
        console.log(err)
      })
    },
    initTeacherScoreCharts() {
      this.getAllTestInfo()
      // 表1 各科的平均分 桶装图
      // 表2 饼状图
      // 表3 线性回归散点图, x轴是考试总分数, y轴是考试成绩
    }
  },
  computed: {
    ...mapGetters([
      'name'
    ])
  }
}
</script>

<style lang="scss" scoped>
.dashboard {
  &-container {
    margin: 30px;
  }
  &-text {
    font-size: 30px;
    line-height: 46px;
  }
}
.chart_container{
  margin-top: 10px;
  display: flex;
};
.sandian_score{

};
.stu_chart_class{
  height: 1000px;
};
.avg_score_class{
  height: 600px;
};
.stu_avg_score{
  height: 600px;
}
</style>
