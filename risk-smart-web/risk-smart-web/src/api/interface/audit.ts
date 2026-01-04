import { post } from '@/utils/request'

enum Api {
    FindAllLog = '/vm/smartData/interfaceLog/findAllLog'
}

export interface AuditParams {
    pageNum?: number
    pageSize?: number
    [key: string]: any
}

// Get Data Audit Logs
export function findAllLog(data: AuditParams) {
    return post(Api.FindAllLog, data)
}
