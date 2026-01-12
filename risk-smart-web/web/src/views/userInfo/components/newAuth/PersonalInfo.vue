<template>
  <div class="personalInfo">
    <main>
      <el-form
        ref="ruleForm"
        :model="formData"
        :rules="rules"
        label-width="90px"
      >
        <div class="title">个人信息</div>
        <el-form-item label="头像：">
          <div class="upload-avatar">
            <el-upload
              class="upload"
              :action="uploadImgUrl"
              :headers="headers"
              :on-success="quillImgSuccess"
              :on-error="uploadError"
              :before-remove="beforeRemove"
              :show-file-list="false"
              name="file"
              ref="upload"
            >
              <div class="edit"><i class="el-icon-edit" /></div>
            </el-upload>
            <div class="avatar">
              <el-image
                style="width: 100%; height: 100%"
                :src="formData.avatar || avatarUrl[Number(formData.sex) || 0]"
                fit="cover"
              ></el-image>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="昵称:" prop="nickName">
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
          <div class="selectSex">
            <div
              class="btn"
              :class="{ activeBtn: item.value == formData.sex }"
              v-for="(item, index) in sexList"
              :key="index"
              @click="
                () => {
                  formData.sex = item.value
                }
              "
            >
              {{ item.label }}
            </div>
          </div>
        </el-form-item>
        <!-- <template v-if="this.hasButton('Supplementary:information:show')">
          <div class="line" />
          <div class="title" style="margin-top: 40px">我的名片</div>
          <el-form-item label="真实姓名:" prop="realName">
            <el-input v-model="formData.realName" placeholder="请输入" />
          </el-form-item>
          <el-form-item label="职务:" prop="duties">
            <el-input v-model="formData.duties" placeholder="请输入" />
          </el-form-item>
          <el-form-item label="微信号:" prop="weChat">
            <el-input v-model="formData.weChat" placeholder="请输入微信号" />
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
                  line-height: 48px;
                  margin-right: 5px;
                "
                >年</span
              >
            </el-input>
          </el-form-item>
          <el-form-item label="个人简介:" prop="introduction">
            <el-input
              type="textarea"
              :rows="5"
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
                v-for="(tagItem, tagIndex) in tagsList"
                :key="tagIndex"
                :label="tagItem.dictValue"
                border
              >
                {{ tagItem.dictLabel }}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
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
              :rows="5"
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
              <MyUpload
                :uploadUrl="uploadImgUrl"
                :dataList.sync="formData.institutionPhotoStrList"
                :limit="6"
                :deleteImg="true"
                :showName="false"
                hasDelete
                :accept="'.jpg,.JPG,.jpeg,.JPEG,.png,.PNG'"
                :fileSize="10"
              >
                <div slot="upload" class="uploadImg">
                  <i class="el-icon-plus" />
                  <span>上传文件</span>
                </div>
              </MyUpload>
              <div class="upload-footer">
                注：图片内容不得含有敏感信息、二维码和联系方式（手机号、微信、QQ邮箱等），一经审核予以驳回。
              </div>
            </template>
          </el-form-item>
        </template> -->
      </el-form>
      <div class="bottomBtns">
        <el-button type="primary" @click="submit">保存</el-button>
      </div>
    </main>
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
import { updateUser } from '@/api/base/index.js'
import MyUpload from '@/components/gutu/gutuUpload.vue'

