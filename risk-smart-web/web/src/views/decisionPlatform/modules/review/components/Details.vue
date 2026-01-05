<template>
  <el-drawer
    :title="drawer.title"
    :visible.sync="drawer.visible"
    :size="drawer.width"
    :before-close="handleClose"
    :destroy-on-close="true"
    :show-close="true"
  >
    <div class="review-details" v-loading="!baseInfo">
      <div class="rd-card">
        <div class="rd-title">基础信息</div>
        <div class="rd-content" v-if="baseInfo">
          <RowData :data="baseInfo" :configList="configList" />
          <RowData
            style="margin-top: 10px"
            :data="baseInfo"
            :configList="configList1"
          />
        </div>
      </div>
      <div class="rd-card">
        <div class="rd-title">附件列表</div>
        <div class="rd-file-list">
          <div
            v-for="(item, index) in fileList"
            :key="index"
            class="rd-file-item"
          >
            <div class="rd-file-item-name">{{ item.fileName }}</div>
            <i class="el-icon-download" @click="handleDownloadFile(item)" />
          </div>
        </div>
      </div>
      <div class="rd-card">
        <div class="rd-title">审批操作</div>
        <div class="rd-content">
          <el-form
            :model="reviewForm"
            ref="form"
            :rules="rules"
            label-position="top"
            :inline="false"
          >
            <el-form-item label="审批意见" prop="comments">
              <el-input
                v-model="reviewForm.comments"
                :disabled="!isEdit"
                type="textarea"
                :rows="5"
              />
            </el-form-item>
          </el-form>
        </div>
      </div>
      <div class="bottomBtns" v-if="isEdit">
        <el-button type="primary" @click="handleOpenVersion"
          >审批通过</el-button
        >
        <el-button type="danger" @click="handleSubmit(false)"
          >审批拒绝</el-button
        >
      </div>
    </div>
    <confirmDialog
      ref="confirmDialog"
      :disabled="disabledVersion"
      @update="handleSubmit(true, true)"
      @reserved="handleSubmit(true, false)"
      updateText="确定并更新版本"
      reservedText="确定并保留版本"
      @cancel="handleCancel"
    >
      <div>
        <div>是否确认发布</div>
        <div>您可以同时选择是否更新版本</div>
      </div>
    </confirmDialog>
  </el-drawer>
</template>
<script>
import RowData from '@/components/RowData.vue'
import confirmDialog from '@/views/decisionPlatform/modules/productDecision/components/confirmDialog.vue'
import { getBatchDetail, processApproval } from '../api'
export default {
  components: {
    RowData,
    confirmDialog,
  },
  props: {
    expand: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      drawer: {
        visible: false,
        title: '工作流审批',
        width: '40%',
      },
      baseInfo: null,
      configList: [
        {
          title: [],
          row: 2,
          fieldList: [
            { label: '批次标题', field: 'batchTitle', unEllipsis: true },
            { label: '批次编号', field: 'batchCode', unEllipsis: true },
            { label: '业务场景', field: 'businessName', unEllipsis: true },
            { label: '模型名称', field: 'ruleName', unEllipsis: true },
            { label: '提交人', field: 'submitterName', unEllipsis: true },
            { label: '提交时间', field: 'submittedAt', type: 'timeStamp' },
          ],
        },
      ],
      configList1: [
        {
          title: [],
          row: 1,
          fieldList: [
            { label: '批次描述', field: 'batchDescription', unEllipsis: true },
          ],
        },
      ],
      fileList: [],

      reviewForm: {
        approved: null,
        comments: null,
      },
      taskId: null,
      rules: {
        comments: [
          { required: true, message: '请输入审批意见', trigger: 'blur' },
        ],
      },
      isEdit: false,
      disabledVersion: false,
      btnLoading: false,
    }
  },
  methods: {
    handleOpenVersion() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.$refs.confirmDialog.visible = true
        }
      })
    },
    handleCancel() {
      this.$refs.confirmDialog.visible = false
    },

    handleSubmit(approved, update = false) {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.reviewForm.approved = approved

          this.disabledVersion = true

          processApproval({
            ...this.reviewForm,
            batchId: this.taskId,
            updateVersion: update,
            personOrCompany: 'C',
          })
            .then((res) => {
              if (res.code === 200) {
                this.$message.success('审批成功')
                this.handleCancel()
                this.handleClose()
                this.$emit('refresh')
              }
            })
            .catch((err) => {
              console.log(err)
            })
            .finally(() => {
              this.disabledVersion = false
            })
        }
      })
    },
    getDetail() {
      getBatchDetail(this.taskId).then((res) => {
        if (res.code == 200) {
          this.baseInfo = res.data

          let data = this.baseInfo

          if (data?.reportFileName && data?.reportFilePath) {
            this.fileList.push({
              fileName: data.reportFileName,
              url: data.reportFilePath,
            })
          }

          this.reviewForm.comments = data?.comments
        }
      })
    },
    handleDownloadFile(item) {
      window.open(item.url, '_blank')
    },
    handleOpen(data, isEdit) {
      this.isEdit = isEdit
      this.taskId = data.taskId

      this.getDetail()
      this.drawer.visible = true
    },
    handleClose() {
      this.reviewForm = this.$options.data().reviewForm
      this.fileList = []
      this.drawer.visible = false
    },
  },
}
</script>
<style lang="less" scoped>
::v-deep .el-drawer {
  font-family: PingFang SC-Medium;
  .el-drawer__header {
    padding: 20px;
    margin-bottom: 0px;
  }
  .el-drawer__body {
    padding: 0px 20px 20px;
  }
}
.review-details {
  .rd-card {
    border-radius: 8px;
    border: 1px solid var(--border-color);
    padding: 16px;
    margin-bottom: 16px;
  }
  .rd-title {
    height: 20px;
    line-height: 20px;
    font-size: 16px;
    color: var(--text-color-secondary);
    margin-bottom: 16px;
    border-left: 2px solid #409eff;
    padding-left: 10px;
  }
  .rd-file-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  .rd-file-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: var(--bg-color-lighter);
    padding: 8px 16px;
    border-radius: 8px;
    font-size: 14px;
    > i {
      font-size: 18px;
      cursor: pointer;
    }
    > i:hover {
      color: #409eff;
    }
  }

  .bottomBtns {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
    .el-button {
      padding: 10px 20px;
      height: 40px;
      font-size: 14px;
    }
  }
}
</style>
