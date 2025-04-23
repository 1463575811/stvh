<template>
  <div>

    <div style="margin-top: 20px; margin-left: 20px; margin-bottom: 10px;">
      <el-button type="primary" icon="el-icon-plus" @click="addManyScore()">批量导入成绩信息</el-button>
    </div>

    <el-table
      :data="tableData"
      highlight-current-row
      style="width: 100%; margin-left: 15px"
      empty-text="暂无数据"
      @selection-change="handleSelectionChange"
    >

      <el-table-column
        type="selection"
        width="55"
      />
      <el-table-column
        type="index"
        width="50"
      />
      <el-table-column
        prop="username"
        label="账号/学号"
        width="180"
      />
      <el-table-column
        prop="fullName"
        label="姓名"
        width="180"
      />
      <el-table-column
        prop="sex"
        label="性别"
        width="180"
      />

      <el-table-column
        prop="grade"
        label="年级"
        width="180"
      />

      <el-table-column
        prop="className"
        label="班级"
        width="180"
      />

      <el-table-column
        prop="age"
        label="年龄"
        width="180"
      />

      <el-table-column
        prop="enabled"
        label="用户操作"
      >
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleShowScore(scope.row)">查看成绩</el-button>
        </template>
      </el-table-column>

      <el-table-column
        align="right"
      >
        <template slot="header" slot-scope="scope">
          <el-input
            v-model="search"
            style="width: 300px;"
            clearable
            placeholder="输入关键字搜索"
          >
            <el-button slot="append" icon="el-icon-search" @click="handleSearch()" />
          </el-input>

        </template>
      </el-table-column>

    </el-table>

    <div style="margin-top: 50px">
      <el-pagination
        background
        layout="prev, pager, next"
        :current-page="page.currentPage"
        :page-size="page.pageSize"
        :total="page.total"
        @current-change="handlePage"
      />
    </div>

    <el-drawer
      title="学生成绩详情"
      :visible.sync="showScoreDetailDrawer"
      direction="rtl"
      size="70%"
    >
      <div style="margin-top: 20px; margin-left: 20px; margin-bottom: 10px;">
        <el-button type="primary" icon="el-icon-plus" @click="addStuScore()">添加学生成绩</el-button>
      </div>
      <el-table
        empty-text="暂无数据"
        :data="studentScoreList"
      >
        <el-table-column
          type="index"
          width="50"
        />
        <el-table-column property="username" label="学号" />
        <el-table-column property="userFullName" label="姓名" />
        <el-table-column property="testName" label="考试名称" />
        <el-table-column property="testTime" label="考试时间" />
        <el-table-column property="chinese" label="语文" />
        <el-table-column property="math" label="数学" />
        <el-table-column property="english" label="英语" />
        <el-table-column property="politics" label="政治" />
        <el-table-column property="sport" label="体育" />
        <el-table-column
          label="用户操作"
        >
          <template slot-scope="scope">
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>

    <el-dialog
      title="学生成绩上传"
      :visible.sync="dialogVisible"
      width="30%"
    >
      <div>
        <el-upload
          ref="uploadExcel"
          :file-list="fileList"
          :on-exceed="handleExceed"
          :before-upload="beforeUpload"
          :on-error="uploadError"
          action="#"
          :http-request="uploadFile"
          :limit="1"
          :on-change="handleChange"
          :auto-upload="false"
        >
          <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
          <div slot="tip" class="el-upload__tip">只能上传xlsx文件，且不超过10m</div>
        </el-upload>

      </div>

      <div slot="footer" class="dialog-footer">
        请选择考试信息:
        <el-select v-model="testInfoSelector" style="width: 200px" placeholder="请选择">
          <el-option
            v-for="item in testInfoSelectorList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>

        <el-button style="margin-left: 10px;" type="success" @click="handleUploadFile">上传到服务器</el-button>

        <el-button type="success" @click="downloadExcel">下载模板</el-button>
        <div style="height: 30px" />
      </div>
    </el-dialog>

    <el-dialog
      title="新增学生成绩"
      :visible.sync="addScoreDialogVisible"
      width="30%"
      @closed="handleAddStuScoreDialogClose"
    >
      <div>

        <el-form ref="stuScoreForm" :inline="true" :model="stuScore" class="demo-form-inline" :rules="stuScoreRules">
          <el-form-item style="width: 100%" prop="testInfoSelector" label="考试名称">
            <el-select v-model="stuScore.testInfoSelector" :disabled="isUpdate ? true : false" style="width: 100%" placeholder="请选择">
              <el-option
                v-for="item in testInfoSelectorList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item prop="username" label="学生学号">
            <el-input v-model="stuScore.username" :disabled="true" placeholder="请输入学生学号" />
          </el-form-item>
          <el-form-item prop="fullName" label="学生姓名">
            <el-input v-model="stuScore.fullName" :disabled="true" placeholder="请输入学生姓名" />
          </el-form-item>
          <el-form-item prop="chinese" label="语文成绩">
            <el-input v-model="stuScore.chinese" placeholder="请输入语文成绩" />
          </el-form-item>
          <el-form-item prop="math" label="数学成绩">
            <el-input v-model="stuScore.math" placeholder="请输入数学成绩" />
          </el-form-item>
          <el-form-item prop="english" label="英语成绩">
            <el-input v-model="stuScore.english" placeholder="请输入英语成绩" />
          </el-form-item>
          <el-form-item prop="politics" label="政治成绩">
            <el-input v-model="stuScore.politics" placeholder="请输入政治成绩" />
          </el-form-item>
          <el-form-item prop="sport" label="体育成绩">
            <el-input v-model="stuScore.sport" placeholder="请输入数学成绩" />
          </el-form-item>
        </el-form>

      </div>

      <div slot="footer" class="dialog-footer">

        <el-button @click="addScoreDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="addStuStore">确 定</el-button>
        <div style="height: 30px" />
      </div>
    </el-dialog>
  </div>
