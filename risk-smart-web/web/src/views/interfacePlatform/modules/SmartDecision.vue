<template>
  <div class="smartDecision-warp">
    <div class="left">
      <div class="left-content">
        <div class="menu-title">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            xmlns:xlink="http://www.w3.org/1999/xlink"
            fill="none"
            version="1.1"
            width="12"
            height="12"
            viewBox="0 0 12 12"
          >
            <defs>
              <clipPath id="master_svg0_1_386">
                <rect x="12" y="0" width="12" height="12" rx="0" />
              </clipPath>
            </defs>
            <g
              transform="matrix(-1,0,0,1,24,0)"
              clip-path="url(#master_svg0_1_386)"
            >
              <g>
                <path
                  d="M22.411376171875,5.17040888671875L22.410416171875,5.16937888671875Q21.920866171874998,4.635188886718749,20.926156171875,3.59549888671875Q20.686096171875,2.23337888671875,20.532646171875,1.46613588671875Q20.331726171875,0.46160888671875,19.358816171875,0.46160888671875Q19.078116171875,0.46160888671875,18.801496171875,0.58921288671875Q18.059686171875,0.93350058671875,16.814236171875,1.54059188671875Q15.345996171875,1.36513888671875,14.603906171875,1.29134988671875Q14.002186171875,1.22665388671875,13.594548171875,1.6816828867187499Q13.224828171875,2.09253888671875,13.283499171875,2.63542888671875Q13.366420171875,3.41810888671875,13.544617171875,4.83021888671875Q12.936422971875,6.09854888671875,12.597440171875,6.84016888671875Q12.329647171875,7.42345888671875,12.589968171875,7.92974888671875Q12.842311171875,8.42032888671875,13.412997171875,8.53281888671875Q14.189186171875,8.68255888671875,15.624576171874999,8.92815888671875Q16.641766171875,9.89113888671875,17.233326171875,10.42576888671875Q17.624386171875,10.78078888671875,18.076056171875,10.78078888671875L18.076436171875,10.78078888671875Q18.723866171875,10.78078888671875,19.097136171875,10.12942888671875Q19.519946171875,9.38749888671875,20.182646171875,8.15783888671875Q21.399936171874998,7.49318888671875,22.102226171875,7.08668888671875Q22.682076171875,6.75151888671875,22.766536171875,6.19753888671875Q22.851816171875,5.64901888671875,22.411376171875,5.17040888671875ZM21.076916171875,5.94119888671875Q20.582746171875,5.41073888671875,19.727496171875,4.51818888671875Q19.679876171875,4.46849888671875,19.674946171875,4.46311888671875Q19.522506171875,4.3042188867187505,19.484016171875,4.07755888671875Q19.300486171875,3.00323888671875,19.124786171875,2.09824888671875Q18.409706171875,2.43490888671875,17.278336171875,2.98716888671875Q17.273696171875,2.98942888671875,17.273406171875,2.98957888671875Q17.069536171875,3.08843888671875,16.848866171875,3.05936888671875L16.838876171875,3.05804888671875L16.828946171875,3.05632888671875Q16.443386171874998,2.98963888671875,14.815656171875,2.82359888671875Q14.961796171875001,4.1753988867187495,15.062576171875,4.85011888671875L15.063486171875,4.856158886718751L15.064236171875,4.86220888671875Q15.091866171875001,5.08412888671875,14.994156171875,5.28385888671875Q14.538936171875001,6.21950888671875,14.115806171875,7.13634888671875Q15.369426171875,7.37503888671875,16.091986171875,7.48291888671875Q16.323556171875,7.51999888671875,16.492306171875,7.67990888671875L16.492226171875,7.67998888671875Q16.515626171875,7.70178888671875,16.573026171875,7.75594888671875Q17.457096171875,8.59011888671875,17.972196171874998,9.06419888671875Q18.414516171875,8.27663888671875,18.964066171875,7.24967888671875Q19.071986171875,7.05124888671875,19.267416171875,6.94496888671875L19.267446171875,6.94502888671875Q19.289526171875,6.93278888671875,19.340576171875,6.90483888671875Q20.444326171874998,6.30058888671875,21.076916171875,5.94119888671875ZM23.534876171875,10.78680888671875Q23.534876171875,10.47622888671875,23.315576171875,10.25575888671875L23.314576171875,10.25476888671875L21.299966171875,8.240108886718751Q21.079736171875,8.01990888671875,20.768426171875,8.01990888671875Q20.457086171874998,8.01990888671875,20.235196171875,8.24181888671875Q20.016926171875,8.46216888671875,20.016926171875,8.77217888671875Q20.016926171875,9.08302888671875,20.236866171875,9.30380888671875L22.252896171875,11.31980888671875Q22.473506171875,11.53870888671875,22.783626171875,11.53870888671875Q23.094376171875,11.53870888671875,23.314676171875,11.31840888671875Q23.534876171875,11.09820888671875,23.534876171875,10.78680888671875Z"
                  fill-rule="evenodd"
                  fill="#FFC300"
                  fill-opacity="1"
                />
              </g>
            </g>
          </svg>
          <span>接口模块主题</span>
          <el-select
            v-model="query.interfaceDataType"
            placeholder="请选择"
            @change="
              () => {
                query.pageNum = 1
                list = []
                getList()
              }
            "
          >
            <el-option
              v-for="item in interfaceDataOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </div>
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
              <div class="button">
                <img src="../image/edit.png" @click="edit(item)" />
                <img
                  src="../image/del.png"
                  @click="remove(item)"
                  style="margin-left: 10px"
                />
              </div>
            </div>
          </div>
          <div
            v-if="noMore && !loadingMore"
            style="
              text-align: center;
              font-size: 12px;
              color: rgba(0, 0, 0, 0.85);
              margin-top: 10px;
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
      <div class="btn">
        <div @click="showAddScene">
          <img src="../image/addOrange.png" class="img" />新增
        </div>
      </div>
    </div>
    <div class="right">
      <RightCompoent :sourceNo="interfaceSourceNo" />
    </div>
    <AddScene
      ref="AddScene"
      @getList="
        () => {
          query.pageNum = 1
          list = []
          getList()
        }
      "
      :isEdit="sceneIsEdit"
    />
  </div>
