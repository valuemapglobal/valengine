<template>
  <div class="myUpload">
    <div v-for="(item, index) in fileInfo" :key="index" class="imgInfo">
      <div class="img" :style="{ width: imgWidth, height: imgHeight }">
        <div
          v-if="item.status === 'uploading'"
          class="picLoading"
          v-loading="true"
        ></div>
        <iframe
          v-if="item.status != 'uploading' && getType(item.name) === 'pdf'"
          :src="item.url"
          frameborder="0"
        ></iframe>
        <video
          id="inner_video"
          style="width: 100%"
          v-if="item.status != 'uploading' && getType(item.name) === 'video'"
          :src="item.url"
        ></video>
        <el-image
          :style="{ width: imgWidth, height: imgHeight }"
          v-if="item.status != 'uploading' && getType(item.name) === 'image'"
          :src="item.url"
          fit="cover"
        ></el-image>
        <el-image
          :style="{ width: imgWidth, height: imgHeight }"
          v-if="item.status != 'uploading' && getType(item.name) === 'other'"
          :src="require('@/assets/images/fujian.png')"
          fit="cover"
        ></el-image>

        <span class="img-mask">
          <span
            class="uploadIcon"
            v-if="['image', 'pdf'].includes(getType(item.name))"
          >
            <i
              class="el-icon-zoom-in"
              @click="handlePreview(item.url, item.name)"
            ></i>
          </span>
          <span
            class="uploadIcon"
            v-if="['video'].includes(getType(item.name))"
          >
            <i class="el-icon-video-play" @click="videoPlay(item.url)"></i>
          </span>
          <span class="uploadIcon" v-if="hasDownload">
            <i class="el-icon-download" @click="handleDownload(item)"></i>
          </span>
          <span class="uploadIcon" v-if="hasDelete">
            <i class="el-icon-delete" @click="handleBeforeRemove(item.url)"></i>
          </span>
        </span>
      </div>
      <span class="name" v-if="showName && uploadStatus">{{ item.name }}</span>
    </div>
    <el-upload
      ref="myUpload"
      v-show="fileInfo.length < limit && uploadStatus"
      :action="uploadImgUrl"
      :headers="headers"
      :file-list="fileList"
      :on-success="handleSuccess"
      :before-upload="handleBeforeUpload"
      :on-progress="handleProgress"
      :before-remove="handleBeforeRemove"
      :on-preview="handlePreview"
      :show-file-list="false"
      multiple
      :accept="accept"
    >
      <slot name="upload">
        <div class="uploadBtn">
          <i slot="default" class="el-icon-plus"></i>
        </div>
      </slot>
    </el-upload>
    <el-image-viewer
      v-if="imgView.visible"
      :url-list="imgView.urlList"
      :initialIndex="imgView.initialIndex"
      :on-close="closeViewer"
      :z-index="9999"
    ></el-image-viewer>
    <!-- <elementVideo ref="elementVideo" :url="videoUrl"></elementVideo> -->
  </div>
</template>

