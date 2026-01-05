<template>
  <div style="position: relative">
    <el-drawer
      class="feed-back-drawer"
      :visible.sync="drawer"
      :destroy-on-close="true"
      :hide-required-asterisk="false"
      :modal="false"
      :wrapperClosable="false"
      size="58%"
      :before-close="resetFields"
      :title="isEdit ? '编辑接口' : '新增接口'"
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="ruleForm"
        label-width="auto"
        label-position="right"
      >
        <el-row :gutter="30">
          <el-col :span="12">
            <el-form-item label="接口编号：" prop="interfaceNo">
              <el-input v-model="form.interfaceNo"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="接口名称：" prop="interfaceName">
              <el-input v-model="form.interfaceName"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="30">
          <el-col :span="12">
            <el-form-item label="选择类型：" prop="interfaceType">
              <el-select
                v-model="form.interfaceType"
                :popper-append-to-body="false"
              >
                <el-option label="个人接口" :value="0"></el-option>
                <el-option label="企业接口" :value="1"></el-option>
                <el-option label="司法接口" :value="2"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="接口描述：" prop="interfaceDescription">
              <el-input v-model="form.interfaceDescription"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="30">
          <el-col :span="12">
            <el-form-item label="接口单价(元)：" prop="price">
              <el-input v-model="form.price"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="30">
          <el-col :span="12">
            <el-form-item label="接口特点：" prop="interfaceFeatures">
              <el-input
                v-model="form.interfaceFeatures"
                type="textarea"
                :rows="3"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="接口场景：" prop="interfaceScenes">
              <el-input
                v-model="form.interfaceScenes"
                type="textarea"
                :rows="3"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="30">
          <el-col :span="12">
            <el-form-item label="接口覆盖体量：" prop="interfaceCoveringVolume">
              <el-input v-model="form.interfaceCoveringVolume"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="接口标签：" prop="interfaceTag">
              <el-input v-model="form.interfaceTag"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="30">
          <el-col :span="12">
            <el-form-item label="请求地址：" prop="interfaceLink">
              <el-input v-model="form.interfaceLink"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序：" prop="interfaceIndex">
              <el-input v-model="form.interfaceIndex" type="number"></el-input>
            </el-form-item>
          </el-col>
          <!-- <el-col :span="12">
            <el-form-item
              label="测试条数："
              prop="interfaceQuota"
            >
              <el-input
                v-model="form.interfaceQuota"
                type="number"
              ></el-input>
            </el-form-item>
          </el-col> -->
        </el-row>

        <el-row :gutter="30">
          <el-col :span="12">
            <el-form-item label="参数类型：" prop="paramType">
              <el-select
                v-model="form.paramType"
                :popper-append-to-body="false"
              >
                <el-option label="Json" :value="0"></el-option>
                <el-option label="from-data" :value="1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="请求类型：" prop="requestType">
              <el-select
                v-model="form.requestType"
                :popper-append-to-body="false"
              >
                <el-option label="POST" :value="0"></el-option>
                <el-option label="GET" :value="1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="30">
          <el-col :span="12">
            <el-form-item label="返回值类型：" prop="returnType">
              <el-select
                v-model="form.returnType"
                :popper-append-to-body="false"
              >
                <el-option label="对象" :value="0"></el-option>
                <el-option label="数组" :value="5"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="超时时间(毫秒)：" prop="timeout">
              <el-input v-model="form.timeout"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="bottomBtns">
        <el-button type="primary" @click="confirm('ruleForm')">确定</el-button>
        <el-button @click="resetFields">关闭</el-button>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import Headline from '@/components/Headline'
