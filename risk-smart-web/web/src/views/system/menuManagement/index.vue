<template>
  <div id="newsWrap">
    <div class="header">
      <div class="left">
        <el-input
          style="height: 42px; width: 240px; margin-right: 10px"
          v-model="searchFrom.menuName"
          :placeholder="$t('menuManage.inputMenuName')"
          size="normal"
          clearable
        ></el-input>
        <el-select
          style="height: 42px; width: 240px; margin-right: 10px"
          v-model="searchFrom.status"
          :placeholder="$t('menuManage.menuStatus')"
          clearable
          @clear="searchFrom.status = null"
        >
          <el-option
            v-for="item in TypeOptions"
            :key="item.dictValue"
            :label="item.dictLabel"
            :value="item.dictValue"
          >
          </el-option>
        </el-select>
      </div>
      <el-button class="btn" size="default" @click="add" icon="el-icon-plus">{{
        $t('menuManage.add')
      }}</el-button>
    </div>
    <div class="main">
      <el-table
        :data="tableData"
        v-loading="tableLoading"
        border
        row-key="menuId"
        height="675px"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :header-cell-style="{
          backgroundColor: '#f8faff',
        }"
      >
        <el-table-column
          :show-overflow-tooltip="true"
          v-for="col in tableHeader"
          :key="col.id"
          :label="col.label"
          :width="col.width"
          :align="col.align || 'left'"
        >
          <template slot-scope="{ row }">
            <!-- 菜单名称 -->
            <div style="display: inline-block" v-if="col.prop == 'menuName'">
              {{ row.menuName }}
            </div>
            <div v-else-if="col.prop == 'status'">
              <el-tag :type="row.status == 0 ? '' : 'danger'">{{
                row.status | processingState
              }}</el-tag>
            </div>
            <!-- 操作 -->
            <div v-else-if="col.prop == 'operation'">
              <el-button
                type="text"
                style="color: var(--primary-color)"
                @click="add(row)"
                >{{ $t('common.add') }}</el-button
              >
              <el-button
                type="text"
                style="color: var(--primary-color)"
                @click="modifyThe(row)"
                >{{ $t('common.modify') }}</el-button
              >
              <el-button
                type="text"
                style="color: #fa5151"
                @click="Delete(row.menuId, row.menuName)"
                >{{ $t('common.delete') }}</el-button
              >
            </div>
            <div
              v-else="
                col.prop !== 'status' &&
                col.prop !== 'operation' &&
                col.prop !== 'menuName' &&
                col.prop !== 'icon'
              "
            >
              {{ row[col.prop] }}
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pageCheck">
        <el-pagination
          background
          @current-change="handleCurrentChange"
          :current-page.sync="pageNum"
          :page-size="pageSize"
          layout="prev, pager, next, jumper"
          :total="totalNum"
        >
        </el-pagination>
      </div>
    </div>
    <Drawer
      ref="drawer"
      @refresh="
        () => {
          this.toGetMenuList()
        }
      "
    />
  </div>
</template>

