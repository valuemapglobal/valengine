<template>
  <div class="textarea-container">
    <div class="textarea-container-inner">
      <div class="textarea-container-inner--title">{{ $t('dataCenter.formulaConfig') }}</div>
      <el-input
        id="testMultiple"
        type="textarea"
        :rows="2"
        resize="none"
        v-model="virtualValue"
        :placeholder="$t('dataCenter.formulaPlaceholder')"
        @input="onInput"
      />
    </div>
    <div class="textarea-container-inner">
      <div class="textarea-container-inner--title">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          xmlns:xlink="http://www.w3.org/1999/xlink"
          fill="none"
          version="1.1"
          width="16"
          height="16"
          viewBox="0 0 16 16"
        >
          <defs>
            <clipPath id="master_svg0_14_3902">
              <rect x="0" y="0" width="16" height="16" rx="0" />
            </clipPath>
          </defs>
          <g clip-path="url(#master_svg0_14_3902)">
            <g>
              <path
                d="M7.9996640625,0.0400390625C3.6156640625,0.0400390625,0.0556640625,3.5920390625,0.0556640625,7.9840390625C0.0556640625,12.3760390625,3.6156640625,15.9200390625,7.9996640625,15.9200390625C12.3836640625,15.9200390625,15.9436640625,12.3680390625,15.9436640625,7.9760390625C15.9436640625,3.5840390625,12.3916640625,0.0400390625,7.9996640625,0.0400390625ZM7.9996640625,14.7520390625C4.2556640625,14.7520390625,1.2236640625,11.7200390625,1.2236640625,7.9760390625C1.2236640625,4.2320390625,4.2556640625,1.2000390625,7.9996640625,1.2000390625C11.7436640625,1.2000390625,14.7756640625,4.2320390625,14.7756640625,7.9760390625C14.7756640625,11.7200390625,11.7436640625,14.7520390625,7.9996640625,14.7520390625Z"
                fill="#F4DCCB"
                fill-opacity="1"
                style="mix-blend-mode: passthrough"
              />
            </g>
            <g>
              <path
                d="M7.97592,8.024103515625C7.919919999999999,8.024103515625,7.86392,8.008103515624999,7.81592,7.984103515625L4.879922,6.296103515625C4.7839220000000005,6.240103515625,4.719923,6.136103515625,4.719923,6.016103515625C4.719923,5.896103515625,4.783923,5.792103515625,4.879922,5.736103515625L7.81592,4.048103315625C7.91192,3.992103315625,8.03992,3.992103315625,8.135919999999999,4.048103315625L11.07192,5.736103515625C11.167919999999999,5.792103515625,11.231919999999999,5.896103515625,11.231919999999999,6.016103515625C11.231919999999999,6.136103515625,11.167919999999999,6.240103515625,11.07192,6.296103515625L8.135919999999999,7.984103515625C8.079920000000001,8.016103515625,8.02392,8.024103515625,7.97592,8.024103515625ZM5.67992,6.024103515625L7.97592,7.344103515625L10.27192,6.024103515625L7.97592,4.704103515625L5.67992,6.024103515625ZM7.247920000000001,11.944103515624999C7.19192,11.944103515624999,7.14392,11.928103515625,7.09592,11.904103515625L4.319922,10.400103515625C4.223922,10.344103515625001,4.159923,10.240103515625,4.151922,10.128103515625L4.03192234,6.808103515625C4.02392244,6.688103515625,4.0879226,6.584103515624999,4.191922,6.520103515624999C4.295922,6.464103515625,4.415922,6.464103515625,4.519922,6.520103515624999L7.41592,8.272103515625C7.51192,8.328103515625,7.56792,8.432103515625,7.56792,8.544103515625L7.56792,11.616103515625C7.56792,11.728103515625001,7.51192,11.832103515625,7.40792,11.888103515625C7.35992,11.928103515625,7.30392,11.944103515624999,7.247920000000001,11.944103515624999ZM4.7839220000000005,9.920103515625L6.92792,11.088103515625L6.92792,8.728103515625001L4.687922,7.376103515625L4.7839220000000005,9.920103515625ZM8.52792,11.944103515624999C8.47192,11.944103515624999,8.40792,11.928103515625,8.359919999999999,11.896103515625C8.263919999999999,11.840103515625,8.20792,11.736103515625,8.20792,11.624103515625L8.20792,8.552103515625C8.20792,8.440103515625001,8.27192,8.328103515625,8.36792,8.272103515625L11.487919999999999,6.520103515624999C11.59192,6.464103515625,11.71192,6.464103515625,11.80792,6.528103515625C11.90392,6.592103515625,11.96792,6.6961035156249995,11.95992,6.8161035156250005L11.83992,10.136103515624999C11.83192,10.256103515625,11.76792,10.360103515625,11.663920000000001,10.408103515625001L8.663920000000001,11.920103515625C8.631920000000001,11.936103515625,8.583919999999999,11.944103515624999,8.52792,11.944103515624999ZM8.84792,8.736103515625L8.84792,11.104103515624999L11.21592,9.912103515624999L11.30392,7.352103515625L8.84792,8.736103515625Z"
                fill="#FF9D4D"
                fill-opacity="1"
                style="mix-blend-mode: passthrough"
              />
            </g>
          </g>
        </svg>
        {{ $t('dataCenter.formulaConfigDisplay') }}
      </div>
      <div class="real-value">
        {{ realValue }}
      </div>
    </div>
  </div>
