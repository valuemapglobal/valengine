<template>
  <div class="smartDecision-warp">
    <div class="left">
      <div class="title-warp">
        <div class="input-warp">
          <el-input
            v-model="query.sourceName"
            placeholder="请输入"
            suffix-icon="el-icon-search"
            style="width: 100%; height: 100%"
            clearable
            @clear="
              () => {
                this.list = []
                ;(this.query.pageNum = 1), (this.query.sourceName = null)
                this.getList()
              }
            "
          ></el-input>
        </div>
        <el-button
          type="primary"
          style="height: 40px"
          @click="
            () => {
              this.list = []
              ;(this.query.pageNum = 1), this.getList()
            }
          "
          >搜索</el-button
        >
      </div>
      <div class="left-content">
        <div class="list" v-if="list.length > 0">
          <div
            v-loading="loadingMore"
            element-loading-text="加载中"
            id="scroll-container"
            ref="scrollContainer"
            @scroll="handleScroll"
            style="height: 100%; overflow-y: auto"
          >
            <div
              :class="['item', item.current ? 'currentItem' : '']"
              @click="changeCurrent(item, index)"
              v-for="(item, index) in list"
              :key="index"
            >
              <div class="name">{{ item.dataName }}</div>
            </div>
            <div
              v-if="noMore && !loadingMore"
              style="
                height: 30px;
                text-align: center;
                font-size: 12px;
                color: rgba(0, 0, 0, 0.85);
                margin-top: 20px;
              "
            >
              没有更多了...
            </div>
          </div>
        </div>
        <div
          v-loading="loading"
          element-loading-text="加载中"
          class="empty"
          v-else
        >
          <img src="../image/empty.png" class="empty" />
          <div style="margin-top: 13px; color: #9e9e9e">暂无数据</div>
        </div>
      </div>
    </div>
    <div class="right">
      <RightCompoent :sourceNo="interfaceSourceNo" />
    </div>
  </div>
</template>
<script>
import RightCompoent from '../components/rightDuditing.vue'
import { findSourceInfo } from '@/views/interfacePlatform/api/dataList'
export default {
  components: {
    RightCompoent,
  },
  data() {
    return {
      loadingMore: false,
      noMore: false, //没有更多
      interfaceSourceNo: '', // 供应商标识
      sceneIsEdit: false,
      loading: false,
      query: {
        pageNum: 1,
        pageSize: 15,
        sourceName: null,
      },
      list: [],
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    // 获取更多数据
    loadMoreItems() {
      if (this.loadingMore) {
        return
      }
      if (this.query.pageNum * 15 < this.total) {
        this.query.pageNum++
        this.getList()
      } else {
        this.noMore = true
      }
    },
    //滚动
    handleScroll() {
      const container = this.$refs.scrollContainer
      const scrollTop = container.scrollTop
      const scrollHeight = container.scrollHeight - 20
      const containerHeight = container.offsetHeight
      const scrollBottom = scrollTop + containerHeight
      console.log('3333333', scrollBottom >= scrollHeight)
      if (scrollBottom >= scrollHeight) {
        this.loadMoreItems()
      }
    },

    // 获取列表

    getList() {
      this.loading = true
      this.loadingMore = true
      findSourceInfo(this.query).then((res) => {
        res.data.list.map((item) => {
          item.current = false
        })
        this.list.push(...res.data.list)
        if (this.list.length > 0) {
          this.interfaceSourceNo = this.list[0].interfaceSourceNo
          this.list[0].current = true
        }
        this.total = res.data.total
        this.loading = false
        this.loadingMore = false
      })
    },

    changeCurrent(item) {
      this.list.map((item1) => {
        item1.current = false
      })
      item.current = true

      this.interfaceSourceNo = item.interfaceSourceNo
    },
  },
}
</script>
<style lang="less" scoped>
::v-deep .input-warp .el-input__inner {
  width: 240px;
  height: 40px;
  background: var(--bg-color);
  padding-right: 56px;
}

.smartDecision-warp {
  width: 100%;
  height: calc(var(--bgvh) - 42px);
  display: flex;
  box-sizing: border-box;
  padding: 20px;
  overflow: hidden;

  .left {
    width: 340px;
    height: 100%;
    background-color: var(--bg-color);

    .title-warp {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 20px;
    }

    .title {
      display: flex;
      justify-content: space-between;

      font-size: 16px;
      // font-family PingFang SC-Medium, PingFang SC;
      font-weight: 500;
      color: var(--text-color-secondary);
      line-height: 22px;
    }

    .left-content {
      // margin-top: 20px;
      height: calc(100% - 80px);

      .list {
        height: 100%;

        .item {
          background-color: var(--bg-color);
          display: flex;
          align-items: center;
          padding: 0px 14px;
          height: 52px;
          justify-content: space-between;
          box-sizing: border-box;
          border-left: 4px solid transparent;
          cursor: pointer;

          .name {
            font-size: 16px;
            // font-family PingFang SC-Medium, PingFang SC;
            font-weight: 500;
            color: var(--text-color-secondary);
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .button {
            display: flex;
            align-items: center;

            img {
              width: 16px;
              height: 16px;
              cursor: pointer;
            }
          }
        }

        .currentItem {
          border-left: 4px solid #437bdd;
          background-color: #f7f9ff;
          .name {
            color: #437bdd !important;
          }
        }
      }

      .empty {
        height: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
        flex-direction: column;

        .empty {
          width: 99px;
          height: 97px;
        }
      }
    }
  }

  .right {
    margin-left: 20px;
    background: #fff;
    width: calc(100% - 360px);
    height: 100%;
  }
}
</style>
