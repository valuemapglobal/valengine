/*
 * @Author: Seastar 1507136388@qq.com
 * @Date: 2025-06-05 11:14:46
 * @LastEditors: Seastar 1507136388@qq.com
 * @LastEditTime: 2025-06-20 11:33:12
 * @FilePath: \vm-RIC-saas\src\store\monitorDaily\index.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
/**
 * Author: detectiveBoy
 * Date: 2022-10-19 14:46:11
 * LastEditTime: 2023-08-25 17:58:18
 * email: 2913379173@qq.com
 * description: 智能监测日报
 */
// import { getRiskTableTitle } from '@/api/intelligentMonitoring'

// 安全JSON解析函数
const safeParseJSON = (str, context = {}) => {
  try {
    return JSON.parse(str)
  } catch (e) {
    console.error('[JSON解析错误] 原始数据:', str)
    throw new Error(
      `无法解析字典值，请检查数据格式：
      元模型: ${context.metaModel || '未知'}
      字典值: ${str}
      错误信息: ${e.message}
      有效JSON格式示例: {"title": "汉字名称", "fieldName": "字段名称"}`
    )
  }
}

export default {
  state: {
    AERTTL: [], // 新增事件风险表格标题列表
    rawData: [], // 添加原始数据用于调试
  },
  getters: {
    AERTTL: (state) =>
      state.AERTTL.map(
        ({
          dictLabel: metaModel,
          dictValue: info,
          dictSort: sort,
          cssClass,
        }) => {
          // 保留原始数据用于错误追踪
          const rawItem = state.rawData.find(
            (item) => item.dictLabel === metaModel && item.dictValue === info
          )

          return {
            metaModel,
            sort,
            info: safeParseJSON(info, {
              metaModel,
              rawItem, // 传递原始数据用于错误定位
            }),
            style: cssClass ? safeParseJSON(cssClass) : null,
          }
        }
      ),
  },
  mutations: {
    SET_AERTTL(state, payload) {
      state.AERTTL = payload
      state.rawData = payload // 存储原始数据
    },
  },
  actions: {
    async setAERTTL({ commit }) {
      // try {
      //   const { code, data } = await getRiskTableTitle()
      //   if (code === 200 && data) {
      //     // 记录获取的原始数据
      //     // console.debug('[API响应数据]', JSON.parse(JSON.stringify(data)))
      //     commit('SET_AERTTL', data)
      //     // 提前验证数据格式
      //     data.forEach((item) => {
      //       try {
      //         JSON.parse(item.dictValue)
      //       } catch (e) {
      //         console.error('[数据预检错误] 无效字典值:', {
      //           dictLabel: item.dictLabel,
      //           dictValue: item.dictValue,
      //           error: e.message,
      //         })
      //       }
      //     })
      //   }
      // } catch (e) {
      //   console.error('[数据加载错误]', e)
      //   throw new Error(`加载表格标题失败: ${e.message}`)
      // }
    },
  },
}
