/*
 * @Date: 2022-04-22 12:05:59
 * @LastEditors: 大濕兄
 * @LastEditTime: 2022-04-22 12:13:53
 * @name: toRegister
 * @FilePath: /gutuProject/newsmartvaluedemo/src/views/login/components/toRegister.js
 */
// 定义一个混入对象
export const toRegister = {
    methods: {
        toRegister(num) {
            this.$emit("toRegister", num);
        }
    }
}
