<template>
  <div class="model_task">
    <div class="stepList">
      <div v-for="(item, index) in stepList" :key="index">
        <div class="step flex">
          <div class="circular flex" :class="{ active: activeStep >= index }">
            {{ index + 1 }}
          </div>
          <div class="lebel">{{ item.label }}</div>
          <div class="line" v-if="index < stepList.length - 1" />
        </div>
      </div>
    </div>
    <div v-if="activeStep === 0" class="content">
      <Search
        class="search"
        :list="searchList"
        :onlySearch="true"
        ref="search"
        @search="handleSearch"
        dark
      />
      <addCodeRule
        :tableConfig="enterpriseList"
        :selectTableConfig="selectTable"
        @turnPage="handleCurrentChange"
        @turnSize="handleSizeChange"
      />
    </div>
    <div v-else-if="activeStep === 1" class="content flex_between">
      <div class="model">
        <div class="title">可选择模型</div>
        <div class="model_content">
          <div class="model_cardList">
            <template v-for="(item, index) in optionsModel">
              <div class="card" :key="index" :style="{ width: cardWidth }">
                <div class="card_content">
                  <div class="top">
                    <div class="title">{{ item.name }}</div>
                    <el-tag
                      type="warning"
                      size="medium"
                      v-if="handleModelType(item.ruleType)"
                    >
                      {{ handleModelType(item.ruleType) }}
                    </el-tag>
                  </div>
                  <div class="descr">
                    <div>{{ item.descr || '暂无' }}</div>
                  </div>
                  <div class="switch">
                    <el-switch
                      v-model="item.status"
                      active-color="#FF5A55"
                      inactive-color="#D6D3D3"
                      active-value="1"
                      inactive-value="0"
                      disabled
                    />
                    <div @click="addModelCode(item)">
                      <i class="el-icon-plus" />添加
                    </div>
                  </div>
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>
      <div class="model">
        <div class="title">已选择模型</div>
        <div class="model_content">
          <div class="model_cardList">
            <template v-for="(item, index) in selectList">
              <div class="card" :key="index" :style="{ width: cardWidth }">
                <div class="card_content">
                  <div class="top">
                    <div class="title">{{ item.name }}</div>
                    <el-tag
                      type="warning"
                      size="medium"
                      v-if="handleModelType(item.ruleType)"
                    >
                      {{ handleModelType(item.ruleType) }}
                    </el-tag>
                  </div>
                  <div class="descr">
                    <div>{{ item.descr || '暂无' }}</div>
                  </div>
                  <div class="switch">
                    <el-switch
                      v-model="item.status"
                      active-color="#FF5A55"
                      inactive-color="#D6D3D3"
                      active-value="1"
                      inactive-value="0"
                      disabled
                    />
                    <div @click="removeModelCode(item)">
                      <i class="el-icon-minus" />移除
                    </div>
                  </div>
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>
    </div>
    <div v-else-if="activeStep === 2" class="content flex_between">
      <el-form
        :model="baseForm"
        :rules="baseRules"
        ref="baseForm"
        label-width="140px"
      >
        <el-row>
          <el-col :span="12">
            <el-form-item label="任务名称" prop="name">
              <el-input
                v-model="baseForm.name"
                placeholder="请输入任务名称"
                style="width: 300px"
                clearable
                size="small"
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="起止时间" prop="dateRange">
              <el-date-picker
                v-model="baseForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="yyyy-MM-dd"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-col :span="24">
          <el-form-item label="模型执行周期" prop="ruleCron">
            <el-radio-group v-model="baseForm.ruleCron">
              <el-radio
                v-for="(item, index) in radioList"
                :key="index"
                :label="item.value"
                >{{ item.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row>
            <el-col :span="12">
              <el-form-item label="已跑批后检查周期" prop="ruleCron">
                <el-select
                  v-model="baseForm.period"
                  placeholder="请选择"
                  :popper-append-to-body="false"
                >
                  <el-option
                    v-for="dict in periodOptions"
                    :key="dict.dictValue"
                    :label="dict.dictLabel"
                    :value="dict.dictValue"
                  />
                </el-select>
                <div style="color: red; font-size: 12px">
                  对于已处理过的风险code，下次检查周期控制
                </div>
              </el-form-item>
            </el-col>
          </el-row>
        </el-col>
      </el-form>
    </div>
    <div v-else-if="activeStep === 3" class="content flex_between">
      <el-form :model="smsForm" label-width="140px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="短信推送" prop="msg">
              <el-input
                type="textarea"
                :autosize="{ minRows: 5, maxRows: 10 }"
                v-model="smsForm.msg"
                placeholder="请输入手机号,多个手机号用‘英文逗号’隔开"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮件推送" prop="email">
              <el-input
                type="textarea"
                :autosize="{ minRows: 5, maxRows: 10 }"
                v-model="smsForm.email"
                placeholder="请输入邮件地址,多个手机号用‘英文逗号’隔开"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="短信提醒时间" prop="msgFrequency">
              <el-radio-group v-model="smsForm.msgFrequency">
                <el-radio label="0">实时</el-radio>
                <el-radio label="6">早上6点</el-radio>
                <el-radio label="9">上午9点</el-radio>
                <el-radio label="13">中午13点</el-radio>
                <el-radio label="18">下午18点</el-radio>
                <el-radio label="21">晚上21点</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮件提醒时间" prop="emailFrequency">
              <el-radio-group v-model="smsForm.emailFrequency">
                <el-radio label="0">实时</el-radio>
                <el-radio label="6">早上6点</el-radio>
                <el-radio label="9">上午9点</el-radio>
                <el-radio label="13">中午13点</el-radio>
                <el-radio label="18">下午18点</el-radio>
                <el-radio label="21">晚上21点</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div class="btnBotton">
      <el-button @click="toNextStep" v-if="!(activeStep === 3 && info.examine)">
        {{
          activeStep !== 2
            ? '下一步'
            : `${info.status === '1' ? '关闭' : '完成'}`
        }}
      </el-button>
      <el-button @click="previousStep" :disabled="activeStep === 0">
        上一步
      </el-button>
    </div>
  </div>
</template>

<script>
import Search from '@/components/pageSearch.vue'
import addCodeRule from '../components/addCodeRule'
import {
  searchEnterprisetList,
  searchSelectEnterprisetList,
  searchModelList,
  searchTasksRuleList,
  submitForm,
  searchSelectModelList,
} from '@/api/dataRisk/modeltask.js'

export default {
  components: {
    Search,
    addCodeRule,
  },
  props: {
    info: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      activeStep: 0,
      stepList: [
        { label: '对象选择', value: 0 },
        { label: '模型选择', value: 1 },
        { label: '基本设定', value: 2 },
        // { label: '消息推送', value: 3 },
      ],
      searchList: [
        {
          type: 'input',
          placeholder: '请输入企业名称',
          prop: {
            key: 'enterpriseName',
            value: null,
          },
        },
      ],
      cardWidth: '300px',
      enterpriseList: {
        dataList: [],
        tableBottom: 185,
        loading: false,
        searchForm: {
          pageNum: 1,
          pageSize: 10,
        },
        list: [
          {
            prop: 'enterpriseName',
            width: '200px',
            label: '企业名称',
          },
          {
            prop: 'legalName',
            width: '',
            label: '法定代表人',
          },
        ],
        api: '',
        totalNum: 0,
      },
      baseForm: {
        id: null,
        name: null,
        beanName: null,
        methodName: null,
        methodParams: null,
        ruleFrequency: null,
        ruleCron: null,
        startDate: null,
        endDate: null,
        msgFrequency: null,
        msg: null,
        msgContent: null,
        emailFrequency: null,
        email: null,
        emailContent: null,
        status: null,
        createTime: null,
        updateTime: null,
        customerList: [],
        personalList: [],
        ruleList: [],
        dateRange: [],
        period: 1,
      },
      baseRules: {
        name: [
          { required: true, message: '任务名称不能为空', trigger: 'blur' },
        ],
        dateRange: [
          { required: true, message: '期止时间不能为空', trigger: 'blur' },
        ],
        ruleCron: [
          { required: true, message: '模型执行不能为空', trigger: 'blur' },
        ],
      },
      //短信推送
      smsForm: {
        msg: null,
        email: null,
        msgFrequency: null,
        emailFrequency: null,
      },
      radio: null,
      radioList: [
        { label: '每天早上6点', value: '* * 6 * * ? *' },
        { label: '每天晚上11点', value: '* * 11 * * ? *' },
        { label: '周一早上6点', value: '* * 6 ? * MON *' },
        { label: '周一晚上11点', value: '* * 11 ? * MON *' },
        { label: '周日早上6点', value: '* * 6 ? * SUN *' },
        { label: '周日晚上11点', value: '* * 11 ? * SUN *' },
        { label: '月末早上6点', value: '* * 6 L * ? *' },
        { label: '月末晚上11点', value: '* * 11 L * ? *' },
      ],
      periodOptions: [
        {
          dictValue: 0,
          dictLabel: '无限制',
        },
        {
          dictValue: 1,
          dictLabel: '每天',
        },
        {
          dictValue: 2,
          dictLabel: '每周',
        },
        {
          dictValue: 3,
          dictLabel: '每月',
        },
        {
          dictValue: 4,
          dictLabel: '每季度',
        },
        {
          dictValue: 5,
          dictLabel: '每季度',
        },
      ],
      selectTable: {
        dataList: [],
        tableBottom: 120,
        loading: false,
        list: [
          {
            prop: 'enterpriseName',
            width: '200px',
            label: '企业名称',
          },
          {
            prop: 'legalName',
            width: '',
            label: '法定代表人',
          },
        ],
        api: '',
      },
      optionsModel: [],
      selectList: [],
      modelType: [
        {
          value: '1',
          label: '自身风险',
        },
        {
          value: '4',
          label: '关联风险',
        },
        {
          value: '3',
          label: '舆情风险',
        },
        {
          value: '2',
          label: '法人风险',
        },
        // {
        //   value: '5',
        //   label: '担保风险'
        // }
      ],
      // enterpriseList.searchForm: {
      //   pageNum: 1,
      //   pageSize: 10
      // },
      searchParams: {},
      selectedRuleList: [],
    }
  },
  watch: {
    info: {
      handler(val) {
        this.reset()
        if (val && val.id) {
          for (let key in this.baseForm) {
            if (key === 'dateRange') {
              this.baseForm.dateRange = [
                new Date(val.startDate),
                new Date(val.endDate),
              ]
            }
            if (val[key]) this.baseForm[key] = val[key]
          }
          for (let key in this.smsForm) {
            if (val[key]) this.smsForm[key] = val[key]
          }
          this.getSelectEnterpriseList()
          this.getSelectModelList()
          if (!val.period) {
            this.info.period = 0
          }
        }
        this.getEnterpriseList()
        this.getModelList()
      },
      immediate: true,
      deep: true,
    },
  },
  mounted() {
    window.addEventListener('resize', () => {
      let modelWidth = document.getElementsByClassName('model')[0]
      if (modelWidth && modelWidth.clientWidth < 640) {
        this.cardWidth = '100%'
      } else {
        this.cardWidth = '300px'
      }
    })
  },
  methods: {
    getEnterpriseList() {
      this.enterpriseList.loading = true
      searchEnterprisetList({
        ...this.enterpriseList.searchForm,
        ...this.searchParams,
      })
        .then((res) => {
          this.enterpriseList.dataList = res.rows
          this.enterpriseList.loading = false
          this.enterpriseList.totalNum = res.total
        })
        .catch((err) => {})
    },
    getSelectModelList() {
      let params = {
        pageSize: 99999,
        pageNum: 1,
        personId: 0,
        taskId: this.info.id,
      }
      searchSelectModelList(params)
        .then((res) => {
          this.selectList = []
          this.selectList = res.rows
        })
        .catch((err) => {})
    },
    getSelectEnterpriseList() {
      let params = {
        pageSize: 99999,
        pageNum: 1,
        personId: 0,
        taskId: this.info.id,
      }
      this.selectTable.loading = true
      searchSelectEnterprisetList(params)
        .then((res) => {
          res.rows.forEach((item) => {
            let obj = {
              id: item.enterpriseId,
              enterpriseName: item.enterpriseName,
              legalName: item.legalName,
            }
            this.selectTable.dataList.push(obj)
          })
          this.selectTable.loading = false
        })
        .catch((err) => {})
    },
    getModelList() {
      this.selectList = []
      let params = {
        pageSize: 99999,
        pageNum: 1,
        modelType: '9',
        status: '1',
      }
      searchModelList({ ...params })
        .then((res) => {
          this.optionsModel = res.rows
          if (!this.info.id) {
            this.optionsModel.forEach((item) => {
              if (item.status === '1') {
                this.selectList.push(item)
              }
            })
          }
          if (res.row.length > 0) {
            this.getRule()
          }
        })
        .catch((err) => {})
    },
    getRule() {
      let params = {
        pageNum: 1,
        pageSize: 9999,
        taskId: this.info.id,
      }
      searchTasksRuleList({ ...params })
        .then((res) => {
          res.rows.forEach((item) => {
            this.selectedRuleList.push(item.modelId)
          })
          this.optionsModel.forEach((item) => {
            item.status = '0'
            for (var i = 0; i < res.rows.length; i++) {
              let item = res.rows[i]
              if (item.id == item.modelId) {
                item.status = '1'
                break
              }
            }
          })
        })
        .catch((err) => {})
    },
    handleSearch(data) {
      this.searchParams = data
      this.enterpriseList.searchForm.pageNum = 1
      this.getEnterpriseList()
    },
    handleCurrentChange(val) {
      this.enterpriseList.searchForm.pageNum = val
      this.getEnterpriseList()
    },
    handleSizeChange(val) {
      this.enterpriseList.searchForm.pageSize = val
      this.getEnterpriseList()
    },
    addModelCode(data) {
      let list = this.selectList
      // let selectData = JSON.parse(JSON.stringify())
      if (data) {
        if (!list.find((item) => item.id == data.id)) {
          data.status = '1'
          list.push(data)
        }
      }
    },
    removeModelCode(data) {
      this.selectList.forEach((item, index) => {
        if (item.id === data.id) {
          data.status = '0'
          this.selectList.splice(index, 1)
        }
      })
    },
    submit() {
      let form = {
        modelList: null,
        startDate: null,
        endDate: null,
        customerList: null,
        taskType: 1,
        ...this.baseForm,
        ...this.smsForm,
      }
      let list = []
      this.selectList.forEach((item) => {
        list.push(item.modelId || item.id)
      })
      form.modelList = list
      form.customerList = this.selectTable.dataList
      if (this.baseForm.dateRange.length > 0) {
        form.startDate = new Date(this.baseForm.dateRange[0])
        form.endDate = new Date(this.baseForm.dateRange[1])
      }
      submitForm({ ...form })
        .then((res) => {
          this.$message.success('操作成功')
          this.$emit('close', this.info.status)
        })
        .catch((err) => {})
    },
    toNextStep() {
      switch (this.activeStep) {
        case 0:
          if (
            this.info.status !== '1' &&
            !this.selectTable.dataList.length > 0
          ) {
            this.$message.error('请先选择企业')
            return
          } else {
            console.log(this.selectTable.dataList)
            this.activeStep++
          }
          break
        case 1:
          if (this.info.status !== '1' && !this.selectList.length > 0) {
            this.$message.error('请先选择模型')
            return
          } else {
            this.activeStep++
          }
          break
        case 2:
          if (this.info.status === '1') {
            this.$emit('close', this.info.status)
            return
          }
          this.$refs.baseForm.validate((valid) => {
            if (valid) {
              // this.activeStep++
              this.submit()
            }
          })
          break
        // case 3:
        //   this.submit()
        //   break;
      }
    },
    previousStep() {
      this.activeStep--
    },
    handleModelType(data) {
      let myData = this.modelType.find((item) => item.value === data)
      if (myData) {
        return myData.label
      }
    },
    reset() {
      this.activeStep = 0
      this.enterpriseList.dataList = []
      this.enterpriseList.searchForm = {
        pageNum: 1,
        pageSize: 10,
      }
      this.selectTable.dataList = []
      this.selectList = []
      this.smsForm = {
        msg: null,
        email: null,
        msgFrequency: null,
        emailFrequency: null,
      }
      this.baseForm = {
        name: null,
        beanName: null,
        methodName: null,
        methodParams: null,
        ruleFrequency: null,
        ruleCron: null,
        startDate: null,
        endDate: null,
        msgFrequency: null,
        msg: null,
        msgContent: null,
        emailFrequency: null,
        email: null,
        emailContent: null,
        status: null,
        createTime: null,
        updateTime: null,
        customerList: [],
        personalList: [],
        ruleList: [],
        dateRange: [],
        period: 1,
        id: null,
      }
    },
  },
}
</script>

<style lang="less" scoped>
.model_task {
  position: relative;
  height: calc(100vh - 85px);

  .stepList {
    width: 860px;
    display: flex;
    align-items: center;
    justify-content: center;

    .step {
      display: flex;

      .circular {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background-color: #cccccc;
        color: #fff;
        margin-right: 12px;
      }

      .active {
        background: var(--primary-color);
      }

      .line {
        width: 80px;
        height: 2px;
        background-color: #e6e9ed;
        border-radius: 1px 1px 1px 1px;
        margin: 0px 20px;
      }
    }
  }

  .content {
    margin-top: 50px;

    .search {
      margin-bottom: 20px;
    }

    .model {
      width: 48%;
      height: calc(100vh - 260px);
      background: rgba(#000, 0.02);
      border-radius: 12px;
      padding: 20px;
      box-sizing: border-box;
      overflow: hidden;

      .title {
        font-size: 12px;
        color: #000;
        font-weight: 550;
        margin-bottom: 20px;
      }

      .model_content {
        height: calc(100% - 30px);
        overflow-y: scroll;

        .model_cardList {
          display: flex;
          justify-content: space-between;
          flex-wrap: wrap;

          .card {
            height: 180px;
            border: none;
            margin-bottom: 10px;
            background-color: rgba(#fa5151, 0.02);

            .card_content {
              cursor: pointer;
              width: 100%;
              height: 100%;
              padding: 20px;
              border-radius: 5px;
              background: rgba(250, 81, 81, 0.05);
              border: 1px solid rgba(0, 0, 0, 0.08);
              display: flex;
              flex-direction: column;
              justify-content: space-between;

              .top {
                display: flex;
                align-items: center;
                justify-content: space-between;
                height: 30px;

                .title {
                  font-size: 20px;
                  color: rgba(#000, 0.85);
                  font-weight: 550;
                }
              }

              .descr {
                height: 70px;
                font-size: 14px;
                color: rgba(#000, 0.6);
                line-height: 24px;
                overflow: hidden;

                > div {
                  height: 100%;
                  overflow-y: scroll;
                  -ms-overflow-style: none;
                  overflow: -moz-scrollbars-none;
                }

                > div::-webkit-scrollbar {
                  width: 0 !important;
                }
              }

              .switch {
                display: flex;
                align-items: center;
                justify-content: space-between;
                height: 30px;

                > div {
                  color: #00b578;
                  font-size: 14px;

                  > i {
                    margin-right: 5px;
                  }
                }
              }
            }

            .card_content:hover {
              border: 1px solid rgba(4, 0, 255, 0.4);
            }
          }
        }
      }

      > .model_content::-webkit-scrollbar {
        width: 0 !important;
      }
    }
  }

  .btnBotton {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    position: absolute;
    bottom: 20px;
    right: 10px;

    > button {
      border-radius: 6px 6px 6px 6px;
      height: 42px;
      border: none;
    }

    > button:nth-of-type(1) {
      color: #fff;
      padding: 10px 30px;
      background: var(--primary-color);
    }

    > button:nth-of-type(2) {
      padding: 10px 10px;
      color: rgba(#000, 0.85);
      background-color: #f0f2f5;
      margin-left: 20px;
    }
  }
}

.flex {
  display: flex;
  align-items: center;
  justify-content: center;
}

.flex_between {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