import { mapGetters } from 'vuex'
export default {
  components: {
    MyUpload,
  },
  data() {
    let checkWeChat = (rule, value, callback) => {
      let pattern = /^[a-zA-Z][a-zA-Z0-9_-]{5,19}$/
      if (!value) {
        return callback(new Error('微信号不能为空'))
      } else if (value && !pattern.test(value)) {
        callback(`请输入正确的微信号`)
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
      formData: {
        avatar: null, //头像
        realName: null, //姓名
        duties: null, //职务
        phonenumber: null, //手机号
        nickName: null, //昵称
        weChat: null, //微信号
        email: null, //邮箱
        sex: 0, //性别
        workingSeniority: null, //工作年限
        introduction: null, //个人简介
        tag: '',
        tagCheckbox: [],
        institutionName: '', //机构名称
        institutionServiceArea: {
          provinceId: '',
          provinceName: '',
          cityId: '',
          cityName: '',
        },
        institutionAddress: null, //机构地址
        institutionIntroduction: null, //机构简介
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
        weChat: [
          {
            required: true,
            validator: checkWeChat,
            trigger: ['blur', 'change'],
          },
        ],
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
          {
            required: true,
            validator: checkWorkingSeniority,
            trigger: ['blur', 'change'],
          },
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
      uploadImgUrl:
        (process.env.VUE_APP_ENV === 'production'
          ? '/prod-api'
          : '/dev-api') + '/bankCashflow/analysis/uploadFile',
      headers: { Authorization: localStorage.getItem('id_token') },
      avatarUrl: [
        'https://www.valuemap.cn/ybxfile/group2/M00/16/83/ChdWRGa99MiAHaoJAAAUoXQTO0s874.png?filename=20240815203000587326.png',
        'https://www.valuemap.cn/ybxfile/group2/M00/16/83/ChdWRGa99OmAOCJvAAAR0H8abmY524.png?filename=202408152030329974711.png',
        // require('/images/nanAvatar.png'),
        // require('/images/nvAvatar.png'),
      ],
      areaOptions: [],
      sexList: [
        { label: '男', value: 0 },
        { label: '女', value: 1 },
      ],
      tagsList: [],
      ServiceArea: [], //服务地域v-model绑定
      organizationOptions: [], //机构列表数据
      institutionNameDisabled: false,
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
  },
  watch: {
    userInfo: {
      handler(newV) {
        if (!newV) return
        let { avatar, duties, phonenumber, nickName, email, sex } = newV
        if (this.hasButton('Supplementary:information:show')) {
          getInstitutionalName().then((res) => {
            if (res.code === 200) {
              this.formData.institutionName = res.data
                ? res.data.institutionalName
                : ''
              this.institutionNameDisabled = !!res.data
            }
          })
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
                tagCheckbox: res.data.tag ? res.data.tag.split(',') : [],
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
        } else {
          Object.assign(this.formData, {
            avatar,
            duties,
            phonenumber,
            nickName,
            email,
            sex,
          })
        }
      },

      deep: true,
      immediate: true,
    },
  },
  mounted() {
    // getRegionData().then((res) => {
    //   if (res.code === 200)
    //     this.areaOptions = res.data.map((item) => {
    //       return {
    //         ...item,
    //         childs: item.childs
    //           ? item.childs.map((item2) => {
    //               return {
    //                 ...item2,
    //                 childs: null,
    //               }
    //             })
    //           : null,
    //       }
    //     })
    // })
    dictType('sys_user_auth_person_financing_expert').then((res) => {
      if (res.code === 200) {
        this.tagsList = res.data
        console.log(this.tagsList)
      }
    })
  },
  methods: {
    submit() {
      // console.log(this.formData.institutionPhotoStrList)
      // return
      if (this.hasButton('Supplementary:information:show'))
        this.$refs.ruleForm.validate((valid) => {
          if (valid) {
            let {
              avatar,
              phonenumber,
              tagCheckbox,
              institutionPhotoStrList,
              ...data
            } = this.formData
            let tag = tagCheckbox.join(',')
            let API = this.formData.id
              ? updateInstitutionInfo
              : institutionSubmit
            API({
              avatar: avatar || this.avatarUrl[data.sex],
              institutionPhotoStrList: institutionPhotoStrList.map(
                (item) => item.url
              ),
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
                    .then((res) => {
                      this.$message.success('提交成功！')
                    })
                    .catch((err) => {})
                } else {
                  this.$message.success('提交成功！')
                }
                // this.$message.success('提交成功！')
                // this.$router.push({ name: 'AIDueDiligence' })
                // window.location.reload()
              }
            })
          } else {
            return false
          }
        })
      else {
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
              message: '已成功修改信息!',
            })
            setTimeout(() => {
              location.reload()
            }, 1000)
          })
          .catch(() => {})
      }
    },
    quillImgSuccess(res, file) {
      if (res.code == 200) {
        this.blobUrl = URL.createObjectURL(file.raw)
        this.formData.avatar = res.url
      } else {
        this.$message({
          message: '上传失败',
          type: 'error',
        })
      }
    },
    uploadError() {
      this.$message({
        message: '文件导入失败',
        type: 'error',
      })
    },
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
  },
}
</script>

