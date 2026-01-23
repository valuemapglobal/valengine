<template>
  <div class="kycReport" :class="separate ? 'kycReportMain' : ''" v-if="info">
    <div
      class="mask"
      v-if="loading"
      v-loading="true"
      :element-loading-text="loadText"
    ></div>
    <div class="download" @click="getPdf('#pdfDom', '风险报告')">
      <img src="../../image/download.png" />下载报告
    </div>
    <div id="pdfDom">
      <div class="report_top">
        <p>{{ kycType == '0' ? '企业' : '个人' }}风险报告</p>
        <p>生成时间：{{ info.baseinfo.createTime || '-' }}</p>
        <p>任务编号：{{ this.orderNo }}</p>
      </div>
      <div class="report_card">
        <div class="card_title">1、基本信息</div>
        <table>
          <tbody>
            <tr>
              <td class="title">
                {{ kycType == '0' ? '企业名称' : '客户姓名' }}
              </td>
              <td>
                {{
                  kycType == '0'
                    ? info.baseinfo.enterpriseName
                    : info.baseinfo.userName || '-'
                }}
              </td>
            </tr>
            <tr v-if="kycType == '1'">
              <td class="title">手机号</td>
              <td>{{ info.baseinfo.mobile || '-' }}</td>
            </tr>
            <tr v-if="kycType == '1'">
              <td class="title">身份证号</td>
              <td>{{ info.baseinfo.idNumber || '-' }}</td>
            </tr>
            <tr>
              <td class="title">评级结果</td>
              <td>{{ info.baseinfo.evaluationResult || '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="report_card">
        <div class="card_title">2、综合评估</div>
        <table>
          <tbody>
            <tr>
              <td class="title">评分</td>
              <td>
                {{
                  scoreInfo && scoreInfo.score
                    ? scoreInfo.score
                    : info.comprehensiveEvaluation.score || '-'
                }}
              </td>
              <td class="title">评级</td>
              <td>{{ info.comprehensiveEvaluation.rating || '-' }}</td>
            </tr>
            <tr>
              <td class="title">最高风险等级</td>
              <td>
                {{ info.comprehensiveEvaluation.highestRiskLevel || '-' }}
              </td>
              <td class="title">风险定级</td>
              <td>{{ info.comprehensiveEvaluation.riskPricing || '-' }}</td>
            </tr>
            <tr>
              <td class="title">风险额度</td>
              <td>{{ '-' }}</td>
              <td class="title">应用模型</td>
              <td>
                {{ info.comprehensiveEvaluation.applicationModel || '-' }}
              </td>
            </tr>
            <tr>
              <td class="title">偿债压力指数</td>
              <td>
                {{ scoreInfo && scoreInfo.debt != null ? scoreInfo.debt : '-' }}
              </td>
              <td class="title">额度调整折扣</td>
              <td>
                {{
                  scoreInfo && scoreInfo.quotaRate
                    ? scoreInfo.quotaRate + '%'
                    : '-'
                }}
              </td>
            </tr>
            <tr v-if="flag == 'txd' && txdJsonData.monthDebt != null">
              <td class="title">存续月债务</td>
              <td :colspan="3">{{ txdJsonData.monthDebt || '-' }}</td>
            </tr>
            <tr v-if="flag == 'txd' && txdJsonData.mortgageLoanIncome != null">
              <td class="title">通过按揭贷款计算客户收入</td>
              <td :colspan="3">{{ txdJsonData.mortgageLoanIncome || '-' }}</td>
            </tr>
            <tr
              v-if="flag == 'txd' && txdJsonData.housingMortgageIncome != null"
            >
              <td class="title">通过房抵贷计算客户收入</td>
              <td :colspan="3">
                {{ txdJsonData.housingMortgageIncome || '-' }}
              </td>
            </tr>
            <tr v-if="flag == 'txd' && txdJsonData.carMortgageIncome != null">
              <td class="title">通过车按揭计算客户收入</td>
              <td :colspan="3">{{ txdJsonData.carMortgageIncome || '-' }}</td>
            </tr>
          </tbody>
        </table>
        <el-link
          style="margin-top: 10px"
          v-if="pdfUrl"
          type="primary"
          @click="openUrlReport(pdfUrl)"
          >征信报告查看</el-link
        >
        <br />
        <el-link
          style="margin-top: 10px"
          v-if="judicialUrl"
          type="primary"
          @click="openUrlReport(judicialUrl)"
          >司法报告查看</el-link
        >
      </div>
      <div class="report_card">
        <div class="card_title">3、数据调用</div>
        <GutuTable
          :dataList="dataCallList"
          :columnConfig="[
            { label: '数据类型', field: 'interfaceType' },
            {
              label: '模型应用',
              field: 'modelApplication',
              type: 'tag',
              pairedList: 'modelType',
            },
            { label: '调用状态', field: 'callStatus' },
          ]"
          :collectionList="collectionList"
        ></GutuTable>
      </div>
      <div class="report_card">
        <div class="card_title">4、命中规则</div>
        <template v-for="(item, index) in hitRuleList">
          <div :key="index">
            <div class="card_subtitle">
              {{ `4.${index + 1}、${item.subtitle}` }}
            </div>
            <GutuTable
              :dataList="item.dataList"
              :columnConfig="[
                { label: '规则编号', field: 'code', width: '160' },
                { label: '规则描述', field: 'decision', width: '' },
                { label: '风险等级', field: 'riskLevel', width: '90' },
                { label: '标签', field: 'stronglyReject', width: '90' },
              ]"
              :collectionList="collectionList"
            ></GutuTable>
          </div>
        </template>
      </div>
      <!-- <div class="report_card">
        <div class="card_title">5、司法数据</div>
        <table style="margin-bottom: 20px;" v-for="(item,index) in judicialList" :key="index">
          <tbody>
            <tr>
              <td class="title">名称</td>
              <td>{{item.companyName || '-'}}</td>
              <td class="title">案件类型</td>
              <td>{{item.caseClass || '-'}}</td>
            </tr>
            <tr>
              <td class="title">案号</td>
              <td>{{item.caseCode || '-'}}</td>
              <td class="title">执行案号</td>
              <td>{{item.executeCase || '-'}}</td>
            </tr>
            <tr>
              <td class="title">一审案号</td>
              <td>{{item.trialCase || '-'}}</td>
              <td class="title">复审案号</td>
              <td>{{item.retrialCase || '-'}}</td>
            </tr>
            <tr>
              <td class="title">案由</td>
              <td>{{item.nCaseReason || '-'}}</td>
              <td class="title">案件身份</td>
              <td>{{item.nCaseRole || '-'}}</td>
            </tr>
            <tr>
              <td class="title">立案时间</td>
              <td>{{item.filingTime || '-'}}</td>
              <td class="title">最早案件日期</td>
              <td>{{item.earliestYear || '-'}}</td>
            </tr>
            <tr>
              <td class="title">最新进程日期</td>
              <td>{{item.lastDate || '-'}}</td>
              <td class="title">审判结果</td>
              <td>{{item.caseResult || '-'}}</td>
            </tr>
            <tr>
              <td class="title">原告是否是金融机构</td>
              <td>{{item.isFinancial?'否':"是" || '-'}}</td>
              <td class="title">案件是否是信贷案件</td>
              <td>{{item.isCredit?'否':"是"  || '-'}}</td>
            </tr>
            <tr>
              <td class="title">起诉金额</td>
              <td>{{item.nCaseAmt || '-'}}</td>
              <td class="title">裁决金额</td>
              <td>{{item.nPunishmentAmt || '-'}}</td>
            </tr>
         
          </tbody>
        </table>
      </div> -->
      <div class="report_card">
        <div class="card_title">5、使用场景</div>
        <table>
          <tbody>
            <tr>
              <td class="title">主体</td>
              <td>{{ info.applicableScenarios.subject || '-' }}</td>
            </tr>
            <tr>
              <td class="title">产品</td>
              <td>{{ info.applicableScenarios.product || '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="explanatory">
        <span>注释：</span>
        <span>1、 评估结果定义和判断标准：</span>
        <div>
          <span class="indentation">（1）</span>
          初筛、准入评估结果：建议拒绝、建议通过、建议复核。
        </div>
        <div>
          <span class="indentation">（2）</span> 评分评估结果：建议复核。
        </div>
        <div>
          <span class="indentation">（3）</span>
          初筛、准入判断标准：命中规则标签显示强拒为建议拒绝；最高风险等级为低风险或中低风险建议通过；其余指标为建议复核。
        </div>
        <span>2、 综合评估指标定义：</span>
        <div>
          <span class="indentation">（1）</span>
          评分：报告根据应用模型分为主体评分/财务评分/个人评分三类报告。
        </div>
        <div>
          <span class="indentation">（2）</span>
          评级：为预留功能，待应用模型成熟后，如业务单位有需求，后期对应评分筛查形成评级策略、评级阶段。
        </div>
        <div>
          <span class="indentation">（3）</span>
          最高风险等级：取所有规则策略命中风险等级中最高项的风险等级。
        </div>
        <div>
          <span class="indentation">（4）</span
          >风险定价：为预留功能，指风险利率，与评分、评级相关联；定价基础逻辑：根据用户群体风险特征结合历史数据表现，制定基准利率，称之为“标准定价”，同时根据个体风险评分、风险评级在标准定价基础上进行上浮或下降不同的利率形成个体风险定价。
        </div>
        <div>
          <span class="indentation">（5）</span
          >风险额度：为预留功能，根据客户的实际风险及收入负债情况决定额度。
        </div>
        <div><span class="indentation">（6）</span>应用模型：</div>
        <div>
          <span class="indentation"></span
          >承租人/共同承租人/担保人营销初筛-企业风险筛查；
        </div>
        <div>
          <span class="indentation"></span
          >承租人/共同承租人/担保人初筛-企业风险筛查；
        </div>
        <div>
          <span class="indentation"></span
          >承租人/共同承租人/担保人准入-企业风险筛查；
        </div>
        <div>
          <span class="indentation"></span
          >承租人/共同承租人/担保人主体评分-企业风险筛查；
        </div>
        <div>
          <span class="indentation"></span
          >承租人/共同承租人/担保人财务评分-企业风险筛查；
        </div>
        <div>
          <span class="indentation"></span
          >借款人/实控人/担保人/共债人（配偶）营销初筛-企业风险筛查；
        </div>
        <div>
          <span class="indentation"></span
          >借款人/实控人/担保人/共债人（配偶）初筛-企业风险筛查；
        </div>
        <div>
          <span class="indentation"></span
          >借款人/实控人/担保人/共债人（配偶）准入-企业风险筛查；
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import GutuTable from '@/components/gutu/gutuTable'
import { getGMReport } from '../../api/platformEngine'
export default {
  components: {
    GutuTable,
  },
  props: {
    data: {
      default: null,
    },
    flag: {
      default: null,
    },
    pdfUrl: {
      default: null,
    },
    judicialUrl: {
      default: null,
    },
  },
  data() {
    return {
      createTime: null,
      tableHandle: null,
      collectionList: {},
      loading: false,
      loadText: '数据加载中',
      collectionList: {
        modelType: [
          { label: '×', value: 0 },
          { label: '√', value: 1 },
        ],
      },
      dataCallList: [],
      judicialList: [],
      info: null,
      scoreInfo: null,
      orderNo: null,
      kycType: null,
      hitRuleList: [],
      separate: false,
      txdJsonData: {
        monthDebt: null,
        mortgageLoanIncome: null,
        housingMortgageIncome: null,
        carMortgageIncome: null,
      },
    }
  },
  watch: {
    data: {
      handler(val) {
        this.reset()
        this.orderNo = val
        this.getInfoData()
        // this.currentTime()
      },
      immediate: true,
    },
    '$route.query': {
      handler(val) {
        if (val.hasOwnProperty('orderNo')) {
          this.reset()
          this.orderNo = val.orderNo
          this.separate = true
          this.getInfoData()
          // this.currentTime()
        }
      },
      deep: true,
      immediate: true,
    },
  },
  mounted() {},
  methods: {
    getInfoData() {
      if (!this.orderNo) return
      this.loading = true
      this.hitRuleList = []
      getGMReport({ orderNo: this.orderNo })
        .then((res) => {
          if (res.code == 200) {
            let data = res.data
            this.info = data
            this.scoreInfo = data.scoreRuleByOrder || null
            this.kycType = data.baseinfo.businessType
            this.dataCallList = data.dataCalling

            // this.txdJsonData = this.$options.data().txdJsonData
            data.dataCalling.forEach((item) => {
              if (item.interfaceType == '征信报告' && item.responseBody) {
                let jsonData = JSON.parse(item.responseBody).data
                if (jsonData.hasOwnProperty('monthDebt')) {
                  this.txdJsonData.monthDebt =
                    this.formatMoney(jsonData.monthDebt) + '元'
                }
                if (jsonData.hasOwnProperty('mortgageLoanIncome')) {
                  this.txdJsonData.mortgageLoanIncome =
                    this.formatMoney(jsonData.mortgageLoanIncome) + '元'
                }
                if (jsonData.hasOwnProperty('housingMortgageIncome')) {
                  this.txdJsonData.housingMortgageIncome =
                    this.formatMoney(jsonData.housingMortgageIncome) + '元'
                }
                if (jsonData.hasOwnProperty('carMortgageIncome')) {
                  this.txdJsonData.carMortgageIncome =
                    this.formatMoney(jsonData.carMortgageIncome) + '元'
                }
              }
              if (item.interfaceType == '个人司法' && item.responseBody) {
                let jsonData = JSON.parse(item.responseBody).data
                this.judicialList = jsonData
              }
            })

            data.hitRule.forEach((item) => {
              if (item.groupName && item.rule.length) {
                this.hitRuleList.push({
                  subtitle: item.groupName,
                  dataList: item.rule,
                })
              }
            })
          }
          this.loading = false
        })
        .catch((err) => {
          this.loading = false
        })
    },
    openUrlReport(url) {
      window.open(url, '_blank')
    },
    reset() {
      this.info = null
      this.scoreInfo = null
      this.kycType = null
      this.dataCallList = null
      this.hitRuleList = []
      this.txdJsonData = this.$options.data()
    },
  },
}
</script>

<style lang="less" scoped>
@import '/src/assets/less/commonCss.less';
.kycReportMain {
  margin: 0px auto;
  height: 100%;
  overflow: auto;
}
.kycReport {
  width: 695px;
  padding: 20px;
  position: relative;
  .mask {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
  }
  .report_top {
    width: 100%;
    padding: 20px 20px 0px;
    // color: #fff;
    position: relative;
    p:first-of-type {
      font-size: 18px;
      font-weight: 800;
      text-align: center;
      margin-bottom: 10px;
    }
    p {
      font-size: 14px;
      font-weight: 400;
      text-align: right;
      margin-bottom: 0px;
    }
  }
  .report_card {
    width: 100%;
    border-radius: 12px;
    background-color: #fff;
    padding: 20px 20px 0px;
    .card_title,
    .card_subtitle {
      font-size: 16px;
      font-weight: 800;
      color: rgba(0, 0, 0, 0.85);
      margin-bottom: 14px;
    }
    .card_subtitle {
      font-size: 14px;
      font-weight: 500;
    }
    .card_row {
      font-size: 16px;
      font-weight: 400;
      color: rgba(0, 0, 0, 0.85);
      line-height: 28px;
    }
    .report_chart {
      display: flex;
      justify-content: space-between;
      .chart_left {
        width: 160px;
        height: 170px;
      }
      .chart_right {
        width: calc(100% - 160px - 20px);
        height: 170px;
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        > div {
          width: 162px;
          height: 80px;
        }
      }
    }
  }
}
.download {
  // position: absolute;
  cursor: pointer;
  // top: 12px;
  // right: 26px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 90px;
  height: 42px;
  background-color: var(--primary-color);
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  border-radius: 5px;
  > img {
    width: 20px;
    height: 20px;
    margin-right: 4px;
  }
}
.explanatory {
  font-size: 12px;
  line-height: 30px;
  padding: 20px;
  > span {
    width: 100%;
    display: inline-block;
  }
  > div {
    display: flex;
    .indentation {
      min-width: 40px;
      height: 100%;
      text-align: right;
    }
  }
}
/deep/.el-table {
  .el-table__header {
    .has-gutter {
      tr {
        background-color: #fff !important;
      }
    }
  }
}
/deep/.gutuTable {
  margin-bottom: 20px;
}
/deep/ table {
  width: 100%;
  border-radius: 6px;
  border-collapse: collapse;
  overflow: hidden;

  th,
  td {
    border: 1px solid rgba(0, 0, 0, 0.1);
    padding: 10px;
    word-break: break-all;
    font-size: 14px;
    font-family: PingFangSC-Regular, PingFang SC !important;
    font-weight: 400;
    color: #000000 !important;
    height: 37px;
    box-sizing: border-box;
    // white-space: nowrap;
  }

  th {
    background-color: rgba(var(--primary-color), 0.06) !important;
  }
}
.title {
  background: rgba(var(--primary-color), 0.06);
  color: rgba(#000, 0.85);
  width: 110px;
  // max-width: 120px !important;
  font-size: 14px;
  font-family: PingFang SC-Medium, PingFang SC;
  font-weight: 500;
}
</style>
