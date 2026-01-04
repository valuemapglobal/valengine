import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'

export const useDataRiskStore = defineStore('dataRisk', () => {
    // Model Decision State
    const modelDecision = reactive({
        currentProduct: 'yhls',
        currentModule: 'ysh',
        currentFun: 'rule',
    })

    // Config
    const config = reactive({
        projectCode: '1001',
        ruleCode: 0,
    })

    // Mappings (kept as reference, though logic might be simplified)


    // Batch Info
    const batch = reactive({
        batchId: null as string | null,
        owner: null as any,
    })

    // Product Decision State
    const decision = reactive({
        projectCode: null as string | number | null, // Product ID
        businessCode: 1 as string | number, // Business Scene ID
        businessName: null as string | null,
        ruleCode: 1 as number, // Strategy Type ID (e.g. 5 for Rule, 6 for Classify)
        moduleName: null as string | null,
    })

    const business = ref(null)
    const productList = ref<any[]>([])

    // Actions
    function setModelDecision(data: any) {
        Object.assign(modelDecision, data)
    }

    function setProductDecision(data: any) {
        Object.assign(decision, data)
    }

    function setBusiness(data: any) {
        business.value = data
    }

    function setProductList(data: any[]) {
        productList.value = data
    }

    function setBatch(data: any) {
        Object.assign(batch, data)
    }

    function changeProductDecision(data: any) {
        setProductDecision(data)
    }

    function changeBusiness(data: any) {
        setBusiness(data)
    }

    function getProList(data: any[]) {
        setProductList(data)
    }

    return {
        modelDecision,
        config,
        batch,
        decision,
        business,
        productList,
        setModelDecision,
        setProductDecision,
        setBusiness,
        setProductList,
        setBatch,
        changeProductDecision,
        changeBusiness,
        getProList
    }
})
