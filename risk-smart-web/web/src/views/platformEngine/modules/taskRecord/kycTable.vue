<template>
  <div class="table">
    <table>
      <thead>
        <tr>
          <th
            v-for="(i, index) in props.title"
            :key="index"
            :style="uniform ? { width: uniformWidth, ...i.style } : i.style"
          >
            {{ i.name }}
          </th>
        </tr>
      </thead>
      <tbody v-if="props.tableData.length">
        <tr v-for="(i, index) in props.tableData" :key="index">
          <td v-if="props.title[0]?.name == '序号'">{{ index + 1 }}</td>
          <td v-for="(j, k) in i" :key="k">{{ j }}</td>
        </tr>
      </tbody>
      <tbody v-else>
        <tr>
          <td :colspan="props.title.length" style="text-align: center">
            无数据
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script>
export default {
  props: {
    title: {
      //表格标题
      type: Array,
      default: () => [],
    },
    tableData: {
      //表格数据 [[]]数组嵌套，按照标题数据以此传入
      type: Array,
      default: () => [],
    },
    uniform: {
      //是否等分
      type: Boolean,
      default: false,
    },
  },
  computed: {
    uniformWidth() {
      let widthTotal = ''
      let noWidthNum = 0
      props.title.forEach((i) => {
        if (i.style && i.style.width) {
          widthTotal += `${i.style.width} - `
        } else {
          noWidthNum++
        }
      })
      return widthTotal
        ? `calc((475px - ${widthTotal.slice(
            0,
            widthTotal.length - 3
          )}) / ${noWidthNum})`
        : `calc(100% / ${noWidthNum})`
    },
  },
}
</script>

<style lang="less" scoped></style>
