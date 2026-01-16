<template>
  <div style="position: relative">
    <el-drawer
      :title="
        isEdit
          ? $t('interfacePlatform.editDataScenario')
          : $t('interfacePlatform.addDataScenario')
      "
      :visible.sync="this.drawer"
      :destroy-on-close="true"
      :hide-required-asterisk="false"
      :modal="false"
      :wrapperClosable="false"
      size="40%"
      :before-close="resetFields"
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="ruleForm"
        :label-width="isEnglish() ? '150px' : '120px'"
        label-position="left"
      >
        <el-form-item
          :label="$t('interfacePlatform.dataScenarioName')"
          prop="dataName"
        >
          <el-input v-model="form.dataName"></el-input>
        </el-form-item>

        <el-form-item
          :label="$t('interfacePlatform.source')"
          prop="source"
          style="margin-top: 20px"
        >
          <el-input v-model="form.source"></el-input>
        </el-form-item>
        <el-form-item
          :label="$t('interfacePlatform.website')"
          prop="webLink"
          style="margin-top: 20px"
        >
          <el-input v-model="form.webLink"></el-input>
        </el-form-item>
        <el-form-item
          :label="$t('interfacePlatform.adminName')"
          prop="adminName"
          style="margin-top: 20px"
        >
          <el-input v-model="form.adminName"></el-input>
        </el-form-item>
        <el-form-item
          :label="$t('interfacePlatform.contactDetails')"
          prop="contactDetails"
          style="margin-top: 20px"
        >
          <el-input v-model="form.contactDetails"></el-input>
        </el-form-item>
        <el-form-item
          :label="$t('interfacePlatform.dataType')"
          prop="interfaceDataType"
          style="margin-top: 20px"
        >
          <el-select v-model="form.interfaceDataType" disabled>
            <el-option
              :label="$t('interfacePlatform.metadata')"
              :value="0"
            ></el-option>
            <el-option
              :label="$t('interfacePlatform.featureVariable')"
              :value="1"
            ></el-option>
            <el-option
              :label="$t('interfacePlatform.analysisTarget')"
              :value="2"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div class="bottomBtns">
        <el-button type="primary" @click="comfirm('ruleForm')">{{
          $t('common.sure')
        }}</el-button>
        <el-button @click="resetFields">{{ $t('common.cancel') }}</el-button>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import Headline from '@/components/Headline'

import { getToken } from '@/utils/auth'
import {
  saveSourceInfo,
  updateSourceInfo,
} from '@/views/interfacePlatform/api/dataList'

export default {
  components: { Headline },
  props: {
    isDetail: {
      // 查看当前是否是详情模块
      type: Boolean,
      default: false,
    },
    isEdit: {
      type: Boolean, // 查看当前是否是编辑
      default: false,
    },
  },
  data() {
    return {
      form: {},
      tabData: [],
      activeName: '',
      headers: {
        Authorization: 'Bearer ' + getToken(),
      },
      rules: {},
      drawer: false,
    }
  },
  computed: {
    rules() {
      return {
        dataName: [
          {
            required: true,
            message: this.$t('interfacePlatform.inputScenarioName'),
            trigger: 'blur',
          },
          {
            min: 1,
            max: 30,
            message: this.$t('interfacePlatform.length1to30'),
            trigger: 'blur',
          },
        ],
        source: [
          {
            required: true,
            message: this.$t('interfacePlatform.inputSource'),
            trigger: 'blur',
          },
          {
            min: 1,
            max: 30,
            message: this.$t('interfacePlatform.length1to30'),
            trigger: 'blur',
          },
        ],
        webLink: {
          required: true,
          message: this.$t('interfacePlatform.inputWebsite'),
          trigger: 'blur',
        },
        adminName: [
          {
            required: true,
            message: this.$t('interfacePlatform.inputAdminName'),
            trigger: 'blur',
          },
          {
            min: 1,
            max: 30,
            message: this.$t('interfacePlatform.length1to30'),
            trigger: 'blur',
          },
        ],
        contactDetails: [
          {
            required: true,
            message: this.$t('interfacePlatform.inputPhone'),
            trigger: 'blur',
          },
          {
            required: true,
            pattern: /^1[3456789]\d{9}$/,
            message: this.$t('interfacePlatform.phoneFormatError'),
            trigger: 'blur',
          },
        ],
        interfaceDataType: {
          required: true,
          message: this.$t('interfacePlatform.selectDataType'),
          trigger: 'change',
        },
      }
    },
  },
  mounted() {},
  methods: {
    getInfo(row) {
      this.form = { ...row }
      this.form.sourceNo = row.interfaceSourceNo
      this.$set(this.form, 'interfaceDataType', Number(row.interfaceDataType))
    },
    resetFields() {
      this.drawer = false
      this.form = {}
    },

    // 保存
    comfirm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.isEdit) {
            updateSourceInfo({ ...this.form }).then((res) => {
              if (res.code == 200) {
                this.resetFields()
                this.$message.success(
                  this.$t('interfacePlatform.modifySuccess')
                )
                this.$emit('getList')
              }
            })
          } else {
            saveSourceInfo({ ...this.form }).then((res) => {
              if (res.code == 200) {
                this.resetFields()
                this.$message.success(this.$t('interfacePlatform.addSuccess'))
                this.$emit('getList')
              }
            })
          }
        } else {
          return false
        }
      })
    },
  },
}
</script>

