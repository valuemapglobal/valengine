<template>
  <div class="basicVariable">
    <div class="box-card">
      <div slot="header" class="clearfix">
        <span>{{ $t('dataCenter.theme') }}</span>
      </div>
      <el-table
        v-loading="theme.loading"
        height="calc(var(--bgvh) - 210px)"
        @cell-click="tableDealTheme"
        :data="theme.list"
        :row-style="selectedlightTheme"
        :row-class-name="tableRowClassNameTheme"
        :border="true"
      >
        <el-table-column prop="name" :label="$t('dataCenter.themeName')" />
        <el-table-column prop="keycode" :label="$t('dataCenter.packageName')" />
        <el-table-column prop="packageType" :label="$t('dataCenter.themeType')">
        </el-table-column>
        <el-table-column
          prop="handle"
          :label="$t('common.operation')"
          width="120"
        >
          <template slot-scope="scope">
            <el-button
              style="float: left; color: #36a3f7"
              type="text"
              @click="handleUpdateTheme(scope.row)"
              >{{ $t('dataCenter.detail') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          v-show="theme.total > 0"
          :total="theme.total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          layout="total,sizes,prev, pager, next, jumper"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        >
        </el-pagination>
      </div>
    </div>

    <div class="box-card">
      <div slot="header" class="clearfix">
        <span>{{ $t('dataCenter.object') }}</span>
      </div>
      <el-table
        ref="groupTable"
        v-loading="group.loading"
        height="calc(var(--bgvh) - 210px)"
        :data="group.list"
        :border="true"
      >
        <el-table-column prop="keycode" label="code" width="" />
        <el-table-column
          prop="name"
          :label="$t('dataCenter.objectName')"
          width=""
        />
        <el-table-column
          prop="interfaceVersion"
          :label="$t('dataCenter.version')"
          width="90px"
        />
        <el-table-column
          prop="type"
          :label="$t('dataCenter.objectType')"
          :width="isEnglish() ? 120 : 90"
          align="center"
        >
          <template slot-scope="scope">
            <div v-if="scope.row.type == 0">{{ $t('dataCenter.number') }}</div>
            <div v-if="scope.row.type == 1">{{ $t('dataCenter.string') }}</div>
            <div v-if="scope.row.type == 2">{{ $t('dataCenter.date') }}</div>
            <div v-if="scope.row.type == 3">
              {{ $t('dataCenter.objectTypeValue') }}
            </div>
            <div v-if="scope.row.type == 4">{{ $t('dataCenter.array') }}</div>
            <div v-if="scope.row.type == 5">{{ $t('dataCenter.file') }}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="handle"
          :label="$t('common.operation')"
          :width="isEnglish() ? 200 : 140"
          fixed="right"
        >
          <template slot-scope="scope">
            <el-button type="text" @click="handleSetVariable(scope.row)"
              >{{ $t('dataCenter.viewProperty') }}
            </el-button>
            <el-button type="text" @click="handleUpdateGroup(scope.row)"
              >{{ $t('dataCenter.detail') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          :total="group.total"
          :page.sync="group.pageNum"
          :limit.sync="group.pageSize"
          layout="total,sizes,prev, pager, next, jumper"
          @current-change="handleGroupCurrentChange"
          @size-change="handleGroupSizeChange"
        >
        </el-pagination>
      </div>
    </div>
    <el-drawer
      :close-on-click-modal="false"
      :title="theme.dialog.title"
      v-loading="theme.dialog.loading"
      :visible.sync="theme.dialog.open"
      :size="theme.dialog.width"
    >
      <el-form
        ref="theme.form"
        :model="theme.form"
        :label-width="isEnglish() ? '140px' : '100px'"
      >
        <el-form-item :label="$t('dataCenter.themeName')" prop="name">
          <el-input
            v-model="theme.form.name"
            disabled
            :placeholder="$t('common.pleaseInput')"
          />
        </el-form-item>
        <el-form-item :label="$t('dataCenter.packageName')" prop="keycode">
          <el-input
            v-model="theme.form.keycode"
            disabled
            :placeholder="$t('common.pleaseInput')"
          />
        </el-form-item>
        <el-form-item :label="$t('dataCenter.themeType')">
          <el-select
            v-model="theme.form.packageType"
            disabled
            :placeholder="$t('dataCenter.selectStatus')"
          >
            <el-option
              v-for="dict in packageTpyeOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </el-drawer>

    <el-drawer
      v-loading="group.dialog.loading"
      :close-on-click-modal="false"
      :title="group.dialog.title"
      :visible.sync="group.dialog.open"
      :size="group.dialog.width"
    >
      <el-form
        ref="group.form"
        :model="group.form"
        :label-width="isEnglish() ? '140px' : '80px'"
      >
        <el-form-item :label="$t('dataCenter.objectName')" prop="name">
          <el-input
            disabled
            v-model="group.form.name"
            :placeholder="$t('dataCenter.inputName')"
          />
        </el-form-item>
        <el-form-item :label="$t('dataCenter.objectCode')" prop="keycode">
          <el-input
            disabled
            v-model="group.form.keycode"
            :placeholder="$t('dataCenter.inputObjectCode')"
          />
        </el-form-item>
        <el-form-item :label="$t('dataCenter.objectType')" prop="type">
          <el-select
            v-model="group.form.type"
            disabled
            :placeholder="$t('dataCenter.selectStatus')"
          >
            <el-option
              v-for="dict in dataTypeList"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </el-drawer>

    <!--设置属性 -->
    <el-drawer
      :title="record.dialog.title"
      :before-close="handleRecordClose"
      :visible.sync="record.dialog.open"
      direction="rtl"
      custom-class="demo-drawer"
      ref="drawer"
      size="50%"
      v-model="activeTab"
    >
      <div name="variable">
        <variable v-if="record.dialog.open" :variable="group.form"></variable>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { getDicts } from '@/api/index'
import { getThemeList, getGroupList } from '../api/dataList'
import variable from '../components/variable'
export default {
  components: {
    variable,
  },
  props: {},
  data() {
    return {
      activeTab: 'variable',
      // 遮罩层
      loading: true,
      // 总条数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderByColumn: 'id',
        isAsc: 'desc',
        name: undefined,
      },
      packageTpyeOptions: [],
      // 表单参数
      form: {},

      theme: {
        index: undefined,
        total: 0,
        loading: false,
        list: [],
        form: {
          id: undefined,
          name: undefined,
          keycode: undefined,
          remark: undefined,
        },
        dialog: {
          loading: false,
          open: false,
          title: '',
          width: '30%',
        },
        rules: {},
      },

      group: {
        index: undefined,
        themeId: undefined,
        total: 0,
        loading: false,
        pageNum: 1,
        pageSize: 10,
        list: [],
        form: {
          id: undefined,
          themeId: undefined,
          name: undefined,
          remark: undefined,
        },
        dialog: {
          loading: false,
          open: false,
          title: '',
          width: '30%',
        },
        rules: {},
      },
      dataTypeList: [],
      record: {
        dialog: {
          loading: false,
          open: false,
          title: '',
          width: '55%',
        },
      },
    }
  },
  computed: {
    packageTpyeOptions() {
      return [
        { dictValue: '0', dictLabel: this.$t('dataCenter.analysisObject') },
        { dictValue: '1', dictLabel: this.$t('dataCenter.derivedObject') },
      ]
    },
  },
  watch: {
    '$i18n.locale'() {
      // 语言切换时，强制表格重新计算布局，修复fixed列位置
      this.$nextTick(() => {
        if (this.$refs.groupTable) {
          this.$refs.groupTable.doLayout()
        }
      })
    },
  },
  created() {
    this.getListTheme()
    getDicts('decision_data_type')
      .then((res) => {
        if (res.code == 200) {
          this.dataTypeList = res.data
        }
      })
      .catch((err) => {})
  },
  methods: {
    isEnglish() {
      return this.$i18n.locale === 'en'
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getListTheme()
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.getListTheme()
    },
    handleGroupSizeChange(val) {
      this.group.pageSize = val
      this.getListGroup(this.group.themeId)
    },
    handleGroupCurrentChange(val) {
      this.group.pageNum = val
      this.getListGroup(this.group.themeId)
    },
    getListTheme() {
      this.theme.loading = true
      this.queryParams.packageType = 0
      getThemeList(this.queryParams).then((response) => {
        let list = response.data.rows || []
        if (list && list.length) {
          this.theme.list = list
          this.theme.total = response.data.total

          this.group.themeId = list[0].themeNo
          this.getListGroup(this.group.themeId)
        }
        this.theme.index = 0
        this.theme.loading = false
      })
    },
    resetTheme() {
      this.theme.form = {
        id: undefined,
        name: undefined,
        keycode: undefined,
        remark: undefined,
      }
    },
    /** 修改按钮操作 */
    handleUpdateTheme(row) {
      this.resetTheme()
      this.theme.dialog.title = this.$t('dataCenter.themeDetail')
      this.theme.form = row
      this.theme.dialog.open = true
    },
    handleSetVariable(row) {
      this.group.form = row
      this.record.dialog.open = true
      this.record.dialog.title = this.$t('dataCenter.viewProperty')
    },

    tableDealTheme(row, column, cell, event) {
      this.group.themeId = row.themeNo
      this.theme.index = row.index
      this.getListGroup(this.group.themeId)
    },
    selectedlightTheme({ row, rowIndex }) {
      if (this.theme.index === rowIndex) {
        return {
          'background-color': '#CAE1FF',
        }
      }
    },
    tableRowClassNameTheme({ row, rowIndex }) {
      //把每一行的索引放进row
      row.index = rowIndex
    },

    getListGroup(themeId) {
      this.group.loading = true
      let data = {
        pageNum: this.group.pageNum,
        pageSize: this.group.pageSize,
      }
      getGroupList({ ...data, themeNo: themeId }).then((response) => {
        this.group.list = response.data.rows
        this.group.total = response.data.total
        this.group.loading = false
      })
    },

    resetGroup() {
      this.group.form = {
        id: undefined,
        themeId: undefined,
        name: undefined,
        remark: undefined,
      }
    },

    /** 修改按钮操作 */
    handleUpdateGroup(row) {
      this.resetGroup()
      this.group.dialog.open = true
      this.group.dialog.title = this.$t('dataCenter.objectDetail')
      this.group.form = { ...row }
    },

    handleRecordClose() {
      this.record.dialog.open = false
    },

    // 回显数据字典
    selectDictLabel(datas, value) {
      var actions = []
      Object.keys(datas).map((key) => {
        if (datas[key].dictValue == '' + value) {
          actions.push(datas[key].dictLabel)
          return false
        }
      })
      return actions.join('')
    },
  },
}
</script>

<style lang="less" scoped>
.basicVariable {
  padding: 20px;
  height: calc(var(--bgvh) - 42px);
  display: flex;
  justify-content: space-between;

  .el-form-item .el-select,
  .el-date-editor {
    width: 100%;
  }

  .box-card {
    width: calc(50% - 10px);
    position: relative;
    padding: 20px;
    background-color: #fff;

    .add-buttom {
      height: 40px;
      padding: 0px 15px;
      background: rgba(255, 143, 31, 0.15);
      display: flex;
      align-items: center;
      justify-content: center;
      position: absolute;
      top: -55px;
      left: 0px;
      cursor: pointer;
      border-radius: 5px;
      font-size: 16px;
      // font-family: PingFang SC-Regular, PingFang SC;
      font-weight: 400;
      color: #ff8f1f;

      img {
        width: 20px;
        height: 20px;
        margin-right: 5px;
      }
    }

    .clearfix {
      display: flex;
      align-items: center;
      position: relative;
      margin-bottom: 10px;
      font-weight: 600;
    }

    ::v-deep .el-table {
      .has-gutter {
        font-weight: 600;
        color: rgba(#000, 0.85);
      }
      .el-table__cell,
      .el-button {
        font-size: 16px;
      }
    }
  }
}

.pagination {
  display: flex;
  justify-content: center;
  background-color: #fff;
  padding: 20px 20px 0px 20px;
}

.dialog-footer {
  text-align: right;
  padding-right: 20px;
}
</style>
