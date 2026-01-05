<template>
  <div>
    <el-drawer
      :visible.sync="visible"
      direction="rtl"
      :title="title"
      :before-close="resetFields"
      size="35%"
    >
      <div class="drawer">
        <div class="form-warp1">
          <div class="text">
            操作用户：<span>{{
              dataList.operName ? dataList.operName : '-'
            }}</span>
          </div>
          <div class="text">
            用户部门：<span>{{
              dataList.deptName ? dataList.deptName : '-'
            }}</span>
          </div>
          <div class="text">
            操作类型：<span>{{
              getOperationTypeText(dataList.businessType)
            }}</span>
          </div>
          <div class="text">
            金融产品：<span>{{
              dataList.productName ? dataList.productName : '-'
            }}</span>
          </div>
          <div class="text">
            业务场景：<span>{{
              dataList.businessCode ? mapText(dataList.businessCode) : '-'
            }}</span>
          </div>
          <div class="text">
            策略模型：<span>{{
              dataList.ruleCode ? moduleObj[dataList.ruleCode] : '-'
            }}</span>
          </div>
          <div class="text">
            操作时间：<span>{{
              dataList.operTime ? dataList.operTime : '-'
            }}</span>
          </div>
          <div class="text" style="display: flex">
            <div style="flex-shrink: 0">操作内容：</div>
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
      default: '详情',
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
      moduleObj: {
        1: '评分模型',
        2: '评级模型',
        3: '额度模型',
        4: '定价模型',
        5: '规则模型',
        6: '分类模型',
      },
      Item: {},
      resVisible: false,
      dataList: [],
      form: {},
      dictLabel: '',
      visible: false,
    }
  },
  computed: {
    getOperationTypeText() {
      return function (businessType) {
        if (businessType === 0) {
          return '其他'
        } else if (businessType === 1) {
          return '新增'
        } else if (businessType === 2) {
          return '修改'
        } else if (businessType === 3) {
          return '删除'
        } else if (businessType === 12) {
          return '启停'
        } else {
          return '未知'
        }
      }
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
