<template>
  <div class="virtualScroll">
    <div
      class="wrapper"
      ref="wrapper"
      @scroll="onScroll"
    >
      <div
        class="background"
        :style="{height:`${total_height}px`}"
      ></div>
      <div
        class="list"
        ref="container"
      >
        <div
          v-for="(item,index) in runList"
          :class="['line']"
          :key="index"
        >
          <div
            class="rowData"
            :class="{'active':item.check}"
          >
            <el-checkbox v-model="item.check" />
            <div
              class="rowLabel"
              @click="handleClick(item)"
            >{{item.data}}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    cache_screens: { // 缓冲的屏幕数量
      type: Number,
      default: 1
    },
    rowHeight: {
      type: Number,
      default: 50
    },
    dataList: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      list: [],//存储处理过的源数据
      runList: [], // 运行时的列表
      total_height: 0, // 列表总高度
      maxNum: 0,// 一屏幕容纳的最大数量
      distance: 0, // 存储滚动的距离
      scroll_scale: [],
    }
  },
  mounted() { },
  watch: {},
  methods: {
    init() {
      this.$nextTick(() => {
        const containerHeight = parseInt(getComputedStyle(this.$refs.wrapper).height);
        this.maxNum = Math.ceil(containerHeight / this.rowHeight);
        this.handleSourceData()
        this.$refs.wrapper.scrollTop = 0
      })
    },
    handleClick(data) {
      this.list[data.index].check = !this.list[data.index].check
    },
    onScroll(e) {
      const distance = e.target.scrollTop;
      this.distance = distance;
      this.getRunData(distance);
    },
    getRunData(distance = null) {
      //滚动的总距离
      const scrollTop = distance ? distance : this.$refs.container.scrollTop;
      //在哪个范围内不执行滚动
      if (this.scroll_scale) {
        if (scrollTop > this.scroll_scale[0] && scrollTop < this.scroll_scale[1]) {
          return;
        }
      }
      //起始索引
      let start_index = this.getStartIndex(scrollTop);
      start_index = start_index < 0 ? 0 : start_index;
      //上屏索引
      let upper_start_index = start_index - this.maxNum * this.cache_screens;
      upper_start_index = upper_start_index < 0 ? 0 : upper_start_index;
      // 调整offset
      this.$refs.container.style.transform = `translate3d(0,${this.list[upper_start_index].top}px,0)`;
      //中间屏幕的元素
      const mid_list = this.list.slice(start_index, start_index + this.maxNum);
      // 上屏
      const upper_list = this.list.slice(upper_start_index, start_index);
      // 下屏元素
      let down_start_index = start_index + this.maxNum;
      down_start_index = down_start_index > this.list.length - 1 ? this.list.length : down_start_index;
      this.scroll_scale = [this.list[Math.floor(upper_start_index + this.maxNum / 2)].top, this.list[Math.ceil(start_index + this.maxNum / 2)].top];
      const down_list = this.list.slice(down_start_index, down_start_index + this.maxNum * this.cache_screens);
      this.runList = [...upper_list, ...mid_list, ...down_list];
    },
    getStartIndex(scrollTop) {
      let start = 0, end = this.list.length - 1;
      while (start < end) {
        const mid = Math.floor((start + end) / 2);
        const { top, height } = this.list[mid];
        if (scrollTop >= top && scrollTop < top + height) {
          start = mid;
          break;
        } else if (scrollTop >= top + height) {
          start = mid + 1;
        } else if (scrollTop < top) {
          end = mid - 1;
        }
      }
      return start;
    },

    handleSourceData() {
      if (this.dataList.length > 0) {
        let total_height = 0;
        let index = 0
        const list = this.dataList.map((data, index) => {
          const height = this.rowHeight;
          const ob = {
            index,
            height,
            top: total_height,
            ...data,
            check: false,
            index: index
          }
          index++
          total_height += height;
          return ob;
        })
        this.total_height = total_height; //  列表总高度
        this.list = list;
        console.log(this.list, 'list===============================');
        this.getRunData()
      }
    }
  }
}
</script>

<style lang="less" scoped>
.virtualScroll {
  width: 100%;
  height: 100%;
  position: relative;

  .wrapper {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    top: 0;
    overflow-y: scroll;
    .background {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      z-index: -1;
    }
    .list {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;

      .rowData {
        width: 100%;
        height: 36px;
        margin-bottom: 2px;
        display: flex;
        align-items: center;
        border-radius: 3px;
        padding: 6px 12px;
        cursor: pointer;
        /deep/.el-checkbox {
          .el-checkbox__inner {
            width: 16px;
            height: 16px;
          }
          .el-checkbox__label {
            font-size: 16px;
            font-family: PingFang SC-Regular, PingFang SC;
            font-weight: 400;
            color: rgba(0, 0, 0, 0.9);
          }
        }
        .rowLabel {
          width: calc(100% - 16px);
          padding-left: 4px;
        }
      }
      .active {
        background-color: #f2f3ff;
        color: #0052d9;
      }
    }
  }
}
::-webkit-scrollbar {
  width: 0 !important;
}
::-webkit-scrollbar {
  width: 0 !important;
  height: 0;
}
</style>