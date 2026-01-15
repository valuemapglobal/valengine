<template>
  <div class="businessTabs">
    <ul class="tabs" :style="{ width: tabsWidth }">
      <li
        v-for="(item, index) in tabs"
        :key="index"
        :style="`cursor:${item.disabled ? 'not-allowed' : 'pointer'}`"
        :class="active === item.businessCode ? 'active' : ''"
        @click="actived(item, item.disabled || false)"
      >
        <div
          v-if="item.deptId != 1"
          class="edit-icon"
          @click.stop="editBusinessHandle(item)"
        >
          <i class="el-icon-edit" />
        </div>
        <div v-else style="width: 10px; height: 10px" />
        {{ item.name }}
        <div
          v-if="item.deptId != 1"
          class="close-icon"
          @click.stop="deleteBusinessHandle(item)"
        >
          <i class="el-icon-delete" />
        </div>
        <div v-else style="width: 10px; height: 10px" />
      </li>
      <li
        class="add-tab"
        :style="{
          background: addInputShow ? '#ffffff' : 'var(--primary-color)',
        }"
      >
        <el-input
          ref="inputRef"
          v-if="addInputShow"
          :placeholder="$t('decisionPlatform.inputName')"
          v-model.trim="nameValue"
          @blur="addInputShow = false"
          @keyup.enter.native="addFormSubmit"
          clearable
        >
        </el-input>
        <div class="add-tab-button" @click="addBtnClick" v-else>
          <div class="add-icon"></div>
          {{ $t('decisionPlatform.add') }}
        </div>
      </li>
    </ul>
    <div class="content">
      <components is="Modules"></components>
    </div>
    <el-dialog
      :title="$t('decisionPlatform.edit')"
      :visible.sync="editVisible"
      width="26%"
    >
      <el-input
        :placeholder="$t('decisionPlatform.inputName')"
        v-model.trim="nameValue"
        clearable
      >
      </el-input>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editVisible = false">{{
          $t('common.cancel')
        }}</el-button>
        <el-button type="primary" @click="editFormSubmit">{{
          $t('common.sure')
        }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Modules from './modules/index.vue'
import { mapActions, mapState } from 'vuex'
import {
  business_search,
  deleteBusiness,
  editBusiness,
  newlyAddBusiness,
} from '@/views/decisionPlatform/modules/productDecision/api'

export default {
  name: 'BusinessTabs',
  props: {},
  components: {
    Modules,
  },
  data() {
    return {
      tabsWidth: 'auto',
      active: 1, //默认选中项
      tabs: [],
      addInputShow: false, //添加输入框
      nameValue: '',
      editVisible: false,
      editId: undefined,
    }
  },
  computed: {
    ...mapState(['dataRisk']),
  },
  watch: {},
  created() {
    // this.changeProductDecision({ businessCode: 1 })
  },
  mounted() {
    this.getTabList()
    if (document.getElementsByClassName('nav-bar-drown')[0]) {
      const elementWidth =
        document.getElementsByClassName('nav-bar-drown')[0].offsetWidth
      this.tabsWidth = `calc(100% - ${elementWidth}px - 82px)`
    }
  },
  beforeDestroy() {},
  methods: {
    ...mapActions(['changeProductDecision', 'changeBusiness']),
    getTabList() {
      business_search().then((res) => {
        this.tabs = res.data.list.map((item) => {
          return {
            ...item,
            show: true,
            disabled: false,
            businessCode: item.id,
            auth: null,
          }
        })
        if (this.tabs.length) {
          this.changeProductDecision({
            businessCode: this.tabs[0].id,
            businessName: this.tabs[0].name,
          })
          this.changeBusiness(this.tabs[0])
        }
      })
    },
    actived(item, disabled) {
      if (disabled) return
      this.changeProductDecision({
        businessCode: item.businessCode,
        businessName: item.name,
      })
      this.changeBusiness(item)

      this.active = item.businessCode
    },
    editBusinessHandle(item) {
      this.nameValue = item.name
      this.editId = item.id
      this.editVisible = true
    },
    editFormSubmit() {
      const name = this.nameValue
      if (!name) {
        this.$message.warning(this.$t('decisionPlatform.inputName'))
      } else {
        this.editVisible = false
        editBusiness({
          name: name,
          id: this.editId,
        }).then((res) => {
          if (res.code === 200) {
            this.$message.success(this.$t('decisionPlatform.editComplete'))
            this.getTabList()
          }
        })
      }
    },
    deleteBusinessHandle(item) {
      this.$confirm(this.$t('common.deleteConfirm'), '', {
        confirmButtonText: this.$t('common.sure'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning',
      })
        .then(() => {
          deleteBusiness(item.id).then((res) => {
            if (res.code === 200) {
              this.$message({
                type: 'success',
                message: this.$t('decisionPlatform.deleteSuccess'),
              })
              this.getTabList()
            }
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: this.$t('decisionPlatform.deleteCancelled'),
          })
        })
    },
    addBtnClick() {
      this.nameValue = ''
      this.addInputShow = true
      this.$nextTick(() => {
        this.$refs.inputRef.focus()
      })
    },
    /**
     * 新增表单提交
     */
    addFormSubmit() {
      const name = this.nameValue
      if (!name) {
        this.$message.warning(this.$t('decisionPlatform.inputName'))
      } else {
        this.addInputShow = false
        newlyAddBusiness({
          name,
        }).then((res) => {
          if (res.code === 200) {
            this.$message.success(this.$t('decisionPlatform.addSuccess'))
            this.getTabList()
          }
        })
      }
    },
  },
}
</script>

<style lang="less" scoped>
.businessTabs {
  height: 100%;
  display: flex;
  flex-direction: column;

  .tabs {
    position: relative;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    margin: 0;
    padding: 0;
    overflow-x: auto;

    > li {
      padding: 0 8px;
      //min-width: 124px;
      height: 50px;
      background: rgba(#000, 0.04);
      display: flex;
      align-items: center;
      justify-content: space-around;
      margin-right: 4px;
      border-radius: 5px 5px 0 0;
      // font-family: HarmonyOS_Sans_SC;
      font-size: 14px;
      font-weight: normal;
      line-height: 16px;
      letter-spacing: 0px;
      color: var(--text-color-secondary);
      cursor: pointer;

      .edit-icon,
      .close-icon {
        cursor: pointer;
        position: relative;
        opacity: 0;
        z-index: -100;
        transition: opacity 0.3s;
        > i {
          font-size: 12px;
        }
      }

      .edit-icon {
        margin-right: 8px;
      }

      .close-icon {
        margin-left: 8px;
      }

      &:hover {
        .edit-icon,
        .close-icon {
          opacity: 1;
          z-index: 6;
        }
      }

      &.add-tab {
        padding: 0;
        color: #fff;
        justify-content: center;
        background: var(--primary-color);

        ::v-deep .el-input {
          .el-input__inner {
            height: 50px;
            line-height: 50px;
            border-radius: 6px 6px 0 0;
            border: none;
          }
        }

        .add-tab-button {
          width: 96px;
          height: 100%;
          display: flex;
          align-items: center;
          justify-content: center;

          .add-icon {
            position: relative;
            width: 10px;
            height: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 8px;

            &::before,
            &::after {
              position: absolute;
              content: '';
              display: block;
              width: 10px;
              height: 2px;
              background: #ffffff;
              border-radius: 2px;
            }

            &::after {
              width: 2px;
              height: 10px;
            }
          }
        }
      }
    }

    > .active {
      background: var(--bg-color);
      border: var(--decision-border);
      border-bottom: none;
      font-size: 14px;
      font-weight: 500;
      line-height: 16px;
      letter-spacing: 0px;
      color: var(--primary-color);
    }
  }

  .content {
    flex: auto;
    overflow: hidden;
    background-color: var(--bg-color);
  }

  .kb {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
  }
}
</style>
