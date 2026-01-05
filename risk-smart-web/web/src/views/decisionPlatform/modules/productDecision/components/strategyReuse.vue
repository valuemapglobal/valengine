<template>
  <!--	规则模型和分类模型-策略复用-->
  <div>
    <div class="reuse">
      <!--		左穿梭框-->
      <div class="transfer-box transfer-left">
        <div class="transfer-box-search">
          <el-input
            placeholder="请输入code或详情"
            v-model="selfBuilt.params.name"
            clearable
            @clear="onSearch"
          >
            <img slot="suffix" src="../../../images/search.png" alt="" />
          </el-input>
          <el-button type="primary" @click="onSearch">查询</el-button>
        </div>
        <div class="transfer-box-checkbox">
          <el-checkbox
            :disabled="selfBuilt.checkAllDisabled"
            :indeterminate="selfBuilt.isIndeterminate"
            v-model="selfBuilt.checkAll"
            @change="handleCheckAllChange"
          >
            自建策略列表
          </el-checkbox>
          <el-checkbox-group
            v-model="selfBuilt.checked"
            v-loading="selfBuilt.loadingShow"
            @change="handleCheckedChange"
          >
            <InfiniteLoading
              :loadingMore="selfBuilt.loadingMore"
              :noMore="selfBuilt.noMore"
              @load="load"
            >
              <el-checkbox
                v-for="(selfItem, selfIndex) in selfBuilt.list"
                :key="selfIndex"
                :label="selfItem.id"
                :disabled="selfItem.disabled"
              >
                <div class="card-item">
                  <div class="card-item-inner">
                    <div class="card-item-inner_top">
                      <el-tooltip :content="selfItem.name" placement="top">
                        <span class="title">{{ selfItem.name }}</span>
                      </el-tooltip>
                      <el-tag
                        :type="selfItem.deptFlag == 1 ? 'primary' : 'warning'"
                      >
                        {{ selfItem.deptFlag == 1 ? '默认' : '自建' }}
                      </el-tag>
                    </div>
                    <div class="card-item-inner_description">
                      描述：{{ selfItem.descr }}
                    </div>
                    <div class="card-item-inner_description">
                      版本：{{ selfItem.versionControl || '-' }}
                    </div>
                  </div>
                  <el-button
                    v-if="selfItem.deptFlag == 1"
                    type="text"
                    @click.stop="openDetail(selfItem)"
                  >
                    查看详情
                  </el-button>
                </div>
              </el-checkbox>
            </InfiniteLoading>
          </el-checkbox-group>
        </div>
      </div>
      <div class="transfer-action">
        <div
          class="transfer-button transfer-button--left"
          :class="{ 'transfer-button-disabled': !selfBuilt.checked.length }"
          @click="transferChange"
        >
          <img src="../../../images/transfer-icon.png" alt="" />
        </div>
        <div
          class="transfer-button transfer-button--right"
          :class="{ 'transfer-button-disabled': !standardBuilt.checked.length }"
          @click="transferChange('standard')"
        >
          <img src="../../../images/transfer-icon.png" alt="" />
        </div>
      </div>
      <!--		右穿梭框-->
      <div class="transfer-box transfer-right">
        <div class="transfer-box-search">
          <el-input
            placeholder="请输入code或详情"
            v-model="standardBuilt.params.name"
            clearable
            @clear="onSearch('standard')"
          >
            <img slot="suffix" src="../../../images/search.png" alt="" />
          </el-input>
          <el-button type="primary" @click="onSearch('standard')"
            >查询</el-button
          >
        </div>
        <div class="transfer-box-search">
          <el-select
            v-model="standardBuilt.params.projectCode"
            clearable
            placeholder="请选择产品列表"
            @change="onSearch('standard')"
          >
            <el-option
              v-for="item in productOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
            </el-option>
          </el-select>
          <el-select
            v-model="standardBuilt.params.businessCode"
            clearable
            placeholder="请选择业务场景"
            @change="onSearch('standard')"
          >
            <el-option
              v-for="item in businessOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
              :disabled="item.disabled"
            >
            </el-option>
          </el-select>
        </div>
        <div class="transfer-box-checkbox">
          <el-checkbox
            :disabled="standardBuilt.checkAllDisabled"
            :indeterminate="standardBuilt.isIndeterminate"
            v-model="standardBuilt.checkAll"
            @change="handleCheckAllChange($event, 'standard')"
          >
            标准策略列表
          </el-checkbox>
          <el-checkbox-group
            v-model="standardBuilt.checked"
            v-loading="standardBuilt.loadingShow"
            @change="handleCheckedChange($event, 'standard')"
          >
            <InfiniteLoading
              :loadingMore="standardBuilt.loadingMore"
              :noMore="standardBuilt.noMore"
              @load="load('standard')"
            >
              <el-checkbox
                v-for="(standardItem, standardIndex) in standardBuilt.list"
                :key="standardIndex"
                :label="standardItem.id"
                :disabled="standardItem.disabled"
              >
                <div class="card-item">
                  <div class="card-item-inner">
                    <div class="card-item-inner_top">
                      <el-tooltip :content="standardItem.name" placement="top">
                        <span class="title">{{ standardItem.name }}</span>
                      </el-tooltip>
                      <el-tag
                        :type="
                          standardItem.deptFlag == 1 ? 'primary' : 'warning'
                        "
                      >
                        {{ standardItem.deptFlag == 1 ? '默认' : '自建' }}
                      </el-tag>
                      <i
                        v-if="standardItem.deptFlag == 1"
                        class="el-icon-view"
                        @click.stop="openDetail(standardItem)"
                      ></i>
                    </div>
                    <div class="card-item-inner_description">
                      描述：{{ standardItem.descr }}
                    </div>
                    <div class="card-item-inner_description">
                      版本：{{ standardItem.versionControl || '-' }}
                    </div>
                  </div>
                  <!-- <el-button
                    v-if="standardItem.deptFlag == 1"
                    type="text"
                    @click.stop="openDetail(standardItem)"
                  >
                    查看详情
                  </el-button> -->
                </div>
              </el-checkbox>
            </InfiniteLoading>
          </el-checkbox-group>
        </div>
      </div>
    </div>
    <div class="action">
      <el-button
        type="success"
        :loading="reuseButtonLoading"
        @click="reuseHandle"
        >复用</el-button
      >
    </div>
    <RuleTypeReuseDetail ref="ruleTypeReuseDetailRef" />
  </div>
