<template>
  <div style="position: relative">
    <el-drawer
      class="feed-back-drawer"
      :visible.sync="this.drawer"
      :title="title + '流程策略模型'"
      :hide-required-asterisk="false"
      :modal="false"
      :before-close="resetFields"
      size="30%"
    >
      <div class="step-warp">
        <div class="step" v-if="step == 1">
          <div class="icon current-step">1</div>
          <div class="text">基本信息</div>
        </div>
        <img
          src="../../image/finish.png"
          style="width: 24px; height: 24px; margin-right: 16px; cursor: pointer"
          v-else
          @click="changeSetp(1)"
        />
        <div class="line"></div>
        <div :class="['step']" @click="changeSetp(2)" style="cursor: pointer">
          <div :class="['icon', step == 2 ? 'current-step' : '']">2</div>
          <div class="text">流程配置</div>
        </div>
      </div>
      <div v-show="step === 1">
        <div class="form-warp">
          <el-form label-width="130px" :model="form" :rules="rules" ref="form">
            <el-form-item
              v-if="form.id"
              label="流程策略ID："
              prop="processStrategy"
            >
              <el-input disabled v-model="form.id"></el-input>
            </el-form-item>
            <el-form-item label="流程策略名称：" prop="processStrategy">
              <el-input v-model="form.processStrategy"></el-input>
            </el-form-item>
            <el-form-item label="关联产品：" prop="productName">
              <el-select
                v-model="form.productName"
                placeholder=""
                @change="productNameChange"
              >
                <el-option
                  v-for="(item, index) in productList"
                  :label="item.productName"
                  :value="item.productName"
                  :key="index"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="描述：" prop="content">
              <el-input
                v-model="form.content"
                type="textarea"
                :autosize="{ minRows: 4 }"
              ></el-input>
            </el-form-item>
          </el-form>
        </div>
        <div class="bottomBtns">
          <el-button type="primary" @click="goNextStep('form')"
            >下一步
          </el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </div>
      <div v-show="step === 2">
        <div class="form-warp1">
          <div class="step-line"></div>
          <div>
            <el-form
              label-width="80px"
              :model="form"
              ref="ruleform"
              :rules="rules"
            >
              <div class="circle-warp">
                <div class="circle-one">1</div>
                <el-form-item label="业务场景" prop="businessCode">
                  <el-select
                    v-model="form.businessCode"
                    placeholder="请选择"
                    style="width: 377px; height: 48px"
                    @change="
                      () => {
                        fromList = []
                      }
                    "
                  >
                    <el-option
                      v-for="item1 in sceneList"
                      :key="item1.dictValue"
                      :label="item1.dictLabel"
                      :value="item1.dictValue"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
              </div>
            </el-form>
            <div v-if="fromList.length > 0">
              <el-form
                label-width="80px"
                :model="item"
                v-for="(item, index) in fromList"
                :key="index"
              >
                <div class="circle-warp">
                  <div class="circle-one">{{ index + 2 }}</div>

                  <el-form-item label="策略模型">
                    <el-row :gutter="10">
                      <el-col :span="11">
                        <el-select
                          v-model="item.moduleId"
                          placeholder="请选择策略模型"
                          @change="changeScore(item, index)"
                        >
                          <el-option
                            v-for="item1 in item.scoreList"
                            :key="item1.value"
                            :label="item1.name"
                            :value="item1.value"
                          >
                          </el-option>
                        </el-select>
                      </el-col>
                      <el-col :span="10">
                        <el-select
                          v-model="item.ruleCode"
                          placeholder="请选择策略模型名称"
                          @change="changeName(item, index)"
                        >
                          <el-option
                            v-for="item1 in item.srcodeNameList"
                            :key="item1.value"
                            :label="item1.name"
                            :value="item1.value"
                          >
                          </el-option>
                        </el-select>
                      </el-col>
                      <el-col :span="1">
                        <img
                          src="../../image/deleteRed.png"
                          style="width: 16px; height: 16px; cursor: pointer"
                          @click="del(index)"
                        />
                      </el-col>
                    </el-row>
                  </el-form-item>
                </div>
              </el-form>
            </div>
            <div style="margin-left: 80px">
              <el-button
                @click="addMap"
                v-if="fromList.length < 6"
                style="
                  width: 377px;
                  height: 48px;
                  color: var(--primary-color);
                  font-size: 14px;
                "
                icon="el-icon-plus"
                >新建流程策略模型
              </el-button>
            </div>
          </div>
        </div>
      </div>
      <div class="button-list" v-if="step === 2">
        <el-button type="primary" @click="confirm">确认</el-button>
        <el-button
          type="info"
          style="background-color: #e4e6ef; color: #3f4254; border: none"
          @click="cancel"
          >取消
        </el-button>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import Headline from '@/components/Headline'