<style lang="less" scoped>
// @import '@/assets/scss/productConfiguration';

::v-deep .el-form-item__label {
  justify-content: flex-end;
  padding-right: 5px;
  box-sizing: border-box;
}

.demo-image {
  display: flex;

  .text {
    margin-bottom: 10px;
    color: rgba(0, 0, 0, 0.85);
    font-size: 14px;
  }

  .left {
    flex: 1;
  }

  .right {
    flex: 1;
  }
}

// ::v-deep .el-form-item {
//   // height: 60px !important;
//   margin-bottom: 0px;
// }

// ::v-deep .el-form .ets {
//   height: 200px !important;
// }

.table_record {
  margin-top: 20px;
  margin-left: 10px;
  width: 100%;
  background-color: #fff;

  .record_list {
    width: 100%;
    height: 150px;
  }
}

.bottomBtns {
  margin-top: 20px;
  width: 100%;
  display: flex;
  justify-content: flex-end;
  .el-button {
    padding: 10px 20px;
  }
}

.record_lable {
  font-size: 15px;
  font-family: PingFang SC-Medium, PingFang SC;
  font-weight: 500;
  margin-bottom: 20px;
}

.detailText {
  font-size: 14px;
  font-family: PingFang SC-Medium, PingFang SC;
  font-weight: 500;
  color: #414355;
  line-height: 48px;
}

.uploadFile {
  width: 352px;
  height: 140px;
  border-radius: 4px 4px 4px 4px;
}

::v-deep .action-btn {
  padding: 12px 5px 12px 9px;
  background: #3d7fff;
  border: none;
  border-radius: 18px 0 0 18px;

  span {
    display: inline-flex;
    justify-content: center;
    flex-direction: column;

    div {
      display: inline-block;
      font-size: 14px;
      letter-spacing: 6px;
      writing-mode: vertical-lr;
    }

    img {
      display: inline-block;
      margin-top: 2px;
      width: 18px;
      height: 18px;
    }
  }
}

.head-line {
  margin-bottom: 22px;
}

.feedback-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-top: 125px;

  &-text {
    font-size: 18px;
    font-weight: 500;
    color: #333333;
    line-height: 27px;
    text-align: center;

    i {
      font-size: 72px;
      color: #00b578;
      margin-bottom: 20px;
    }
  }

  .drawer-footer {
    margin-top: 20px;

    .el-button {
      padding: 11px 14px;
    }

    .el-button + .el-button {
      margin-left: 17px;
    }
  }
}

.feed-method {
  .feed-method-tab + .feed-method-tab {
    margin-top: 20px;
  }

  &-tab {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 30px 15px 30px 20px;
    border-radius: 6px;
    background: rgba(0, 0, 0, 0.04);
    border: 1px solid transparent;

    &-left {
      display: flex;
      align-items: center;
      font-size: 16px;
      font-weight: 500;
      line-height: 22px;
      color: rgba(0, 0, 0, 0.85);

      img {
        height: 40px;
        margin-right: 9px;
      }
    }

    &-right {
      cursor: pointer;
      display: flex;
      align-items: center;
      font-size: 12px;
      font-weight: 400;
      line-height: 18px;
      color: rgba(0, 0, 0, 0.6);

      img {
        width: 18px;
        height: 18px;
      }
    }
  }

  .tab-active {
    border-color: var(--primary-color);
    background: rgba(40, 136, 232, 0.04);

    .feed-method-tab-left {
      color: var(--primary-color);
    }
  }
}

.titleWarp {
  display: flex;

  .text {
    font-size: 16px;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: rgba(0, 0, 0, 0.85);
    margin-left: 16px;
  }

  .icon {
    width: 22px;
    height: 22px;
  }
}

.hint {
  font-size: 14px;
  font-family: PingFang SC-Regular, PingFang SC;
  font-weight: 400;
  color: #666666;
  margin-left: 58px;
  margin-bottom: 30px;
}

::v-deep .el-dialog {
  width: 432px !important;
  height: 170px !important;
}

::v-deep .el-dialog__body {
  padding: 0px !important;
}

.file-name {
  width: 100%;
  height: 32px;
  background-color: rgb(246, 249, 253);
  color: rgba(0, 0, 0, 0.85);
  font-size: 14px;
  line-height: 32px;
  padding-left: 10px;
  box-sizing: border-box;
}

::v-deep .el-switch {
  vertical-align: bottom;
  margin-left: 20px;
}

::v-deep .el-form-item .el-input__inner {
  padding: 14px;
  width: 100%;
}

::v-deep .el-input-number--small {
  line-height: 44px;
}

::v-deep .el-select {
  width: 100%;
}
</style>
