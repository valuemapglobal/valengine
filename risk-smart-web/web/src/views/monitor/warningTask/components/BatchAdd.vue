<template>
  <el-dialog
    :title="dialog.title"
    :visible.sync="dialog.visible"
    :width="dialog.width"
    :before-close="handleClose"
  >
    <div class="batchAdd">
      <el-upload
        v-if="fileList.length == 0"
        action="#"
        drag
        multiple
        :file-list="fileList"
        :on-change="handleFileChange"
        :on-remove="handleFileRemove"
        :before-upload="beforeUpload"
        :auto-upload="false"
        accept=".xlsx,.xls,.csv"
      >
        <template #trigger>
          <div class="upload">
            <img src="@/assets/images/comment/upload.png" alt="" />
            {{ $t('monitor.uploadTip') }}
          </div>
        </template>
      </el-upload>

      <!-- 文件列表 -->
      <div v-if="fileList.length > 0" class="file-list">
        <div v-for="(file, index) in fileList" :key="index" class="file-item">
          <img src="@/assets/images/comment/uploadFile.png" alt="" />
          <span class="file-name">{{ file.name }}</span>
          <span class="file-size">{{ formatFileSize(file.size) }}</span>
          <el-button
            type="text"
            size="mini"
            @click="handleFileRemove(file)"
            class="remove-btn"
          >
            <i class="el-icon-close"></i>
          </el-button>
        </div>
      </div>

      <div class="uploadTip">
        <div class="title">{{ $t('monitor.uploadTipTitle') }}</div>
        <p>
          {{ $t('monitor.uploadTip1') }}
          <span @click="downloadTemplate">
            {{ $t('monitor.downloadTemplate') }}
          </span>
        </p>
        <p>{{ $t('monitor.uploadTip2') }}</p>
        <p>{{ $t('monitor.uploadTip3') }}</p>
        <p>{{ $t('monitor.uploadTip4') }}</p>
      </div>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button
        type="primary"
        @click="handleConfirmUpload"
        :loading="uploading"
        :disabled="fileList.length === 0"
      >
        {{ uploading ? $t('monitor.importing') : $t('monitor.import') }}
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import { batchAdd } from '../../api'
export default {
  name: 'MonitoringList',
  components: {},
  props: {
    subjectType: {
      type: [String, Number],
      default: 0,
    },
    taskId: {
      type: [String, Number],
      default: null,
    },
  },
  data() {
    return {
      dialog: {
        visible: false,
        title: '',
        width: '560px',
      },
      fileList: [],
      uploading: false,
    }
  },
  methods: {
    handleOpen() {
      this.fileList = []
      this.dialog.title = this.$t('monitor.batchAddMonitorList')
      this.dialog.visible = true
    },
    handleClose() {
      this.dialog.visible = false
      this.fileList = []
      this.uploading = false
    },
    // 文件改变时的处理
    handleFileChange(file, fileList) {
      this.fileList = fileList
    },
    // 删除文件
    handleFileRemove(file) {
      const index = this.fileList.indexOf(file)
      if (index !== -1) {
        this.fileList.splice(index, 1)
      }
    },
    // 上传前的验证
    beforeUpload(file) {
      const isValidType = [
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'application/vnd.ms-excel',
        'text/csv',
      ].includes(file.type)
      if (!isValidType) {
        this.$message.error(this.$t('monitor.onlyExcelOrCsv'))
        return false
      }

      const isLt10M = file.size / 1024 / 1024 < 10
      if (!isLt10M) {
        this.$message.error(this.$t('monitor.fileSizeLimit'))
        return false
      }

      return false // 阻止自动上传
    },
    // 格式化文件大小
    formatFileSize(bytes) {
      if (bytes === 0) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    },

    downloadTemplate() {
      const aEl = document.createElement('a')

      // 正确设置下载路径 (href) 和文件名 (download)
      const subjectTypeName =
        this.subjectType === 0
          ? this.$t('monitor.enterprise')
          : this.$t('monitor.personal')
      const fileName = `${subjectTypeName}${this.$t(
        'monitor.batchMonitorTemplate'
      )}`
      const filePath = `${
        window.location.origin + window.location.pathname
      }/${fileName}` // 👉 替换为实际模板路径

      aEl.href = filePath
      aEl.setAttribute('download', fileName) // 设置下载时的文件名

      // 必须将元素加入DOM才能触发点击 (某些浏览器要求)
      aEl.style.display = 'none'
      document.body.appendChild(aEl)

      aEl.click()

      // 清理DOM
      document.body.removeChild(aEl)
    },
    // 确认上传
    async handleConfirmUpload() {
      if (this.fileList.length === 0) {
        this.$message.warning(this.$t('monitor.pleaseSelectFile'))
        return
      }

      this.uploading = true
      try {
        const formData = new FormData()
        const file = this.fileList[0].raw // 原始文件
        formData.append('file', file) // 假设后端接口接收的文件字段名为'file'
        formData.append('taskId', this.taskId)

        batchAdd(formData)
          .then((res) => {
            if (res.code == 200) {
              this.$message.success(this.$t('monitor.uploadSuccess'))
              this.handleClose()
              this.$emit('refresh')
            }
            this.uploading = false
          })
          .catch(() => {
            this.uploading = false
          })
      } catch (error) {
        console.error('上传失败:', error)
        this.$message.error(this.$t('monitor.uploadFailed'))
      }
    },
  },
}
</script>
<style lang="less" scoped>
::v-deep .el-dialog {
  border-radius: 12px;
  font-family: PingFang SC-Medium;
  .el-dialog__header {
    padding: 20px;
    margin-bottom: 0px;
  }
  .el-dialog__body {
    padding: 0px 20px 20px;
  }
}
.batchAdd {
  ::v-deep .el-upload {
    width: 100%;
    margin-bottom: 20px;
    .el-upload-dragger {
      width: 100%;
      height: 200px;
      border: 1px dashed var(--primary-color);
    }
  }
  .upload {
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    background: var(--primary-color-lighter);
    color: rgba(#000, 0.85);
    padding: 0px 20px;

    img {
      width: 65px;
      margin-top: 32px;
      margin-bottom: 32px;
    }
  }

  .file-list {
    margin-bottom: 20px;
    padding: 15px;
    background: #f8f9fa;
    border-radius: 6px;

    .file-list-title {
      font-weight: 500;
      margin-bottom: 10px;
      color: rgba(#000, 0.85);
    }

    .file-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 8px 0;
      border-bottom: 1px solid #e8e8e8;

      &:last-child {
        border-bottom: none;
      }

      img {
        margin-right: 10px;
      }

      .file-name {
        flex: 1;
        color: rgba(#000, 0.85);
        margin-right: 10px;
      }

      .file-size {
        color: rgba(#000, 0.45);
        font-size: 12px;
        margin-right: 10px;
      }

      .remove-btn {
        color: #f56c6c;
        padding: 0;

        i {
          font-size: 16px;
        }

        &:hover {
          color: #f78989;
        }
      }
    }
  }

  .uploadTip {
    .title {
      // font-size: 14px;
      color: rgba(#000, 0.85);
      margin-bottom: 10px;
    }
    > p {
      font-size: 14px;
      margin-bottom: 4px;

      span {
        display: inline-block;
        margin-left: 10px;
        color: var(--primary-color);
        cursor: pointer;
      }
    }
  }
}

.dialog-footer {
  text-align: right;
  .el-button {
    padding: 0px 20px;
    height: 36px;
  }
}
</style>
