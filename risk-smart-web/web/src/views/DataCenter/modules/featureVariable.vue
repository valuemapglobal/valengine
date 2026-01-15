<template>
  <div class="feature-variable">
    <ModuleTable
      :title="$t('featureVariable.title')"
      :placeholder="$t('featureVariable.inputModuleName')"
      :buttonText="$t('featureVariable.addModule')"
      :params="moduleParams"
      :loading="moduleLoading"
      :tableColumn="moduleTableColumn"
      :tableData="moduleTableData"
      :total="moduleTotal"
      @search="onSearch($event, 'module')"
      @cellClick="cellClickHandle"
      @add="openDrawer($t('featureVariable.addModule'), 'addEditingModuleRef')"
      @edit="
        openDrawer(
          $t('featureVariable.editModule'),
          'addEditingModuleRef',
          $event
        )
      "
      @delete="onDelete($event, 'module')"
      @currentChange="handleCurrentChange($event, 'module')"
    />
    <ModuleTable
      :title="$t('featureVariable.attribute')"
      :placeholder="$t('featureVariable.inputAttribute')"
      titleIconType="2"
      :buttonText="$t('featureVariable.addAttribute')"
      :params="attributeParams"
      :cellClickDisabled="true"
      :loading="attributeLoading"
      :tableColumn="attributeTableColumn"
      :tableData="attributeTableData"
      :total="attributeTotal"
      @search="onSearch($event, 'attribute')"
      @add="
        openDrawer($t('featureVariable.addAttribute'), 'addEditingVariableRef')
      "
      @edit="
        openDrawer(
          $t('featureVariable.editAttribute'),
          'addEditingVariableRef',
          $event
        )
      "
      @delete="onDelete($event, 'attribute')"
      @currentChange="handleCurrentChange($event, 'attribute')"
    />
    <AddEditingModule
      ref="addEditingModuleRef"
      :title="drawerTitle"
      grade="2"
      :cascade-props="cascadeProps"
      :cascade-options="cascadeOptions"
      @submit="addEditingModuleSubmit"
    />
    <AddEditingVariable
      ref="addEditingVariableRef"
      :title="drawerTitle"
      @submit="addEditingVariableSubmit"
    />
  </div>
</template>

