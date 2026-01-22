<template>
  <div class="preApproval_rule">
    <div class="tableCard">
      <template v-for="(cardItem, cardIndex) in cardList">
        <div
          class="card"
          :key="cardIndex"
          element-loading-text="数据加载中"
          v-loading="mine[cardItem.dataSources].loading"
        >
          <div class="top">
            <div class="title">{{ cardItem.cardTitle }}</div>
            <div
              class="icon"
              @click="openDrawer(cardItem.dataSources)"
              v-if="
                ['tactics'].includes(cardItem.dataSources) ||
                tactics.showIcon == '0'
              "
            >
              <img src="@/assets/images/dataRisk/add.png" />
              <span>新增</span>
            </div>
          </div>
          <div class="dataList" ref="dataList">
            <div
              class="dataContent"
              :ref="`${cardItem.dataSources}Scroll`"
              @scroll="
                (event) => {
                  handleScroll(event, cardItem.dataSources)
                }
              "
            >
              <div
                class="scroll_background"
                :style="{
                  height: `${
                    mine[cardItem.dataSources].dataList.length * 118
                  }px`,
                }"
              ></div>
              <div
                class="bufferList"
                :ref="`${cardItem.dataSources}Buffer`"
                :style="{ translate3d: `(0,${bufferListTop}px,0)` }"
              >
                <template
                  v-for="(item, index) in mine[cardItem.dataSources]
                    .visibleData"
                >
                  <div
                    class="dataCard"
                    @click="mine[cardItem.searchDataFun](item)"
                    :class="{
                      activeCard:
                        item.id == mine[cardItem.dataSources].activeId,
                      defaultModel: handleOperate(item),
                    }"
                    :key="index"
                  >
                    <div class="detail">
                      <div class="name">
                        <span>{{ item.name }}</span>
                        <el-tag class="defaultTag" v-if="item.deptFlag == '1'"
                          >默认策略
                        </el-tag>
                        <el-tag v-else type="warning" class="buildTag"
                          >自建策略
                        </el-tag>
                      </div>
                      <span class="descr">描述：{{ item.descr }}</span>
                    </div>
                    <div class="operate">
                      <div
                        v-if="handleOperate(item) && !gutu"
                        @click.stop
                        class="view_btnList"
                      >
                        <!-- <button @click="openDrawer(cardItem.dataSources, item,false)">
                          <img
                            class="operate_img"
                            src="@/assets/images/dataRisk/view.png"
                          />
                          <span class="operate_span_edit">查看</span>
                        </button> -->
                      </div>
                      <div class="operate_btnList" @click.stop v-else>
                        <button @click="openDrawer(cardItem.dataSources, item)">
                          <img
                            class="operate_img"
                            src="@/assets/images/dataRisk/edit.png"
                          />
                          <span class="operate_span_edit">编辑</span>
                        </button>
                        <button @click="mine[cardItem.delFun](item)">
                          <img
                            class="operate_img"
                            src="@/assets/images/dataRisk/del.png"
                          />
                          <span class="operate_span_del">删除</span>
                        </button>
                      </div>
                      <div @click.stop class="switch">
                        <div style="position: relative">
                          <!-- <span
                            class="switch-text"
                            :style="{[item.status == '1'?'right':'left']:'7px',color:item.status == '1'?'#fff':'#969696'}"
                          >
                            {{item.status == '1'?'使用':'禁用'}}
                          </span> -->
                          <el-switch
                            v-model="item.status"
                            active-color="#D6D3D3"
                            inactive-color="var(--primary-color)"
                            active-text="禁用"
                            inactive-text="使用"
                            active-value="0"
                            inactive-value="1"
                            @change="mine[cardItem.switchFun](item)"
                          />
                        </div>
                      </div>
                    </div>
                  </div>
                </template>
              </div>
            </div>
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
      :before-close="handleClose"
      :size="drawer.width"
    >
      <addTactics
        ref="addTactics"
        v-if="drawer.type === 'tactics'"
        :formData="tactics.tacticsRowData"
        :status="drawer.status"
        @resetStep="resetStep"
        @close="handleClose"
      ></addTactics>
      <addRuleGroup
        ref="addRuleGroup"
        v-if="drawer.type === 'ruleGroup'"
        :formData="ruleGroup.ruleGroupData"
        :status="drawer.status"
        @resetStep="resetStep"
        @close="handleClose"
      ></addRuleGroup>
      <addClassify
        v-if="drawer.type === 'rule'"
        :info="rule.ruleData"
        :selectList="rule.dataList"
        :tacticsId="tactics.activeId"
        :ruleGroupId="ruleGroup.activeId"
        :activeRow="ruleGroup.activeRow"
        :status="drawer.status"
        @resetStep="resetStep"
        @close="handleClose"
      ></addClassify>
    </el-drawer>
    <el-dialog
      :visible="dialog.visible"
      :before-close="handleDialog"
      :close-on-click-modal="false"
      :width="dialog.width"
      class="dialogRelease"
    >
      <div class="dialogTitle" slot="title">
        <!-- <img src="@/assets/images/doubt.png" /> -->
        <span>是否确认发布</span>
      </div>
      <div class="releaseContent">
        <span>分类发布系统需要2-3分钟更新，请耐心等候</span>
      </div>
      <div class="releaseBtnList">
        <el-button
          type="primary"
          :class="btnDisable ? 'btnDisable' : 'btnWarn'"
          @click="release"
          >确认
        </el-button>
        <el-button @click="handleDialog">取消</el-button>
      </div>
    </el-dialog>
    <testDataDrawer
      ref="testDrawer"
      :modelList="modelIdList"
      :outStep.sync="myStep"
    ></testDataDrawer>
  </div>
