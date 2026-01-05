<!--
 * @Date: 2022-04-22 10:30:36
 * @LastEditors: 大濕兄
 * @LastEditTime: 2022-08-30 18:17:15
 * @name: Authority 同意授权
 * @FilePath: /gutuProject/vmkj/src/login/components/Authority.vue
-->
<template>
  <div class="agree">
    <el-checkbox v-model="setState"></el-checkbox>
    <span>阅读并同意</span><a href="https://portal.valuemap.cn/sm-agreement/index.html">《用户协议》</a>和<a
      href="https://portal.valuemap.cn/aivisit-agreement/private_agreement_v1.0_20211201.html">《个人信息保护政策》</a>
  </div>
</template>

<script>
  export default {
    name: "Authority",
    model: {
      prop: "checked",
      event: "backChecked",
    },
    props: {
      checked: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        state: false,
      };
    },
    computed: {
      setState: {
        get() {
          return this.checked;
        },
        set(newV) {
          this.$emit("backChecked", newV);
        },
      },
    },
  };
</script>

<style lang="less" scoped>
  .agree {
    display: flex;
    align-items: center;
    font-size: 1rem;
    font-weight: 400;
    color: #3f4254;
    line-height: 22px;

    :deep .el-checkbox {
      margin-right: 10px;
    }

    a {
      color: #3699ff;
      flex-shrink: 0;
    }

    >span {
      flex-shrink: 0;
    }
  }
</style>