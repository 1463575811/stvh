<template>
  <div>

    <div style="margin-top: 20px; margin-left: 20px; margin-bottom: 10px;">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">添加考试信息</el-button>
    </div>

    <el-table
      :data="tableData"
      highlight-current-row
      style="width: 100%; margin-left: 15px"
      empty-text="暂无数据"
      @selection-change="handleSelectionChange">

      <el-table-column
        type="selection"
        width="55">
      </el-table-column>
      <el-table-column
        type="index"
        width="50">
      </el-table-column>
      <el-table-column
        prop="testName"
        label="考试名称">
      </el-table-column>
      <el-table-column
        prop="testTime"
        label="考试日期">
      </el-table-column>

      <el-table-column
        prop="enabled"
        label="用户操作">
        <template slot-scope="scope">
          <el-button @click="handleDelete(scope.row)" type="danger" size="small">删除</el-button>
          <el-button @click="handleEdit(scope.row)" type="primary" size="small">编辑</el-button>
        </template>
      </el-table-column>

      <el-table-column
        align="right">
        <template slot="header" slot-scope="scope">
          <el-input
            style="width: 300px;"
            v-model="search"
            clearable
            placeholder="输入关键字搜索">
            <el-button slot="append" icon="el-icon-search" @click="handleSearch()"/>
          </el-input>

        </template>
      </el-table-column>

    </el-table>

    <div style="margin-top: 50px">
      <el-pagination
        background
        layout="prev, pager, next"
        @current-change="handlePage"
        :current-page="page.currentPage"
        :page-size="page.pageSize"
        :total="page.total">
      </el-pagination>
    </div>
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="30%">
      <el-form ref="testInfoForm" :model="testInfo" class="demo-form-inline" :rules="testInfoRules">
        <el-form-item prop="testName" label="考试名称">
          <el-input v-model="testInfo.testName" placeholder="请输入考试名称"></el-input>
        </el-form-item>
        <el-form-item prop="testTime" label="考试时间">
          <el-date-picker type="date" placeholder="选择日期" v-model="testInfo.testTime" style="width: 100%;"></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

  </div>
</template>

<script>

import { getDefaultPage, generateTestInfoShowInfo } from '@/utils/user'
import { addTestInfo, listAllTestInfo, deleteTestInfo} from '@/api/teacher'

export default {
  name: 'TestInfo',
  data() {
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
      dialogTitle: '',
      dialogVisible: false,
      testInfo: {
        id: null,
        testName: '',
        testTime: ''
      },
      testInfoRules: {
        testName: [
          { required: true, message: '请输入考试名称', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        testTime: [
          { required: true, message: '请选择日期', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    submitForm() {
      this.$refs['testInfoForm'].validate((valid) => {
        if (valid) {
          console.log(this.testInfo)
          this.testInfo.testTime = new Date(this.formatDate(this.testInfo.testTime))
          addTestInfo(this.baseUrl, this.testInfo).then(res => {
            this.dialogVisible = false
            this.reloadTable()
            this.$refs['testInfoForm'].resetFields()
          })
        } else {
          this.$message.error('请完善表格信息!')
          return false
        }
      })
    },

    handleEdit(row) {
      this.dialogTitle = '编辑考试信息'
      this.dialogVisible = true
      this.testInfo = { ...row }
    },

    formatDate(date) {
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`

    },
    handleDelete(testInfo) {
      this.$confirm(`是否删除该条信息?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteTestInfo(this.baseUrl, { id: testInfo.id }).then(res => {
          this.reloadTable()
        })

        this.$message({
          type: 'success',
          message: '操作成功!'
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        })
      })
    },
    handleAdd() {
      this.dialogVisible = true
      this.dialogTitle = '添加考试信息'
      this.$nextTick(() => {
        this.$refs['testInfoForm'].resetFields()
      })
    },
    // 搜索按钮
    handleSearch() {
      if (this.search.trim() === '') {
        this.reloadTable()
        return
      }
      listAllTestInfo(this.baseUrl, this.page, this.search).then(res => {
        if (!res.data || res.data.length === 0) {
          this.$message.info('没查询到相关信息, 换个关键词搜索试试吧!')
          return
        }

        this.tableData = generateTestInfoShowInfo(res.data)
        this.page = res.pageInfo
      })
    },

    reloadTable() {
      listAllTestInfo(this.baseUrl, this.page).then(res => {
        this.tableData = generateTestInfoShowInfo(res.data)
        this.page = res.pageInfo
      })
    },

    // 每次多选框有变更, 会同步到data
    handleSelectionChange(val) {
      this.selectedUserIds = val.map(item => {
        return item.id
      })
    },

    handlePage(currentPage) {
      this.page.currentPage = currentPage
      this.reloadTable()
    },

  },
  created() {
    this.page = getDefaultPage()
    this.reloadTable()
  }
}

</script>

<style scoped lang="scss">

</style>