<style lang="less" scoped>
.personalInfo {
  width: 100%;
  height: calc(var(--bgvh) - 150px);
  overflow-y: auto;

  main {
    padding: 20px 30px;
    position: relative;
    font-family: PingFang SC-Regular;

    .title {
      height: 30px;
      border-left: 2px solid var(--primary-color);
      padding-left: 20px;
      font-family: PingFang SC-Medium;
      font-size: 18px;
      color: rgba(#000, 0.85);
      font-weight: 500;
      margin-bottom: 30px;
    }

    ::v-deep(.el-form) {
      .el-form-item {
        &:has(.el-checkbox-group) {
          .el-form-item__label {
            line-height: 22px;
          }
        }

        .el-form-item__label {
          line-height: 48px;
        }

        .el-form-item__content {
          width: 580px;
        }
      }

      .el-input,
      .el-textarea {
        .el-input__inner,
        .el-textarea__inner,
        .el-input__count {
          background-color: #f4f6f9;
        }

        .el-input__inner {
          height: 48px;
        }
      }

      .el-select,
      .el-cascader {
        width: 100%;
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

          .el-checkbox__label {
            color: #fff;
          }

          .el-checkbox__inner {
            background: transparent;
          }
        }
      }
    }

    .upload-avatar {
      width: 130px;
      height: 130px;
      position: relative;

      .upload {
        position: absolute;
        width: 26px;
        height: 26px;
        top: -13px;
        right: -13px;
        z-index: 99;
        border-radius: 50%;
        background-color: var(--primary-color);
        display: flex;
        align-items: center;
        justify-content: center;

        .el-icon-edit {
          font-size: 16px;
          color: #fff;
        }
      }

      .avatar {
        width: 100%;
        height: 100%;
        border-radius: 6px;
        border: 1px solid rgba(#000, 0.1);
        overflow: hidden;
      }
    }

    .selectSex {
      display: flex;
      align-items: center;
      width: 160px;
      height: 48px;
      padding: 5px;
      background-color: #f4f6f9;

      .btn {
        width: 75px;
        height: 40px;
        border-radius: 6px;
        font-size: 14px;
        color: #414355;
        display: flex;
        justify-content: center;
        align-items: center;
      }

      .activeBtn {
        background-color: var(--primary-color);
        color: #fff;
      }
    }

    .line {
      position: absolute;
      width: 100%;
      height: 2px;
      left: 0;
      background-color: #eeeeee;
    }

    .uploadImg {
      width: 150px;
      height: 150px;
      border: 1px dashed rgba(40, 136, 232, 0.5);
      margin: 5px;
      border-radius: 6px;
      display: flex;
      align-content: center;
      justify-content: center;
      flex-direction: column;

      .el-icon-plus {
        font-weight: 600;
        font-size: 30px;
        color: var(--primary-color);
      }

      > span {
        margin-top: 14px;
        font-size: 14px;
        font-weight: 400;
        color: var(--primary-color);
        line-height: 22px;
      }
    }

    .upload-footer {
      margin-top: 20px;
      font-size: 14px;
      font-weight: 400;
      color: #999999;
      line-height: 22px;
    }

    .bottomBtns {
      margin-top: 30px;
      width: 100%;
      padding: 0px 90px;

      .el-button {
        width: 96px;
        height: 50px;
        font-family: PingFang SC-Medium;
        font-size: 18px;
        font-weight: 500;
      }
    }
  }
}

/* 修改垂直滚动条 */
::-webkit-scrollbar {
  width: 4px;
  /* 修改宽度 */
}

/* 修改滚动条轨道背景色 */
::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

/* 修改滚动条滑块颜色 */
::-webkit-scrollbar-thumb {
  background-color: #888;
}

/* 修改滚动条滑块悬停时的颜色 */
::-webkit-scrollbar-thumb:hover {
  background-color: #555;
}
</style>
