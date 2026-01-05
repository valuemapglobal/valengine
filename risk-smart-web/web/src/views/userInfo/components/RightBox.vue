<template>
  <div class="rightBox">
    <FurtherInformation v-if="hasButton('Supplementary:information:show')" />
    <!-- 个人信息 -->
    <person-info v-else-if="show === 'person'" @back="show = 'editPerson'" />
    <!-- 个人信息实名认证 -->
    <edit-person-info
      v-else-if="show === 'editPerson'"
      @back="show = 'person'"
    />
    <!-- 企业认证 -->
    <approve-info
      ref="approve"
      v-else-if="show === 'approve'"
      @submit="show = 'editApprove'"
      @back="show = 'person'"
      @to="show = 'editApprove'"
    />
    <!-- 企业确认认证 -->
    <edit-approve-info
      v-else-if="show === 'editApprove'"
      @back="
        () => {
          $emit('reset')
          show = 'authManage'
        }
      "
    />
    <user-password
      v-else-if="show === 'userPassword'"
      @back="show = 'person'"
    />
    <AuthManage
      v-else-if="show === 'authManage'"
      @to="show = 'approve'"
      @paymentHandle="show = 'editApprove'"
    />
  </div>
</template>

<script>
import FurtherInformation from '@/views/userInfo/components/FurtherInformation'
import PersonInfo from './PersonInfo.vue'
import EditPersonInfo from './EditPersonInfo.vue'
import ApproveInfo from './ApproveInfo.vue'
import EditApproveInfo from './EditApproveInfo.vue'
import UserPassword from './UserPassword.vue'
import AuthManage from '@/views/userInfo/components/AuthManage'

export default {
  name: 'RightBox',
  components: {
    FurtherInformation,
    PersonInfo,
    EditPersonInfo,
    ApproveInfo,
    EditApproveInfo,
    UserPassword,
    AuthManage,
  },
  data() {
    return {
      show: 'person',
    }
  },
}
</script>

<style lang="less" scoped>
.rightBox {
  width: 80%;
  min-width: 1000px;
  border-radius: 0.375rem;
  background-color: #ffffff;
}
</style>
