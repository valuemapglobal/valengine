<template>
  <div>
    <el-drawer
      :visible.sync="visible"
      direction="rtl"
      :title="drawerTitle"
      :before-close="resetFields"
      size="35%"
    >
      <div class="drawer">
        <div class="form-warp1">
          <div class="text">
            {{ $t('decisionManage.operator') }}：<span>{{
              dataList.operName ? dataList.operName : '-'
            }}</span>
          </div>
          <div class="text">
            {{ $t('decisionManage.userDept') }}：<span>{{
              dataList.deptName ? dataList.deptName : '-'
            }}</span>
          </div>
          <div class="text">
            {{ $t('decisionManage.operationType') }}：<span>{{
              getOperationTypeText(dataList.businessType)
            }}</span>
          </div>
          <div class="text">
            {{ $t('decisionManage.financialProduct') }}：<span>{{
              dataList.productName ? dataList.productName : '-'
            }}</span>
          </div>
          <div class="text">
            {{ $t('decisionManage.businessScene') }}：<span>{{
              dataList.businessCode ? mapText(dataList.businessCode) : '-'
            }}</span>
          </div>
          <div class="text">
            {{ $t('decisionManage.strategyModel') }}：<span>{{
              dataList.ruleCode ? moduleObj[dataList.ruleCode] : '-'
            }}</span>
          </div>
          <div class="text">
            {{ $t('decisionManage.operationTime') }}：<span>{{
              dataList.operTime ? dataList.operTime : '-'
            }}</span>
          </div>
          <div class="text" style="display: flex">
            <div style="flex-shrink: 0">
              {{ $t('decisionManage.operationContent') }}：
            </div>
            <div v-if="dataList.operParam">
              <json-viewer
                :value="dataList.operParam"
                :expand-depth="0"
                expanded
              ></json-viewer>
            </div>
            <div v-else>-</div>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import Headline from '@/components/Headline'
import { selectProduct } from './api'
export default {
  components: {
    Headline,
  },
  props: {
    title: {
      type: String,
      default: '',
    },
    info: {
      type: Object,
      default: {},
    },
    sceneList: {
      type: Array,
      default: [],
    },
  },
  data() {
    return {
      Item: {},
      resVisible: false,
      dataList: [],
      form: {},
      dictLabel: '',
      visible: false,
    }
  },
  computed: {
    moduleObj() {
      return {
        1: this.$t('decisionManage.scoreModel'),
        2: this.$t('decisionManage.ratingModel'),
        3: this.$t('decisionManage.limitModel'),
        4: this.$t('decisionManage.priceModel'),
        5: this.$t('decisionManage.ruleModel'),
        6: this.$t('decisionManage.classifyModel'),
      }
    },
    getOperationTypeText() {
      return function (businessType) {
        if (businessType === 0) {
          return this.$t('decisionManage.other')
        } else if (businessType === 1) {
          return this.$t('common.add')
        } else if (businessType === 2) {
          return this.$t('common.modify')
        } else if (businessType === 3) {
          return this.$t('common.delete')
        } else if (businessType === 12) {
          return this.$t('decisionManage.enableDisable')
        } else {
          return this.$t('decisionManage.unknown')
        }
      }.bind(this)
    },
    drawerTitle() {
      return this.title || this.$t('operlogManage.detail')
    },
  },
  methods: {
    mapText(code) {
      if (code) {
        return this.sceneList.filter((item) => item.dictValue == code)[0]
          .dictLabel
      } else {
        return '/'
      }
    },
    handlderRes(item) {
      this.Item = item
      this.resVisible = true
    },
    getDetail(row) {
      let data = {
        pageNum: 1,
        pageSize: 10,
        id: row.projectCode,
      }
      selectProduct({ ...data }).then((res) => {
        this.visible = true
        this.dataList = row
        if (res.data.list.length > 0) {
          this.dataList.productName = res.data.list[0].name
        }

        try {
          this.dataList.operParam = JSON.parse(row.operParam)
          console.log(this.dataList.operParam, 'this.dataList.operParam ')
        } catch (error) {
          console.log(error)

          this.dataList.operParam = null
        }
      })
    },
    resetFields() {
      this.visible = false
    },
  },
}
</script>

<style lang="less" scoped>
::v-deep .jv-container .jv-code {
  padding: 0px !important;
}

::v-deep .jv-container {
  background: var(--bg-color);
  .js-code,
  .js-node,
  .jv-push,
  .jv-key,
  .jv-value {
    color: var(--text-color) !important;
  }
}

.drawer {
  box-sizing: border-box;

  .form-warp1 {
    display: flex;
    flex-direction: column;
    // position: relative;
    .text {
      padding: 5px 0;
      flex: 1;
      color: var(--text-color-secondary);
      span {
        color: var(--text-color);
      }
      .blue {
        color: #00b578;
      }
      .red {
        color: #fa5151;
      }
    }
  }
}
</style>