import {
  addSubmit,
  getmoduleId,
  getmoduleName,
  checkName,
  listByDeptId,
  policyUpdate,
} from '../../api/platformEngine'
import { product_search, selectAll } from '@/api/financing/productConfiguration'
import { debounce } from '@/utils/tool.js'

export default {
  components: { Headline },
  props: {
    title: {
      type: String,
      default: '',
    },
    sceneList: {
      // 场景下拉
      type: Array,
      required: true,
    },
  },

  data() {
    return {
      str: '', // 编辑时记录流程名称
      check: false,
      moduleType: null, //策略模型参数
      srcodeKey: null, //如果评分模型的值为2.评级3.额度4.评级，则带上srcodeKey
      addIndex: 0,
      fromList: [],
      rules: {
        processStrategy: [
          { required: true, message: '请输入名称', trigger: 'blur' },
          {
            min: 2,
            max: 20,
            message: '长度在 2 到 20 个字符',
            trigger: 'blur',
          },
        ],
        productName: [
          { required: true, message: '请选择管理产品', trigger: 'change' },
        ],
        businessCode: [
          { required: true, message: '请选择业务场景', trigger: 'change' },
        ],
      },
      step: 1, // 当前在第几步
      form: {},
      drawer: false,
      productList: [],
    }
  },
  mounted() {
    // this.getListByDeptId()
    product_search({
      pageNum: 1,
      pageSize: 999,
    }).then((res) => {
      if (res.code === 200) {
        let { list } = res.data
        this.productList = list.map((item) => {
          return {
            ...item,
            productName: item.name,
          }
        })
      }
      this.loading = false
    })
  },
  methods: {
    // 获取详情
    getInfo(item) {
      this.form = { ...item }
      if (this.productList.length) {
        let findData = this.productList.find(
          (item2) => item2.productName == item.productName
        )
        if (findData) this.form.productId = findData.id
      }
      this.str = item.processStrategy
      let list = item.mapList
      Promise.all(
        item.mapList.map((mapItem, index) =>
          this.getOptionList(
            mapItem.module_id,
            index === 0 ? undefined : item.mapList[index - 1].module_id,
            this.getKey(mapItem.module_id, list, index)
          )
        )
      ).then((ok) => {
        let depList = []
        // listByDeptId().then((res) => {
        // 	depList = res.rows
        this.fromList = item.mapList.map((mapItem, index) => {
          const find = ok.find((r) =>
            Object.keys(r).includes(mapItem.module_id + '')
          )
          const optionObj = find[mapItem.module_id]
          return {
            moduleId: mapItem.module_id + '',
            ruleName: mapItem.rule_name,
            ruleCode: mapItem.rule_code,
            approvalUserId: mapItem.approval_user_id,
            approvalName: mapItem.approval_name,
            scoreList: optionObj.modelList,
            srcodeNameList: optionObj.scoreList.map((scoreItem) => {
              return {
                name: scoreItem.name,
                value:
                  typeof scoreItem.value === 'number'
                    ? scoreItem.value.toString()
                    : scoreItem.value,
              }
            }),
            deptList: depList,
            userId: mapItem.approval_user_id,
          }
        })
        // })
      })
    },
    getKey(modelId, list, index) {
      let srcodeKey = ''
      if (modelId == '2') {
        let a = list.findLastIndex((c, t) => c.module_id == 1)
        srcodeKey = list[a].rule_code

        // 如果3是额度||4是定价，取评级对应得模型名称
      } else if (modelId == '3' || modelId == '4') {
        let b = list.findLastIndex((c, t) => c.module_id == 2)
        srcodeKey = list[b].rule_code
      } else if (index !== 0) {
        srcodeKey = list[index - 1].rule_code
      } else {
        srcodeKey = ''
      }
      return srcodeKey
    },
    // 获取评分卡名称下拉
    getRatingName(item, index) {
      // 如果新增的是评级-取评分对应得模型名称item.moduleId==2
      if (this.fromList.length > 1) {
        if (item.moduleId === '2') {
          let a = this.fromList.findLastIndex((c, t) => c.moduleId == '1')
          this.srcodeKey = this.fromList[a].srcodeNameList.filter(
            (f, i) => f.value === this.fromList[a].ruleCode
          )[0].value
          // 如果3是额度||4是定价，取评级对应得模型名称
        } else if (item.moduleId == '3' || item.moduleId == '4') {
          let b = this.fromList.findLastIndex((c, t) => c.moduleId == '2')
          this.srcodeKey = this.fromList[b].srcodeNameList.filter(
            (f, i) => f.value === this.fromList[b].ruleCode
          )[0].value
        } else {
          console.log('5', this.fromList)
          this.srcodeKey = this.fromList[
            this.fromList.length - 2
          ].srcodeNameList.filter(
            (f, i) =>
              f.value === this.fromList[this.fromList.length - 2].ruleCode
          )[0].value
        }
      } else {
        this.srcodeKey = ''
      }
      let strategyIds = []
      this.fromList.forEach((item) => {
        let ruleCode = item.srcodeNameList.filter(
          (i, c) => i.value == item.ruleCode
        )
        if (item.moduleId == 5 && ruleCode.length) {
          strategyIds.push(ruleCode[0].value)
        }
      })
      getmoduleName({
        moduleType: item.moduleId,
        key: this.srcodeKey,
        bussiness: this.form.businessCode,
        strategyIds: strategyIds,
        projectCode: this.form.productId,
      }).then((res) => {
        if (res.code == 200) {
          this.fromList[index].ruleName = ''
          this.fromList[index].srcodeNameList = res.data
        }
      })
    },
    // 评分卡名称改变的时候，保存当前条的数据-作为下一次的key
    changeName(item, index) {
      // this.srcodeKey = item.srcodeNameList.filter(item1 => item.ruleName == item1.name)[0].value
    },
    //产品名称修改
    productNameChange() {
      if (!this.form.productName) return
      if (this.productList.length) {
        let findData = this.productList.find(
          (item2) => item2.productName == this.form.productName
        )
        if (findData) this.form.productId = findData.id
      }
      let fromList = []
      this.fromList.map((item) => {
        getmoduleName({
          moduleType: item.moduleId,
          bussiness: this.form.businessCode,
          projectCode: this.form.productId,
        }).then((res) => {
          fromList.push({
            ...item,
            ruleName: undefined,
            ruleCode: undefined,
            srcodeNameList: res.data || [],
          })
        })
      })
      this.fromList = fromList
    },
    getOptionList(modelId, upModelId, srcodeKey) {
      return getmoduleName({
        moduleType: modelId.toString(),
        key: srcodeKey ? srcodeKey : '',
        bussiness: this.form.businessCode,
        projectCode: this.form.productId,
      })
        .then((res) => {
          return res.data
        })
        .then((ok) => {
          return getmoduleId({ moduleType: upModelId }).then((res) => {
            return { [modelId]: { modelList: res.data, scoreList: ok } }
          })
        })
    },
    //获取审批人员
    getListByDeptId() {
      listByDeptId().then((res) => {
        if (
          this.fromList[this.addIndex] &&
          this.fromList[this.addIndex].deptList
        ) {
          this.fromList[this.addIndex].deptList = res.rows
        }
      })
    },
    // 切换当前step
    changeSetp(index) {
      if (index == 1) {
        this.step = index
      } else {
        this.$refs.form.validate((valid) => {
          if (valid) {
            if (this.form.id) {
              if (this.str == this.form.processStrategy) {
                this.check = true
                this.step = 2
              } else {
                this.checkName()
              }
            } else {
              // if (this.form.processStrategy) {
              this.checkName()
              this.step = 2
              // }
            }
          }
        })
      }
    },
    // 删除
    del(index) {
      this.fromList.splice(index, 1)
      this.addIndex = this.fromList.length - 1
      this.moduleType =
        this.fromList.length > 0
          ? this.fromList.map((item) => item.moduleId).join(';')
          : ''
      if (index === 0) {
        this.srcodeKey = ''
      } else {
        this.srcodeKey = this.fromList[index - 1].srcodeNameList.filter(
          (item1) => item1.value == this.fromList[index - 1].ruleCode
        )[0].value
      }
    },
    // // 检验流程同名
    checkName() {
      checkName({ id: '', processStrategy: this.form.processStrategy })
        .then((res) => {
          this.check = true
          this.step = 2
        })
        .catch((msg) => {
          this.check = false
        })
    },

    confirm() {
      debounce(2000)(() => {
        if (!this.fromList.length) {
          this.$message.warning('请先新建流程策略模型')
          return
        }
        let flag = null
        console.log(this.fromList, 'fromList---fromList')

        this.fromList.map((item) => {
          if (item.moduleId && item.ruleCode) {
            flag = true
          } else {
            flag = false
            this.$message.error('策略模型或名称不能为空')
          }
        })
        if (!flag) return
        this.drawer = false

        let res = this.fromList.map((item, index) => {
          let ruleName = item.srcodeNameList.filter(
            (i, c) => i.value == item.ruleCode
          )[0].name
          let approvalName = item.userId
            ? item.deptList.filter((i, c) => i.userId == item.userId)[0]
                .nickName
            : null
          return {
            productName: '1',
            moduleId: item.moduleId,
            ruleName,
            ruleCode: item.ruleCode,
            approvalUserId: item.userId ? item.userId : null,
            approvalName: approvalName,
          }
        })
        let modelForm = {
          ...this.form,
          processStrategyId: this.form.id,
          useing: this.form.useIf,
        }
        this.form.mapList = res
        if (this.form.id) {
          modelForm.operationType = 2
          policyUpdate({ ...this.form }).then((res) => {
            if (res.code == 200) {
              this.$parent.addModel(modelForm)
              this.resetFields()
              this.$emit('getList')
            }
          })
        } else {
          modelForm.operationType = 0
          addSubmit({ ...this.form }).then((res) => {
            if (res.code == 200) {
              this.$parent.addModel(modelForm)
              this.resetFields()
              this.$emit('getList')
            }
          })
        }
      })
    },
    // 改变模型
    changeScore(item, index) {
      this.getRatingName(item, index)
      this.fromList.forEach((item, index1) => {
        if (index1 > index) {
          item.moduleId = ''
          item.ruleName = ''
          this.fromList.splice(index1, 1)
        }
      })
    },

    // 获取下拉框评分模型
    getmodule() {
      getmoduleId({ moduleType: this.moduleType }).then((res) => {
        if (
          this.fromList[this.addIndex] &&
          this.fromList[this.addIndex].scoreList
        ) {
          this.fromList[this.addIndex].scoreList = res.data
        }
      })
    },
    addMap() {
      this.$refs.ruleform.validate((valid) => {
        if (valid) {
          if (this.fromList.length <= 0) {
            this.addIndex = 0
            this.fromList.push({
              moduleId: '',
              ruleName: '',
              ruleCode: '',
              approvalUserId: '',
              approvalName: '',
              scoreList: [], // 评分
              srcodeNameList: [],
              deptList: [],
            })
            this.moduleType = null

            this.getmodule() // 第一次不带参数
            this.getListByDeptId()
            return
          }
          if (this.fromList.length >= 6) {
            return this.$message.error('已到新增最大限度')
          }
          let flag = null
          this.fromList.map((item) => {
            console.log(
              'item.moduleId:',
              item.moduleId,
              'item.ruleCode:',
              item.ruleCode
            )
            if (item.moduleId && item.ruleCode) {
              flag = true
            } else {
              flag = false
              this.$message.error('请完整输入')
            }
          })
          if (flag) {
            this.moduleType = this.fromList
              .map((item) => item.moduleId)
              .join(';')
            this.addIndex = this.fromList.length
            this.fromList.push({
              moduleId: '',
              ruleName: '',
              ruleCode: '',
              approvalUserId: '',
              approvalName: '',
              scoreList: [], // 评分
              srcodeNameList: [],
              deptList: [],
            })
            this.getmodule()
            this.getListByDeptId()
          } else {
            this.$message.error('请先完成选择')
          }
        }
      })
    },
    // 点击去下一步
    goNextStep(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.form.id) {
            if (this.str == this.form.processStrategy) {
              this.check = true
              this.step = 2
            } else {
              this.checkName()
            }
          } else {
            this.checkName()
          }
        }
      })
    },
    cancel() {
      this.resetFields()
    },
    resetFields() {
      this.check = false
      this.drawer = false
      this.form = {}
      this.fromList = []
      this.step = 1
    },
  },
}
</script>

