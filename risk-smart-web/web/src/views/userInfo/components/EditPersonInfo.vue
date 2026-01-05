<template>
  <div style="height: 100%">
    <!-- 头部信息 -->
    <InfoTitle title="个人实名认证">
      <template slot="right">
        <el-button type="primary" @click="submitCertification">
          提交认证
        </el-button>
        <el-button type="info" @click="$emit('back')"> 取消 </el-button>
      </template>
    </InfoTitle>
    <div class="formBox">
      <el-form
        :model="formData"
        :rules="rules"
        ref="form"
        label-width="150px"
        class="form"
        label-position="left"
      >
        <el-form-item label="证件信息"></el-form-item>
        <el-form-item label="证件类型" prop="idType">
          <el-select
            style="width: 100%"
            v-model="formData.idType"
            :disabled="user.personAuthStatus == 1"
          >
            <el-option
              v-for="item in IDTypes"
              :key="item.id"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="姓名:" prop="realName">
          <el-input
            :disabled="user.personAuthStatus == 1"
            v-model="formData.realName"
            placeholder="请输入姓名"
          ></el-input>
        </el-form-item>
        <el-form-item label="证件号码:" prop="idNumber">
          <el-input
            :disabled="user.personAuthStatus == 1"
            v-model="formData.idNumber"
            placeholder="请输入证件号码"
          ></el-input>
        </el-form-item>
        <el-form-item class="upload" label="上传身份证正反面">
          <el-upload
            :disabled="formData.identityFrontUrl && user.personAuthStatus == 1"
            :action="uploadImgUrl"
            :headers="headers"
            accept="image/png, image/jpeg"
            :before-upload="beforeUplod"
            :on-error="uploadError"
            :on-success="uploadFront"
            list-type="picture-card"
            :show-file-list="false"
          >
            <div v-show="!formData.identityFrontUrl" class="upload-tip">
              <i class="el-icon-plus"></i>
              <span>上传正面照</span>
            </div>
            <img
              v-show="!!formData.identityFrontUrl"
              :src="
                blobIdentityFrontUrl ? blobIdentityFrontUrl : identityFrontUrl
              "
              alt="身份证"
            />
          </el-upload>
          <el-upload
            :disabled="formData.identityBackUrl && user.personAuthStatus == 1"
            :action="uploadImgUrl"
            :headers="headers"
            accept="image/png, image/jpeg"
            :before-upload="beforeUplod"
            :on-error="uploadError"
            :on-success="uploadBack"
            list-type="picture-card"
            :show-file-list="false"
          >
            <div v-show="!formData.identityBackUrl" class="upload-tip">
              <i class="el-icon-plus"></i>
              <span>上传正面照</span>
            </div>
            <img
              v-show="!!formData.identityBackUrl"
              :src="blobIdentityBackUrl ? blobIdentityBackUrl : identityBackUrl"
              alt="身份证"
            />
          </el-upload>
        </el-form-item>
        <el-form-item>
          <span style="color: #999999">支持jpg、png等格式</span>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { InfoTitle } from '@/views/userInfo/components/components'
import { userSubmit, userAuthInfo } from '@/api/base/index.js'

