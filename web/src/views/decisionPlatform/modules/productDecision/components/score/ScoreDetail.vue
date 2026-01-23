<template>
  <!--评分模型复用查看详情-->
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
          <img src="../../../../images/transfer-icon.png" alt="" />
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
                    {{ dataItem.primaryIndex || dataItem.indexRule }}
                    <el-tag
                      :type="dataItem.deptFlag == 1 ? 'primary' : 'warning'"
                    >
                      {{ dataItem.deptFlag == 1 ? '默认' : '自建' }}
                    </el-tag>
                  </div>
                  <div class="module-item-card-other" v-if="cardIndex < 4">
                    权重：{{
                      `${dataItem.weight ? dataItem.weight + '%' : '-'}`
                    }}
                  </div>
                  <div class="module-item-card-other" v-else>
                    评分：{{ dataItem.score || '-' }}
                  </div>
                  <div class="module-item-card-other">
                    描述：{{ dataItem.description || '-' }}
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
      <IndicatorsAdd
        v-if="complexDrawer.type === 'indicatorsAdd'"
        :status="false"
        :readOnly="true"
        :score-card="cardData"
        :form-data="formData"
      />
      <RuleAdd
        v-if="complexDrawer.type === 'ruleAdd'"
        :status="false"
        :readOnly="true"
        :score-card="cardData"
        :form-data="formData"
      />
    </el-drawer>
  </div>
</template>

<script>
import IndicatorsAdd from '../score/indicatorsAdd'
import RuleAdd from '../score/ruleAdd'
import { getIndicatorsList, getRuleList } from '../../api/score'