import { getToken } from '@/utils/auth'
import {
  getAllApiServiceType,
  getApiCategoryByType,
  save,
} from '@/api/dataRisk/basicVariables'
import {
  saveInterfaceInfo,
  updateInterfaceInfo,
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
    isAudit: {
      type: Boolean, // 查看当前是否是审核
      default: false,
    },
    sourceNo: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      form: {
        sourceNo: '', // 应用场景标识
      },
      tabData: [],
      activeName: '',
      headers: {
        Authorization: 'Bearer ' + getToken(),
      },
      rules: {
        interfaceNo: [
          { required: true, message: '请输入接口编号', trigger: 'blur' },
          { min: 1, max: 50, message: '长度在 1到 50个字符', trigger: 'blur' },
        ],
        interfaceName: [
          { required: true, message: '请输入接口名称', trigger: 'blur' },
          { min: 1, max: 30, message: '长度在 1到 30个字符', trigger: 'blur' },
        ],
        price: [
          { required: true, message: '请输入接口单价', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              let req = /^\d+(.\d{1,2})?$/
              if (!req.test(value))
                callback('请输入0-99999之间的金额，可保留两位小数')
              callback()
            },
            trigger: 'blur',
          },
        ],
        interfaceDescription: [
          { required: true, message: '请输入接口描述', trigger: 'blur' },
          { min: 1, max: 30, message: '长度在 1到 30个字符', trigger: 'blur' },
        ],
        interfaceFeatures: [
          { required: true, message: '请输入接口特点', trigger: 'blur' },
          { min: 1, max: 30, message: '长度在 1到 200个字符', trigger: 'blur' },
        ],
        interfaceScenes: [
          { required: true, message: '请输入接口场景', trigger: 'blur' },
          { min: 1, max: 30, message: '长度在 1到 200个字符', trigger: 'blur' },
        ],
        interfaceCoveringVolume: [
          { required: true, message: '请输入接口覆盖体量', trigger: 'blur' },
          { min: 1, max: 30, message: '长度在 1到 30个字符', trigger: 'blur' },
        ],
        interfaceTag: [
          { required: true, message: '请输入接口标签', trigger: 'blur' },
          { min: 1, max: 30, message: '长度在 1到 30个字符', trigger: 'blur' },
        ],
        //
        interfaceQuota: {
          required: true,
          pattern: /^[1-9]*[1-9][0-9]*$/,
          message: '只能输入数字且大于0',
          trigger: 'blur',
        },
        interfaceLink: {
          required: true,
          message: '请输入请求地址',
          trigger: 'blur',
        },
        interfaceIndex: [
          {
            required: true,
            message: '请输入排序',
            trigger: 'blur',
          },
          {
            validator: (rule, value, callback) => {
              let req = /^-?[1-9]\d*$|^0$/
              if (!req.test(value)) callback('请输入整数')
              callback()
            },
            trigger: 'blur',
          },
        ],
        paramType: {
          required: true,
          message: '请选择参数类型',
          trigger: 'change',
        },
        requestType: {
          required: true,
          message: '请选择请求类型',
          trigger: 'change',
        },
        returnType: {
          required: true,
          message: '请选择返回值类型',
          trigger: 'change',
        },
        timeout: {
          required: true,
          pattern: /^[1-9]*[1-9][0-9]*$/,
          message: '只能输入数字且大于0',
          trigger: 'blur',
        },
      },
      drawer: false,
    }
  },
  watch: {
    drawer: {
      handler(val) {
        if (!val) {
          this.form = this.$options.data().form
        }
      },
      immediate: true,
    },
  },
  mounted() {},
  methods: {
    //赋值
    getInfo(row) {
      this.form = { ...row }
      this.form.sourceNo = row.interfaceSourceNo
      this.form.manageNo = row.interfaceManageNo
      this.$set(this.form, 'paramType', Number(row.paramType))
      this.$set(this.form, 'requestType', Number(row.requestType))
    },

    getType() {
      getAllApiServiceType('api_service_type').then((res) => {
        this.tabData = res.data
        this.activeName = this.tabData[0].name
        this.getMenuList(this.activeName)
      })
    },
    getMenuList(name) {
      //   let type=name=='公开数据管理'?'0' :'1'
      getApiCategoryByType({ type: 1 }).then((res) => {
        res.data.forEach((item) => {
          item.current = false
        })
        res.data[0].current = true
        this.menuList = res.data
        // 初始调用一次
        this.$emit('leftMenu', this.menuList[0])
      })
    },
    resetFields() {
      this.drawer = false
      // this.formData = {};
      // this.fileList = [];
    },

    // 保存
    confirm(formName) {
      this.$refs[formName].validate((valid) => {
        this.form.sourceNo = this.sourceNo
        if (valid) {
          if (this.isEdit) {
            updateInterfaceInfo(this.form).then((res) => {
              this.drawer = false
              this.$message.success('修改成功!')
              this.form = {}
              this.$emit('getList')
            })
          } else {
            saveInterfaceInfo(this.form).then((res) => {
              if (res.code == 200) {
                this.drawer = false
                this.$message.success('添加成功!')
                this.form = {}
                this.$emit('getList')
              }
            })
          }
        } else {
        }
      })
    },
  },
}
</script>

<style lang="less" scoped>
// @import '@/assets/scss/productConfiguration';

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
  width: 100%;
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  .el-button {
    padding: 0px 24px;
    height: 42px;
    font-size: 14px;
    border-radius: 8px;
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
