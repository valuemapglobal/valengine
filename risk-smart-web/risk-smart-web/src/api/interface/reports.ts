import { post } from '@/utils/request'

enum Api {
    CountLog = '/vm/smartData/interfaceLog/countLog'
}

// Get Data Reports
export function countLog(data?: any) {
    return post(Api.CountLog, data)
}
