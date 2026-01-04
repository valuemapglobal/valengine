import { defineStore } from 'pinia'
import type { Product, Business } from '@/api/decision'

/** 模型类型枚举 */
export enum ModelType {
  RULE = 5,      // 规则模型
  CLASSIFY = 6,  // 分类模型
  SCORE = 1,     // 评分模型
  RATE = 2,      // 评级模型
  LIMIT = 3,     // 额度模型
  PRICE = 4,     // 定价模型
}

/** 模型名称映射 */
export const ModelTypeNames: Record<ModelType, string> = {
  [ModelType.RULE]: '规则模型',
  [ModelType.CLASSIFY]: '分类模型',
  [ModelType.SCORE]: '评分模型',
  [ModelType.RATE]: '评级模型',
  [ModelType.LIMIT]: '额度模型',
  [ModelType.PRICE]: '定价模型',
}

/** 决策状态接口 */
export interface DecisionState {
  /** 当前选中的产品ID */
  projectCode: number | null
  /** 当前选中的业务场景ID */
  businessCode: number | null
  /** 当前选中的业务场景名称 */
  businessName: string
  /** 当前选中的模型类型 */
  ruleCode: ModelType
  /** 当前选中的模型名称 */
  moduleName: string
  /** 批次ID */
  batchId: string | null
}

/** 产品列表状态 */
export interface ProductListState {
  list: Product[]
  total: number
  loading: boolean
}

/** 业务场景状态 */
export interface BusinessState {
  current: Business | null
  list: Business[]
}

interface State {
  decision: DecisionState
  productList: ProductListState
  business: BusinessState
}

export const useDecisionStore = defineStore('decision', {
  state: (): State => ({
    decision: {
      projectCode: null,
      businessCode: null,
      businessName: '',
      ruleCode: ModelType.RULE,
      moduleName: '规则模型',
      batchId: null,
    },
    productList: {
      list: [],
      total: 0,
      loading: false,
    },
    business: {
      current: null,
      list: [],
    },
  }),

  getters: {
    /** 获取当前批次ID */
    batchId: (state) => state.decision.batchId,

    /** 获取当前产品ID */
    currentProjectCode: (state) => state.decision.projectCode,

    /** 获取当前业务场景ID */
    currentBusinessCode: (state) => state.decision.businessCode,

    /** 获取当前模型类型 */
    currentRuleCode: (state) => state.decision.ruleCode,

    /** 获取决策参数 (用于API调用) */
    decisionParams: (state) => ({
      projectCode: String(state.decision.projectCode || ''),
      businessCode: String(state.decision.businessCode || ''),
      ruleCode: String(state.decision.ruleCode),
    }),
  },

  actions: {
    /** 更新决策状态 */
    updateDecision(payload: Partial<DecisionState>) {
      Object.assign(this.decision, payload)
    },

    /** 设置当前产品 */
    setProject(projectCode: number) {
      this.decision.projectCode = projectCode
    },

    /** 设置当前业务场景 */
    setBusiness(business: Business) {
      this.decision.businessCode = business.id
      this.decision.businessName = business.name
      this.business.current = business
    },

    /** 设置当前模型类型 */
    setModelType(ruleCode: ModelType, moduleName?: string) {
      this.decision.ruleCode = ruleCode
      this.decision.moduleName = moduleName || ModelTypeNames[ruleCode]
    },

    /** 设置批次ID */
    setBatchId(batchId: string | null) {
      this.decision.batchId = batchId
    },

    /** 设置产品列表 */
    setProductList(list: Product[], total?: number) {
      this.productList.list = list
      if (total !== undefined) {
        this.productList.total = total
      }
    },

    /** 追加产品列表 (用于加载更多) */
    appendProductList(list: Product[], total: number) {
      this.productList.list = [...this.productList.list, ...list]
      this.productList.total = total
    },

    /** 设置产品列表加载状态 */
    setProductLoading(loading: boolean) {
      this.productList.loading = loading
    },

    /** 设置业务场景列表 */
    setBusinessList(list: Business[]) {
      this.business.list = list
    },

    /** 重置决策状态 */
    resetDecision() {
      this.decision = {
        projectCode: null,
        businessCode: null,
        businessName: '',
        ruleCode: ModelType.RULE,
        moduleName: '规则模型',
        batchId: null,
      }
    },
  },
})
