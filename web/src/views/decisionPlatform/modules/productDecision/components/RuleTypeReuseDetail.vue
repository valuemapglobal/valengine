<template>
  <!--规则模型&分类模型复用查看详情-->
  <div class="score-detail">
    <el-drawer
      :visible.sync="drawer.visible"
      :modal="false"
      :wrapperClosable="false"
      :before-close="handleClose"
      :size="drawer.size"
      :with-header="false"
    >
      <div class="body-header">
        <el-button type="primary" @click="drawer.visible = false">
          <img src="../../../images/transfer-icon.png" alt="" />
          复用页
        </el-button>
      </div>
      <div class="scroll-view">
        <template v-for="(cardItem, cardIndex) in cardList">
          <div class="module-item" :key="cardIndex" v-if="cardItem.show">
            <div class="module-item-header">{{ cardItem.cardTitle }}</div>
            <div class="module-item-wrapper" v-loading="cardItem.loading">
              <div
                class="module-item-card"
                :class="{ active: dataItem.id === cardItem.activeId }"
                v-for="(dataItem, dataIndex) in cardItem.dataList"
                :key="dataIndex"
                @click="handleModuleCard(dataItem, dataIndex, cardIndex)"
              >
                <div class="card-info">
                  <div class="module-item-card-name">
                    {{ dataItem.code || dataItem.name }}
                    <el-tag
                      :type="dataItem.deptFlag == 1 ? 'primary' : 'warning'"
                    >
                      {{ dataItem.deptFlag == 1 ? '默认' : '自建' }}
                    </el-tag>
                  </div>
                  <div class="module-item-card-other">
                    描述：{{ dataItem.descr || dataItem.name }}
                  </div>
                </div>
                <el-button
                  type="text"
                  @click.stop="openDrawer(dataItem, cardIndex)"
                  >查看详情</el-button
                >
              </div>
            </div>
          </div>
        </template>
      </div>
    </el-drawer>
    <el-drawer
      class="complex-drawer"
      :title="complexDrawer.title"
      :visible.sync="complexDrawer.visible"
      :modal="false"
      :wrapperClosable="false"
      :size="complexDrawer.size"
    >
      <AddRuleGroup
        v-if="complexDrawer.type === 'ruleGroup'"
        :status="false"
        :readOnly="true"
        :form-data="formData"
      />
      <AddOrEditRule
        v-if="complexDrawer.type === 'rule'"
        :status="false"
        :readOnly="true"
        :info="formData"
      />
    </el-drawer>
  </div>
</template>

<script>
import AddRuleGroup from '@/views/decisionPlatform/modules/productDecision/components/addRuleGroup'
import AddOrEditRule from '@/views/decisionPlatform/modules/productDecision/components/addOrEditRule'
import {
  searchCodeNewList,
  searchGroupNewList,
} from '@/views/decisionPlatform/modules/productDecision/api/riskModel'