export default {
  components: { IndicatorsAdd, RuleAdd },
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
          cardTitle: '一级指标',
          dataSources: 'firstIndicators',
          activeId: undefined, //当前列选择行的id
          activeIndex: undefined, //当前列选择行在数组中下标
          loading: false,
          total: 0, //为分页做贮备
          dataList: [],
          show: true,
        },
        {
          cardTitle: '二级指标',
          dataSources: 'secondIndicators',
          activeId: undefined, //当前列选择行的id
          activeIndex: undefined, //当前列选择行在数组中下标
          loading: false,
          total: 0, //为分页做贮备
          dataList: [],
          show: false,
        },
        {
          cardTitle: '三级指标',
          dataSources: 'thirdIndicators',
          activeId: undefined, //当前列选择行的id
          activeIndex: undefined, //当前列选择行在数组中下标
          loading: false,
          total: 0, //为分页做贮备
          dataList: [],
          show: false,
        },
        {
          cardTitle: '四级指标',
          dataSources: 'fourthIndicators',
          activeId: undefined, //当前列选择行的id
          activeIndex: undefined, //当前列选择行在数组中下标
          loading: false,
          total: 0, //为分页做贮备
          dataList: [],
          show: false,
        },
        {
          cardTitle: '指标规则',
          dataSources: 'indicatorRules',
          activeId: undefined, //当前列选择行的id
          activeIndex: undefined, //当前列选择行在数组中下标
          loading: false,
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
      lastIndicatorActiveId: undefined, //最后一个层级指标卡
      cardData: {},
      formData: {},
    }
  },
  watch: {
    standardData: {
      handler(cur) {
        if (cur.id) this.getIndicators(undefined, 1)
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    handleClose() {},
    /**
     * 获取层级指标数据（一级指标、二级指标、三级指标、四级指标）
     * @param parentCardId 父指标id
     * @param level 当前层级  1:一级指标 2:二级指标 3:三级指标 4:四级指标
     */
    getIndicators(parentCardId, level) {
      if (!level || level > 4) return
      this.cardList[level - 1].show = true
      this.cardList[level - 1].loading = true
      getIndicatorsList({
        scoreCardId: this.standardData.id, //scoreCardId  评分卡id
        parentCardId,
      })
        .then((res) => {
          this.cardList[level - 1].loading = false
          if (res.code === 200) {
            const { data } = res
            this.cardList[level - 1].dataList = data
            if (typeof this.cardList[level - 1].activeIndex !== 'number') {
              this.cardList[level - 1].activeIndex = 0
            }
            let { activeIndex, dataList } = this.cardList[level - 1]
            this.cardList[level - 1].activeId = dataList[activeIndex].id
            let { activeId } = this.cardList[level - 1]
            //priFlag指标卡是否存在下级指标卡  0——不存在，1——存在
            if (dataList[activeIndex].priFlag === 1) {
              this.getIndicators(activeId, level + 1)
            } else {
              //没有下级指标卡，则不展示下一级
              for (let i = level; i < 3; i++) {
                this.cardList[i].dataList = []
                this.cardList[i].show = false
                this.cardList[i].activeIndex = undefined
                this.cardList[i].activeId = undefined
              }
              // console.log('调用指标规则')
              //调用指标规则
              this.getRules(activeId)
            }
          }
        })
        .catch(() => {
          this.cardList[level - 1].loading = false
        })
    },
    /**
     * 获取指标规则列表
     * @param scorePrimaryId
     */
    getRules(scorePrimaryId) {
      if (!scorePrimaryId) return
      this.lastIndicatorActiveId = scorePrimaryId
      const ruleIndex = 4 //指标规则对应cardList中的下标
      this.cardList[ruleIndex].activeIndex = undefined
      this.cardList[ruleIndex].activeId = undefined
      this.cardList[ruleIndex].dataList = []
      this.cardList[ruleIndex].show = true
      this.cardList[ruleIndex].loading = true
      getRuleList({ scorePrimaryId })
        .then((res) => {
          this.cardList[ruleIndex].loading = false
          if (res.code === 200) {
            const { data } = res
            this.cardList[ruleIndex].dataList = data
            if (typeof this.cardList[ruleIndex].activeIndex !== 'number') {
              this.cardList[ruleIndex].activeIndex = 0
            }
            const { activeIndex, dataList } = this.cardList[ruleIndex]
            this.cardList[ruleIndex].activeId = dataList[activeIndex].id
          }
        })
        .catch(() => {
          this.cardList[ruleIndex].loading = false
        })
    },
    handleModuleCard(dataItem, dataIndex, cardIndex) {
      //点击指标规则卡片，直接return
      if (cardIndex > 3) return
      if (this.cardList[cardIndex].activeId === dataItem.id) return
      this.cardList[cardIndex].activeIndex = dataIndex
      this.cardList[cardIndex].activeId = dataItem.id
      if (dataItem.priFlag === 1) {
        this.getIndicators(this.cardList[cardIndex].activeId, cardIndex + 2)
      } else {
        for (let i = cardIndex + 1; i < 3; i++) {
          this.cardList[i].dataList = []
          this.cardList[i].show = false
          this.cardList[i].activeIndex = undefined
          this.cardList[i].activeId = undefined
        }
        //调用指标规则
        this.getRules(this.cardList[cardIndex].activeId)
      }
    },
    openDrawer(dataItem, cardIndex) {
      this.cardData = this.cardList[cardIndex]
      switch (this.cardList[cardIndex].dataSources) {
        case 'firstIndicators':
        case 'secondIndicators':
        case 'thirdIndicators':
        case 'fourthIndicators':
          this.complexDrawer.type = 'indicatorsAdd'
          this.complexDrawer.size = 'auto'
          this.complexDrawer.title = `【${dataItem.primaryIndex}】详情`
          this.formData = {
            ...dataItem,
            scoreCardId: this.standardData.id,
          }
          break
        case 'indicatorRules':
          this.complexDrawer.type = 'ruleAdd'
          this.complexDrawer.size = '60%'
          this.complexDrawer.title = `【${dataItem.indexRule}】详情`
          let scordPrimaryIds = []
          for (let i = 0; i < cardIndex - 1; i++) {
            if (this.cardList[i].show) {
              scordPrimaryIds.push(this.cardList[i].activeId)
            }
          }
          this.formData = {
            ...dataItem,
            scordCardId: this.standardData.id,
            scorePrimaryId: this.lastIndicatorActiveId,
            scordPrimaryIds: scordPrimaryIds.join(';'),
            default: true,
            forbid: true,
          }
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