</template>

<script>

import { getDefaultPage } from '@/utils/user'
import {
  generateStudentShowInfo,
  listAllStudents,
  addStudentScoreExcelUpload,
  downloadAddStudentScoreExcelTemp,
  getAllTestInfo, getStudentScores, deleteStudentScore, addStudentScore
} from '@/api/teacher'

export default {
  name: 'SysTeacher',
  data() {
    const valiNum = (rule, value, callback) => {
      if (!isNaN(value)) {
        if (value >= 0 && value <= 100) {
          callback()
        } else {
          callback(new Error('成绩不能小于0或者大于100!'))
        }
      } else {
        callback(new Error('成绩只能是数字!'))
      }
    }

    return {
      rootUrl: 'localhost"' + process.env.port,
      baseUrl: '/teacher',
      tableData: [],
      search: '',
      page: getDefaultPage(),
      selectedUserIds: [],
      showScoreDetailDrawer: false,
      fileList: [],
      studentScoreList: [],
      dialogVisible: false,
      testInfoSelector: '',
      testInfoSelectorList: [],
      addScoreDialogVisible: false,
      stuScore: {
        id: '',
        testInfoSelector: '',
        userId: '',
        testInfoId: '',
        username: '',
        fullName: '',
        chinese: '',
        math: '',
        english: '',
        politics: '',
        sport: ''
      },
      isUpdate: false,
      stuScoreRules: {
        testInfoSelector: [
          { required: true, message: '请选择考试信息', trigger: 'blur' }
        ],
        username: [
          { required: true, message: '请输入学生学号', trigger: 'blur' }
        ],
        fullName: [
          { required: true, message: '请输入学生姓名', trigger: 'blur' }
        ],
        chinese: [
          { required: true, message: '请输入语文成绩', trigger: 'blur' },
          { validator: valiNum, trigger: 'blur' }
        ],

        math: [
          { required: true, message: '请输入数学成绩', trigger: 'blur' },
          { validator: valiNum, trigger: 'blur' }
        ],
        english: [
          { required: true, message: '请输入英语成绩', trigger: 'blur' },
          { validator: valiNum, trigger: 'blur' }
        ],
        politics: [
          { required: true, message: '请输入政治成绩', trigger: 'blur' },
          { validator: valiNum, trigger: 'blur' }
        ],
        sport: [
          { required: true, message: '请输入体育成绩', trigger: 'blur' },
          { validator: valiNum, trigger: 'blur' }
        ]
      },
      currUpdateScoreUser: {}
    }
  },
  created() {
    this.page = getDefaultPage()
    this.reloadTable()
  },
  methods: {
    handleAddStuScoreDialogClose() {
      this.stuScore = {
        id: '',
        testInfoSelector: '',
        userId: this.currUpdateScoreUser.id,
        testInfoId: '',
        username: this.currUpdateScoreUser.username,
        fullName: this.currUpdateScoreUser.fullName,
        chinese: '',
        math: '',
        english: '',
        politics: '',
        sport: ''
      }
      this.$refs['stuScoreForm'].clearValidate()
    },
    handleEdit(scoreInfo) {
      const _scoreInfo = { ...scoreInfo }
      this.fileList = []
      this.testInfoSelector = ''
      this.testInfoSelectorList = []
      getAllTestInfo(this.baseUrl).then(res => {
        res.data.forEach(item => {
          this.testInfoSelectorList.push({
            value: item.id,
            label: item.testName
          })
        })
        this.isUpdate = true
        this.stuScore['testInfoSelector'] = _scoreInfo.testInfoId
        this.stuScore['id'] = _scoreInfo.id
        this.stuScore['chinese'] = _scoreInfo.chinese
        this.stuScore['math'] = _scoreInfo.math
        this.stuScore['english'] = _scoreInfo.english
        this.stuScore['politics'] = _scoreInfo.politics
        this.stuScore['sport'] = _scoreInfo.sport
        this.stuScore['username'] = this.currUpdateScoreUser.username
        this.stuScore['userId'] = this.currUpdateScoreUser.id
        this.stuScore['fullName'] = this.currUpdateScoreUser.fullName
        this.addScoreDialogVisible = true
      })
    },
    addStuStore() {
      this.$refs['stuScoreForm'].validate((valid) => {
        if (valid) {
          this.stuScore['testInfoId'] = this.stuScore.testInfoSelector
          addStudentScore(this.baseUrl, this.stuScore).then(res => {
            this.handleShowScore({ id: this.stuScore.userId, username: this.stuScore.username, fullName: this.stuScore.fullName })
            this.handleAddStuScoreDialogClose()
            this.addScoreDialogVisible = false
            this.$message.success('操作成功!')
          })
        } else {
          this.$message.error('请完善表格信息!')
          return false
        }
      })
    },
    // 删除单个考试记录
    handleDelete(score) {
      this.$confirm(`是否删除该考试记录?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteStudentScore(this.baseUrl, { id: score.id }).then(res => {
          this.handleShowScore({ id: this.stuScore.userId, username: this.stuScore.username, fullName: this.stuScore.fullName })
          this.$message.success('操作成功!')
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        })
      })
    },
    handleChange(file, fileList) {
      // 这里可以处理文件添加的逻辑，如更新 fileList
      this.fileList = fileList
    },

    // 搜索按钮
    handleSearch() {
      if (this.search.trim() === '') {
        this.reloadTable()
        return
      }
      listAllStudents(this.baseUrl, this.$store.getters.user.id, this.search, this.page).then(res => {
        if (!res.data || res.data.length === 0) {
          this.$message.info('没查询到相关信息, 换个关键词搜索试试吧!')
          return
        }

        this.tableData = generateStudentShowInfo(res.data)
        this.page = res.pageInfo
      })
    },

    // 下载excel模板
    downloadExcel() {
      downloadAddStudentScoreExcelTemp(this.baseUrl).then(res => {
        const href = URL.createObjectURL(res)
        const link = document.createElement('a')
        link.download = '用户添加模板.xlsx'
        link.href = href
        link.click()
        document.body.removeChild(link)
        URL.revokeObjectURL(href)
      }).catch(err => {
        console.log(err)
      })
    },

    reloadTable() {
      listAllStudents(this.baseUrl, this.$store.getters.user.id, this.search, this.page).then(res => {
        this.tableData = generateStudentShowInfo(res.data)
        this.page = res.pageInfo
      })
    },

    // 每次多选框有变更, 会同步到data
    handleSelectionChange(val) {
      this.selectedUserIds = val.map(item => {
        return item.id
      })
    },
    handleExceed(files, fileList) {
      this.$message.error('文件超出限制，请重试！')
    },

    // 上传失败提示
    uploadError(response, file, fileList) {
      this.$message.error('上传失败，请重试！')
    },

    // 上传前限制文件类型，文件大小
    beforeUpload(file) {
      const nameStrArr = file.name.split('.')
      const fileLastname = nameStrArr[nameStrArr.length - 1].toLowerCase()
      const extension = fileLastname === 'xlsx' || fileLastname === 'xls'
      if (!extension) {
        this.$message.warning('上传文件只能是 xlsx 格式!')
        return false
      }

      const isLt10M = file.size / 1024 / 1024 < 10
      if (!isLt10M) {
        this.$message.warning('上传附件大小不能超过 10MB!')
        return false
      }
      this.uploadFileName = file.name
      this.fileSuffixType = fileLastname
      return true
    },

    // 添加单个学生成绩
    addStuScore() {
      this.fileList = []
      this.testInfoSelector = ''
      this.testInfoSelectorList = []
      getAllTestInfo(this.baseUrl).then(res => {
        res.data.forEach(item => {
          this.testInfoSelectorList.push({
            value: item.id,
            label: item.testName
          })
        })
        this.stuScore['username'] = this.currUpdateScoreUser.username
        this.stuScore['userId'] = this.currUpdateScoreUser.id
        this.stuScore['fullName'] = this.currUpdateScoreUser.fullName
        this.isUpdate = false
        this.addScoreDialogVisible = true
      })
    },
    // 批量导入角色
    addManyScore() {
      this.fileList = []
      this.testInfoSelector = ''
      this.testInfoSelectorList = []
      getAllTestInfo(this.baseUrl).then(res => {
        res.data.forEach(item => {
          this.testInfoSelectorList.push({
            value: item.id,
            label: item.testName
          })
        })
        this.dialogVisible = true
      })
    },

    handlePage(currentPage) {
      this.page.currentPage = currentPage
      this.reloadTable()
    },
    handleShowScore(rowInfo) {
      this.currUpdateScoreUser = rowInfo
      getStudentScores(this.baseUrl, rowInfo.id).then(res => {
        res.data.forEach(item => {
          item.testTime = item.testTime.split('T')[0]
        })
        this.studentScoreList = res.data
      })
      this.showScoreDetailDrawer = true
    },

    // 上传前检查
    handleUploadFile() {
      if (this.fileList.length === 0) {
        this.$message.warning('请先选择文件')
        return
      }

      if (this.testInfoSelector.length === 0 || this.testInfoSelector === '') {
        this.$message.warning('请先选择要上传的考试信息')
        return
      }

      // 调用 submit 触发自定义上传
      this.$refs.uploadExcel.submit()
    },
    // 上传文件
    uploadFile(item) {
      const { file } = item // 获取文件对象

      // 创建 FormData
      const formData = new FormData()
      formData.append('file', file) // 追加文件
      formData.append('testInfoId', this.testInfoSelector) // 追加其他参数

      addStudentScoreExcelUpload(this.baseUrl, formData).then(response => {
        // 清空上传的文件缓存
        this.$refs.uploadExcel.clearFiles()

        this.$message.success('操作成功!')
        this.dialogVisible = false
        this.reloadTable()
      }).catch(() => {
        this.$refs.uploadExcel.clearFiles()
      })
      // 处理成功
      this.fileList = [] // 清空文件列表
    }

  }
}

</script>

<style scoped lang="scss">
</style>
