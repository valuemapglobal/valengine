<template>
  <div style="position: relative">
    <el-drawer
      class="feed-back-drawer"
      :visible.sync="this.drawer"
      show-close
      :destroy-on-close="true"
      :hide-required-asterisk="false"
      :with-header="false"
      :modal="false"
      :wrapperClosable="false"
      size="30%"
    >
      <Headline @click="resetFields">{{
        isEdit ? '编辑参数' : '新增参数'
      }}</Headline>
      <!--  -->
      <el-form
        :model="form"
        :rules="rules"
        ref="ruleForm"
        label-width="auto"
        label-position="left"
        style="margin-top: 38px"
      >
        <el-form-item
          label="参数名称："
          prop="interfaceFieIdName"
          style="margin-top: 20px"
        >
          <el-input v-model="form.interfaceFieIdName"></el-input>
        </el-form-item>

        <el-form-item
          label="参数别名："
          prop="interfaceFieIdAlias"
          style="margin-top: 20px"
        >
          <el-input v-model="form.interfaceFieIdAlias"></el-input>
        </el-form-item>
        <el-form-item
          label="参数说明："
          prop="interfaceFieIdDescription"
          style="margin-top: 20px"
        >
          <el-input v-model="form.interfaceFieIdDescription"></el-input>
        </el-form-item>
        <el-row type="flex" class="row-bg" justify="center" :gutter="20">
          <el-col :span="16">
            <el-form-item
              label="参数类型："
              prop="interfaceFieIdType"
              style="margin-top: 20px"
            >
              <el-select v-model="form.interfaceFieIdType">
                <el-option label="入参" :value="0"></el-option>
                <el-option label="出参" :value="1"></el-option>
              </el-select> </el-form-item
          ></el-col>
          <el-col
            :span="8"
            style="
              display: flex;
              align-items: center;
              flex-direction: column;
              justify-content: center;
            "
          >
            <el-radio-group
              v-model="form.interfaceFieIdRequired"
              v-if="showRadio"
            >
              <el-radio :label="0">必填</el-radio>
              <el-radio :label="1">非必填</el-radio>
            </el-radio-group>
            <div
              class="msg"
              style="font-size: 12px; color: red; margin-top: 10px"
              v-if="showRadio && showRadioMsg"
            >
              请选择是否必填
            </div>
          </el-col>
        </el-row>
        <el-form-item
          label="数据类型："
          prop="interfaceFieIdDataType"
          style="margin-top: 20px"
        >
          <el-select v-model="form.interfaceFieIdDataType">
            <el-option
              v-for="(item, index) in dataTypeList"
              :key="index"
              :label="item.dictLabel"
              :value="item.dictValue"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          label="排序："
          prop="interfaceFieIdIndex"
          style="margin-top: 20px"
        >
          <el-input v-model="form.interfaceFieIdIndex"></el-input>
        </el-form-item>
        <el-form-item
          label="参数备注："
          prop="interfaceFieIdRemark"
          style="margin-top: 20px"
        >
          <el-input v-model="form.interfaceFieIdRemark"></el-input>
        </el-form-item>
        <el-form-item
          label="默认值："
          prop="interfaceFieIdDefultValue"
          style="margin-top: 20px"
        >
          <el-input v-model="form.interfaceFieIdDefultValue"></el-input>
        </el-form-item>
        <el-form-item
          label="父级属性："
          prop="interfaceFieIdFather"
          style="margin-top: 20px"
        >
          <el-input v-model="form.interfaceFieIdFather"></el-input>
        </el-form-item>
      </el-form>
      <div class="table_button">
        <div class="table_button_right">
          <el-button class="button_btn" @click="comfirm('ruleForm')"
            >确定</el-button
          >
          <el-button class="button_close" @click="resetFields">关闭</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import Headline from '@/components/Headline'

