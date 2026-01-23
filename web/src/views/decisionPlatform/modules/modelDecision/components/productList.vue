<template>
  <div class="productList">
    <template v-for="(item, index) in dataList">
      <div class="product" :key="index">
        <el-button
          class="btn"
          @click="clickActive(index)"
          :class="
            activeIndex === index
              ? 'active'
              : item.operate === false
              ? 'disable'
              : ''
          "
          :disabled="item.operate === false"
        >
          {{ item.name }}
        </el-button>
      </div>
    </template>
  </div>
</template>

<script>
export default {
  name: 'productList',
  data() {
    return {
      dataList: [
        { name: '银行流水尽调', operate: true },
        { name: '企业KYC尽调', operate: false },
        { name: 'AI远程尽调', operate: false },
        { name: '企业司法尽调', operate: false },
        { name: '企业财务尽调', operate: false },
        { name: '个体KYC尽调', operate: false },
        { name: '支付流水尽调', operate: true },
        { name: '个体司法尽调', operate: false },
      ],
      activeIndex: 0,
    }
  },
  methods: {
    clickActive(index) {
      if (this.activeIndex === index) return
      this.activeIndex = index
      this.$emit('getDueActive', index)
    },
  },
}
</script>

<style lang="less" scoped>
.productList {
  width: 100%;
  padding: 20px;

  .product {
    position: relative;

    .icon {
      width: 40px;
      height: 48px;
      background-color: var(--primary-color);
      border-radius: 6px;
      position: absolute;
      left: -54px;
    }

    .btn {
      width: 100%;
      background: linear-gradient(90deg, #f7fafe 0%, #fff 100%);
      padding: 14px;
      font-size: 14px;
      color: rgba(#000, 0.85);
      height: 48px;
      display: flex;
      align-items: center;
      margin-bottom: 10px;
      border-radius: 6px;
      font-weight: 400;
    }

    .btn:focus {
      box-shadow: none;
    }

    .active {
      background: linear-gradient(
        90deg,
        var(--primary-color) 0%,
        var(--primary-color) 100%
      );
      color: #fff;
    }

    .disable {
      pointer-events: all;
      cursor: not-allowed;
    }
  }
}
</style>
