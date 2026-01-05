<template>
  <el-drawer
    :title="drawer.title"
    :visible.sync="drawer.visible"
    :size="drawer.width"
    :before-close="handleClose"
    :destroy-on-close="true"
    :show-close="true"
  >
    <div class="formula-configuration fc">
      <div class="fc-title">选择计算字段</div>
      <div class="fc-field">
        <div class="fc-field-cascader">
          <div class="cascader-panel">
            <div
              class="cascader-menu"
              v-for="(item, index) in computedList"
              :key="index"
            >
              <div class="cascader-menu_list" v-if="item.list">
                <div
                  class="cascader-menu_list-node"
                  v-for="(itemX, indexX) in item.list"
                  :key="indexX"
                  :class="{
                    'node-active':
                      itemX.value === currentSelectData[index]?.value &&
                      itemX.children,
                    'node-select': currentData.find(
                      (current) => current.value === itemX.value
                    ),
                  }"
                  @click="handleSelectField(itemX, index)"
                >
                  <div class="node-label">
                    <div v-if="!itemX.children" class="node-label-selected" />
                    {{ itemX.label }}
                  </div>
                  <div v-if="itemX.children" class="node-icon">
                    <i class="el-icon-arrow-right"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="current-select">
          {{ currentData.map((item) => item.label).join('/') }}
        </div>
      </div>
      <div class="fc-subtitle">
        选择具体字段后点击"添加字段"按钮，可将字段插入到计算公式中
      </div>
      <div class="fc-select-field">
        <div
          class="select-field-item"
          v-for="(item, index) in selectedFields"
          :key="index"
          @mousedown.prevent="insertField(item)"
        >
          <span>{{ item.label }}</span>
          <div
            class="select-field-item-close"
            @mousedown.stop.prevent="removeField(index)"
          >
            <i class="el-icon-close" />
          </div>
        </div>
      </div>
      <div class="fc-operation">
        <el-button
          type="primary"
          size="small"
          icon="el-icon-plus"
          @click="addFieldToFormula"
          >添加字段到公式</el-button
        >
        <el-button
          type=""
          size="small"
          icon="el-icon-delete"
          @click="clearSelectedFields"
          >清除已选</el-button
        >
      </div>
      <div class="fc-title">计算公式</div>
      <div class="fc-formula-box">
        <div class="fc-formula-box-header">
          <div class="base-formula">
            <div
              class="fc-formula-box-header-item"
              v-for="(item, index) in baseFormula"
              :key="index"
              @mousedown.prevent="insertFormula(item.formula)"
            >
              {{ item.label }}
            </div>
          </div>
          <div class="expanded-formula">
            <div
              class="fc-formula-box-header-item"
              v-for="(item, index) in expandedFormula"
              :key="index"
              @mousedown.prevent="insertFormula(item.formula)"
            >
              {{ item.label }}
            </div>
          </div>
          <el-button @click="clearFormula">清空</el-button>
        </div>
        <div
          class="fc-formula-box-content"
          :class="{ 'has-error': formulaError }"
          ref="formulaInput"
          contenteditable="true"
          @input="handleInput"
          @keyup="saveSelection"
          @mouseup="saveSelection"
          @focus="saveSelection"
          @click="saveSelection"
          placeholder="请输入计算公式..."
        />
        <div v-if="formulaError" class="fc-formula-error">
          <i class="el-icon-warning"></i>
          <span>{{ formulaError }}</span>
        </div>
      </div>

      <div class="fc-title">可用函数参考</div>
      <div class="fc-formula-select">
        <div
          v-for="(item, index) in expandedFormula"
          :key="index"
          class="fc-formula-select-item"
          @mousedown.prevent="insertFormula(item.formula)"
        >
          <span>{{ item.label }}({{ item.desc }})</span>
          <span>{{ item.desc }}</span>
        </div>
      </div>
    </div>
    <div class="fc-footer">
      <el-button @click="handleReset">重置</el-button>
      <el-button type="primary" @click="handleSubmit">保存配置</el-button>
    </div>
  </el-drawer>
