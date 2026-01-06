<template>
  <div class="preApproval">
    <div class="btnList">
      <div style="display: flex">
        <template v-for="(item, index) in btnList">
          <el-button
            class="btn"
            @click="clickActive(item)"
            :class="{
              active: active === item.template,
              disable: item.hasOwnProperty('btnPermission')
                ? !hasButton(item.btnPermission)
                : item.disabled,
            }"
            :disabled="
              item.hasOwnProperty('btnPermission')
                ? !hasButton(item.btnPermission)
                : item.disabled
            "
            :key="index"
          >
            {{ item.label }}
          </el-button>
        </template>
      </div>
      <div class="operateBtn">
        <!-- 暂时注释 20260105 -->
        <!-- v-if="active != 'rule' && hasButton('strategy:release:show')" -->
        <el-tooltip
          class="item"
          effect="light"
          content="点击这里，让功能操作同步到在线运行环境生效！"
          placement="bottom"
          v-if="hasButton('strategy:release:show')"
        >
          <el-button type="primary" @click="releaseRule"> 确认发布 </el-button>
        </el-tooltip>
        <!-- 暂时注释 20260105 -->
        <!-- <template v-if="active == 'rule'">
          <el-button
            @click="handleCompare"
            :disabled="btnLoading"
            type="primary"
            v-if="batchId"
          >
            <i v-if="btnLoading" class="el-icon-loading" />
            {{ btnLoading ? '版本对比中...' : '版本对比' }}
          </el-button>
          <el-button
            type="primary"
            @click="handleOpenReview"
            v-if="batchId && hasButton('strategy:release:show')"
          >
            确认发布
          </el-button>
          <div class="icon" @click="handleOpenHistory">
            <i class="el-icon-time" />
            <div v-if="hasNew" class="redPoint" />
          </div>
        </template> -->
      </div>
    </div>
    <div class="content">
      <components :is="active" :ref="active" :step.sync="step"></components>
    </div>
    <SubmitReview ref="submitReviewRef" />
    <HistoricalBatch ref="historicalBatchRef" @isRead="hasNew = false" />
  </div>
</template>

<script>
import SubmitReview from '../components/SubmitReview.vue'
import HistoricalBatch from '../components/HistoricalBatch.vue'
import rule from './rule.vue'
import score from './score.vue'
import classify from './classify.vue'
import rate from './rate.vue'
import limit from './limit.vue'
import price from './price.vue'
import { mapActions, mapGetters, mapState } from 'vuex'
import { notificationsStatus, exportStrategy, comparePreview } from '../api'
// 暂时注释 20260105
// import { handleCheckLock } from '../utils/index.js'
import { getFileNameFromHeaders } from '@/utils/rouyi.js'

