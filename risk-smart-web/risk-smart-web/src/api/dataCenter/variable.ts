import { post } from '@/utils/request'

enum Api {
    FindTopic = '/vm/smartData/characteristicVariables/findVariablesTopic',
    FindGroup = '/vm/smartData/characteristicVariables/findVariablesGroup',
    FindField = '/vm/smartData/characteristicVariables/findVariablesField',
    FindAllTopic = '/vm/smartData/characteristicVariables/findVariablesAllTopic',
}

// Feature Variable Topic
export function findVariablesTopic(data: any) {
    return post(Api.FindTopic, data)
}

// Feature Variable Group
export function findVariablesGroup(data: any) {
    return post(Api.FindGroup, data)
}

// Feature Variable Field
export function findVariablesField(data: any) {
    return post(Api.FindField, data)
}

// All Topics
export function findVariablesAllTopicAll() {
    return post(Api.FindAllTopic)
}