</template>
<script>
import RightCompoent from '../components/rightCompoent.vue'
import AddScene from '../components/AddScene.vue'
import {
  findSourceInfo,
  removeSourceInfo,
} from '@/views/interfacePlatform/api/dataList'
export default {
  components: {
    RightCompoent,
    AddScene,
  },
  data() {
    return {
      loadingMore: false,
      noMore: false, //没有更多
      interfaceSourceNo: null, // 供应商标识
      sceneIsEdit: false,
      loading: false,
      query: {
        pageNum: 1,
        pageSize: 15,
        interfaceDataType: 0, //默认选择元数据
      },
      // 接口模块主题 选项
      interfaceDataOptions: [
        { value: 0, label: '元数据' },
        { value: 1, label: '特征变量' },
        { value: 2, label: '分析指标' },
      ],
      list: [],
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    // 获取更多数据
    loadMoreItems() {
      if (this.query.pageNum * 15 < this.total) {
        this.query.pageNum++
        this.getList()
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
    remove(item) {
      this.$confirm(`是否确认删除 ${item.dataName}?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          removeSourceInfo({ sourceNo: item.interfaceSourceNo })
            .then((res) => {
              if (res.code == 200) {
                this.$message({
                  type: 'success',
                  message: '删除成功!',
                })
                this.query.pageNum = 1
                this.list = []
                this.getList()
              }
            })
            .catch(() => {})
        })
        .catch(() => {})
    },
    // 获取列表
    getList() {
      this.loading = true
      this.loadingMore = true
      const { interfaceDataType, ...queryData } = this.query
      findSourceInfo({
        ...queryData,
        interfaceDataType: [interfaceDataType],
      }).then((res) => {
        res.data.list.map((item) => {
          item.current = false
        })
        this.list.push(...res.data.list)
        if (this.list && this.list.length) {
          this.list[0].current = true
          this.interfaceSourceNo = this.list[0].interfaceSourceNo
        } else this.interfaceSourceNo = null
        this.total = res.data.total
        this.loading = false
        this.loadingMore = false
      })
    },
    // 编辑列表
    edit(row) {
      this.sceneIsEdit = true
      this.$refs.AddScene.drawer = true
      this.$refs.AddScene.getInfo(row)
    },
    showAddScene() {
      this.sceneIsEdit = false
      this.$refs.AddScene.drawer = true
      this.$refs.AddScene.getInfo({
        interfaceDataType: this.query.interfaceDataType,
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
.smartDecision-warp {
  width: 100%;
  height: calc(var(--bgvh) - 42px);
  // height: 100vh;
  display: flex;
  box-sizing: border-box;
  padding: 20px;
  overflow: hidden;

  .left {
    width: 340px;
    height: 100%;
    background: var(--bg-color);
    padding: 10px 0px;
    padding-right: 0px;
    box-sizing: border-box;
    border-radius: 4px;
    position: relative;
    // margin-top: 55px;
    // overflow-y: auto;

    .title {
      display: flex;
      justify-content: space-between;
      padding-right: 20px;
      font-size: 16px;
      // font-family PingFang SC-Medium, PingFang SC;
      font-weight: 500;
      color: rgba(0, 0, 0, 0.85);
      line-height: 22px;
    }

    .btn {
      position: absolute;
      width: calc(100% - 40px);
      margin: 0px 20px;
      bottom: 20px;

      > div {
        width: 100%;
        height: 40px;
        font-size: 14px;
        font-weight: 400;
        color: var(--primary-color);
        border: 1px var(--primary-color) dashed;
        border-radius: 5px;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .img {
        width: 20px;
        height: 20px;
        margin-right: 10px;
      }
    }

    .left-content {
      height: calc(100% - 100px);

      .menu-title {
        top: 0;
        position: sticky;
        display: flex;
        align-items: center;
        padding: 10px 20px 20px;
        font-weight: 500;
        font-size: 14px;
        color: #ff8f1f;
        line-height: 26px;
        background: var(--bg-color);

        > span {
          margin-left: 4px;
          white-space: nowrap;
        }

        ::v-deep .el-select {
          margin-left: 8px;
          width: 192px;

          .el-input__inner {
            height: 40px;
            border-radius: 6px;
          }
        }
      }

      .list {
        height: calc(100% - 70px);

        .item {
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
            font-weight: 500;
            color: var(--text-color-secondary);
            text-overflow: ellipsis;
            width: 180px;
            white-space: nowrap;
            overflow: hidden;
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
    background: var(--bg-color);
    width: calc(100% - 360px);
    // height: calc(100% - 42px);
    // margin-top: 55px;
  }
}
</style>
