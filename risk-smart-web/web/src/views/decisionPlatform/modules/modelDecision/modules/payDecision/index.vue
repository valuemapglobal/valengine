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
              disable: item.disabled,
            }"
            :disabled="item.disabled"
            :key="index"
          >
            {{ item.label }}
          </el-button>
        </template>
      </div>
      <div class="operateBtn">
        <el-button
          v-if="['classify'].includes(active)"
          @click="openTestDrawer"
          class="step"
          :class="{ pass: step >= 3 }"
          >测试通过：{{ step }} / 3
        </el-button>
        <el-button @click="releaseRule">确认发布</el-button>
      </div>
    </div>
    <div class="content">
      <components
        :is="active"
        :ref="active"
        :step.sync="step"
        :dueActive="dueActive"
      />
    </div>
  </div>
</template>

<script>
import rule from './rule.vue'
import classify from './classify.vue'
import tags from './tags.vue'
import { mapActions, mapMutations, mapState } from 'vuex'

export default {
  components: { rule, classify, tags },
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
  name: 'PayDecision',
  data() {
    return {
      btnList: [
        { label: '评分', template: '', disabled: true },
        { label: '评级', template: '', disabled: true },
        { label: '额度', template: '', disabled: true },
        { label: '定价', template: '', disabled: true },
        { label: '规则', template: 'rule', disabled: false },
        { label: '分类', template: 'classify', disabled: false },
        { label: '标签', template: 'tags', disabled: false },
      ],
      active: 'rule',
      step: 0,
    }
  },
  watch: {
    'dataRisk.config.ruleCode': {
      handler(val) {
        this.active = new Map([
          [0, 'rule'],
          [1, 'classify'],
          [2, 'tags'],
        ]).get(val)
      },
      deep: true,
      immediate: true,
    },
  },
  mounted() {
    console.log('支付流水尽调展示')
  },
  computed: {
    ...mapState(['dataRisk']),
  },
  methods: {
    ...mapActions(['changeModel']),
    clickActive(data) {
      this.active = data.template
      this.changeModel({ currentFun: data.template })
    },
    openTestDrawer() {
      this.$refs[this.active].openTestDrawer()
    },
    releaseRule() {
      this.$refs[this.active].releaseRule()
    },
  },
}
</script>

<style lang="less" scoped>
.preApproval {
  width: 100%;
  height: 100%;
  padding: 20px;
  background-color: #fff;

  .btnList {
    width: 100%;
    display: flex;
    justify-content: space-between;
    margin-bottom: 17px;

    .operateBtn {
      display: flex;

      /deep/ .el-button {
        width: 96px;
        height: 40px;
        background-color: #409eff;
        color: #fff;
      }

      .step {
        width: 122px;
        font-size: 14px;
        font-family: PingFang SC-Regular;
        color: #ff8f1f;
        background-color: rgba(#ff8f1f, 0.1);
        border: none;
        display: flex;
        justify-content: center;
        align-items: center;
      }

      .pass {
        background-color: rgba(#00b578, 0.1);
        color: #00b578;
      }
    }

    .btn {
      width: 68px;
      background: rgba(#000, 0.04);
      font-family: PingFang SC-Medium, PingFang SC;
      font-size: 14px;
      font-weight: normal;
      color: #000;
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
