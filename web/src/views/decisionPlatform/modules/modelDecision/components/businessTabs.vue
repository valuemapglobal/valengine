<template>
  <div style="height: 100%; display: flex; flex-direction: column">
    <ul class="tabs" v-if="dataList.length">
      <li
        v-for="(item, index) in tabs"
        :key="index"
        v-show="
          item.auth === 'baseInfo' ? true : item.show && hasButton(item.auth)
        "
        :style="`cursor:${item.disabled ? 'not-allowed' : 'pointer'}`"
        :class="active === item.template ? 'active' : ''"
        @click="tabsHandle(item.template, item.disabled || false)"
      >
        {{ item.name }}
      </li>
    </ul>
    <div class="content">
      <components :is="active" :dueActive="dueActive" :step="0" />
    </div>
  </div>
</template>

<script>
import payDecision from '@/views/decisionPlatform/modules/modelDecision/modules/payDecision/index.vue'
import { mapMutations, mapState } from 'vuex'
import preApproval from '../modules/preApproval/index.vue'

export default {
  name: 'RoBot',
  props: {
    /**
     * 0 银行流水尽调
     * 1 企业KYC尽调
     * 2 AI远程尽调
     * 3 企业司法尽调
     * 4 企业财务尽调
     * 5 个体KYC尽调
     * 6 支付流水尽调
     * 7 个体司法尽调
     */
    dueActive: { type: Number, default: 0 },
  },
  components: {
    preApproval,
    payDecision,
  },
  data() {
    return {
      dataList: [1],
      active: 'preApproval', //默认选中项
      tabs: [
        {
          name: '准入',
          template: 'admittance',
          auth: 'baseInfo',
          show: true,
          disabled: true,
        },
        {
          name: '反欺诈',
          auth: 'baseInfo',
          show: true,
          disabled: true,
        },
        {
          name: '预授信',
          template: 'preApproval',
          auth: 'baseInfo',
          show: true,
          disabled: false,
        },
        {
          name: '授信',
          auth: 'baseInfo',
          show: true,
          disabled: true,
        },
        {
          name: '放款（支取）',
          auth: 'baseInfo',
          show: true,
          disabled: true,
        },
        {
          name: '监测',
          auth: 'baseInfo',
          show: true,
          disabled: true,
        },
        {
          name: '贷后',
          auth: 'baseInfo',
          show: true,
          disabled: true,
        },
        {
          name: '催收',
          auth: 'baseInfo',
          show: true,
          disabled: true,
        },
      ],
      state: true,
    }
  },
  watch: {
    dueActive: {
      handler(newV) {
        let activeComponent
        if (newV === 0) {
          activeComponent = 'preApproval'
          this.SET_PRODUCTDECISION({
            currentProduct: 'yhls',
          })
          this.SET_CONFIG({
            projectCode: '1001',
            ruleCode: 0,
          })
        } else if (newV === 6) {
          activeComponent = 'payDecision'
          this.SET_PRODUCTDECISION({
            currentProduct: 'zfls',
          })
          this.SET_CONFIG({
            projectCode: '1002',
            ruleCode: 0,
          })
        }
        this.tabs[2].template = activeComponent
        this.active = activeComponent
      },
      deep: true,
      immediate: true,
    },
    '$route.query': {
      handler(newV) {
        if (newV.type) {
          this.active = newV.type
        } else {
          this.$store.commit('setBaseInfo', '')
        }
      },
      deep: true,
      immediate: true,
    },
    '$store.state.baseInfo.baseInfo': {
      handler(newV) {
        if (newV) {
          this.active = newV.type
        }
      },
      deep: true,
      immediate: true,
    },
  },
  computed: {
    ...mapState(['dataRisk']),
  },
  created() {
    if (this.$route.query.type) {
      this.active = this.$route.query.type
    }
  },
  methods: {
    ...mapMutations(['SET_PRODUCTDECISION', 'SET_CONFIG']),
    // 设置选中项
    tabsHandle(template, disabled) {
      if (disabled) return
      this.active = template
    },
  },
}
</script>

<style lang="less" scoped>
.tabs {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  background-color: #f2f3f7;
  margin: 0;
  padding: 0;

  > li {
    width: 124px;
    height: 60px;
    background: rgba(0, 0, 0, 0.04);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 4px;
    border-radius: 5px 5px 0 0;
    font-family: HarmonyOS_Sans_SC;
    font-size: 14px;
    font-weight: normal;
    line-height: 16px;
    letter-spacing: 0px;
    color: rgba(0, 0, 0, 0.85);
    cursor: pointer;
  }

  > .active {
    background: #ffffff;
    font-family: PingFangSC-Medium;
    font-size: 14px;
    font-weight: 500;
    line-height: 16px;
    letter-spacing: 0px;
    color: var(--primary-color);
  }
}

.content {
  //   padding: 20px;
  //   background-color: var(--primary-color);
  flex: auto;
  overflow: hidden;
  background-color: #ffffff;
}

.kb {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}
</style>
