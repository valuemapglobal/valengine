<script setup lang="ts">
import ProductList from './components/ProductList.vue'
import BusinessTabs from './components/BusinessTabs.vue'
</script>

<template>
  <div class="decision-platform">
    <div class="left-panel">
      <ProductList />
    </div>
    <div class="right-panel">
      <BusinessTabs />
    </div>
  </div>
</template>

<style lang="scss" scoped>
.decision-platform {
  display: flex;
  width: 100%;
  height: calc(100vh - 100px);
  padding: 20px;
  gap: 20px;

  .left-panel {
    flex-shrink: 0;
    width: 280px;
    height: 100%;
    background-color: #fff;
    border-radius: 8px;
    overflow: hidden;
  }

  .right-panel {
    flex: 1;
    height: 100%;
    overflow: hidden;
  }
}
</style>
