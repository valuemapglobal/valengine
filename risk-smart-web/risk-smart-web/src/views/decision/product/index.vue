<script setup lang="ts">
import ProductList from './components/ProductList.vue'
import BusinessTabs from './components/BusinessTabs.vue'
import RuleStrategy from './modules/RuleStrategy.vue'
</script>

<template>
  <div class="product-decision-container">
      <div class="left-panel">
          <ProductList />
      </div>
      <div class="right-panel">
          <BusinessTabs>
              <!-- Content inside BusinessTabs slot -->
              <RuleStrategy />
          </BusinessTabs>
      </div>
  </div>
</template>

<style scoped lang="scss">
.product-decision-container {
    display: flex;
    height: calc(100vh - 84px); /* Adjust based on navbar height */
    padding: 20px;
    background: #f0f2f5;
    gap: 20px;
}

.left-panel {
    width: 280px;
    flex-shrink: 0;
    overflow: hidden;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
}

.right-panel {
    flex: 1;
    overflow: hidden;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
    background: #fff;
}
</style>
