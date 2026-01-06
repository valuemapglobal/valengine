<template>
  <div class="productList">
    <div class="search">
      <div class="search-input">
        <img src="../../images/search.png" alt="search" />
        <el-input
          type="text"
          v-model="queryParams.name"
          placeholder="请输入"
          clearable
          @clear="search"
          @keyup.enter.native="search"
        />
      </div>
      <div class="add-product" @click="addBtnClick">
        <i class="el-icon-plus"></i>
      </div>
    </div>
    <main class="product_wrap" v-loading="loadingShow">
      <template v-for="(item, index) in dataList">
        <div class="product" :key="index">
          <div
            class="btn"
            @click="clickActive(item, index)"
            :class="{ active: activeIndex == index, disable: item.disabled }"
            :disabled="item.disabled"
          >
            <div class="btn-text">
              {{ item.productName }}
            </div>
            <div class="btn-action">
              <div class="edit-icon" @click.stop="editProductHandle(item)">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  xmlns:xlink="http://www.w3.org/1999/xlink"
                  fill="none"
                  version="1.1"
                  width="14"
                  height="14"
                  viewBox="0 0 14 14"
                >
                  <defs>
                    <mask
                      id="master_svg0_499_73682"
                      style="mask-type: alpha"
                      maskUnits="objectBoundingBox"
                    >
                      <g>
                        <rect
                          x="0"
                          y="0"
                          width="14"
                          height="14"
                          rx="0"
                          fill="#FFFFFF"
                          fill-opacity="1"
                        />
                      </g>
                    </mask>
                  </defs>
                  <g mask="url(#master_svg0_499_73682)">
                    <g>
                      <g>
                        <rect
                          x="0"
                          y="11.4579416513443"
                          width="5.939393997192383"
                          height="1.4848483800888062"
                          rx="0.7424241900444031"
                          :fill="activeIndex == index ? '#FFFFFF' : '#666666'"
                          fill-opacity="0.6000000238418579"
                        />
                      </g>
                      <g
                        transform="matrix(0.7071067690849304,-0.7071067690849304,0.7071067690849304,0.7071067690849304,-4.952927210694362,4.485733126849077)"
                      >
                        <path
                          d="M2.9382951259613037,8.22157871723175L12.140415125961304,8.22157871723175C12.648645125961304,8.22157871723175,13.060595125961303,8.633571717231751,13.060595125961303,9.14179171723175L13.060595125961303,12.82263871723175C13.060595125961303,13.33085871723175,12.648645125961304,13.742848717231752,12.140415125961304,13.742848717231752L2.9382951259613037,13.742848717231752L2.9382951259613037,8.22157871723175ZM12.140415125961304,12.82263871723175L3.8585081259613037,12.82263871723175L3.8585081259613037,9.14179071723175L12.140415125961304,9.14179171723175L12.140415125961304,12.82263871723175Z"
                          fill-rule="evenodd"
                          :fill="activeIndex == index ? '#FFFFFF' : '#666666'"
                          fill-opacity="1"
                        />
                      </g>
                      <g
                        transform="matrix(0.9789952635765076,-0.20388270914554596,0.20388270914554596,0.9789952635765076,-1.7102932122412131,0.6132693315595432)"
                      >
                        <path
                          d="M3.0453940056152344,12.738228289680482L2.1209873446152345,12.663628289680481L2.3099600056152343,11.755678289680482L2.7000310056152346,9.88150828968048L2.9652640056152344,8.607148289680481L3.678582005615234,9.695968289680481L5.179662005615235,11.987248289680482L5.818232005615235,12.96197828968048L4.656732005615234,12.86824828968048L3.0453940056152344,12.738228289680482Z"
                          fill-rule="evenodd"
                          :fill="activeIndex == index ? '#FFFFFF' : '#666666'"
                          fill-opacity="1"
                        />
                      </g>
                      <g
                        transform="matrix(0.9789952635765076,-0.20388270914554596,0.20388270914554596,0.9789952635765076,-1.9791168074042336,0.8780362788654337)"
                      >
                        <path
                          d="M3.271785020828247,11.685655307960511L4.764975020828247,11.79876530796051L3.613996020828247,10.04420530796051L3.271785020828247,11.685655307960511Z"
                          fill="#FFFFFF"
                          fill-opacity="1"
                        />
                      </g>
                    </g>
                  </g>
                </svg>
              </div>
              <div class="close-icon" @click.stop="deleteProductHandle(item)">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  xmlns:xlink="http://www.w3.org/1999/xlink"
                  fill="none"
                  version="1.1"
                  width="10"
                  height="10"
                  viewBox="0 0 10 10"
                >
                  <defs>
                    <mask
                      id="master_svg0_499_73690"
                      style="mask-type: alpha"
                      maskUnits="objectBoundingBox"
                    >
                      <g>
                        <rect
                          x="0"
                          y="0"
                          width="10"
                          height="10"
                          rx="0"
                          fill="#FFFFFF"
                          fill-opacity="1"
                        />
                      </g>
                    </mask>
                  </defs>
                  <g mask="url(#master_svg0_499_73690)">
                    <g>
                      <g
                        transform="matrix(0.6907609105110168,0.7230832576751709,-0.6907609105110168,0.7230831980705261,0.40679558681542716,-0.9511963012452895)"
                      >
                        <rect
                          x="1.3154727220535278"
                          y="0"
                          width="12.57242202758789"
                          height="1.2572423219680786"
                          rx="0.6286211609840393"
                          :fill="activeIndex == index ? '#FFFFFF' : '#666666'"
                          fill-opacity="1"
                        />
                      </g>
                      <g
                        transform="matrix(-0.6907609105110168,0.7230832576751709,0.6907609105110168,0.7230831980705261,14.68349814781908,-6.279652911767016)"
                      >
                        <rect
                          x="8.684550285339355"
                          y="0"
                          width="12.57242202758789"
                          height="1.2572423219680786"
                          rx="0.6286211609840393"
                          :fill="activeIndex == index ? '#FFFFFF' : '#666666'"
                          fill-opacity="1"
                        />
                      </g>
                    </g>
                  </g>
                </svg>
              </div>
            </div>
          </div>
        </div>
      </template>
      <div class="load_more" v-show="showLoadMore">
        <img
          src="@/assets/images/dataRisk/load_more.png"
          alt="load-more"
          class="rotate"
        />
        {{ '加载更多' }}
      </div>
    </main>
    <el-dialog
      :title="addOrEditProductTitle"
      :visible.sync="addVisible"
      width="26%"
    >
      <el-input placeholder="请输入产品名称" v-model.trim="nameValue" clearable>
      </el-input>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="addFormSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapActions, mapState } from 'vuex'
