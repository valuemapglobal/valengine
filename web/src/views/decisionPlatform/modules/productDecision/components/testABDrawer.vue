<template>
  <div class="testABDrawer">
    <el-drawer
      :visible.sync="visible"
      size="1000px"
      title="A/B测试"
      :wrapperClosable="false"
      append-to-body
      :before-close="colseMask"
    >
      <div class="content">
        <div class="step-list">
          <div class="step-item" :class="{ active: currentIndex === 0 }">
            <div class="icon">1</div>
            <div class="text">确认模型信息</div>
          </div>
          <div class="line"></div>
          <div class="step-item" :class="{ active: currentIndex === 1 }">
            <div class="icon">2</div>
            <div class="text">添加对照数据，进行模型回溯</div>
          </div>
        </div>
        <div class="form-warp" v-show="currentIndex === 0">
          <div class="text">1.模型信息</div>
          <el-form
            :model="ruleForm"
            :rules="rules"
            ref="ruleForm"
            label-width="100px"
          >
            <el-form-item label="产品信息" prop="productId">
              <el-select
                v-model="ruleForm.productId"
                placeholder="请选择"
                style="width: 100%"
                @change="changeSelect"
              >
                <el-option
                  :label="item.productName"
                  :value="item.productId"
                  v-for="(item, index) in productList"
                  :key="index"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="业务场景" prop="businessCode">
              <el-select
                v-model="ruleForm.businessCode"
                placeholder="请选择"
                style="width: 100%"
                @change="changeBusiness"
              >
                <el-option
                  :label="item.name"
                  :value="item.businessCode"
                  v-for="(item, index) in tabs"
                  :key="index"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="模型分类" prop="moduleCode">
              <el-select
                v-model="ruleForm.moduleCode"
                placeholder="请选择"
                style="width: 100%"
                @change="changeModuleCode"
              >
                <el-option
                  :label="item.label"
                  :value="item.ruleCode"
                  v-for="(item, index) in btnList"
                  :key="index"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="模型名称" prop="moduleId">
              <el-select
                v-model="ruleForm.moduleId"
                placeholder="请选择"
                style="width: 100%"
                @change="changeV"
              >
                <el-option
                  :label="item.moduleName"
                  :value="item.moduleId"
                  v-for="(item, index) in ruleForm.moduleCode == 5
                    ? scoreCardList1
                    : scoreCardList"
                  :key="index"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-form>
          <div class="v-text">历史最高版本：{{ firstVersionControl }}</div>
          <div class="text">2.预测结果阈值</div>
          <!-- 判断是不是评分阶段 不是就显示下拉框，如果当前是评分阶段，且产品名称创程租赁||海翔小贷 显示输入框 反之 显示该产品下的“评分模型”暂无总分值信息，请联系开发人员处理-->
          <div
            class="forecast-box"
            v-if="
              (ruleForm.moduleCode === 1 && mapProName == '创程租赁') ||
              (ruleForm.moduleCode === 1 && mapProName == '海翔小贷')
            "
          >
            <div class="dec" style="margin-bottom: 14px">
              该模型风险评估结果为：建议复核（分值）
            </div>
            <div class="dec" style="margin-bottom: 14px">
              总分值：{{
                mapProName == '海翔小贷' ? 550 : 100
              }}；指标类型：正向增长
            </div>
            <div class="forecast-box-select">
              <div class="dec weight">判断条件：判断条件：如果评估分值 ≥</div>
              <div class="select-box" style="width: 200px">
                <el-form
                  hide-required-asterisk
                  :model="ruleForm"
                  ref="ruleForm1"
                  :rules="rules"
                >
                  <el-form-item prop="scoreRule">
                    <el-input
                      v-model="ruleForm.scoreRule"
                      placeholder="请输入分值"
                    ></el-input>
                  </el-form-item>
                </el-form>
              </div>
              <div class="dec weight">则将其预测为好客户</div>
            </div>
          </div>
          <div
            class="forecast-box"
            style="font-size: 14px"
            v-else-if="
              (ruleForm.moduleCode === 1 && mapProName != '创程租赁') ||
              (ruleForm.moduleCode === 1 && mapProName != '海翔小贷')
            "
          >
            该产品下的“评分模型”暂无总分值信息，请联系开发人员处理
          </div>
          <div class="forecast-box" v-else>
            <div class="dec" style="margin-bottom: 14px">
              该模型风险评估结果为：建议通过、建议复核、建议拒绝
            </div>
            <div class="forecast-box-select">
              <div class="dec weight">判断条件：如果风险评估结果为</div>
              <div class="select-box">
                <el-form
                  hide-required-asterisk
                  ref="ruleForm2"
                  :model="ruleForm"
                  :rules="rules"
                >
                  <el-form-item prop="rdeRule">
                    <el-select
                      v-model="ruleForm.rdeRule"
                      placeholder="请选择（可多选）"
                      style="width: 100%"
                      multiple
                    >
                      <el-option label="建议通过" value="建议通过"></el-option>
                      <el-option label="建议复核" value="建议复核"></el-option>
                    </el-select>
                  </el-form-item>
                </el-form>
              </div>
              <div class="dec weight">则将其预测为好客户</div>
            </div>
          </div>
          <div class="tip">
            <span style="color: #ff8f1f">注意：</span
            >除好客户外，其余默认均预测为坏客户
          </div>
          <div class="btn-box">
            <el-button @click="colseMask">取消</el-button>
            <el-button type="primary" @click="nextPage" :disabled="disabledNext"
              >下一步</el-button
            >
          </div>
        </div>
        <div v-show="currentIndex != 0">
          <Recall
            @prepage="prepage"
            ref="Recall"
            @colseMask="colseMask"
            :moduleName="ruleForm.modelName"
            :activeId="ruleForm.moduleId"
            :ruleCode="ruleForm.moduleCode"
            :ruleForm="ruleForm"
          />
        </div>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import { selectAll } from '@/api/financing/productConfiguration.js'