</template>

<script>
import RuleTypeReuseDetail from './RuleTypeReuseDetail'
import InfiniteLoading from '@/components/InfiniteLoading'
import {
  selfBuiltStrategyList,
  standardBuiltStrategyList,
  addStandardModel,
  selectPolicyGroup,
} from '@/views/decisionPlatform/modules/productDecision/api/riskModel'
import {
  business_search,
  standardProductList,
} from '@/views/decisionPlatform/modules/productDecision/api'
import { formatTime } from '@/utils'

export default {
  name: 'reuse',
  components: { RuleTypeReuseDetail, InfiniteLoading },
  props: {
    paramsData: { type: Object, default: () => {} },
  },
  data() {
    return {
      productOptions: [], //标准产品下拉选项
      businessOptions: [], //标准产品下拉选项
      //左侧——自建
      selfBuilt: {
        list: [],
        total: 0,
        loadingShow: false, //loading是否显示
        loadingMore: false, //正在获取更多内容……
        noMore: false, //没有更多了
        params: {
          name: null,
          pageSize: 10,
          pageNum: 1,
        },
        checkAll: false, //自建全选v-model
        checked: [], //checkbox-group v-model
        isIndeterminate: false, //设置 indeterminate 状态，只负责样式控制
        //自建全选是否禁用
        checkAllDisabled: false,
        //自建增加的
        selfIncreaseList: [],
        //自建减少的
        selfReductionList: [],
      },
      //右侧——标准
      standardBuilt: {
        list: [],
        total: 0,
        loadingShow: false, //loading是否显示
        loadingMore: false, //正在获取更多内容……
        noMore: false, //没有更多了
        params: {
          name: null,
          projectCode: null,
          businessCode: null,
          pageSize: 10,
          pageNum: 1,
        },
        checkAll: false, //自建全选v-model
        checked: [], //checkbox-group v-model
        isIndeterminate: false, //设置 indeterminate 状态，只负责样式控制
        //标准全选是否禁用
        checkAllDisabled: false,
        //标准增加的
        standardIncreaseList: [],
        //标准减少的
        standardReductionList: [],
      },
      //自建（已复用的 标准 数据list）
      reuseStandardList: [],
      reuseButtonLoading: false,
    }
  },
  watch: {
    '$parent.$parent.drawer.visible': {
      handler(cur) {
        if (cur) {
          this.onReset()
          this.getList(true)
          this.init()
        }
      },
      deep: true,
      immediate: true,
    },
  },
  mounted() {},
  methods: {
    async init() {
      this.getSelected()
      await Promise.all([this.getProductList(), this.getBusinessList()])
      const { projectCode, businessCode } = this.standardBuilt.params
      if (projectCode && businessCode) this.getList(true, 'standard')
    },
    getProductList() {
      return standardProductList().then((res) => {
        if (res.code === 200) {
          this.productOptions = res.data
          if (this.productOptions.length) {
            this.standardBuilt.params.projectCode = this.productOptions[0].id
          }
        }
      })
    },
    getBusinessList() {
      return business_search().then((res) => {
        if (res.code === 200) {
          this.businessOptions = res.data.list
          if (this.businessOptions.length) {
            this.businessOptions = this.businessOptions.map((item) => {
              return {
                ...item,
                disabled: this.paramsData.businessName !== item.name,
              }
            })
            const currentIndex = this.businessOptions.findIndex(
              (item) => this.paramsData.businessName === item.name
            )
            this.standardBuilt.params.businessCode =
              this.businessOptions[currentIndex === -1 ? 0 : currentIndex].id
          }
        }
      })
    },
    getSelected() {
      selectPolicyGroup({
        buildProjectCode: this.paramsData.productCode,
        buildBusinessCode: this.paramsData.businessCode,
        buildRuleCode: this.paramsData.ruleCode,
        moudleId: 1, //模块区分 1:策略 2:规则组 3:规则
      }).then((res) => {
        this.reuseStandardList = res.data || []
      })
    },
    /**
     * 获取自建或标准列表
     * @param isFirst 是否为首次加载
     * @param type 默认-自建、standard-标准
     * @constructor
     */
    getList(isFirst, type) {
      const API =
        type === 'standard' ? standardBuiltStrategyList : selfBuiltStrategyList
      const name = type === 'standard' ? 'standardBuilt' : 'selfBuilt'
      let paramsData = {
        ruleCode: this.paramsData.ruleCode,
        ...this[name].params,
      }
      if (type === 'standard') {
        Object.assign(paramsData, {
          buildProjectCode: this.paramsData.productCode,
          buildBusinessCode: this.paramsData.businessCode,
          buildRuleCode: this.paramsData.ruleCode,
          modelAntiStandardDataList: isFirst
            ? []
            : this.formatList(this[name].standardIncreaseList),
          modelAntiStandardDataDelList: isFirst
            ? []
            : this.formatList(this[name].standardReductionList),
        })
      } else {
        Object.assign(paramsData, {
          projectCode: this.paramsData.productCode,
          businessCode: this.paramsData.businessCode,
          modelAntiDataList: isFirst
            ? []
            : this.formatList(this[name].selfIncreaseList),
          modelAntiDataDelList: isFirst
            ? []
            : this.formatList(this[name].selfReductionList),
        })
      }

      if (isFirst) this[name].loadingShow = true
      API(paramsData)
        .then((res) => {
          const { list, pages } = res.data
          // deptFlag 1标准0自建
          let disableList = []
          const newList = list.map((item) => {
            disableList.push(item.deptFlag != 1)
            return { ...item, disabled: item.deptFlag != 1 }
          })
          this[name].checkAllDisabled = disableList.indexOf(false) < 0
          this[name].list =
            this[name].params.pageNum === 1
              ? newList
              : this[name].list.concat(newList)
          this[name].total = pages
          this[name].loadingShow = false
          this[name].loadingMore = false
          if (
            this[name].params.pageNum >= this[name].total &&
            this[name].total
          ) {
            this[name].noMore = true
          }
        })
        .catch(() => {
          this[name].loadingShow = false
          this[name].loadingMore = false
        })

      // console.log(Object.assign(this.$data, this.$options.data.call(this)))
    },
    /**
     * 滚动加载
     * @param type 默认-自建、standard-标准
     */
    load(type) {
      const name = type === 'standard' ? 'standardBuilt' : 'selfBuilt'
      if (!this[name].total) return
      if (!this[name].loadingMore && !this[name].loadingShow) {
        if (this[name].params.pageNum >= this[name].total && this[name].total) {
          this[name].noMore = true
        } else {
          this[name].loadingMore = true
          this[name].params.pageNum++
          this.getList(false, type)
        }
      }
    },
    /**
     * 自建&标准input搜索
     * @param type 默认-自建、standard-标准
     * @constructor
     */
    onSearch(type) {
      switch (type) {
        case 'standard':
          this.standardBuilt.params.pageNum = 1
          break
        default:
          this.selfBuilt.params.pageNum = 1
          break
      }
      this.getList(false, type)
    },
    /**
     * 全选按钮change
     * @param val
     * @param type 默认-自建、standard-标准
     */
    handleCheckAllChange(val, type) {
      let checkedIds = []
      switch (type) {
        case 'standard':
          this.standardBuilt.list.forEach((item) => {
            if (!item.disabled) {
              checkedIds.push(item.id)
            }
          })
          this.standardBuilt.checked = val ? checkedIds : []
          this.standardBuilt.isIndeterminate = false
          break
        default:
          this.selfBuilt.list.forEach((item) => {
            if (!item.disabled) {
              checkedIds.push(item.id)
            }
          })
          this.selfBuilt.checked = val ? checkedIds : []
          this.selfBuilt.isIndeterminate = false
          break
      }
    },
    /**
     * 多选按钮change
     * @param value
     * @param type 默认-自建、standard-标准
     */
    handleCheckedChange(value, type) {
      let checkedCount = value.length
      switch (type) {
        case 'standard':
          this.standardBuilt.checkAll =
            checkedCount === this.standardBuilt.list.length
          this.standardBuilt.isIndeterminate =
            checkedCount > 0 && checkedCount < this.standardBuilt.list.length
          break
        default:
          this.selfBuilt.checkAll = checkedCount === this.selfBuilt.list.length
          this.selfBuilt.isIndeterminate =
            checkedCount > 0 && checkedCount < this.selfBuilt.list.length
          break
      }
    },
    /**
     * 穿梭按钮点击
     * @param type 默认-自建、standard-标准
     */
    transferChange(type) {
      const name = type === 'standard' ? 'selfBuilt' : 'standardBuilt'
      let disableList = []
      switch (type) {
        case 'standard':
          //标准——→自建
          this.standardBuilt.checked.forEach((id) => {
            //从标准增加列表————————删除对应数据
            const currentIndex =
              this.standardBuilt.standardIncreaseList.findIndex(
                (item2) => id === item2.id
              )
            if (currentIndex >= 0) {
              this.standardBuilt.standardIncreaseList.splice(currentIndex, 1)
            }
            //从自建减少列表————————删除对应数据
            const currentIndex2 = this.selfBuilt.selfReductionList.findIndex(
              (item2) => id === item2.id
            )
            if (currentIndex2 >= 0) {
              this.selfBuilt.selfReductionList.splice(currentIndex2, 1)
            }
            this.standardBuilt.list.forEach((item, index) => {
              if (id === item.id) {
                this.selfBuilt.list.push(item)
                if (this.reuseStandardList.length) {
                  this.reuseStandardList.forEach((item3) => {
                    if (item3.id !== id) {
                      //往自建增加列表————————添加对应数据
                      this.selfBuilt.selfIncreaseList.push(item)
                      //往标准减少列表————————添加对应数据
                      this.standardBuilt.standardReductionList.push(item)
                    }
                  })
                } else {
                  //往自建增加列表————————添加对应数据
                  this.selfBuilt.selfIncreaseList.push(item)
                  //往标准减少列表————————添加对应数据
                  this.standardBuilt.standardReductionList.push(item)
                }

                this.standardBuilt.list.splice(index, 1)
              }
            })
          })
          this.standardBuilt.checkAll = false
          this.handleCheckAllChange(this.standardBuilt.checkAll, type)
          break
        default:
          //自建——→标准
          this.selfBuilt.checked.forEach((id) => {
            //从标准减少列表————————删除对应数据
            const currentIndex =
              this.standardBuilt.standardReductionList.findIndex(
                (item2) => id === item2.id
              )
            if (currentIndex >= 0) {
              this.standardBuilt.standardReductionList.splice(currentIndex, 1)
            }
            //从自建增加列表————————删除对应数据
            const currentIndex2 = this.selfBuilt.selfIncreaseList.findIndex(
              (item2) => id === item2.id
            )
            if (currentIndex2 >= 0) {
              this.selfBuilt.selfIncreaseList.splice(currentIndex2, 1)
            }
            this.selfBuilt.list.forEach((item, index) => {
              if (id === item.id) {
                this.standardBuilt.list.push(item)
                this.reuseStandardList.forEach((item3) => {
                  if (item3.id === id) {
                    //往自建减少列表————————添加对应数据
                    this.selfBuilt.selfReductionList.push(item)
                    //往标准增加列表————————添加对应数据
                    this.standardBuilt.standardIncreaseList.push(item)
                  }
                })
                this.selfBuilt.list.splice(index, 1)
              }
            })
          })
          this.selfBuilt.checkAll = false
          this.handleCheckAllChange(this.selfBuilt.checkAll, type)
          break
      }

      this[name].list.forEach((item3) => {
        disableList.push(item3.deptFlag !== 1)
      })
      this[name].checkAllDisabled = disableList.indexOf(false) < 0
    },
    //复用按钮点击
    reuseHandle() {
      this.reuseButtonLoading = true
      let arr = this.reuseStandardList.filter(
        (item) =>
          !this.selfBuilt.selfReductionList.some((ele) => ele.id === item.id)
      )
      let arrs = [...arr, ...this.selfBuilt.selfIncreaseList],
        ruleRecordData = []
      //根据id去重
      let map = new Map()
      for (let item of arrs) {
        if (!map.has(item.id)) {
          map.set(item.id, item)
          ruleRecordData = [...map.values()] //把map中所有的值取出来放进数组
        }
      }

      addStandardModel({
        buildProjectCode: this.paramsData.productCode,
        buildBusinessCode: this.paramsData.businessCode,
        buildRuleCode: this.paramsData.ruleCode,
        ruleRecordData: this.formatList(ruleRecordData),
      }).then((res) => {
        this.reuseButtonLoading = false
        this.$message.success('操作成功')
        this.$emit('success', 0)
      })
    },
    /**
     * 格式化对应创建时间&&更新时间
     * @param dataList
     * @returns {*}
     */
    formatList(dataList) {
      return dataList.map((item) => {
        return {
          ...item,
          createTime: item.createTime
            ? formatTime(item.createTime, 'yyyy-MM-dd HH:mm:ss')
            : null,
          updateTime: item.updateTime
            ? formatTime(item.updateTime, 'yyyy-MM-dd HH:mm:ss')
            : null,
        }
      })
    },
    /**
     * 标准列表--点击查看详情按钮
     * @param data
     */
    openDetail(data) {
      const { ruleCode } = this.paramsData
      const { projectCode, businessCode } = this.standardBuilt.params
      this.$refs.ruleTypeReuseDetailRef.drawer.visible = true
      this.$refs.ruleTypeReuseDetailRef.standardData = {
        businessCode,
        modelId: data.id,
        modelType: 1,
        projectCode,
        ruleCode,
      }
    },
    //重置this.data
    onReset() {
      this.selfBuilt = this.$options.data().selfBuilt
      this.standardBuilt = this.$options.data().standardBuilt
      this.reuseStandardList = this.$options.data().reuseStandardList
      this.reuseButtonLoading = this.$options.data().reuseButtonLoading
      if (this.productOptions.length) {
        this.standardBuilt.params.projectCode = this.productOptions[0].id
      }
      if (this.businessOptions.length) {
        const currentIndex = this.businessOptions.findIndex(
          (item) => this.paramsData.businessName === item.name
        )
        this.standardBuilt.params.businessCode =
          this.businessOptions[currentIndex === -1 ? 0 : currentIndex].id
      }
    },
  },
}
</script>