import {
  deleteProduct,
  editProduct,
  newlyAddProduct,
  product_search,
} from './api/index'
// import { handleCheckLock } from './utils/index.js'
export default {
  name: 'productList',
  props: {},
  data() {
    return {
      dataList: [],
      queryParams: {
        name: undefined,
        pageNum: 1,
        pageSize: 20,
      },
      total: 0,
      activeIndex: 0,
      showLoadMore: false,
      loadingShow: true, //列表加载
      addOrEditProductTitle: '',
      addVisible: false,
      nameValue: '',
      editId: undefined,
    }
  },
  computed: {
    ...mapState(['dataRisk']),
  },
  mounted() {
    this.dataList = []
    this.getProductList()
    this.loadMore()
  },
  methods: {
    ...mapActions(['changeProductDecision', 'getProList']),
    // 暂时注释 20260105
    // handleCheckLock,
    search() {
      this.dataList = []
      Object.assign(this.queryParams, { pageNum: 1 })
      this.getProductList()
    },
    addBtnClick() {
      //初始化表单
      this.addOrEditProductTitle = '新增'
      this.nameValue = ''
      this.addVisible = true
    },
    addFormSubmit() {
      const name = this.nameValue
      if (!name) {
        this.$message.warning('请输入产品名称')
      } else {
        this.addVisible = false
        let params = { name },
          API = newlyAddProduct
        if (this.addOrEditProductTitle === '编辑') {
          Object.assign(params, {
            id: this.editId,
          })
          API = editProduct
        }
        API(params).then((res) => {
          if (res.code === 200) {
            this.$message.success(`${this.addOrEditProductTitle}成功`)
            this.search()
          }
        })
      }
    },
    editProductHandle(item) {
      this.addOrEditProductTitle = '编辑'
      this.nameValue = item.productName
      this.editId = item.id
      this.addVisible = true
    },
    deleteProductHandle(item) {
      this.$confirm('是否确认删除?', '', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          deleteProduct(item.id).then((res) => {
            if (res.code === 200) {
              if (res.data) {
                this.$message({
                  type: 'success',
                  message: '删除成功!',
                })

                this.search()
              } else {
                this.$message({
                  type: 'warning',
                  message: res.msg,
                })
              }
            }
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除',
          })
        })
    },
    getProductList() {
      if (!this.showLoadMore) this.loadingShow = true
      product_search(this.queryParams)
        .then((res) => {
          this.loadingShow = false
          if (res.code == 200) {
            if (this.total == 0) {
              this.changeProductDecision({ projectCode: res.data.list[0].id })

              // 暂时注释 20260105
              // if (this.dataRisk.decision.ruleCode == 5) {
              //   this.handleCheckLock()
              // }
            }
            let list = res.data.list.map((item) => {
              return {
                ...item,
                productName: item.name,
              }
            })
            this.dataList = this.dataList.concat(list)
            this.getProList(this.dataList)
            this.total = res.data.total
            this.showLoadMore = false
          }
        })
        .catch((err) => {
          this.loadingShow = false
        })
    },
    loadMore() {
      //滚动加载更多
      const el = document.getElementsByClassName('product_wrap')[0]
      el.addEventListener('scroll', () => {
        if (this.isLoadMore) return
        if (el.scrollTop + el.clientHeight == el.scrollHeight) {
          let queryParams = this.queryParams
          if (queryParams.pageNum * queryParams.pageSize < this.total) {
            this.showLoadMore = true
            setTimeout(() => {
              queryParams.pageNum++
              this.getProductList()
            }, 0)
          }
        }
      })
    },
    clickActive(data, index) {
      if (this.activeIndex === index) return
      this.activeIndex = index
      this.changeProductDecision({ projectCode: data.id })

      // 暂时注释 20260105
      // if (this.dataRisk.decision.ruleCode == 5) {
      //   this.handleCheckLock()
      // }
    },
  },
}
</script>

