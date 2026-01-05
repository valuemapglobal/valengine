<!--
 * @Author: Seastar 1507136388@qq.com
 * @Date: 2024-07-03 10:53:58
 * @LastEditTime: 2024-07-11 23:48:31
 * @LastEditors: Seastar 1507136388@qq.com
 * @Description: 
-->
<template>
  <div class="gutuProtocolRead" v-if="show" v-loading="loading">
    <div class="mask" v-if="loading"></div>
    <main @scroll="handleScroll">
      <template v-for="(item, index) in protocolList">
        <div :key="index">
          <iframe
            :ref="'iframe_' + index"
            :src="`${item}?index=${index}`"
            width="100%"
            frameborder="0"
          />
          <div class="connect" v-if="index != protocolList.length - 1">
            <span>继续滑动查看其他协议</span>
            <div class="line" />
          </div>
        </div>
      </template>
    </main>
    <footer>
      <el-button @click="handleClose">不同意</el-button>
      <el-button
        :disabled="!reachBottom || time != 0"
        type="primary"
        @click="handleClick"
      >
        {{
          reachBottom && time == 0
            ? '我已阅读并同意'
            : `请上滑阅读完全部协议${time > 0 ? '(' + time + ')' : ''}`
        }}
      </el-button>
    </footer>
  </div>
</template>

<script>
export default {
  props: {
    //协议网址
    protocolList: {
      type: Array,
      default: () => {
        return [
          'https://portal.valuemap.cn/aivisit-agreement/private_agreement_v1.0_20211201.html',
          'https://www.valuemap.cn/agreement/surveyAgreement.html',
        ]
      },
    },
  },
  data() {
    return {
      reachBottom: false,
      time: 10,
      loading: true,
      show: false,
      interval: null,
      scrollHeight: null,
      abc: null,
      viewHeight: 0,
    }
  },
  watch: {
    show: {
      handler(val) {
        if (val) this.start()
        else {
          if (this.interval) {
            this.loading = true
            clearInterval(this.interval)
            this.time = 10
          }
        }
      },
      immediate: true,
    },
  },
  mounted() {
    this.setVhUnit()
    window.addEventListener('resize', this.setVhUnit)
    window.addEventListener('message', this.receiveMessage)
  },
  methods: {
    setVhUnit() {
      let vh = window.innerHeight * 0.01
      document.documentElement.style.setProperty('--vh', `${vh}px`)
    },
    handleClick() {
      this.$emit('confirm')
      this.handleClose()
    },
    open() {
      this.show = true
    },
    handleClose() {
      this.show = false
    },
    start() {
      this.interval = setInterval(() => {
        this.time--
        if (!this.time) {
          clearInterval(this.interval)
        }
      }, 1000)
    },
    handleScroll(event) {
      const { scrollTop, clientHeight, scrollHeight } = event.target
      this.scrollHeight = scrollHeight
      this.abc = scrollTop + clientHeight
      if (scrollTop + clientHeight >= scrollHeight - 20) {
        this.reachBottom = true
      }
    },
    receiveMessage(event) {
      this.loading = true
      const { index, width, height } = event.data
      const iframe = this.$refs[`iframe_${index}`]
      if (iframe && iframe[0]) {
        setTimeout(() => {
          let gutuProtocolRead =
            document.getElementsByClassName('gutuProtocolRead')
          let clientWidth = gutuProtocolRead[0].clientWidth
          iframe[0].style.height = `${
            height * (clientWidth / width).toFixed(2)
          }px`

          this.loading = false
        }, 500)
      }
    },
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.setVhUnit)
  },
}
</script>

<style lang="less" scoped>
:root {
  --vh: 100%;
}
.gutuProtocolRead {
  width: 100%;
  height: calc(var(--vh, 1vh) * 100);
  position: absolute;
  top: 0;
  left: 0;
  z-index: 9999;
  overflow: hidden;
  background-color: #f3f3f3;

  .mask {
    position: absolute;
    background-color: #f3f3f3;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
  }

  main {
    width: 100%;
    height: calc(100% - 70px);
    overflow-y: auto;

    .connect {
      width: 100%;
      margin: 80px 0px;
      text-align: center;
      position: relative;

      > span {
        display: inline-block;
        padding: 5px 15px;
        background: #f3f3f3;
        color: rgba(#000, 0.6);
        font-size: 14px;
      }

      .line {
        position: absolute;
        top: 50%;
        left: 20px;
        width: calc(100% - 40px);
        border-top: 1px solid rgba(#000, 0.4);
        z-index: -1;
      }
    }
  }

  footer {
    width: 100%;
    padding: 8px 20px;
    background-color: #fff;
    display: flex;
    .el-button {
      height: 44px;
      font-size: 14px;
      border: none;
      border-radius: 12px;
      font-family: PingFang SC-Medium;
    }
    .el-button:first-child {
      background: rgba(#000, 0.04);
      width: 140px;
      color: rgba(#000, 0.3);
    }
    .el-button:last-child {
      flex: 1;
    }
  }
}
::-webkit-scrollbar {
  display: none;
}
</style>
