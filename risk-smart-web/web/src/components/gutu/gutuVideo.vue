<template>
  <div class="element_video">
    <div
      class="mask"
      v-if="visible"
    >
      <span
        class="videoBtn"
        @click="closeVideo"
      >
        <i class="el-icon-close"></i>
      </span>
      <video
        id="video"
        controls="controls"
        autoplay
        muted
        playsinline
        loop
        type="video/mp4"
        :src="url"
      ></video>
    </div>
  </div>
</template>

<script>
export default {
  name: 'elementVideo',
  props: {
    url: {
      type: String,
      default: '',
    },
  },
  watch: {
    url: {
      handler(val) {
        if (val) this.videoUrl = JSON.parse(JSON.stringify(val))
      },
    },
    visible: {
      handler(val) {
        if (val) {
          this.$nextTick(() => {
            let mainvideo = document.getElementById('video')
            // console.log(mainvideo, 'mainvideo');
            mainvideo.currentTime = 0
            mainvideo.play()
          })
        }
      },
      immediate: true,
    },
  },
  data() {
    return {
      visible: false,
      videoUrl: null,
    }
  },
  methods: {
    closeVideo() {
      this.videoUrl = null
      this.visible = false
    },
  },
}
</script>

<style lang="less" scoped>
.element_video {
  .mask {
    width: 100%;
    height: 100%;
    background-color: rgba(#000, 0.5);
    position: fixed;
    top: 0;
    left: 0;
    z-index: 999;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 60px;

    .videoBtn {
      width: 40px;
      height: 40px;
      position: absolute;
      top: 40px;
      right: 40px;
      border-radius: 50%;
      display: flex;
      justify-content: center;
      align-items: center;
      background-color: #606266;
      > i {
        color: #fff;
        font-size: 24px;
        cursor: pointer;
      }
    }

    > video {
      width: calc(100% - 200px);
      height: 100vh;
    }
  }
}
</style>