<style lang="less" scoped>
// /deep/ .el-drawer {
//   user-select: none;
//   box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.16);

//   .el-drawer__body {
//     padding: 30px 30px 70px;
//   }
// }

// /deep/ .el-form-item__label {
//   line-height: 38px !important;
// }

.button-list {
  display: flex;
  justify-content: flex-end;
  position: absolute;
  bottom: 30px;
  left: 0px;
  width: 100%;
  padding-right: 20px;
}

.step-warp {
  display: flex;
  align-items: center;
  margin-top: 24px;

  .step {
    display: flex;
    align-items: center;

    .icon {
      width: 24px;
      height: 24px;
      border: 1px solid var(--text-color-tertiary);
      font-size: 16px;
      // font-family: PingFang SC-Semibold, PingFang SC;
      font-weight: 600;
      color: var(--text-color-tertiary);
      text-align: center;
      line-height: 24px;
      border-radius: 50%;
    }

    .text {
      font-size: 16px;
      // font-family: PingFang SC-Regular, PingFang SC;
      font-weight: 400;
      color: var(--text-color-tertiary);
      margin-left: 16px;
    }

    .current-step {
      background: var(--primary-color);
      color: #fff;
      border: none;
    }
  }

  .line {
    width: 228px;
    height: 2px;
    background: var(--bg-color-lighter);
    margin: 0px 16px;
  }
}

