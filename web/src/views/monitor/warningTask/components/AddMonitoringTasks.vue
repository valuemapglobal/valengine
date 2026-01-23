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
        :label-width="isEnglish() ? '140px' : '80px'"
      >
        <el-form-item
          :label="$t('monitor.processStrategy')"
          prop="processPolicyId"
        >
          <el-select
            v-model="taskForm.processPolicyId"
            :placeholder="$t('monitor.pleaseSelect')"
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
        <el-form-item :label="$t('monitor.monitorTime')" prop="monitorDays">
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
        <el-form-item :label="$t('monitor.monitorSubject')" prop="subjectType">
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
        <el-form-item :label="$t('monitor.remark')" prop="remark">
          <el-input
            v-model="taskForm.remark"
            type="textarea"
            :rows="5"
            :placeholder="$t('monitor.pleaseInput')"
          />
        </el-form-item>
      </el-form>
      <div class="bottomBtns">
        <el-button type="primary" @click="handleSubmit">
          <i class="el-icon-loading" v-if="btnLoading" />
          {{ btnLoading ? $t('monitor.submitting') : $t('monitor.submit') }}
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
        title: '',
        width: '40%',
      },
      btnLoading: false,
      taskForm: {
        taskId: null,
        processPolicyId: null,
        monitorDays: '1',
        subjectType: '0',
        remark: null,
      },
      policyOptions: [],
    }
  },
  computed: {
    rules() {
      return {
        processPolicyId: [
          {
            required: true,
            message: this.$t('monitor.inputProcessStrategy'),
            trigger: 'change',
          },
        ],
        monitorDays: [
          {
            required: true,
            message: this.$t('monitor.inputMonitorTime'),
            trigger: 'change',
          },
        ],
        subjectType: [
          {
            required: true,
            message: this.$t('monitor.inputMonitorSubject'),
            trigger: 'change',
          },
        ],
      }
    },
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
                this.$message.success(this.$t('monitor.operationSuccess'))
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
      if (this.$refs.formRef) this.$refs.formRef.clearValidate()
      if (data) {
        for (let key in this.taskForm) {
          if (data[key] != null) this.taskForm[key] = data[key] + ''
        }
        this.drawer.title = this.$t('monitor.editMonitorTask')
      } else {
        this.drawer.title = this.$t('monitor.addMonitorTask')
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
        background: rgba(#000, 0.04);
        height: 42px;
      }
    }
    .el-textarea {
      .el-textarea__inner {
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
