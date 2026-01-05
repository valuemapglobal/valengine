<template>
  <div class="rightCompoent" v-show="!showEmpty">
    <div class="header">
      <div class="text">接口配置</div>
      <div class="right">
        <div class="input-warp">
          <el-input
            v-model="formData.interfaceName"
            placeholder="请输入接口名称"
            suffix-icon="el-icon-search"
            style="width: 100%; height: 100%"
            clearable
            @clear="getList"
          ></el-input>
        </div>
        <div class="add" @click="add">
          <img src="../image/addOrange.png" />
          <div>新增接口</div>
        </div>
      </div>
    </div>
    <div class="content">
      <div class="table-warp">
        <el-table
          :data="tableData"
          :border="true"
          style="width: 100%"
          v-loading="loading"
          height="calc(var(--bgvh) - 230px)"
        >
          <el-table-column
            prop="interfaceNo"
            label="编号"
            width="90"
            type="index"
          >
          </el-table-column>
          <el-table-column prop="interfaceName" label="接口名称">
          </el-table-column>
          <el-table-column prop="interfaceNo" label="接口编号">
          </el-table-column>
          <el-table-column prop="interfaceVersion" label="接口版本" width="100">
          </el-table-column>
          <el-table-column prop="interfaceType" label="接口类型" width="160">
            <template slot-scope="scope">
              {{
                scope.row.interfaceType == 1
                  ? '企业接口'
                  : scope.row.interfaceType == 2
                  ? '司法接口'
                  : '个人接口'
              }}
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="190">
          </el-table-column>
          <el-table-column fixed="right" label="操作" width="208">
            <template slot-scope="scope">
              <el-button @click="editClick(scope.row)" type="text" size="small"
                >编辑接口</el-button
              >
              <el-button type="text" size="small" @click="editParms(scope.row)"
                >编辑参数</el-button
              >
              <el-button
                type="text"
                size="small"
                style="color: #fa5151"
                @click="del(scope.row)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination">
        <el-pagination
          background
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
          :current-page.sync="formData.pageNum"
          :page-size="formData.pageSize"
          layout="prev, pager, next, jumper"
          :total="formData.total"
        >
        </el-pagination>
      </div>
    </div>
    <AddInterface
      ref="add"
      :isEdit="InterfaceEdit"
      :sourceNo="sourceNo"
      @getList="getList"
    />
    <AddParameter
      ref="AddParameter"
      :manageNo="interfaceManageNo"
      :interfaceNo="interfaceNo"
    />
  </div>
</template>

<script>
import AddInterface from './AddInterface.vue'
import AddParameter from './AddParameter.vue'
import {
  findInterfaceInfo,
  removeInterfaceInfo,
} from '@/views/interfacePlatform/api/dataList'
export default {
  components: {
    AddInterface,
    AddParameter,
  },
  props: ['sourceNo'],
  data() {
    return {
      showEmpty: false, // 是否显示空状态
      interfaceNo: '',
      interfaceManageNo: '',
      loading: false,
      InterfaceEdit: false, // 新增||编辑弹出
      formData: {
        pageNum: 1,
        pageSize: 10,
        interfaceName: '',
        total: 0,
      },
      tableData: [],
    }
  },
  // mounted(){
  //     this.getList()
  // },
  watch: {
    sourceNo: {
      handler(n, o) {
        // this.showEmpty = n ? false : true
        this.getList()
      },
      immediate: true,
    },
    formData: {
      handler(val) {
        this.getList()
      },
      deep: true,
    },
  },
  methods: {
    handleCurrentChange(val) {
      this.formData.pageNum = val
    },
    handleSizeChange() {
      this.formData.pageSize = val
    },
    // 编辑参数
    editParms(row) {
      this.interfaceNo = row.interfaceNo
      this.interfaceManageNo = row.interfaceManageNo
      this.$refs.AddParameter.drawer = true
    },
    getList() {
      this.tableData = []
      if (!this.sourceNo) return
      this.loading = true
      findInterfaceInfo({ ...this.formData, sourceNo: this.sourceNo })
        .then((res) => {
          if (res.code == 200) {
            this.tableData = res.data.list
            this.formData.total = res.data.total
            this.loading = false
          }
        })
        .catch(() => {
          this.loading = false
        })
    },
    //删除
    del(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          removeInterfaceInfo({ manageNo: row.interfaceManageNo })
            .then((res) => {
              if (res.code == 200) {
                this.$message({
                  type: 'success',
                  message: '删除成功!',
                })
                this.getList()
              }
            })
            .catch(() => {})
        })
        .catch(() => {})
    },
    editClick(row) {
      this.InterfaceEdit = true
      this.$refs.add.drawer = true
      this.$refs.add.getInfo(row)
    },
    add() {
      this.InterfaceEdit = false
      this.$refs.add.drawer = true
    },
  },
}
</script>

<style lang="less" scoped>
.pagination {
  justify-content: center;
  margin-top: 20px;
}
.table-warp {
  width: 100%;
  :deep(.el-table) {
    font-size: 16px !important;
    .has-gutter,
    .el-button {
      font-size: 16px !important;
    }
  }
}
::v-deep .input-warp .el-input__inner {
  width: 300px;
  height: 40px;
  // background: #f4f6f9;
  // border: none;
}

::v-deep .el-table__cell {
  height: 53px;
}

.rightCompoent {
  width: 100%;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;

  .header {
    display: flex;
    justify-content: space-between;

    .text {
      font-size: 18px;
      // font-family: PingFang SC-Medium, PingFang SC;
      font-weight: 500;
      color: var(--text-color-secondary);
    }

    .right {
      display: flex;

      .input-warp {
        width: 300px;
        height: 40px;
      }

      .add {
        width: 110px;
        height: 40px;
        border-radius: 6px;
        background: var(--primary-color-lighter);
        display: flex;
        justify-content: center;
        align-items: center;
        font-size: 14px;
        // font-family: PingFang SC-Regular, PingFang SC;
        font-weight: 400;
        color: var(--primary-color);
        flex-shrink: 0;
        margin-left: 10px;
        cursor: pointer;

        img {
          width: 20px;
          height: 20px;
          margin-right: 4px;
        }
      }
    }
  }

  .content {
    margin-top: 20px;
  }
}
</style>