import { getToken } from '@/utils/auth'
import {
  saveInterfaceFieIdInfo,
  updateInterfaceFieIdInfo,
} from '@/views/interfacePlatform/api/dataList'
import { getDicts } from '@/api/index'

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
    manageNo: {
      type: String,
      required: true,
    },
    interfaceNo: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      showRadioMsg: false, // 单选框 是否必填
      showRadio: false,
      form: {
        interfaceFieIdRequired: 1,
      },
      tabData: [],
      activeName: '',
      headers: {
        Authorization: 'Bearer ' + getToken(),
      },
      rules: {
        interfaceFieIdName: [
          { required: true, message: '请输入参数名称', trigger: 'blur' },
          { min: 1, max: 50, message: '长度在 1到 50个字符', trigger: 'blur' },
        ],
        interfaceFieIdAlias: [
          { required: true, message: '请输入参数别名', trigger: 'blur' },
          { min: 1, max: 50, message: '长度在 1到 50个字符', trigger: 'blur' },
        ],
        interfaceFieIdDescription: [
          { required: true, message: '请输入参数说明', trigger: 'blur' },
          {
            min: 1,
            max: 200,
            message: '长度在 1到 200个字符',
            trigger: 'blur',
          },
        ],
        interfaceFieIdType: {
          required: true,
          message: '请选择参数类型',
          trigger: 'change',
        },
        interfaceFieIdIndex: {
          required: true,
          pattern: /^[0-9]*$/,
          message: '只能输入数字',
          trigger: 'blur',
        },
        interfaceFieIdRemark: [
          { required: true, message: '请输入备注', trigger: 'blur' },
          { min: 1, max: 30, message: '长度在 1到 30个字符', trigger: 'blur' },
        ],
        interfaceFieIdDefultValue: [
          // { required: true, message: '请输入默认值', trigger: 'blur' },
          {
            min: 1,
            max: 200,
            message: '长度在 1到 200个字符',
            trigger: 'blur',
          },
        ],
      },
      dataTypeList: [],
      drawer: false,
    }
  },
  mounted() {
    getDicts('decision_data_type')
      .then((res) => {
        if (res.code == 200) {
          let data = res.data.map((item) => {
            item.dictValue = parseInt(item.dictValue)
            return item
          })
          console.log(data, 'data---')
          this.dataTypeList = data
        }
      })
      .catch((err) => {})
  },
  watch: {
    form: {
      handler(n, o) {
        if (n.interfaceFieIdType === 0) {
          this.showRadio = true
        } else {
          this.showRadio = false
        }
      },
      deep: true,
    },
  },
  methods: {
    getInfo(row) {
      this.form = { ...row }
      this.form.manageNo = row.interfaceManageNo
      this.form.fieIdNo = row.interfaceFieIdManage
      // this.form.sourceNo = row.interfaceManageNo
      // this.$set(this.form, 'interfaceDataType', Number(row.interfaceDataType))
    },
    resetFields() {
      this.drawer = false
      this.form = {
        interfaceFieIdRequired: 1,
      }
    },

    // 保存
    comfirm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // console.log('3333', this.showRadio, this.form.interfaceFieIdRequired)
          // if (this.showRadio && !this.form.interfaceFieIdRequired && this.form.interfaceFieIdRequired !== 0) {
          //   this.showRadioMsg = true
          //   return
          // } else {
          //   this.showRadioMsg = false
          //   this.form.interfaceFieIdRequired = 1 // 没有意义的参数 ，不传报错
          // }
          if (this.isEdit) {
            updateInterfaceFieIdInfo({
              ...this.form,
              manageNo: this.manageNo,
              interfaceNo: this.interfaceNo,
            })
              .then((res) => {
                if (res.code == 200) {
                  this.resetFields()
                  this.$message.success('修改成功')
                  this.$emit('getList')
                }
              })
              .catch((msg) => {
                this.$message.error(msg)
              })
          } else {
            saveInterfaceFieIdInfo({
              ...this.form,
              manageNo: this.manageNo,
              interfaceNo: this.interfaceNo,
            }).then((res) => {
              if (res.code == 200) {
                this.resetFields()
                this.$message.success('新增成功')
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
@import '@/assets/scss/productConfiguration';

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

.detailFile {
  /deep/ .el-form-item .upload-demo .el-upload-dragger {
    display: none !important;
  }
}

/deep/ .el-form-item {
  // height: 60px !important;
  margin-bottom: 0px;
}

/deep/ .el-form .ets {
  height: 200px !important;
}

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

.table_button {
  padding-left: 35px;
  padding-right: 20px;
  margin-top: 50px;
  width: 100%;
  height: 60px;
  z-index: 99;
  display: flex;
  align-items: center;
  box-sizing: border-box;
  background-color: #fff;

  .button_close {
    background-color: white;
    color: black;
  }

  .button_btn {
    background-color: #2071e1;
    color: white;
  }

  .table_button_left {
    flex: 1;
  }

  .table_button_right {
    flex: 2;
    text-align: right;
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

/deep/ .action-btn {
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

/deep/ .el-drawer {
  user-select: none;
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.16);

  .el-drawer__body {
    padding: 30px 30px 70px;
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

/deep/ .el-dialog {
  width: 432px !important;
  height: 170px !important;
}

/deep/ .el-dialog__body {
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

/deep/ .el-switch {
  vertical-align: bottom;
  margin-left: 20px;
}

/deep/ .el-form-item .el-input__inner {
  padding: 14px;
  width: 100%;
}

/deep/ .el-input-number--small {
  line-height: 44px;
}
</style>
