<template>
  <div class="box" ref="box">
    <!-- 左侧部分 -->
    <left-box :userInfo="userInfo" @toView="toView" />
    <!-- 右侧部分 -->
    <right-box ref="rightBoxRef" @reset="init()" />
    <!-- 认证确认弹窗 -->
    <el-dialog :visible.sync="dialogVisible">
      <div slot="title" class="title">请进行{{ infoOption.title }}</div>
      <img
        v-if="!infoOption.index"
        src="/images/person_approve.png"
        alt=""
        srcset=""
      />

      <img v-else src="/images/approve.png" alt="" srcset="" />
      <div class="text">
        <span>
          您当前账号未进行<span style="color: #4598ec">{{
            infoOption.title
          }}</span>
        </span>
        <span>请点击下方按钮</span>
      </div>
      <el-button type="primary" @click="certification">立即认证</el-button>
    </el-dialog>
  </div>
</template>

<script>
import { getLoginInfo } from '@/api/base/index.js'
import { mapGetters } from 'vuex'
import LeftBox from './components/newAuth/LeftBox.vue'
import RightBox from './components/newAuth/RightBox.vue'

export default {
  name: 'GtUserInfo',
  components: {
    LeftBox,
    RightBox,
  },
  data() {
    return {
      dialogVisible: false, //认证弹窗
      infoOption: {
        //显示当前信息下标
        index: 0,
        title: '个人认证',
      },
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
  },
  created() {
    this.init()
  },

  methods: {
    /**
     * @name: certification
     * @msg: 立即认证
     * @param {*}
     * @return {*}
     */
    certification() {
      if (this.infoOption.index === 0) {
        this.$refs.rightBoxRef.show = 'editPerson'
      }
      if (this.infoOption.index === 1) {
        this.$refs.rightBoxRef.$refs.approve.state = false
      }
      this.dialogVisible = false //关闭认证弹窗
    },
    /**
     * @name: toView
     * @msg: 跳转详细页面
     * @param {*} type//0个人信息,1企业信息,2修改密码
     * @param enterpriseStatus 企业认证状态
     * @return {*}
     */
    toView(type, enterpriseStatus) {
      switch (type) {
        case 0:
          this.infoOption = {
            index: 0,
            title: '个人认证',
          }
          if (this.userInfo && this.userInfo.personAuthStatus === '0') {
            this.dialogVisible = true //打开认证弹窗
          }

          this.$refs.rightBoxRef.show = 'person'
          break
        case 1:
          this.infoOption = {
            index: 1,
            title: '企业认证',
          }
          if (enterpriseStatus === 0) {
            this.dialogVisible = true //打开认证弹窗
            this.$refs.rightBoxRef.show = 'approve'
          } else {
            this.$refs.rightBoxRef.show = 'authManage'
          }
          break
        case 2:
          this.$refs.rightBoxRef.show = 'userPassword'
          break
      }
    },
    /**
     * @name: init
     * @msg: 初始化信息
     * @param {*}
     * @return {*}
     */
    init() {
      getLoginInfo()
        .then((res) => {
          // this.userInfo = res.user
          this.$store.commit('setUserInfo', res.user)
          this.$store.commit(
            'setUserAuthInfo',
            res.userAuthPersonFinancingExpertInfo
          )
        })
        .catch(() => {})
    },
  },
}
</script>

<style lang="less" scoped>
.box {
  height: calc(var(--bgvh) - 42px);
  width: 1440px;
  padding: 20px;
  display: flex;
  align-items: flex-start;
  margin: 0px auto;
}

:deep .el-dialog {
  border-radius: 6px;
  width: 472px;
  height: 361px;

  .title {
    text-align: center;
    font-size: 18px;
    font-weight: 500;
    color: #3f4254;
  }

  .el-dialog__body {
    display: flex;
    flex-direction: column;
    align-items: center;

    .text {
      margin-top: 1.25rem;
      display: flex;
      flex-direction: column;
      align-items: center;
    }

    button {
      margin-top: 1.875rem;
    }
  }
}
</style>
