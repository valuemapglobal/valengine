<template>
  <div style="height: 100%">
    <!-- 头部信息 -->
    <InfoTitle title="个人信息">
      <template slot="right">
        <el-button type="primary" @click="$emit('back')"> 实名认证 </el-button>
        <el-button type="primary" @click="modify"> 修改资料 </el-button>
        <el-button
          type="info"
          @click="$router.push({ name: 'AIDueDiligence' })"
        >
          取消
        </el-button>
      </template>
    </InfoTitle>
    <div class="formBox">
      <el-form
        :model="formData"
        :rules="rules"
        ref="ruleForm"
        label-width="100px"
        class="form"
      >
        <el-form-item label="头像:" prop="avatar">
          <div class="upload">
            <el-upload
              class="upload-demo"
              :action="uploadImgUrl"
              :headers="headers"
              :on-success="quillImgSuccess"
              :on-error="uploadError"
              :before-remove="beforeRemove"
              :show-file-list="false"
              name="file"
              ref="upload"
            >
              <div class="edit"><i class="el-icon-edit"></i></div>
            </el-upload>

            <el-avatar
              shape="square"
              :size="120"
              :src="formData.avatar || avatarUrl[Number(formData.sex)]"
            />
          </div>
        </el-form-item>
        <el-form-item label="用户昵称:" prop="nickName">
          <el-input
            v-model="formData.nickName"
            placeholder="请输入用户昵称"
          ></el-input>
        </el-form-item>
        <el-form-item label="手机号码:" prop="phonenumber">
          <el-input
            disabled
            v-model="formData.phonenumber"
            placeholder="请输入手机号码"
          ></el-input>
        </el-form-item>
        <el-form-item label="邮箱:" prop="email">
          <el-input
            v-model="formData.email"
            placeholder="请输入邮箱"
          ></el-input>
        </el-form-item>
        <el-form-item label="性别:" prop="sex">
          <el-radio-group v-model="formData.sex" disabled>
            <el-radio :label="'0'">男</el-radio>
            <el-radio :label="'1'">女</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { InfoTitle } from '@/views/userInfo/components/components'
import { updateUser } from '@/api/base/index.js'

export default {
  name: 'PersonInfo',
  components: {
    InfoTitle,
  },
  data() {
    return {
      userInfo: {}, //用户信息
      avatarUrl: [
        require('../../../public/images/nanAvatar.png'),
        require('../../../public/images/nvAvatar.png'),
      ], //头像默认图片
      uploadImgUrl: null, //上传服务器地址
      headers: {
        //图片上传数据
        Authorization: null,
      },
      formData: {
        avatar: null, //头像
        nickName: null, //用户名称
        phonenumber: null, //手机号
        email: null, //邮箱
        sex: null, //性别
      },
      rules: {
        name: [
          { required: true, message: '请输入活动名称', trigger: 'blur' },
          { min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' },
        ],
      },
    }
  },
  watch: {
    '$store.state.userInfo': {
      handler(newV) {
        this.formData = newV
      },
      deep: true,
      immediate: true,
    },
  },
  created() {
    this.uploadImgUrl =
      process.env.VUE_APP_ENV === 'production'
        ? '/prod-api'
        : '/dev-api/prod-api' + '/bankCashflow/analysis/uploadFile'
    this.headers.Authorization = localStorage.getItem('id_token')
  },
  methods: {
    /**
     * @name: quillImgSuccess
     * @msg: 上传文件
     * @param {*} res
     * @param {*} file
     * @return {*}
     */
    quillImgSuccess(res, file) {
      // res为图片服务器返回的数据
      // 如果上传成功
      if (res.code == 200) {
        this.blobUrl = URL.createObjectURL(file.raw)
        console.log(res)
        this.formData.avatar = res.url
      } else {
        this.$message({
          message: '上传失败',
          type: 'error',
        })
      }
    },
    /**
     * @name: uploadError
     * @msg: 富文本图片上传失败
     * @param {*}
     * @return {*}
     */
    uploadError() {
      // loading动画消失
      this.$message({
        message: '文件导入失败',
        type: 'error',
      })
    },
    /**
     * @name: beforeRemove
     * @msg: 失败后移除图片
     * @param {*} file
     * @param {*} fileList
     * @return {*}
     */
    beforeRemove(file, fileList) {
      let bool = false
      this.$confirm(`确定移除 ${file.name}？`)
        .then(() => {
          this.$message({
            type: 'success',
            message: '删除成功!',
          })
          fileList.splice(fileList.indexOf(file), 1)
        })
        .catch(() => {
          bool = false
        })
      return bool
    },
    /**
     * @name: modify
     * @msg: 修改资料
     * @param {*}
     * @return {*}
     */
    modify() {
      this.$confirm('此操作会修改您的资料, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          updateUser(this.formData).then((res) => {
            localStorage.setItem('userInfo', JSON.stringify(res.data))
          })
          this.$message({
            type: 'success',
            message: '已修改信息!',
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消修改',
          })
        })
    },
  },
}
</script>

<style lang="less" scoped>
.form {
  width: 50%;
}

.formBox {
  padding: 1.25rem;
  overflow-y: auto;
  height: calc(100% - 8.75rem);
}

:deep .el-form-item__content {
  margin-left: 9.375rem !important;
}

.upload {
  position: relative;

  .upload-demo {
    position: absolute;
    width: 7.5rem;
    height: 7.5rem;

    .edit {
      position: absolute;
      right: -0.8125rem;
      top: -0.8125rem;
      height: 1.625rem;
      width: 1.625rem;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: var(--primary-color);
      border-radius: 50%;

      > i {
        color: #ffffff;
      }
    }
  }
}
</style>
