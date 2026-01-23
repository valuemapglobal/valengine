<template>
  <div
    class="gutuTable"
    :style="[
      { position: !resize ? 'relative' : 'absolute' },
      { width: tableWidth ? `${tableWidth}px` : '100%' },
    ]"
    v-if="resizeTable"
  >
    <el-table
      v-loading="loading"
      ref="table"
      :data="dataList"
      :border="border"
      :height="
        !fixedHeight
          ? tableHeight
            ? `calc(var(--bgvh) - ${tableHeight}px)`
            : null
          : tableHeight
      "
      :row-key="rowKey"
      :highlight-current-row="true"
      @selection-change="handleSelection"
    >
      <el-table-column
        v-if="selection"
        type="selection"
        width="40"
      ></el-table-column>
      <el-table-column
        v-if="tabIndex"
        :key="'index'"
        align="center"
        label="序号"
        width="60px"
        fixed
      >
        <template v-slot="scope">
          <span>{{
            scope.$index + 1 + (query.pageNum - 1) * query.pageSize
          }}</span>
        </template>
      </el-table-column>
      <el-table-column
        v-for="(item, index) in columnConfig"
        :key="index"
        :prop="item.field"
        :label="item.label"
        :fixed="item.fixed"
        :align="item.align ? item.align : tableAlign ? tableAlign : 'center'"
        :width="
          item.width == 'auto' ? getTextWidth(item.label) + 'px' : item.width
        "
        :min-width="item.minWidth || '100px'"
      >
        <template slot="header" slot-scope="scope">
          <slot
            v-if="item.hasOwnProperty('slotHeader') && item.slotHeader"
            :name="'slotHeader_' + item.slotHeader"
            :row="scope.row"
            :index="scope.$index"
          />
          <span v-else>{{ item.label }}</span>
        </template>
        <template v-slot="scope">
          <span v-if="item.type == 'serial'">
            {{ scope.$index + 1 + (query.pageNum - 1) * query.pageSize }}
          </span>
          <div v-else-if="item.type === 'slot'">
            <slot
              :name="'slot_' + item.field"
              :row="scope.row"
              :index="scope.$index"
            />
          </div>
          <div v-else-if="item.type == 'popover'" class="popover-container">
            <el-popover
              :width="item.popoverWidth || '300'"
              :content="scope.row[item.field]"
              :placement="item.popoverPlacement || 'top-start'"
              trigger="hover"
            >
              <span slot="reference">{{ scope.row[item.field] }}</span>
            </el-popover>
          </div>
          <div v-else-if="item.type == 'row'" class="row-container">
            <span>{{ scope.row[item.field] }}</span>
          </div>
          <div v-else-if="item.type == 'image'">
            <span v-if="!scope.row[item.field]">暂无图片</span>
            <el-image
              v-else
              :style="
                item.imageStyle
                  ? { ...item.imageStyle }
                  : { width: '80px', height: '50px' }
              "
              :src="scope.row[item.field]"
              fit="contain"
              :preview-src-list="item.imageList"
            >
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
          </div>
          <div v-else-if="item.type == 'object'">
            {{
              typeof scope.row[item.field] == 'object' && item.subfield
                ? scope.row[item.field][item.subfield]
                : scope.row[item.field]
            }}
          </div>
          <div v-else-if="item.type == 'video'">
            <span
              v-if="!scope.row[item.field] || !isVedio(scope.row[item.field])"
            >
              暂无视频</span
            >
            <div v-else style="line-height: 0px">
              <video
                style="width: 100%; height: 60px"
                :src="scope.row[item.field]"
              ></video>
              <div class="table_vedio">
                <i
                  class="el-icon-video-play"
                  @click="openVideo(scope.row[item.field])"
                ></i>
              </div>
            </div>
          </div>
          <span v-else-if="item.type === 'timeStamp'">
            {{ parseTime(scope.row[item.field], item.pattern) }}
          </span>
          <span v-else-if="item.type === 'replace'">
            {{ handleReplace(item.field, scope.row) }}
          </span>
          <span
            :id="`tagSpan${item.field}${scope.$index}`"
            class="tagSpan"
            v-else-if="['dict', 'tag'].includes(item.type)"
          >
            {{ handleDict(item, scope.row[item.field], scope.$index) }}
          </span>
          <span v-else-if="item.type == 'defaultValue'">
            {{ item.field }}
          </span>
          <span
            v-else-if="item.type == 'joinList'"
            v-html="
              scope.row[item.field] ? scope.row[item.field].join('<br/>') : '-'
            "
          ></span>
          <span v-else-if="item.type == 'money'">
            {{ formatMoney(scope.row[item.field]) }}
          </span>
          <div v-else-if="item.type == 'switch'">
            <el-switch
              v-model="scope.row[item.field]"
              :active-value="item.activeValue || true"
              :inactive-value="
                item.hasOwnProperty('inactiveValue') &&
                item.inactiveValue != null
                  ? item.inactiveValue
                  : false
              "
              :active-text="item.activeText || null"
              :inactive-text="item.inactiveText || null"
              @change="handleSwitchChange(scope.row)"
            ></el-switch>
          </div>
          <span v-else>
            {{ scope.row[item.field] || '-' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column
        v-if="handle"
        :key="'handle'"
        :fixed="handle.fixed"
        :align="handle.align"
        :label="handle.label"
        :width="handle.width"
      >
        <template v-if="handle.slot" v-slot="scope">
          <slot v-if="handle.slot" :name="'bt_handle'" :data="scope" />
        </template>
        <template v-slot="scope" v-else>
          <template v-for="(item, index) in handle.btList">
            <!-- 自定义操作类型 -->
            <slot
              v-if="item.slot"
              :name="'bt_' + item.event"
              :data="{ item, row: scope.row }"
            />
            <!-- 操作按钮 -->
            <el-button
              v-if="
                !item.slot &&
                item.show &&
                (!item.ifRender || item.ifRender(scope.row))
              "
              :key="index"
              size="mini"
              :type="item.type"
              :icon="item.icon"
              :disabled="item.disabled"
              :loading="scope.row[item.loading]"
              @click="handleClick(item.event, scope.row)"
            >
              {{ item.label }}
            </el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
    <gutuVideo ref="gutuVideo" :url="videoUrl"></gutuVideo>
  </div>
</template>

<script>
import gutuVideo from './gutuVideo'

export default {
  name: 'GutuTableZHX',
  components: {
    gutuVideo,
  },
  props: {
    dataList: {
      type: Array,
      default: () => [],
    },
    /**
     * 表格基本配置
     * type参数
     *  slot：插槽
     *  image：列表显示图片，如果需要大图显示需要在传入imageList。imageList：当前页所有图片的数组集合
     *  video：列表显示视频
     *
     *  object：有的时候返回的数据是对象包对象，如果想用的字段在对象的对象里面，需要多传一个subfield
     *          field：是一级对象的字段名字。subfield：是名为[field]对象下的字段名
     *
     *  timeStamp：时间格式化 将时间格式化为yyyy-MM-dd hh:mm:ss
     *  replace：替换 ${feild} 为对应的值
     *
     *  dict：使用字典，需要传入 [pairedList] 用到的字典数组的名字。在 [collectionList] 对象下需要有对应的名为 [pairedList] 的数组 --需要颜色用tag
     *  tag：使用字典
     *  ——传入 [styleList]，styleList是一个map，例如  statusStyle: new Map([[0, { color: '#FF8F1F', bgColor: false }])。不传入功能同 上面的 dict
     *  ——color & bgColor: '颜色,透明度'  不传透明度默认为1。
     *    bgColor也可以是 Boolean,true:有背景，背景颜色是文字颜色的0.1透明度，false：没有背景色,默认为false
     */
    columnConfig: {
      type: Array,
      default: () => [],
    },
    //表格是否可选择
    selection: {
      type: Boolean,
      default: false,
    },
    /**
     * 是否有操作栏，操作栏相关的配置
     * slot：是否启用插槽
     * 不使用插槽就传 btList
     * btList：数组，用法同el-button
     * btList - 参数event：点击按钮调用方法
     */
    handle: {
      type: Object,
      default: () => {},
    },
    loading: {
      type: Boolean,
      default: false,
    },
    border: {
      type: Boolean,
      default: true,
    },
    fixedHeight: {
      type: Boolean,
      default: false,
    },
    tableWidth: {
      type: Number || Object,
      default: null,
    },
    //表格适配高度
    tableHeight: {
      type: Number || Object,
      default: null,
    },
    tableAlign: {
      type: String,
      default: 'center',
    },
    //表格是否显示序号
    tabIndex: {
      type: Boolean,
      default: false,
    },
    //需要显示序号就需要传入当前的pageSize,pageNum
    query: {
      type: Object,
      default: function () {
        return { pageNum: 1, pageSize: 10 }
      },
    },
    //表格中列用到的字典
    collectionList: {
      type: Object,
      default: () => {},
    },
    //滚动加载数据
    scrollLoadData: {
      type: Boolean,
      default: false,
    },
    resize: {
      type: Boolean,
      default: false,
    },
    rowKey: {
      type: [String, null],
      default: null,
    },
    highlightRow: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      visible: false,
      videoUrl: null,

      //虚拟滚动
      startIndex: 0,
      selectedRows: [],
      vEle: null,
      isSelectedAll: false,
      resizeTable: true,
    }
  },
  watch: {
    dataList: {
      handler(val) {},
      deep: true,
    },
  },
  computed: {
    sliceTable() {},
  },
  mounted() {
    // this.vEle = document.createElement('div')
    this.$refs.table.$el
      .querySelector('.el-table__body-wrapper')
      .addEventListener('scroll', this.tableScroll, {
        passive: true,
      })
  },
  methods: {
    // 派发按钮点击事件
    handleClick(event, data) {
      this.$emit(event, data)
    },
    handleSelection(data) {
      this.$emit('handleSelection', data)
    },
    tableScroll() {
      if (!this.scrollLoadData) return
      let bodyWrapperEle = this.$refs.table.$el.querySelector(
        '.el-table__body-wrapper'
      )
      let scrollTop = bodyWrapperEle.scrollTop
      if (
        bodyWrapperEle.scrollHeight <=
        scrollTop + bodyWrapperEle.clientHeight
      ) {
        this.$emit('loadMoreData')
        return
      }
    },
    openVideo(data) {
      this.videoUrl = data
      this.$refs.gutuVideo.visible = true
    },
    isVedio(data) {
      let suffix = data.split('.')[data.split('.').length - 1]
      if (['mp4'].includes(suffix)) return true
      else return false
    },
    handleSwitchChange(data) {
      this.$emit('handleSwitchChange', data)
    },
    clearSelection() {
      this.$refs.table.clearSelection()
    },
    handleDict(item, data, index) {
      let styleList = null
      if (
        item.styleList &&
        this.collectionList.hasOwnProperty(item.styleList)
      ) {
        styleList = this.collectionList[item.styleList]
      }
      if (
        item.pairedList &&
        this.collectionList.hasOwnProperty(item.pairedList)
      ) {
        let list = this.collectionList[item.pairedList]
        let findData = list.find((itemChild) => {
          let value = itemChild.value || itemChild.dictValue || 0
          if (value == data) {
            return itemChild
          }
        })
        if (findData) {
          this.$nextTick(() => {
            let tag = document.getElementById(`tagSpan${item.field}${index}`)
            if (tag && styleList != null) {
              let style = styleList.get(findData.value)
              if (style) {
                let colorList = style.color.split(',')
                let color = colorList[0]
                let opacity = colorList.length > 1 ? colorList[1] : null
                let bgList = []
                let bgColor = null
                let bgOpacity = null
                let showBg = false
                if (style.bgColor && typeof style.bgColor != 'boolean') {
                  bgList = style.bgColor.split(',')
                  bgColor = bgList[0]
                  bgOpacity = bgList.length > 0 ? bgList[1] : null
                } else showBg = style.bgColor
                if (color) {
                  tag.style.color = `rgba(${this.colorRgb(color)},${
                    opacity ? opacity : 1
                  })`
                  if (showBg) {
                    tag.style.backgroundColor = `rgba(${this.colorRgb(
                      color
                    )},0.1)`
                  }
                }
                if (bgColor) {
                  tag.style.backgroundColor = `rgba(${this.colorRgb(bgColor)},${
                    bgOpacity ? bgOpacity : 1
                  })`
                }
              }
            }
          })
          return findData.label || findData.dictLabel
        } else return data
      }
      return data
    },
    colorRgb(color) {
      var reg = /^#([0-9a-fA-f]{3}|[0-9a-fA-f]{6})$/
      var sColor = color.toLowerCase()
      if (sColor && reg.test(sColor)) {
        if (sColor.length === 4) {
          var sColorNew = '#'
          for (var i = 1; i < 4; i += 1) {
            sColorNew += sColor.slice(i, i + 1).concat(sColor.slice(i, i + 1))
          }
          sColor = sColorNew
        }
        //处理六位的颜色值
        var sColorChange = []
        for (var i = 1; i < 7; i += 2) {
          sColorChange.push(parseInt('0x' + sColor.slice(i, i + 2)))
        }
        return sColorChange.join(',')
      } else {
        return sColor
      }
    },
    handleReplace(value, data) {
      // 分别将json数据 data 中的键名和键值取出存储
      const keys = Object.keys(data)
      const dataList = keys.map(function (key) {
        return data[key]
      })
      // 遍历键名：判断字符串value中是否包含对应键名。若有，则替换为相应的键值。
      for (var i = 0; i < keys.length; i++) {
        value = value.replace(
          new RegExp('\\$\\{' + keys[i] + '\\}', 'gm'),
          dataList[i]
        )
      }
      return value
    },
    parseTime(time, pattern) {
      if (arguments.length === 0 || !time) {
        return null
      }
      const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}'
      let date
      if (typeof time === 'object') {
        date = time
      } else {
        if (typeof time === 'string' && /^[0-9]+$/.test(time)) {
          time = parseInt(time)
        } else if (typeof time === 'string') {
          time = time
            .replace(new RegExp(/-/gm), '/')
            .replace('T', ' ')
            .replace(new RegExp(/\.[\d]{3}/gm), '')
        }
        if (typeof time === 'number' && time.toString().length === 10) {
          time = time * 1000
        }
        date = new Date(time)
      }
      const formatObj = {
        y: date.getFullYear(),
        m: date.getMonth() + 1,
        d: date.getDate(),
        h: date.getHours(),
        i: date.getMinutes(),
        s: date.getSeconds(),
        a: date.getDay(),
      }
      const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
        let value = formatObj[key]
        // Note: getDay() returns 0 on Sunday
        if (key === 'a') {
          return ['日', '一', '二', '三', '四', '五', '六'][value]
        }
        if (result.length > 0 && value < 10) {
          value = '0' + value
        }
        return value || 0
      })
      return time_str
    },
    getTextWidth(text, fontSize = 14) {
      const span = document.createElement('span')
      span.style.visibility = 'hidden'
      span.style.whiteSpace = 'nowrap'
      span.style.fontSize = `${fontSize}px`
      span.textContent = text
      document.body.appendChild(span)
      const width = span.offsetWidth + 30 // 增加边距
      document.body.removeChild(span)
      return width
    },
  },
}
</script>

<style lang="less" scoped>
@import '/src/assets/less/commonCss.less';
.gutuTable {
  // height: 100%;
  display: flex;
}
.tagSpan {
  padding: 6px 10px;
}
.table_vedio {
  position: absolute;
  top: 0;
  left: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  > i {
    font-size: 36px;
    color: #fff;
    cursor: pointer;
  }
}
.popover-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;

  span {
    display: inline-block;
    max-width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
  }
}
.row-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;

  span {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 1; /* 显示3行，可以根据需要调整 */
    line-clamp: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    word-break: break-word;
    line-height: 1.4;
    max-width: 100%;
  }
}
</style>
