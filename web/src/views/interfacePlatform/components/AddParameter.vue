<template>
  <div style="position: relative">
    <el-drawer
      class="feed-back-drawer"
      :visible.sync="this.drawer"
      :before-close="resetFields"
      :destroy-on-close="true"
      :hide-required-asterisk="false"
      :modal="false"
      :wrapperClosable="false"
      size="58%"
      :title="$t('interfacePlatform.editParameter')"
    >
      <div class="header">
        <div class="right">
          <div class="input-warp">
            <el-input
              v-model="formData.interfaceFieldIdName"
              :placeholder="$t('interfacePlatform.inputParameterName')"
              suffix-icon="el-icon-search"
              style="width: 100%; height: 100%"
              clearable
              @clear="getList"
            ></el-input>
          </div>
          <el-button
            type="primary"
            style="margin-left: 10px; width: 85px; flex-shrink: 0"
            @click="getList"
            >{{ $t('common.search') }}</el-button
          >
        </div>
        <div
          class="add"
          @click="add"
          :style="{ width: (isEnglish() ? 140 : 110) + 'px' }"
        >
          <img src="../image/addOrange.png" />
          <div>{{ $t('interfacePlatform.addParameter') }}</div>
        </div>
      </div>

      <div class="content">
        <div class="table-warp">
          <el-table
            ref="parameterTable"
            :data="tableData"
            :border="true"
            v-loading="loading"
            style="width: 100%"
            height="calc(var(--bgvh) - 240px)"
          >
            <el-table-column
              prop="date"
              :label="$t('interfacePlatform.index')"
              width="60"
              type="index"
              align="center"
            >
            </el-table-column>
            <el-table-column
              prop="interfaceNo"
              :label="$t('interfacePlatform.interfaceNo')"
              width="160"
            >
            </el-table-column>
            <el-table-column
              prop="interfaceFieldIdName"
              :label="$t('interfacePlatform.parameterName')"
              width="144"
            >
            </el-table-column>
            <el-table-column
              prop="interfaceFieldIdAlias"
              :label="$t('interfacePlatform.parameterAlias')"
              width="144"
            >
            </el-table-column>
            <el-table-column
              prop="interfaceFieldIdType"
              :label="$t('interfacePlatform.parameterType')"
              width="144"
            >
              <template slot-scope="scope">
                <span>
                  {{
                    scope.row.interfaceFieldIdType == '1'
                      ? $t('interfacePlatform.outputParameter')
                      : $t('interfacePlatform.inputParameter')
                  }}</span
                >
              </template>
            </el-table-column>
            <el-table-column
              prop="interfaceFieldIdDataType"
              :label="$t('interfacePlatform.dataTypeLabel')"
              width="144"
            >
              <template slot-scope="scope">
                <div>
                  {{
                    selectDictLabel(
                      dataTypeList,
                      scope.row.interfaceFieldIdDataType
                    )
                  }}
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop="interfaceFieldIdRequired"
              :label="$t('interfacePlatform.isRequired')"
              width="144"
            >
              <template slot-scope="{ row }">
                <span>{{
                  row.interfaceFieldIdRequired == 1
                    ? $t('interfacePlatform.optional')
                    : $t('interfacePlatform.required')
                }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="interfaceFieldIdRemark"
              :label="$t('interfacePlatform.parameterRemark')"
              width="144"
            >
            </el-table-column>
            <el-table-column
              prop="interfaceFieldIdFather"
              :label="$t('interfacePlatform.parentProperty')"
              width="144"
            >
            </el-table-column>
            <el-table-column
              prop="createTime"
              :label="$t('common.createTime')"
              width="180"
            >
            </el-table-column>
            <el-table-column
              fixed="right"
              :label="$t('common.operation')"
              width="110"
            >
              <template slot-scope="scope">
                <el-button
                  type="text"
                  size="small"
                  @click="update(scope.row)"
                  >{{ $t('common.modify') }}</el-button
                >
                <el-button
                  type="text"
                  size="small"
                  style="color: #fa5151"
                  @click="del(scope.row)"
                  >{{ $t('common.delete') }}</el-button
                >
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="pagination">
          <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page.sync="formData.pageNum"
            :page-size="formData.pageSize"
            layout="prev, pager, next, jumper"
            :total="formData.totalNum"
          >
          </el-pagination>
        </div>
      </div>
    </el-drawer>
    <NewParameter
      :manageNo="manageNo"
      :interfaceNo="interfaceNo"
      ref="NewParameter"
      @getList="getList"
      :isEdit="isNewParameter"
    />
  </div>
</template>
<script>
import Headline from '@/components/Headline'
import NewParameter from './NewParameter.vue'
import {
  findInterfaceFieldIdInfo,
  removeInterfaceFieldIdInfo,
} from '@/views/interfacePlatform/api/dataList'
import { getDicts } from '@/api/index'
import { parseTime } from '@/utils/rouyi'
export default {
  components: { Headline, NewParameter },
  props: {
    isDetail: {
      // 查看当前是否是详情模块
      type: Boolean,
      default: false,
    },
    isEdit: {
      type: Boolean, // 查看当前是否是编辑
      default: false,
    },
    manageNo: {
      type: String,
      required: true,
    },
    interfaceNo: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      isNewParameter: false,
      loading: false,
      drawer: false,
      formData: {
        interfaceFieldIdName: '',
        pageNum: 1,
        pageSize: 10,
        name: '',
        totalNum: 0,
      },
      tableData: [],
      dataTypeList: [],
    }
  },
  watch: {
    '$i18n.locale'() {
      // 语言切换时，强制表格重新计算布局，修复fixed列位置
      this.$nextTick(() => {
        if (this.$refs.parameterTable) {
          this.$refs.parameterTable.doLayout()
        }
      })
    },
    drawer: {
      handler(n, o) {
        if (n) {
          this.formData.pageNum = 1
          this.getList()
        }
      },
    },
  },
  mounted() {
    getDicts('decision_data_type').then((res) => {
      if (res.code == 200) {
        this.dataTypeList = res.data
      }
    })
  },
  methods: {
    parseTime,
    isEnglish() {
      return this.$i18n.locale === 'en'
    },
    handleCurrentChange(val) {
      this.formData.pageNum = val
      this.getList()
    },
    getList() {
      this.loading = true
      findInterfaceFieldIdInfo({
        ...this.formData,
        manageNo: this.manageNo,
      }).then((res) => {
        if (res.code == 200) {
          this.tableData = res.data.list.map((item) => {
            return {
              ...item,
              createTime: this.parseTime(item.createTime),
            }
          })
          this.loading = false
          this.formData.totalNum = res.data.total
        }
      })
    },
    add() {
      this.isNewParameter = false
      this.$refs.NewParameter.drawer = true
    },
    resetFields() {
      this.drawer = false
      // this.formData = {};
      // this.fileList = [];
    },
    // 修改
    update(row) {
      this.isNewParameter = true
      this.$refs.NewParameter.drawer = true
      this.$refs.NewParameter.getInfo(row)
    },
    // 删除
    del(row) {
      this.$confirm(
        this.$t('interfacePlatform.deleteConfirmSimple'),
        this.$t('common.systemTip'),
        {
          confirmButtonText: this.$t('common.sure'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning',
        }
      )
        .then(() => {
          removeInterfaceFieldIdInfo({ fieIdNo: row.interfaceFieldIdManage }).then(
            (res) => {
              if (res.code == 200) {
                if (!res.data) {
                  this.$message.warning(res.msg)
                  return
                }
                this.$message({
                  type: 'success',
                  message: this.$t('interfacePlatform.deleteSuccess'),
                })
                this.getList()
              }
            }
          )
        })
        .catch(() => {})
    },
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
// @import '@/assets/scss/productConfiguration';

.pagination {
  justify-content: center;
  margin-top: 20px;
}

// .table-warp {
//   height: calc(100vh - 300px);
// }

.header {
  display: flex;
  justify-content: space-between;

  .text {
    font-size: 18px;
    font-family: PingFang SC-Medium, PingFang SC;
    font-weight: 500;
    color: rgba(0, 0, 0, 0.85);
  }

  .right {
    display: flex;

    .input-warp {
      width: 300px;
      height: 40px;
    }
  }

  .add {
    width: 110px;
    height: 40px;
    background: var(--primary-color-lighter);
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 14px;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: var(--primary-color);
    flex-shrink: 0;
    margin-left: 10px;
    cursor: pointer;
    border-radius: 6px;

    img {
      width: 20px;
      height: 20px;
      margin-right: 4px;
    }
  }
}

.content {
  margin-top: 20px;
}

.demo-image {
  display: flex;

  .text {
    margin-bottom: 10px;
    color: rgba(0, 0, 0, 0.85);
    font-size: 14px;
  }

  .left {
    flex: 1;
  }

  .right {
    flex: 1;
  }
}

.table_record {
  margin-top: 20px;
  margin-left: 10px;
  width: 100%;
  background-color: #fff;

  .record_list {
    width: 100%;
    height: 150px;
  }
}

.table_button {
  padding-left: 35px;
  padding-right: 20px;

  // width: calc(100% - 60px);
  width: 100%;
  height: 60px;
  z-index: 99;
  display: flex;
  align-items: center;
  box-sizing: border-box;
  background-color: #fff;

  .button_close {
    background-color: white;
    color: black;
  }

  .button_btn {
    background-color: #2071e1;
    color: white;
  }

  .table_button_left {
    flex: 1;
  }

  .table_button_right {
    flex: 2;
    text-align: right;
  }
}

.record_lable {
  font-size: 15px;
  font-family: PingFang SC-Medium, PingFang SC;
  font-weight: 500;
  margin-bottom: 20px;
}

.detailText {
  font-size: 14px;
  font-family: PingFang SC-Medium, PingFang SC;
  font-weight: 500;
  color: #414355;
  line-height: 48px;
}

.uploadFile {
  width: 352px;
  height: 140px;
  border-radius: 4px 4px 4px 4px;
}

/deep/ .action-btn {
  padding: 12px 5px 12px 9px;
  background: #3d7fff;
  border: none;
  border-radius: 18px 0 0 18px;

  span {
    display: inline-flex;
    justify-content: center;
    flex-direction: column;

    div {
      display: inline-block;
      font-size: 14px;
      letter-spacing: 6px;
      writing-mode: vertical-lr;
    }

    img {
      display: inline-block;
      margin-top: 2px;
      width: 18px;
      height: 18px;
    }
  }
}

.head-line {
  margin-bottom: 22px;
}

.feedback-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-top: 125px;

  &-text {
    font-size: 18px;
    font-weight: 500;
    color: #333333;
    line-height: 27px;
    text-align: center;

    i {
      font-size: 72px;
      color: #00b578;
      margin-bottom: 20px;
    }
  }

  .drawer-footer {
    margin-top: 20px;

    .el-button {
      padding: 11px 14px;
    }

    .el-button + .el-button {
      margin-left: 17px;
    }
  }
}

.feed-method {
  .feed-method-tab + .feed-method-tab {
    margin-top: 20px;
  }

  &-tab {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 30px 15px 30px 20px;
    border-radius: 6px;
    background: rgba(0, 0, 0, 0.04);
    border: 1px solid transparent;

    &-left {
      display: flex;
      align-items: center;
      font-size: 16px;
      font-weight: 500;
      line-height: 22px;
      color: rgba(0, 0, 0, 0.85);

      img {
        height: 40px;
        margin-right: 9px;
      }
    }

    &-right {
      cursor: pointer;
      display: flex;
      align-items: center;
      font-size: 12px;
      font-weight: 400;
      line-height: 18px;
      color: rgba(0, 0, 0, 0.6);

      img {
        width: 18px;
        height: 18px;
      }
    }
  }

  .tab-active {
    border-color: var(--primary-color);
    background: rgba(40, 136, 232, 0.04);

    .feed-method-tab-left {
      color: var(--primary-color);
    }
  }
}

.titleWarp {
  display: flex;

  .text {
    font-size: 16px;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: rgba(0, 0, 0, 0.85);
    margin-left: 16px;
  }

  .icon {
    width: 22px;
    height: 22px;
  }
}

.hint {
  font-size: 14px;
  font-family: PingFang SC-Regular, PingFang SC;
  font-weight: 400;
  color: #666666;
  margin-left: 58px;
  margin-bottom: 30px;
}

// /deep/ .el-dialog {
//   width: 432px !important;
//   height: 170px !important;
// }

// /deep/ .el-dialog__body {
//   padding: 0px !important;
// }

.file-name {
  width: 100%;
  height: 32px;
  background-color: rgb(246, 249, 253);
  color: rgba(0, 0, 0, 0.85);
  font-size: 14px;
  line-height: 32px;
  padding-left: 10px;
  box-sizing: border-box;
}

// /deep/ .el-switch {
//   vertical-align: bottom;
//   margin-left: 20px;
// }

// /deep/ .el-form-item .el-input__inner {
//   padding: 14px;
//   width: 100%;
// }

// /deep/ .el-input-number--small {
//   line-height: 44px;
// }
</style>
