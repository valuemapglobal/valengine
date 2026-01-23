<template>
  <div class="preApproval_rule">
    <div class="tableCard">
      <template v-for="(cardItem, cardIndex) in cardList">
        <div
          :key="cardIndex"
          class="card"
          :element-loading-text="$t('decisionPlatform.dataLoading')"
          v-loading="mine[cardItem.dataSources].loading"
        >
          <div class="top">
            <div class="title">
              {{ $t(cardItem.cardTitleKey) }}
              <div class="search-button">
                <el-input
                  :class="{ isFocus: cardItem.isFocus }"
                  ref="searchInputRef"
                  v-model="cardItem.filterParams.name"
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
              <el-upload
                ref="uploadJSONRef"
                class="upload-demo"
                action="#"
                :show-file-list="false"
                accept=".json"
                :limit="1"
                :before-upload="beforeUpload"
                :http-request="uploadFile"
              >
                <div
                  ref="importButtonRef"
                  class="importBtn"
                  v-if="
                    hasButton('import:strategy:show') &&
                    cardItem.dataSources == 'tactics'
                  "
                >
                  <div class="operateBtn" v-if="!cardItem.isFocus">
                    <img
                      src="../../../images/DueDiligence_active.png"
                      :alt="$t('decisionPlatform.importStrategy')"
                    />
                    <span ref="importButtonTextRef">{{
                      $t('decisionPlatform.importStrategy')
                    }}</span>
                  </div>
                  <img
                    v-else
                    src="../../../images/DueDiligence_active.png"
                    :alt="$t('decisionPlatform.importStrategy')"
                  />
                </div>
              </el-upload>

              <!-- v-if="hasButton('strategy:reuse:show') && !isStandardDept()" -->
              <div
                ref="copyButtonRef"
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
                @click="openDrawer(cardItem.dataSources)"
                v-if="
                  hasButton('Newstrategy:addition:show') &&
                  (['tactics'].includes(cardItem.dataSources) ||
                    tactics.showIcon == '0')
                "
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
              <template
                v-for="(item, index) in mine[cardItem.dataSources].dataList"
              >
                <div
                  :key="index"
                  class="dataCard"
                  @click="mine[cardItem.searchDataFun](item, true)"
                  :class="{
                    activeCard: item.id == mine[cardItem.dataSources].activeId,
                    defaultModel: handleOperate(item),
                  }"
                >
                  <div
                    class="detail"
                    :class="{
                      'sort-detail': cardItem.dataSources === 'tactics',
                    }"
                  >
                    <div class="name">
                      <span
                        :title="
                          cardItem.dataSources === 'rule'
                            ? item.code
                            : item.name
                        "
                        :style="`max-width: calc(100% - ${
                          isEnglish() ? '130px' : '95px'
                        });`"
                      >
                        {{
                          cardItem.dataSources === 'rule'
                            ? item.code
                            : item.name
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
                    <span class="descr"
                      >{{ $t('decisionPlatform.description') }}：{{
                        cardItem.dataSources === 'rule' ? item.name : item.descr
                      }}</span
                    >
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
                      <div
                        class="top-tag"
                        v-if="
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
                  </div>
                  <div class="operate">
                    <div class="operate_btnList" @click.stop>
                      <button
                        v-if="
                          hasButton('strategy:edit:show') &&
                          (item.deptFlag != 1 ||
                            (item.deptFlag == 1 && isEditShow(item.deptId)))
                        "
                        class="edit"
                        @click="openDrawer(cardItem.dataSources, item)"
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
                        v-if="hasButton('Strategy:deletion:show')"
                        class="delete"
                        @click="mine[cardItem.delFun](item)"
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
                    <div class="export-button">
                      <el-button
                        round
                        v-if="
                          hasButton('strategy:export:show') &&
                          cardItem.dataSources === 'tactics'
                        "
                        @click="exportTactics(item)"
                      >
                        {{ $t('decisionPlatform.export') }}
                      </el-button>
                    </div>
                    <div @click.stop class="switch">
                      <div
                        class="test-button"
                        v-if="
                          hasButton('strategy:test:show') &&
                          cardItem.dataSources === 'tactics' &&
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
                          v-if="hasButton('enable:disable:show')"
                          v-model="item.status"
                          active-color="#D6D3D3"
                          inactive-color="var(--primary-color)"
                          :active-text="$t('decisionPlatform.disabled')"
                          :inactive-text="$t('decisionPlatform.enabled')"
                          active-value="0"
                          inactive-value="1"
                          @change="mine[cardItem.switchFun](item)"
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </template>
            </InfiniteLoading>
          </div>
        </div>
      </template>
    </div>

    <el-drawer
      :title="drawer.title"
      :visible.sync="drawer.visible"
      direction="rtl"
      :modal="false"
      :wrapperClosable="false"
      :size="drawer.width"
    >
      <addTactics
        ref="addTactics"
        :versionControl="getVersionControl"
        type="rule"
        v-if="drawer.type === 'tactics'"
        :formData="tactics.tacticsRowData"
        :status="drawer.status"
        @close="drawer.visible = false"
        @success="handleClose"
        :tactics="tactics"
      ></addTactics>
      <addRuleGroup
        ref="addRuleGroup"
        v-if="drawer.type === 'ruleGroup'"
        :tactics="tactics"
        :tacticsId="tactics.activeId"
        :formData="ruleGroup.ruleGroupData"
        :status="drawer.status"
        @close="drawer.visible = false"
        @success="handleClose"
        :versionControl="getVersionControl"
      ></addRuleGroup>
      <addOrEditRule
        v-if="drawer.type === 'rule'"
        :versionControl="getVersionControl"
        :info="rule.ruleData"
        :selectList="rule.dataList"
        :tacticsId="tactics.activeId"
        :tactics="tactics"
        :ruleGroupId="ruleGroup.activeId"
        :status="drawer.status"
        @close="drawer.visible = false"
        @success="handleClose"
      ></addOrEditRule>
      <TestModel
        v-if="drawer.type === 'testModel'"
        ref="testModelRef"
        :paramsData="testParams"
      />
      <strategyReuse
        v-if="drawer.type === 'tacticsReuse'"
        ref="reuseRef"
        :paramsData="reuseParams"
        @success="handleClose"
      />
      <groupReuse
        v-if="drawer.type === 'ruleGroupReuse'"
        ref="reuseRef"
        :paramsData="reuseParams"
        @success="handleClose"
      />
      <ruleReuse
        v-if="drawer.type === 'ruleReuse'"
        ref="reuseRef"
        :paramsData="reuseParams"
        @success="handleClose"
      />
    </el-drawer>
    <confirmDialog
      ref="confirmDialog"
      :disabled="disabledVersion"
      @update="publishVersion"
      @reserved="publishSnapshot"
      :updateText="$t('decisionPlatform.publishVersion')"
      :reservedText="$t('decisionPlatform.publishSnapshot')"
      @cancel="cancel"
    >
      <div>
        <div>{{ $t('decisionPlatform.choosePublishType') }}</div>
        <div style="margin-top: 8px; color: #666; font-size: 13px;">
          {{ $t('decisionPlatform.publishVersionTip') }}
        </div>
        <div style="margin-top: 4px; color: #666; font-size: 13px;">
          {{ $t('decisionPlatform.publishSnapshotTip') }}
        </div>
      </div>
    </confirmDialog>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import {
  releaseStrategy,
  updateState,
  // logChange, 暂时注释 20260105
} from '../api/index'
import confirmDialog from '../components/confirmDialog.vue'
import {
  delAntiProjectFraud,
  deletePolicyGroup,
  delRuleGroup,
  delRuleRecord,
  delTactics,
  getVersion,
  searchCodeNewList,
  searchGroupNewList,
  selectNewList,
  versionControlFraud,
  versionReserveFraud,
  exportRuleJSON,
  importRuleJSON,
} from '../api/riskModel'
import AddOrEditRule from '../components/addOrEditRule.vue'
import AddRuleGroup from '../components/addRuleGroup.vue'
import addTactics from '../components/addTactics.vue'
import TestModel from '../components/testModel.vue'
import strategyReuse from '../components/strategyReuse.vue'
import groupReuse from '../components/groupReuse.vue'
import ruleReuse from '../components/ruleReuse.vue'
import InfiniteLoading from '@/components/InfiniteLoading'
import { isEditShow, isStandardDept } from '../utils/index.js'
// import { handleCheckLock } from '../utils/index.js' 暂时注释 20260105
export default {
  components: {
    addTactics,
    AddRuleGroup,
    AddOrEditRule,
    confirmDialog,
    TestModel,
    strategyReuse,
    groupReuse,
    ruleReuse,
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
      mine: this,
      activeIndex: 4,
      deleteText: '',
      operationInfo: {}, // 删除,是否使用操作信息
      cardList: [
        {
          cardTitleKey: 'decisionPlatform.strategy',
          dataSources: 'tactics',
          searchDataFun: 'searchRuleGroup',
          delFun: 'delTactics',
          switchFun: 'handleTacticsSwitch',
          loadingMore: false, //正在获取更多内容……
          noMore: false, //没有更多了
          filterParams: {
            pageNum: 1,
            pageSize: 10,
            name: undefined,
          },
          isFocus: false, //搜索框是否获取焦点
        },
        {
          cardTitleKey: 'decisionPlatform.ruleGroup',
          dataSources: 'ruleGroup',
          searchDataFun: 'searchRuleList',
          delFun: 'delRuleGroup',
          switchFun: 'handleRuleGroupSwitch',
          loadingMore: false, //正在获取更多内容……
          noMore: false, //没有更多了
          filterParams: {
            pageNum: 1,
            pageSize: 10,
            name: undefined,
          },
          isFocus: false, //搜索框是否获取焦点
        },
        {
          cardTitleKey: 'decisionPlatform.rule',
          dataSources: 'rule',
          searchDataFun: 'handleRuleClick',
          delFun: 'delRule',
          switchFun: 'handleRuleSwitch',
          loadingMore: false, //正在获取更多内容……
          noMore: false, //没有更多了
          filterParams: {
            pageNum: 1,
            pageSize: 10,
            name: undefined,
          },
          isFocus: false, //搜索框是否获取焦点
        },
      ],
      disabledVersion: false,
      tactics: {
        total: 0,
        dataList: [], //策略列表
        tacticsRowData: {}, //策略列表行数据
        activeId: null, //当前策略列选择行的id
        projectCode: undefined, //当前策略的projectCode
        switchStatus: '', //当前策略列选择行的switch状态
        btDisable: false, //规则组和规则switch是否启用
        showIcon: '1',
        loading: false,
      },
      ruleGroup: {
        total: 0,
        dataList: [], //规则组数据
        ruleGroupData: {}, //规则组行数据
        activeId: null, //当前规则组选择行的id
        projectCode: undefined, //当前规则组的projectCode
        switchStatus: '', //当前规则组列选择行的switch状态
        btDisable: false, //规则switch是否启用
        loading: false,
      },
      rule: {
        total: 0,
        dataList: [], //规则
        ruleData: {},
        activeId: null,
        loading: false,
      },
      headerClass: 'headerClass',
      drawer: {
        title: '',
        visible: false,
        width: '',
        type: '',
        status: true,
      },
      time: null,
      gutu: null,
      enterprise: null,
      decision: null,
      //策略- 测试-请求参数
      testParams: {},
      //复用弹窗-请求参数
      reuseParams: {},
      // 暂时注释 20260105
      // logRecord: {
      //   controlRecordId: null,
      //   ownershipSubject: null,
      //   currentVersion: null,
      //   projectCode: null,
      //   businessCode: null,
      //   ruleCode: null,
      //   changeType: 'DELETED', //CREATED,UPDATED,DELETED
      // },
    }
  },
  mounted() {
    this.gutu = this.hasButton('productDecision:gutu:show')
    this.enterprise = this.hasButton('productDecision:enterprise:show')
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
            filterParams: { ...defaultFilterParams },
            isFocus: false, //搜索框是否获取焦点
          }
        })
        const defaultListParams = {
          total: 0,
          dataList: [],
          activeId: null,
          loading: false,
        }
        Object.assign(this.tactics, {
          ...defaultListParams,
          tacticsRowData: {}, //策略列表行数据
          projectCode: undefined, //当前策略的projectCode
          switchStatus: '', //当前策略列选择行的switch状态
          btDisable: false, //规则组和规则switch是否启用
          showIcon: '1',
        })
        Object.assign(this.ruleGroup, {
          ...defaultListParams,
          ruleGroupData: {}, //规则组行数据
          projectCode: undefined, //当前规则组的projectCode
          switchStatus: '', //当前规则组列选择行的switch状态
          btDisable: false, //规则switch是否启用
        })
        Object.assign(this.rule, {
          ...defaultListParams,
          ruleData: {},
        })
        this.searchTacticsData(true)
      },
      deep: true,
      immediate: true,
    },
    'tactics.switchStatus': {
      handler(val) {
        if (val == '1') {
          this.tactics.btDisable = false
          if (this.ruleGroup.switchStatus != '1') {
            this.ruleGroup.btDisable = false
          }
        } else {
          this.tactics.btDisable = true
          this.ruleGroup.btDisable = true
        }
      },
      deep: true,
      immediate: true,
    },
    'ruleGroup.switchStatus': {
      handler(val) {
        if (val == '1') this.ruleGroup.btDisable = false
        else this.ruleGroup.btDisable = true
      },
      deep: true,
      immediate: true,
    },
  },
  computed: {
    ...mapState(['dataRisk', 'batchId']),
    getVersionControl() {
      let obj = this.tactics.dataList.find(
        (item) => item.id === this.tactics.activeId
      )
      if (obj) {
        return obj.versionObj ? obj.versionObj.newVersion : ''
      }
    },
    mapProObj() {
      let findObj = this.dataRisk.productList.find(
        (item) => item.id === this.dataRisk.decision.projectCode
      )
      if (findObj) {
        return findObj
      }
    },
  },
  methods: {
    isEditShow,
    isStandardDept,
    // 暂时注释 20260105
    // handleCheckLock,
    // handleLogBaseData(data) {
    //   let decision = this.dataRisk.decision
    //   let productList = this.dataRisk.productList
    //   this.logRecord.projectCode = decision.projectCode || null
    //   this.logRecord.businessCode = decision.businessCode || null
    //   this.logRecord.ruleCode = decision.ruleCode || null
    //   this.logRecord.ownershipSubject = this.mapProObj?.productName || null
    //   this.logRecord.controlRecordId = data?.controlRecordId || null
    //   this.logRecord.currentVersion = data?.versionObj?.userVersion || null
    // },
    // 点击了取消
    cancel() {
      if (this.operationInfo.type == 2) {
        if (this.operationInfo.status === '1') {
          this.operationInfo.status = '0'
          let findData = this.ruleGroup.dataList.find(
            (item) => item.id === this.operationInfo.id
          )
          findData.status = '0'
          this.ruleGroup.switchStatus = '0'
          this.ruleGroup.btDisable = true
        } else {
          this.operationInfo.status = '1'
          let findData = this.ruleGroup.dataList.find(
            (item) => item.id === this.operationInfo.id
          )
          findData.status = '1'
          this.ruleGroup.switchStatus = '1'
          this.ruleGroup.btDisable = true
        }
      } else if (this.operationInfo.type == 4) {
        if (this.operationInfo.status === '1') {
          this.operationInfo.status = '0'
          let findData = this.rule.dataList.find(
            (item) => item.id === this.operationInfo.id
          )
          findData.status = '0'
        } else {
          this.operationInfo.status = '1'
          let findData = this.rule.dataList.find(
            (item) => item.id === this.operationInfo.id
          )
          findData.status = '1'
        }
      }
    },
    // 获取更新||保留版本参数
    getqueryData() {
      return {
        ...this.dataRisk.decision,
        modelId: this.tactics.activeId,
        projectName: this.mapProObj.productName,
        personOrCompany: 'C',
        versionControl: this.getVersionControl,
        modelName: this.tactics.dataList.find(
          (item) => item.id === this.tactics.activeId
        ).name,
        championVersion: this.tactics.dataList.find(
          (item) => item.id === this.tactics.activeId
        ).versionObj.championVersion,
      }
    },
    /**
     * 获取全部的策略id
     * @returns {*[]}
     */
    async getModelIdList() {
      try {
        const res = await selectNewList({
          modelType: 1,
          ...this.decision,
        })
        // 提取 id 列表
        return res.data.map((item) => item.id) // 返回 id 列表
      } catch (error) {
        console.error('获取数据失败:', error)
        return [] // 返回空数组作为兜底
      }
    },
    // 发布版本 - 生成新版本号并发布到快照
    async publishVersion() {
      this.disabledVersion = true
      let modelIdList = await this.getModelIdList()
      try {
        const res = await versionControlFraud(this.getqueryData())
        if (res.code == 200) {
          const res2 = await releaseStrategy({
            modelIdList,
            ...this.decision,
          })
          if (res2.code == 200) {
            this.$refs.confirmDialog.visible = false
            this.disabledVersion = false
            this.$message.success(this.$t('decisionPlatform.publishVersionSuccess'))
            this.searchTacticsData(true)
          }
        }
      } catch (err) {
        this.disabledVersion = false
        console.error('发布版本失败:', err)
      }
    },
    // 发布快照 - 仅发布到快照表，不更新版本
    async publishSnapshot() {
      this.disabledVersion = true
      let modelIdList = await this.getModelIdList()
      try {
        const res = await releaseStrategy({
          modelIdList,
          ...this.decision,
        })
        if (res.code == 200) {
          this.$refs.confirmDialog.visible = false
          this.disabledVersion = false
          this.$message.success(this.$t('decisionPlatform.publishSnapshotSuccess'))
          this.searchTacticsData(true)
        }
      } catch (err) {
        this.disabledVersion = false
        console.error('发布快照失败:', err)
      }
    },

    handleOperate(data) {
      return false
    },
    handleTacticsSwitch(data, event) {
      const action =
        data.status === '1'
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
        .then((res) => {
          let form = {
            ...this.decision,
            id: data.id,
            classify: 1,
            status: data.status,
            versionControl: data.versionObj ? data.versionObj.newVersion : '',
          }
          this.handleSubmitSwitch(form)
        })
        .catch((err) => {
          if (data.status === '1') {
            data.status = '0'
            this.tactics.switchStatus = '0'
            this.tactics.btDisable = true
            this.ruleGroup.btDisable = true
          } else {
            data.status = '1'
            this.tactics.switchStatus = '1'
            this.tactics.btDisable = false
            this.ruleGroup.btDisable = false
          }
        })
    },
    handleRuleGroupSwitch(data) {
      const action =
        data.status === '1'
          ? this.$t('decisionPlatform.enable')
          : this.$t('decisionPlatform.close')
      this.$confirm(
        this.$t('decisionPlatform.enableOrDisableRuleGroup', {
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
          this.handleSubmitSwitch({
            ...this.decision,
            id: data.id,
            classify: 2,
            status: data.status,
            versionControl: data.versionControl,
          })
        })
        .catch(() => {
          if (data.status == 1) {
            data.status = 0
          } else {
            data.status = 1
          }
        })
    },
    handleRuleSwitch(data) {
      const action =
        data.status === '1'
          ? this.$t('decisionPlatform.enable')
          : this.$t('decisionPlatform.close')
      this.$confirm(
        this.$t('decisionPlatform.enableOrDisableRule', {
          action: action,
          code: data.code,
        }),
        this.$t('decisionPlatform.tip'),
        {
          confirmButtonText: this.$t('decisionPlatform.confirm'),
          cancelButtonText: this.$t('decisionPlatform.cancel'),
          type: 'warning',
        }
      )
        .then(() => {
          this.handleSubmitSwitch({
            ...this.decision,
            id: data.id,
            classify: 3,
            status: data.status,
            versionControl: data.versionControl,
          })
        })
        .catch(() => {
          if (data.status == 1) {
            data.status = 0
          } else {
            data.status = 1
          }
        })
    },
    handleSubmitSwitch(form) {
      updateState({ ...form })
        .then((res) => {
          this.$message.success(this.$t('decisionPlatform.operationSuccess'))
          switch (form.classify) {
            case 2:
              //如果是规则组加载数据，初始化规则列表参数
              this.initRuleData()
              this.searchRuleGroup(this.tactics.activeId, true)
              break
            case 3:
              this.searchRuleList(this.ruleGroup.activeId, true)
              break
            default:
              //如果是策略加载数据，初始化规则组和规则列表参数
              this.initRuleGroupData()
              this.initRuleData()
              this.searchTacticsData()
              break
          }
        })
        .catch((err) => {})
    },
    handleDelSwitch(form) {
      delAntiProjectFraud({ ...form })
        .then((res) => {
          this.$message.success(this.$t('decisionPlatform.operationSuccess'))
          this.searchTacticsData()
        })
        .catch((err) => {})
    },
    /**
     * 发布接口调用
     * @returns {ElMessageComponent}
     */
    async releaseRule() {
      // 弹出选择对话框，让用户选择发布快照还是发布版本
      this.$refs.confirmDialog.visible = true
    },
    async openDrawer(type, data, status = true) {
      // if (!(await this.handleCheckLock())) return 暂时注释 20260105
      if (data && status) {
        if (this.handleOperate(data)) return
      }
      this.drawer.type = type
      this.drawer.status = status
      switch (type) {
        case 'tactics':
          this.drawer.width = '40%'
          if (data) {
            this.tactics.tacticsRowData = JSON.parse(JSON.stringify(data))
            this.drawer.title = this.$t('decisionPlatform.editStrategy')
            if (this.$refs.addTactics) this.$refs.addTactics.clearForm()
          } else {
            this.drawer.title = this.$t('decisionPlatform.addStrategy')
            this.tactics.tacticsRowData = {}
            if (this.$refs.addTactics) {
              this.$refs.addTactics.resetForm()
            }
          }
          break
        case 'ruleGroup':
          this.drawer.width = '40%'
          if (data) {
            this.ruleGroup.ruleGroupData = JSON.parse(JSON.stringify(data))
            this.drawer.title = this.$t('decisionPlatform.editRuleGroup')
            if (this.$refs.addRuleGroup) this.$refs.addRuleGroup.clearForm()
          } else {
            this.ruleGroup.ruleGroupData = {
              modelId: this.tactics.activeId,
              name: '',
            }
            this.drawer.title = this.$t('decisionPlatform.addRuleGroup')
            if (this.$refs.addRuleGroup) this.$refs.addRuleGroup.resetForm()
          }
          break
        case 'rule':
          if (this.ruleGroup.dataList.length == 0) {
            this.$message.warning(
              this.$t('decisionPlatform.pleaseAddRuleGroupFirst')
            )
            return
          }
          this.drawer.width = '1300px'
          if (data) {
            this.drawer.title = this.$t('decisionPlatform.editRule')
            this.rule.ruleData = JSON.parse(JSON.stringify(data))
          } else {
            this.drawer.title = this.$t('decisionPlatform.newRule')
            this.rule.ruleData = {}
          }
          break
        case 'testModel':
          if (data.status != 1) {
            return this.$message.warning(
              this.$t('decisionPlatform.pleaseEnableModelFirst')
            )
          }
          this.drawer.title = data.name
          this.drawer.type = 'testModel'
          this.drawer.width = '75%'
          this.testParams = {
            modelId: data.id,
            productCode: this.mapProObj.id,
            productName: this.mapProObj.productName,
            businessCode: this.dataRisk.decision.businessCode,
            ruleCode: this.decision.ruleCode,
            modelName: data.name,
            businessName: this.dataRisk.decision.businessName,
            modelRemark: data.descr,
          }
          break
        case 'reuse':
          const titleKey =
            data.dataSources === 'ruleGroup'
              ? 'decisionPlatform.standardRuleGroupReuse'
              : data.dataSources === 'rule'
              ? 'decisionPlatform.standardRuleReuse'
              : 'decisionPlatform.standardStrategyReuse'
          this.drawer.title = this.$t(titleKey)
          this.drawer.type = `${data.dataSources}Reuse`
          this.drawer.width = 'auto'
          this.reuseParams = {
            dataSources: data.dataSources,
            productCode: this.mapProObj.id,
            productName: this.mapProObj.productName,
            businessCode: this.dataRisk.decision.businessCode,
            ruleCode: this.decision.ruleCode,
            businessName: this.dataRisk.decision.businessName,
            tacticsId:
              data.dataSources !== 'tactics'
                ? this.mine['tactics'].activeId
                : undefined,
            ruleGroupId:
              data.dataSources !== 'tactics'
                ? this.mine['ruleGroup'].activeId
                : undefined,
          }
          break
      }
      this.drawer.visible = true
    },
    async delTactics(data) {
      // if (!(await this.handleCheckLock())) return 暂时注释 20260105
      if (this.handleOperate(data)) return
      this.$confirm(
        this.$t('decisionPlatform.deleteStrategyConfirm', { name: data.name }),
        this.$t('decisionPlatform.tip'),
        {
          confirmButtonText: this.$t('decisionPlatform.confirm'),
          cancelButtonText: this.$t('decisionPlatform.cancel'),
          type: 'warning',
        }
      )
        .then(() => {
          // 暂时注释 20260105
          // this.handleLogBaseData(data)
          // logChange({
          //   ...this.logRecord,
          //   batchId: this.batchId,
          //   changeDetails: [
          //     {
          //       targetId: data.id,
          //       targetType: 'POLICY',
          //       fieldName: '_ENTITY_DELETE_',
          //     },
          //   ],
          // })
          //   .then((result) => {
          //     if (result.code == 200) {
          //       const API =
          //         !isStandardDept() && data.deptFlag == 1
          //           ? deletePolicyGroup
          //           : delTactics
          //       const paramsData =
          //         !isStandardDept() && data.deptFlag == 1
          //           ? {
          //               buildProjectCode: this.decision.projectCode,
          //               buildBusinessCode: this.decision.businessCode,
          //               buildRuleCode: this.decision.ruleCode,
          //               parentCardId: data.id,
          //               // 1为策略列表数据 2为规则组列表数据 3为规则列表数据
          //               moudleId: 1,
          //               versionControl: data.versionObj
          //                 ? data.versionObj.userVersion
          //                 : '',
          //             }
          //           : {
          //               ...this.decision,
          //               id: data.id,
          //               versionControl: this.getVersionControl,
          //             }
          //       API(paramsData)
          //         .then((res) => {
          //           if (API == delTactics && !res.data) {
          //             this.$message.warning(res.msg)
          //             return
          //           }
          //           this.$message.success(this.$t('decisionPlatform.operationSuccess'))
          //           this.initRuleGroupData()
          //           this.initRuleData()
          //           this.searchTacticsData(true)
          //         })
          //         .catch((err) => {})
          //     }
          //   })
          //   .catch(() => {})

          /**
           * 非标准部门下自建产品 & 标准部门下的产品：使用 delTactics
           * 非标准部门下标准产品：使用 deletePolicyGroup
           */
          const API =
            !isStandardDept() && data.deptFlag == 1
              ? deletePolicyGroup
              : delTactics
          const paramsData =
            !isStandardDept() && data.deptFlag == 1
              ? {
                  buildProjectCode: this.decision.projectCode,
                  buildBusinessCode: this.decision.businessCode,
                  buildRuleCode: this.decision.ruleCode,
                  parentCardId: data.id,
                  // 1为策略列表数据 2为规则组列表数据 3为规则列表数据
                  moudleId: 1,
                  versionControl: data.versionObj
                    ? data.versionObj.userVersion
                    : '',
                }
              : {
                  ...this.decision,
                  id: data.id,
                  versionControl: this.getVersionControl,
                }
          API(paramsData)
            .then((res) => {
              if (API == delTactics && !res.data) {
                this.$message.warning(res.msg)
                return
              }
              this.$message.success(
                this.$t('decisionPlatform.operationSuccess')
              )
              this.initRuleGroupData()
              this.initRuleData()
              this.searchTacticsData(true)
            })
            .catch((err) => {})
        })
        .catch((err) => {})
    },
    /**
     * 删除规则组数据
     * @param data
     */
    delRuleGroup(data) {
      if (this.handleOperate(data)) return
      this.$confirm(
        this.$t('decisionPlatform.deleteRuleGroupConfirm', { name: data.name }),
        this.$t('decisionPlatform.tip'),
        {
          confirmButtonText: this.$t('decisionPlatform.confirm'),
          cancelButtonText: this.$t('decisionPlatform.cancel'),
          type: 'warning',
        }
      )
        .then(() => {
          // 暂时注释 20260105
          // this.handleLogBaseData(data)
          // logChange({
          //   ...this.logRecord,
          //   batchId: this.batchId,
          //   parentId: this.tactics.activeId,
          //   changeDetails: [
          //     {
          //       targetId: data.id,
          //       targetType: 'RULE_GROUP',
          //       fieldName: '_ENTITY_DELETE_',
          //     },
          //   ],
          // })
          //   .then((result) => {
          //     if (result.code == 200) {
          //       const API =
          //         !isStandardDept() && data.deptFlag == 1
          //           ? deletePolicyGroup
          //           : delRuleGroup
          //       const paramsData =
          //         !isStandardDept() && data.deptFlag == 1
          //           ? {
          //               ruleId: this.tactics.activeId,
          //               buildProjectCode: this.decision.projectCode,
          //               buildBusinessCode: this.decision.businessCode,
          //               buildRuleCode: this.decision.ruleCode,
          //               parentCardId: data.id,
          //               // 1为策略列表数据 2为规则组列表数据 3为规则列表数据
          //               moudleId: 2,
          //               versionControl: data.versionControl,
          //             }
          //           : {
          //               ...this.decision,
          //               id: data.id,
          //               versionControl: data.versionControl,
          //             }
          //       API(paramsData)
          //         .then((res) => {
          //           this.$message.success(this.$t('decisionPlatform.operationSuccess'))
          //           this.initRuleData()
          //           this.searchRuleGroup(this.tactics.activeId, true)
          //         })
          //         .catch((err) => {})
          //     }
          //   })
          //   .catch((err) => {})

          const API =
            !isStandardDept() && data.deptFlag == 1
              ? deletePolicyGroup
              : delRuleGroup
          const paramsData =
            !isStandardDept() && data.deptFlag == 1
              ? {
                  ruleId: this.tactics.activeId,
                  buildProjectCode: this.decision.projectCode,
                  buildBusinessCode: this.decision.businessCode,
                  buildRuleCode: this.decision.ruleCode,
                  parentCardId: data.id,
                  // 1为策略列表数据 2为规则组列表数据 3为规则列表数据
                  moudleId: 2,
                  versionControl: data.versionControl,
                }
              : {
                  ...this.decision,
                  id: data.id,
                  versionControl: data.versionControl,
                }
          API(paramsData)
            .then((res) => {
              this.$message.success(
                this.$t('decisionPlatform.operationSuccess')
              )
              this.initRuleData()
              this.searchRuleGroup(this.tactics.activeId, true)
            })
            .catch((err) => {})
        })
        .catch(() => {})
    },
    delRule(data) {
      if (this.handleOperate(data)) return
      this.$confirm(
        this.$t('decisionPlatform.deleteRuleConfirm', { code: data.code }),
        this.$t('decisionPlatform.tip'),
        {
          confirmButtonText: this.$t('decisionPlatform.confirm'),
          cancelButtonText: this.$t('decisionPlatform.cancel'),
          type: 'warning',
        }
      )
        .then(() => {
          // 暂时注释 20260105
          // this.handleLogBaseData(data)
          // logChange({
          //   ...this.logRecord,
          //   batchId: this.batchId,
          //   parentId: this.ruleGroup.activeId,
          //   changeDetails: [
          //     {
          //       targetId: data.id,
          //       targetType: 'RULE',
          //       fieldName: '_ENTITY_DELETE_',
          //     },
          //   ],
          // })
          //   .then((result) => {
          //     if (result.code == 200) {
          //       const API =
          //         !isStandardDept() && data.deptFlag == 1
          //           ? deletePolicyGroup
          //           : delRuleRecord
          //       const paramsData =
          //         !isStandardDept() && data.deptFlag == 1
          //           ? {
          //               ruleId: this.tactics.activeId,
          //               groupId: this.ruleGroup.activeId,
          //               buildProjectCode: this.decision.projectCode,
          //               buildBusinessCode: this.decision.businessCode,
          //               buildRuleCode: this.decision.ruleCode,
          //               parentCardId: data.id,
          //               // 1为策略列表数据 2为规则组列表数据 3为规则列表数据
          //               moudleId: 3,
          //               versionControl: data.versionControl,
          //             }
          //           : {
          //               ...this.decision,
          //               id: data.codeId,
          //               // versionControl: this.getVersionControl,
          //               versionControl: data.versionControl,
          //             }
          //       API(paramsData)
          //         .then((res) => {
          //           this.$message.success(this.$t('decisionPlatform.operationSuccess'))
          //           this.searchRuleList(this.ruleGroup.activeId, true)
          //         })
          //         .catch((err) => {})
          //     }
          //   })
          //   .catch((err) => {})

          const API =
            !isStandardDept() && data.deptFlag == 1
              ? deletePolicyGroup
              : delRuleRecord
          const paramsData =
            !isStandardDept() && data.deptFlag == 1
              ? {
                  ruleId: this.tactics.activeId,
                  groupId: this.ruleGroup.activeId,
                  buildProjectCode: this.decision.projectCode,
                  buildBusinessCode: this.decision.businessCode,
                  buildRuleCode: this.decision.ruleCode,
                  parentCardId: data.id,
                  // 1为策略列表数据 2为规则组列表数据 3为规则列表数据
                  moudleId: 3,
                  versionControl: data.versionControl,
                }
              : {
                  ...this.decision,
                  id: data.codeId,
                  // versionControl: this.getVersionControl,
                  versionControl: data.versionControl,
                }
          API(paramsData)
            .then((res) => {
              this.$message.success(
                this.$t('decisionPlatform.operationSuccess')
              )
              this.searchRuleList(this.ruleGroup.activeId, true)
            })
            .catch((err) => {})
        })
        .catch(() => {})
    },
    handleClose(index) {
      this.drawer.visible = false
      switch (index) {
        case 0:
          this.searchTacticsData(true)
          break
        case 1:
          this.searchRuleGroup(this.tactics.activeId, true)
          break
        case 2:
          this.searchRuleList(this.ruleGroup.activeId, true)
          break
      }

      if (this.$refs.reuseRef) {
        this.$refs.reuseRef.onreset()
      }
      if (this.drawer.type === 'testModel') {
        this.$refs.testModelRef.clearTimer()
      }
    },
    /**
     * 获取策略列表数据
     * @param isFirst 是否为首次加载
     */
    searchTacticsData(isFirst) {
      let filterParams = this.cardList[0].filterParams
      if (isFirst) {
        this.tactics.loading = true
        this.cardList[0].loadingMore = false
        this.cardList[0].noMore = false
        filterParams.pageNum = 1
        this.tactics.dataList = []
      }
      this.ruleGroup.dataList = []
      this.rule.dataList = []
      selectNewList({
        ...filterParams,
        modelType: 1,
        ...this.decision,
      })
        .then(async (res) => {
          if (res.code == 200) {
            const { list, pages } = res.data
            list.forEach((item) => {
              /**
               * 非标准部门下自己新建产品 & 标准部门下的产品
               */
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
            this.tactics.dataList =
              this.cardList[0].filterParams.pageNum === 1
                ? list
                : this.tactics.dataList.concat(list)
            this.tactics.total = pages
            this.cardList[0].loadingMore = false
            if (!this.tactics.total) {
              this.cardList[0].noMore = false
            }
            if (
              this.cardList[0].filterParams.pageNum >= this.tactics.total &&
              this.tactics.total
            ) {
              this.cardList[0].noMore = true
            }

            if (isFirst) {
              setTimeout(() => {
                let parallelIndex = this.tactics.dataList.findIndex(
                  (item) => item.id === this.tactics.activeId
                )
                if (parallelIndex === -1) {
                  if (this.tactics.dataList.length) {
                    this.searchRuleGroup(this.tactics.dataList[0], true)
                    this.tactics.activeId = this.tactics.dataList[0].id
                    this.tactics.projectCode =
                      this.tactics.dataList[0].projectCode
                  }
                } else {
                  let clickRowData = this.tactics.dataList[parallelIndex]
                  this.tactics.projectCode = clickRowData.projectCode
                  this.searchRuleGroup(this.tactics.activeId, true)
                  this.tactics.switchStatus = clickRowData.status
                }
              }, 200)
            }
          }
          this.tactics.loading = false
        })
        .catch((err) => {
          this.tactics.loading = false
          this.cardList[0].loadingMore = false
        })
    },
    /**
     * 获取规则组数据
     * @param row
     * @param isFirst 是否为首次加载
     */
    searchRuleGroup(row, isFirst) {
      let filterParams = this.cardList[1].filterParams
      if (isFirst) {
        this.ruleGroup.loading = true
        this.ruleGroup.dataList = []
        this.cardList[1].loadingMore = false
        this.cardList[1].noMore = false
        filterParams.pageNum = 1

        this.cardList[2].loadingMore = false
        this.cardList[2].noMore = false
        filterParams.pageNum = 1
        this.rule.dataList = []
      }
      let search = {
        ...filterParams,
        modelId: undefined,
        modelType: 1,
        ...this.decision,
        projectCode: this.tactics.projectCode,
      }
      if (typeof row == 'object') {
        search.modelId = row.id
        search.projectCode = row.projectCode
        this.tactics.activeId = row.id
        this.tactics.projectCode = row.projectCode
        this.tactics.switchStatus = row.status
        this.tactics.showIcon = this.handleOperate(row)
      } else {
        search.modelId = row
        this.tactics.activeId = row
      }

      searchGroupNewList(search)
        .then((res) => {
          if (res.code == 200) {
            const { list, total } = res.data
            this.ruleGroup.dataList =
              this.cardList[1].filterParams.pageNum === 1
                ? list
                : this.ruleGroup.dataList.concat(list)
            this.ruleGroup.total = total
            this.cardList[1].loadingMore = false
            if (!this.ruleGroup.total) {
              this.cardList[1].noMore = false
            }
            if (
              this.ruleGroup.total &&
              this.cardList[1].filterParams.pageNum *
                this.cardList[1].filterParams.pageSize >=
                this.ruleGroup.total
            ) {
              this.cardList[1].noMore = true
            }

            if (isFirst) {
              setTimeout(() => {
                let parallelIndex = this.ruleGroup.dataList.findIndex(
                  (item) => item.id == this.ruleGroup.activeId
                )
                if (parallelIndex === -1) {
                  if (this.ruleGroup.dataList.length) {
                    this.searchRuleList(this.ruleGroup.dataList[0], true)
                    this.ruleGroup.activeId = this.ruleGroup.dataList[0].id
                    this.ruleGroup.projectCode =
                      this.ruleGroup.dataList[0].projectCode
                  }
                } else {
                  let chickRowData = this.ruleGroup.dataList[parallelIndex]
                  this.ruleGroup.projectCode = chickRowData.projectCode
                  this.searchRuleList(this.ruleGroup.activeId, true)
                  this.ruleGroup.switchStatus = chickRowData.status
                }
              }, 200)
            }
          }
          this.ruleGroup.loading = false
        })
        .catch((err) => {
          this.ruleGroup.loading = false
          this.cardList[1].loadingMore = false
        })
    },
    /**
     * 获取规则数据
     * @param row
     * @param isFirst 是否为首次加载
     */
    searchRuleList(row, isFirst) {
      let filterParams = this.cardList[2].filterParams

      if (isFirst) {
        this.rule.loading = true
        this.cardList[2].loadingMore = false
        this.cardList[2].noMore = false
        filterParams.pageNum = 1
        this.rule.dataList = []
      }

      let search = {
        pageNum: filterParams.pageNum,
        pageSize: filterParams.pageSize,
        code: filterParams.name,
        modelId: this.tactics.activeId, //策略选中id
        groupId: undefined, //规则组选中id
        modelType: 1,
        ...this.decision,
        projectCode: this.ruleGroup.projectCode,
      }
      if (typeof row == 'object') {
        search.modelId = row.modelId || this.tactics.activeId
        search.groupId = row.id
        search.projectCode = row.projectCode
        this.ruleGroup.activeId = row.id
        this.ruleGroup.projectCode = row.projectCode
        this.ruleGroup.switchStatus = row.status
      } else {
        search.groupId = row
        this.ruleGroup.activeId = row
      }
      searchCodeNewList(search)
        .then((res) => {
          if (res.code == 200) {
            const { list, total } = res.data
            this.rule.dataList =
              this.cardList[2].filterParams.pageNum === 1
                ? list
                : this.rule.dataList.concat(list)
            this.rule.total = total
            this.cardList[2].loadingMore = false
            if (!this.rule.total) {
              this.cardList[2].noMore = false
            }
            if (
              this.rule.total &&
              this.cardList[2].filterParams.pageNum *
                this.cardList[2].filterParams.pageSize >=
                this.rule.total
            ) {
              this.cardList[2].noMore = true
            }
            let parallelIndex = this.rule.dataList.findIndex(
              (item) => item.id == this.rule.activeId
            )
            if (parallelIndex !== -1) {
              this.rule.activeId = this.rule.dataList[index].id
            }
          }
          this.rule.loading = false
        })
        .catch((err) => {
          this.rule.loading = false
          this.cardList[2].loadingMore = false
        })
    },
    handleRuleClick(row) {
      this.rule.activeId = row.id
    },
    /**
     * 滚动加载列表数据
     * @param objName 值为：tactics，ruleGroup，rule
     * @param cardIndex
     */
    viewLoad(objName, cardIndex) {
      if (!this[objName].total) return
      if (!this.cardList[cardIndex].loadingMore && !this[objName].loading) {
        if (
          this[objName].total &&
          this.cardList[cardIndex].filterParams.pageNum *
            this.cardList[cardIndex].filterParams.pageSize >=
            this[objName].total
        ) {
          this.cardList[cardIndex].noMore = true
        } else {
          this.cardList[cardIndex].loadingMore = true
          this.cardList[cardIndex].filterParams.pageNum++
          //	调用接口
          switch (objName) {
            case 'tactics':
              this.searchTacticsData()
              break
            case 'ruleGroup':
              this.searchRuleGroup(this.tactics.activeId)
              break
            case 'rule':
              this.searchRuleList(this.ruleGroup.activeId)
              break
          }
        }
      }
    },
    /**
     * 初始化规则组和规则列表和请求参数
     */
    initRuleGroupData() {
      //如果是策略加载数据，初始化规则组和规则列表参数
      this.ruleGroup.total = 0
      this.ruleGroup.dataList = []
      this.cardList[1].loadingMore = false
      this.cardList[1].noMore = false
      this.cardList[1].filterParams.pageNum = 1
    },
    /**
     * 初始化规则列表和请求参数
     */
    initRuleData() {
      this.rule.total = 0
      this.rule.dataList = []
      this.cardList[2].loadingMore = false
      this.cardList[2].noMore = false
      this.cardList[2].filterParams.pageNum = 1
    },
    /**
     * 搜索框搜索
     * @param e 搜索值
     * @param cardIndex 卡片下标
     */
    onsearch(e, cardIndex) {
      this.cardList[cardIndex].loadingMore = false
      this.cardList[cardIndex].noMore = false
      this.cardList[cardIndex].filterParams.pageNum = 1
      this.onBlur(this.cardList[cardIndex].filterParams, cardIndex)
      switch (cardIndex) {
        case 0:
          this.tactics.total = 0
          //策略初始化，同时初始化规则组和规则
          this.initRuleGroupData()
          this.initRuleData()
          this.searchTacticsData(true)
          break
        case 1:
          this.ruleGroup.total = 0
          //规则组初始化，同时初始化规则
          this.initRuleData()
          this.searchRuleGroup(this.tactics.activeId, true)
          break
        case 2:
          this.rule.total = 0
          this.searchRuleList(this.ruleGroup.activeId, true)
          break
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
      // if (this.$refs.importButtonRef && this.$refs.importButtonRef[cardIndex]) {
      //   this.$refs.importButtonRef[cardIndex].style.background = 'transparent'
      //   this.$refs.importButtonRef[cardIndex].style.padding = 0
      //   this.$refs.importButtonTextRef[cardIndex].style.fontSize = 0
      //   this.$refs.importButtonTextRef[cardIndex].style.opacity = 0
      // }
    },
    /**
     * 搜索框失去焦点
     * @param filterItem
     * @param cardIndex
     */
    onBlur(filterItem, cardIndex) {
      if (filterItem.name || filterItem.code) {
        this.onFocus(filterItem, cardIndex)
      } else {
        this.cardList[cardIndex].isFocus = false
        // this.$nextTick(() => {
        //   if (this.$refs.copyButtonRef && this.$refs.copyButtonRef[cardIndex]) {
        //     this.$refs.copyButtonRef[cardIndex].style.background = '#e1f3ed'
        //     this.$refs.copyButtonRef[cardIndex].style.padding = '10px 15px'
        //     this.$refs.copyButtonTextRef[cardIndex].style.fontSize = '14px'
        //     this.$refs.copyButtonTextRef[cardIndex].style.opacity = 1
        //   }
        //   if (this.$refs.addButtonRef && this.$refs.addButtonRef[cardIndex]) {
        //     this.$refs.addButtonRef[cardIndex].style.background = '#fbf0e4'
        //     this.$refs.addButtonRef[cardIndex].style.padding = '10px 15px'
        //     this.$refs.addButtonTextRef[cardIndex].style.fontSize = '14px'
        //     this.$refs.addButtonTextRef[cardIndex].style.opacity = 1
        //   }
        //   if (
        //     this.$refs.importButtonRef &&
        //     this.$refs.importButtonRef[cardIndex]
        //   ) {
        //     this.$refs.importButtonRef[cardIndex].style.background = '#e8effa'
        //     this.$refs.importButtonRef[cardIndex].style.padding = '10px 15px'
        //     this.$refs.importButtonTextRef[cardIndex].style.fontSize = '14px'
        //     this.$refs.importButtonTextRef[cardIndex].style.opacity = 1
        //   }
        // })
      }
    },

    /**
     * @description:导入策略-文件上传之前校验
     * @param {*} file
     * @return {*}
     */
    beforeUpload(file) {
      const { name, size } = file
      const accept = ['json']
      if (
        !accept.some(
          (i) => name.slice(name.lastIndexOf('.') + 1).toLowerCase() == i
        )
      ) {
        setTimeout(() => {
          this.$message.warning(
            this.$t('decisionPlatform.pleaseUploadJsonFile')
          )
        }, 0)
        return false
      }
    },
    uploadFile({ file }) {
      importRuleJSON({
        file,
        productId: this.decision.projectCode,
        businessId: this.decision.businessCode,
        modelType: this.decision.ruleCode,
      }).then((res) => {
        if (res.code === 200) {
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
          this.searchTacticsData(true)
          if (this.$refs.uploadJSONRef[0])
            this.$refs.uploadJSONRef[0].clearFiles()

          this.$message.success(
            this.$t('decisionPlatform.importStrategySuccess')
          )
        }
      })
    },

    /**
     * 导出-策略
     * @param item
     */
    exportTactics(item) {
      exportRuleJSON(item.id)
        .then((res) => {
          // 下载 JSON 数据
          this.downloadJSON(res, `strategy_${item.id}.json`)
        })
        .catch((err) => {
          console.error('导出失败:', err)
        })
    },

    /**
     * 下载 JSON 数据为文件
     * @param {Object} data - JSON 数据
     * @param {String} filename - 文件名
     */
    downloadJSON(data, filename = 'data.json') {
      try {
        // 将 JSON 数据转换为字符串，并格式化
        const jsonString = JSON.stringify(data, null, 2)

        // 创建 Blob 对象
        const blob = new Blob([jsonString], { type: 'application/json' })

        // 创建下载链接
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = filename
        link.style.display = 'none'

        // 添加到页面并触发下载
        document.body.appendChild(link)
        link.click()

        // 清理
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)

        this.$message.success(this.$t('decisionPlatform.exportStrategySuccess'))
      } catch (error) {
        console.error('下载失败:', error)
        this.$message.error(this.$t('decisionPlatform.exportStrategyFailed'))
      }
    },
  },
}
</script>

<style lang="less" scoped>
.version-box {
  font-size: 14px;
  font-family: PingFang SC, PingFang SC;
  font-weight: 400;
  color: var(--text-color-tertiary);

  div {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
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

    .card {
      width: 32.5%;
      height: 100%;
      padding: 0px 20px 20px 20px;
      background-color: var(--decision-model-card-bg);
      border: none;

      /deep/ .el-loading-mask {
        top: 80px;
      }

      .top {
        width: 100%;
        height: 80px;
        border-bottom: 1px rgba(#000, 0.08) solid;
        box-sizing: border-box;
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 14px;

        .title {
          flex: 1;
          // width: 100%;
          height: 100%;
          display: flex;
          justify-content: center;
          align-items: center;
          // // font-family: "PingFang SC-Medium", PingFang SC;
          font-size: 18px;
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
                // border-color: transparent;
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
          .importBtn {
            margin-left: 10px;
            .operateBtn {
              border-radius: 6px;
              display: flex;
              align-items: center;
              padding: 10px;
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

          .importBtn {
            .operateBtn {
              background: var(--decision-model-card-operate-import-btn-bg);
              border: var(--decision-model-card-operate-import-btn-border);
              color: #4d96ff;
            }

            img {
              width: 18px;
              height: 18px;
              margin-right: 5px;
            }
          }
        }
      }

      .dataContent {
        height: calc(100% - 80px);
        overflow-y: auto;

        .dataCard {
          margin: 0 0 14px;
          min-height: 104px;
          background-color: var(--bg-color);
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
            width: calc(100% - 100px);

            &.sort-detail {
              width: calc(100% - 124px);
            }

            .name {
              display: flex;
              align-items: center;
              width: 100%;

              > span {
                font-size: 16px;
                font-weight: 500;
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

            .descr {
              margin-top: 4px;
              display: block;
              height: calc(100% - 24px);
              font-size: 14px;
              font-weight: 400;
              color: var(--text-color-tertiary);
              text-overflow: ellipsis;
              -webkit-line-clamp: 3;
              line-clamp: 3;
              -webkit-box-orient: vertical;
              overflow: hidden;
              display: -webkit-box;
              line-height: 22px;
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
                padding: 0px 10px;

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

            .view_btnList {
              justify-content: flex-end;
              // background: linear-gradient(45deg, orange, red);
            }

            .export-button {
              display: flex;
              justify-content: flex-end;
              margin-left: 10px;

              .el-button {
                height: 24px;
                line-height: 24px;
                font-size: 10px;
                padding: 0 15px;
                border-radius: 12px;
                background: var(--bg-color);
                color: var(--text-color-secondary);
              }
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
          // background: linear-gradient(45deg, yellow 10%, green 20%);
        }

        .activeCard {
          background: var(--decision-model-card-active-bg) !important;
          top: 0;
          left: 0;
          right: 0;
        }
      }

      /deep/ .el-table {
        .el-table__header,
        .el-table__body {
          padding: 0px;

          .el-switch {
            z-index: 999;
          }
        }

        .el-table__header {
          height: 62px;

          .cell {
            font-size: 16px;
            font-weight: 500 !important;
            // font-family: "PingFang SC-Medium", "PingFang SC" !important;
            color: rgba(#000, 0.85);
          }
        }

        .el-table__body tr.current-row > td {
          background-color: #dadff6 !important;
        }

        .el-table__cell {
          background-color: transparent;
        }

        .el-table__fixed-header-wrapper {
          padding: 0px;
        }

        .el-table__fixed-body-wrapper {
          padding: 0px;
        }

        .cell {
          font-size: 14px;
          // font-family: PingFang SC !important;
          color: rgba(#000, 0.85);
        }
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
