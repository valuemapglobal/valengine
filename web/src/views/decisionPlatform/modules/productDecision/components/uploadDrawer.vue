<template>
    <el-drawer custom-class="import-drawer" :title="title" :visible.sync="isShowDrawer" append-to-body size="570px"
        :wrapperClosable="false" :before-close="cancel">
        <div class="upload">
            <div class="left-upload">
                <span class="text"> 上传 </span>
                <el-upload ref="upload" :action="uploadUrl" :on-success='handleSuccess' :before-upload='handleBeforeUpload'
                    :headers='uploadHeader' multiple :limit="1" :file-list="fileList">
                    <div class="upload-demo uploadLink">
                        上传文件
                        <span class="el-upload__tip"> 只能上传.XLS和.XLSX文件</span>
                    </div>
                </el-upload>
            </div>
            <div class="uploadLink downLoad" @click="onDownload">下载模版</div>
        </div>
        <div style="padding: 0px 20px;">
            <div class="btn-box">
                <el-button style="background: #E4E6EF;height: 42px;border: none;width: 82px;" @click="$emit('change')">
                    取消
                </el-button>
                <el-button type="primary" style="height: 42px;width: 82px;">
                    导入
                </el-button>
            </div>
            <div class="tip-box">
                <div class="text-color">提示</div>
                <div>1.如果上传的数据不在进件历史数据库内，该数据上传将不会成功。</div>
                <div>2.多次进行上传操作时，系统无法叠加数据，将以最新一次上传的数据作为模型回溯依据。</div>
            </div>
        </div>

    </el-drawer>
</template>
  
<script>
import {
    getToken
} from "@/utils/auth";
// import { uploadFileUrl } from "@/api";

import {
    uploadAB,
} from '../api/riskModel'
export default {
    name: 'uploadDrawer',
    props: {
        isShow: Boolean,
        title: String,
        searchForm:Object
    },
    model: {
        event: 'change',
        prop: 'isShow',
    },
    data() {
        return {
            uploadUrl: uploadAB(), //图片上传地址,
            form: {},
            formRules: {},

            fileList: [],
            uploadHeader: {
                Authorization: getToken()
            }
        }
    },
    computed: {
        isShowDrawer: {
            get() {
                return this.isShow
            },
            set(val) {
                this.$emit('change', val)
            },
        },
    },
    mounted() {
    },

    watch: {},

    methods: {
        handleSuccess(res, file, fileList) {
            if (res.code == 200) {
                this.$message.success('操作成功')
                    this.cancel()
            } else {
                this.$message.warning('上传失败')
                this.$refs.upload.clearFiles()
            }
        },

        /*
            * 下载模版
            * url: 模版路径
            * */
        onDownload() {
            const Link = document.createElement("a");
            Link.href = `${window.location.origin}/回溯列表上传.xlsx`;
            Link.style.display = "none";
            document.body.appendChild(Link);
            Link.click(); // 触发点击事件
            document.body.removeChild(Link);
        },

        handleBeforeUpload(file) {
            const extension = file.name.substring(file.name.lastIndexOf('.') + 1)
            const size = file.size / 1024 / 1024
            if (!['xls', 'xlsx'].includes(extension)) {
                this.$message.warning('只能上传excel的文件')
                return false
            }
            if (size > 5) {
                this.$message.warning('文件大小不得超过5M')
                return false
            }
            return
        },
        // 取消
        cancel() {
            this.$refs.upload.clearFiles()
            this.$emit('change', false)
        },
    },
}
</script>
  
<style lang="less" scoped>
::v-deep .el-drawer__header {
    color: rgba(0, 0, 0, 0.85);
}

.import-drawer {
    .upload {
        padding: 0px 20px;
        display: flex;
        align-items: end;

        .left-upload {
            display: flex;

            .text {
                font-size: 14px;
                font-family: PingFang SC, PingFang SC;
                font-weight: 400;
                color: rgba(0, 0, 0, 0.85);
                margin-right: 14px;
            }

        }

        .uploadLink {
            font-size: 14px;
            color: #5492fd;
            cursor: pointer;
            margin-bottom: 10px;
        }

        .downLoad {
            font-weight: 500;
            color: #3369EB;
            margin-left: 20px;

        }

        .uploadLink:hover {
            color: #005eff;
        }
    }
}

.btn-box {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}

.upload-demo {
    width: 395px;
    height: 160px;
    background: rgba(40, 136, 232, 0.04);
    border-radius: 2px 2px 2px 2px;
    opacity: 1;
    border: 1px dashed #366EF4;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;

    .el-upload__tip {
        color: rgba(0, 0, 0, 0.3);
    }

}

.tip-box {
    margin-top: 20px;
    font-size: 13px;
    color: rgba(0, 0, 0, 0.45);
    line-height: 23px;

    .text-color {
        color: #FF8F1F;
        font-size: 14px;

    }
}
</style>
  