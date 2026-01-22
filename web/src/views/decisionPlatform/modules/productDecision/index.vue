<template>
  <div class="productDecision">
    <div class="left">
      <ProductList />
    </div>
    <div class="right">
      <BusinessTabs />
    </div>
  </div>
</template>

<script>
import ProductList from './productList.vue'
import BusinessTabs from './businessTabs.vue'
import { getDicts } from '@/api'

export default {
  name: 'productDecision',
  components: {
    ProductList,
    BusinessTabs,
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  mounted() {
    getDicts('decision_standard').then((res) => {
      const arr = res.data.map((item) => {
        return {
          dictLabel: item.dictLabel,
          dictValueList: JSON.parse(item.dictValue),
        }
      })
      let arr2 = []
      arr.forEach((item) => {
        arr2 = arr2.concat(item.dictValueList)
      })
      sessionStorage.setItem('decisionStandard', JSON.stringify(arr2))
    })
  },
  beforeDestroy() {},
  methods: {},
}
</script>

<style lang="less" scoped>
.productDecision {
  display: flex;
  min-width: 1200px;
  width: 100%;
  height: calc(var(--bgvh) - 42px);
  padding: 20px;
  // overflow: auto;

  .left {
    flex-shrink: 0;
    width: 280px;
    height: 100%;
    background-color: var(--bg-color);
    overflow: hidden;
    transition: width 0.2s linear;
    margin-right: 20px;
  }

  .full-screen {
    width: 74px;
  }

  .right {
    flex: auto;
    // margin-left: 20px;
    height: 100%;
    overflow: hidden;
  }
}
</style>