<script>
import ModuleTable from '../components/ModuleTable'
import AddEditingModule from '../components/AddEditingModule'
import AddEditingVariable from '../components/AddEditingVariable'
import {
  addAttributeData,
  addModuleData,
  deleteAttribute,
  deleteModule,
  editAttributeData,
  editModuleData,
  featureAttributeList,
  featureModuleList,
  findInterfaceInfo,
  findSourceInfo,
} from '@/views/DataCenter/api/featureVariable'
export default {
  name: 'featureVariable',
  components: {
    ModuleTable,
    AddEditingModule,
    AddEditingVariable,
  },
  computed: {
    moduleTableColumn() {
      return [
        {
          prop: 'index',
          label: this.$t('featureVariable.index'),
          width: '68',
          align: 'center',
        },
        {
          prop: 'name',
          label: this.$t('featureVariable.moduleName'),
          align: 'center',
        },
        {
          prop: 'code',
          label: this.$t('featureVariable.moduleCode'),
          align: 'center',
        },
        {
          prop: 'type',
          label: this.$t('featureVariable.moduleType'),
          align: 'center',
          type: 'tooltip',
          valueOption: 'typeOptions',
          tooltip: this.$t('featureVariable.tooltip'),
        },
        {
          prop: 'createTime',
          label: this.$t('common.createTime'),
          width: '180',
          align: 'center',
          type: 'date',
        },
      ]
    },
    attributeTableColumn() {
      return [
        {
          prop: 'index',
          label: this.$t('featureVariable.index'),
          width: '68',
          align: 'center',
        },
        {
          prop: 'name',
          label: this.$t('featureVariable.attributeName'),
          align: 'center',
        },
        {
          prop: 'code',
          label: this.$t('featureVariable.paramName'),
          align: 'center',
        },
        {
          prop: 'moduleName',
          label: this.$t('featureVariable.moduleName'),
          align: 'center',
        },
        {
          prop: 'createTime',
          label: this.$t('common.createTime'),
          width: '180',
          align: 'center',
          type: 'date',
        },
      ]
    },
  },
  data() {
    return {
      drawerTitle: '',
      //特征变量模块（一级）
      moduleParams: {
        page: 1,
        size: 10,
        name: undefined,
        moduleId: undefined, //当前选中模块id
        moduleName: undefined, //当前选中的模版名称
      },
      moduleLoading: false,
      moduleTableData: [],
      moduleTotal: 0,
      cascadeOptions: [],
      cascadeProps: {
        multiple: true,
        lazy: true, //是否动态加载子节点
        lazyLoad: this.getFindInterfaceInfo,
      },
      //特征变量属性（二级）
      attributeParams: {
        page: 1,
        size: 10,
        name: undefined,
        moduleId: undefined, //所属模块id
      },
      attributeLoading: false,
      attributeTableData: [],
      attributeTotal: 0,
    }
  },
  mounted() {
    this.getFeatureModuleList()
    this.getFindSourceInfo()
  },
  methods: {
    /**
     * 打开弹窗
     * @param title 弹窗标题
     * @param refName 弹窗ref
     * @param data 编辑时需回显的值
     */
    openDrawer(title, refName, data) {
      this.drawerTitle = title
      switch (refName) {
        case 'addEditingModuleRef':
          if (data) {
            //处理关联元数据的回显
            data.metadata.forEach((item) => {
              item.forEach((child, index) => {
                if (index === 0) {
                  // console.log("child", child);
                  const parentIndex = this.cascadeOptions.findIndex(
                    (parentItem) => parentItem.value === child
                  )
                  // console.log('this.cascadeOptions[parentIndex]',this.cascadeOptions[parentIndex])
                  if (
                    typeof parentIndex === 'number' &&
                    !this.cascadeOptions[parentIndex].children
                  ) {
                    findInterfaceInfo({
                      pageNum: 1,
                      pageSize: 999,
                      interfaceName: undefined,
                      sourceNo: child, //供应商编号
                    }).then((res) => {
                      if (res.code === 200) {
                        // options配置的懒加载数据给children赋值的时候，我们要用this.$set,不然回显不会显示
                        this.$set(
                          this.cascadeOptions[parentIndex],
                          'children',
                          res.data.list.map((item) => {
                            return {
                              value: item.interfaceManageNo,
                              label: item.interfaceName,
                              leaf: true,
                            }
                          })
                        )
                      }
                    })
                  }
                }
              })
            })
          }
          this.$refs[refName].openDrawer(data)
          break
        case 'addEditingVariableRef':
          this.$refs[refName].openDrawer({
            ...data,
            moduleId: this.moduleParams.moduleId,
            moduleName: this.moduleParams.moduleName,
          })
          break
      }
    },
    /**
     * @description: 特征变量模块名称搜索 && 特征变量属性名称搜索
     * @param data
     * @param type
     */
    onSearch(data, type) {
      const { pageNum, pageSize, ...newData } = data
      switch (type) {
        case 'module':
          console.log('data', data)
          this.moduleParams = {
            ...newData,
            page: pageNum,
            size: pageSize,
          }
          this.getFeatureModuleList()
          break
        case 'attribute':
          this.attributeParams = {
            ...newData,
            page: pageNum,
            size: pageSize,
          }
          this.getFeatureAttributeList()
          break
      }
    },
    /**
     * @description: 特征变量模块名称删除 && 特征变量属性名称删除
     * @param data
     * @param type
     */
    onDelete(data, type) {
      switch (type) {
        case 'module':
          deleteModule(data.id).then((res) => {
            if (res.code === 200) {
              this.moduleParams.page = 1
              this.getFeatureModuleList()
            }
          })
          break
        case 'attribute':
          deleteAttribute(data.id).then((res) => {
            if (res.code === 200) {
              this.attributeParams.page = 1
              this.getFeatureAttributeList()
            }
          })
          break
      }
    },
    /**
     * @description: 模块单元格点击事件
     * @param id
     * @param name
     */
    cellClickHandle({ id, name }) {
      console.log('this.attributeParams.moduleId', id)
      if (this.moduleParams.moduleId == id) return
      this.moduleParams.moduleId = id
      this.moduleParams.moduleName = name
      this.attributeParams.moduleId = id
      this.getFeatureAttributeList()
    },
    /**
     * @description: 获取特征变量模块列表
     */
    getFeatureModuleList() {
      this.moduleLoading = true
      const { moduleId, moduleName, ...moduleParams } = this.moduleParams
      featureModuleList(moduleParams).then((res) => {
        if (res.code === 200) {
          const { records, total } = res.data
          this.moduleTableData = records.map((item, index) => {
            return {
              ...item,
              index:
                (this.moduleParams.page - 1) * this.moduleParams.size +
                (index + 1),
            }
          })
          this.moduleTotal = total
          this.moduleParams.moduleId = records.length
            ? records[0].id
            : undefined
          this.moduleParams.moduleName = records.length
            ? records[0].name
            : undefined
          this.attributeParams.moduleId = records.length
            ? records[0].id
            : undefined
          this.getFeatureAttributeList()
          this.attributeTableData = []
          this.attributeTotal = 0
        }
        this.moduleLoading = false
      })
    },
    /**
     * @description: 获取特征变量属性列表
     */
    getFeatureAttributeList() {
      if (!this.attributeParams.moduleId) return
      this.attributeLoading = true
      featureAttributeList(this.attributeParams).then((res) => {
        if (res.code === 200) {
          const { records, total } = res.data
          const moduleData = this.moduleTableData.find(
            (item) => this.attributeParams.moduleId == item.id
          )
          this.attributeTableData = records.map((item, index) => {
            return {
              ...item,
              moduleName: moduleData ? moduleData.name : '',
              index:
                (this.attributeParams.page - 1) * this.attributeParams.size +
                (index + 1),
            }
          })
          this.attributeTotal = total
        }
        this.attributeLoading = false
      })
    },
    /**
     * 特征变量分页
     * @param num
     * @param type
     */
    handleCurrentChange(num, type) {
      switch (type) {
        case 'module':
          this.moduleParams.page = num
          this.getFeatureModuleList()
          break
        case 'attribute':
          this.attributeParams.page = num
          this.getFeatureAttributeList()
          break
      }
    },
    /**
     * @description: 关联元数据第一级
     */
    getFindSourceInfo() {
      findSourceInfo({
        pageNum: 1,
        pageSize: 30,
        sourceName: undefined,
        interfaceDataType: [0], //0 元数据，1 特征变量，2 分析指标
      }).then((res) => {
        if (res.code === 200)
          this.cascadeOptions = res.data.list.map((item) => {
            return {
              value: item.interfaceSourceNo,
              label: item.dataName,
            }
          })
      })
    },
    /**
     * @description: 加载关联元数据下一级
     * @param node
     * @param resolve
     */
    getFindInterfaceInfo(node, resolve) {
      // console.log("node", node);
      const { level } = node
      let nodes = []
      if (node.hasChildren || node.root) {
        // 0 代表第一次请求
        let nodeId = level == 0 ? null : node.value
        findInterfaceInfo({
          pageNum: 1,
          pageSize: 999,
          interfaceName: undefined,
          sourceNo: node.value, //供应商编号
        }).then((res) => {
          if (res.code === 200) {
            nodes = res.data.list.map((item) => {
              return {
                value: item.interfaceManageNo,
                label: item.interfaceName,
                leaf: level >= 1,
              }
            })
            resolve(nodes)
          }
        })
      } else {
        resolve(nodes)
      }
    },
    /**
     * @description: 新增模块提交 && 编辑模块提交
     * @param form
     */
    addEditingModuleSubmit(form) {
      const API = form.id ? editModuleData : addModuleData
      API(form).then((res) => {
        if (res.code === 200) {
          this.moduleParams.page = 1
          this.getFeatureModuleList()
        }
      })
    },
    /**
     * @description: 新增属性提交 && 编辑属性提交
     * @param form
     */
    addEditingVariableSubmit(form) {
      console.log(form)
      const API = form.id ? editAttributeData : addAttributeData
      API(form).then((res) => {
        if (res.code === 200) {
          this.attributeParams.page = 1
          this.getFeatureAttributeList()
        }
      })
    },
  },
}
</script>

<style lang="less" scoped>
.feature-variable {
  height: calc(var(--bgvh) - 42px);
  padding: 20px;
  display: flex;
  justify-content: space-between;
}
</style>