import { mapState } from 'vuex'
import { queryLastVersion, selectNewList } from '../api/riskModel'
import { getScoreList } from '../api/score'
import Recall from './recall.vue'
export default {
  name: 'testABDrawer',
  components: {
    Recall,
  },
  props: ['activeId', 'versionControl'],
  data() {
    return {
      firstVersionControl: null,
      moduleName: '',
      scoreCardList: [], // 模型名称-评分
      scoreCardList1: [], //规则
      btnList: [
        // 模型分类
        { label: '评分模型', ruleCode: 1 },
        // { label: '评级模型', ruleCode: 2 },
        // { label: '额度模型', ruleCode: 3 },
        // { label: '定价模型', ruleCode: 4 },
        { label: '规则模型', ruleCode: 5 },
        // { label: '分类模型', ruleCode: 6 },
      ],
      tabs: [
        {
          name: '准入',
          businessCode: 1,
        },
        // {
        //   name: '反欺诈',
        //   businessCode: 2,
        // },
        {
          name: '预授信',
          businessCode: 3,
        },
        // {
        //   name: '授信',

        //   businessCode: 4,

        // },
        // {
        //   name: '放款（支取）',

        //   businessCode: 5,
        // },
        // {
        //   name: '监测',

        //   businessCode: 6,
        // },
        // {
        //   name: '贷后',

        //   businessCode: 7,
        // },
        // {
        //   name: '催收',
        //   businessCode: 8,
        // },
      ],
      decision: {},
      productList: [],
      visible: false,
      currentIndex: 0,
      ruleForm: {
        businessCode: '',
        rdeRule: [],
        productId: '',
        moduleId: '',
        scoreRule: '',
      },
      rules: {
        productId: { required: true, message: '请选择', trigger: 'change' },
        businessCode: { required: true, message: '请选择', trigger: 'change' },
        moduleCode: { required: true, message: '请选择', trigger: 'change' },
        moduleId: { required: true, message: '请选择', trigger: 'change' },
        scoreRule: [
          {
            required: true,
            message: '请输入分值',
            trigger: 'blur',
          },
          {
            validator: (rule, value, callback) => {
              if (this.mapProName == '海翔小贷') {
                if (!this.isInteger(value) || value < 0 || value > 550) {
                  callback(new Error('请输入0-550之间的整数'))
                }
              } else {
                let seq = /^(\d{1,2}(\.\d{1,2})?|100(\.0{1,2})?)$/
                if (!seq.test(value) || !this.isInteger(value)) {
                  callback(new Error('请输入0-100之间的整数'))
                }
              }
              callback()
            },
            trigger: 'blur',
          },
        ],
        rdeRule: [
          {
            required: true,
            message: '评估结果不能为空',
            trigger: 'change',
          },
        ],
      },
    }
  },
  computed: {
    ...mapState(['dataRisk']),
    mapProName() {
      if (this.ruleForm.productId) {
        let findObj = this.productList.find(
          (item) => item.productId === this.ruleForm.productId
        )
        if (findObj) {
          return findObj.productName
        }
      }
    },
    disabledNext() {
      if (
        (this.ruleForm.moduleCode === 1 && this.mapProName == '创程租赁') ||
        (this.ruleForm.moduleCode === 1 && this.mapProName == '海翔小贷')
      ) {
        return false
      } else if (
        (this.ruleForm.moduleCode === 1 && this.mapProName != '创程租赁') ||
        (this.ruleForm.moduleCode === 1 && this.mapProName != '海翔小贷')
      ) {
        return true
      } else {
        return false
      }
    },
  },
  watch: {
    visible: {
      handler(n, o) {
        if (n) {
          this.init()
        } else {
          this.ruleForm = this.$options.data().ruleForm
        }
      },
    },
  },
  methods: {
    // 校验是否是整数，true 代表是整数，false 代表是小数
    isInteger(obj) {
      return obj % 1 === 0
    },
    colseMask() {
      this.currentIndex = 0
      this.visible = false
      this.ruleForm = this.$options.data().ruleForm
      if (this.$refs.Recall) {
        this.$refs.Recall.clearForm()
      }
    },
    // 产品信息
    changeSelect(v) {
      // this.ruleForm = this.$options.data().ruleForm
      this.ruleForm.businessCode = ''
      this.ruleForm.moduleCode = ''
      this.ruleForm.moduleId = ''
      this.ruleForm.rdeRule = []
      this.ruleForm.scoreRule = ''
      this.$set(this.ruleForm, 'productId', v)
    },
    // 业务场景
    changeBusiness(v) {
      this.ruleForm.moduleCode = ''
      this.ruleForm.moduleId = ''
      this.ruleForm.rdeRule = []
      this.ruleForm.scoreRule = ''
    },
    // // 模型分类
    changeModuleCode() {
      this.ruleForm.moduleId = ''
      this.ruleForm.rdeRule = []
      this.ruleForm.scoreRule = ''
      this.getModuleName()
    },
    changeV() {
      this.queryLastVersion()
    },
    // 获取产品信息
    init() {
      selectAll({
        pageNum: 1,
        pageSize: 999,
      }).then((res) => {
        if (res.code == 200) {
          this.productList = res.data.list.map((item) => {
            return {
              productId: item.id,
              productName: item.productName,
            }
          })
          this.$set(
            this.ruleForm,
            'productId',
            this.dataRisk.decision.projectCode
          )
          this.$set(
            this.ruleForm,
            'businessCode',
            this.dataRisk.decision.businessCode
          )
          this.$set(
            this.ruleForm,
            'moduleCode',
            this.dataRisk.decision.ruleCode
          )
          if (this.activeId) {
            this.$set(this.ruleForm, 'moduleId', this.activeId)
            this.queryLastVersion()
          } else {
            this.$set(this.ruleForm, 'moduleId', null)
          }
          this.getModuleName()
        }
      })
    },
    queryLastVersion() {
      queryLastVersion({ modelId: this.ruleForm.moduleId }).then((res) => {
        this.firstVersionControl =
          res.data && res.data.versionControl ? res.data.versionControl : '-'
      })
    },
    // 获取产品名称
    getModuleName() {
      // 获取当前产品下的-规则模型名称
      if (this.ruleForm.moduleCode == 5) {
        selectNewList({
          pageNum: 1,
          pageSize: 9999,
          modelType: 1,
          businessCode: this.ruleForm.businessCode,
          projectCode: this.ruleForm.productId,
          ruleCode: 5,
        }).then((res) => {
          if (res.rows.length > 0) {
            this.scoreCardList1 = res.rows.map((item) => {
              return {
                moduleName: item.name,
                moduleId: item.id,
              }
            })
          } else {
            this.scoreCardList1 = []
          }
        })
      } else {
        // 获取当前产品下的-评分模型名称
        getScoreList({
          businessCode: this.ruleForm.businessCode,
          projectCode: this.ruleForm.productId,
          ruleCode: 1,
        })
          .then((res) => {
            if (res.code == 200) {
              if (res.data.length) {
                this.scoreCardList = res.data.map((item) => {
                  return {
                    moduleName: item.scoreCard,
                    moduleId: item.id,
                  }
                })
              } else {
                this.scoreCardList = []
              }
            }
          })
          .catch((err) => {})
      }
    },
    // 获取业务场景

    // 上一步
    prepage() {
      this.currentIndex = 0
    },
    validateForm() {
      return new Promise((resolve, reject) => {
        this.$refs.ruleForm.validate((valid) => {
          if (valid) {
            resolve()
          } else {
            reject()
          }
        })
      })
    },
    validateForm1() {
      return new Promise((resolve, reject) => {
        this.$refs.ruleForm1.validate((valid) => {
          if (valid) {
            resolve()
          } else {
            reject()
          }
        })
      })
    },
    validateForm2() {
      // 返回一个Promise对象，表示表单2的校验结果
      return new Promise((resolve, reject) => {
        this.$refs.ruleForm2.validate((valid) => {
          if (valid) {
            resolve()
          } else {
            reject()
          }
        })
      })
    },
    // 下一步
    nextPage() {
      let promiseArr = []
      if (
        (this.ruleForm.moduleCode === 1 && this.mapProName == '创程租赁') ||
        (this.ruleForm.moduleCode === 1 && this.mapProName == '海翔小贷')
      ) {
        promiseArr = [this.validateForm1(), this.validateForm()]
      } else {
        promiseArr = [this.validateForm2(), this.validateForm()]
      }
      Promise.all([...promiseArr])
        .then(() => {
          this.$nextTick(() => {
            let arr =
              this.ruleForm.moduleCode == 5
                ? this.scoreCardList1
                : this.scoreCardList
            this.ruleForm.moduleName = arr.find(
              (item, index) => item.moduleId === this.ruleForm.moduleId
            ).moduleName
            this.ruleForm.productName = this.productList.find(
              (item) => item.productId === this.ruleForm.productId
            ).productName
            this.ruleForm.versionLast = this.versionControl
            this.currentIndex = 1
            this.$refs.Recall.getList()
          })
        })
        .catch(() => {})

      // this.$refs.ruleForm.validate((valid) => {
      //     if (valid) {
      //         this.$nextTick(() => {
      //             let arr = this.ruleForm.moduleCode == 5 ? this.scoreCardList1 : this.scoreCardList
      //             this.ruleForm.moduleName = arr.find((item, index) => item.moduleId === this.ruleForm.moduleId).moduleName
      //             this.ruleForm.productName = this.productList.find(item => item.productId === this.ruleForm.productId).productName
      //             this.ruleForm.versionLast = this.versionControl
      //             this.currentIndex = 1
      //             this.$refs.Recall.getList()
      //         })
      //     }
      // });
    },
  },
}
</script>