<style lang="less" scoped>
.reuse {
  // padding: 0 30px;
  display: flex;

  .transfer-box {
    width: 480px;
    padding: 20px 0 0;
    border-radius: 6px;
    border: 1px solid var(--border-color);

    &-search {
      padding: 0 20px;
      display: flex;

      & + .transfer-box-search {
        margin-top: 10px;
      }

      ::v-deep .el-input {
        & + .el-select {
          margin-left: 10px;
        }

        &__inner {
          height: 42px;
          line-height: 42px;
          font-size: 14px;
          border-radius: 4px;
          // background: rgba(0, 0, 0, 0.04);

          &::placeholder {
            color: var(--text-color-quaternary);
          }
        }

        .el-input__suffix {
          right: 14px;
          line-height: 42px;

          img {
            width: 20px;
            height: 20px;
          }
        }
      }

      ::v-deep .el-button {
        margin-left: 10px;
        height: 42px;
        padding: 0 22px;
        line-height: 42px;
        font-size: 14px;
        border-radius: 6px;
        border: none;

        &--primary {
          background: var(--primary-color);
        }
      }

      ::v-deep .el-select {
        & + .el-select {
          margin-left: 10px;
        }

        .el-input {
          width: 178px;

          .el-select__caret {
            font-weight: 600;
            color: #1e2135;
            line-height: 42px;
          }
        }
      }
    }

    &-checkbox {
      ::v-deep .el-checkbox {
        display: flex;
        align-items: center;
        padding: 12px;
        margin: 20px 20px 0;
        background: rgba(40, 136, 232, 0.1);
      }

      ::v-deep .el-checkbox-group {
        padding: 0 20px 20px;
        //height: calc(var(--bgvh) - 229px);
        height: calc(var(--bgvh) - 373px);
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

        .el-checkbox {
          margin: 10px 0 0;
          border-radius: 6px;
          border: 1px solid var(--border-color);
          background: var(--bg-color);
          display: flex;

          &__label {
            width: calc(100% - 14px);
            padding-left: 12px;

            .card-item {
              width: 100%;
              display: flex;
              align-items: center;
              justify-content: space-between;

              &-inner {
                width: 100%;
                &_top {
                  font-weight: 400;
                  font-size: 14px;
                  color: var(--text-color-secondary);
                  line-height: 16px;
                  display: flex;
                  align-items: center;

                  > i {
                    cursor: pointer;
                    margin-left: 10px;
                    color: #2888e8;
                  }

                  > .title {
                    flex: 1;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                  }

                  .el-tag {
                    // width: 40px;
                    white-space: nowrap;
                    margin-left: 10px;
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
                &_description {
                  margin-top: 10px;
                  color: var(--text-color-tertiary);
                }
                &_description,
                &_top {
                  width: 100%;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  white-space: nowrap;
                }
              }

              .el-button {
                width: 50px;
                margin-left: 10px;
              }
            }
          }
        }
      }
    }

    &.transfer-left {
      .transfer-box-checkbox {
        ::v-deep .el-checkbox-group {
          height: calc(var(--bgvh) - 321px);
        }
      }
    }

    /*&.transfer-right {
				.transfer-box-checkbox {
					::v-deep .el-checkbox-group {
						height: calc(var(--bgvh) - 281px);
					}
				}
			}*/
  }

  .transfer-action {
    margin: 0 60px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    .transfer-button {
      cursor: pointer;
      width: 80px;
      height: 80px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
      background: var(--primary-color);

      & + .transfer-button {
        margin-top: 74px;
      }

      &-disabled {
        opacity: 0.4;
      }

      &--left {
        transform: rotate(180deg);
      }

      > img {
        width: 46px;
        height: 40px;
      }
    }
  }
}

.action {
  text-align: right;
  margin-top: 30px;

  ::v-deep .el-button {
    &--success {
      width: 88px;
      height: 40px;
      font-weight: 500;
      font-size: 14px;
      background: #00b578;
      border-radius: 6px;
      border: none;
    }
  }
}
</style>
