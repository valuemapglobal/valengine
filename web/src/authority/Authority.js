/*
 * @Date: 2022-07-15 10:41:30
 * @LastEditors: 大濕兄
 * @LastEditTime: 2022-07-25 18:27:37
 * @name:
 * @FilePath: \god:\gutu-projeckt\vmkj\src\authority\Authority.js
 */
import router from "../router";
import { configRoutes, module } from "../router/config";
import stote from "../store";
let components = {},
  routerList = ["userInfo", "login"];
export default function (routers) {
  // 克隆路由
  let configs = deepClone(configRoutes);

  // 把每个路由懒加载地址记下来，后面根据地址对应路由
  setComponents(configs);
  let data = getAuth(routers);
  configs[0].children = data;
  if (data[0]) {
    configs[0].redirect = `/${data[0].name}/${data[0].children[0].name}`;
  }
  router.addRoute(configs[0]);
  stote.commit("setRouters", data);
  return routerList;
}

function getAuth(auth) {
  let dataList = auth.filter(
    (res) =>
      !res.name.includes("#") &&
      !res.name.includes(":") &&
      res.name !== "System" &&
      res.name !== "Monitor" &&
      res.name !== "Tool"
  );

  function setRouter(list) {
    let arr = [];
    list.forEach((item) => {
      let data = {};
      routerList.push(item.name);
      data = {
        name: item.name,
        path: item.name,
        hidden: process.env.VUE_APP_ENV === "production" ? item.hidden : false, //开发环境拥有所有显示权限
        meta: item.meta,
        component: components[item.name],
      };
      if (item.children) {
        data.children = setRouter(item.children);
      }
      arr.push(data);
    });
    return arr;
  }

  return setRouter(dataList);
}

// 递归记录
function setComponents(configs) {
  configs.forEach((item) => {
    // 记录地址
    components[item.name] = item.component;
    if (item.children) {
      // 如果有children则递归
      setComponents(item.children);
    }
  });
}
// 深copy
function deepClone(target) {
  // 定义一个变量
  let result;
  // 如果当前需要深拷贝的是一个对象的话
  if (typeof target === "object") {
    // 如果是一个数组的话
    if (Array.isArray(target)) {
      result = []; // 将result赋值为一个数组，并且执行遍历
      for (let i in target) {
        // 递归克隆数组中的每一项
        result.push(deepClone(target[i]));
      }
      // 判断如果当前的值是null的话；直接赋值为null
    } else if (target === null) {
      result = null;
      // 判断如果当前的值是一个RegExp对象的话，直接赋值
    } else if (target.constructor === RegExp) {
      result = target;
    } else {
      // 否则是普通对象，直接for in循环，递归赋值对象的所有值
      result = {};
      for (let i in target) {
        result[i] = deepClone(target[i]);
      }
    }
    // 如果不是对象的话，就是基本数据类型，那么直接赋值
  } else {
    result = target;
  }
  // 返回最终结果
  return result;
}