export default {
  components: {
    SubmitReview,
    HistoricalBatch,
    rule,
    classify,
    score,
    rate,
    limit,
    price,
  },
  name: 'PreApproval',
  data() {
    return {
      btnList: [
        { label: '规则模型', template: 'rule', disabled: false, ruleCode: 5 },
        {
          label: '分类模型',
          template: 'classify',
          disabled: false,
          ruleCode: 6,
        },
        { label: '评分模型', template: 'score', disabled: false, ruleCode: 1 },
        {
          label: '评级模型',
          template: 'rate',
          disabled: false,
          ruleCode: 2,
        },
        { label: '额度模型', template: 'limit', disabled: false, ruleCode: 3 },
        { label: '定价模型', template: 'price', disabled: false, ruleCode: 4 },
      ],
      active: 'rule',
      step: 0,

      addLevelStatus: false,
      hasNew: false,
      interval: null,
      btnLoading: false,
    }
  },
  computed: {
    ...mapState(['dataRisk']),
    // ...mapGetters(['batchId']), 暂时注释 20260105
  },
  watch: {
    active: {
      handler(cur) {
        const obj = this.btnList.find((item) => item.template === this.active)
        this.changeProductDecision({
          ruleCode: obj.ruleCode,
          moduleName: obj.label,
        })

        // 暂时注释 20260105
        // if (cur == 'rule') {
        //   this.handleStartPoll()
        // }
      },
      deep: true,
      immediate: true,
    },
  },
  mounted() {},
  methods: {
    ...mapActions(['changeProductDecision']),
    getFileNameFromHeaders,

    // 暂时注释 20260105
    // handleCheckLock,
    // getNotificationsStatus() {
    //   let decision = this.dataRisk.decision
    //   notificationsStatus({
    //     projectCode: decision.projectCode + '',
    //     businessCode: decision.businessCode + '',
    //     ruleCode: decision.ruleCode + '',
    //   }).then((res) => {
    //     if (res.code == 200) {
    //       this.hasNew = res.data.hasNew
    //     }
    //   })
    // },
    // handleStartPoll() {
    //   this.getNotificationsStatus()
    //   if (this.interval) {
    //     clearInterval(this.interval)
    //   }
    //   this.interval = setInterval(() => {
    //     this.getNotificationsStatus()
    //   }, 5000)
    // },

    clickActive(data) {
      this.active = data.template
    },
    async handleCompare() {
      // 暂时注释 20260105
      // if (!(await this.handleCheckLock())) return
      this.btnLoading = true
      let decision = this.dataRisk.decision
      comparePreview({
        // batchId: this.batchId, 暂时注释 20260105
        projectCode: decision.projectCode + '',
        businessCode: decision.businessCode + '',
        ruleCode: decision.ruleCode + '',
      })
        .then((res) => {
          const fileName =
            this.getFileNameFromHeaders(res.headers) || '版本对比'
          this.handleDownload(res.data, fileName)
        })
        .catch((err) => {})
        .finally(() => {
          this.btnLoading = false
        })
    },

    handleDownload(file, fileName = '版本对比') {
      const blob = new Blob([file], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      })

      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = fileName // 设置下载文件名
      a.style.display = 'none' // 隐藏元素

      document.body.appendChild(a)
      a.click()

      // 清理：移除元素和释放 URL
      setTimeout(() => {
        document.body.removeChild(a)
        URL.revokeObjectURL(url)
      }, 100)
    },
    handleOpenReview() {
      this.$refs.submitReviewRef.handleOpen()
    },
    handleOpenHistory() {
      this.$refs.historicalBatchRef.handleOpen()
    },
    releaseRule() {
      this.$refs[this.active].releaseRule()
    },
  },
  beforeDestroy() {
    if (this.interval) {
      clearInterval(this.interval)
    }
  },
}
</script>

<style lang="less" scoped>
.preApproval {
  width: 100%;
  height: 100%;
  padding: 20px;
  background-color: var(--bg-color);
  border: var(--decision-border);
  .btnList {
    width: 100%;
    display: flex;
    justify-content: space-between;
    margin-bottom: 17px;

    .operateBtn {
      display: flex;

      .step {
        width: 122px;
        font-size: 14px;
        color: #ff8f1f;
        background-color: rgba(#ff8f1f, 0.1);
        border: none;
        display: flex;
        justify-content: center;
        align-items: center;
      }

      .addLevel {
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: rgba(#ff8f1f, 0.1);
        color: #ff8f1f;
        font-size: 14px;
        font-family: 'PingFang SC Regular';
        border-radius: 6px;
        box-sizing: border-box;
        padding: 5px 10px;
        border: none;

        .icon {
          width: 20px;
          height: 20px;
          margin-right: 5px;
        }
      }

      .icon {
        width: 40px;
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 12px;
        background-color: var(--bg-color-lighter);
        cursor: pointer;
        margin-left: 10px;
        position: relative;
      }

      .redPoint {
        width: 12px;
        height: 12px;
        background-color: #ff0000;
        border-radius: 50%;
        position: absolute;
        top: 0;
        right: 0;
      }

      .el-button {
        border-radius: 12px;
      }
    }

    .btn {
      background: rgba(#000, 0.04);
      font-size: 14px;
      font-weight: normal;
      color: var(--text-color-secondary);
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 4px;
      margin-right: 16px;
      margin-left: 0px;
    }

    .btn:focus {
      box-shadow: none;
    }

    .active {
      background-color: var(--primary-color);
      color: #fff;
    }

    .disable {
      pointer-events: all;
      cursor: not-allowed;
    }
  }

  .content {
    height: calc(100% - 55px);
  }
}
</style>