export default {
  name: 'PersonInfo',
  components: { InfoTitle },
  data() {
    return {
      userInfo: {}, //用户信息
      avatarUrl: ['/images/nanAvatar.png', '/images/nvAvatar.png'], //头像默认图片
      uploadImgUrl: null, //上传服务器地址
      headers: {
        //图片上传数据
        Authorization: null,
      },
      formData: {
        idType: '中国大陆身份证', //身份证类型
        realName: null, //姓名
        idNumber: null, //身份证号
        identityFrontUrl: null, //身份证正面
        identityBackUrl: null, //身份证背面
      },
      IDTypes: [
        {
          id: 1,
          value: '中国大陆身份证',
          label: '中国大陆身份证',
        },
      ],
      rules: {
        realName: {
          trigger: ['change', 'blur'],
          validator: (rule, value, callback) => {
            if (!value || /[\\\\u4e00-\\\\u9fa5]/.test(value)) {
              callback(new Error('请输入正确姓名!'))
              return
            }
            callback()
          },
        },
        idNumber: {
          trigger: ['change', 'blur'],
          validator: (rule, value, callback) => {
            if (
              !value ||
              value.length !== 18 ||
              !/^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/.test(
                value
              )
            ) {
              callback(new Error('请输入正确身份证信息!'))
              return
            }
            callback()
          },
        },
      },
      blobIdentityFrontUrl: null, //身份证正面
      blobIdentityBackUrl: null, //身份证背面
      user: {}, //用户信息
    }
  },

  computed: {
    identityFrontUrl() {
      return this.formData.identityFrontUrl
    },
    identityBackUrl() {
      return this.formData.identityBackUrl
    },
  },
  created() {
    let that = this
    window.addEventListener('setItemEvent', function (e) {
      if (e.key === 'userInfo') {
        that.userInfo = JSON.parse(e.newValue)
      }
    })
    that.userInfo = JSON.parse(localStorage.getItem('userInfo'))

    this.uploadImgUrl =
      process.env.VUE_APP_ENV === 'development'
        ? '/dev-api/vm/aiVisit/common/uploadFile'
        : '/vm/aiVisit/common/uploadFile'
    this.headers.Authorization = localStorage.getItem('id_token')
    this.user = this.userInfo
    this.getAuthInfo() //获取用户信息
  },
  methods: {
    //获取个人认证信息
    getAuthInfo() {
      userAuthInfo().then((res) => {
        if (res.code == 200 && res.data) {
          for (const key in this.formData) {
            this.formData[key] = res.data[key]
          }
        }
      })
    },
    //身份证上传前
    beforeUplod(file) {
      const file_size = file.size / 1024 / 1024
      if (file_size >= 10) {
        this.$message({
          type: 'warning',
          message: '上传文件不能超过10M',
        })
        return false
      }
    },
    //上传失败
    uploadError() {
      // loading动画消失
      this.$message({
        message: '照片上传失败',
        type: 'error',
      })
    },
    //身份证正面上传
    uploadFront(res, file) {
      if (res.code == 200) {
        this.blobIdentityFrontUrl = URL.createObjectURL(file.raw)
        this.formData.identityFrontUrl = res.data.url
      } else {
        this.formData.identityFrontUrl = file.url
        this.$message({
          message: '上传失败',
          type: 'error',
        })
      }
    },
    //身份证反面
    uploadBack(res, file) {
      if (res.code == 200) {
        this.blobIdentityBackUrl = URL.createObjectURL(file.raw)
        this.formData.identityBackUrl = res.data.url
      } else {
        this.formData.identityBackUrl = file.url
        this.$message({
          message: '上传失败',
          type: 'error',
        })
      }
    },
    /**
     * @name: submitCertification
     * @msg: 实名认证
     * @param {*}
     * @return {*}
     */
    //提交个人认证
    submitCertification() {
      if (this.user.personAuthStatus == 1) {
        this.$message.success('已实名认证!')
        return
      }
      if (!this.formData.identityFrontUrl || !this.formData.identityBackUrl) {
        this.$message({
          type: 'warning',
          message: '请先上传个人身份证',
        })
        return
      }
      this.$refs.form.validate((bol) => {
        if (bol) {
          this.$confirm('是否确认实名认证?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          })
            .then(() => {
              userSubmit(this.formData).then((res) => {
                if (res.code == 200) {
                  this.$message.success('个人实名认证信息验证成功!')
                  this.$emit('back')
                } else {
                  this.$message.warning('个人实名认证信息验证失败!')
                }
              })
            })
            .catch(() => {
              this.$message({
                type: 'info',
                message: '已取消认证',
              })
            })
        } else {
          this.$message({
            message: '请先完成填写',
            type: 'warning',
          })
        }
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
  margin-left: 11.25rem !important;
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

:deep .upload {
  .el-form-item__content {
    display: flex;

    .el-upload {
      margin-left: 20px;
      width: 330px;
      height: 166px;
      background: #f4f6f9;
      border-radius: 6px;
      border: 1px dashed rgba(151, 151, 151, 0.5);

      .upload-tip {
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;

        span {
          line-height: initial;
          margin-top: 14px;
        }
      }

      img {
        width: 100%;
        height: 100%;
      }
    }
  }

  .el-form-item__content > div:first-child .el-upload {
    margin-left: 0;
  }
}
</style>
