<template>
  <el-dialog title="用户导入" :visible.sync="dialogVisible" width="507px">
    <span
      >·仅允许导入xls、xlsx格式文件
      <a href="#" @click="importTemplate">下载样例文件</a></span
    >
    <div class="box">
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">
          只能上传jpg/png文件，且不超过500kb
        </div>
      </el-upload>
    </div>
    <el-checkbox v-model="upload.updateSupport"
      >是否更新已经存在的用户数据</el-checkbox
    >
    <div class="btnBottom">
      <el-button type="primary" @click="submitFileForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getTemplate } from '@/api/system/userManagement.js'
import { getToken } from '@/utils/auth'
export default {
  data() {
    return {
      dialogVisible: false,
      // 用户导入参数
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: '',
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的用户数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: 'Bearer ' + getToken() },
        // 上传的地址
        url:
          process.env.VUE_APP_ENV === 'development'
            ? '/dev-api/system/user/importData'
            : '/prod-api/system/user/importData',
      },
    }
  },
  methods: {
    show() {
      this.dialogVisible = !this.dialogVisible
    },
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false
      this.upload.isUploading = false
      this.$refs.upload.clearFiles()
      this.$alert(response.msg, '导入结果', { dangerouslyUseHTMLString: true })
      this.getList()
    },
    submitFileForm() {
      this.$refs.upload.submit()
    },
    importTemplate() {
      this.download(
        getTemplate(),
        {},
        `user_template_${new Date().getTime()}.xlsx`
      )
    },
  },
}
</script>

<style lang="less" scoped>
:deep .el-dialog {
  border-radius: 12px;
}
.box {
  width: 100%;
  display: flex;
  justify-content: center;
  margin: 14px 0;
}
.btnBottom {
  width: 100%;
  margin-top: 10px;
  text-align: right;
}
</style>