.form-warp {
  margin-top: 24px;
  display: flex;
  position: relative;

  ::v-deep .el-input__inner {
    width: 371px;
    height: 45px;
    background: var(--bg-color);
    // border: none;
  }

  ::v-deep .el-textarea__inner {
    background: var(--bg-color);
    // border: none;
  }
}

.form-warp1 {
  margin-top: 24px;
  display: flex;
  position: relative;

  ::v-deep .el-input__inner {
    // width: 371px;
    // height: 48px;
    background: var(--bg-color);
    // border: none;
  }

  // ::v-deep .el-textarea__inner {
  //     background: #F4F6F9;
  //     border: none;
  // }
}

.step-line {
  width: 1px;
  // height: 134px;
  background: var(--bg-color-lighter);
  margin-right: 11px;
  margin-top: 28px;
}

.circle-one {
  width: 16px;
  height: 16px;
  background: var(--primary-color);
  border-radius: 8px 8px 8px 8px;
  position: relative;
  left: -18px;
  top: 24px;
  font-size: 12px;
  // font-family: PingFang SC-Regular, PingFang SC;
  font-weight: 400;
  color: #fff;
  text-align: center;
  line-height: 16px;
}
.bottomBtns {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
  .el-button {
    padding: 0px 24px;
    height: 42px;
    border-radius: 6px;
    font-size: 14px;
  }
}
</style>
