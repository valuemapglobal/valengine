<template>
  <div class="rightCompoent" v-show="!showEmpty">
    <div class="header">
      <div class="text">{{ $t('interfacePlatform.interfaceLog') }}</div>
      <div class="search-form">
        <el-input
          v-model="formData.interfaceNameZh"
          :maxlength="30"
          :placeholder="$t('interfacePlatform.inputInterfaceName')"
          clearable
          @clear="getList"
        ></el-input>
        <el-input
          v-model="formData.createBy"
          :maxlength="11"
          :placeholder="$t('interfacePlatform.inputUserNickname')"
          clearable
          @clear="getList"
        ></el-input>
        <el-input
          v-model="formData.userDept"
          :maxlength="30"
          :placeholder="$t('interfacePlatform.inputUserDept')"
          clearable
          @clear="getList"
        ></el-input>
        <el-select
          v-model="formData.state"
          clearable
          :placeholder="$t('interfacePlatform.selectCallStatus')"
          :popper-append-to-body="false"
          @clear="
            () => {
              formData.state = null
              this.getList()
            }
          "
        >
          <el-option :label="$t('interfacePlatform.success')" :value="0">
          </el-option>
          <el-option :label="$t('interfacePlatform.failure')" :value="1">
          </el-option>
        </el-select>
        <el-date-picker
          :default-value="defaultTime"
          v-model="defaultTime"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="——"
          @change="timeChange"
          :start-placeholder="$t('interfacePlatform.startDate')"
          :end-placeholder="$t('interfacePlatform.endDate')"
          :clearable="false"
        >
        </el-date-picker>
        <el-button
          type="primary"
          style="
            margin-left: 10px;
            width: 85px;
            flex-shrink: 0;
            height: 42px;
            font-size: 14px;
          "
          @click="resetFields"
          >{{ $t('common.reset') }}</el-button
        >
      </div>
    </div>
    <div class="content">
      <div class="table-warp">
        <el-table
          :data="tableData"
          :border="false"
          ref="duditingTable"
          style="width: 100%"
          v-loading="loading"
          height="calc(var(--bgvh) - 290px)"
        >
          <el-table-column
            type="index"
            width="50"
            :index="
              (index) => {
                return index + (formData.pageNum - 1) * formData.pageSize + 1
              }
            "
          >
          </el-table-column>
          <el-table-column
            prop="interfaceNameZh"
            :label="$t('interfacePlatform.interfaceName')"
            width=""
          >
          </el-table-column>
          <el-table-column
            prop="createBy"
            :label="$t('interfacePlatform.userNickname')"
            width="120"
          >
          </el-table-column>
          <el-table-column
            prop="userDept"
            :label="$t('interfacePlatform.userDept')"
            width="200"
          >
          </el-table-column>
          <el-table-column
            prop="accessTime"
            :label="$t('interfacePlatform.callTime')"
            width="190"
          >
          </el-table-column>
          <el-table-column
            prop="resultTime"
            :label="$t('interfacePlatform.responseTime')"
            width="190"
          >
          </el-table-column>
          <el-table-column
            prop="ip"
            :label="$t('interfacePlatform.ipAddress')"
            width="140"
          >
          </el-table-column>
          <el-table-column
            prop="code"
            :label="$t('interfacePlatform.callStatus')"
            width="90"
          >
            <template slot-scope="scope">
              <el-tag :type="scope.row.code == 200 ? 'success' : 'danger'">{{
                scope.row.code == 200
                  ? $t('interfacePlatform.success')
                  : $t('interfacePlatform.failure')
              }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column
            fixed="right"
            :label="$t('common.operation')"
            width="140"
          >
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="detail(scope.row)">{{
                $t('interfacePlatform.detail')
              }}</el-button>
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
          layout="total, prev, pager, next, jumper"
          :total="totalNum"
        >
        </el-pagination>
      </div>
    </div>
    <InterfaceDeatil ref="InterfaceDeatil" />
  </div>
</template>

<script>
import { findAllLog } from '@/views/interfacePlatform/api/dataList'
import InterfaceDeatil from './InterfaceDeatil.vue'
export default {
  components: {
    InterfaceDeatil,
  },
  props: ['sourceNo'],
  data() {
    const today = new Date()
    const startDate = new Date(
      today.getFullYear(),
      today.getMonth(),
      today.getDate(),
      0,
      0,
      0
    )
    const endDate = new Date(
      today.getFullYear(),
      today.getMonth(),
      today.getDate(),
      23,
      59,
      59
    )

    const startDateFormatted = formatDate(startDate)
    const endDateFormatted = formatDate(endDate)
    return {
      defaultTime: [startDateFormatted, endDateFormatted],
      // formData: {
      //   time: [], // 用于存储选择的日期范围
      // },
      // defaultTime: [],
      startDateFormatted: startDateFormatted,
      showEmpty: false, // 是否显示空状态
      endDateFormatted: endDateFormatted,
      interfaceNo: '',
      interfaceManageNo: '',
      loading: false,
      InterfaceEdit: false, // 新增||编辑弹出
      formData: {
        pageNum: 1,
        pageSize: 10,
        // time: [], // 用于存储选择的日期范围
        // sourceNo: "DATA_MIDDLE_STATION:889550115247624192",
        // interfaceName: "baseinfo",
        startTime: startDateFormatted,
        endTime: endDateFormatted,
      },
      totalNum: 0,
      tableData: [],
    }
  },
  watch: {
    '$i18n.locale'() {
      // 语言切换时，强制表格重新计算布局，修复fixed列位置
      this.$nextTick(() => {
        if (this.$refs.duditingTable) {
          this.$refs.duditingTable.doLayout()
        }
      })
    },
    sourceNo: {
      handler(n, o) {
        if (n) {
          this.showEmpty = false
          this.defaultTime = [this.startDateFormatted, this.endDateFormatted]
          this.getList()
        } else {
          this.showEmpty = true
        }
        this.formData = this.$options.data().formData
      },
      immediate: true,
    },
    formData: {
      handler() {
        this.getList()
      },
      deep: true,
    },
  },
  mounted() {},
  methods: {
    resetFields() {
      this.formData = {
        pageNum: 1,
        pageSize: 10,
        time: [], // 用于存储选择的日期范围
        // sourceNo: "DATA_MIDDLE_STATION:889550115247624192",
        // interfaceName: "baseinfo",
        startTime: this.startDateFormatted,
        endTime: this.endDateFormatted,
      }
      this.defaultTime = [this.startDateFormatted, this.endDateFormatted]
      this.getList()
    },
    timeChange(e) {
      if (e) {
        this.formData.startTime = e[0] + ' 00:00:00'
        this.formData.endTime = e[1] + ' 23:59:59'
      } else {
        this.formData.startTime = ''
        this.formData.endTime = ''
      }
    },
    // 详情
    detail(row) {
      // this.=row
      this.$refs.InterfaceDeatil.drawer = true
      this.$refs.InterfaceDeatil.getInfo(row)
    },
    getList() {
      this.loading = true
      this.tableData = []
      findAllLog({ ...this.formData, sourceNo: this.sourceNo })
        .then((res) => {
          if (res.code == 200) {
            this.tableData = res.data.list
            this.totalNum = res.data.total
            this.loading = false
          }
        })
        .catch(() => {
          this.loading = false
        })
    },
    handleCurrentChange(val) {
      this.formData.pageNum = val
      this.getList()
    },
    handleSizeChange() {
      this.formData.pageSize = val
      this.getList()
    },
  },
}
function formatDate(date) {
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  const seconds = date.getSeconds().toString().padStart(2, '0')

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}
</script>

<style lang="less" scoped>
@import '@/assets/scss/productConfiguration.less';

.input-warp {
  width: 100%;
  ::v-deep .el-range-editor--small.el-input__inner {
    // height: 40px;
    background: #f4f6f9;
    border: none;
  }
}

// .input-wrap_box {
//   ::v-deep .el-date-editor {
//     background: #f4f6f9;
//     border: none;
//   }
// }

::v-deep .el-col-3 {
  margin-right: 25px !important;
}

::v-deep .input-wrap_box {
  width: 400px;
}

.pagination {
  justify-content: center;
}

::v-deep .pagination .el-input {
  position: static;
}

::v-deep .input-warp .el-input__inner {
  width: 100% !important;
  background-color: #ff8f1f;
  padding-right: 25px !important;
}

::v-deep.el-range-editor--small.el-input__inner {
  height: 40px;
}

::v-deep .el-range-editor--small .el-range-separator {
  line-height: 40px;
  color: #ced0d6;
  font-weight: 500;
}

.table-warp {
  width: 100%;
  height: calc(var(--bgvh) - 320px);
  ::v-deep.el-table {
    font-size: 16px !important;
    ::v-deep.el-table {
      font-size: 16px !important;
      .has-gutter,
      .el-button {
        font-size: 16px !important;
      }
    }
  }
}

::v-deep .el-table__cell {
  height: 53px;
}

.rightCompoent {
  width: 100%;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
  background-color: var(--bg-color);

  .header {
    .text {
      font-size: 18px;
      font-weight: 500;
      color: var(--text-color-secondary);
    }

    .search-form {
      margin-top: 20px;
      display: flex;
      align-items: center;

      ::v-deep .el-input,
      ::v-deep .el-select {
        width: 160px !important;
        margin-right: 10px;
        .el-input__inner {
          height: 40px;
        }
      }

      ::v-deep .el-date-editor {
        width: 240px;
        padding: 0px 10px;
        .el-range-separator {
          padding: 0px;
        }
      }
      .input-warp {
        width: 100%;
        height: 40px;
      }

      .add {
        width: 110px;
        height: 40px;
        background: var(--bg-color);
        display: flex;
        justify-content: center;
        align-items: center;
        font-size: 14px;
        // font-family: PingFang SC-Regular, PingFang SC;
        font-weight: 400;
        color: #ff8f1f;
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
