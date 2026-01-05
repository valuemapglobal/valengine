<template>
  <el-drawer
    :title="drawer.title"
    :visible.sync="drawer.visible"
    :size="drawer.width"
    :before-close="handleClose"
  >
    <div class="amt">
      <el-form
        ref="formRef"
        :model="taskForm"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="流程策略" prop="processPolicyId">
          <el-select
            v-model="taskForm.processPolicyId"
            placeholder="请选择"
            clearable
            filterable
          >
            <el-option
              v-for="item in policyOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="监测时间" prop="monitorDays">
          <div class="select-box">
            <div
              class="select-box_item"
              :class="{
                'select-box_item--active': taskForm.monitorDays === item.value,
              }"
              v-for="(item, index) in collectionList.ric_monitor_time"
              :key="index"
              @click="taskForm.monitorDays = item.value"
            >
              {{ item.label }}
            </div>
          </div>
        </el-form-item>
        <el-form-item label="监测主体" prop="subjectType">
          <div class="select-box">
            <div
              class="select-box_item"
              :class="{
                'select-box_item--active': taskForm.subjectType === item.value,
              }"
              v-for="(item, index) in collectionList.ric_monitor_subject"
              :key="index"
              @click="taskForm.subjectType = item.value"
            >
              {{ item.label }}
            </div>
          </div>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="taskForm.remark"
            type="textarea"
            :rows="5"
            placeholder="请输入"
          />
        </el-form-item>
      </el-form>
      <div class="bottomBtns">
        <el-button type="primary" @click="handleSubmit">
          <i class="el-icon-loading" v-if="btnLoading" />
          {{ btnLoading ? '提交中...' : '提交' }}
        </el-button>
      </div>
    </div>
  </el-drawer>
</template>
<script>
import { saveOrUpdate, processPolicyList } from '../../api'
export default {
  name: 'AddMonitoringTasks',
  props: {
    collectionList: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  data() {
    return {
      drawer: {
        visible: false,
        title: '新增监测任务',
        width: '450px',
      },
      btnLoading: false,
      taskForm: {
        taskId: null,
        processPolicyId: null,
        monitorDays: '1',
        subjectType: '0',
        remark: null,
      },
      rules: {
        processPolicyId: [
          { required: true, message: '请输入流程策略', trigger: 'change' },
        ],
        monitorDays: [
          { required: true, message: '请输入监测时间', trigger: 'change' },
        ],
        subjectType: [
          { required: true, message: '请输入监控主体', trigger: 'change' },
        ],
      },

      policyOptions: [],
    }
  },
  methods: {
    init() {
      processPolicyList({})
        .then((res) => {
          this.policyOptions = res.data.list.map((item) => {
            return {
              label: item.processStrategy,
              value: item.id,
            }
          })
        })
        .catch(() => {})
    },
    handleSubmit() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.btnLoading = true
          saveOrUpdate({ ...this.taskForm })
            .then((res) => {
              if (res.code == 200) {
                this.$message.success('操作成功')
                this.handleClose()
                this.$emit('refresh')
              }
              this.btnLoading = false
            })
            .catch((err) => {
              this.btnLoading = false
            })
        }
      })
    },
    handleOpen(data) {
      this.init()
      this.taskForm = this.$options.data().taskForm
      if (this.$refs.taskFormRef) this.$refs.taskFormRef.clearValidate()
      if (data) {
        for (let key in this.taskForm) {
          if (data[key] != null) this.taskForm[key] = data[key] + ''
        }
      }
      this.drawer.visible = true
    },
    handleClose() {
      this.drawer.visible = false
    },
  },
}
</script>
<style lang="less" scoped>
::v-deep .el-drawer {
  .el-drawer__header {
    padding: 20px;
    margin-bottom: 0px;
  }
  .el-drawer__body {
    padding: 0px 20px 20px;
  }
}
.amt {
  ::v-deep .el-form {
    .el-select {
      width: 100%;
      .el-input__inner {
        border: none;
        background: rgba(#000, 0.04);
        height: 42px;
      }
    }
    .el-textarea {
      .el-textarea__inner {
        border: none;
        background: rgba(#000, 0.04);
      }
    }
  }
  .select-box {
    display: flex;
    align-items: center;
    &_item {
      min-width: 70px;
      padding: 0px 20px;
      font-size: 14px;
      margin-right: 16px;
      border: 1px solid #ddd;
      border-radius: 6px;
      cursor: pointer;
      color: rgba(#000, 0.6);

      &--active {
        border: var(--primary-color) 1px solid;
        color: var(--primary-color);
      }
    }
  }

  .bottomBtns {
    width: 100%;
    margin-top: 50px;
    display: flex;
    justify-content: flex-end;
    .el-button {
      padding: 10px 32px;
      height: 42px;
      font-size: 16px;
      border-radius: 6px;
    }
  }
}
</style>