<script>
// 获取菜单管理接口
import {
  getMenuList,
  getStateDictionary,
  DeleteMenuList,
} from '@/api/system/menuManage'
import { handleTree } from '@/utils/index'
// 获取字典列表
import Drawer from './components/drawer.vue'
export default {
  name: 'MenuList',
  components: {
    Drawer,
  },
  computed: {
    tableHeader() {
      return [
        {
          prop: 'menuName',
          id: 1,
          label: this.$t('menuManage.menuName'),
          width: 'auto',
        },
        {
          prop: 'perms',
          id: 3,
          label: this.$t('menuManage.perms'),
          width: 'auto',
        },
        {
          prop: 'path',
          id: 4,
          label: this.$t('menuManage.path'),
          width: 'auto',
        },
        {
          prop: 'status',
          id: 5,
          label: this.$t('common.status'),
          width: 200,
        },
        {
          prop: 'createTime',
          id: 6,
          label: this.$t('common.createTime'),
          width: 200,
        },
        {
          prop: 'operation',
          id: 7,
          label: this.$t('common.operation'),
          width: 160,
          align: 'center',
        },
      ]
    },
  },
  data() {
    return {
      totalNum: 0,
      pageNum: 1,
      pageSize: 1,
      createrTime: '',
      //筛选
      searchFrom: {
        menuName: null,
        status: null,
      },
      //表格数据
      tableData: [],
      tableLoading: false,
      TypeOptions: [],
    }
  },
  created() {
    //菜单列表
    this.toGetMenuList()
    //菜单状态字典加载
    this.toGetStateDictionary()
  },
  filters: {
    // 处理时间
    dateFormat: function (originVal) {
      const dt = new Date(originVal)
      const y = dt.getFullYear()
      // 月份从0开始,使她变成字符串,不足两位时,前面补个0.
      const m = (dt.getMonth() + 1 + '').padStart(2, '0')
      const d = (dt.getDate() + '').padStart(2, '0')
      const hh = (dt.getHours() + '').padStart(2, '0')
      const mm = (dt.getMinutes() + '').padStart(2, '0')
      const ss = (dt.getSeconds() + '').padStart(2, '0')
      return `${y}-${m}-${d} ${hh}:${mm}:${ss}`
    },
    // 处理状态
    processingState: function (val) {
      if (val == 0) {
        return this.$t('common.normal')
      } else {
        return this.$t('common.disabled')
      }
    },
  },
  watch: {
    searchFrom: {
      handler() {
        this.search()
      },
      deep: true,
    },
  },
  methods: {
    // 获取菜单管理列表
    async toGetMenuList(params = {}) {
      this.tableLoading = true
      const { data: res } = await getMenuList(params)
      this.tableData = handleTree(res, 'menuId')
      // 获取查询菜单树菜单
      this.$refs['drawer'].menuOptions = []
      const menu = { value: 0, label: '主类目', children: [] }
      let tableDatas = JSON.parse(JSON.stringify(this.tableData))
      const mapTree = (org) => {
        const haveChildren =
          Array.isArray(org.children) && org.children.length > 0
        return {
          label: org.menuName,
          value: org.menuId,
          children: haveChildren ? org.children.map((i) => mapTree(i)) : [],
        }
      }
      let result = []
      result = tableDatas.map((org) => mapTree(org))
      menu.children = result
      this.$nextTick(() => {
        this.$refs['drawer'].menuOptions.push(menu)
      })
      this.tableLoading = false
    },

    // 获取状态字典列表
    async toGetStateDictionary() {
      const res = await getStateDictionary()
      this.TypeOptions = res.data
    },
    // 菜单查询接口
    search() {
      let params = JSON.parse(JSON.stringify(this.searchFrom))
      for (let key in this.params) {
        if (params[key] === null) {
          delete params[key]
        }
      }
      this.toGetMenuList(params)
    },
    // 添加菜单
    add(row) {
      this.$refs['drawer'].dialog = true
      this.$refs['drawer'].flag = 0
      this.$refs['drawer'].rowData = row
    },
    // 修改菜单
    modifyThe(row) {
      this.$refs['drawer'].dialog = true
      this.$refs['drawer'].flag = 1
      this.$refs['drawer'].form = {
        parentId: row.parentId,
        menuType: row.menuType,
        icon: row.icon,
        menuName: row.menuName,
        orderNum: row.orderNum,
        isFrame: row.isFrame,
        path: row.path,
        perms: row.perms,
        visible: row.visible,
        status: row.status,
      }
      this.$refs['drawer'].rowData = { ...row, children: [] }
    },
    // 删除菜单
    Delete(id, title) {
      this.$confirm(
        this.$t('menuManage.deleteConfirm', { name: title }),
        this.$t('common.systemTip'),
        {
          confirmButtonText: this.$t('common.sure'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning',
        }
      )
        .then(async () => {
          await DeleteMenuList(id)
          // 重新获取菜单列表
          this.toGetMenuList()
          this.$message({
            type: 'success',
            message: this.$t('common.success'),
          })
        })
        .catch(() => {
          return
        })
    },
    handleCurrentChange(val) {
      this.toGetNewsList()
    },
  },
}
</script>

<style lang="less" scoped>
/deep/ .el-pagination.is-background .el-pager li {
  border: 1px solid rgba(0, 0, 0, 0.08);
  background-color: #fff;
}
/deep/ .el-pagination.is-background .btn-prev {
  border: 1px solid rgba(0, 0, 0, 0.08);
  background-color: #fff;
}
/deep/ .el-pagination.is-background .btn-next {
  border: 1px solid rgba(0, 0, 0, 0.08);
  background-color: #fff;
}
:deep.el-pagination {
  display: flex;
  justify-content: center;
  .el-pager {
    .active {
      // background: linear-gradient(135deg, #ff7f73 0%, #ff4040 100%);
      background-color: #409eff;
    }
  }
}
#newsWrap {
  padding: 20px;
  .header {
    ::v-deep .el-input--small .el-input__inner {
      height: 42px;
    }
    display: flex;
    align-items: center;
    .left {
      display: flex;

      :deep .el-input__inner,
      :deep input {
        font-size: 14px;
        height: 42px;
        background: var(--bg-color);
        border-radius: 6px;

        &::placeholder {
          color: var(--text-color-secondary);
        }
      }
    }
    .btn {
      color: #fff;
      border: none;
      border-radius: 6px;
      height: 42px;
      // background: linear-gradient(135deg, #ff7f73 0%, #ff4040 100%);
      background-color: var(--primary-color);
    }
  }
  .main {
    margin-top: 20px;
    background: var(--bg-color);
    border-radius: 6px 6px 6px 6px;
    opacity: 1;
    padding: 20px;
    .pageCheck {
      display: flex;
      justify-content: center;
      padding: 20px 20px 0 20px;
    }
  }
}
</style>
