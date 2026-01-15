<template>
  <div class="app-container">
    <div class="item">
      <el-table :data="variableList">
        <el-table-column prop="code" :label="$t('dataCenter.code')" />
        <el-table-column prop="name" :label="$t('dataCenter.variableName')" />
        <el-table-column prop="type" :label="$t('dataCenter.variableType')">
          <template slot-scope="scope">
            <div>{{ selectDictLabel(dataTypeList, scope.row.type) }}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="parentName"
          :label="$t('dataCenter.parentObject')"
        />
        <el-table-column
          prop="handle"
          :label="$t('common.operation')"
          width="150"
        >
          <template slot-scope="scope">
            <el-button type="text" @click="handleUpdate(scope.row)"
              >{{ $t('dataCenter.detail') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          v-show="total > 0"
          :total="total"
          :current-page.sync="queryParams.pageNum"
          :page-sizes.sync="queryParams.pageSize"
          @size-change="getList"
          @current-change="getList"
        ></el-pagination>
      </div>
    </div>

    <el-drawer
      :modal="false"
      :close-on-click-modal="false"
      :title="title"
      :visible.sync="open"
      size="40%"
    >
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="submitForm isShowborder"
      >
        <el-form-item :label="$t('dataCenter.code')" prop="code">
          <el-input
            v-model="form.code"
            :placeholder="$t('dataCenter.inputCode')"
            disabled
          />
        </el-form-item>
        <el-form-item :label="$t('dataCenter.variableName')" prop="name">
          <el-input
            v-model="form.name"
            :placeholder="$t('common.pleaseInput')"
            disabled
          />
        </el-form-item>
        <el-form-item :label="$t('dataCenter.variableType')" prop="type">
          <div
            v-if="form.type == 0"
            style="
              color: rgba(0, 0, 0, 0.65);
              padding-left: 15px;
              font-size: 13px;
            "
          >
            {{ $t('dataCenter.objectTypeValue') }}
          </div>
          <div
            v-if="form.type == 1"
            style="
              color: rgba(0, 0, 0, 0.65);
              padding-left: 15px;
              font-size: 13px;
            "
          >
            {{ $t('dataCenter.collection') }}
          </div>
          <div
            v-if="form.type == 2"
            style="
              color: rgba(0, 0, 0, 0.65);
              padding-left: 15px;
              font-size: 13px;
            "
          >
            {{ $t('dataCenter.string') }}
          </div>
          <div
            v-if="form.type == 3"
            style="
              color: rgba(0, 0, 0, 0.65);
              padding-left: 15px;
              font-size: 13px;
            "
          >
            {{ $t('dataCenter.boolean') }}
          </div>
          <div
            v-if="form.type == 4"
            style="
              color: rgba(0, 0, 0, 0.65);
              padding-left: 15px;
              font-size: 13px;
            "
          >
            {{ $t('dataCenter.integer') }}
          </div>
          <div
            v-if="form.type == 5"
            style="
              color: rgba(0, 0, 0, 0.65);
              padding-left: 15px;
              font-size: 13px;
            "
          >
            {{ $t('dataCenter.float') }}
          </div>
        </el-form-item>
        <el-form-item :label="$t('dataCenter.parentObject')" prop="parentName">
          <el-input v-model="form.parentName" placeholder="" disabled />
        </el-form-item>
      </el-form>
      <!-- <div class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div> -->
    </el-drawer>
  </div>
</template>

<script>
import { getDicts } from '@/api/index'
import { getRecordList } from '../api/dataList'
export default {
  props: {
    variable: {
      type: [Object, Array],
      default: () => {},
    },
  },
  data() {
    return {
      groupId: undefined,
      title: '',
      loading: false,
      open: false,
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        groupId: undefined,
        themeId: undefined,
      },
      group: {},
      // 表单参数
      variableList: [],
      //
      typeOptions: [],
      form: {
        id: undefined,
        themeId: undefined,
        groupId: undefined,
        code: undefined,
        name: undefined,
        type: undefined,
        remark: undefined,
      },
      rules: {},
      dataTypeList: [],
    }
  },
  computed: {
    rules() {
      return {
        code: [
          {
            required: true,
            message: this.$t('dataCenter.codeCannotBeEmpty'),
            trigger: 'blur',
          },
        ],
        name: [
          {
            required: true,
            message: this.$t('dataCenter.nameCannotBeEmpty'),
            trigger: 'blur',
          },
        ],
        type: [
          {
            required: true,
            message: this.$t('dataCenter.typeCannotBeEmpty'),
            trigger: 'blur',
          },
        ],
      }
    },
  },
  created() {
    this.queryParams.groupId = this.$route.params.id
    if (this.variable) {
      this.queryParams.groupId = this.variable.groupNo
    }
    // getDicts("rde_variable_type").then(response => {
    //   this.typeOptions = response.data;
    // });
    this.getList()

    getDicts('decision_data_type')
      .then((res) => {
        if (res.code == 200) {
          this.dataTypeList = res.data
        }
      })
      .catch((err) => {})
    // this.getGroup();
  },
  methods: {
    // getGroup() {
    //   getVariableRecordById(this.queryParams.groupId).then(response => {
    //     this.group = response.data;

    //   });
    // },
    getList() {
      this.loading = true
      getRecordList({
        groupNo: this.queryParams.groupId,
        pageNum: this.queryParams.pageNum,
        pageSize: this.queryParams.pageSize,
      }).then((response) => {
        this.variableList = response.data.rows
        this.total = response.data.total
        this.loading = false
      })
    },
    tabledeal(row, column, cell, event) {
      //this.dealList = row.childList;
    },
    reset() {
      this.form = {
        id: undefined,
        themeId: undefined,
        groupId: undefined,
        code: undefined,
        name: undefined,
        type: undefined,
        remark: undefined,
      }

      if (this.$refs.form !== undefined) {
        this.$refs.form.resetFields()
      }
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    handleAdd() {
      this.reset()
      this.open = true
    },
    handleUpdate(row) {
      this.reset()
      this.form = { ...row }
      this.open = true
      // getVariableRecordById(row.id).then(response => {
      //   this.form = response.data;
      //   this.open = true;
      // });
    },
    // /** 提交按钮 */
    // submitForm: function () {
    //   this.$refs["form"].validate(valid => {
    //     if (valid) {
    //       // console.log(this.group);
    //       // console.log(this.variable);
    //       this.form.groupId = this.queryParams.groupId;
    //       this.form.themeId = this.variable.themeId;
    //       submitVariableRecord(this.form).then(response => {
    //         if (response.code === 200) {
    //           // this.msgSuccess("修改成功");
    //           this.$message({
    //             showClose: true,
    //             message: "修改成功",
    //             type: "success"
    //           })
    //           this.open = false;
    //           this.getList();
    //         } else {
    //           this.msgError(response.msg);
    //         }
    //       });
    //     }
    //   });
    // },

    typeFormat(row, column) {
      return this.selectDictLabel(this.typeOptions, row.type)
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
::v-deep .el-pagination__rightwrapper {
  margin-left: 20px;
}
.isShowborder ::v-deep .el-input__inner {
  border: none;
  background-color: #fff;
}

.app-container {
  .el-form-item .el-select,
  .el-date-editor {
    width: 100%;
  }
}

.tb-edit .input-box {
  display: none;
}

.tb-edit .current-cell .input-box {
  display: block;
  margin-left: -15px;
}

.item {
  margin-bottom: 18px;
}

.item-mini {
  margin-bottom: 9px;
}

.title {
  font-size: 20px;
}

.title-button {
  float: right;
}

.pagination {
  display: flex;
  justify-content: center;
  background-color: #fff;
  padding: 20px;
}

.dialog-footer {
  text-align: right;
  padding-right: 20px;
}
</style>
