<template>
  <div style="position: relative">
    <el-drawer
      class="feed-back-drawer"
      :visible.sync="this.drawer"
      show-close
      :destroy-on-close="true"
      :hide-required-asterisk="false"
      :with-header="false"
      :modal="false"
      :wrapperClosable="false"
      size="40%"
    >
      <Headline @click="resetFields">{{
                $t('interfacePlatform.interfaceCallDetail')
            }}</Headline>

      <div class="list">
        <div class="item">
          <div
            class="lable"
            style="font-size: 18px;font-family: PingFang SC-Medium, PingFang SC;font-weight: 500;color: rgba(0,0,0,0.85);"
          >
            {{ $t('interfacePlatform.interfaceName') }}：</div>
          <div
            class="value"
            style="font-size: 18px;font-family: PingFang SC-Medium, PingFang SC;font-weight: 500;color: rgba(0,0,0,0.85);"
          >
            {{ info.interfaceName }}</div>
        </div>
        <div class="item">
          <div class="lable">{{ $t('interfacePlatform.userId') }}</div>
          <div
            class="value"
            style="font-size: 14px;font-family: PingFang SC-Medium, PingFang SC;font-weight: 500;color: rgba(0,0,0,0.85);"
          >{{ info.userId }}</div>
        </div>
        <div class="item">
          <div class="lable">{{ $t('interfacePlatform.userDept') }}：</div>
          <div
            class="value"
            style="font-size: 14px;font-family: PingFang SC-Medium, PingFang SC;font-weight: 500;color: rgba(0,0,0,0.85);"
          >{{ info.userDept }}</div>
        </div>
        <div class="item">
          <div class="lable">{{ $t('interfacePlatform.callTime') }}：</div>
          <div class="value">{{ info.accessTime }}</div>
        </div>
        <div class="item">
          <div class="lable">{{ $t('interfacePlatform.responseTime') }}：</div>
          <div class="value">{{ info.resultTime }}</div>
        </div>
        <div class="item">
          <div class="lable">{{ $t('interfacePlatform.ipAddress') }}：</div>
          <div class="value">{{ info.ip }}</div>
        </div>
        <div class="item">
          <div class="lable">{{ $t('interfacePlatform.callStatus') }}：</div>
          <div
            class="value"
            :style="{ color: info.code == 200 ? '#00B578' : 'red' }"
          >{{ info.code == 200 ? $t('interfacePlatform.success') :
                        $t('interfacePlatform.failure') }}</div>
        </div>
        <div class="item">
          <div class="lable">{{ $t('interfacePlatform.requestInput') }}</div>
          <div class="value">{{ info.param }}</div>
        </div>
        <div class="item">
          <div class="lable">{{ $t('interfacePlatform.responseResult') }}</div>
          <div class="value">{{ info.result }}</div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import Headline from "@/components/Headline";


export default {
  components: { Headline },

  data() {
    return {
      drawer: false,
      info: {}
    };
  },
  mounted() {
  },
  methods: {
    resetFields() {
      this.drawer = false
    },
    getInfo(row) {
      this.info = row
    }
  },
};
</script>
  
<style lang="less" scoped>
/deep/ .el-drawer {
  // user-select: none;
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.16);

  .el-drawer__body {
    padding: 30px 30px 70px;
  }
}

.list {
  margin-top: 30px;

  .item {
    display: flex;
    align-items: center;
    margin-bottom: 14px;

    .lable {
      width: 90px;
      text-align: end;
      font-size: 14px;
      font-family: PingFang SC-Regular, PingFang SC;
      font-weight: 400;
      color: rgba(0, 0, 0, 0.6);
      flex-shrink: 0;
    }

    .value {
      font-size: 14px;
      font-family: PingFang SC-Medium, PingFang SC;
      font-weight: 500;
      color: rgba(0, 0, 0, 0.85);
    }
  }
}
</style>