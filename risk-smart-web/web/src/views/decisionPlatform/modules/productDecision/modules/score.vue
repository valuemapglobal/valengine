<template>
  <div class="preApproval_rule">
    <div
      class="tableCard"
      :class="cardList.length <= 3 ? 'default' : 'moreCard'"
    >
      <template v-for="(cardItem, cardIndex) in cardList">
        <div
          class="card"
          :key="cardIndex"
          :class="{ marginLeft: cardIndex != (cardList.length && 0) }"
          :element-loading-text="$t('decisionPlatform.dataLoading')"
          v-loading="
            mine[cardItem.dataSources]
              ? mine[cardItem.dataSources].loading
              : null
          "
        >
          <div class="top">
            <div
              class="title"
              :style="cardItem.dataSources === 'scoreCard' ? 'flex: 1;' : ''"
            >
              {{ $t(cardItem.cardTitleKey) }}
              <div
                class="search-button"
                v-if="cardItem.dataSources === 'scoreCard'"
              >
                <el-input
                  :class="{ isFocus: cardItem.isFocus }"
                  ref="searchInputRef"
                  v-model="cardItem.filterParams.scoreCard"
                  clearable
                  :placeholder="$t('decisionPlatform.inputPlaceholder')"
                  @clear="onsearch($event, cardIndex)"
                  @keyup.enter.native="onsearch($event, cardIndex)"
                  @focus="onFocus(cardItem.filterParams, cardIndex)"
                  @blur="onBlur(cardItem.filterParams, cardIndex)"
                >
                  <div
                    slot="prefix"
                    class="search-button-icon"
                    @click="$refs.searchInputRef[cardIndex].focus()"
                  >
                    <img
                      src="../../../images/search.svg"
                      :alt="$t('decisionPlatform.search')"
                    />
                  </div>
                </el-input>
              </div>
            </div>
            <div class="top_operate">
              <div
                class="delBtn"
                v-if="cardIndex != 1 && cardIndex == levelIndex"
                @click="handleDelLevel"
              >
                <span>{{ $t('decisionPlatform.delete') }}</span>
              </div>
              <div
                ref="copyButtonRef"
                v-if="
                  hasButton('strategy:reuse:show') &&
                  cardItem.dataSources === 'scoreCard' &&
                  !isStandardDept()
                "
                class="copyBtn"
                @click.stop="openDrawer('reuse', cardItem)"
              >
                <div class="operateBtn" v-if="!cardItem.isFocus">
                  <img
                    src="../../../images/copy-green.png"
                    :alt="$t('decisionPlatform.reuse')"
                  />
                  <span ref="copyButtonTextRef">{{
                    $t('decisionPlatform.reuse')
                  }}</span>
                </div>
                <img
                  v-else
                  src="../../../images/copy-green.png"
                  :alt="$t('decisionPlatform.reuse')"
                />
              </div>
              <div
                ref="addButtonRef"
                class="addBtn"
                v-if="hasButton('Newstrategy:addition:show')"
                @click="openDrawer(cardItem.dataSources, null, cardIndex)"
              >
                <div class="operateBtn" v-if="!cardItem.isFocus">
                  <img
                    src="@/assets/images/dataRisk/add.png"
                    :alt="$t('decisionPlatform.add')"
                  />
                  <span ref="addButtonTextRef">{{
                    $t('decisionPlatform.add')
                  }}</span>
                </div>
                <img
                  v-else
                  src="@/assets/images/dataRisk/add.png"
                  :alt="$t('decisionPlatform.add')"
                />
              </div>
            </div>
          </div>
          <div class="dataContent">
            <InfiniteLoading
              :loadKey="cardIndex"
              :loadingMore="cardItem.loadingMore"
              :noMore="cardItem.noMore"
              @load="viewLoad(cardItem.dataSources, cardIndex)"
            >
              <draggable
                v-if="cardItem.dataSources"
                v-model="mine[cardItem.dataSources].dataList"
                chosen-class="chosen"
                force-fallback="true"
                group="people"
                animation="500"
                :disabled="
                  noDraggable || !cardItem.hasOwnProperty('hasDraggable')
                "
                filter=".forbid"
                :move="onMove"
                @sort="handleRuleSort"
              >
                <transition-group>
                  <div
                    class="dataCard"
                    v-for="(item, index) in mine[cardItem.dataSources].dataList"
                    :key="index"
                    @click="handleClick(cardItem, item, cardIndex)"
                    :class="{
                      activeCard:
                        item.id == mine[cardItem.dataSources].activeId,
                      forbid: item.forbid,
                    }"
                  >
                    <div
                      class="detail"
                      :class="{
                        'sort-detail': cardItem.dataSources === 'scoreCard',
                      }"
                    >
                      <div
                        class="name"
                        :title="
                          item.scoreCard || item.primaryIndex || item.indexRule
                        "
                      >
                        <span
                          :style="{
                            'max-width':
                              ![0, 4].includes(cardIndex) && item.leafNode == 0
                                ? 'calc(100% - 121px)'
                                : 'calc(100% - 75px)',
                          }"
                        >
                          {{
                            item.scoreCard ||
                            item.primaryIndex ||
                            item.indexRule
                          }}
                        </span>
                        <el-tag
                          :type="item.deptFlag == 1 ? 'primary' : 'warning'"
                        >
                          {{
                            item.deptFlag == 1
                              ? $t('decisionPlatform.default')
                              : $t('decisionPlatform.selfBuilt')
                          }}
                        </el-tag>
                      </div>
                      <span
                        class="weight weight-inner"
                        v-if="
                          cardIndex != 0 && cardIndex != cardList.length - 1
                        "
                      >
                        {{ $t('decisionPlatform.weight') }}：
                        <el-tooltip
                          class="item"
                          effect="light"
                          :content="
                            mine[cardItem.dataSources].dataList.length ===
                              index + 1 || item.weight == 100
                              ? $t(
                                  'decisionPlatform.youCanChangeWeightBySettingOtherIndicators'
                                )
                              : $t('decisionPlatform.weight')
                          "
                          placement="top"
                        >
                          <el-input
                            @click.native.stop
                            :ref="`${cardItem.dataSources}Input${index}`"
                            v-model.number="item.weight"
                            @keyup.enter.native="
                              handleInput(
                                item.weight,
                                index,
                                mine[cardItem.dataSources].dataList,
                                cardItem.dataSources
                              )
                            "
                            :disabled="
                              mine[cardItem.dataSources].dataList.length ===
                              index + 1
                            "
                          >
                            <span slot="suffix">%</span>
                          </el-input>
                        </el-tooltip>
                      </span>
                      <span
                        class="weight"
                        v-if="cardIndex == cardList.length - 1"
                      >
                        {{ $t('decisionPlatform.score') }}：{{
                          item.score || '-'
                        }}
                      </span>
                      <div :class="cardIndex == 0 ? 'descr' : 'weight'">
                        {{ $t('decisionPlatform.description') }}：{{
                          item.description || '-'
                        }}
                      </div>
                      <div class="version-box" v-show="cardIndex === 0">
                        <el-tooltip
                          class="item"
                          effect="dark"
                          :content="
                            item.versionObj ? item.versionObj.newVersion : ''
                          "
                          placement="top"
                        >
                          <div>
                            {{ $t('decisionPlatform.currentVersion')
                            }}{{
                              item.versionObj ? item.versionObj.newVersion : ''
                            }}
                          </div>
                        </el-tooltip>
                        <el-tooltip
                          class="item"
                          effect="dark"
                          :content="
                            item.versionObj ? item.versionObj.userVersion : ''
                          "
                          placement="top"
                        >
                          <div style="white-space: nowrap">
                            {{ $t('decisionPlatform.usingVersion')
                            }}{{
                              item.versionObj ? item.versionObj.userVersion : ''
                            }}
                          </div>
                        </el-tooltip>
                        <!-- <el-popover placement="bottom-start" title="" width="288" trigger="click">
                          <div class="version-list">
                            <div class="version-item"  v-for="(item, index) in versionList" :key="index">
                              <div>{{ item }}</div>
                            </div>
                          </div>
                          <span slot="reference">CCZL-ZR-P-V1.0-20231221-01 <i
                              class="iconfont el-icon-caret-bottom"></i></span>
                        </el-popover> -->
                      </div>
                      <div
                        class="top-tag"
                        v-if="
                          cardIndex === 0 &&
                          item.versionObj &&
                          item.versionObj.championVersion == 1
                        "
                      >
                        <img
                          src="@/assets/images/dataRisk/champion-icon.png"
                          class="champion-icon"
                        />
                        {{ $t('decisionPlatform.abTestChampion') }}
                      </div>
                    </div>
                    <div class="operate">
                      <div
                        v-if="
                          item.deptFlag != 1 ||
                          (item.deptFlag == 1 && isEditShow(item.deptId)) ||
                          (!(cardIndex == cardList.length - 1 && index == 0) &&
                            (cardItem.dataSources === 'scoreCard' ||
                              (item.deptFlag != 1 &&
                                cardItem.dataSources !== 'scoreCard')))
                        "
                        class="operate_btnList"
                        @click.stop
                      >
                        <button
                          v-if="
                            hasButton('strategy:edit:show') &&
                            (item.deptFlag != 1 ||
                              (item.deptFlag == 1 && isEditShow(item.deptId)))
                          "
                          class="edit"
                          @click="
                            openDrawer(
                              cardItem.dataSources,
                              item,
                              cardIndex,
                              false
                            )
                          "
                        >
                          <img
                            class="operate_img"
                            src="@/assets/images/dataRisk/edit.png"
                          />
                          <span class="operate_span_edit">{{
                            $t('decisionPlatform.edit')
                          }}</span>
                        </button>
                        <button
                          class="delete"
                          @click="
                            handleFun(
                              mine[cardItem.dataSources],
                              'delFun',
                              item
                            )
                          "
                          v-if="
                            hasButton('Strategy:deletion:show') &&
                            !(cardIndex == cardList.length - 1 && index == 0) &&
                            (cardItem.dataSources === 'scoreCard' ||
                              (item.deptFlag != 1 &&
                                cardItem.dataSources !== 'scoreCard'))
                          "
                        >
                          <img
                            class="operate_img"
                            src="@/assets/images/dataRisk/del.png"
                          />
                          <span class="operate_span_del">{{
                            $t('decisionPlatform.delete')
                          }}</span>
                        </button>
                      </div>
                      <button
                        class="addLevel"
                        v-if="
                          hasButton('Newstrategy:addition:show') &&
                          ![0, 4].includes(cardIndex) &&
                          item.leafNode == 0 &&
                          item.deptFlag != 1
                        "
                        @click.stop="handleAddLevel"
                      >
                        <img
                          class="icon"
                          src="@/assets/images/dataRisk/add.png"
                        />
                        {{ $t('decisionPlatform.addScoreLevel') }}
                      </button>
                      <div @click.stop class="switch">
                        <div
                          class="test-button"
                          v-if="
                            hasButton('strategy:test:show') &&
                            cardItem.dataSources === 'scoreCard' &&
                            (item.deptFlag != 1 ||
                              (item.deptFlag == 1 && isEditShow(item.deptId)))
                          "
                          @click.stop="openDrawer('testModel', item)"
                        >
                          {{ $t('decisionPlatform.test') }}
                        </div>
                        <div
                          style="
                            position: relative;
                            display: flex;
                            align-items: center;
                          "
                        >
                          <el-switch
                            v-if="
                              hasButton('enable:disable:show') &&
                              (cardItem.dataSources === 'scoreCard' ||
                                (item.deptFlag != 1 &&
                                  cardItem.dataSources !== 'scoreCard'))
                            "
                            v-model="item.buttonState"
                            active-color="#D6D3D3"
                            inactive-color="var(--primary-color)"
                            :active-text="$t('decisionPlatform.disabled')"
                            :inactive-text="$t('decisionPlatform.enabled')"
                            :active-value="0"
                            :inactive-value="1"
                            @change="
                              handleFun(
                                mine[cardItem.dataSources],
                                'switchFun',
                                item
                              )
                            "
                          />
                        </div>
                      </div>
                    </div>
                  </div>
                </transition-group>
              </draggable>
            </InfiniteLoading>
          </div>
        </div>
      </template>
    </div>
    <el-drawer
      :title="drawer.title"
      :visible.sync="drawer.visible"
      :modal="false"
      :wrapperClosable="false"
      :before-close="handleClose"
      :size="drawer.size"
    >
      <score-add
        v-if="drawer.type == 'scoreAdd'"
        :scoreCard="scoreCard"
        :formData="scoreFrom"
        :versionControl="getVersionControl"
        @success="handleSuccess"
        @close="handleClose"
      />
      <indicators-add
        :versionControl="getVersionControl"
        :scoreCard="scoreCard"
        v-if="drawer.type == 'indicatorsAdd'"
        :formData="scoreFrom"
        :scordPrimaryIds="scordPrimaryIds"
        @success="handleSuccess()"
        @close="handleClose"
      />
      <rule-add
        v-if="drawer.type == 'ruleAdd'"
        :scoreCard="scoreCard"
        :versionControl="getVersionControl"
        :formData="scoreFrom"
        @success="handleSuccess"
        @close="handleClose"
      />
      <TestModel
        v-if="drawer.type === 'testModel'"
        ref="testModelRef"
        :paramsData="testParams"
      />
      <ScoreReuse
        v-if="drawer.type === 'reuse'"
        ref="reuseRef"
        :paramsData="reuseParams"
        @success="handleSuccess"
        @close="handleClose"
      />
    </el-drawer>
    <confirmDialog
      @update="update"
      ref="confirmDialog"
      @reserved="reserved"
      :disabled="disabledVersion"
      @cancel="closeDialog"
      :updateText="$t('decisionPlatform.confirmAndUpdateVersion')"
      :reservedText="$t('decisionPlatform.confirmAndReserveVersion')"
    >
      <div>
        <div>{{ dataText }}</div>
        <div>
          {{ $t('decisionPlatform.youCanChooseToUpdateVersionQuestion') }}
        </div>
      </div>
    </confirmDialog>
    <testABDrawer
      ref="testDrawer"
      :activeId="scoreCard.activeId"
      :versionControl="getVersionControl"
    ></testABDrawer>
  </div>
