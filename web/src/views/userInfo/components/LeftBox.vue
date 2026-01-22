<template>
  <div class="leftBox">
    <!-- 头像信息 -->
    <div class="head">
      <el-avatar
        shape="square"
        :size="100"
        :src="getUserInfo.avatar || avatarUrl[Number(getUserInfo.sex)]"
      />
      <span>{{ getUserInfo.nickName || '' }}</span>
      <span>
        {{
          enterpriseAuthStatus[enterpriseStatus]
            ? enterpriseAuthStatus[enterpriseStatus].label
            : '企业已认证'
        }}
      </span>
    </div>
    <!-- 用户信息列表 -->
    <div class="info">
      <ul>
        <li>
          <span>手机号码：</span>
          <span>{{ getUserInfo.phonenumber || '暂无' }}</span>
        </li>
        <li>
          <span>用户邮箱：</span>
          <span>{{ getUserInfo.email || '暂无' }}</span>
        </li>
        <li>
          <span>创建日期：</span>
          <span>{{ getUserInfo.createTime || '暂无' }}</span>
        </li>
      </ul>
    </div>
    <!-- 按钮 -->
    <div
      class="gt-btn"
      :class="{ 'is-disabled': hasButton('Supplementary:information:show') }"
    >
      <ul>
        <li @click="toView(0)">
          <span class="gt-btn-title">
            <gt-icon type="user" size="1.5" />
            个人信息
          </span>
          <el-tag size="small" :class="userStatus[getUserInfo.personAuthStatus]"
            >{{ userAuthStatus[getUserInfo.personAuthStatus] }}
          </el-tag>
        </li>
        <li @click="toView(1)">
          <span class="gt-btn-title">
            <gt-icon type="qiye" size="1.2" />
            企业信息
          </span>
          <el-tag
            size="small"
            :class="enterpriseAuthStatus[enterpriseStatus].class"
            v-if="enterpriseAuthStatus[enterpriseStatus]"
          >
            {{ enterpriseAuthStatus[enterpriseStatus].label }}
          </el-tag>
        </li>
      </ul>
    </div>
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
      default: () => {},
    },
  },
  data() {
    return {
      avatarUrl: [
        require('../../../public/images/nanAvatar.png'),
        require('../../../public/images/nvAvatar.png'),
      ], //头像默认图片
      userStatus: {
        0: 'gray',
        1: 'has',
      },
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
  computed: {
    getUserInfo() {
      return this.userInfo || {}
    },
  },
  mounted() {
    getAuthStatus({}).then((res) => {
      if (res.code === 200) {
        this.enterpriseStatus = res.data
      }
    })
  },
  watch: {
    '$route.query': {
      handler(cur) {
        if (cur) {
          this.$nextTick(() => {
            this.$emit('toView', Number(cur.type), this.enterpriseStatus)
          })
        }
      },
      deep: true,
      immediate: true,
    },
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
  overflow-y: auto;
  width: 20%;
  min-width: 200px;
  background-color: #ffffff;
  margin-right: 2vw;
  padding: 30px;

  .head {
    display: flex;
    flex-direction: column;
    align-items: center;
    font-family: PingFangSC-Medium, PingFang SC;

    > span:nth-of-type(2) {
      margin: 20px 0 8px 0;
      font-size: 16px;
      font-weight: 500;
      color: #3f4254;
      line-height: 22px;
    }

    > span:nth-of-type(3) {
      font-size: 14px;
      font-weight: 400;
      color: #b5b5c3;
      line-height: 20px;
    }
  }

  .info {
    margin: 20px 0 30px 0;

    > ul {
      > li {
        display: flex;
        justify-content: space-between;
        margin: 20px 0;

        > :nth-of-type(1) {
          font-size: 14px;
          font-weight: 400;
          color: #333333;
          line-height: 20px;
          flex-shrink: 0;
        }

        > :nth-of-type(2) {
          font-size: 14px;
          font-weight: 400;
          color: #b5b5c3;
          line-height: 20px;
          flex-shrink: 0;
        }
      }
    }
  }

  .gt-btn {
    > ul {
      margin: 0;
      padding: 0;

      > li {
        // height: 30px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 16px 20px;
        cursor: pointer;

        .gray {
          background-color: rgba(255, 59, 48, 0.1);
          color: gray;
        }

        .not {
          background-color: rgba(255, 59, 48, 0.1);
          color: #ff3b30;
        }

        .has {
          background-color: rgba(40, 136, 232, 0.09);
          color: #1677ff;
        }

        .aip {
          background-color: #ffece4;
          color: #ff9419;
        }

        > span {
          font-size: 14px;
          font-weight: 400;
          color: #333333;
          display: flex;
          align-items: center;
          flex-shrink: 0;
          border: none;

          > i {
            margin-right: 10px;
          }
        }
      }

      > li:hover {
        background: rgba(40, 136, 232, 0.04);
        border-radius: 6px;

        .gt-btn-title {
          color: var(--primary-color);
        }
      }
    }

    &.is-disabled {
      > ul {
        > li {
          cursor: not-allowed;

          &:hover {
            background: #ffffff;

            .gt-btn-title {
              color: #333333;
            }
          }
        }
      }
    }
  }
}
</style>
