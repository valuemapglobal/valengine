<template>
  <!--流程使用弹窗-->
  <el-drawer title="流程使用" size="70%" :visible.sync="drawer">
    <div class="process-usage">
      <div class="process-usage-right">
        <div class="search">
          <el-select class="post" value="post" placeholder="请选择">
            <el-option label="post" value="post"></el-option>
          </el-select>
          <div class="api-url">
            <span>{{ API }}</span>
            <div class="copy" @click="copyText(API)">
              <i class="el-icon-document-copy"></i>
              复制
            </div>
          </div>
          <el-tooltip
            class="item"
            effect="dark"
            content="点击触发流程"
            placement="right-start"
          >
            <el-button type="primary" @click="submit">发送</el-button>
          </el-tooltip>
          <el-select
            v-model="isBatch"
            clearable
            placeholder="请选择是否批量"
            @clear="isBatch = false"
          >
            <el-option
              v-for="(item, index) in batchOptions"
              :key="index"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
          <el-select
            v-model="params.responseForm"
            clearable
            placeholder="请选择响应形式"
          >
            <el-option
              v-for="item in options"
              :key="item.dictValue"
              :label="item.dictLabel"
              :value="item.dictValue"
            >
            </el-option>
          </el-select>
          <el-select
            v-if="!isBatch"
            v-model="requestMethod"
            placeholder="请选择请求形式"
          >
            <el-option
              v-for="(item, index) in requestOptions"
              :key="index"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </div>
        <div class="content">
          <div class="content-item">
            <div class="content-item-title">请求参数</div>
            <el-table
              v-loading="tableLoading"
              :data="tableData"
              style="width: 100%"
              height="calc(100% - 58px)"
              v-if="!isBatch"
            >
              <el-table-column prop="name" label="参数名称"></el-table-column>
              <el-table-column prop="nameZh" label="参数说明"></el-table-column>
              <el-table-column label="参数类型" align="center">
                <el-tag type="primary" slot-scope="{ row }">{{
                  row.typeName
                }}</el-tag>
              </el-table-column>
              <el-table-column label="是否必填" align="center">
                <span slot-scope="{ row }">{{
                  row.isRequired ? '是' : '否'
                }}</span>
              </el-table-column>
              <el-table-column label="参数值" align="center">
                <template slot-scope="{ row }">
                  <el-input
                    placeholder="请输入"
                    v-model="params[row.name]"
                    clearable
                  ></el-input>
                </template>
              </el-table-column>
            </el-table>
            <div v-else>
              <el-upload
                v-if="fileList.length == 0"
                class="upload-file"
                drag
                :action="getUploadUrl()"
                :headers="headersConfig()"
                :before-upload="beforeUpload"
                :on-success="handleSuccess"
                :limit="1"
                accept=".xlsx,.xls"
                :show-file-list="false"
                :file-list="fileList"
                :data="{ processId: processData.id }"
                v-loading="uploadLoading"
              >
                <div class="upload">
                  <i class="el-icon-upload" />
                  <div class="upload-text">将文件拖到此处或选择文件</div>
                  <div class="upload-tip">
                    支持 xlsx,.xls 格式，单个文件最大 10MB
                  </div>
                  <div class="download-example" @click.stop="downloadExample">
                    下载示例文件
                  </div>
                </div>
              </el-upload>
              <div class="file-list">
                <div
                  class="file-item"
                  v-for="(item, index) in fileList"
                  :key="index"
                >
                  <div class="file-item-content">
                    <div class="content-left">{{ item.fileName }}</div>
                    <div class="content-right">
                      <!-- <div
                        class="content-right-download"
                        @click.stop="handleDownloadFile(item)"
                      >
                        <i class="el-icon-download"></i>
                      </div> -->
                      <div
                        class="content-right-delete"
                        @click.stop="handleDeleteFile(index)"
                      >
                        <i class="el-icon-close"></i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="content-item">
            <div class="content-item-title">
              响应参数
              <el-button
                v-if="!requestMethod"
                type="primary"
                @click="lookHandle"
                >查看结果</el-button
              >
            </div>
            <div
              class="content-item-json"
              v-if="requestMethod"
              v-loading="jsonLoading"
            >
              <json-viewer
                v-if="jsonData"
                :value="jsonData"
                expand-depth="5"
                expanded
                copyable
              />
            </div>
            <el-table
              v-else
              v-loading="tableLoading2"
              :data="tableData2"
              style="width: 100%"
              height="calc(100% - 58px)"
            >
              <el-table-column prop="name" label="参数名称"></el-table-column>
              <el-table-column prop="nameZh" label="参数说明"></el-table-column>
              <el-table-column label="参数类型" align="center">
                <el-tag type="primary" slot-scope="{ row }">{{
                  row.typeName
                }}</el-tag>
              </el-table-column>
              <el-table-column
                prop="value"
                label="参数值"
                align="center"
              ></el-table-column>
            </el-table>
          </div>
        </div>
      </div>
    </div>
  </el-drawer>
