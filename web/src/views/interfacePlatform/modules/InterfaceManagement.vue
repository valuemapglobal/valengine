<template>
  <div class="smartDecision-warp">
    <div class="left">
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
            <div class="item" v-for="(item, index) in list" :key="index">
              <div
                :class="['name', item.current ? 'currentItem' : '']"
                @click="changeCurrent(item, index)"
              >
                <div
                  style="
                    width: 180px;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                  "
                >
                  {{ item.dataName }}
                </div>
                <div>
                  （{{ item.children ? item.children.length : 0 }}）
                  <i
                    :class="
                      item.current ? 'el-icon-arrow-down' : 'el-icon-arrow-up'
                    "
                  ></i>
                </div>
              </div>
              <div v-show="item.current">
                <div
                  v-for="(item1, index1) in item.children"
                  :key="index1"
                  :class="['children', item1.current ? 'currentChild' : '']"
                  @click="changeItem(index, index1, item1, item.children)"
                >
                  {{ item1.interfaceName }}
                </div>
              </div>
            </div>
          </div>
          <div
            v-if="noMore && !loadingMore"
            style="
              text-align: center;
              font-size: 12px;
              color: rgba(0, 0, 0, 0.85);
              margin-top: 20px;
            "
          >
            没有更多了...
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
      <InterfaceInfo
        :sourceNo="interfaceSourceNo"
        :itemInfo="itemInfo"
        @changeStatus="changeStatus"
      />
    </div>
  </div>
</template>
<script>
import InterfaceInfo from '../components/InterfaceInfo.vue'
import {
  findSourceInfo,
  findInterfaceInfo,
} from '@/views/interfacePlatform/api/dataList'
export default {
  components: {
    InterfaceInfo,
  },
  data() {
    return {
      loadingMore: false,
      noMore: false, //没有更多
      itemInfo: {}, // 当前点击子集数据
      interfaceSourceNo: '', // 供应商标识
      sceneIsEdit: false,
      loading: false,
      query: {
        pageNum: 1,
        pageSize: 15,
      },
      list: [],
    }
  },
  mounted() {
    this.getList('once')
  },
  methods: {
    // 获取更多数据
    loadMoreItems() {
      if (this.query.pageNum * 15 < this.total) {
        this.query.pageNum++
        this.getList('scroll')
      } else {
        this.noMore = true
        setTimeout(() => {
          this.noMore = false
        }, 2000)
      }
    },
    //滚动
    handleScroll() {
      const container = this.$refs.scrollContainer
      const scrollTop = container.scrollTop
      const scrollHeight = container.scrollHeight - 20
      const containerHeight = container.offsetHeight
      const scrollBottom = scrollTop + containerHeight
      if (scrollBottom >= scrollHeight) {
        this.loadMoreItems()
      }
    },
    changeStatus(item) {},
    handleChange(val) {},
    // 获取列表

    getList(type) {
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
        if (type) {
          let data = {
            sourceNo: this.interfaceSourceNo,
            // pageNum: 1,
            // pageSize: 10
          }
          this.list.map((item, index) => {
            findInterfaceInfo({
              sourceNo: this.list[index].interfaceSourceNo,
            }).then((res) => {
              if (res.data.list) {
                this.$set(this.list[index], 'children', res.data.list)
              } else {
                this.$set(this.list[index], 'children', [])
              }

              if (index == 0 && this.list[0].children[0]) {
                this.$set(this.list[0].children[0], 'current', true)
                this.itemInfo = this.list[0].children[0]
              }
            })
          })
        }

        this.loadingMore = false
        this.loading = false
      })
    },

    changeCurrent(item, index) {
      //    item.current=!item.current
      if (this.list[index].current) {
        this.list[index].current = false
        return
      }
      this.interfaceSourceNo = item.interfaceSourceNo
      this.list.map((f, i) => {
        this.$set(f, 'current', false)

        if (f.children) {
          f.children.map((c, k) => this.$set(c, 'current', false))
        }
        this.$set(this.list[index], 'current', true)
      })
      if (this.list[index].children) {
        this.$set(this.list[index].children[0], 'current', true)
        this.itemInfo = this.list[index].children[0]
      }
    },
    changeItem(pIndex, cIndex, cItem, list) {
      this.list[pIndex].children.map((item1) => {
        this.$set(item1, 'current', false)
      })
      this.itemInfo = cItem
      this.$set(this.list[pIndex].children[cIndex], 'current', true)
      cItem.current = true
    },
  },
}
</script>
<style lang="less" scoped>
.title {
  display: flex;
  justify-content: space-between;
  font-size: 18px;
  // font-family PingFang SC-Medium, PingFang SC;
  font-weight: 500;
  color: var(--text-color-secondary);
  line-height: 22px;
}

::v-deep .input-warp .el-input__inner {
  width: 240px;
  height: 40px;
  background: #f4f6f9;
  border: none;
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
    background: var(--bg-color);

    .title {
      display: flex;
      justify-content: space-between;
      padding: 20px;
      font-size: 18px;
      // font-family PingFang SC-Medium, PingFang SC;
      font-weight: 500;
      color: var(--text-color-secondary);
      line-height: 22px;
    }

    .left-content {
      height: 100%;

      .list {
        height: 100%;
        overflow: hidden;

        .item {
          margin-top: 10px;
          background: rgba(0, 0, 0, 0.04);
          border-radius: 4px 4px 4px 4px;
          box-sizing: border-box;

          .name {
            font-size: 16px;
            font-weight: 500;
            color: var(--text-color-secondary);
            height: 62px;
            background-color: var(--bg-color);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding-left: 30px;
            padding-right: 27px;
            box-sizing: border-box;
            cursor: pointer;
          }

          .children {
            height: 62px;
            background: rgba(0, 0, 0, 0.04);
            font-size: 16px;
            font-weight: 400;
            color: var(--text-color-tertiary);
            padding-left: 50px;
            padding-right: 20px;
            box-sizing: border-box;
            white-space: nowrap;
            text-overflow: ellipsis;
            overflow: hidden;
            line-height: 62px;
            cursor: pointer;
          }

          .currentChild {
            background: var(--primary-color-lighter);
            border-left: 4px solid #437bdd;
            color: #437bdd;
          }
        }

        .currentItem {
          color: #437bdd !important;
          background-color: #fff;
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
    width: calc(100% - 360px);
    height: 100%;
  }
}
</style>
