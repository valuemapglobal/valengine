<template>
  <div>
    <el-drawer
      :visible.sync="visible"
      direction="rtl"
      :with-header="false"
      size="35%"
    >
      <div class="drawer">
        <Headline @click="resetFields">{{ title }}</Headline>

        <div class="form-warp1" v-if="dictLabel">
          <div class="step-line"></div>
          <div>
            <el-form label-width="90px" :model="form">
              <div class="circle-warp">
                <div class="circle-one">1</div>
                <el-form-item label="业务场景：">
                  <div class="value">{{ dictLabel }}</div>
                </el-form-item>
              </div>
            </el-form>
            <el-form
              label-width="90px"
              :model="form"
              v-for="(item, index) in dataList"
              :key="index"
            >
              <div class="circle-warp">
                <div class="circle-one">{{ index + 2 }}</div>
                <el-form-item label="策略模型：">
                  <div>
                    <div class="m-info">
                      <div class="name">评分模型-{{ item.ruleName }}</div>
                      <div
                        class="name"
                        style="
                          color: #3662ec;
                          margin-left: 10px;
                          cursor: pointer;
                        "
                        @click="handlderRes(item)"
                      >
                        运行结果
                      </div>
                    </div>
                    <div class="m-info" style="margin-top: 10px">
                      <div class="key">运行时间：</div>
                      <div class="value">{{ item.approvalTime }}</div>
                    </div>
                    <div class="status" style="margin-top: 10px">
                      <div class="key">审批状态：</div>
                      <div class="value" style="color: #00b578">
                        {{ item.result }}
                      </div>
                    </div>
                  </div>
                </el-form-item>
              </div>
            </el-form>
          </div>
        </div>
      </div>
    </el-drawer>
    <el-drawer
      :visible.sync="resVisible"
      direction="rtl"
      :with-header="false"
      size="20%"
    >
      <div class="drawer">
        <Headline @click="resVisible = false">运行结果</Headline>
        <div class="" style="display: flex">
          <!-- <div style="color: rgba(0,0,0,0.6);">运行结果：</div> -->
          <div style="color: rgba(0, 0, 0, 0.85)">
            <div class="resItem">{{ Item.detail }}</div>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import Headline from '@/components/Headline'
import { getTaskDetail } from '../../api/platformEngine'
export default {
  components: {
    Headline,
  },
  props: {
    title: {
      type: String,
      default: '详情',
    },
    info: {
      type: Object,
      default: {},
    },
    sceneList: {
      type: Array,
      default: [],
    },
  },
  data() {
    return {
      Item: {},
      resVisible: false,
      dataList: [],
      form: {},
      dictLabel: '',
      visible: false,
    }
  },
  computed: {
    //    mapresList(){
    //     return this.dataList.map((item)=>item.detail)
    //    }
  },
  methods: {
    handlderRes(item) {
      this.Item = item
      this.resVisible = true
    },
    getDetail(id) {
      getTaskDetail({ id: id }).then((res) => {
        if (res.code == 200) {
          this.dataList = res.data.dataList
          this.dictLabel = this.sceneList.filter(
            (item) => item.dictValue == res.data.processPolicy.businessCode
          )[0].dictLabel
        }
      })
    },
    resetFields() {
      this.visible = false
    },
  },
}
</script>

<style lang="less" scoped>
.drawer {
  padding: 30px 20px;
  box-sizing: border-box;

  .form-warp1 {
    margin-top: 34px;
    display: flex;
    position: relative;

    ::v-deep .el-input__inner {
      // width: 371px;
      // height: 48px;
      background: #f4f6f9;
      border: none;
    }

    /deep/ .el-form-item__label {
      line-height: 18px !important;
    }
  }

  .step-line {
    width: 1px;
    // height: 134px;
    background: rgba(0, 0, 0, 0.08);
    margin-right: 11px;
    margin-top: 16px;
  }

  .circle-one {
    width: 16px;
    height: 16px;
    background: var(--primary-color);
    border-radius: 8px 8px 8px 8px;
    position: relative;
    left: -18px;
    top: 16px;
    font-size: 12px;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: #fff;
    text-align: center;
    line-height: 16px;
  }
}

.info_warp {
  display: flex;
  margin-left: 5px;
}

.key {
  font-size: 14px;
  font-family: PingFang SC-Regular, PingFang SC;
  font-weight: 400;
  color: rgba(0, 0, 0, 0.6);
  line-height: 18px;
}

.value {
  font-size: 14px;
  font-family: PingFang SC-Medium, PingFang SC;
  font-weight: 500;
  color: rgba(0, 0, 0, 0.85);
  line-height: 18px;
}

.m-info {
  display: flex;

  .name {
    line-height: 18px;
  }
}

.status {
  width: 530px;
  height: 46px;
  background: #fbfbfb;
  border-radius: 6px 6px 6px 6px;
  box-sizing: border-box;
  padding-left: 12px;
  display: flex;
  align-items: center;

  .name {
    line-height: 18px;
  }
}
</style>
