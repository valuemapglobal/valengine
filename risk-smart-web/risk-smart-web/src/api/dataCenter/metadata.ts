import { post, get } from '@/utils/request'

enum Api {
    ThemeList = '/vm/smartData/metadata/theme/list',
    GroupList = '/vm/smartData/metadata/group/list',
    RecordList = '/vm/smartData/metadata/record/list',
    ThemeListAll = '/vm/smartData/metadata/theme/listAll',
}

// Metadata Theme List
export function getThemeList(data: any) {
    return post(Api.ThemeList, data)
}

// Metadata Group List
export function getGroupList(data: any) {
    return post(Api.GroupList, data)
}

// Metadata Record List
export function getRecordList(data: any) {
    return post(Api.RecordList, data)
}

// All Themes (Get)
export function themelistAll() {
    return get(Api.ThemeListAll)
}