<style lang="less" scoped>
.productList {
  width: 100%;
  height: 100%;

  .search {
    padding: 20px;
    display: flex;
    background: var(--bg-color);
    .search-input {
      flex: auto;
      height: 100%;
      position: relative;

      img {
        width: 14px;
        position: absolute;
        top: 50%;
        left: 16px;
        transform: translate(-50%, -50%);
      }

      ::v-deep .el-input--small .el-input__inner {
        width: 100%;
        height: 40px;
        line-height: 40px;
        border-radius: 6px;
        outline: none;
        padding-left: 32px;
        // border: none;
        font-size: 14px;
        background: var(--bg-color-lighter);
      }
    }

    .add-product {
      cursor: pointer;
      margin-left: 10px;
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--primary-color);
      border-radius: 6px;
      background: #f2f8fe;
    }
  }

  main {
    height: calc(100% - 80px);
    padding: 0 20px 20px;
    overflow-y: auto;

    &::-webkit-scrollbar {
      width: 8px;
    }

    &::-webkit-scrollbar-thumb {
      border-radius: 4px;
      background-color: rgba(193, 193, 193, 1);
    }

    &::-webkit-scrollbar-track {
      border-radius: 4px;
      background-color: rgba(241, 241, 241, 1);
    }
  }

  .product {
    position: relative;

    .icon {
      width: 40px;
      height: 48px;
      background-color: var(--primary-color);
      border-radius: 6px;
      position: absolute;
      left: -54px;
    }

    .btn {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 100%;
      background: var(--decision-product-list-bg-gradient);
      padding: 14px;
      font-size: 14px;
      color: var(--text-color-secondary);
      height: 48px;
      text-align: left;
      margin-bottom: 10px;
      border-radius: 6px;
      font-weight: 400;
      border: 1px solid var(--border-color);

      .btn-text {
        width: calc(100% - 56px);
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .btn-action {
        display: flex;
        align-items: center;

        .edit-icon,
        .close-icon {
          cursor: pointer;
          position: relative;
          opacity: 0;
          z-index: -100;
          // transition: opacity 0.3s;
        }

        .edit-icon {
          margin-right: 10px;
        }
      }

      &:hover {
        .btn-action {
          .edit-icon,
          .close-icon {
            opacity: 1;
            z-index: 6;
          }
        }
      }
    }

    .btn:focus {
      box-shadow: none;
    }

    .active {
      background: linear-gradient(
        90deg,
        var(--primary-color) 0%,
        var(--primary-color) 100%
      );
      color: #fff;
    }

    .disable {
      pointer-events: all;
      cursor: not-allowed;
    }
  }
}

.load_more {
  font-size: 16px;
  font-family: PingFangSC-Regular, PingFang SC;
  font-weight: 400;
  color: RGBA(85, 161, 237, 1);
  padding: 10px 0;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;

  img {
    width: 14px;
    height: 14px;
    margin-right: 4px;
  }

  .rotate {
    animation: rotate 1s linear infinite;
  }

  @keyframes rotate {
    0% {
      transform: rotate(0deg);
    }

    100% {
      transform: rotate(360deg);
    }
  }
}

.rotate {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

// ::v-deep .el-dialog {
//   border-radius: 6px;

//   .el-dialog__title {
//     font-size: 16px;
//   }

//   .el-input {
//     .el-input__inner {
//       height: 40px;
//       line-height: 40px;
//       border-radius: 6px;
//     }
//   }

//   .el-button {
//     padding: 14px 13px;
//     border: none;
//     border-radius: 4px;

//     &--default {
//       color: rgba(0, 0, 0, 0.6);
//       background: rgba(0, 0, 0, 0.04);
//     }

//     &--primary {
//       background: var(--primary-color);
//     }
//   }
// }
.dialog-footer {
  .el-button {
    height: 42px;
    padding: 0px 32px;
    border-radius: 4px;
  }
}
</style>