</template>

<script>
import Tribute from 'tributejs'
import { POUNDList } from '../const'
import '../style/tribute.less'
export default {
  props: {
    atList: { type: Array, default: () => [] },
    //输入框默认值
    defaultValue: {
      type: String,
    },
    //输出默认值
    defaultValueZh: {
      type: String,
    },
  },
  data() {
    return {
      virtualValue: '', //输入框中的值
      realValue: '', //展示栏中的值
      tributeMultipleTriggers: null,
      /*atList: [
				 {
				 name: "字段1",
				 value: "closeCaseMoney"
				 },
				 {
				 name: "字段12",
				 value: "filingCaseMoney"
				 }
				 ],*/
      poundList: POUNDList,
      /*[
				 { name: "加", value: "+" },
				 { name: "減", value: "-" },
				 { name: "乘", value: "*" },
				 { name: "除", value: "/" },
				 { name: "最大值( , )", value: "MAX" },
				 { name: "平均值( , )", value: "AVERAGE" }
				 ]*/
    }
  },
  mounted() {
    this.tributeDestroy()
    this.tributeInit()
  },
  beforeDestroy() {
    this.tributeDestroy()
  },
  watch: {
    defaultValue: {
      handler(cur) {
        this.virtualValue = cur
      },
      deep: true,
      immediate: true,
    },
    defaultValueZh: {
      handler(cur) {
        this.realValue = cur
      },
      deep: true,
      immediate: true,
    },
    atList: {
      handler(cur) {
        if (cur.length) {
          this.tributeDestroy()
          this.tributeInit()
        }
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    tributeInit() {
      this.tributeMultipleTriggers = new Tribute({
        collection: [
          {
            // The function that gets call on select that retuns the content to insert
            selectTemplate: function (item) {
              /*if (this.range.isContentEditable(this.current.element)) {
								 return (
								 `<span contenteditable="false">
								 <span
								 class="at-item"
								 title="${item.original.key}"
								 >
								 @${item.original.key}
								 </span>
								 </span>`
								 );
								 }*/
              return '@' + item.original.name
            },
            // the array of objects
            values: this.atList,
            lookup: 'name',
            fillAttr: 'name',
          },
          {
            // The symbol that starts the lookup
            trigger: '#',
            loadingItemTemplate: '<div style="padding: 16px">Loading</div>',
            // The function that gets call on select that retuns the content to insert
            selectTemplate: function (item) {
              /*if (this.range.isContentEditable(this.current.element)) {
								 return (
								 `<span contenteditable="false">
								 <span class="pound-item" title="${item.original.name}">
								 #${item.original.value}
								 </span>
								 </span>`
								 );
								 }*/
              return '#' + item.original.name
            },
            // function retrieving an array of objects
            values: this.poundList,
            lookup: 'name',
            fillAttr: 'name',
          },
        ],
      })
      this.tributeMultipleTriggers.attach(
        document.getElementById('testMultiple')
      )
    },
    tributeDestroy() {
      if (
        this.tributeMultipleTriggers &&
        document.querySelector('.tribute-container')
      ) {
        this.tributeMultipleTriggers = null
        document.body.removeChild(document.querySelector('.tribute-container'))
      }
    },
    /**
     * @description:輸入框 @input
     * @param e
     */
    onInput(e) {
      let _this = this
      /*	let atMatch = e.match(/@[\u4E00-\u9FA5]+\d+/g), poundMatch = e.match(/#[\u4E00-\u9FA5]+/g);
				 console.log("atMatch：", atMatch, "poundMatch:", poundMatch);*/
      let atPattern = /@[\u4E00-\u9FA5]+\d+/g,
        poundPattern = /#[\u4E00-\u9FA5]+/g
      let replacements = [
        {
          pattern: atPattern,
          replacement: function (match) {
            let currentAtData = _this.atList.find(
              (item) => `@${item.name}` === match
            )
            return currentAtData ? currentAtData.value : match
          },
        },
        {
          pattern: poundPattern,
          replacement: function (match) {
            let currentPoundData = _this.poundList.find(
              (item) => `#${item.name.replace('( , )', '')}` === match
            )
            return currentPoundData ? currentPoundData.value : match
          },
        },
      ]
      this.realValue = e
      replacements.forEach((item) => {
        this.realValue = this.realValue.replace(item.pattern, item.replacement)
      })
      this.$emit('input', this.virtualValue, this.realValue)
    },
  },
}
</script>
<style lang="less" scoped>
.textarea-container {
  display: flex;

  &-inner {
    &--title {
      font-weight: 400;
      font-size: 14px;
      color: var(--text-color-secondary);
      line-height: 20px;
      margin-bottom: 10px;
    }
  }

  ::v-deep .el-textarea {
    width: 350px;
    border: 1px solid var(--border-color);

    .el-textarea__inner {
      height: 152px;
    }
  }

  .real-value {
    padding: 0 10px;
    margin-left: 14px;
    width: 268px;
    height: 152px;
    background: var(--bg-color);
    border-radius: 4px 4px 4px 4px;
    overflow-y: auto;
    font-size: 12px;
    line-height: 20px;
    color: var(--text-color-secondary);
    border: 1px solid var(--border-color);
  }
}
</style>
