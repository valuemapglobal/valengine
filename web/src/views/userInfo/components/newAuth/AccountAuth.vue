<template>
  <div class="accountAuth">
    <div class="auth" v-if="currentComponent == null">
      <div class="authCard" v-for="(item, index) in authList" :key="index">
        <el-image :src="require(`@/assets/images/${item.icon}`)"></el-image>
        <div class="title">{{ item.label }}</div>

        <!-- <div v-if="item.status == 0" class="tagWarning">快速审核</div> -->
        <div :class="item.tag.class">
          {{ item.tag.label }}
        </div>

        <el-button class="bottomBtn" @click="handleClickAuth(item)">
          {{ item.status == 0 ? '立即认证' : '查看' }}
        </el-button>
      </div>
    </div>
    <component
      v-if="currentComponent"
      :is="currentComponent"
      @close="currentComponent = null"
      @submit="currentComponent = 'EditApproveInfo'"
      @to="currentComponent = 'ApproveInfo'"
      @paymentHandle="currentComponent = 'EditApproveInfo'"
    />
  </div>
</template>

<script>
import PersonalAuth from './PersonalAuth'
import EnterpriseAuth from './EnterpriseAuth'
import ApproveInfo from './ApproveInfo.vue'
import EditApproveInfo from './EditApproveInfo.vue'
import AuthManage from './AuthManage.vue'
import { mapGetters } from 'vuex'
export default {
  components: {
    PersonalAuth,
    EnterpriseAuth,
    ApproveInfo,
    EditApproveInfo,
    AuthManage,
  },
  data() {
    return {
      currentComponent: null,
      authList: [
        {
          label: '个人认证',
          field: 'personAuthStatus',
          icon: 'personalAuth.png',
          components: null,
          status: 0,
          tag: {
            label: null,
            class: null,
          },
          authStatus: {
            0: {
              class: 'tagBase',
              label: '未认证',
              components: 'PersonalAuth',
            },
            1: {
              class: 'tagSuccess',
              label: '已认证',
              components: 'PersonalAuth',
            },
          },
        },
        {
          label: '企业认证',
          field: 'enterpriseAuthStatus',
          icon: 'enterpriseAuth.png',
          components: null,
          status: 0,
          tag: {
            label: null,
            class: null,
          },
          authStatus: {
            0: { class: 'tagBase', label: '未认证', components: 'ApproveInfo' },
            1: {
              class: 'tagWarning',
              label: '认证中',
              components: 'AuthManage',
            },
            2: {
              class: 'tagSuccess',
              label: '已认证',
              components: 'AuthManage',
            },
            3: {
              class: 'tagError',
              label: '认证失败',
              components: 'AuthManage',
            },
          },
        },
      ],
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
  },
  watch: {
    userInfo: {
      handler(val) {
        if (val && Object.keys(val).length) {
          this.authList.forEach((item) => {
            if (val[item.field] != null) item.status = val[item.field]
            let find = item.authStatus[item.status]
            if (find) {
              item.tag = { ...find }
              item.components = find.components
            }
          })
        }
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    handleClickAuth(data) {
      this.currentComponent = data.components
    },
  },
}
</script>

<style lang="less" scoped>
.accountAuth {
  width: 100%;
  height: calc(var(--bgvh) - 140px);
  padding: 30px;
  font-family: PingFang SC-Regular;
  .auth {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;

    .authCard {
      position: relative;
      width: 274px;
      height: 420px;
      border: 1px solid rgba(#3662ec, 0.16);
      background-color: rgba(#3662ec, 0.04);
      border-radius: 12px;
      margin-right: 20px;
      display: flex;
      align-items: center;
      flex-direction: column;

      .el-image {
        width: 26px;
        height: 26px;
        margin-top: 25px;
      }

      .title {
        font-family: PingFang SC-Medium;
        color: rgba(#000, 0.85);
        font-size: 24px;
        font-weight: 500;
        margin-top: 16px;
      }

      .tagWarning,
      .tagSuccess,
      .tagBase,
      .tagError {
        padding: 4px 10px;
        font-size: 14px;
        margin-top: 20px;
        border-radius: 2px;
      }
      .tagBase {
        color: gray;
        background-color: rgba(255, 59, 48, 0.1);
      }
      .tagError {
        color: #ff3b30;
        background: rgba(#ff3b30, 0.1);
      }
      .tagWarning {
        color: #ff8f1f;
        background: rgba(#ff8f1f, 0.1);
      }
      .tagSuccess {
        color: var(--primary-color);
        background: var(--primary-color-lighter);
      }

      .bottomBtn {
        width: 82px;
        height: 36px;
        position: absolute;
        bottom: 60px;
        background-color: var(--primary-color);
        color: #fff;
        font-size: 14px;
        padding: 0px;
        display: flex;
        justify-content: center;
        align-items: center;
        border-radius: 3px;
      }
    }
  }
}

/* 修改垂直滚动条 */
::-webkit-scrollbar {
  width: 4px; /* 修改宽度 */
}

/* 修改滚动条轨道背景色 */
::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

/* 修改滚动条滑块颜色 */
::-webkit-scrollbar-thumb {
  background-color: #888;
}

/* 修改滚动条滑块悬停时的颜色 */
::-webkit-scrollbar-thumb:hover {
  background-color: #555;
}
</style>