</template>

<script>
import {
  selectNewList,
  delTactics,
  delRuleGroup,
  delRuleRecord,
  searchGroupNewList,
  searchCodeNewList,
  submitAntiProjectFraud,
  delAntiProjectFraud,
} from '@/api/dataRisk/riskModel.js'
import {
  releaseStrategy,
  resetTestState,
  updateState,
} from '@/api/dataRisk/productDecision.js'
import addTactics from './components/addTactics.vue'
import AddRuleGroup from '../../components/addRuleGroup.vue'
import AddClassify from '../../components/addClassify.vue'
import TestDataDrawer from './components/testDataDrawer.vue'
import { mapState } from 'vuex'

export default {
  components: { addTactics, AddRuleGroup, AddClassify, TestDataDrawer },
  props: {
    step: {
      type: Number,
      default: 0,
    },
    /**
     * 0 银行流水尽调
     * 1 企业KYC尽调
     * 2 AI远程尽调
     * 3 企业司法尽调
     * 4 企业财务尽调
     * 5 个体KYC尽调
     * 6 支付流水尽调
     * 7 个体司法尽调
     */
    dueActive: { type: Number, default: 0 },
  },
  data() {
    return {
      mine: this,
      btnList: [
        { label: '评分', disabled: true },
        { label: '评级', disabled: true },
        { label: '额度', disabled: true },
        { label: '定价', disabled: true },
        { label: '规则', disabled: true },
        { label: '分类', disabled: true },
      ],
      activeIndex: 4,
      cardList: [
        {
          cardTitle: '策略',
          dataSources: 'tactics',
          searchDataFun: 'searchRuleGroup',
          delFun: 'delTactics',
          switchFun: 'handleTacticsSwitch',
        },
        {
          cardTitle: '规则组',
          dataSources: 'ruleGroup',
          searchDataFun: 'searchRuleList',
          delFun: 'delRuleGroup',
          switchFun: 'handleRuleGroupSwitch',
        },
        {
          cardTitle: '规则',
          dataSources: 'rule',
          searchDataFun: 'handleRuleClick',
          delFun: 'delRule',
          switchFun: 'handleRuleSwitch',
        },
      ],
      tactics: {
        dataList: [], //策略列表
        tacticsRowData: {}, //策略列表行数据
        activeId: null, //当前策略列选择行的id
        switchStatus: '', //当前策略列选择行的switch状态
        btDisable: false, //规则组和规则switch是否启用
        showIcon: '1',
        loading: false,
        scroll: 0,
        visibleData: [], //虚拟滚动
      },
      ruleGroup: {
        dataList: [], //规则组数据
        ruleGroupData: {}, //规则组行数据
        activeId: null, //当前规则组选择行的id
        switchStatus: '', //当前规则组列选择行的switch状态
        btDisable: false, //规则switch是否启用
        activeRow: {},
        loading: false,
        scroll: 0,
        visibleData: [], //虚拟滚动
      },
      rule: {
        dataList: [], //规则
        ruleData: {},
        activeId: null,
        loading: false,
        scroll: 0,
        visibleData: [], //虚拟滚动
      },

      headerClass: 'headerClass',
      drawer: {
        title: '',
        visible: false,
        width: '',
        type: '',
        status: true,
      },
      dialog: {
        title: '',
        visible: false,
        width: '',
        type: '',
      },
      btnDisable: false,
      countdown: 5,
      myStep: 0,
      time: null,
      modelIdList: [],
      bufferListTop: 0,
      maxNum: 0,
      gutu: null,
      enterprise: null,
      projectCode: null,
      ruleCode: null,
    }
  },
  created() {},
  mounted() {
    let config = this.dataRisk.config
    this.projectCode = config.projectCode
    this.ruleCode = config.ruleCode

    this.searchTacticsData()
    this.$nextTick(() => {
      //获取列表有多少条数据进行展示
      this.maxNum = Math.ceil(
        parseInt(getComputedStyle(this.$refs.dataList[0]).height) / 118
      )
    })

    this.gutu = this.hasButton('productDecision:gutu:show')
    this.enterprise = this.hasButton('productDecision:enterprise:show')
  },
  watch: {
    myStep: {
      handler(val) {
        this.$emit('update:step', val)
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
    ...mapState(['dataRisk']),
  },
  methods: {
    handleOperate(data) {
      if (data.deptFlag == '1' && this.gutu) {
        return false
      } else if (data.deptFlag == '2' && this.enterprise) {
        return false
      } else {
        return true
      }
    },

    handleScroll(event, data) {
      if (this.ticking) {
        return
      }
      this.ticking = true
      requestAnimationFrame(() => {
        this.ticking = false
      })
      const distance = event.target.scrollTop
      this.getRunData(distance, data)
    },

    getRunData(distance = null, data) {
      let list = this[data].dataList

      //滚动的总距离
      const scrollTop = distance
        ? distance
        : this.$refs[`${data}Scroll`][0].scrollTop

      //在哪个范围内不执行滚动
      // if (this.scroll_scale) {
      //   if (scrollTop > this.scroll_scale[0] && scrollTop < this.scroll_scale[1]) {
      //     return;
      //   }
      // }

      //起始索引
      let start_index = this.getStartIndex(scrollTop, list)
      start_index = start_index < 0 ? 0 : start_index
      //上屏索引
      let upper_start_index = start_index - this.maxNum
      upper_start_index = upper_start_index < 0 ? 0 : upper_start_index
      // 调整offset
      this.$refs[
        `${data}Buffer`
      ][0].style.transform = `translate3d(0,${list[upper_start_index].top}px,0)`
      //中间屏幕的元素
      const mid_list = list.slice(start_index, start_index + this.maxNum)
      // 上屏
      const upper_list = list.slice(upper_start_index, start_index)
      // 下屏元素
      let down_start_index = start_index + this.maxNum
      down_start_index =
        down_start_index > list.length - 1 ? list.length : down_start_index
      const down_list = list.slice(
        down_start_index,
        down_start_index + this.maxNum
      )
      this[data].visibleData = [...upper_list, ...mid_list, ...down_list]
    },

    getStartIndex(scrollTop, data) {
      let start = 0,
        end = data.length - 1
      while (start < end) {
        const mid = Math.floor((start + end) / 2)
        const { top, height } = data[mid]
        if (scrollTop >= top && scrollTop < top + height) {
          start = mid
          break
        } else if (scrollTop >= top + height) {
          start = mid + 1
        } else if (scrollTop < top) {
          end = mid - 1
        }
      }
      return start
    },

    handleTacticsSwitch(data, event) {
      this.$confirm(
        `是否${data.status === '1' ? '启用' : '关闭'}策略 【${data.name}】?`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      )
        .then((res) => {
          this.resetStep()
          let form = {
            id: data.id,
            classify: 1,
            status: data.status,
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
      this.$confirm(
        `是否${data.status === '1' ? '启用' : '关闭'}规则组【${data.name}】?`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      )
        .then((res) => {
          this.resetStep()
          let form = {
            id: data.id,
            classify: 2,
            status: data.status,
          }

          this.handleSubmitSwitch(form)
        })
        .catch((err) => {
          if (data.status === '1') {
            data.status = '0'
            this.ruleGroup.switchStatus = '0'
            this.ruleGroup.btDisable = true
          } else {
            data.status = '1'
            this.ruleGroup.switchStatus = '1'
            this.ruleGroup.btDisable = true
          }
        })
    },
    handleRuleSwitch(data) {
      this.$confirm(
        `是否${data.status === '1' ? '启用' : '关闭'}规则【${data.name}】?`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      )
        .then((res) => {
          this.resetStep()
          let form = {
            id: data.id,
            classify: 3,
            status: data.status,
          }
          this.handleSubmitSwitch(form)
        })
        .catch((err) => {
          if (data.status === '1') data.status = '0'
          else data.status = '1'
        })
    },
    handleSubmitSwitch(form) {
      updateState({ ...form })
        .then((res) => {
          this.$message({
            message: '操作成功',
            type: 'success',
          })
          this.searchTacticsData()
        })
        .catch((err) => {})
    },
    handleDelSwitch(form) {
      delAntiProjectFraud({ ...form })
        .then((res) => {
          this.$message({
            message: '操作成功',
            type: 'success',
          })
          this.searchTacticsData()
        })
        .catch((err) => {})
    },

    resetStep() {
      resetTestState({
        projectCode: this.projectCode,
        ruleCode: this.ruleCode,
      })
        .then((res) => {
          if (res.code == 200) {
            this.myStep = 0
            this.$emit()
          }
        })
        .catch((err) => {})
    },

    release() {
      releaseStrategy({
        modelIdList: this.modelIdList,
        projectCode: this.projectCode,
        ruleCode: this.ruleCode,
      })
        .then((res) => {
          if (res.code == 200) {
            this.$refs.testDrawer.closeDrawer()
            this.handleDialog()
            this.myStep = 0
            this.$message({
              type: 'success',
              message: '发布成功',
            })
          }
        })
        .catch((err) => {})
    },

    releaseRule() {
      if (this.myStep >= 3) {
        this.release()
      } else {
        this.dialog.type = 'release'
        this.dialog.width = '430px'
        this.btnDisable = true
        this.countdown = 5
        clearInterval(this.time)
        this.time = setInterval(() => {
          this.countdown--
          if (this.countdown < 1) {
            clearInterval(this.time)
            this.btnDisable = false
            this.countdown = 5
          }
        }, 1000)
        this.dialog.visible = true
      }
    },

    openTestDrawer() {
      this.handleDialog()
      this.$refs.testDrawer.drawer.visible = true
    },
    handleDialog() {
      this.dialog.visible = false
    },

    openDrawer(type, data, status = true) {
      if (data && status) {
        if (this.handleOperate(data)) return
      }
      this.drawer.type = type
      this.drawer.status = status
      switch (type) {
        case 'tactics':
          this.drawer.width = '500px'
          if (data) {
            this.tactics.tacticsRowData = JSON.parse(JSON.stringify(data))
            this.drawer.title = '修改策略'
            if (this.$refs.addTactics) this.$refs.addTactics.clearForm()
          } else {
            this.drawer.title = '新增策略'
            this.tactics.tacticsRowData = {}
            if (this.$refs.addTactics) {
              this.$refs.addTactics.resetForm()
            }
          }
          break
        case 'ruleGroup':
          this.drawer.width = '1000px'
          if (data) {
            this.ruleGroup.ruleGroupData = JSON.parse(JSON.stringify(data))
            this.drawer.title = '修改规则组'
            if (this.$refs.addRuleGroup) this.$refs.addRuleGroup.clearForm()
          } else {
            this.ruleGroup.ruleGroupData = {
              modelId: this.tactics.activeId,
              name: '',
            }
            this.drawer.title = '新增规则组'
            if (this.$refs.addRuleGroup) this.$refs.addRuleGroup.resetForm()
          }
          break
        case 'rule':
          if (this.ruleGroup.dataList.length == 0) {
            this.$message({
              message: '请先增加规则组',
              type: 'warning',
            })
            return
          }
          this.drawer.width = '1200px'
          this.drawer.title = '新建规则'
          if (data) {
            this.rule.ruleData = JSON.parse(JSON.stringify(data))
          } else {
            this.rule.ruleData = {}
          }
          break
      }
      this.drawer.visible = true
    },

    delTactics(data) {
      if (this.handleOperate(data)) return
      this.$confirm(`是否删除决策【${data.name}】?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then((res) => {
          delTactics(data.id)
            .then((res) => {
              this.$message({
                message: '操作成功',
                type: 'success',
              })
              this.searchTacticsData()
            })
            .catch((err) => {})
        })
        .catch((err) => {})
    },

    delRuleGroup(data) {
      if (this.handleOperate(data)) return
      this.$confirm(`是否删除规则组【${data.name}】?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then((res) => {
          delRuleGroup(data.id)
            .then((res) => {
              this.$message({
                message: '操作成功',
                type: 'success',
              })
              this.searchTacticsData()
            })
            .catch((err) => {})
        })
        .catch((err) => {})
    },

    delRule(data) {
      if (this.handleOperate(data)) return
      this.$confirm(`是否删除规则【${data.name}】?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then((res) => {
          delRuleRecord(data.id)
            .then((res) => {
              this.$message({
                message: '操作成功',
                type: 'success',
              })
              this.searchTacticsData()
            })
            .catch((err) => {})
        })
        .catch((err) => {})
    },

    handleClose() {
      this.drawer.visible = false
      this.searchTacticsData()
    },
    //获取策略列表数据
    searchTacticsData() {
      let search = {
        pageNum: 1,
        pageSize: 9999,
        modelType: 1,
        projectCode: this.projectCode,
        ruleCode: this.ruleCode,
      }
      this.tactics.loading = true
      selectNewList({ ...search })
        .then((res) => {
          if (res.code == 200) {
            if (res.rows.length > 0) {
              res.rows.forEach((item, index) => {
                this.$set(item, 'height', 118)
                this.$set(item, 'top', index * 118)
                return item
              })
              this.tactics.dataList = res.rows
              this.tactics.visibleData = res.rows.slice(0, 7)
              // if (this.tactics.activeId == null)
              //   this.searchRuleGroup(this.tactics.dataList[0])
              // else {
              //   this.searchRuleGroup(this.tactics.activeId)
              // }
              this.modelIdList = []
              res.rows.forEach((item) => {
                if (item.status == '1') {
                  this.modelIdList.push(item.id)
                }
              })
              setTimeout(() => {
                let index = this.tactics.dataList.findIndex(
                  (item) => item.id == this.tactics.activeId
                )
                if (index == -1) {
                  this.searchRuleGroup(this.tactics.dataList[0])
                  this.tactics.activeId = this.tactics.dataList[0].id
                  // this.$refs.tactics.setCurrentRow(this.tactics.dataList[0])
                } else {
                  let clickRowData = this.tactics.dataList[index]
                  this.searchRuleGroup(this.tactics.activeId)
                  // this.$refs.tactics.setCurrentRow(clickRowData)
                  this.tactics.switchStatus = clickRowData.status
                }
              }, 200)
            } else {
              this.tactics.dataList = []
              this.tactics.visibleData = []
            }
            this.tactics.loading = false
          }
        })
        .catch((err) => {})
    },

    searchRuleGroup(row) {
      let search = {
        pageNum: 1,
        pageSize: 9999,
        modelId: null,
        modelType: 1,
        projectCode: this.projectCode,
      }

      if (typeof row == 'object') {
        search.modelId = row.id
        this.tactics.activeId = row.id
        this.tactics.switchStatus = row.status
        // this.tactics.showIcon = row.flag
        this.tactics.showIcon = this.handleOperate(row)
      } else {
        search.modelId = row
        this.tactics.activeId = row
        // this.tactics.showIcon = '1'
      }
      this.ruleGroup.loading = true
      searchGroupNewList({ ...search })
        .then((res) => {
          if (res.code == 200) {
            if (res.rows.length > 0) {
              res.rows.forEach((item, index) => {
                this.$set(item, 'height', 118)
                this.$set(item, 'top', index * 118)
                return item
              })
              this.ruleGroup.dataList = res.rows
              this.ruleGroup.visibleData = res.rows.slice(0, 7)
              setTimeout(() => {
                let index = this.ruleGroup.dataList.findIndex(
                  (item) => item.id == this.ruleGroup.activeId
                )
                if (index == -1) {
                  this.searchRuleList(this.ruleGroup.dataList[0])
                  this.ruleGroup.activeId = this.ruleGroup.dataList[0].id
                  // this.$refs.ruleGroup.setCurrentRow(this.ruleGroup.dataList[0])
                } else {
                  let chickRowData = this.ruleGroup.dataList[index]
                  this.searchRuleList(this.ruleGroup.activeId)
                  // this.$refs.ruleGroup.setCurrentRow(chickRowData)
                  this.ruleGroup.switchStatus = chickRowData.status
                }
              }, 200)
            } else {
              this.ruleGroup.dataList = []
              this.ruleGroup.visibleData = []
              this.rule.dataList = []
              this.rule.visibleData = []
            }
            this.ruleGroup.loading = false
          }
        })
        .catch((err) => {})
    },

    searchRuleList(row) {
      let search = {
        pageNum: 1,
        pageSize: 9999,
        groupId: null,
        modelType: 1,
        projectCode: this.projectCode,
      }

      if (typeof row == 'object') {
        search.groupId = row.id
        this.ruleGroup.activeId = row.id
        this.ruleGroup.switchStatus = row.status
        this.ruleGroup.activeRow = row
      } else {
        search.groupId = row
        this.ruleGroup.activeId = row
      }
      this.rule.loading = true
      searchCodeNewList({ ...search })
        .then((res) => {
          if (res.code == 200) {
            this.rule.dataList = []
            this.rule.visibleData = []
            res.rows.forEach((item, index) => {
              this.$set(item, 'height', 118)
              this.$set(item, 'top', index * 118)
              return item
            })
            this.rule.dataList = res.rows
            this.rule.visibleData = res.rows.slice(0, 7)

            if (this.rule.activeId != null) {
              let index = this.rule.dataList.findIndex(
                (item) => item.id == this.rule.activeId
              )
              if (index != -1) {
                this.rule.activeId = this.rule.dataList[index].id
              }
            } else {
            }

            this.rule.loading = false
          }
        })
        .catch((err) => {})
    },
    handleRuleClick(row) {
      this.rule.activeId = row.id
    },
  },
}
</script>

<style lang="less" scoped>
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
      background-color: rgba(#000, 0.02);
      border: none;

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
          font-family: 'PingFang SC-Medium', PingFang SC;
          font-size: 18px;
          font-weight: 500;
          color: rgba(0, 0, 0, 0.85);
        }

        .icon {
          width: 82px;
          height: 40px;
          // position: absolute;
          right: 0px;
          top: 0px;
          display: flex;
          align-items: center;
          justify-content: center;
          background-color: rgba(#ff8f1f, 0.1);
          color: #ff8f1f;
          font-size: 14px;
          font-family: 'PingFang SC Regular';
          border-radius: 6px;
          cursor: pointer;

          > img {
            width: 20px;
            height: 20px;
            margin-right: 5px;
          }
        }
      }

      .dataList {
        width: 100%;
        height: calc(100% - 80px);
        overflow: hidden;

        .dataContent {
          // width: calc(100% + 16px);
          // height: 100%;
          // overflow-y: auto;
          // // position: absolute;
          // position: relative;
          position: absolute;
          left: 0;
          right: 0;
          bottom: 0;
          top: 80px;
          overflow-y: scroll;

          .scroll_background {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            z-index: -1;
          }

          .bufferList {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            padding: 20px;

            .dataCard {
              width: 100%;
              height: 104px;
              background-color: #fff;
              margin-bottom: 14px;
              border-radius: 4px;
              padding: 14px;
              display: flex;
              justify-content: space-between;
              box-sizing: border-box;
              cursor: pointer;

              .detail {
                height: 100%;
                display: flex;
                flex-direction: column;
                width: calc(100% - 155px);

                .name {
                  display: flex;
                  align-items: center;
                  width: 100%;

                  > span {
                    max-width: calc(100% - 95px);
                    font-size: 16px;
                    font-family: 'PingFang SC-Medium', PingFang SC;
                    font-weight: 500;
                    color: rgba(0, 0, 0, 0.85);
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                    margin-bottom: 4px;
                    display: block;
                    margin-right: 5px;
                  }

                  /deep/ .el-tag {
                    height: 20px;
                    font-size: 12px;
                    min-width: 66px;
                    display: flex;
                    align-items: center;
                  }

                  .defaultTag {
                    color: var(--primary-color) !important;
                  }

                  .buildTag {
                    color: #ff8f1f !important;
                  }
                }

                .descr {
                  display: block;
                  height: calc(100% - 24px);
                  font-size: 14px;
                  font-family: 'PingFang SC Regular', PingFang SC;
                  font-weight: 400;
                  color: rgba(#000, 0.6);
                  text-overflow: ellipsis;
                  -webkit-line-clamp: 2;
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
                z-index: 999;

                .operate_btnList,
                .view_btnList {
                  display: flex;

                  > button {
                    width: 68px;
                    height: 32px;
                    // background-color: rgba(#fa5151, 0.06);
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
                      font-family: 'PingFang SC Regular';
                    }

                    .operate_span_edit {
                      color: #4d96ff;
                    }

                    .operate_span_del {
                      color: #fa5853;
                    }
                  }

                  > button:nth-child(1) {
                    background-color: rgba(#d9ebfa);
                  }

                  > button:nth-child(2) {
                    background-color: rgba(#fa5151, 0.06);
                  }
                }

                .operate_btnList {
                  justify-content: space-between;
                }

                .view_btnList {
                  justify-content: flex-end;
                  // background: linear-gradient(45deg, orange, red);
                }

                .switch {
                  width: 100%;
                  display: flex;
                  justify-content: flex-end;
                  position: relative;

                  // .switch-text {
                  //   width: 20px;
                  //   position: absolute;
                  //   font-size: 10px;
                  //   top: 5px;
                  //   z-index: 999;
                  // }

                  // /deep/.el-switch__core {
                  //   width: 50px !important;
                  // }

                  /deep/ .el-switch {
                    .el-switch__core {
                      width: 50px !important;
                      height: 24px;
                      border-radius: 12px;

                      &:after {
                        left: 4px;
                        top: 3px;
                      }
                    }

                    &.el-switch {
                      &.is-checked {
                        .el-switch__core {
                          &:after {
                            margin-left: -20px;
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
          }

          .defaultModel {
            background: linear-gradient(135deg, #eef3ff 20%, #fff 60%);
            // background: linear-gradient(45deg, yellow 10%, green 20%);
          }

          .activeCard {
            background: rgba(#58a5f1, 0.1) !important;
            top: 0;
            left: 0;
            right: 0;
          }
        }

        .noScroll {
          width: calc(100% + 21px);
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
            font-family: 'PingFang SC-Medium', 'PingFang SC' !important;
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
          font-family: PingFang SC !important;
          color: rgba(#000, 0.85);
        }
      }
    }
  }
}

.nameDescr {
  display: flex;
  flex-direction: column;
  width: 100%;
  cursor: pointer;

  .name {
    font-size: 16px;
    font-family: 'PingFang SC-Medium', PingFang SC;
    font-weight: 500;
    color: rgba(0, 0, 0, 0.85);
  }

  .descr {
    font-size: 12px;
    font-family: 'PingFang SC Regular', PingFang SC;
    font-weight: 400;
    color: rgba(#000, 0.6);
  }

  .name,
  .descr {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.tableIcon {
  width: 100%;
  height: 100%;
  display: flex;
  align-content: center;
  justify-content: center;
  box-sizing: border-box;

  > img {
    width: 24px;
    height: 24px;
    cursor: pointer;
    margin: 0px 5px;
  }
}

.noClick {
  > button {
    cursor: not-allowed;
    // pointer-events: none;
  }
}

.dialogRelease {
  /deep/ .el-dialog {
    border-radius: 4px;

    .el-dialog__body {
      padding: 0px 0px 20px 0px;
    }

    .dialogTitle {
      display: flex;
      align-items: center;

      > img {
        width: 22px;
        height: 22px;
        margin-right: 16px;
      }

      > span {
        font-size: 16px;
        color: rgba(#000, 0.85);
        font-weight: 500;
        font-family: PingFang SC-Medium, PingFang SC;
      }
    }

    .releaseContent {
      padding: 0px 20px;
      font-weight: 400;
      font-size: 14px;
      font-family: PingFang SC-Regular, PingFang SC;
    }

    .releaseBtnList {
      display: flex;
      justify-content: flex-end;
      margin-top: 14px;
      padding-right: 20px;

      .btnDisable {
        width: 60px;
        // background-color: #f0f2f5;
        // color: rgba(#000000, 0.3);
      }

      // .btnWarn {
      //   border: none;
      //   background-color: rgba(#ff8f1f, 0.1);
      //   color: #ff8f1f;
      // }
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
