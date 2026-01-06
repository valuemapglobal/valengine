<!--
 * @Date: 2022-04-24 13:48:33
 * @LastEditors: Seastar 1507136388@qq.com
 * @LastEditTime: 2024-07-11 11:27:45
 * @name: LeftBox 左侧部分
 * @FilePath: \vm-micro-middleground\src\userInfo\components\LeftBox.vue
-->
<template>
  <div class="leftBox" v-loading="loading">
    <template v-if="!loading">
      <div class="head">
        <div class="avatar">
          <el-image
            style="width: 100%; height: 100%"
            :src="
              userInfo && userInfo.avatar
                ? userInfo.avatar
                : avatarUrl[Number(userInfo.sex) || 0]
            "
            fit="cover"
          ></el-image>
        </div>
        <div class="userName">{{ userInfo.nickName || '' }}</div>
        <div class="userAuth">
          <div v-for="(item, index) in authList" :key="index">
            <span>{{ item.label }}</span>
            <div :class="item.tag.class">
              {{ item.tag.label }}
            </div>
          </div>
        </div>
      </div>
      <div class="info">
        <div class="infoRow" v-for="(item, index) in infoList" :key="index">
          <div class="label">{{ item.label }}：</div>
          <div class="content">{{ item.data || '暂无' }}</div>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import { getAuthStatus } from '@/api/base'
import GtIcon from '@/components/Icon'

export default {
  name: 'LeftBox',
  components: {
    GtIcon,
  },
  props: {
    userInfo: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  data() {
    return {
      loading: true,
      avatarUrl: ['/images/nanAvatar.png', '/images/nvAvatar.png'], //头像默认图片
      userStatus: {
        0: 'gray',
        1: 'has',
      },
      infoList: [
        { label: '手机号码', field: 'phonenumber', data: null },
        { label: '用户邮箱', field: 'email', data: null },
        { label: '创建日期', field: 'createTime', data: null },
      ],
      // authList: [
      //   {
      //     label: '个人认证',
      //     field: 'personAuthStatus',
      //     data: null,
      //   },
      //   {
      //     label: '企业认证',
      //     field: 'enterpriseAuthStatus',
      //     data: null,
      //   },
      // ],
      authList: [
        {
          label: '个人认证',
          field: 'personAuthStatus',
          data: null,
          tag: { label: null, class: null },
          authStatus: {
            0: {
              class: 'tagBase',
              label: '未认证',
            },
            1: {
              class: 'tagSuccess',
              label: '已认证',
            },
          },
        },
        {
          label: '企业认证',
          field: 'enterpriseAuthStatus',
          data: null,
          tag: { label: null, class: null },
          authStatus: {
            0: { class: 'tagBase', label: '未认证' },
            1: {
              class: 'tagWarning',
              label: '认证中',
            },
            2: {
              class: 'tagSuccess',
              label: '已认证',
            },
            3: {
              class: 'tagError',
              label: '认证失败',
            },
          },
        },
      ],
      userAuthStatus: {
        0: '未认证',
        1: '已认证',
      },
      enterpriseStatus: null,
      enterpriseAuthStatus: {
        0: { class: 'gray', label: '未认证' },
        1: { class: 'aip', label: '认证中' },
        2: { class: 'has', label: '已认证' },
        3: { class: 'not', label: '认证失败' },
      },
    }
  },
  computed: {},
  mounted() {
    getAuthStatus({}).then((res) => {
      if (res.code === 200) {
        this.enterpriseStatus = res.data
      }
    })
  },
  watch: {
    userInfo: {
      handler(val) {
        if (val != null && Object.keys(val).length) {
          this.loading = false
          this.infoList.forEach((item) => {
            if (val[item.field] != null) item.data = val[item.field]
          })
          this.authList.forEach((item) => {
            if (val[item.field] != null) item.data = val[item.field]
            let find = item.authStatus[item.data]
            if (find) item.tag = { ...find }
          })
        } else this.loading = true
      },
      deep: true,
      immediate: true,
    },
    // '$route.query': {
    //   handler(cur) {
    //     // if (cur) {
    //     //   this.$nextTick(() => {
    //     //     this.$emit('toView', Number(cur.type), this.enterpriseStatus)
    //     //   })
    //     // }
    //   },
    //   deep: true,
    //   immediate: true,
    // },
  },
  methods: {
    /**
     * @name: toView
     * @msg: 跳转详细页面
     * @param {*} type//0个人信息,1企业信息,2修改密码
     * @return {*}
     */
    toView(type) {
      if (this.hasButton('Supplementary:information:show')) return
      this.$emit('toView', type, this.enterpriseStatus)
    },
  },
}
</script>

<style lang="less" scoped>
.leftBox {
  border-radius: 6px;
  width: 280px;
  min-height: 360px;
  background-color: #ffffff;
  margin-right: 20px;
  padding: 20px;
  font-family: PingFang SC-Regular;

  .head {
    display: flex;
    flex-direction: column;
    align-items: center;

    .avatar {
      width: 108px;
      height: 108px;
      border-radius: 6px;
      border: 1px solid rgba(#000, 0.1);
      overflow: hidden;
    }

    .userName {
      color: #3f4254;
      margin-top: 20px;
      font-size: 16px;
      font-family: PingFang SC-Medium;
      font-weight: 500;
    }
    .userAuth {
      width: 100%;
      display: flex;
      flex-direction: column;
      margin-top: 15px;
      > div {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 4px 0px;

        .tagWarning,
        .tagSuccess,
        .tagBase,
        .tagError {
          padding: 4px 10px;
          font-size: 14px;
          border-radius: 4px;
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
      }
    }
  }

  .info {
    margin-top: 30px;
    .infoRow {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 14px;
      margin-bottom: 20px;

      .label {
        white-space: nowrap;
        color: #333;
      }
      .content {
        flex: 1;
        text-align: right;
        color: #b5b5c3;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
      }
    }
    .infoRow:last-child {
      margin-bottom: 0px;
    }
  }
}
</style>
