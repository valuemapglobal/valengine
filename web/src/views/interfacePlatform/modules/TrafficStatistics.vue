<template>
  <div class="dataView">
    <div class="left">
      <div
        class="card"
        v-for="(item, index) in dataV"
        :key="index"
      >
        <div class="title">
          {{ item.title }}
        </div>
        <div class="text_box">
          <h6>{{ item.text1 }}</h6>
          <h6>{{ item.text2 }}</h6>
          <i
            v-show="item.icon == 1"
            class="el-icon-refresh"
          ></i>
        </div>
        <div class="text_foot">
          <div class="value">{{ item.value }}</div>
          <div class="dev">{{ item.dev }}</div>
        </div>
      </div>
    </div>
    <div class="right">
      <div class="title_box">
        <div class="title">{{ $t('interfacePlatform.totalInterfaceCallDistribution') }}</div>
        <i class="el-icon-refresh"></i>
      </div>
      <InterFace :info="info"></InterFace>
    </div>
  </div>
</template>

<script>
import InterFace from './interface.vue'
import { countLog } from '@/views/interfacePlatform/api/dataList'
export default {
  components: {
    InterFace
  },
  data() {
    return {
      info: {},
    };
  },
  computed: {
    dataV() {
      return [
        {
          title: this.$t('interfacePlatform.totalData'),
          text1: this.$t('interfacePlatform.updatedAt'),
          text2: "2023-08-15",
          value: "",
          dev: this.$t('interfacePlatform.pieces'),
          icon: 1,
        },
        {
          title: this.$t('interfacePlatform.avgResponseTime7days'),
          text1: "2023-08-08～",
          text2: "2023-08-15",
          value: '',
          dev: this.$t('interfacePlatform.seconds'),
          icon: 0,
        },
        {
          title: this.$t('interfacePlatform.totalCalls'),
          text1: this.$t('interfacePlatform.updatedAt'),
          text2: "2023-08-15",
          value: '',
          dev: this.$t('interfacePlatform.times'),
          icon: 1,
        },
        {
          title: this.$t('interfacePlatform.yesterdayCalls'),
          text1: this.$t('interfacePlatform.updatedAt'),
          text2: "2023-08-15",
          value: '',
          dev: this.$t('interfacePlatform.times'),
          icon: 0,
        },
        {
          title: this.$t('interfacePlatform.successCalls7days'),
          text1: "2023-08-08～",
          text2: "2023-08-15",
          value: '',
          dev: this.$t('interfacePlatform.times'),
          icon: 0,
        },
        {
          title: this.$t('interfacePlatform.failureCalls7days'),
          text1: "2023-08-08～",
          text2: "2023-08-15",
          value: '',
          dev: this.$t('interfacePlatform.times'),
          icon: 0,
        },
      ]
    },
  },
  mounted() {
    this.getCountLog()
  },
  methods: {
    getCountLog() {
      countLog().then((res) => {
        if (res.code == 200) {
          this.info = res.data
          this.dataV[0].value = res.data.interfaceTotal
          this.dataV[1].value = res.data.avgResponseTime7days
          this.dataV[2].value = res.data.total
          this.dataV[3].value = res.data.callsYesterday
          this.dataV[4].value = res.data.callsSuccess7days
          this.dataV[5].value = res.data.callsFailure7days
        }
      })
    }
  }
};
</script>

<style lang="less" scoped>
.dataView {
  width: 1920px;
  height: 886px;
  padding: 20px;
  padding-right: 20px;
  display: flex;
  background-color: #f2f3f7;
  .left {
    flex: 1;
    display: flex;
    flex-wrap: wrap;
    .card {
      width: 48%;
      padding: 20px;
      height: 256px;
      margin-right: 2%;
      border: none;
      background-color: white;
      .title {
        font-size: 18px;
        font-weight: 600;
      }
      .text_box {
        display: flex;
        padding-top: 10px;
        h6 {
          font-weight: 400;
          font-size: 16px;
          color: #9e9e9e;
        }
        .el-icon-refresh {
          color: #3662ec;
          margin-left: 5px;
          padding-top: 2px;
        }
      }
      .text_foot {
        display: flex;
        align-items: baseline;
        padding-top: 70px;
        .value {
          font-size: 62px;
          font-family: PingFang SC-Medium, PingFang SC;
          font-weight: 500;
          color: rgba(0, 0, 0, 0.85);
        }
        .dev {
          color: #515151;
          margin-bottom: 2px;
        }
      }
    }
  }
  .right {
    flex: 1;
    background-color: white;
    padding: 20px;
    margin-bottom: 25px;
    margin-top: 60px;
    .title_box {
      display: flex;
      .title {
        font-size: 18px;
        font-weight: 600;
      }
      .el-icon-refresh {
        color: #3662ec;
        margin-left: 5px;
        padding-top: 2px;
      }
    }
  }
}
</style>
