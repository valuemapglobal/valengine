<template>
  <div class="submitReview">
    <el-drawer
      :title="drawer.title"
      :visible.sync="drawer.visible"
      :size="drawer.size"
      :before-close="handleClose"
    >
      <el-form
        ref="formRef"
        :model="reviewForm"
        :rules="rules"
        label-position="top"
      >
        <el-form-item label="业务场景名称" prop="businessName">
          <el-input
            v-model="reviewForm.businessName"
            placeholder="-"
            disabled
            clearable
          />
        </el-form-item>
        <el-form-item label="策略场景名称" prop="moduleName">
          <el-input
            v-model="reviewForm.moduleName"
            placeholder="-"
            disabled
            clearable
          />
        </el-form-item>
        <el-form-item label="批次标题" prop="batchTitle">
          <el-input
            v-model="reviewForm.batchTitle"
            placeholder="请输入批次标题"
            clearable
          />
        </el-form-item>
        <el-form-item label="批次描述" prop="batchDescription">
          <el-input
            v-model="reviewForm.batchDescription"
            type="textarea"
            :rows="3"
            placeholder="请输入批次描述"
            clearable
          />
          <span class="tip">可选，用于详细说明批次变更的背景和目的</span>
        </el-form-item>
        <el-form-item label="批次编号" prop="batchCode">
          <el-input
            v-model="reviewForm.batchCode"
            disabled
            placeholder="请输入批次编号"
            clearable
          />
          <span class="tip">格式：年月日-序号。如：20250915-001</span>
        </el-form-item>
        <!-- <el-form-item label="变更策略列表">
          <div class="tableSearch">
            <el-input
              v-model="queryParams.strategyName"
              placeholder="请输入策略名称"
            ></el-input>
          </div>
          <GutuTable
            :dataList="dataList"
            :loading="loading"
            :columnConfig="columnConfig"
            :collectionList="collectionList"
            :tableHeight="600"
          />
          <GutuPagination
            style="margin-top: 10px"
            ref="gutuPaginationRef"
            :total="total"
            @searchData="updateParams"
          />
        </el-form-item> -->
        <el-form-item label="附件上传" prop="fileList">
          <el-upload
            v-if="reviewForm.fileList.length == 0"
            v-loading="uploadLoading"
            class="upload-file"
            drag
            :action="getUploadUrl()"
            :headers="headersConfig()"
            accept=".xlsx,.xls"
            :before-upload="beforeUpload"
            :on-success="handleSuccess"
            :show-file-list="false"
            :file-list="reviewForm.fileList"
          >
            <div class="upload">
              <i class="el-icon-upload" />
              <div class="upload-text">将文件拖到此处或选择文件</div>
              <div class="upload-tip">
                支持 xlsx,.xls 格式，单个文件最大 50MB
              </div>
            </div>
          </el-upload>
          <div class="file-list">
            <div
              class="file-item"
              v-for="(item, index) in reviewForm.fileList"
              :key="index"
            >
              <div class="file-item-content">
                <div class="content-left">{{ item.fileName }}</div>
                <div class="content-right">
                  <div
                    class="content-right-download"
                    @click.stop="handleDownloadFile(item)"
                  >
                    <i class="el-icon-download"></i>
                  </div>
                  <div
                    class="content-right-delete"
                    @click.stop="handleDeleteFile(index)"
                  >
                    <i class="el-icon-close"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="审核节点" prop="approverId">
          <div class="auditNode">
            <div class="auditNode_name">
              <span>审核人：</span>
              <span>{{ currentReviewer?.userName || '-' }}</span>
            </div>
            <div class="auditNode_operation" @click="handleOpenDialog">
              <i class="el-icon-edit-outline" />
              更换审核人
            </div>
          </div>
        </el-form-item>
      </el-form>
      <div class="bottomBtn">
        <el-button type="primary" :disabled="btnLoading" @click="handleSubmit">
          <i class="el-icon-loading" v-if="btnLoading" />
          {{ btnLoading ? '提交中...' : '提交审批' }}</el-button
        >
        <el-button @click="handleClose">取消</el-button>
      </div>
    </el-drawer>
    <el-dialog
      :title="dialog.title"
      :visible.sync="dialog.visible"
      :width="dialog.width"
      :before-close="handleCloseDialog"
    >
      <el-input
        style="margin-bottom: 16px"
        v-model="queryParams.keyword"
        placeholder="请输入人员姓名或所属部门"
        size="normal"
        clearable
      />
      <GutuTable
        :dataList="authList"
        :loading="authLoading"
        :columnConfig="authColumnConfig"
        :tableHeight="400"
        :handle="authTableHandle"
      >
        <template #bt_handle="{ data }">
          <el-button type="primary" @click="handleSelectReviewer(data.row)"
            >选择</el-button
          >
        </template>
      </GutuTable>
      <GutuPagination
        style="margin-top: 10px"
        ref="authGutuPaginationRef"
        :total="authTotal"
        @searchData="updateAuthParams"
      />
    </el-dialog>
  </div>
