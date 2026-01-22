<template>
  <router-view></router-view>
</template>

<script>
import { mapActions } from 'vuex'
export default {
  methods: {
    ...mapActions(['setAERTTL']),
  },
  created() {
    this.setAERTTL()
  },
}
</script>

<style></style>
