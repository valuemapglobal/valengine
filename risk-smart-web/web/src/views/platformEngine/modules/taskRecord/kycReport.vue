<template>
  <el-drawer
    :title="drawer.title"
    :visible.sync="drawer.visible"
    :size='drawer.width'
    :before-close="handleClose"
  >
    <div
      class="kycReport"
      id="pdfDom"
      v-loading="loading"
      :element-loading-text="loadText"
    >
      <div class="report_top">
        <p>风控评估报告</p>
        <p>生成时间：{{createTime}}</p>
        <div
          class="download"
          @click="getPdf('#pdfDom','测试')"
        ><img src="../../image/download.png">下载报告</div>
      </div>
      <div class="report_card">
        <div class="card_title">评估主体信息</div>
        <template v-for="(item,index) in subjectInfoList">
          <div
            class="card_row"
            :key="index"
          >
            {{`${item.label}：${item.data}`}}
          </div>
        </template>
      </div>
      <div class="report_card">
        <div class="card_title">风险评估结果</div>
        <div class="report_chart">
          <div class="chart_left moduleCard">
            <div class="trapezium_title">
              审批建议
              <div class="left"></div>
              <div class="right"></div>
            </div>
            <div class="bold_red">
              {{approvalSuggestions || '-'}}
            </div>
          </div>
          <div class="chart_right">
            <template v-for="(item,index) in scoreList">
              <div
                class="moduleCard"
                :key="index"
              >
                <div class="trapezium_title">
                  {{item.label}}
                  <div class="left"></div>
                  <div class="right"></div>
                </div>
                <div class="result">
                  {{`${item.data != '-'?item.data+item.unit:'-'}`}}
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>
      <div class="report_card">
        <div class="card_title">风险信息结果</div>
        <gutuTable
          :dataList="tableList"
          :loading="tableLoading"
          :columnConfig="columnConfig"
          :handle="tableHandle"
          :collectionList='collectionList'
          tabIndex
        />
      </div>
    </div>
  </el-drawer>
</template>

<script>
import GutuTable from '@/components/gutu/gutuTable'
import { getGMReport } from '../../api/platformEngine';
export default {
  components: {
    GutuTable
  },
  props: {
    data: {
      default: null
    }
  },
  data() {
    return {
      createTime: null,
      subjectInfoList: [
        { label: '客户名称', data: '-', field: 'userName' },
        { label: '客户身份', data: '-', field: 'userType' },
        { label: '手机号', data: '-', field: 'phone' },
        { label: '身份证号', data: '-', field: 'idNumber' },
        { label: '企业名称', data: '-', field: 'enterpriseName' },
        { label: '统一社会信用代码证号', data: '-', field: 'creditCode' },
      ],
      scoreList: [
        { label: '评分', data: '-', field: 'score', unit: '分' },
        { label: '多头评分', data: '-', field: 'multiScore', unit: '分' },
        { label: '偿债压力评分', data: '-', field: 'debt', unit: '分' },
        { label: '额度调整折扣', data: '-', field: 'quotaRate', unit: '%' },
      ],
      tableList: [],
      tableLoading: false,
      columnConfig: [
        { label: '编号', field: 'code', width: '75px', },
        { label: '风险等级', field: 'risk_level', width: '96px', },
        { label: '风险说明', field: 'decision_desc' },
        { label: '是否强拒绝', field: 'strongly_reject', width: '100px', },
      ],
      tableHandle: null,
      collectionList: {},
      loading: false,
      approvalSuggestions: '-',
      loadText: null,
      drawer: {
        title: '风控评估报告',
        visible: false,
        type: 'addCode',
        width: '640px'
      },
      orderNo: null
    }
  },
  watch: {
    data: {
      handler(val) {
        if (val) {
          this.orderNo = val
        }
      },
    },
    'drawer.visible': {
      handler(val) {
        if (val) this.getInfoData()
        else this.reset()

      }
    }
  },
  mounted() {
    this.currentTime()
  },
  methods: {
    getInfoData() {
      if (!this.orderNo) return
      this.loading = true
      getGMReport({ orderNo: this.orderNo }).then((res) => {
        if (res.code == 200) {
          let data = res.data
          this.info = data
          this.subjectInfoList.forEach(item => {
            if (data.subjectInformation[item.field]) {
              item.data = data.subjectInformation[item.field]
            }
          })
          this.approvalSuggestions = data.riskAssessmentResults.approvalSuggestions || '-'
          this.scoreList.forEach(item => {
            if (data.riskAssessmentResults[item.field]) {
              item.data = data.riskAssessmentResults[item.field]
            }
          })
          this.tableList = data.insuranceInformationResults
        }
        this.loading = false
      }).catch((err) => {

      });
    },
    currentTime() {
      var date = new Date();
      var year = date.getFullYear(); //月份从0~11，所以加一
      let month = date.getMonth();
      console.log("month", month);
      var dateArr = [
        date.getMonth() + 1,
        date.getDate(),
      ];
      //如果格式是MM则需要此步骤，如果是M格式则此循环注释掉
      for (var i = 0; i < dateArr.length; i++) {
        if (dateArr[i] >= 1 && dateArr[i] <= 9) {
          dateArr[i] = "0" + dateArr[i];
        }
      }
      var strDate = `${year}-${dateArr[0]}-${dateArr[1]}`
      this.createTime = strDate
    },
    reset() {
      this.subjectInfoList = this.$options.data().subjectInfoList
      this.scoreList = this.$options.data().scoreList
      this.tableList = []
    },
    handleClose() {
      this.reset()
      this.drawer.visible = false
    },
  },

}
</script>

