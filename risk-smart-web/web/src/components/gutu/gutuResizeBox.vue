<!--
 * @Author: Seastar 1507136388@qq.com
 * @Date: 2024-07-10 14:29:16
 * @LastEditTime: 2024-07-15 10:46:53
 * @LastEditors: Seastar 1507136388@qq.com
 * @Description: 
-->
<template>
  <div ref="resize" class="resize">
    <div ref="resizeHandle" class="handle-resize" />
    <slot />
  </div>
</template>
<script>
export default {
  name: 'ResizeBox',
  props: {
    resizeConf: {
      type: Object,
      default: () => ({
        width: 360, // 初始宽度
        widthRange: [360, 600], // 宽度范围
      }),
    },
  },
  mounted() {
    this.dragControllerDiv(this.$refs.resize, this.$refs.resizeHandle)
  },
  methods: {
    dragControllerDiv: function (resizeBox, resizeHandle) {
      resizeBox.style.width = this.resizeConf.width + 'px'
      resizeHandle.onmousedown = (e) => {
        const resizeWidth = resizeBox.offsetWidth
        const startX = e.clientX
        document.onmousemove = (ev) => {
          const moveX = ev.clientX
          const moveLen = resizeWidth + (moveX - startX)
          if (
            this.resizeConf.widthRange[0] <= moveLen &&
            this.resizeConf.widthRange[1] >= moveLen
          ) {
            resizeBox.style.width = moveLen + 'px'
          }
        }
        document.onmouseup = function () {
          document.onmousemove = null
          document.onmouseup = null
        }
      }
    },
  },
}
</script>
<style lang="less" scoped>
.resize {
  background: #fbfbfb;
  position: relative;
  word-wrap: break-word;
  border-right: 1px solid rgba(#000, 0.08);

  .handle-resize {
    position: absolute;
    cursor: col-resize;
    top: 0px;
    right: -7px;
    width: 15px;
    height: 100%;
  }
}
</style>