export default {
  components: {
    AddRuleGroup,
    AddOrEditRule,
  },
  data() {
    return {
      drawer: {
        visible: false,
        size: '70%',
      },
      //标准列表-对应单卡片数据
      standardData: {},
      cardList: [
        {
          cardTitle: '规则组',
          dataSources: 'ruleGroup',
          activeId: undefined, //当前列选择行的id
          activeIndex: undefined, //当前列选择行在数组中下标
          loading: false,
          loadingMore: false, //正在获取更多内容……
          noMore: false, //没有更多了
          filterParams: {
            pageNum: 1,
            pageSize: 10,
          },
          total: 0, //为分页做贮备
          dataList: [],
          show: false,
        },
        {
          cardTitle: '规则',
          dataSources: 'rule',
          activeId: undefined, //当前列选择行的id
          activeIndex: undefined, //当前列选择行在数组中下标
          loading: false,
          loadingMore: false, //正在获取更多内容……
          noMore: false, //没有更多了
          filterParams: {
            pageNum: 1,
            pageSize: 10,
          },
          total: 0, //为分页做贮备
          dataList: [],
          show: true,
        },
      ],
      complexDrawer: {
        type: '',
        title: '',
        visible: false,
        size: '',
      },
      formData: {},
    }
  },
  watch: {
    standardData: {
      handler(cur) {
        if (!cur.groupId) {
          //	请求规则组列表数据&&请求规则列表数据
          if (cur.modelId) {
            this.cardList[0].show = true
            this.getRuleGroupList(true)
          }
        } else {
          //	请求规则列表数据
          this.cardList[0].show = false
          this.getRuleList(true)
        }
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    handleClose() {},
    /**
     * 获取规则组数据
     * @param isFirst 是否为首次加载
     */
    getRuleGroupList(isFirst) {
      if (isFirst) this.cardList[0].loading = true
      searchGroupNewList({
        ...this.cardList[0].filterParams,
        ...this.standardData,
      })
        .then((res) => {
          this.cardList[0].loading = false
          if (res.code == 200) {
            const { list, pages } = res.data
            const { pageNum } = this.cardList[1].filterParams
            this.cardList[0].dataList =
              pageNum === 1 ? list : this.cardList[0].dataList.concat(list)
            this.cardList[0].total = pages
            this.cardList[0].loadingMore = false
            if (pageNum >= this.cardList[0].total && this.cardList[0].total) {
              this.cardList[0].noMore = true
            }
            const { dataList, activeId } = this.cardList[0]
            setTimeout(() => {
              const parallelIndex = dataList.findIndex(
                (item) => item.id == activeId
              )
              if (parallelIndex === -1) {
                if (dataList.length) {
                  this.cardList[0].activeId = dataList[0].id
                  this.cardList[0].activeIndex = 0
                  this.getRuleList(true)
                }
              } else {
                this.cardList[0].activeId = dataList[parallelIndex].id
                this.cardList[0].activeIndex = parallelIndex
                this.getRuleList(true)
              }
            }, 200)
          }
        })
        .catch(() => {
          this.cardList[0].loading = false
          this.cardList[0].loadingMore = false
        })
    },
    /**
     * 获取规则列表数据
     * @param isFirst
     */
    getRuleList(isFirst) {
      if (isFirst) this.cardList[1].loading = true
      searchCodeNewList({
        ...this.cardList[1].filterParams,
        groupId: this.cardList[0].activeId,
        ...this.standardData,
      })
        .then((res) => {
          this.cardList[1].loading = false
          if (res.code == 200) {
            const { list, pages } = res.data
            const { pageNum } = this.cardList[1].filterParams
            this.cardList[1].dataList =
              pageNum === 1 ? list : this.cardList[1].dataList.concat(list)
            this.cardList[1].total = pages
            this.cardList[1].loadingMore = false
            if (pageNum >= this.cardList[1].total && this.cardList[1].total) {
              this.cardList[1].noMore = true
            }
          }
        })
        .catch(() => {
          this.cardList[1].loading = false
          this.cardList[1].loadingMore = false
        })
    },
    /**
     * 卡片点击，如果点击规则卡片则return
     * @param dataItem
     * @param dataIndex
     * @param cardIndex
     */
    handleModuleCard(dataItem, dataIndex, cardIndex) {
      if (this.cardList[cardIndex].activeId == dataItem.id || cardIndex === 1)
        return
      this.cardList[cardIndex].activeId = dataItem.id
      this.cardList[cardIndex].activeIndex = dataIndex
      this.cardList[cardIndex + 1].loadingMore = false
      this.cardList[cardIndex + 1].noMore = false
      this.cardList[cardIndex + 1].filterParams.pageNum = 1
      this.getRuleList(true)
    },
    /**
     * 规则组和规则--查看详情按钮
     * @param dataItem
     * @param cardIndex
     */
    openDrawer(dataItem, cardIndex) {
      this.complexDrawer.type = this.cardList[cardIndex].dataSources
      switch (this.cardList[cardIndex].dataSources) {
        case 'ruleGroup':
          this.complexDrawer.size = '40%'
          this.complexDrawer.title = `【${dataItem.name}】详情`
          this.formData = dataItem
          break
        case 'rule':
          this.complexDrawer.size = '1200px'
          this.complexDrawer.title = `【${dataItem.code}】详情`
          this.formData = dataItem
          break
      }
      this.complexDrawer.visible = true
    },
  },
}
</script>

<style lang="less" scoped>
.score-detail {
  ::v-deep .el-drawer__body {
    padding: 30px;

    .body-header {
      margin-bottom: 20px;

      .el-button {
        &--primary {
          font-size: 14px;

          &:not(:hover) {
            border-color: var(--primary-color);
            background: var(--primary-color);
          }

          > span {
            display: flex;
            align-items: center;
          }

          img {
            height: 12px;
            margin-right: 6px;
          }
        }
      }
    }

    .scroll-view {
      height: calc(var(--bgvh) - 114px);
      display: flex;
      overflow-x: auto;

      &::-webkit-scrollbar {
        height: 12px;
      }

      &::-webkit-scrollbar-thumb {
        border-radius: 6px;
        background-color: rgba(193, 193, 193, 1);
      }

      &::-webkit-scrollbar-track {
        border-radius: 6px;
        background-color: rgba(241, 241, 241, 1);
      }

      .module-item {
        padding: 0 20px;
        border-radius: 6px;
        background: rgba(0, 0, 0, 0.02);

        & + .module-item {
          margin-left: 20px;
        }

        &-header {
          padding: 20px 0;
          font-weight: 500;
          font-size: 18px;
          color: rgba(0, 0, 0, 0.85);
          line-height: 25px;
          border-bottom: 1px solid rgba(0, 0, 0, 0.08);
        }

        &-wrapper {
          min-width: 400px;
          height: calc(100% - 84px);
          overflow-x: hidden;
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

          .module-item-card {
            cursor: pointer;
            margin-top: 14px;
            width: 400px;
            padding: 14px 20px 14px 14px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            background: #ffffff;
            border-radius: 4px;

            &.active {
              background: rgba(88, 165, 241, 0.1);
            }

            &-name {
              font-weight: 500;
              font-size: 16px;
              color: rgba(0, 0, 0, 0.85);
              line-height: 24px;

              .el-tag {
                padding: 2px 6px;
                border: none;
                border-radius: 2px;
                font-size: 12px;
                line-height: 20px;

                &--primary {
                  color: var(--primary-color);
                  background: var(--primary-color-lighter);
                }

                &--warning {
                  color: #ff8f1f;
                  background: rgba(#ff8f1f, 0.1);
                }
              }
            }

            &-other {
              margin-top: 4px;
              font-weight: 400;
              font-size: 14px;
              color: rgba(0, 0, 0, 0.6);
              line-height: 22px;
            }
          }
        }
      }
    }
  }

  ::v-deep .complex-drawer {
    .el-drawer__body {
      padding: 0;
    }
  }
}
</style>
