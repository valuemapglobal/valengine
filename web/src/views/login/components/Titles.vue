<!--
 * @Date: 2022-04-21 18:23:21
 * @LastEditors: Seastar 1507136388@qq.com
 * @LastEditTime: 2024-01-26 01:24:42
 * @name: Title
 * @FilePath: \vm-micro-middleground\src\login\components\Titles.vue
-->
<template>
  <div>
    <h2 :class="`titles ${getType.type===9?'center':''}`">{{ getType.title }}</h2>
    <div class="text">
      {{ getType.type === 1 && type == 0 ? getType.text : "" }}
      <!-- <a href="#" v-if="getType.type === 1 && type == 0" @click="toRegister(1)"
        >去注册</a
      > -->
      <!-- <a href="#" v-if="getType.type === 2" @click="toRegister(0)">立即登录</a> -->
    </div>
  </div>
</template>

<script>
import { toRegister } from "./toRegister";

export default {
  name: "Title",
  mixins: [toRegister],

  props: {
    type: {
      type: Number,
    },
  },
  data() {
    return {};
  },
  computed: {
    getType() {
      switch (this.type) {
        case 0:
          return {
            type: 1,
            title: '验证码登录/注册',
            // title: process.env.VUE_APP_TITLE,
            text: "",
          };
        case 1:
          return {
            type: 1,
            title: '验证码登录/注册',
            // title: process.env.VUE_APP_TITLE,
            text: "",
          };
        case 2:
          return {
            type: 2,
            title: "免费注册",
            text: "已有账户？",
          };
        case 3:
          return {
            type: 2,
            title: "信息完善",
            text: "已有账户？",
          };
        case 4:
          return {
            type: 4,
            title: "忘记密码",
            text: "请输入手机号以重设您的账户密码",
          };
        case 5:
          return {
            type: 5,
            title: "设置新的密码",
            text: "请输入新的账户密码完成密码重置",
          };
        // case 9:
        //   return {
        //     type: 9,
        //     title: "微信扫码登录",
        //     text: "请输入新的账户密码完成密码重置",
        //   };
        default:
          return {
            type: -1,
            title: "",
            text: "",
          };
      }
    },
  },
  methods: {},
};
</script>
<style lang="less" scoped>
.titles {
  font-size: 30px;
  font-family: PingFang SC-Semibold, PingFang SC;
  font-weight: 600;
  color: #000000;
  line-height: 42px;
}
h2 {
  text-align: center;
}
.text {
  font-size: 16px;
  font-family: PingFang SC-Medium, PingFang SC;
  font-weight: 500;
  color: #b3b3c2;
  line-height: 22px;

  > a {
    color: #3699ff;
    text-decoration: none;
    margin-top: 6px;
  }
}
.center {
  text-align: center;
}
</style>
