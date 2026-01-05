/*
 * @Date: 2022-04-23 02:09:51
 * @LastEditors: 大濕兄
 * @LastEditTime: 2022-04-23 02:09:51
 * @name: 
 * @FilePath: /gutuProject/newsmartvaluedemo/src/components/utils/auth.js
 */
const ID_TOKEN_KEY = 'id_token'

export const getToken = () => {
  return window.localStorage.getItem(ID_TOKEN_KEY)
}

export const setToken = token => {
  window.localStorage.setItem(ID_TOKEN_KEY, token)
}

export const removeToken = () => {
  window.localStorage.removeItem(ID_TOKEN_KEY)
}

export default { getToken, setToken, removeToken }