<style lang="less" scoped>
::v-deep .el-drawer__header {
  color: rgba(0, 0, 0, 0.85);
}

.content {
  padding: 0px 20px;

  .step-list {
    display: flex;
    flex-wrap: nowrap;
    align-items: center;

    .step-item {
      display: flex;
      align-items: center;

      &.active {
        .icon {
          background: #0052d9;
          color: rgba(255, 255, 255, 0.9);
          border: none;
        }

        .text {
          color: #0052d9;
        }
      }

      .icon {
        width: 24px;
        height: 24px;
        border: 1px solid rgba(0, 0, 0, 0.4);
        color: rgba(0, 0, 0, 0.4);
        text-align: center;
        line-height: 24px;
        border-radius: 50%;
      }

      .text {
        font-size: 16px;
        font-family: PingFang SC, PingFang SC;
        font-weight: 400;
        color: rgba(0, 0, 0, 0.4);
        margin-left: 16px;
      }
    }

    .line {
      width: 457px;
      height: 2px;
      background: #dcdcdc;
      margin: 0px 16px;
    }
  }

  .form-warp {
    padding: 24px 50px;

    .text {
      font-size: 18px;
      font-family: PingFang SC, PingFang SC;
      font-weight: 500;
      color: rgba(0, 0, 0, 0.85);
      margin-bottom: 20px;
    }

    ::v-deep .el-input--small .el-input__inner {
      background-color: #f4f6f9;
      height: 45px;
      border: none;
    }

    .v-text {
      font-family: PingFang SC, PingFang SC;
      font-weight: 500;
      color: rgba(0, 0, 0, 0.85);
      font-size: 14px;
      margin-bottom: 30px;
    }
  }

  .forecast-box {
    // height: 120px;
    background: #f7fafe;
    border-radius: 4px 4px 4px 4px;
    padding: 20px;
    box-sizing: border-box;

    .dec {
      font-size: 14px;
      font-family: PingFang SC, PingFang SC;
      font-weight: 400;
      color: rgba(0, 0, 0, 0.85);
    }

    .weight {
      font-weight: 500;
    }

    &-select {
      display: flex;
      align-items: center;

      ::v-deep .el-form-item--small.el-form-item {
        margin-bottom: 0px;
      }

      .select-box {
        width: 436px;
        margin: 0px 10px;

        ::v-deep .el-input--small .el-input__inner {
          background-color: #fff;
          height: 44px;
          border: none;
        }
      }
    }
  }

  .tip {
    margin-top: 10px;
    font-size: 14px;
    font-family: PingFang SC, PingFang SC;
    font-weight: 500;
    color: rgba(0, 0, 0, 0.85);
    line-height: 22px;
  }

  .btn-box {
    margin-top: 40px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