</template>

<script>
import {
  getProcessStrategySelect,
  getRequestData,
  getType,
  processTaskInitiation,
  processTaskInitiationSync,
  uploadFile,
  getTemplateExcel,
  submitBatch,
} from '@/views/platformEngine/api/platformEngine'
export default {
  data() {
    return {
      drawer: false,
      params: {
        responseForm: undefined,
      },
      processData: {
        id: undefined,
        processStrategy: undefined,
      },
      options: [],
      jsonData: null,
      jsonLoading: false,
      uploadLoading: false,

      fileList: [],
      isBatch: false,
      batchOptions: [
        {
          label: '单次',
          value: false,
        },
        {
          label: '批量',
          value: true,
        },
      ],
      requestMethod: false,
      requestOptions: [
        {
          label: '异步',
          value: false,
        },
        {
          label: '同步',
          value: true,
        },
      ],
      //请求参数
      tableLoading: false,
      tableData: [],
      //响应参数
      tableLoading2: false,
      tableData2: [],
      API: process.env.VUE_APP_PROCESS,
    }
  },
  mounted() {
    this.init()
  },
  watch: {
    drawer: {
      handler(cur) {
        if (cur && this.tableData) {
          this.tableData.forEach((item) => {
            //初始化params
            this.$set(this.params, item.name, undefined)
          })
        }
      },
      deep: true,
      immediate: true,
    },
    'processData.id': {
      handler(cur) {
        if (cur) {
          this.tableData2 = []
          this.getRequestData()
        }
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    init() {
      getType('model_response_form').then((res) => {
        this.options = res.data
      })
    },
    copyText(textToCopy) {
      const dom = document.createElement('input')
      dom.value = textToCopy
      document.body.appendChild(dom)
      dom.select()
      document.execCommand('copy')
      document.body.removeChild(dom)
      this.$message.success('文本已复制到剪贴板')
    },
    /**
     * 获取请求参数表格数据
     * @returns {Promise<void>}
     */
    async getRequestData() {
      this.tableLoading = true
      getRequestData(this.processData.id)
        .then((res) => {
          this.tableLoading = false
          if (res.code === 200) {
            this.tableData = res.data
          }
        })
        .catch(() => {
          this.tableLoading = false
        })
    },
    /**
     * 发送按钮点击
     * @returns {ElMessageComponent}
     */
    submit() {
      if (!this.params.responseForm) {
        return this.$message.warning('请选择响应形式')
      }
      const { responseForm, ...processEntry } = this.params
      let params = null
      if (!this.isBatch) {
        let policyRequestList = []
        for (let i = 0; i < this.tableData.length; i++) {
          let item = this.tableData[i]
          if (
            item.isRequired &&
            (processEntry[item.name] === '' ||
              processEntry[item.name] === undefined ||
              this.params[item.name] === null)
          ) {
            return this.$message.warning(`${item.name}是必填`)
          }
          if (
            processEntry[item.name] !== '' &&
            processEntry[item.name] !== undefined &&
            this.params[item.name] !== null
          ) {
            policyRequestList.push({
              ...item,
              value: processEntry[item.name],
            })
          }
        }
        params = {
          processStrategy: this.processData.processStrategy,
          processEntry,
          policyRequestList,
        }
      } else {
        if (this.fileList.length) {
          params = {
            ...this.fileList[0],
          }
        } else {
          this.$message.warning('请先上传请求参数')
          return
        }
      }

      // console.log({
      //   processId: this.processData.id,
      //   responseForm,
      //   ...params,
      // })

      // return

      this.tableLoading2 = true
      this.jsonLoading = true
      let URL = this.isBatch
        ? submitBatch
        : this.requestMethod
        ? processTaskInitiationSync
        : processTaskInitiation
      URL({
        processId: this.processData.id,
        responseForm,
        ...params,
      })
        .then((res) => {
          this.tableLoading2 = false
          this.jsonLoading = false
          if (res.code === 200) {
            if (this.requestMethod) {
              this.jsonData = res.data
            } else {
              if (!this.isBatch) {
                this.tableData2 = [
                  {
                    name: 'taskNo',
                    nameZh: '任务号',
                    typeName: '字符型',
                    value: res.data.taskNo,
                  },
                ]
              } else {
                this.tableData2 = [
                  {
                    name: 'batchNo',
                    nameZh: '任务号',
                    typeName: '字符型',
                    value: res.data.batchNo,
                  },
                ]
              }
            }
          }
        })
        .catch(() => {
          this.tableLoading2 = false
          this.jsonLoading = false
        })
    },
    //查看结果
    lookHandle() {
      this.$router.push({
        name: 'ProcessTask',
      })
    },
    getUploadUrl() {
      return uploadFile()
    },
    headersConfig: () => {
      return { Authorization: localStorage.getItem('id_token') }
    },
    downloadExample() {
      getTemplateExcel(this.processData.id).then((res) => {
        let blob = new Blob([res], { type: 'application/vnd.ms-excel' })
        let url = window.URL.createObjectURL(blob)
        let a = document.createElement('a')
        a.href = url
        a.download = '示例文件.xlsx'
        a.click()
        window.URL.revokeObjectURL(url)
        a.remove()
      })
    },
    beforeUpload(file) {
      // 文件大小限制50M
      if (file.size > 10 * 1024 * 1024) {
        this.$message.error('文件大小不能超过10MB')
        return false
      }

      // 检查文件扩展名
      const fileName = file.name.toLowerCase()
      const allowedExtensions = ['.xlsx', '.xls']
      const hasValidExtension = allowedExtensions.some((ext) =>
        fileName.endsWith(ext)
      )

      if (!hasValidExtension) {
        this.$message.error('只能上传Excel格式的文件（.xlsx, .xls）')
        return false
      }

      // 检查MIME类型
      const allowedMimeTypes = [
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', // .xlsx
        'application/vnd.ms-excel', // .xls
        'application/excel',
        'application/x-excel',
        'application/x-msexcel',
      ]

      if (!allowedMimeTypes.includes(file.type)) {
        this.$message.error('文件格式不正确，请上传Excel文件')
        return false
      }

      this.uploadLoading = true

      return true
    },
    handleDeleteFile() {
      this.fileList = []
    },
    handleSuccess(response, file, fileList) {
      if (response.code == 200) {
        let data = response.data

        this.fileList.push({
          fileName: file.name,
          fileUrl: data.fileUrl,
        })
      }
      this.uploadLoading = false
    },
  },
}
</script>

<style lang="less" scoped>
// ::v-deep .el-drawer {
//   &__header {
//     color: rgba(0, 0, 0, 0.85);
//     margin-bottom: 20px;
//   }

//   ::-webkit-scrollbar {
//     width: 8px;
//   }

//   ::-webkit-scrollbar-thumb {
//     border-radius: 4px;
//     background-color: rgba(193, 193, 193, 1);
//   }

//   ::-webkit-scrollbar-track {
//     border-radius: 4px;
//     background-color: rgba(241, 241, 241, 1);
//   }
// }

.process-usage {
  display: flex;
  width: 100%;
  height: calc(var(--bgvh) - 70px);

  &-right {
    flex: auto;
    height: 100%;
    overflow: hidden;

    .search {
      display: flex;

      ::v-deep .el-select {
        margin-right: 10px;
        .el-input {
          &__inner {
            font-size: 14px;
            width: 150px;
            height: 42px;
            border: 1px solid #ff8f1f;
          }
        }

        &.post {
          .el-input__inner {
            width: 80px;
            border: 1px solid #0bda96;
          }
        }
      }

      .api-url {
        margin-left: 10px;
        width: 60%;
        height: 42px;
        font-size: 14px;
        display: flex;
        align-items: center;
        padding: 0 15px;
        border-radius: 6px;
        background: var(--bg-color);
        color: var(--text-color-secondary);
        border: 1px solid #0bda96;

        > span {
          flex: 1;
          margin-left: 10px;
        }

        .copy {
          font-size: 12px;
          margin-left: 20px;
          color: #ffffff;
          background: #0bda96;
          padding: 4px 10px;
          border-radius: 4px;
          cursor: pointer;
          transition: opacity 0.2s;

          &:hover {
            opacity: 0.8;
          }
        }
      }

      ::v-deep .el-button {
        margin: 0 10px;

        &--primary {
          font-size: 14px;
          padding: 13px 20px;
          height: 42px;
          border-radius: 6px;
          background: var(--primary-color);
          border: none;
        }
      }
    }

    .content {
      height: calc(100% - 69px);

      &-item {
        height: 50%;

        &-title {
          padding: 20px 0;
          font-weight: 500;
          font-size: 14px;
          color: var(--text-color-secondary);
          line-height: 20px;

          ::v-deep .el-button {
            margin-left: 10px;
          }
        }

        ::v-deep .el-table {
          .el-input {
            &__inner {
              height: 35px;
              border-color: transparent;
              font-size: 12px;
              background: transparent;

              &:focus {
                border: 1px solid var(--primary-color);
              }
            }
          }

          .el-tag {
            height: 20px;
            line-height: 20px;
            padding: 0 6px;
            font-weight: 400;
            font-size: 12px;
            border: none;
            border-radius: 2px;

            &--success {
              color: #00b578;
              background: rgba(#00b578, 0.1);
            }

            &--primary {
              color: var(--primary-color);
              background: var(--primary-color-lighter);
            }
          }

          .el-table__cell {
            &:last-child {
              &:hover {
                .el-input {
                  &__inner {
                    border: 1px solid var(--primary-color);
                  }
                }
              }
            }
          }
        }

        &-json {
          height: calc(100% - 58px);
          background: rgba(var(--primary-color), 0.05);
          overflow-y: auto;
          padding: 20px;
          ::v-deep .jv-container .jv-code {
            background: rgba(var(--primary-color), 0.05);
            padding: 0px !important;
          }
        }
      }
    }
  }

  .upload-file {
    ::v-deep .el-upload {
      width: 100%;
      .el-upload-dragger {
        width: 100%;
        height: 100%;
        min-height: 200px;
        background: var(--bg-color);
        .upload {
          width: 100%;
          height: 100%;
          display: flex;
          flex-direction: column;
          padding: 24px;

          .el-icon-upload {
            margin: 10px 0px;
          }

          .upload-text {
            color: var(--text-color-secondary);
          }

          .upload-tip {
            margin-top: 8px;
            font-size: 13px;
            color: var(--text-color-tertiary);
          }

          .download-example {
            font-size: 13px;
            color: #2888e8;
            cursor: pointer;
            margin-top: 10px;
          }
        }
      }
    }
  }

  .file-list {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 10px;

    .file-item {
      padding: 4px 12px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      background: var(--bg-color-lighter);
      border-radius: 8px;
      margin-top: 8px;
      .file-item-content {
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;
        .content-left {
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          color: var(--text-color-secondary);
        }
        .content-right {
          display: flex;
          align-items: center;
          &-download,
          &-delete {
            width: 24px;
            height: 24px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
          }
          &-download:hover,
          &-delete:hover {
            background-color: var(--bg-color-lighter);
            border-radius: 4px;
          }
        }
      }
    }
  }
}
</style>
