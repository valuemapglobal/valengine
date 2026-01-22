<template>
  <el-dialog title="选择用户" :visible.sync="dialogVisible" width="1026px">
    <el-form
      label-position="left"
      :model="queryParams"
      :rules="rules"
      ref="ruleForm"
      label-width="100px"
      class="demo-ruleForm"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="用户昵称" prop="userName">
            <el-input
              size="medium"
              placeholder="请输入用户名称"
              clearable
              v-model="queryParams.userName"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="手机号码" prop="phonenumber">
            <el-input
              size="medium"
              clearable
              placeholder="请输入手机号码"
              v-model="queryParams.phonenumber"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-table
      :header-cell-style="{
        background: 'rgba(230,238,252,0.3)',
      }"
      @row-click="clickRow"
      ref="table"
      :data="userList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column
        label="用户名称"
        prop="userName"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="用户昵称"
        prop="nickName"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="邮箱"
        prop="email"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="手机"
        prop="phonenumber"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success">正常</el-tag>
          <el-tag v-else type="warning">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination
        v-show="total > 0"
        :current-page.sync="queryParams.pageNum"
        :page-size="queryParams.pageSize"
        background
        layout="prev, pager, next"
        :total="total"
      >
      </el-pagination>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button class="btn" @click="handleSelectUser">确认添加</el-button>
      <el-button @click="dialogVisible = false">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>
import {
  authUserSelectAll,
  unallocatedList,
} from '@/api/system/roleManagement.js'
export default {
  data() {
    return {
      // 遮罩层
      loading: true,
      dialogVisible: false,
      rules: {
        name: [
          { required: true, message: '请输入活动名称', trigger: 'blur' },
          { min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' },
        ],
      },
      userList: [],
      total: 0,
      // 选中用户组
      userIds: [],
      // 非多个禁用
      multiple: true,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        roleId: this.$route.query.roleId,
        userName: undefined,
        phonenumber: undefined,
      },
    }
  },
  watch: {
    queryParams: {
      handler() {
        this.init()
      },
      deep: true,
    },
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      unallocatedList(this.queryParams).then((res) => {
        this.userList = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    show() {
      this.dialogVisible = !this.dialogVisible
    },
    // 保存选中的数据编号
    getRowKey(row) {
      return row.roleId
    },
    /** 单击选中行数据 */
    clickRow(row) {
      this.$refs.table.toggleRowSelection(row)
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.userIds = selection.map((item) => item.userId)
      this.multiple = !selection.length
    },
    /** 选择授权用户操作 */
    handleSelectUser() {
      const roleId = this.queryParams.roleId
      const userIds = this.userIds.join(',')
      if (userIds == '') {
        this.$message({
          type: 'error',
          message: '请选择要分配的用户!',
        })
        return
      }
      authUserSelectAll({ roleId: roleId, userIds: userIds }).then((res) => {
        if (res.code === 200) {
          this.$message({
            type: 'success',
            message: '操作成功!',
          })
          this.$emit('ok')
          this.show()
        }
      })
    },
    parseTime(time, pattern) {
      if (arguments.length === 0 || !time) {
        return null
      }
      const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}'
      let date
      if (typeof time === 'object') {
        date = time
      } else {
        if (typeof time === 'string' && /^[0-9]+$/.test(time)) {
          time = parseInt(time)
        } else if (typeof time === 'string') {
          time = time
            .replace(new RegExp(/-/gm), '/')
            .replace('T', ' ')
            .replace(new RegExp(/\.[\d]{3}/gm), '')
        }
        if (typeof time === 'number' && time.toString().length === 10) {
          time = time * 1000
        }
        date = new Date(time)
      }
      const formatObj = {
        y: date.getFullYear(),
        m: date.getMonth() + 1,
        d: date.getDate(),
        h: date.getHours(),
        i: date.getMinutes(),
        s: date.getSeconds(),
        a: date.getDay(),
      }
      const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
        let value = formatObj[key]
        // Note: getDay() returns 0 on Sunday
        if (key === 'a') {
          return ['日', '一', '二', '三', '四', '五', '六'][value]
        }
        if (result.length > 0 && value < 10) {
          value = '0' + value
        }
        return value || 0
      })
      return time_str
    },
  },
}
</script>

<style lang="less" scoped>
.pagination {
  display: flex;
  justify-content: flex-end;
  padding: 20px 0;
}
:deep .el-dialog {
  border-radius: 12px;
}
.box {
  width: 100%;
  display: flex;
  justify-content: center;
  margin: 14px 0;
}
:deep .el-upload-dragger,
:deep .el-upload,
:deep .upload-demo {
  width: 100%;
}
.btn {
  color: #fff;
  border: none;
  // background: linear-gradient(135deg, #ff7f73 0%, #ff4040 100%);
  background-color: #409eff;
}
</style>
