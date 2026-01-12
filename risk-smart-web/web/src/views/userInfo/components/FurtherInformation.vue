<template>
  <div class="further-information">
    <InfoTitle title="个人信息">
      <template slot="right">
        <el-button type="primary" @click="submit"> 确认</el-button>
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
        label-width="90px"
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
              :src="formData.avatar || avatarUrl[Number(formData.sex) || 0]"
            />
          </div>
        </el-form-item>
        <el-form-item label="姓名:" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="职务:" prop="duties">
          <el-input v-model="formData.duties" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="手机号:" prop="phonenumber">
          <el-input
            :value="setSensitiveWord(formData.phonenumber, 3, 4, '****')"
            disabled
          />
        </el-form-item>
        <el-form-item label="昵称:" prop="nickName">
          <el-input v-model="formData.nickName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="微信号:" prop="weChat">
          <el-input v-model="formData.weChat" placeholder="请输入微信号" />
        </el-form-item>
        <el-form-item label="邮箱:" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="性别:" prop="sex">
          <div class="sex-tab">
            <div
              class="sex-tab-item"
              :class="{ 'sex-tab-item-active': formData.sex == sexItem.value }"
              @click="formData.sex = sexItem.value"
              v-for="(sexItem, sexIndex) in sexList"
              :key="sexIndex"
            >
              {{ sexItem.label }}
            </div>
          </div>
        </el-form-item>
        <el-form-item label="工作年限:" prop="workingSeniority">
          <el-input
            v-model="formData.workingSeniority"
            placeholder="请输入工作年限"
          >
            <span
              slot="suffix"
              style="
                font-weight: 500;
                font-size: 14px;
                color: #414355;
                line-height: 20px;
              "
              >年</span
            >
          </el-input>
        </el-form-item>
        <el-form-item label="个人简介:" prop="introduction">
          <el-input
            type="textarea"
            resize="none"
            v-model="formData.introduction"
            placeholder="请输入个人简介内容"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="个人标签(最多4个):" prop="tagCheckbox">
          <el-checkbox-group v-model="formData.tagCheckbox" :max="4">
            <el-checkbox
              :label="tagItem.dictValue"
              border
              v-for="(tagItem, tagIndex) in tagsList"
              :key="tagIndex"
            >
              {{ tagItem.dictLabel }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <div
          class="form-title"
          style="
            font-weight: 500;
            font-size: 16px;
            color: #191c31;
            line-height: 22px;
            margin-bottom: 30px;
          "
        >
          机构信息
        </div>
        <el-form-item label="机构名称:" prop="institutionName">
          <el-select
            v-model="formData.institutionName"
            filterable
            :filter-method="institutionNameFilter"
            @change="institutionNameChange"
            placeholder="请输入机构名称"
            :disabled="institutionNameDisabled"
          >
            <el-option
              v-for="item in organizationOptions"
              :key="item"
              :label="item"
              :value="item"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          label="服务区域:"
          prop="institutionServiceArea.provinceId"
        >
          <el-cascader
            v-model="ServiceArea"
            :options="areaOptions"
            :props="{ value: 'code', label: 'name', children: 'childs' }"
            @change="cascaderChange"
          ></el-cascader>
        </el-form-item>
        <el-form-item label="机构地址:" prop="institutionAddress">
          <el-input
            v-model="formData.institutionAddress"
            placeholder="请输入机构地址"
          />
        </el-form-item>
        <el-form-item label="机构简介:" prop="institutionIntroduction">
          <el-input
            type="textarea"
            resize="none"
            v-model="formData.institutionIntroduction"
            placeholder="请输入机构简介内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item>
          <div slot="label" style="line-height: 17px">
            机构相册<br />({{ formData.institutionPhotoStrList.length }}/6):
          </div>
          <template>
            <div
              style="
                font-weight: 400;
                font-size: 14px;
                color: rgba(0, 0, 0, 0.6);
                line-height: 20px;
                margin-bottom: 10px;
              "
            >
              建议上传机构前台、办公室等照片
            </div>
            <div
              class="license-img el-upload-list--picture-card"
              v-if="formData.institutionPhotoStrList.length"
            >
              <div
                class="el-upload-list__item"
                v-for="(
                  photoItem, photoIndex
                ) in formData.institutionPhotoStrList"
                :key="photoIndex"
              >
                <el-image :src="photoItem" fit="scale-down" />
                <div class="el-upload-list__item-actions">
                  <span
                    class="el-upload-list__item-delete"
                    @click="handleRemove(photoIndex)"
                  >
                    <i class="el-icon-delete"></i>
                  </span>
                </div>
              </div>
            </div>
            <el-upload
              :action="uploadImgUrl"
              :headers="headers"
              accept=".jpg,.png,.jpeg"
              list-type="picture-card"
              :on-success="institutionImgSuccess"
              :before-upload="beforeUpload"
              class="upload-demo-2"
              :class="{
                'upload-hidden': formData.institutionPhotoStrList.length >= 6,
              }"
            >
              <div class="upload-tip">
                <div class="el-icon-plus" />
                <div class="upload-tip-text">上传照片</div>
              </div>
            </el-upload>
            <div class="upload-footer">
              注：图片内容不得含有敏感信息、二维码和联系方式（手机号、微信、QQ邮箱等），一经审核予以驳回。
            </div>
          </template>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { dictType } from '@/api'