<style lang='less' scoped>
@import "/src/assets/less/commonCss.less";
.kycReport {
  width: 595px;
  background-color: #59abfc;
  padding: 40px 24px 24px;
  .report_top {
    width: 100%;
    height: 210px;
    padding: 40px 26px 0px 26px;
    color: #fff;
    position: relative;
    p:first-of-type {
      font-size: 60px;
      font-weight: 800;
    }
    p:last-of-type {
      font-size: 30px;
      font-weight: 400;
    }
    .download {
      position: absolute;
      cursor: pointer;
      top: 12px;
      right: 26px;
      font-size: 14px;
      font-weight: 500;
      > img {
        width: 20px;
        height: 20px;
        margin-right: 4px;
      }
    }
  }
  .report_card {
    width: 100%;
    border-radius: 12px;
    background-color: #fff;
    padding: 20px;
    margin-top: 20px;
    .card_title {
      font-size: 22px;
      font-weight: 800;
      color: #2a87e8;
      margin-bottom: 14px;
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
    .moduleCard {
      border-radius: 12px;
      background: linear-gradient(180deg, #f6faff 0%, #ffffff 100%);
      border-radius: 12px 12px 12px 12px;
      border: 2px solid rgba(126, 182, 239, 0.2);
      overflow: hidden;
      position: relative;
      .trapezium_title {
        min-width: 90px;
        text-align: center;
        padding: 2px 12px;
        font-size: 14px;
        background-color: #2a87e8;
        color: #fff;
        position: relative;
        display: inline;
        position: absolute;
        left: 50%;
        transform: translateX(-50%);
        white-space: nowrap;
        > .left,
        > .right {
          height: 20px;
          width: 50px;
          position: absolute;
          background-color: #f6faff;
          transform: rotate(75deg);
          // bottom: -6px;
          top: -5px;
        }

        > .left {
          left: -32px;
        }

        > .right {
          right: -32px;
          transform: rotate(-75deg);
        }
      }
      .result,
      .bold_red {
        width: 100%;
        height: 100%;
        display: flex;
        justify-content: center;
      }
      .result {
        padding-top: 35px;
        font-size: 20px;
        font-weight: 700;
        color: #3662ec;
      }
      .bold_red {
        font-size: 40px;
        font-weight: 700;
        color: #fa5151 !important;
        align-items: center;
        padding-top: 0px;
      }
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
</style>