</template>

<script>
import Draggable from 'vuedraggable'
import ScoreAdd from '../components/score/ScoreAdd.vue'
import RuleAdd from '../components/score/ruleAdd.vue'
import IndicatorsAdd from '../components/score/indicatorsAdd.vue'
import TestModel from '../components/testModel.vue'
import ScoreReuse from '../components/scoreReuse.vue'
import { mapState } from 'vuex'
import confirmDialog from '../components/confirmDialog.vue'
import testABDrawer from '../components/testABDrawer.vue'
import InfiniteLoading from '@/components/InfiniteLoading'
import {
  getScoreList,
  deleteScore,
  updateScoreStatus,
  getIndicatorsList,
  getRuleList,
  deleteIndicatorsCard,
  deleteIndicators,
  deleteRules,
  releaseScore,
  updateIncreaseStatus,
  setRuleSort,
  updateStatusIndicators,
  versionControlFraud,
  versionReserveScore,
  updateIndicators,
  deleteParentCard,
} from '../api/score'
import { getVersion } from '../api/riskModel'
import { isEditShow, isStandardDept } from '../utils/index.js'
export default {
  components: {
    Draggable,
    ScoreAdd,
    RuleAdd,
    IndicatorsAdd,
    TestModel,
    ScoreReuse,
    confirmDialog,
    testABDrawer,
    InfiniteLoading,
  },
  props: {
    step: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      disabledVersion: false,
      mine: this,
      dataInfo: {},
      dataText: '',
      cardList: [
        {
          cardTitleKey: 'decisionPlatform.scoreCard',
          dataSources: 'scoreCard',
          loadingMore: false, //正在获取更多内容……
          noMore: false, //没有更多了
          filterParams: {
            pageNum: 1,
            pageSize: 10,
            scoreCard: undefined,
          },
          isFocus: false, //搜索框是否获取焦点
        },
        {
          cardTitleKey: 'decisionPlatform.firstLevelIndicator',
          dataSources: 'firstIndicators',
          loadingMore: false, //正在获取更多内容……
          noMore: false, //没有更多了
          filterParams: {
            pageNum: 1,
            pageSize: 10,
          },
          isFocus: false, //搜索框是否获取焦点
        },
        {
          cardTitleKey: 'decisionPlatform.indicatorRule',
          dataSources: 'indicatorRules',
          hasDraggable: true,
          loadingMore: false, //正在获取更多内容……
          noMore: false, //没有更多了
          filterParams: {
            pageNum: 1,
            pageSize: 10,
          },
          isFocus: false, //搜索框是否获取焦点
        },
      ],
      scoreFrom: {},
      scoreCard: {
        activeId: null, //当前列选择行的id
        activeIndex: null, //当前列选择行在数组中下标
        dataList: [], //当前列所有的数据
        loading: false,
        searchDataFun: 'getIndicators',
        delFun: 'delScoreCard',
        switchFun: 'handleScoreSwitch',
        total: 0,
        form: {},
      },
      //一级指标
      firstIndicators: {
        activeId: null, //当前列选择行的id
        dataList: [], //当前列所有的数据
        loading: false,
        searchDataFun: '',
        delFun: 'delIndicatorsCard',
        switchFun: 'handleIndicatorsSwitch',
        total: 0,
        loadingMore: false, //正在获取更多内容……
        noMore: false, //没有更多了
        filterParams: {
          pageNum: 1,
          pageSize: 10,
        },
        isFocus: false, //搜索框是否获取焦点
        form: {},
      },
      indicatorRules: {
        activeId: null, //当前列选择行的id
        dataList: [], //当前列所有的数据
        loading: false,
        searchDataFun: '',
        delFun: 'delRulesCard',
        switchFun: 'handleRuleSwitch',
        total: 0,
        loadingMore: false, //正在获取更多内容……
        noMore: false, //没有更多了
        filterParams: {
          pageNum: 1,
          pageSize: 10,
        },
        isFocus: false, //搜索框是否获取焦点
        form: {},
      },
      //二级指标
      secondIndicators: {
        activeId: null, //当前列选择行的id
        dataList: [], //当前列所有的数据
        loading: false,
        searchDataFun: '',
        delFun: 'delIndicatorsCard',
        switchFun: 'handleIndicatorsSwitch',
        total: 0,
        loadingMore: false, //正在获取更多内容……
        noMore: false, //没有更多了
        filterParams: {
          pageNum: 1,
          pageSize: 10,
        },
        isFocus: false, //搜索框是否获取焦点
        form: {},
      },
      //三级指标
      thirdIndicators: {
        activeId: null, //当前列选择行的id
        dataList: [], //当前列所有的数据
        loading: false,
        searchDataFun: '',
        delFun: 'delIndicatorsCard',
        switchFun: 'handleIndicatorsSwitch',
        total: 0,
        loadingMore: false, //正在获取更多内容……
        noMore: false, //没有更多了
        filterParams: {
          pageNum: 1,
          pageSize: 10,
        },
        isFocus: false, //搜索框是否获取焦点
        form: {},
      },
      //四级指标
      fourthIndicators: {
        activeId: null, //当前列选择行的id
        dataList: [], //当前列所有的数据
        loading: false,
        searchDataFun: '',
        delFun: 'delIndicatorsCard',
        switchFun: 'handleIndicatorsSwitch',
        total: 0,
        loadingMore: false, //正在获取更多内容……
        noMore: false, //没有更多了
        filterParams: {
          pageNum: 1,
          pageSize: 10,
        },
        isFocus: false, //搜索框是否获取焦点
        form: {},
      },
      noDraggable: false,
      lastParentCardId: null,
      scordPrimaryIds: [],
      levelMap: new Map([
        [
          1,
          {
            titleKey: 'decisionPlatform.firstLevelIndicator',
            dataSources: 'firstIndicators',
          },
        ],
        [
          2,
          {
            titleKey: 'decisionPlatform.secondLevelIndicator',
            dataSources: 'secondIndicators',
          },
        ],
        [
          3,
          {
            titleKey: 'decisionPlatform.thirdLevelIndicator',
            dataSources: 'thirdIndicators',
          },
        ],
        [
          4,
          {
            titleKey: 'decisionPlatform.fourthLevelIndicator',
            dataSources: 'fourthIndicators',
          },
        ],
      ]),
      levelIndex: 1, //当前选中数据有多少级指标
      addLevel: {
        cardTitleKey: null,
        dataSources: null,
        type: 'add',
      },
      decision: {},
      drawer: {
        title: '',
        visible: false,
        size: '30%',
        type: '',
        status: true,
      },
      //策略- 测试-请求参数
      testParams: {},
      //复用弹窗-请求参数
      reuseParams: {},
    }
  },
  computed: {
    ...mapState(['dataRisk']),
    getVersionControl() {
      let obj = this.scoreCard.dataList.find(
        (item) => item.id === this.scoreCard.activeId
      )
      if (obj) {
        return obj.versionObj ? obj.versionObj.newVersion : undefined
      }
    },
    mapProObj() {
      let findObj = this.dataRisk.productList.find(
        (item) => item.id === this.dataRisk.decision.projectCode
      )
      return findObj
    },
  },
  watch: {
    'dataRisk.decision': {
      handler(val) {
        this.decision = val
        //切换产品时，初始化搜索和滚动加载参数
        const defaultFilterParams = {
          pageNum: 1,
          pageSize: 10,
          scoreCard: undefined,
        }
        this.cardList = this.cardList.map((item, index) => {
          this.onBlur(defaultFilterParams, index)
          return {
            ...item,
            loadingMore: false, //正在获取更多内容……
            noMore: false, //没有更多了
            filterParams: defaultFilterParams,
            isFocus: false, //搜索框是否获取焦点
          }
        })
        if (val.projectCode) {
          this.scoreCard.activeId = null
          this.resetCardList()
          this.getFirstList(true)
        }
      },
      deep: true,
      immediate: true,
    },
    levelIndex: {
      handler(val) {
        this.$parent.addLevelStatus = val == 3 ? true : false
      },
    },
  },
  methods: {
    isEditShow,
    isStandardDept,
    mapType(customerType) {
      // let type = null
      // switch (customerType) {
      // 	case 0:
      // 		type = 'C、P';
      // 		break
      // 	case 1:
      // 		type = 'P';
      // 		break
      // 	case 2:
      // 		type = 'C';
      // 		break
      // }
      return 'C'
    },
    // 关闭
    closeDialog() {
      /*if (this.dataInfo.type === 2 || this.dataInfo.type === 4) {
       if (this.dataInfo.buttonState === 1) {
       this.dataInfo.buttonState = 0
       } else {
       this.dataInfo.buttonState = 1
       }
       }*/
    },
    // 获取更新||保留版本参数
    getqueryData() {
      let obj = this.scoreCard.dataList.find(
        (item) => item.id === this.scoreCard.activeId
      )
      console.log('4444', this.scoreCard.dataList, this.scoreCard.activeId)
      return {
        modelId: this.scoreCard.activeId,
        ...this.dataRisk.decision,
        modelName: obj.name,
        projectName: this.mapProObj.productName,
        personOrCompany: this.mapType(this.mapProObj.customerType),
        championVersion: obj.versionObj.championVersion,
        versionControl: this.getVersionControl,
      }
    },
    // 更新版本按钮点击
    async update() {
      this.disabledVersion = true
      const res = await versionControlFraud(this.getqueryData())
      if (res.code == 200) {
        releaseScore({
          ...this.decision,
          versionControl: this.getVersionControl,
          id: this.scoreCard.activeId,
        })
          .then((res2) => {
            if (res2.code == 200) {
              this.$refs.confirmDialog.visible = false
              this.disabledVersion = false
              this.$message.success(this.$t('decisionPlatform.releaseSuccess'))
              this.getFirstList(true)
            }
          })
          .catch((err) => {})
      }
    },
    // 保留原版本按钮点击
    async reserved() {
      this.disabledVersion = true
      /*const res = await versionReserveScore({ ...this.getqueryData(), versionControl: this.getVersionControl })
       if (res.code == 200) {*/
      releaseScore({
        ...this.decision,
        versionControl: this.getVersionControl,
        id: this.scoreCard.activeId,
      })
        .then((res2) => {
          if (res2.code == 200) {
            this.$refs.confirmDialog.visible = false
            this.disabledVersion = false
            this.$message.success(this.$t('decisionPlatform.releaseSuccess'))
            this.getFirstList(true)
          }
        })
        .catch((err) => {})
      /*}*/
    },
    init() {},
    openABTestDrawer() {
      this.$refs.testDrawer.visible = true
    },
    /**
     * 获取评分卡列表数据
     * @param isFirst 是否为首次加载
     * @param clearActive 是清除this.scoreCard.activeId值
     */
    getFirstList(isFirst, clearActive) {
      if (isFirst) this.scoreCard.loading = true
      this.levelIndex = 1
      this.cardList = this.cardList.filter(
        (item) => !item.hasOwnProperty('type')
      )
      getScoreList({ ...this.decision, ...this.cardList[0].filterParams })
        .then((res) => {
          if (res.code == 200) {
            const { list, pages } = res.data
            list.forEach((item) => {
              if (
                isStandardDept() ||
                (!isStandardDept() && item.deptFlag == 2)
              ) {
                return getVersion({
                  modelId: item.id,
                  ruleCode: this.decision.ruleCode,
                }).then((res2) => {
                  item.versionObj = res2.data
                })
              } else {
                item.versionObj = {
                  newVersion: item.versionControl,
                  userVersion: item.versionControl,
                }
              }
            })
            this.scoreCard.dataList =
              this.cardList[0].filterParams.pageNum === 1
                ? list
                : this.scoreCard.dataList.concat(list)
            this.scoreCard.total = pages
            this.cardList[0].loadingMore = false
            if (!this.scoreCard.total) {
              this.cardList[0].noMore = false
            }
            if (
              this.cardList[0].filterParams.pageNum >= this.scoreCard.total &&
              this.scoreCard.total
            ) {
              this.cardList[0].noMore = true
            }
            if (!this.scoreCard.activeId && this.scoreCard.dataList.length) {
              this.scoreCard.activeId = this.scoreCard.dataList[0].id
            } else {
              this.scoreCard.activeId = clearActive
                ? undefined
                : this.scoreCard.activeId
            }
            this.cardList = this.cardList.filter(
              (item) => !item.hasOwnProperty('type')
            )
            if (this.scoreCard.activeId)
              this.getIndicators(this.scoreCard.activeId, null, 1)
          }
          this.scoreCard.loading = false
        })
        .catch((err) => {
          this.scoreCard.loading = false
          this.cardList[0].loadingMore = false
        })
    },
    //递归获取当前评分卡下所有指标层级的数据
    //update 刷新数据时，不需要新增下一级指标
    getIndicators(
      scoreCardId,
      parentCardId = null,
      index,
      change = false,
      update
    ) {
      if (!index || index > 5) return
      this.lastParentCardId = parentCardId
      if (index < 5) {
        let dataSources = this[this.levelMap.get(index).dataSources]
        dataSources.loading = true
        getIndicatorsList({
          scoreCardId: scoreCardId,
          parentCardId: parentCardId,
        })
          .then((res) => {
            if (res.code == 200) {
              let data = res.data
              dataSources.loading = false
              dataSources.dataList = res.data
              if (data.length > 0) {
                const currentIndicatorsIndex = data.findIndex(
                  (item) => item.id == dataSources.activeId
                )
                if (change || currentIndicatorsIndex == -1) {
                  dataSources.activeId = data[0].id
                }
                //果当前指标有数据时，新增一级指标
                if (!update && data && data.length) {
                  if (
                    change ||
                    data[
                      currentIndicatorsIndex === -1 ? 0 : currentIndicatorsIndex
                    ].priFlag == 1
                  ) {
                    this.handleAddLevel()
                  }
                }
                //priFlag指标卡是否存在下级指标卡  0——不存在，1——存在
                if (
                  data[
                    currentIndicatorsIndex === -1 ? 0 : currentIndicatorsIndex
                  ].priFlag == 1
                ) {
                  this.getIndicators(
                    this.scoreCard.activeId,
                    dataSources.activeId,
                    index + 1
                  )
                } else {
                  if (index === 1) {
                    this.lastParentCardId = dataSources.activeId
                  }
                  this.getRule()
                }
              }
            }
          })
          .catch((err) => {})
      } else {
        this.getRule()
      }
    },
    getRule() {
      if (!this.lastParentCardId) return
      let dataSources = this.indicatorRules
      dataSources.loading = true
      this.scordPrimaryIds = []
      for (let i = 1; i < 5; i++) {
        let dataSources = this[this.levelMap.get(i).dataSources]
        if (dataSources.activeId && i <= this.levelIndex)
          this.scordPrimaryIds.push(dataSources.activeId)
      }
      getRuleList({
        scorePrimaryId: this.lastParentCardId,
      })
        .then((res) => {
          if (res.code == 200) {
            dataSources.loading = false
            let data = res.data
            data[0].default = true
            data[0].forbid = true
            this.indicatorRules.dataList = data
          }
        })
        .catch((err) => {})
    },
    handleRuleSort() {
      let dataSources = this.indicatorRules
      this.noDraggable = true
      dataSources.loading = true
      let list = []
      dataSources.dataList.forEach((item, index) => {
        if (item.id) {
          list.push({
            sort: index,
            id: item.id,
          })
        }
      })
      setRuleSort(list)
        .then((res) => {
          if (res.code == 200) {
            this.$message.success(this.$t('decisionPlatform.operationSuccess'))
            this.noDraggable = false
            dataSources.loading = false
          }
        })
        .catch((err) => {
          this.noDraggable = false
          dataSources.loading = false
        })
    },
    handleClick(data, item, index) {
      let dataSources = this[data.dataSources]
      // if (dataSources.activeId === item.id) return;
      dataSources.activeId = item.id
      let lastIndex = this.cardList.length
      if (index == lastIndex - 1) return
      if (index == 0) {
        this.resetCardList()
        // this.levelIndex = 1
        // this.cardList = this.cardList.filter(item => !item.hasOwnProperty('type'))
        // this.indicatorRules.dataList = []
      } else {
        this.levelIndex = index
        this.cardList.splice(index + 1, lastIndex - index - 2)
      }
      let parentCardId = null
      if (index != 0) {
        parentCardId = item.id
      }
      this.$nextTick(() => {
        //如果存在下一个指标，则获取指标数据
        if (
          (data.dataSources === 'scoreCard' &&
            typeof item.priFlag === 'undefined') ||
          item.priFlag === 1
        ) {
          this.getIndicators(
            this.scoreCard.activeId,
            parentCardId,
            index + 1,
            data.dataSources !== 'scoreCard'
          )
        } else {
          //否则直接调用指标规则数据
          this.lastParentCardId = parentCardId
          this.getRule()
        }
      })
    },
    openDrawer(sources, data, index, type = true) {
      if (!sources) return
      let dataSources = null
      let params = null
      this.scoreFrom = {}
      // this.drawer.size = '30%'
      switch (sources) {
        case 'scoreCard':
          this.drawer.title = type
            ? this.$t('decisionPlatform.addScoreCard')
            : this.$t('decisionPlatform.editScoreCard')
          this.scoreFrom = {
            ...data,
            scordCardId: this.scoreCard.activeId,
          }
          this.drawer.type = 'scoreAdd'
          break
        case 'firstIndicators':
        case 'secondIndicators':
        case 'thirdIndicators':
        case 'fourthIndicators':
          this.drawer.type = 'indicatorsAdd'
          const levelTitleKey = this.levelMap.get(index).titleKey
          const levelTitle = this.$t(levelTitleKey)
          this.drawer.title = type
            ? this.$t('decisionPlatform.addLevelIndicator', {
                level: levelTitle,
              })
            : this.$t('decisionPlatform.editLevelIndicator', {
                level: levelTitle,
              })
          if (index > 1)
            dataSources = this[this.levelMap.get(index - 1).dataSources]
          params = {
            scoreCardId: this.scoreCard.activeId || data.scordCardId,
            parentCardId: dataSources ? dataSources.activeId : null,
          }
          this.scoreFrom = { ...data, ...params }
          let totalPercentage = 0,
            list = this.mine[sources] ? this.mine[sources].dataList : []
          list.forEach((item) => {
            totalPercentage += Number(item.weight)
          })
          //总权重(编辑时，去除当前权重)
          localStorage.setItem(
            'weightTotalPercentage',
            data ? totalPercentage - data.weight : totalPercentage
          )
          //list最后一条数据
          let obj = list.length
            ? this.scoreCard.dataList.find(
                (item) => item.id === list[list.length - 1].scoreCardId
              )
            : {}
          localStorage.setItem(
            'weightLast',
            list.length
              ? JSON.stringify({
                  ...list[list.length - 1],
                  modelName: obj.name,
                })
              : ''
          )
          break
        case 'indicatorRules':
          this.drawer.type = 'ruleAdd'
          this.drawer.size = '60%'
          this.drawer.title =
            data && data.default
              ? this.$t('decisionPlatform.editDefaultIndicatorRule')
              : type
              ? this.$t('decisionPlatform.add') +
                this.$t('decisionPlatform.rule')
              : this.$t('decisionPlatform.edit') +
                this.$t('decisionPlatform.rule')
          if (index > 1)
            dataSources = this[this.levelMap.get(index - 1).dataSources]
          this.scoreFrom = {
            ...data,
            scorePrimaryId: this.lastParentCardId || dataSources.activeId,
            scordCardId: this.scoreCard.activeId || data.scordCardId,
            scordPrimaryIds:
              this.scordPrimaryIds.join(';') ||
              dataSources.dataList.map((key) => key.id).join(';'),
          }
          break
        case 'testModel':
          if (!data.buttonState) {
            return this.$message.warning(
              this.$t('decisionPlatform.pleaseEnableModelFirst')
            )
          }
          this.drawer.title = data.name
          this.drawer.type = 'testModel'
          this.drawer.size = '75%'
          this.testParams = {
            modelId: data.id,
            productCode: this.mapProObj.id,
            productName: this.mapProObj.productName,
            businessCode: this.dataRisk.decision.businessCode,
            ruleCode: this.decision.ruleCode,
            modelName: data.scoreCard,
            businessName: this.dataRisk.decision.businessName,
            modelRemark: data.descr,
          }
          break
        case 'reuse':
          this.drawer.title = this.$t('decisionPlatform.standardScoreCardReuse')
          this.drawer.type = 'reuse'
          this.drawer.size = 'auto'
          this.reuseParams = {
            dataSources: data.dataSources,
            productCode: this.mapProObj.id,
            productName: this.mapProObj.productName,
            businessCode: this.dataRisk.decision.businessCode,
            ruleCode: 1,
            businessName: this.dataRisk.decision.businessName,
          }
          break
      }
      this.drawer.visible = true
    },
    handleFun(dataSources, type, data) {
      if (dataSources) {
        this[dataSources[type]](data)
      }
    },
    delScoreCard(data) {
      this.$confirm(
        this.$t('decisionPlatform.deleteScoreCardConfirm', {
          name: data.scoreCard,
        }),
        this.$t('decisionPlatform.tip'),
        {
          confirmButtonText: this.$t('decisionPlatform.confirm'),
          cancelButtonText: this.$t('decisionPlatform.cancel'),
          type: 'warning',
        }
      )
        .then((res) => {
          const API =
            !isStandardDept() && data.deptFlag == 1
              ? deleteParentCard
              : deleteScore
          const params =
            !isStandardDept() && data.deptFlag == 1
              ? {
                  buildProjectCode: this.decision.projectCode,
                  buildBusinessCode: this.decision.businessCode,
                  parentCardId: data.id,
                  versionControl: data.versionControl,
                }
              : {
                  ...this.decision,
                  id: data.id,
                  versionControl: data.versionControl,
                }
          // console.log('params', params)
          API(params)
            .then((res) => {
              if (API == deleteScore && !res.data) {
                this.$message.warning(res.msg)
                return
              }
              this.$message.success(
                this.$t('decisionPlatform.operationSuccess')
              )
              this.getFirstList(true)
            })
            .catch((err) => {})
        })
        .catch((err) => {})
    },
    delIndicatorsCard(data) {
      /*this.dataInfo = data
       this.dataInfo.type = 1
       this.dataText = `是否删除指标卡【${data.primaryIndex}】?`
       this.$refs.confirmDialog.visible = true*/
      this.$confirm(
        this.$t('decisionPlatform.deleteIndicatorCardConfirm', {
          name: data.primaryIndex,
        }),
        this.$t('decisionPlatform.tip'),
        {
          confirmButtonText: this.$t('decisionPlatform.confirm'),
          cancelButtonText: this.$t('decisionPlatform.cancel'),
          type: 'warning',
        }
      )
        .then((res) => {
          deleteIndicators({
            ...this.decision,
            id: data.id,
            parentCardId: data.parentCardId,
            versionControl: this.getVersionControl,
          })
            .then((res) => {
              this.$message.success(
                this.$t('decisionPlatform.operationSuccess')
              )
              this.getIndicatorsLevelData(true)
            })
            .catch((err) => {})
        })
        .catch((err) => {})
    },
    delRulesCard(data) {
      /*this.dataInfo = data
       this.dataInfo.type = 3
       this.dataText = `是否删除规则【${data.indexRule}】?`
       this.$refs.confirmDialog.visible = true*/
      this.$confirm(
        this.$t('decisionPlatform.deleteRuleConfirm', { code: data.indexRule }),
        this.$t('decisionPlatform.tip'),
        {
          confirmButtonText: this.$t('decisionPlatform.confirm'),
          cancelButtonText: this.$t('decisionPlatform.cancel'),
          type: 'warning',
        }
      )
        .then((res) => {
          deleteRules({
            ...this.decision,
            id: data.id,
            parentCardId: data.parentCardId,
            versionControl: this.getVersionControl,
          })
            .then((res) => {
              this.$message.success(
                this.$t('decisionPlatform.operationSuccess')
              )
              this.getIndicatorsLevelData(true)
            })
            .catch((err) => {})
        })
        .catch((err) => {})
    },
    handleScoreSwitch(data) {
      const action =
        data.buttonState == 1
          ? this.$t('decisionPlatform.enable')
          : this.$t('decisionPlatform.close')
      this.$confirm(
        this.$t('decisionPlatform.enableOrDisableStrategy', {
          action: action,
          name: data.name,
        }),
        this.$t('decisionPlatform.tip'),
        {
          confirmButtonText: this.$t('decisionPlatform.confirm'),
          cancelButtonText: this.$t('decisionPlatform.cancel'),
          type: 'warning',
        }
      )
        .then(() => {
          let params = {
            buttonState: data.buttonState,
            id: data.id,
            ...this.decision,
            versionControl: data.versionControl || this.getVersionControl,
          }
          // 如果一级菜单关闭-后面的子菜单全部关闭
          if (data.buttonState == 0) {
          }
          updateScoreStatus(params)
            .then((res) => {
              if (res.code == 200) {
                this.$message.success(
                  this.$t('decisionPlatform.operationSuccess')
                )
                this.getFirstList(true)
              }
            })
            .catch((err) => {})
        })
        .catch(() => {
          if (data.buttonState === 1) {
            data.buttonState = 0
          } else {
            data.buttonState = 1
          }
        })
    },
    handleIndicatorsSwitch(data) {
      /*this.dataInfo = data
       this.dataInfo.type = 2
       this.dataText = `是否${data.buttonState == 1 ? '启用' : '关闭'}指标 【${data.primaryIndex}】?`
       this.$refs.confirmDialog.visible = true*/
      const action =
        data.buttonState == 1
          ? this.$t('decisionPlatform.enable')
          : this.$t('decisionPlatform.close')
      this.$confirm(
        this.$t('decisionPlatform.enableOrDisableIndicator', {
          action: action,
          name: data.primaryIndex,
        }),
        this.$t('decisionPlatform.tip'),
        {
          confirmButtonText: this.$t('decisionPlatform.confirm'),
          cancelButtonText: this.$t('decisionPlatform.cancel'),
          type: 'warning',
        }
      )
        .then(() => {
          updateStatusIndicators({
            buttonState: data.buttonState,
            id: data.id,
            ...this.decision,
            versionControl: this.getVersionControl,
          })
            .then((res) => {
              if (res.code == 200) {
                this.$message.success(
                  this.$t('decisionPlatform.operationSuccess')
                )
                this.getFirstList(true)
              }
            })
            .catch((err) => {})
        })
        .catch(() => {
          if (data.buttonState === 1) {
            data.buttonState = 0
          } else {
            data.buttonState = 1
          }
        })
    },
    handleRuleSwitch(data) {
      /*this.dataInfo = data
       this.dataInfo.type = 4
       this.dataText = `是否${data.buttonState == 1 ? '启用' : '关闭'}规则 【${data.indexRule}】?`
       this.$refs.confirmDialog.visible = true*/
      const action =
        data.buttonState == 1
          ? this.$t('decisionPlatform.enable')
          : this.$t('decisionPlatform.close')
      this.$confirm(
        this.$t('decisionPlatform.enableOrDisableRule', {
          action: action,
          name: data.indexRule,
        }),
        this.$t('decisionPlatform.tip'),
        {
          confirmButtonText: this.$t('decisionPlatform.confirm'),
          cancelButtonText: this.$t('decisionPlatform.cancel'),
          type: 'warning',
        }
      )
        .then(() => {
          updateIncreaseStatus({
            buttonState: data.buttonState,
            id: data.id,
            ...this.decision,
            versionControl: this.getVersionControl,
          })
            .then((res) => {
              if (res.code == 200) {
                this.$message.success(
                  this.$t('decisionPlatform.operationSuccess')
                )
                this.getFirstList(true)
              }
            })
            .catch((err) => {})
        })
        .catch(() => {
          if (data.buttonState === 1) {
            data.buttonState = 0
          } else {
            data.buttonState = 1
          }
        })
    },
    handleAddLevel() {
      if (this.levelIndex < 4) {
        this.levelIndex++
        let levelInfo = this.levelMap.get(this.levelIndex)
        let level = JSON.parse(JSON.stringify(this.addLevel))
        level.cardTitleKey = levelInfo.titleKey
        level.dataSources = levelInfo.dataSources
        this.cardList.splice(this.levelIndex, 0, level)
      }
    },
    handleDelLevel() {
      /*this.dataInfo.type = 5
       this.dataText = `是否删除当前【${this.numberChange(this.levelIndex)}级指标卡】?`
       this.$refs.confirmDialog.visible = true*/
      const levelTitle = this.$t(this.levelMap.get(this.levelIndex).titleKey)
      this.$confirm(
        this.$t('decisionPlatform.deleteCurrentLevelIndicatorCardConfirm', {
          level: levelTitle,
        }),
        this.$t('decisionPlatform.tip'),
        {
          confirmButtonText: this.$t('decisionPlatform.confirm'),
          cancelButtonText: this.$t('decisionPlatform.cancel'),
          type: 'warning',
        }
      )
        .then(() => {
          let data = this[this.levelMap.get(this.levelIndex - 1).dataSources]
          deleteIndicatorsCard({
            ...this.decision,
            id: data.activeId,
          })
            .then((res) => {
              if (res.code == 200) {
                this.$message.success(
                  this.$t('decisionPlatform.operationSuccess')
                )
                this.getFirstList(true)
                // this.getIndicators(this.scoreCard.activeId, data.activeId, this.levelIndex)
                // this.cardList.splice(this.levelIndex, 1)
                // this.levelIndex--
              }
            })
            .catch((err) => {})
        })
        .catch(() => {})
    },
    /**
     * 发布接口调用
     */
    releaseRule() {
      if (isStandardDept()) {
        // 标准部门——触发更改版本的弹窗进行去除,点击发布的时候强制触发更新版本
        releaseScore({
          ...this.decision,
          versionControl: this.getVersionControl,
          id: this.scoreCard.activeId,
        })
          .then((res) => {
            if (res.code == 200) {
              this.$message.success(this.$t('decisionPlatform.releaseSuccess'))
              this.getFirstList(true)
              versionControlFraud(this.getqueryData()).then((res2) => {})
            }
          })
          .catch((err) => {})
      } else {
        // 非标准部门——触发更改版本的弹窗进行去除,点击发布的时候弹出是否更新版本弹窗
        this.dataText = this.$t('decisionPlatform.confirmRelease')
        this.$refs.confirmDialog.visible = true
      }
    },
    onMove(e) {
      if (e.relatedContext.element.forbid) return false
      return true
    },
    handleSuccess() {
      this.handleClose()
      this.getFirstList(true)
    },
    getIndicatorsLevelData() {
      if (this.levelIndex == 1) {
        this.indicatorRules.dataList = []
        this.getFirstList(true)
        return
      }
      this.cardList.splice(this.levelIndex, 1)
      this.levelIndex--
      let data = this[this.levelMap.get(this.levelIndex).dataSources]
      this.getIndicators(
        this.scoreCard.activeId,
        data.activeId,
        this.levelIndex + 1,
        true
      )
    },
    resetCardList() {
      this.cardList = this.cardList.filter(
        (item) => !item.hasOwnProperty('type')
      )
      this.levelIndex = 1
      this.indicatorRules.dataList = []
      this.cardList.forEach((item, index) => {
        if (index != 0) {
          let data = this[item.dataSources]
          data.dataList = []
          data.activeId = null
        }
      })
    },
    handleClose() {
      this.drawer.visible = false
      if (this.$refs.reuseRef) {
        this.$refs.reuseRef.onreset()
      }
      if (this.drawer.type === 'testModel') {
        this.$refs.testModelRef.clearTimer()
      }
    },
    //数字转中文大写
    numberChange(num) {
      const changeNum = [
        '零',
        '一',
        '二',
        '三',
        '四',
        '五',
        '六',
        '七',
        '八',
        '九',
      ]
      const unit = ['', '十', '百']
      num = parseInt(num)
      const getWan = (temp) => {
        const strArr = temp.toString().split('').reverse()
        let newNum = ''
        for (var i = 0; i < strArr.length; i++) {
          newNum =
            (i === 0 && strArr[i] === 0
              ? ''
              : i > 0 && strArr[i] === 0 && strArr[i - 1] === 0
              ? ''
              : changeNum[strArr[i]] + (strArr[i] === 0 ? unit[0] : unit[i])) +
            newNum
        }
        return newNum
      }
      const overWan = Math.floor(num / 100)
      let noWan = num % 100
      if (noWan.toString().length < 2) noWan = '0' + noWan
      return overWan ? getWan(overWan) + '百' + getWan(noWan) : getWan(num)
    },
    /**
     *权重输入框 enter
     * @param e 当前值
     * @param index 下标
     * @param list 当前dataList
     * @param type 对应cardList--dataSources
     */
    handleInput(e, index, list, type) {
      if (list.length === index + 1) return
      let totalPercentage = 0,
        maxPercentage = 100
      list.forEach((item, i) => {
        if (index !== i) totalPercentage += Number(item.weight)
      })
      if (
        Number(e) >
        Number(maxPercentage - totalPercentage + list[list.length - 1].weight)
      ) {
        return this.$message.error(
          this.$t('decisionPlatform.totalWeightCannotExceed')
        )
      }
      this.mine[type].loading = true
      this.$refs[`${type}Input${index}`][0].blur()
      switch (type) {
        case 'firstIndicators':
          this.levelIndex = 1
          break
        case 'secondIndicators':
          this.levelIndex = 2
          break
        case 'thirdIndicators':
          this.levelIndex = 3
          break
        case 'fourthIndicators':
          this.levelIndex = 4
          break
        default:
          break
      }
      const lastWeightValue1 = Number(
        maxPercentage - (totalPercentage + Number(e))
      )
      const lastWeightValue2 = Number(
        maxPercentage -
          (totalPercentage - list[list.length - 1].weight + Number(e))
      )
      const currentItem = list[index]
      const parentCardId =
        this.levelIndex === 1
          ? null
          : this[this.levelMap.get(this.levelIndex - 1).dataSources].activeId
      updateIndicators({
        businessCode: currentItem.businessCode,
        description: currentItem.description,
        weight: currentItem.weight,
        id: currentItem.id,
        primaryIndex: currentItem.primaryIndex,
        projectCode: currentItem.projectCode,
        ruleCode: currentItem.ruleCode,
        scoreCardId: currentItem.scoreCardId,
        versionControl: currentItem.versionControl,
      }).then(() => {
        //	默认-保留原版本
        versionReserveScore({
          ...this.getqueryData(),
          versionControl: currentItem.versionControl,
        }).then((res) => {
          if (Number(e) !== Number(maxPercentage - totalPercentage)) {
            const lastItem = list[list.length - 1]
            let lastWeight =
              totalPercentage == lastItem.weight
                ? Number(maxPercentage - e)
                : Number(e) > Number(maxPercentage - totalPercentage)
                ? lastWeightValue2
                : lastWeightValue1
            list[list.length - 1].weight = lastWeight
            updateIndicators({
              businessCode: lastItem.businessCode,
              description: lastItem.description,
              weight: lastWeight,
              id: lastItem.id,
              primaryIndex: lastItem.primaryIndex,
              projectCode: lastItem.projectCode,
              ruleCode: lastItem.ruleCode,
              scoreCardId: lastItem.scoreCardId,
              versionControl: lastItem.versionControl,
            }).then(() => {
              versionReserveScore({
                ...this.getqueryData(),
                versionControl: lastItem.versionControl,
              }).then((res2) => {
                if (res2.code === 200) {
                  this.getIndicators(
                    this.scoreCard.activeId,
                    parentCardId,
                    this.levelIndex,
                    false,
                    true
                  )
                }
              })
            })
          } else {
            if (res.code === 200) {
              this.getIndicators(
                this.scoreCard.activeId,
                parentCardId,
                this.levelIndex,
                false,
                true
              )
            }
          }
        })
      })
      list[index].weight = e
    },
    /**
     * 滚动加载列表数据
     * @param objName 值为：scoreCard，firstIndicators，secondIndicators，thirdIndicators，fourthIndicators，indicatorRules
     * @param cardIndex
     */
    viewLoad(objName, cardIndex) {
      if (objName !== 'scoreCard') return
      if (!this[objName].total) return
      if (!this.cardList[cardIndex].loadingMore && !this[objName].loading) {
        if (
          this.cardList[cardIndex].filterParams.pageNum >=
            this[objName].total &&
          this[objName].total
        ) {
          this.cardList[cardIndex].noMore = true
        } else {
          this.cardList[cardIndex].loadingMore = true
          this.cardList[cardIndex].filterParams.pageNum++
          //	调用接口
          switch (objName) {
            case 'scoreCard':
              this.getFirstList()
              break
            default:
              break
          }
        }
      }
    },
    /**
     * 搜索框搜索
     * @param e 搜索值
     * @param cardIndex 卡片下标
     */
    onsearch(e, cardIndex) {
      this.cardList[cardIndex].filterParams.pageNum = 1
      this.onBlur(this.cardList[cardIndex].filterParams, cardIndex)
      if (cardIndex === 0) {
        this.getFirstList(false, true)
      }
    },
    /**
     * 搜索框获取焦点
     * @param filterItem
     * @param cardIndex
     */
    onFocus(filterItem, cardIndex) {
      this.cardList[cardIndex].isFocus = true
      // if (this.$refs.copyButtonRef && this.$refs.copyButtonRef[cardIndex]) {
      //   this.$refs.copyButtonRef[cardIndex].style.background = 'transparent'
      //   this.$refs.copyButtonRef[cardIndex].style.padding = 0
      //   this.$refs.copyButtonTextRef[cardIndex].style.fontSize = 0
      //   this.$refs.copyButtonTextRef[cardIndex].style.opacity = 0
      // }
      // if (this.$refs.addButtonRef && this.$refs.addButtonRef[cardIndex]) {
      //   this.$refs.addButtonRef[cardIndex].style.background = 'transparent'
      //   this.$refs.addButtonRef[cardIndex].style.padding = 0
      //   this.$refs.addButtonTextRef[cardIndex].style.fontSize = 0
      //   this.$refs.addButtonTextRef[cardIndex].style.opacity = 0
      // }
    },
    /**
     * 搜索框失去焦点
     * @param filterItem
     * @param cardIndex
     */
    onBlur(filterItem, cardIndex) {
      if (filterItem.scoreCard) {
        this.onFocus(filterItem, cardIndex)
      } else {
        this.cardList[cardIndex].isFocus = false
        // this.$nextTick(() => {
        //   if (this.$refs.copyButtonRef && this.$refs.copyButtonRef[cardIndex]) {
        //     this.$refs.copyButtonRef[cardIndex].style.background =
        //       'rgba(0, 181, 120, 0.1)'
        //     this.$refs.copyButtonRef[cardIndex].style.padding = '10px 15px'
        //     this.$refs.copyButtonTextRef[cardIndex].style.fontSize = '14px'
        //     this.$refs.copyButtonTextRef[cardIndex].style.opacity = 1
        //   }
        //   if (this.$refs.addButtonRef && this.$refs.addButtonRef[cardIndex]) {
        //     this.$refs.addButtonRef[cardIndex].style.background =
        //       'rgba(255, 143, 31, 0.1)'
        //     this.$refs.addButtonRef[cardIndex].style.padding = '10px 15px'
        //     this.$refs.addButtonTextRef[cardIndex].style.fontSize = '14px'
        //     this.$refs.addButtonTextRef[cardIndex].style.opacity = 1
        //   }
        // })
      }
    },
  },
}
</script>

