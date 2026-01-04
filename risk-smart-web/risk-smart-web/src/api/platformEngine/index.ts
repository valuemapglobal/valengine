import { post } from '@/utils/request'

enum Api {
    // Workflow Strategy
    PolicySubmit = '/vm/smartDecision/process-policy/submit',
    PolicyUpdate = '/vm/smartDecision/process-policy/update',
    PolicyList = '/vm/smartDecision/process-policy/list',
    PolicyDelete = '/vm/smartDecision/process-policy/delete',
    PolicyUpdateStatus = '/vm/smartDecision/process-policy/updateStatus',

    // Task Approval
    TaskGet = '/vm/smartDecision/process-policy-task/getTask',
    TaskApproval = '/vm/smartDecision/process-policy-task/approval',
    TaskDetail = '/vm/smartDecision/process-policy-task/getTaskDetail',

    // Task Records
    TaskRecordList = '/vm/smartDecision/model-task-record/taskRecordList',
    BatchList = '/vm/smartDecision/model-task-record-batch/batchList',

    // Dictionaries & Helpers
    BusinessSearch = '/vm/smartDecision/business/_search',
}

// --- Workflow Strategy APIs ---
export function policyList(data: any) {
    return post(Api.PolicyList, data)
}

export function addPolicy(data: any) {
    return post(Api.PolicySubmit, data)
}

export function updatePolicy(data: any) {
    return post(Api.PolicyUpdate, data)
}

export function deletePolicy(data: any) {
    return post(Api.PolicyDelete, data)
}

export function updatePolicyStatus(data: any) {
    return post(Api.PolicyUpdateStatus, data)
}

// --- Task Approval APIs ---
export function getPendingTasks(data: any) {
    return post(Api.TaskGet, data)
}

export function submitApproval(data: any) {
    return post(Api.TaskApproval, data)
}

export function getTaskDetail(data: any) {
    return post(Api.TaskDetail, data)
}

// --- Task Record APIs ---
export function getTaskRecords(data: any) {
    return post(Api.TaskRecordList, data)
}

export function getBatchRecords(data: any) {
    return post(Api.BatchList, data)
}

// --- Helpers ---
export function getBusinessScenario() {
    return post(Api.BusinessSearch, {})
}
