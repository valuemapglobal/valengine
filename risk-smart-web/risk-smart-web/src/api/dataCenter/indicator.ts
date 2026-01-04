import { post, get } from '@/utils/request'

enum Api {
    // Category
    AddCategory = '/vm/smartData/analysisIndicatorsCategory/addCategory',
    GetCategoryList = '/vm/smartData/analysisIndicatorsCategory/list',
    UpdateCategory = '/vm/smartData/analysisIndicatorsCategory/updateCategory',
    DeleteCategory = '/vm/smartData/analysisIndicatorsCategory/deleteCategory',

    // Object
    ObjectList = '/vm/smartData/analysisIndicatorsObject/objectList',
    AddObject = '/vm/smartData/analysisIndicatorsObject/addObject',
    DeleteObject = '/vm/smartData/analysisIndicatorsObject/deleteObject',

    // Rule
    RulePage = '/vm/smartData/analysisIndicatorsRule/rulePage',
    TreeList = '/vm/smartData/analysisIndicatorsRule/treeList',
    AddRule = '/vm/smartData/analysisIndicatorsRule/addRule',
    RuleDetails = '/vm/smartData/analysisIndicatorsRule/ruleDetails',
    DeleteRule = '/vm/smartData/analysisIndicatorsRule/deleteRule',
    UpdateRule = '/vm/smartData/analysisIndicatorsRule/updateRule',
}

// --- Category ---
export function addCategory(data: any) {
    return post(Api.AddCategory, data)
}

export function getCategoryList(data: any) {
    return post(Api.GetCategoryList, data)
}

export function updateCategory(data: any) {
    return post(Api.UpdateCategory, data)
}

export function deleteCategory(data: any) {
    return post(Api.DeleteCategory, data)
}

// --- Object ---
export function objectList(data: any) {
    return post(Api.ObjectList, data)
}

export function addObject(data: any) {
    return post(Api.AddObject, data)
}

export function deleteObject(data: any) {
    return post(Api.DeleteObject, data)
}

// --- Rule ---
export function rulePage(data: any) {
    return post(Api.RulePage, data)
}

export function treeList(data: any) {
    return get(Api.TreeList, data)
}

export function addRule(data: any) {
    return post(Api.AddRule, data)
}

export function ruleDetails(data: any) {
    return post(Api.RuleDetails, data)
}

export function deleteRule(data: any) {
    return post(Api.DeleteRule, data)
}

export function updateRule(data: any) {
    return post(Api.UpdateRule, data)
}