</template>
<script>
import {
  interfaceTree,
  standardQuotaFormula,
  getStandardQuotaFormula,
} from '../api/limit'
export default {
  props: {
    quotaCardId: {
      type: [String, Number, null],
      default: null,
    },
  },
  data() {
    return {
      drawer: {
        visible: false,
        title: '标准额度计算公式配置',
        width: '40%',
      },
      // 配置项：是否过滤掉没有三层的数据
      // 设置为 true：只保留有完整三层结构的数据（第一级 -> 第二级 -> 第三级）
      // 设置为 false：不过滤，保留所有数据
      // 如需放开限制，将此处改为 false 即可
      filterIncompleteTreeData: true,
      fieldDataList: [],
      currentSelectData: [],
      currentData: [],
      fieldPathMap: new Map(), // 存储每个节点对应的完整路径
      selectedFields: [],
      formulaContent: null,
      savedRange: null,
      formulaError: '请输入计算公式',
      baseFormula: [
        {
          label: '+',
          formula: '+',
        },
        {
          label: '-',
          formula: '-',
        },
        {
          label: '*',
          formula: '*',
        },
        {
          label: '/',
          formula: '/',
        },
        {
          label: '(',
          formula: '(',
        },
        {
          label: ')',
          formula: ')',
        },
      ],
      expandedFormula: [
        {
          label: 'IF',
          formula: 'IF(条件, 值1, 值2)',
          desc: '条件判断',
        },
        {
          label: 'MAX',
          formula: 'MAX(值1, 值2...)',
          desc: '取最大值',
        },
        {
          label: 'MIN',
          formula: 'MIN(值1, 值2...)',
          desc: '取最小值',
        },
        {
          label: 'ROUND',
          formula: 'ROUND(值,小数位)',
          desc: '四舍五入',
        },
        {
          label: 'SUM',
          formula: 'SUM(值1, 值2...)',
          desc: '求和',
        },
      ],
    }
  },
  watch: {},
  computed: {
    computedList() {
      if (!this.currentSelectData.length) {
        let firstLevel = this.fieldDataList
        let secondLevel = firstLevel[0]?.children || []
        let thirdLevel = secondLevel[0]?.children || []
        return [
          { list: firstLevel },
          { list: secondLevel },
          { list: thirdLevel },
        ]
      } else {
        let count = this.currentSelectData.length
        let firstLevel = this.fieldDataList
        let secondLevel, thirdLevel
        switch (count) {
          case 1:
            secondLevel =
              firstLevel.find(
                (item) => item.value === this.currentSelectData[0].value
              )?.children || []
            thirdLevel = secondLevel[0]?.children || []
            break
          case 2:
            secondLevel =
              firstLevel.find(
                (item) => item.value === this.currentSelectData[0].value
              )?.children || []
            thirdLevel = secondLevel[0]?.children || []
            break
          case 3:
            secondLevel =
              firstLevel.find(
                (item) => item.value === this.currentSelectData[0].value
              )?.children || []
            thirdLevel =
              secondLevel.find(
                (item) => item.value === this.currentSelectData[1].value
              )?.children || []
            break
        }

        return [
          { list: firstLevel },
          { list: secondLevel },
          { list: thirdLevel },
        ]
      }
    },
  },
  methods: {
    handleSubmit() {
      if (this.formulaContent) {
        let formulaFields = []
        this.selectedFields.forEach((item) => {
          if (
            item.list.length == 3 &&
            this.formulaContent.includes(item.label)
          ) {
            formulaFields.push({
              fullPath: item.label,
              ...item.list[2].params,
              allPaths: item.list.map((item) => item.data),
            })
          }
        })
        if (formulaFields.length > 0) {
          standardQuotaFormula({
            formulaFields: formulaFields,
            rawFormula: this.formulaContent,
            quotaCardId: this.quotaCardId,
            selectedFields: this.selectedFields,
          }).then((res) => {
            if (res.code == 200) {
              this.$message.success('保存成功')
              this.handleClose()
            }
          })
        } else {
          this.$message.error('计算公式中没有匹配的字段')
        }
      }
    },
    initData() {
      interfaceTree().then((res) => {
        if (res.code == 200) {
          // 处理三级数组数据，转换为树形结构
          const treeData = this.transformToTree(res.data)
          this.fieldDataList = treeData
          // 生成路径映射
          this.fieldPathMap = this.buildPathMap(treeData)
        }
      })

      getStandardQuotaFormula(this.quotaCardId).then((res) => {
        if (res.code == 200) {
          this.selectedFields = res.data.selectedFields || []
          this.formulaContent = res.data.rawFormula || ''
          this.$nextTick(() => {
            this.$nextTick(() => {
              const inputEl = this.$refs.formulaInput
              if (inputEl && this.formulaContent) {
                inputEl.textContent = this.formulaContent
                // 校验公式
                this.validateFormula()
              }
            })
          })
        }
      })
    },
    // 将三级数组转换为树形结构
    transformToTree(data) {
      if (!Array.isArray(data)) {
        return []
      }

      const result = data.map((firstLevel) => {
        const firstLevelItem = {
          label: firstLevel.dataName || '',
          value: firstLevel.interfaceSourceNo || '',
          data: { ...firstLevel },
        }

        // 处理第二级：interfaceManageList
        if (
          firstLevel.interfaceManageList &&
          Array.isArray(firstLevel.interfaceManageList) &&
          firstLevel.interfaceManageList.length > 0
        ) {
          const secondLevelItems = firstLevel.interfaceManageList.map(
            (secondLevel) => {
              const secondLevelItem = {
                label: secondLevel.interfaceName || '',
                value: secondLevel.interfaceManageNo || '',
                data: { ...secondLevel },
              }

              // 处理第三级：fields
              if (
                secondLevel.fields &&
                Array.isArray(secondLevel.fields) &&
                secondLevel.fields.length > 0
              ) {
                secondLevelItem.children = secondLevel.fields.map(
                  (thirdLevel) => ({
                    label: thirdLevel.interfaceFieIdDescription,
                    value: thirdLevel.interfaceFieIdManage,
                    params: {
                      dataType: thirdLevel.interfaceFieIdDataType,
                      fieldCode: thirdLevel.interfaceFieIdName,
                      fieldName: thirdLevel.interfaceFieIdAlias,
                      manageNo: thirdLevel.interfaceManageNo,
                    },
                    data: { ...thirdLevel },
                  })
                )
              }

              return secondLevelItem
            }
          )

          // 根据配置项决定是否过滤
          if (this.filterIncompleteTreeData) {
            // 过滤模式：只保留有第三级的第二级项
            const filteredSecondLevelItems = secondLevelItems.filter(
              (item) => item.children && item.children.length > 0
            )

            // 只有当第二级中有至少一个有第三级的项时，才设置 children
            if (filteredSecondLevelItems.length > 0) {
              firstLevelItem.children = filteredSecondLevelItems
              return firstLevelItem
            }
            // 如果没有第二级或第二级中没有有第三级的项，返回 null
            return null
          } else {
            // 不过滤模式：保留所有数据
            firstLevelItem.children = secondLevelItems
            return firstLevelItem
          }
        }

        // 如果没有第二级，根据配置决定是否返回
        return this.filterIncompleteTreeData ? null : firstLevelItem
      })

      // 根据配置项决定是否过滤最终结果
      return this.filterIncompleteTreeData
        ? result.filter((item) => item !== null)
        : result
    },
    // 构建路径映射，为每个节点存储所在链路的完整路径（从根到最深层）
    buildPathMap(treeData) {
      const pathMap = new Map()

      // 递归遍历树，构建从根到最深层的完整路径
      const traverse = (nodes, parentPath = []) => {
        if (!Array.isArray(nodes)) return

        nodes.forEach((node) => {
          // 当前路径 = 父路径 + 当前节点
          const currentPath = [
            ...parentPath,
            {
              label: node.label,
              value: node.value,
              params: node?.params,
              data: node?.data,
            },
          ]

          // 如果有子节点，继续递归到最深层
          if (
            node.children &&
            Array.isArray(node.children) &&
            node.children.length > 0
          ) {
            traverse(node.children, currentPath)
          } else {
            // 到达最深层（叶子节点），此时 currentPath 就是完整链路
            // 为路径上的所有节点都存储这个完整链路
            currentPath.forEach((pathNode) => {
              pathMap.set(pathNode.value, currentPath)
            })
          }
        })
      }

      traverse(treeData)
      console.log(pathMap, 'pathMap')

      return pathMap
    },
    handleSelectField(item, level) {
      const fullPath = this.fieldPathMap.get(item.value) || []
      console.log(fullPath, 'fullPath')

      this.currentSelectData = fullPath

      if (!item.children) {
        this.currentData = fullPath
      }
    },
    addFieldToFormula() {
      if (!this.currentData.length) return
      let path = this.currentData.map((item) => item.label).join('/')
      if (this.selectedFields.findIndex((item) => item.label === path) === -1)
        this.selectedFields.push({
          label: path,
          list: this.currentData,
        })
    },
    clearSelectedFields() {
      this.selectedFields = []
    },

    handleOpen() {
      this.drawer.visible = true
      // 等待 drawer 打开后再初始化数据
      this.$nextTick(() => {
        this.initData()
      })
    },
    handleClose() {
      this.drawer.visible = false
    },
    // 保存当前光标位置和选中文本（使用文本偏移量）
    saveSelection() {
      const inputEl = this.$refs.formulaInput
      if (!inputEl) return

      const selection = window.getSelection()
      if (selection.rangeCount > 0) {
        const range = selection.getRangeAt(0)
        // 检查 range 是否在 inputEl 内部
        const container = range.commonAncestorContainer

        // 更严格的检查：确保容器节点在 inputEl 内部
        if (inputEl === container || inputEl.contains(container)) {
          try {
            // 计算文本偏移量
            const preCaretRange = range.cloneRange()
            preCaretRange.selectNodeContents(inputEl)
            preCaretRange.setEnd(range.endContainer, range.endOffset)
            const endOffset = preCaretRange.toString().length

            preCaretRange.setEnd(range.startContainer, range.startOffset)
            const startOffset = preCaretRange.toString().length

            this.savedRange = {
              start: startOffset,
              end: endOffset,
            }
          } catch (e) {
            // 如果计算失败，不更新 savedRange，保持之前的值
            console.warn('保存光标位置失败:', e)
          }
        }
      }
    },
    // 恢复光标位置（根据文本偏移量）
    restoreSelection() {
      const inputEl = this.$refs.formulaInput
      if (!inputEl) return

      const selection = window.getSelection()
      selection.removeAllRanges()

      if (!this.savedRange) {
        // 如果没有保存的位置，将光标移到末尾
        const range = document.createRange()
        if (inputEl.childNodes.length === 0) {
          // 如果内容为空，直接设置到容器
          range.setStart(inputEl, 0)
          range.setEnd(inputEl, 0)
        } else {
          range.selectNodeContents(inputEl)
          range.collapse(false)
        }
        selection.addRange(range)
        return
      }

      try {
        const range = document.createRange()
        const startOffset = this.savedRange.start
        const endOffset = this.savedRange.end

        // 如果内容为空，直接设置到容器
        if (inputEl.childNodes.length === 0) {
          range.setStart(inputEl, 0)
          range.setEnd(inputEl, 0)
          selection.addRange(range)
          return
        }

        // 根据文本偏移量找到对应的节点和位置
        const setRangeByOffset = (container, offset, isStart) => {
          let currentOffset = 0
          const walker = document.createTreeWalker(
            container,
            NodeFilter.SHOW_TEXT,
            null,
            false
          )

          let node
          while ((node = walker.nextNode())) {
            const nodeLength = node.textContent.length
            if (currentOffset + nodeLength >= offset) {
              const nodeOffset = offset - currentOffset
              if (isStart) {
                range.setStart(node, nodeOffset)
              } else {
                range.setEnd(node, nodeOffset)
              }
              return true
            }
            currentOffset += nodeLength
          }

          // 如果没找到，设置到末尾
          const lastNode = container.lastChild
          if (lastNode && lastNode.nodeType === Node.TEXT_NODE) {
            if (isStart) {
              range.setStart(lastNode, lastNode.textContent.length)
            } else {
              range.setEnd(lastNode, lastNode.textContent.length)
            }
          } else {
            if (isStart) {
              range.setStart(container, container.childNodes.length)
            } else {
              range.setEnd(container, container.childNodes.length)
            }
          }
          return false
        }

        setRangeByOffset(inputEl, startOffset, true)
        setRangeByOffset(inputEl, endOffset, false)

        selection.addRange(range)
      } catch (e) {
        console.error('恢复光标位置失败:', e)
        // 如果恢复失败，将光标移到末尾
        const range = document.createRange()
        if (inputEl.childNodes.length === 0) {
          range.setStart(inputEl, 0)
          range.setEnd(inputEl, 0)
        } else {
          range.selectNodeContents(inputEl)
          range.collapse(false)
        }
        selection.addRange(range)
      }
    },
    // 插入内容到光标位置或替换选中文本
    insertText(text) {
      const inputEl = this.$refs.formulaInput
      if (!inputEl) return

      // 确保输入框获得焦点
      inputEl.focus()

      // 等待焦点恢复后再操作，需要多个 nextTick 确保 DOM 更新完成
      this.$nextTick(() => {
        this.$nextTick(() => {
          // 恢复光标位置
          this.restoreSelection()

          const selection = window.getSelection()
          let range

          if (selection.rangeCount === 0) {
            // 如果没有选中范围，创建新的 range
            range = document.createRange()
            if (inputEl.childNodes.length === 0) {
              // 如果内容为空，直接设置到容器
              range.setStart(inputEl, 0)
              range.setEnd(inputEl, 0)
            } else {
              // 将光标移到末尾
              range.selectNodeContents(inputEl)
              range.collapse(false)
            }
            selection.addRange(range)
          } else {
            range = selection.getRangeAt(0)
          }

          // 删除选中的内容（如果有）
          range.deleteContents()

          // 如果容器为空，先创建一个文本节点
          if (inputEl.childNodes.length === 0) {
            const textNode = document.createTextNode('')
            inputEl.appendChild(textNode)
            range.setStart(textNode, 0)
            range.setEnd(textNode, 0)
          }

          // 插入新内容
          const textNode = document.createTextNode(text)
          range.insertNode(textNode)

          // 将光标移到插入内容之后
          range.setStartAfter(textNode)
          range.collapse(true)
          selection.removeAllRanges()
          selection.addRange(range)

          // 保存新的光标位置
          this.saveSelection()

          // 更新 formulaContent
          this.formulaContent = inputEl.textContent || inputEl.innerText || ''

          // 校验公式
          this.validateFormula()
        })
      })
    },
    // 插入字段
    insertField(field) {
      const inputEl = this.$refs.formulaInput
      if (!inputEl) return

      // 检查输入框是否有焦点
      if (document.activeElement !== inputEl) {
        return
      }

      // 检查是否有有效的光标位置
      const selection = window.getSelection()
      if (selection.rangeCount === 0) {
        return
      }

      const range = selection.getRangeAt(0)
      const container = range.commonAncestorContainer

      // 检查光标是否在输入框内
      if (inputEl !== container && !inputEl.contains(container)) {
        return
      }

      // 在 mousedown 时立即保存光标位置（此时输入框还没有失去焦点）
      this.saveSelection()
      // 使用 setTimeout 延迟执行插入，确保保存操作完成
      setTimeout(() => {
        this.insertText(field.label || field)
      }, 10)
    },
    // 插入公式
    insertFormula(formula) {
      const inputEl = this.$refs.formulaInput
      if (!inputEl) return

      // 检查输入框是否有焦点
      if (document.activeElement !== inputEl) {
        return
      }

      // 检查是否有有效的光标位置
      const selection = window.getSelection()
      if (selection.rangeCount === 0) {
        return
      }

      const range = selection.getRangeAt(0)
      const container = range.commonAncestorContainer

      // 检查光标是否在输入框内
      if (inputEl !== container && !inputEl.contains(container)) {
        return
      }

      // 在 mousedown 时立即保存光标位置（此时输入框还没有失去焦点）
      this.saveSelection()
      // 使用 setTimeout 延迟执行插入，确保保存操作完成
      setTimeout(() => {
        this.insertText(formula)
      }, 10)
    },
    // 处理输入
    handleInput(event) {
      this.formulaContent =
        event.target.textContent || event.target.innerText || ''
      this.saveSelection()
      // 校验公式
      this.validateFormula()
    },
    // 校验公式格式
    validateFormula() {
      const formula = this.formulaContent.trim()

      // 如果为空，恢复初始提示
      if (!formula) {
        this.formulaError = '请输入计算公式'
        return true
      }

      // 1. 检查括号匹配
      let leftParen = 0
      let rightParen = 0
      for (let i = 0; i < formula.length; i++) {
        if (formula[i] === '(') {
          leftParen++
        } else if (formula[i] === ')') {
          rightParen++
          // 右括号不能多于左括号
          if (rightParen > leftParen) {
            this.formulaError = '括号不匹配：右括号多于左括号'
            return false
          }
        }
      }
      if (leftParen !== rightParen) {
        this.formulaError = `括号不匹配：缺少${
          leftParen > rightParen ? '右' : '左'
        }括号`
        return false
      }

      // 2. 检查运算符连续（不允许连续的运算符，除了负号）
      const operatorPattern = /[+\-*/]{2,}/
      // 排除负号的情况（如 -1, IF(-1, 1, 2)）
      const validNegativePattern = /(^|[+\-*/\(,])-(\d|\(|\w)/
      const invalidOperatorMatch = formula.match(operatorPattern)
      if (invalidOperatorMatch) {
        // 检查是否是有效的负号
        const matchIndex = invalidOperatorMatch.index
        const beforeChar = matchIndex > 0 ? formula[matchIndex - 1] : ''
        const afterChar =
          matchIndex + invalidOperatorMatch[0].length < formula.length
            ? formula[matchIndex + invalidOperatorMatch[0].length]
            : ''

        // 如果连续运算符不是以负号开头，或者负号后面不是数字/括号/字母，则报错
        if (
          !invalidOperatorMatch[0].startsWith('-') ||
          !/[\d\(a-zA-Z]/.test(afterChar)
        ) {
          this.formulaError = '运算符格式错误：不能连续使用运算符'
          return false
        }
      }

      // 3. 检查函数格式（IF, MAX, MIN, ROUND等）
      const functionPattern = /(IF|MAX|MIN|ROUND)\s*\(/gi
      const functionMatches = Array.from(formula.matchAll(functionPattern))

      // 定义占位符映射
      const placeholderMap = {
        IF: ['条件', '值1', '值2'],
        MAX: ['值1', '值2...'],
        MIN: ['值1', '值2...'],
        ROUND: ['值', '小数位'],
      }

      for (const match of functionMatches) {
        const funcName = match[1]
        const startIndex = match.index + match[0].length
        // 查找对应的右括号
        let parenCount = 1
        let endIndex = startIndex
        let found = false

        for (let i = startIndex; i < formula.length; i++) {
          if (formula[i] === '(') parenCount++
          if (formula[i] === ')') parenCount--
          if (parenCount === 0) {
            endIndex = i
            found = true
            break
          }
        }

        if (!found) {
          this.formulaError = `函数 ${funcName} 缺少右括号`
          return false
        }

        // 检查函数参数
        const params = formula.substring(startIndex, endIndex).trim()

        // 先检查是否包含占位符
        const placeholders = placeholderMap[funcName.toUpperCase()]
        if (placeholders) {
          const hasPlaceholder = placeholders.some((placeholder) => {
            // 检查占位符是否在参数中
            if (placeholder.includes('...')) {
              // 对于 "值1, 值2..." 这种格式，检查是否包含 "值1" 或 "值2"
              return params.includes('值1') || params.includes('值2')
            }
            // 直接检查是否包含占位符文本
            return params.includes(placeholder)
          })

          if (hasPlaceholder) {
            const placeholderText = placeholders
              .map((p) => (p.includes('...') ? '值1、值2等' : p))
              .join('、')
            this.formulaError = `请替换 ${funcName} 函数中的占位符：${placeholderText}`
            return false
          }
        }

        // 检查参数数量（在占位符已替换的前提下）
        const paramList = params
          .split(',')
          .map((p) => p.trim())
          .filter((p) => p)

        if (funcName.toUpperCase() === 'IF') {
          if (paramList.length !== 3) {
            this.formulaError = 'IF 函数需要3个参数：IF(条件, 值1, 值2)'
            return false
          }
        } else if (funcName.toUpperCase() === 'ROUND') {
          if (paramList.length !== 2) {
            this.formulaError = 'ROUND 函数需要2个参数：ROUND(值, 小数位)'
            return false
          }
        } else if (
          funcName.toUpperCase() === 'MAX' ||
          funcName.toUpperCase() === 'MIN'
        ) {
          if (paramList.length < 1) {
            this.formulaError = `${funcName} 函数至少需要1个参数`
            return false
          }
        }
      }

      // 4. 检查是否以运算符开头或结尾（负号除外）
      if (/^[+*/]/.test(formula)) {
        this.formulaError = '公式不能以运算符开头（负号除外）'
        return false
      }
      if (/[+\-*/]$/.test(formula)) {
        this.formulaError = '公式不能以运算符结尾'
        return false
      }

      // 5. 检查是否有未闭合的字符串或字段引用（简单检查）
      // 这里可以根据实际需求扩展

      // 校验通过
      this.formulaError = ''
      return true
    },
    // 清空公式
    clearFormula() {
      const inputEl = this.$refs.formulaInput
      if (inputEl) {
        inputEl.textContent = ''
        this.formulaContent = ''
        this.savedRange = null
        this.formulaError = '请输入计算公式'
      }
    },
    // 移除字段
    removeField(index) {
      this.selectedFields.splice(index, 1)
    },
    handleReset() {
      this.selectedFields = []
      this.currentData = []
      this.currentSelectData = []
      this.clearFormula()
    },
  },
  mounted() {
    // 初始化时保存光标位置
    this.$nextTick(() => {
      this.saveSelection()
    })
  },
}
</script>
<style lang="less" scoped>
::v-deep .el-drawer {
  font-family: PingFang SC-Medium;
  .el-drawer__header {
    padding: 20px;
    margin-bottom: 0px;
  }
  .el-drawer__body {
    padding: 0px;
  }
}
.formula-configuration,
.fc {
  font-family: PingFang SC-Medium;
  height: calc(100% - 60px);
  overflow-y: auto;
  padding: 0px 20px 40px;
  &-title {
    font-size: 14px;
    color: #000;
    margin-bottom: 10px;
  }

  &-subtitle {
    font-size: 12px;
    color: #333;
    margin-bottom: 10px;
  }

  &-field {
    width: 100%;
    border-radius: 8px;
    border: 1px solid #e5e5e5;
    margin-bottom: 10px;

    &-cascader {
      height: 240px;
      overflow: hidden;
      .cascader-panel {
        width: 100%;
        height: 100%;
        display: flex;
        .cascader-menu {
          flex: 1;
          min-width: 0;
          display: flex;
          flex-direction: column;
          border-left: 1px solid #e5e5e5;
          overflow-y: auto;

          &_list {
            flex: 1;
            min-height: 0;
            &-node {
              display: flex;
              align-items: center;
              justify-content: space-between;
              padding: 10px;
              width: 100%;
              cursor: pointer;

              .node-label {
                display: flex;
                align-items: center;
                width: 100%;

                &-selected {
                  margin-right: 6px;
                  width: 16px;
                  height: 16px;
                  border-radius: 50%;
                  border: 1px solid #000;
                }
              }

              &:hover {
                background-color: rgba(#2888e8, 0.1);
              }
            }
            .node-active {
              color: #2888e8;
            }
            .node-select {
              .node-label-selected {
                border-color: #2888e8;
                background: #fff;
                position: relative;
                &::after {
                  content: '';
                  display: block;
                  width: 8px;
                  height: 8px;
                  position: absolute;
                  top: 50%;
                  left: 50%;
                  transform: translate(-50%, -50%);
                  border-radius: 50%;
                  background: #2888e8;
                }
              }
            }
          }

          &:last-child {
            border-right: none;
          }

          -ms-overflow-style: none; // IE/Edge
          scrollbar-width: none; // Firefox
          &::-webkit-scrollbar {
            display: none; // Chrome, Safari, Edge
          }
        }
      }
    }

    .current-select {
      height: 36px;
      padding: 10px;
      background: #f8fafc;
      font-size: 14px;
      border-top: 1px solid #e5e5e5;
    }
  }

  .fc-select-field {
    min-height: 40px;
    border: 1px dashed #e5e5e5;
    padding: 10px;
    gap: 10px;
    display: flex;
    flex-wrap: wrap;
    border-radius: 4px;

    .select-field-item {
      display: inline-block;
      padding: 4px;
      background: rgba(#2888e8, 0.1);
      color: #2888e8;
      border-radius: 8px;
      border: 1px solid #e5e5e5;
      font-size: 12px;
      display: flex;
      align-items: center;
      cursor: pointer;

      &:hover {
        background: rgba(#2888e8, 0.2);
      }

      .select-field-item-close {
        display: inline-block;
        width: 16px;
        height: 16px;
        border-radius: 50%;
        margin-left: 4px;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;

        &:hover {
          background: rgba(#2888e8, 0.1);
        }
      }
    }
  }

  .fc-operation {
    display: flex;
    margin-top: 10px;
    margin-bottom: 20px;
    .el-button {
      height: 38px;
      font-size: 14px;
    }
  }

  .fc-formula-box {
    border: 1px solid #e5e5e5;
    border-radius: 4px;
    margin-top: 10px;
    position: relative;
    margin-bottom: 40px;
    &-header {
      display: flex;
      padding: 8px;
      background: #f9fafb;
      border-bottom: 1px solid #e5e5e5;

      &-item {
        padding: 4px 8px;
        border-radius: 4px;
        border: 1px solid #e5e5e5;
        font-size: 12px;
        margin-right: 4px;
        cursor: pointer;
      }

      .base-formula,
      .expanded-formula {
        display: flex;
        padding-right: 6px;
        margin-right: 10px;
        border-right: 1px solid #e5e5e5;
      }

      .expanded-formula {
        .fc-formula-box-header-item {
          border-color: #fde68a;
          background: #fef3c7;
          color: #92400e;
        }
      }

      .el-button {
        font-size: 14px;
        padding: 4px 8px;
      }
    }

    &-content {
      padding: 10px;
      min-height: 100px;
      outline: none;
      word-break: break-all;
      white-space: pre-wrap;
      transition: border-color 0.3s;
      font-size: 14px;

      &:empty:before {
        content: attr(placeholder);
        color: #c0c4cc;
        pointer-events: none;
      }

      &:focus {
        border: none;
        outline: none;
      }
    }

    .fc-formula-error {
      position: absolute;
      bottom: -24px;
      left: 0;
      color: #f56c6c;
      font-size: 12px;
      display: flex;
      align-items: center;
      gap: 6px;

      i {
        font-size: 14px;
      }
    }
  }

  .fc-formula-select {
    height: 160px;
    overflow-y: auto;
    border: 1px solid #e5e5e5;
    border-radius: 4px;
    .fc-formula-select-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 10px;
      border-bottom: 1px solid #e5e5e5;
      cursor: pointer;
      font-size: 12px;
      color: #000;

      &:hover {
        background: rgba(#2888e8, 0.1);
      }

      &:last-child {
        border-bottom: none;
      }
    }
  }
}
.fc-footer {
  height: 60px;
  padding: 0px 20px;
  border-top: 1px solid #e5e5e5;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  .el-button {
    height: 40px;
    font-size: 14px;
  }
}
</style>