<style lang="less" scoped>
.version-box {
  // display: flex;
  font-size: 14px;
  font-family: PingFang SC, PingFang SC;
  font-weight: 400;
  color: rgba(0, 0, 0, 0.6);

  div {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

.version-list {
  height: 260px;
  overflow-y: auto;
  padding: 0px 14px;

  .version-item {
    border-bottom: 1px solid #ebebeb;
    height: 48px;
    box-sizing: border-box;
    line-height: 48px;
    cursor: pointer;
    text-align: center;

    &:hover {
      background: #f2f8fe;
      color: #4471fc;
    }
  }
}

.top-tag {
  display: flex;
  align-items: center;
  font-size: 14px;
  font-family: PingFang SC, PingFang SC;
  font-weight: 400;
  color: #fa5151;

  .champion-icon {
    width: 16px;
    height: 16px;
    margin-right: 3px;
  }
}

.preApproval_rule {
  width: 100%;
  height: 100%;

  .tableCard {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: space-between;

    .marginLeft {
      margin-left: 20px;
    }

    .card {
      height: 100%;
      padding: 0px 20px 20px 20px;
      background-color: var(--decision-model-card-bg);
      border: none;
      user-select: none;

      /deep/ .el-loading-mask {
        top: 80px;
      }

      .top {
        width: 100%;
        height: 80px;
        // position: relative;
        border-bottom: 1px rgba(#000, 0.08) solid;
        box-sizing: border-box;
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;

        .title {
          // width: 100%;
          height: 100%;
          display: flex;
          justify-content: center;
          align-items: center;
          // font-family: "PingFang SC-Medium", PingFang SC;
          font-size: 18px;
          font-weight: 600;
          color: var(--text-color-secondary);

          .search-button {
            flex: 1;
            margin-left: 4px;
            display: flex;
            align-items: center;

            &-icon {
              img {
                width: 14px;
                height: 14px;
              }
            }

            ::v-deep .el-input {
              &__inner {
                background: rgba(2, 86, 255, 0.06);
                border-color: transparent;
                height: 40px;
                line-height: 40px;
                opacity: 1;
                border-radius: 20px;
                transition: width 300ms ease, height 300ms ease,
                  border-radius 300ms ease, opacity 300ms ease;

                & + .el-input__prefix {
                  .search-button-icon {
                    width: 34px;
                    height: 100%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                  }
                }
              }

              &:not(.isFocus) {
                .el-input__inner {
                  opacity: 0;
                  width: 0;
                  height: 30px;
                  line-height: 30px;
                  padding: 0;

                  &::placeholder {
                    color: transparent;
                  }

                  & + .el-input__prefix {
                    .search-button-icon {
                      cursor: pointer;
                      border-radius: 4px;
                      background: rgba(2, 86, 255, 0.06);
                    }
                  }
                }
              }
            }
          }
        }

        .top_operate {
          display: flex;

          .copyBtn,
          .addBtn,
          .delBtn {
            margin-left: 10px;
            .operateBtn {
              border-radius: 6px;
              display: flex;
              align-items: center;
              padding: 10px 15px;
              font-size: 14px;
              cursor: pointer;
            }

            img {
              width: 20px;
              height: 20px;
              margin-right: 5px;
            }

            span {
              opacity: 1;
              transition: font-size 300ms ease, opacity 300ms ease;
            }
          }

          .copyBtn {
            .operateBtn {
              background: var(--decision-model-card-operate-copy-btn-bg);
              border: var(--decision-model-card-operate-copy-btn-border);
              color: #00b578;
            }
          }

          .addBtn {
            .operateBtn {
              background: var(--decision-model-card-operate-add-btn-bg);
              border: var(--decision-model-card-operate-add-btn-border);
              color: #ff8f1f;
            }
          }

          .delBtn {
            background-color: rgba(var(--primary-color), 0.06);
            color: var(--primary-color);
          }
        }
      }

      .dataContent {
        height: calc(100% - 80px);
        overflow-y: auto;

        &::-webkit-scrollbar {
          width: 0 !important;
        }

        .dataCard {
          margin: 0 0 14px;
          background-color: var(--bg-color);
          margin-bottom: 14px;
          border-radius: 4px;
          padding: 14px;
          display: flex;
          justify-content: space-between;
          box-sizing: border-box;
          cursor: pointer;
          position: relative;
          border: var(--decision-model-card-border);

          .detail {
            height: 100%;
            display: flex;
            flex-direction: column;
            width: calc(100% - 75px);

            &.sort-detail {
              width: calc(100% - 124px);
            }

            .name {
              display: flex;
              align-items: center;
              width: 100%;

              > span {
                font-size: 16px;
                font-weight: 600;
                color: var(--text-color-secondary);
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                margin-bottom: 4px;
                display: block;
                margin-right: 5px;
              }

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

            .descr,
            .weight {
              display: block;
              font-size: 14px;
              font-weight: 400;
              color: var(--text-color-tertiary);
              text-overflow: ellipsis;
              overflow: hidden;
              white-space: nowrap;
              line-height: 22px;

              &-inner {
                overflow: initial;
                padding: 10px 0;

                ::v-deep .el-popover {
                  min-width: 0;
                  padding: 10px;
                }

                ::v-deep .el-input {
                  width: 45px;

                  &__inner {
                    height: 32px !important;
                    padding: 0 0 0 5px;
                    color: var(--text-color-tertiary);
                    background: transparent;
                    border-color: transparent;

                    &:hover {
                      background: #ffffff;
                      border-color: #dcdfe6;
                    }

                    &:focus {
                      background: #ffffff;
                      border-color: var(--primary-color);
                    }

                    &[disabled='disabled'] {
                      background: rgba(238, 239, 241);
                      border-color: rgb(221, 222, 223);
                    }
                  }

                  &__suffix {
                    top: 5px;
                    height: auto;
                    color: var(--text-color-tertiary);
                  }
                }
              }
            }

            .descr {
              -webkit-line-clamp: 3;
              line-clamp: 3;
              -webkit-box-orient: vertical;
              white-space: normal;
              display: -webkit-box;
            }
          }

          .operate {
            width: 146px;
            height: 100%;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            position: absolute;
            right: 14px;
            top: 0px;
            padding: 14px 0px;
            box-sizing: border-box;

            .operate_btnList,
            .view_btnList {
              display: flex;

              > button {
                min-width: 70px;
                height: 32px;
                display: flex;
                justify-content: center;
                align-items: center;

                .operate_img {
                  width: 16px;
                  height: 16px;
                  margin-right: 2px;
                }

                .operate_span_edit,
                .operate_span_del {
                  font-size: 14px;
                  margin-top: 2px;
                }

                .operate_span_edit {
                  color: #4d96ff;
                }

                .operate_span_del {
                  color: #fa5853;
                }
              }

              .edit {
                background-color: var(
                  --decision-model-card-operate-edit-btn-bg
                );
                border: var(--decision-model-card-operate-edit-btn-border);
                border-radius: 6px;
              }

              .delete {
                background-color: var(
                  --decision-model-card-operate-delete-btn-bg
                );
                border: var(--decision-model-card-operate-delete-btn-border);
                border-radius: 6px;
              }
            }

            .operate_btnList {
              justify-content: flex-end;
              position: relative;

              > button {
                margin-left: 10px;
              }
            }

            .addLevel {
              display: flex;
              align-items: center;
              justify-content: center;
              background-color: var(
                --decision-model-card-operate-addLevel-btn-bg
              ) !important;
              border: var(--decision-model-card-operate-addLevel-btn-border);
              color: #ff8f1f;
              font-size: 14px;
              border-radius: 6px;
              box-sizing: border-box;
              padding: 5px;

              .icon {
                width: 20px;
                height: 20px;
                margin-right: 5px;
              }
            }

            .view_btnList {
              justify-content: flex-end;
            }

            .switch {
              width: 100%;
              display: flex;
              justify-content: flex-end;
              position: relative;

              .test-button {
                cursor: pointer;
                height: 24px;
                line-height: 24px;
                font-size: 10px;
                color: #ff8f1f;
                padding: 0 15px;
                border-radius: 12px;
                border: 1px solid #ff8f1f;
                margin-right: 10px;
              }

              /deep/ .el-switch {
                .el-switch__core {
                  width: 50px !important;
                  height: 24px;
                  border-radius: 12px;

                  &:after {
                    width: 12px;
                    height: 12px;
                    left: 4px;
                    top: 5px;
                  }
                }

                &.el-switch {
                  &.is-checked {
                    .el-switch__core {
                      &:after {
                        left: 100%;
                      }
                    }
                  }
                }

                &.is-checked {
                  .el-switch__label--left {
                    opacity: 0;
                  }

                  .el-switch__label--right {
                    opacity: 1;
                  }
                }

                .el-switch__label {
                  position: absolute;
                  top: 0;
                  display: flex;
                  align-items: center;

                  > span {
                    font-size: 10px !important;
                  }
                }

                .el-switch__label--left {
                  right: 0;
                  color: #fff !important;
                  z-index: 1;
                  margin-right: 5px;
                }

                .el-switch__label--right {
                  left: 0;
                  color: #969696 !important;
                  opacity: 0;
                  margin-left: 5px;
                }
              }
            }
          }
        }

        .defaultModel {
          background: linear-gradient(135deg, #eef3ff 20%, #fff 60%);
        }

        .activeCard {
          background: var(--decision-model-card-active-bg) !important;
          top: 0;
          left: 0;
          right: 0;
        }
      }
    }
  }

  .default {
    .card {
      width: 32.5%;
    }
  }

  .moreCard {
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

    .card {
      width: 500px;
      min-width: 500px;
    }
  }
}
</style>
