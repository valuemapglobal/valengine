<template>
  <el-dialog
    :title="$t('userManage.assignRoles')"
    :destroy-on-close="true"
    :visible.sync="dialogVisible"
    width="1026px"
  >
    <el-form
      label-position="left"
      :model="ruleForm"
      :rules="rules"
      ref="ruleForm"
      :label-width="isEnglish() ? '140px' : '100px'"
      class="demo-ruleForm"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="$t('userManage.nickName')" prop="nickName">
            <el-input
              size="medium"
              disabled
              v-model="ruleForm.nickName"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item
            :label="$t('userManage.loginAccount')"
            prop="phonenumber"
          >
            <el-input
              size="medium"
              disabled
              v-model="ruleForm.phonenumber"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-table
      style="width: 100%"
      border
      :header-cell-style="{
        background: 'rgba(230,238,252,0.3)',
      }"
      v-loading="loading"
      :row-key="getRowKey"
      @row-click="clickRow"
      ref="table"
      @selection-change="handleSelectionChange"
      :data="roles.slice((pageNum - 1) * pageSize, pageNum * pageSize)"
    >
      <el-table-column
        :label="$t('userManage.serialNumber')"
        type="index"
        align="center"
      >
        <template slot-scope="scope">
          <span>{{ (pageNum - 1) * pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column
        type="selection"
        :reserve-selection="true"
        width="55"
      ></el-table-column>
      <el-table-column
        :label="$t('userManage.roleId')"
        align="center"
        prop="roleId"
      />
      <el-table-column
        :label="$t('userManage.roleName')"
        align="center"
        prop="roleName"
      />
      <el-table-column
        :label="$t('userManage.roleKey')"
        align="center"
        prop="roleKey"
      />
      <el-table-column
        :label="$t('common.createTime')"
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
        :current-page.sync="pageNum"
        :page-size="pageSize"
        background
        layout="prev, pager, next"
        :total="total"
      >
      </el-pagination>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button class="btn" @click="handleSelectUser">{{
        $t('userManage.confirmAdd')
      }}</el-button>
      <el-button @click="handleClose">{{ $t('common.cancel') }}</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { authRole, putAuthRole } from '@/api/system/userManagement.js'
export default {
  data() {
    return {
      // 遮罩层
      loading: true,
      dialogVisible: false,
      ruleForm: {
        nickName: null,
        phonenumber: null,
      },
      roles: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      // 选中用户组
      roleId: [],
      // 非多个禁用
      multiple: true,
      id: null,
    }
  },
  computed: {
    rules() {
      return {}
    },
  },
  methods: {
    authRole(id) {
      this.id = id
      // console.log(id, 'id');
      authRole(id).then((res) => {
        this.ruleForm = res.user
        this.roles = res.roles
        this.total = res.roles.length
        this.$nextTick(() => {
          this.roles.forEach((row) => {
            if (row.flag) {
              this.$refs.table.toggleRowSelection(row)
            }
          })
        })
        this.loading = false
      })
    },
    handleClose() {
      this.reset()
      this.dialogVisible = false
    },
    reset() {
      this.ruleForm = {
        nickName: null,
        phonenumber: null,
      }
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
      this.roleId = selection.map((item) => item.roleId)
      this.multiple = !selection.length
    },
    /** 选择授权用户操作 */
    handleSelectUser() {
      const userIds = this.id
      const roleId = this.roleId.join(',')
      if (userIds == '') {
        this.$message({
          type: 'error',
          message: this.$t('userManage.selectUserToAssign'),
        })
        return
      }
      // console.log(userIds, 'userIds');
      // console.log(roleId, 'roleId');
      putAuthRole({ userId: userIds, roleIds: roleId }).then((res) => {
        if (res.code === 200) {
          this.$message({
            type: 'success',
            message: this.$t('common.success'),
          })
          this.show()
        }
      })
    },

    // system/ user / authRole ? roleId = 446584 & userIds=29, 40
    // system / user / authRole ? userId = 446584 & roleIds=29, 40

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
          return this.$t('userManage.weekDays')[value]
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
.dialog-footer {
  .el-button {
    height: 42px;
    padding: 0 20px;
    font-size: 14px;
  }
  .btn {
    color: #fff;
    border: none;
    background: #409eff;
  }
}
</style>