import {
  getInstitutionInfo,
  initChatUserinfo,
  institutionSubmit,
  selectInstitution,
  updateInstitutionInfo,
} from '@/api/base'
import { getRegionData } from '@/api/financing/productConfiguration'
import { getInstitutionalName } from '@/api/guideApi'
import { getGongShang } from '@/api/risk/search'
import { InfoTitle } from '@/views/userInfo/components/components'

export default {
  components: { InfoTitle },
  data() {
    let checkWeChat = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('微信号不能为空'))
      } else if (value && isNaN(value)) {
        callback(`微信号仅支持数字`)
      } else {
        callback()
      }
    }
    let checkWorkingSeniority = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('工作年限不能为空'))
      } else if (value && isNaN(value) && value < 0) {
        callback(`工作年限仅支持正整数`)
      } else if (value.indexOf('.') >= 0) {
        callback(`工作年限仅支持正整数`)
      } else {
        callback()
      }
    }
    return {
      avatarUrl: [
        'https://www.valuemap.cn/ybxfile/group2/M00/15/6E/ChdWRGYLmXKANKCjAAAUoXQTO0s108.png',
        'https://www.valuemap.cn/ybxfile/group2/M00/15/6E/ChdWRGYLmYmAdnpCAAAR0H8abmY713.png',
      ], //头像默认图片
      uploadImgUrl: null, //上传服务器地址
      headers: {
        //图片上传数据
        Authorization: null,
      },
      formData: {
        avatar: null, //头像
        realName: undefined, //姓名
        duties: undefined, //职务
        phonenumber: null, //手机号
        nickName: null, //昵称
        weChat: undefined, //微信号
        email: null, //邮箱
        sex: 0, //性别
        workingSeniority: undefined, //工作年限
        introduction: undefined, //个人简介
        tag: '',
        tagCheckbox: [],
        institutionName: '', //机构名称
        institutionServiceArea: {
          provinceId: '',
          provinceName: '',
          cityId: '',
          cityName: '',
        },
        institutionAddress: undefined, //机构地址
        institutionIntroduction: undefined, //机构简介
        institutionPhotoStrList: [],
      },
      rules: {
        realName: [
          {
            required: true,
            message: '个人姓名不能为空',
            trigger: ['blur', 'change'],
          },
        ],
        duties: [
          {
            required: true,
            message: '职务不能为空',
            trigger: ['blur', 'change'],
          },
        ],
        nickName: [
          {
            required: true,
            message: '昵称不能为空',
            trigger: ['blur', 'change'],
          },
        ],
        weChat: [{ validator: checkWeChat, trigger: ['blur', 'change'] }],
        email: [
          {
            required: true,
            message: '邮箱不能为空',
            trigger: ['blur', 'change'],
          },
          {
            type: 'email',
            message: '请输入正确的邮箱格式',
            trigger: ['blur', 'change'],
          },
        ],
        workingSeniority: [
          { validator: checkWorkingSeniority, trigger: ['blur', 'change'] },
        ],
        introduction: [
          {
            required: true,
            message: '个人简介不能为空',
            trigger: ['blur', 'change'],
          },
        ],
        tagCheckbox: [
          {
            required: true,
            message: '个人标签不能为空',
            trigger: ['change'],
          },
        ],
        institutionName: [
          {
            required: true,
            message: '机构名称不能为空',
            trigger: ['blur', 'change'],
          },
        ],
        'institutionServiceArea.provinceId': [
          {
            required: true,
            message: '服务区域不能为空',
            trigger: ['blur', 'change'],
          },
        ],
        institutionAddress: [
          {
            required: true,
            message: '机构地址不能为空',
            trigger: ['blur', 'change'],
          },
        ],
        institutionIntroduction: [
          {
            required: true,
            message: '机构简介不能为空',
            trigger: ['blur', 'change'],
          },
        ],
      },
      sexList: [
        { label: '男', value: 0 },
        { label: '女', value: 1 },
      ],
      tagsList: [],
      organizationOptions: [], //机构列表数据
      ServiceArea: [], //服务地域v-model绑定
      areaOptions: [],
      institutionNameDisabled: false, //是否禁用机构名称输入
    }
  },
  created() {
    this.uploadImgUrl =
      process.env.VUE_APP_ENV === 'production'
        ? '/prod-api/finance/common/uploadFile'
        : '/dev-api/finance/common/uploadFile'
    this.headers.Authorization = localStorage.getItem('id_token')
  },
  mounted() {
    getRegionData().then((res) => {
      if (res.code === 200)
        this.areaOptions = res.data.map((item) => {
          return {
            ...item,
            childs: item.childs
              ? item.childs.map((item2) => {
                  return {
                    ...item2,
                    childs: null,
                  }
                })
              : null,
          }
        })
    })
    dictType('sys_user_auth_person_financing_expert').then((res) => {
      if (res.code === 200) {
        this.tagsList = res.data
      }
    })
  },
  watch: {
    '$store.state.userInfo': {
      handler(newV) {
        getInstitutionalName().then((res) => {
          if (res.code === 200) {
            this.formData.institutionName = res.data
              ? res.data.institutionalName
              : ''
            this.institutionNameDisabled = !!res.data
          }
        })
        let { avatar, duties, phonenumber, nickName, email, sex } = newV
        getInstitutionInfo().then((res) => {
          if (res.code === 200 && res.data) {
            this.ServiceArea = []
            let { institutionPhotoList, ...newData } = res.data
            let { institutionServiceArea } = res.data
            if (institutionServiceArea.cityId) {
              this.ServiceArea.push(institutionServiceArea.provinceId)
              this.ServiceArea.push(institutionServiceArea.cityId)
            } else {
              this.ServiceArea.push(institutionServiceArea.provinceId)
            }
            Object.assign(this.formData, {
              ...newData,
              institutionPhotoStrList: institutionPhotoList,
              tagCheckbox: res.data.tag.split(','),
              phonenumber,
            })
            this.institutionNameDisabled = !!this.formData.institutionName
          } else {
            if (this.formData.institutionName) {
              this.institutionNameChange(this.formData.institutionName)
            }
            Object.assign(this.formData, {
              avatar,
              duties,
              phonenumber,
              nickName,
              email,
              sex,
            })
            this.institutionNameDisabled = false
          }
        })
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    /**
     * 机构名称输入筛选
     * @param val
     */
    institutionNameFilter(val) {
      selectInstitution({
        entry: val,
      }).then((res) => {
        this.organizationOptions = res.data
      })
    },
    institutionNameChange(value) {
      getGongShang({ cname: value }).then((res) => {
        if (res.code === 200) {
          this.ServiceArea = []
          let { baseinfo } = res.data
          this.areaOptions.forEach((item) => {
            if (item.name === baseinfo.province) {
              this.ServiceArea.push(item.code)
              this.formData.institutionServiceArea.provinceId = item.code
              if (item.childs) {
                item.childs.forEach((item2) => {
                  if (item2.name === baseinfo.city) {
                    this.ServiceArea.push(item2.code)
                    this.formData.institutionServiceArea.cityId = item2.code
                  }
                })
              }
            }
          })
          this.formData.institutionServiceArea.provinceName = baseinfo.province
          this.formData.institutionServiceArea.cityName = baseinfo.city
          this.formData.institutionAddress = baseinfo.registAddress
          this.formData.institutionIntroduction = baseinfo.info
        }
      })
    },
    cascaderChange(value) {
      this.areaOptions.map((item) => {
        if (value[0] === item.code) {
          this.formData.institutionServiceArea.provinceId = item.code
          this.formData.institutionServiceArea.provinceName = item.name
          if (item.childs) {
            item.childs.map((child) => {
              if (value[1] === child.code) {
                this.formData.institutionServiceArea.cityId = child.code
                this.formData.institutionServiceArea.cityName = child.name
              }
            })
          } else {
            this.formData.institutionServiceArea.cityId = ''
            this.formData.institutionServiceArea.cityName = ''
          }
        }
      })
    },
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
      if (res.code === 200) {
        this.formData.avatar = res.data.url
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
    institutionImgSuccess(res) {
      if (res.code === 200)
        this.formData.institutionPhotoStrList.push(res.data.url)
    },
    /*
     * 上传文件之前
     * */
    beforeUpload(file) {
      const that = this
      //上传的文件类型
      const file_type = file.name
        .slice(file.name.lastIndexOf('.'))
        .toLocaleLowerCase()
      let acceptList = ['.jpg', '.png', '.jpeg']
      if (!acceptList.includes(file_type)) {
        that.$message.warning('文件类型错误！')
        return false
      }
    },
    /**
     * 删除已上传的文件
     */
    handleRemove(index) {
      this.formData.institutionPhotoStrList.splice(index, 1)
    },
    submit() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          let { avatar, phonenumber, tagCheckbox, ...data } = this.formData
          let tag = tagCheckbox.join(',')
          let API = this.formData.id ? updateInstitutionInfo : institutionSubmit
          API({
            avatar: avatar || this.avatarUrl[data.sex],
            ...data,
            tag,
          }).then((res) => {
            if (res.code === 200) {
              if (institutionSubmit == API) {
                initChatUserinfo({
                  identity: 'ACCOUNT_MANAGER',
                  userName: this.formData.realName,
                  toJob: this.formData.duties,
                  toInstitution: this.formData.institutionName,
                })
                  .then((res) => {})
                  .catch((err) => {})
              }
              this.$message.success('提交成功！')
              this.$router.push({ name: 'AIDueDiligence' })
            }
          })
        } else {
          return false
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
  height: calc(var(--bgvh) - 163px);
}

:deep .el-form-item__content {
  margin-left: 9.375rem !important;
}

::v-deep .el-form-item {
  .el-form-item__label {
    line-height: 48px;
  }

  &:has(.el-checkbox-group) {
    .el-form-item__label {
      line-height: 22px;
    }
  }

  .el-select,
  .el-cascader {
    width: 100%;
  }

  .el-input__inner,
  .el-textarea__inner,
  .el-input__count {
    background: #f4f6f9;
    border: none;
  }

  .el-input__inner {
    height: 48px;
    line-height: 48px;
  }

  .el-textarea__inner {
    height: 120px;
  }

  .el-input__suffix {
    right: 14px;
    line-height: 48px;
  }

  .el-checkbox__input.is-checked + .el-checkbox__label {
    color: #ffffff;
  }

  .el-checkbox {
    margin-right: 0;

    &:nth-child(6) {
      margin-left: 0;
    }

    .el-checkbox__inner {
      border: none;
      background: transparent;
    }

    &.is-checked {
      background: var(--primary-color);
      border-color: var(--primary-color);

      .el-checkbox__inner {
        background: transparent;
      }
    }
  }
}

.sex-tab {
  padding: 4px;
  display: inline-flex;
  font-weight: 400;
  font-size: 14px;
  line-height: 17px;
  border-radius: 6px;
  background: #f4f6f9;

  &-item {
    width: 75px;
    text-align: center;
    padding: 12px 0;
    color: #414355;
    border-radius: 6px;
    cursor: pointer;

    &-active {
      color: #ffffff;
      background: var(--primary-color);
    }
  }
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

.upload-demo-2 {
  ::v-deep .el-upload-list--picture-card {
    display: none;
  }

  ::v-deep .el-upload {
    width: 148px;
    height: 148px;
    background: rgba(40, 136, 232, 0.1);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    border-radius: 6px;
    border: 1px dashed rgba(40, 136, 232, 0.5);

    .upload-tip {
      display: flex;
      flex-direction: column;

      .el-icon-plus {
        font-weight: 600;
        font-size: 30px;
        color: var(--primary-color);
      }

      &-text {
        margin-top: 14px;
        font-size: 14px;
        font-weight: 400;
        color: var(--primary-color);
        line-height: 22px;
      }
    }
  }
}

.upload-hidden {
  display: none;
}

.upload-footer {
  margin-top: 20px;
  font-size: 14px;
  font-weight: 400;
  color: #999999;
  line-height: 22px;
}
</style>
