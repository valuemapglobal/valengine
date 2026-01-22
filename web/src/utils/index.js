/**
 * @param {Function} fn 防抖函数
 * @param {Number} delay 延迟时间
 */
export function debounce(fn, delay) {
  var timer;
  return function () {
    var context = this;
    var args = arguments;
    clearTimeout(timer);
    timer = setTimeout(function () {
      fn.apply(context, args);
    }, delay);
  };
}

/**
 * @param {date} time 需要转换的时间
 * @param {String} fmt 需要转换的格式 如 yyyy-MM-dd、yyyy-MM-dd HH:mm:ss
 */
export function formatTime(time, fmt) {
  if (!time) return "";
  else {
    const date = new Date(time);
    const o = {
      "M+": date.getMonth() + 1,
      "d+": date.getDate(),
      "H+": date.getHours(),
      "m+": date.getMinutes(),
      "s+": date.getSeconds(),
      "q+": Math.floor((date.getMonth() + 3) / 3),
      S: date.getMilliseconds(),
    };
    if (/(y+)/.test(fmt))
      fmt = fmt.replace(
        RegExp.$1,
        (date.getFullYear() + "").substr(4 - RegExp.$1.length)
      );
    for (const k in o) {
      if (new RegExp("(" + k + ")").test(fmt)) {
        fmt = fmt.replace(
          RegExp.$1,
          RegExp.$1.length === 1
            ? o[k]
            : ("00" + o[k]).substr(("" + o[k]).length)
        );
      }
    }
    return fmt;
  }
}

// 扁平化数组转化为树形结构数据
export function handleTree(data, id, parentId, children) {
  let config = {
    id: id || "id",
    parentId: parentId || "parentId",
    childrenList: children || "children",
  };

  var childrenListMap = {};
  var nodeIds = {};
  var tree = [];

  for (let d of data) {
    let parentId = d[config.parentId];
    if (childrenListMap[parentId] == null) {
      childrenListMap[parentId] = [];
    }
    nodeIds[d[config.id]] = d;
    childrenListMap[parentId].push(d);
  }

  for (let d of data) {
    let parentId = d[config.parentId];
    if (nodeIds[parentId] == null) {
      tree.push(d);
    }
  }

  for (let t of tree) {
    adaptToChildrenList(t);
  }

  function adaptToChildrenList(o) {
    if (childrenListMap[o[config.id]] !== null) {
      o[config.childrenList] = childrenListMap[o[config.id]];
    }
    if (o[config.childrenList]) {
      for (let c of o[config.childrenList]) {
        adaptToChildrenList(c);
      }
    }
  }

  return tree;
}

/**
 * 指定日期加上多少天，多少月，多少年的日期
 * @param interval
 * @param number
 * @param date
 * @returns {*}
 */
export function dateAdd(interval, number, date) {
  switch (interval) {
    case "Y": {
      date.setFullYear(date.getFullYear() + number);
      return date;
      break;
    }
    case "Q": {
      date.setMonth(date.getMonth() + number * 3);
      return date;
      break;
    }
    case "M": {
      date.setMonth(date.getMonth() + number);
      return date;
      break;
    }
    case "W": {
      date.setDate(date.getDate() + number * 7);
      return date;
      break;
    }
    case "D": {
      date.setDate(date.getDate() + number);
      return date;
      break;
    }
    case "h": {
      date.setHours(date.getHours() + number);
      return date;
      break;
    }
    case "m": {
      date.setMinutes(date.getMinutes() + number);
      return date;
      break;
    }
    case "s": {
      date.setSeconds(date.getSeconds() + number);
      return date;
      break;
    }
    default: {
      date.setDate(date.getDate() + number);
      return date;
      break;
    }
  }
}

/**
 * 根据对应值value从数组中提取对应中文label-设置展示值
 * @param value string||number字符串或数字  后端返回的字段值
 * @param option array数组
 * @param extractValueName string||number字符串或数字  数组对应值value的字段名称
 * @param extractLabelName string||number字符串或数字  数组对应标签label的字段名称
 */
export function setShowValue(
  value,
  option,
  extractValueName = "value",
  extractLabelName = "label"
) {
  if (option && option.length) {
    const currentObj = option.find((item) => item[extractValueName] == value);
    return currentObj ? currentObj[extractLabelName] : '';
  } else {
    return '';
  }
}
