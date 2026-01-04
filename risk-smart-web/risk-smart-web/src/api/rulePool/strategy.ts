import { post, get } from '@/utils/request'

enum Api {
    Page = '/vm/smartDecision/rule-pool/page',
    Stats = '/vm/smartDecision/rule-pool/stats',
    HotKeywords = '/vm/smartDecision/rule-pool/hot-keywords',
    Export = '/vm/smartDecision/rule-pool/export',

    // Dependency from Decision Platform (needed for filters)
    BusinessSearch = '/vm/smartDecision/business/_search'
}

// Rule Pool Page
export function getRulePoolGage(data: any) {
    return post(Api.Page, data)
}

// Rule Pool Stats
export function getRulePoolStats() {
    return get(Api.Stats)
}

// Hot Keywords
export function getRulePoolHotKeywords() {
    return get(Api.HotKeywords)
}

// Export
export function getRulePoolExport(data: any) {
    return post(Api.Export, data, { responseType: 'blob' })
}

// Business Search (Dependency)
export function business_search(data: any) {
    return post(Api.BusinessSearch, data)
}