</template>

<script>
import GutuTable from '@/components/gutu/gutuTable'
import GutuPagination from '@/components/gutu/gutuPagination'
import { mapState, mapGetters } from 'vuex'
import { approvalSubmit, uploadReport, downloadReport, batchCode } from '../api'
import { approvers } from '@/api/system/approvalAuth'

export default {
  components: {
    GutuTable,
    GutuPagination,
  },
  data() {
    return {
      drawer: {
        title: '提交审核',
        visible: false,
        size: '50%',
      },

      reviewForm: {
        businessName: null,
        moduleName: null,
        batchTitle: null,
        batchDescription: null,
        batchNumber: null,
        approverId: null,
        batchCode: null,
        fileList: [],
      },
      uploadLoading: false,
      rules: {
        businessName: [
          { required: true, message: '请输入业务场景名称', trigger: 'blur' },
        ],
        moduleName: [
          { required: true, message: '请输入策略场景名称', trigger: 'blur' },
        ],
        batchTitle: [
          { required: true, message: '请输入批次标题', trigger: 'blur' },
        ],
        // batchDescription: [
        //   { required: true, message: '请输入批次描述', trigger: 'blur' },
        // ],
        batchNumber: [
          { required: true, message: '请输入批次编号', trigger: 'blur' },
        ],
        fileList: [{ required: true, message: '请上传附件', trigger: 'blur' }],
        approverId: [
          { required: true, message: '请选择审核人', trigger: 'blur' },
        ],
      },
      btnLoading: false,

      queryParams: {
        keyword: null,
      },
      params: {
        pageNum: 1,
        pageSize: 10,
      },
      dataList: [],
      total: 10,
      loading: false,
      columnConfig: [
        {
          label: '策略名称',
          field: 'strategyName',
          width: null,
        },
        {
          label: '版本号',
          field: 'version',
          width: null,
        },
        {
          label: '类型',
          field: 'strategyType',
          width: null,
        },
      ],
      collectionList: {},

      fileList: [],

      dialog: {
        title: '选择审核人',
        visible: false,
        width: '40%',
      },
      authParams: {
        pageNum: 1,
        pageSize: 10,
      },
      authTotal: 10,
      authList: [],
      authLoading: false,
      authColumnConfig: [
        {
          label: '部门',
          field: 'deptName',
          width: null,
        },
        {
          label: '用户账号',
          field: 'userName',
          width: null,
        },
        {
          label: '用户名',
          field: 'nickName',
          width: null,
        },
      ],
      authTableHandle: {
        fixed: 'right',
        width: '140px',
        label: '操作',
        align: 'left',
        slot: true,
      },
      currentReviewer: null,
    }
  },
  watch: {
    queryParams: {
      handler() {
        this.getAuthList()
      },
      deep: true,
    },
  },
  computed: {
    ...mapState(['dataRisk']),
    ...mapGetters(['batchId']),
  },
  mounted() {},
  methods: {
    initData() {
      batchCode(this.batchId).then((res) => {
        if (res.code == 200) {
          this.reviewForm.batchCode = res.data.batchCode
        }
      })
    },
    handleSubmit() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.btnLoading = true
          let reportFilePath =
            this.reviewForm.fileList.length > 0
              ? this.reviewForm.fileList[0].url
              : null
          let params = {
            batchId: this.batchId,
            ...this.reviewForm,
            reportFilePath: reportFilePath,
          }
          approvalSubmit(params)
            .then((res) => {
              if (res.code == 200) {
                this.$message.success('提交成功')
                this.handleClose()
              }
            })
            .finally(() => {
              this.btnLoading = false
            })
        }
      })
    },

    handleDownloadFile(item) {
      window.open(item.url, '_blank')
    },
    handleDeleteFile(index) {
      this.reviewForm.fileList.splice(index, 1)
    },
    getUploadUrl() {
      return uploadReport()
    },

    getAuthList() {
      this.authLoading = true
      approvers({ ...this.queryParams, ...this.authParams }).then((res) => {
        if (res.code === 200) {
          this.authList = res.data.data
        }
        this.authLoading = false
      })
    },

    updateAuthParams(data) {
      this.authParams.pageNum = data.pageNum
      this.getAuthList()
    },
    resetAuthParams() {
      this.authParams = this.$options.data().authParams
      this.authList = []
      this.authTotal = 0
      if (this.$refs.authGutuPaginationRef)
        this.$refs.authGutuPaginationRef.reset()
    },

    handleSelectReviewer(row) {
      this.currentReviewer = row
      this.reviewForm.approverId = row.userId
      this.dialog.visible = false
    },

    updateParams(data) {
      this.params.pageNum = data.pageNum
    },

    handleOpen() {
      this.reviewForm = this.$options.data().reviewForm
      if (this.$refs.formRef) this.$refs.formRef.resetFields()
      this.reviewForm.businessName = this.dataRisk.decision.businessName
      this.reviewForm.moduleName = this.dataRisk.decision.moduleName
      this.initData()
      this.drawer.visible = true
    },
    handleClose() {
      this.drawer.visible = false
    },

    handleOpenDialog() {
      this.resetAuthParams()
      this.getAuthList()
      this.dialog.visible = true
    },

    handleCloseDialog() {
      this.dialog.visible = false
    },

    headersConfig: () => {
      return { Authorization: localStorage.getItem('id_token') }
    },
    beforeUpload(file) {
      //文件大小限制20M
      if (file.size > 20 * 1024 * 1024) {
        this.$message.error('文件大小不能超过20M')
        return false
      }
      if (
        ![
          'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
          'application/vnd.ms-excel',
        ].includes(file.type)
      ) {
        this.$message.error('上传文件格式错误')
        return false
      }
      this.uploadLoading = true
      return true
    },
    handleSuccess(response, file, fileList) {
      if (response.code == 200) {
        let data = response.data

        this.uploadLoading = false

        this.reviewForm.fileList.push({
          fileName: file.name,
          url: data.url,
        })
      }
    },
  },
}
</script>
<style lang="less" scoped>
.submitReview {
  :deep(.el-drawer) {
    .el-drawer__header {
      margin-bottom: 0;
      padding: 20px;
    }
    .el-drawer__body {
      padding: 0px 20px 20px;
    }
  }
  :deep(.el-dialog) {
    border-radius: 12px;
    .el-dialog__header {
      margin-bottom: 0;
      padding: 20px;
    }
    .el-dialog__body {
      padding: 0px 20px 20px;
    }
  }

  :deep(.el-form) {
    .el-form-item__label {
      font-weight: 500;
      font-size: 14px;
      color: #000;
      padding-bottom: 0;
    }

    .el-input {
      .el-input__inner {
        height: 40px;
      }
    }
  }

  .tip {
    font-size: 13px;
    color: var(--text-color-tertiary);
  }

  .tableSearch {
    margin-bottom: 10px;
  }

  .auditNode {
    display: flex;
    align-items: center;
    justify-content: space-between;
    border: 1px solid #e5e5e5;
    padding: 10px 20px;
    border-radius: 12px;
    &_name {
      span {
        color: var(--text-color-tertiary);
      }
      span:last-child {
        color: var(--text-color-secondary);
        font-weight: 500;
      }
    }
    &_operation {
      color: #409eff;
      cursor: pointer;
    }
  }

  .upload-file {
    ::v-deep .el-upload {
      width: 100%;
      .el-upload-dragger {
        width: 100%;
        height: 100%;
        min-height: 200px;
        background: var(--bg-color);
        .upload {
          width: 100%;
          height: 100%;
          display: flex;
          flex-direction: column;
          padding: 24px;

          .el-icon-upload {
            margin: 20px 0px 10px;
          }

          .upload-text {
            color: var(--text-color-secondary);
          }

          .upload-tip {
            font-size: 13px;
            color: var(--text-color-tertiary);
          }
        }
      }
    }
  }

  .file-list {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 10px;

    .file-item {
      padding: 4px 12px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      background: var(--bg-color-lighter);
      border-radius: 8px;
      margin-top: 8px;
      .file-item-content {
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;
        .content-left {
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          color: var(--text-color-secondary);
        }
        .content-right {
          display: flex;
          align-items: center;
          &-download,
          &-delete {
            width: 24px;
            height: 24px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
          }
          &-download:hover,
          &-delete:hover {
            background-color: var(--bg-color-lighter);
            border-radius: 4px;
          }
        }
      }
    }
    // .file-item:last-child {
    //   margin-right: 0;
    // }
  }

  .bottomBtn {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;

    .el-button {
      width: 100px;
      height: 40px;
    }
  }
}
</style>
