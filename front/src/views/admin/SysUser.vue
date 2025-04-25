<template>
  <div style="padding-right: 20px">

    <div style="margin-top: 20px; margin-left: 20px; margin-bottom: 10px;">
      <el-button type="danger" icon="el-icon-delete" @click="deleteMany">批量删除</el-button>
      <el-button type="primary" icon="el-icon-plus" @click="addMany()">批量导入</el-button>
      <el-button type="primary" icon="el-icon-document" @click="exportMany()"> 导出用户信息</el-button>
    </div>

    <el-table
      :data="tableData"
      highlight-current-row
      style="width: 100%; margin-left: 15px"
      :row-class-name="tableRowClassName"
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
        prop="tel"
        label="联系方式"
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
        prop="roleName"
        label="权限"
        width="180"
      />

      <el-table-column
        prop="enabled"
        label="用户登录状态"
      >
        <template v-slot="scope">
          <el-switch
            :value="scope.row.enabled"
            active-color="#13ce66"
            inactive-color="#ff4949"
            @change="changeUserStatus(scope.row)"
          />
        </template>
      </el-table-column>

      <el-table-column
        prop="enabled"
        label="用户操作"
      >
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleDelete(scope.row)">删除</el-button>
          <el-button type="text" size="small" @click="handleResetPassword(scope.row)">重置密码</el-button>
        </template>
      </el-table-column>

      <el-table-column
        align="right"
      >
        <template slot="header" slot-scope="scope">
          <el-input
            v-model="search"
            style="width: 300px"
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

    <el-dialog
      title="文件上传"
      :visible.sync="dialogVisible"
      width="30%"
    >

      <div>
        <el-upload
          ref="uploadExcel"
          style="display: inline;margin-left: 25%"
          action
          drag
          :show-file-list="false"
          :http-request="uploadFile"
          :file-list="fileList"
          :limit="1"
          :on-exceed="handleExceed"
          :before-upload="beforeUpload"
          :on-error="uploadError"
        >
          <i class="el-icon-upload" />
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <div slot="tip" class="el-upload__tip" style="margin-left: 25%">只能上传xlsx文件，且不超过10m</div>
        </el-upload>
      </div>
      <span />
      <span slot="footer" class="dialog-footer">

        <el-button type="success" style="float: left" @click="downloadAddUserExcelTemp">下载模板</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import {
  changeUserStatus,
  deleteUser,
  listAllUsers,
  deleteBunchUser,
  addUserExcelUpload,
  downloadAddUserExcelTemp,
  searchUser,
  resetUserLoginPassword, exportUserInfo
} from '@/api/admin'
import { getDefaultPage, generateUserShowInfo } from '@/utils/user'
import * as XLSX from 'xlsx'

export default {
  name: 'SysUser',
  data() {
    return {
      rootUrl: 'localhost"' + process.env.port,
      baseUrl: '/admin',
      tableData: [],
      search: '',
      page: getDefaultPage(),
      selectedUserIds: [],
      dialogVisible: false,
      fileList: []
    }
  },

  created() {
    this.page = getDefaultPage()
    this.reloadTable()
  },

  methods: {
    exportMany() {
      exportUserInfo(this.baseUrl).then(res => {
        const data = [['学号', '姓名', '性别', '联系方式', '出生日期', '年级', '班级', '权限']]
        const testName = '用户信息表'
        res.data.forEach(item => {
          data.push([item.username, item.fullName, item.sex, item.tel, item.birthDate, item.grade, item.className, item.roleName])
        })
        const ws = XLSX.utils.aoa_to_sheet(data)
        const wb = XLSX.utils.book_new()
        XLSX.utils.book_append_sheet(wb, ws, 'Sheet1')
        XLSX.writeFile(wb, `${testName}.xlsx`)
      })
    },
    handleResetPassword(row) {
      this.$confirm(`是否重置该用户的登录密码?  (重置后的默认登陆密码为123456)`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        resetUserLoginPassword(this.baseUrl, { id: row.id }).then(res => {
          this.$message({
            type: 'success',
            message: '操作成功!'
          })
          this.reloadTable()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        })
      })
    },
    // 下载excel模板
    downloadAddUserExcelTemp() {
      downloadAddUserExcelTemp(this.baseUrl).then(res => {
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

    // 上传文件
    uploadFile(item) {
      const formData = new FormData()
      formData.append('file', item.file)

      addUserExcelUpload(this.baseUrl, formData).then(response => {
        // 清空上传的文件缓存
        this.$refs.uploadExcel.clearFiles()

        this.$message.success('操作成功!')
        this.dialogVisible = false
        this.reloadTable()
      }).catch(err => {
        this.$refs.uploadExcel.clearFiles()
      })
    },

    // 搜索按钮
    handleSearch() {
      if (this.search.trim() === '') {
        this.reloadTable()
        return
      }
      searchUser(this.baseUrl, this.search, getDefaultPage()).then((res) => {
        if (!res.data || res.data.length === 0) {
          this.$message.info('没查询到相关信息, 换个关键词搜索试试吧!')
          return
        }

        this.tableData = generateUserShowInfo(res.data)
        this.page = res.pageInfo
      })
    },

    reloadTable() {
      listAllUsers(this.baseUrl, this.page).then(res => {
        this.tableData = generateUserShowInfo(res.data)
        console.log(this.tableData)
        this.page = res.pageInfo
      })
    },

    changeUserStatus(user) {
      const userCurrStatus = user.enabled ? '禁用' : '起用'
      this.$confirm(`是否 ${userCurrStatus} 该用户的登录功能?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        changeUserStatus(this.baseUrl, { id: user.id, enabled: user.enabled }).then(res => {
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
    handlePage(currentPage) {
      this.page.currentPage = currentPage
      this.reloadTable()
    },

    // 根据用户权限调整表格背景色
    tableRowClassName({ row, rowIndex }) {
      if (row.roleNames[0] === 'STUDENT') {
        return 'student-row'
      } else if (row.roleNames[0] === 'TEACHER') {
        return 'teacher-row'
      } else if (row.roleNames[0] === 'ADMIN') {
        return 'admin-row'
      }
    },

    // 删除单个用户
    handleDelete(user) {
      this.$confirm(`是否删除该用户?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if (user.username == this.$store.getters.name) {
          this.$message({
            type: 'error',
            message: '不可以删除当前角色'
          })
          return
        }

        deleteUser(this.baseUrl, { id: user.id }).then(res => {
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

    // 批量删除
    deleteMany() {
      if (this.selectedUserIds.length <= 0) {
        this.$message({
          type: 'error',
          message: '请先勾选角色'
        })
        return
      }

      this.$confirm(`是否删所选用户?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'

      }).then(() => {
        if (this.selectedUserIds.length > 0 && this.selectedUserIds.includes(this.$store.getters.user.id)) {
          this.$message({
            type: 'error',
            message: '不可以删除当前角色'
          })
          return
        }

        deleteBunchUser(this.baseUrl, { ids: this.selectedUserIds }).then(res => {
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

    // 每次多选框有变更, 会同步到data
    handleSelectionChange(val) {
      this.selectedUserIds = val.map(item => {
        return item.id
      })
      console.log(this.selectedUserIds)
    },

    // 批量导入角色
    addMany() {
      this.dialogVisible = true
    }
  }
}

</script>

<style scoped lang="css">

.upload-demo{
  align-self: center;
}
.el-table /deep/ .student-row {
  background: #f0f9eb;

}

.el-table /deep/ .teacher-row {
  background: oldlace;
}

.el-table /deep/ .admin-row {
  background: #f9ebeb;
}

</style>