<script>
import { getToken } from '@/utils/auth'
import ElImageViewer from 'element-ui/packages/image/src/image-viewer'
// import elementVideo from '@/components/element/video/index.vue'
export default {
  props: {
    dataList: {
      type: Array || String,
      default: () => [],
    },
    field: {
      type: Number,
      default: 0,
    },
    limit: {
      type: Number,
      default: 10,
    },
    accept: {
      type: String,
      default: '.jpg,.jpeg,.png,.JPG,.JPEG,.PDF,.pdf',
    },
    uploadStatus: {
      type: Boolean,
      default: true,
    },
    showName: {
      type: Boolean,
      default: true,
    },
    imgWidth: {
      type: String,
      default: '148px',
    },
    imgHeight: {
      type: String,
      default: '148px',
    },
    hasDownload: {
      type: Boolean,
      default: false,
    },
    hasDelete: {
      type: Boolean,
      default: false,
    },
    fileSize: {
      type: Number,
      default: 10,
    },
    uploadUrl: {
      type: String,
      // default: '/common/upload'
      default: '/element/upload',
    },
    uploadHeaders: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  components: {
    ElImageViewer,
    // elementVideo,
  },
  data() {
    return {
      uploadImgUrl: null, //图片上传地址
      headers: {
        Authorization: getToken(),
        // Authorization: 'Bearer ' + getToken(),
      },
      fileList: [],

      //是否允许上传
      // uploadStatus: true,

      fileStatus: {
        progress: '',
        status: '',
        url: '',
        name: '',
        uid: '',
      },
      fileInfo: [],
      //大图展示相关
      imgView: {
        visible: false,
        initialIndex: 0,
        urlList: [],
      },
      firstTime: true,
      callBackList: [],
      syncList: [],
      watchStatus: true,
      videoUrl: null,
    }
  },
  watch: {
    fileInfo: {
      handler(val) {
        this.$emit('startUpload', val.length < this.limit)
      },
    },
    dataList: {
      handler(val) {
        if (this.watchStatus) {
          this.imgView.urlList = []
          this.fileInfo = val.map((item) => {
            let form = {
              name: null,
              url: null,
            }
            if (typeof item == 'string') {
              form.name = item
              form.url = item
              this.imgView.urlList.push(item)
            } else if (typeof item == 'object') {
              form.name = item.fileName || item.name || item.filename
              form.url = item.fileUrl || item.url || item.fileurl
              this.imgView.urlList.push(form.url)
            }
            return form
          })
          this.callBackList = JSON.parse(JSON.stringify(this.fileInfo))
        }
        this.watchStatus = true
      },
      deep: true,
      immediate: true,
    },
    callBackList: {
      handler(val) {
        this.watchStatus = false
        this.$emit('update:dataList', val)
      },
      deep: true,
    },
    uploadHeaders: {
      handler(val) {
        this.headers = { ...this.headers, ...val }
      },
      deep: true,
    },
  },
  computed: {
    getType() {
      return (name) => {
        if (name) {
          let suffix = name.split('.')[name.split('.').length - 1]
          if (['pdf', 'PDF'].includes(suffix)) return 'pdf'
          else if (
            ['jpg', 'jpeg', 'png', 'JPG', 'JPEG', 'PNG'].includes(suffix)
          )
            return 'image'
          else if (['mp4'].includes(suffix)) return 'video'
          else return 'other'
        }
      }
    },
  },
  created() {
    this.uploadImgUrl =
      process.env.VUE_APP_ENV === 'production'
        ? '/prod-api/finance/common/uploadFile'
        : '/dev-api/prod-api/finance/common/uploadFile'
  },
  mounted() {},
  methods: {
    handleProgress(event, file, fileList) {
      fileList.forEach((item) => {
        this.showProgress(item, item.percentage)
      })
    },
    handleDownload(data) {
      let link = document.createElement('a')
      link.style.display = 'none'
      link.href = data.url
      link.download = data.name
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    },
    showProgress(file, progress) {
      const arr = [...this.fileInfo].map((items) => {
        if (items.uid === file.uid && items.status != 'success') {
          items.progress = parseInt(progress.toFixed(0))
        }
        return items
      })
      this.fileInfo = [...arr]
    },

    handlePreview(file, name) {
      if (this.getType(name) == 'pdf') {
        window.open(file, name)
        return
      }
      this.imgView.urlList.forEach((item, index) => {
        if (item === file) {
          this.imgView.initialIndex = index
        }
      })
      this.imgView.visible = true
    },

    videoPlay(url) {
      this.videoUrl = url
      this.$refs.elementVideo.visible = true
      this.$nextTick(() => {
        let mainvideo = document.getElementById('inner_video')
        mainvideo.currentTime = 0
        mainvideo.pause()
      })
    },

    handleBeforeUpload(file) {
      let imgSize = Number(file.size / 1024 / 1024)
      let fileType = file.name.substring(file.name.lastIndexOf('.'))

      if (imgSize > this.fileSize) {
        this.$message({
          duration: 5000,
          message: `文件大小不能超过${this.fileSize}MB，请重新上传`,
          type: 'warning',
        })
        return false
      }

      if (this.accept != 'all') {
        let type = this.accept.split(',')
        if (!type.includes(fileType)) {
          this.$message({
            duration: 5000,
            message: `请上传文件类型为${type.join('、')}的文件`,
            type: 'warning',
          })
          return false
        }
      }

      let fileList = {}
      for (let key in file) {
        fileList.uid = file.uid
        fileList.name = file.name
      }
      this.fileInfo.push({ ...fileList, progress: 0, status: 'uploading' })
    },

    closeViewer() {
      this.imgView.visible = false
    },

    handleSuccess(response, file, fileList) {
      this.$emit('uploadSuccess', response, this.field)
      fileList.forEach((item) => {
        if (
          item.response &&
          item.response.code == 200 &&
          item.status === 'success'
        ) {
          let url = item.response.data.url
          this.fileInfo.map((file) => {
            if (file.uid === item.uid) {
              file.status = item.status
              file.url = url
              this.imgView.urlList.push(url)
              this.imgView.urlList = [...new Set(this.imgView.urlList)]
              this.callBackList.push({
                name: file.name,
                url: file.url,
              })
              this.callBackList = [...new Set(this.callBackList)]
            }
            return file
          })
        } else {
          this.$message.warning(item.response.msg)
          this.fileInfo = this.fileInfo.filter((item) => item.uid != file.uid)
        }
      })
    },

    handleBeforeRemove(url) {
      this.fileInfo.forEach((item, index) => {
        if (item.url === url) {
          this.fileInfo.splice(index, 1)
        }
      })
      this.imgView.urlList.forEach((item, index) => {
        if (item == url) {
          this.imgView.urlList.splice(index, 1)
        }
      })
      this.callBackList.forEach((item, index) => {
        if (url.indexOf(item.url) != -1) {
          this.callBackList.splice(index, 1)
        }
      })
    },

    handleUpload() {
      this.$refs['myUpload'].$children[0].$refs.input.click()
    },
  },
}
</script>

<style lang="less" scoped>
.myUpload {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 10px;
  justify-content: flex-start;

  // > div:nth-of-type(2) {
  //   display: flex;
  //   align-items: center;
  //   justify-content: center;
  // }

  .imgInfo {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .img {
    margin-right: 8px;
    width: 148px;
    height: 148px;
    box-sizing: border-box;
    border-radius: 4px;
    overflow: hidden;
    box-shadow: 0px 0px 2px rgb(0, 0, 0);
    display: flex;
    position: relative;
    // margin-bottom: 10px;
    margin: 5px;
    z-index: 99;

    .picLoading {
      width: 100%;
      height: 100%;
      display: flex;
    }

    // div {
    //   display: flex;
    //   align-items: center;
    //   justify-content: center;
    // }

    .other {
      width: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      box-sizing: border-box;

      .icon {
        width: 120px;
        height: 120px;
        // background-color: red;
        margin: 5px 0px;
        border-radius: 5px;
      }

      span {
        display: block;
        // width: 100%;
        font-size: 14px;
      }
    }

    .el-progress {
      width: 100%;
      height: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .img-mask {
      position: absolute;
      background-color: rgba(0, 0, 0, 0.5);
      display: none;
      width: 100%;
      height: 100%;

      .uploadIcon {
        margin: 0px 8px;

        i {
          color: aliceblue;
        }
      }
    }
  }

  .img:hover {
    .img-mask {
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 1s linear;
    }
  }

  span {
    font-size: 12px;
  }

  .uploadBtn {
    width: 150px;
    height: 150px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px dashed #bd9c70;
    border-radius: 4px;
  }
}

:deep(.el-upload--picture-card) {
  margin: 5px;
  // width: 100% !important;
  // height: 100% !important;
  box-sizing: border-box;
  line-height: normal;
}

.el-upload-list__item-actions {
  display: flex;
  align-items: center;
  justify-content: center;
}

.uploadPic {
  width: 100%;
}

.uploadIcon {
  .el-icon-zoom-in,
  .el-icon-delete,
  .el-icon-download,
  .el-icon-video-play {
    font-size: 20px;
    cursor: pointer;
  }
}

::v-deep.el-image {
  display: flex;
  align-items: center;

  .el-image__inner {
    width: 100%;
    height: auto;
  }
}
</style>
