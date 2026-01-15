<template>
  <!--接口平台（新版）-->
  <div class="scenario">
    <div class="scenario-menu">
      <div class="menu-title">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          xmlns:xlink="http://www.w3.org/1999/xlink"
          fill="none"
          version="1.1"
          width="12"
          height="12"
          viewBox="0 0 12 12"
        >
          <defs>
            <clipPath id="master_svg0_1_386">
              <rect x="12" y="0" width="12" height="12" rx="0" />
            </clipPath>
          </defs>
          <g
            transform="matrix(-1,0,0,1,24,0)"
            clip-path="url(#master_svg0_1_386)"
          >
            <g>
              <path
                d="M22.411376171875,5.17040888671875L22.410416171875,5.16937888671875Q21.920866171874998,4.635188886718749,20.926156171875,3.59549888671875Q20.686096171875,2.23337888671875,20.532646171875,1.46613588671875Q20.331726171875,0.46160888671875,19.358816171875,0.46160888671875Q19.078116171875,0.46160888671875,18.801496171875,0.58921288671875Q18.059686171875,0.93350058671875,16.814236171875,1.54059188671875Q15.345996171875,1.36513888671875,14.603906171875,1.29134988671875Q14.002186171875,1.22665388671875,13.594548171875,1.6816828867187499Q13.224828171875,2.09253888671875,13.283499171875,2.63542888671875Q13.366420171875,3.41810888671875,13.544617171875,4.83021888671875Q12.936422971875,6.09854888671875,12.597440171875,6.84016888671875Q12.329647171875,7.42345888671875,12.589968171875,7.92974888671875Q12.842311171875,8.42032888671875,13.412997171875,8.53281888671875Q14.189186171875,8.68255888671875,15.624576171874999,8.92815888671875Q16.641766171875,9.89113888671875,17.233326171875,10.42576888671875Q17.624386171875,10.78078888671875,18.076056171875,10.78078888671875L18.076436171875,10.78078888671875Q18.723866171875,10.78078888671875,19.097136171875,10.12942888671875Q19.519946171875,9.38749888671875,20.182646171875,8.15783888671875Q21.399936171874998,7.49318888671875,22.102226171875,7.08668888671875Q22.682076171875,6.75151888671875,22.766536171875,6.19753888671875Q22.851816171875,5.64901888671875,22.411376171875,5.17040888671875ZM21.076916171875,5.94119888671875Q20.582746171875,5.41073888671875,19.727496171875,4.51818888671875Q19.679876171875,4.46849888671875,19.674946171875,4.46311888671875Q19.522506171875,4.3042188867187505,19.484016171875,4.07755888671875Q19.300486171875,3.00323888671875,19.124786171875,2.09824888671875Q18.409706171875,2.43490888671875,17.278336171875,2.98716888671875Q17.273696171875,2.98942888671875,17.273406171875,2.98957888671875Q17.069536171875,3.08843888671875,16.848866171875,3.05936888671875L16.838876171875,3.05804888671875L16.828946171875,3.05632888671875Q16.443386171874998,2.98963888671875,14.815656171875,2.82359888671875Q14.961796171875001,4.1753988867187495,15.062576171875,4.85011888671875L15.063486171875,4.856158886718751L15.064236171875,4.86220888671875Q15.091866171875001,5.08412888671875,14.994156171875,5.28385888671875Q14.538936171875001,6.21950888671875,14.115806171875,7.13634888671875Q15.369426171875,7.37503888671875,16.091986171875,7.48291888671875Q16.323556171875,7.51999888671875,16.492306171875,7.67990888671875L16.492226171875,7.67998888671875Q16.515626171875,7.70178888671875,16.573026171875,7.75594888671875Q17.457096171875,8.59011888671875,17.972196171874998,9.06419888671875Q18.414516171875,8.27663888671875,18.964066171875,7.24967888671875Q19.071986171875,7.05124888671875,19.267416171875,6.94496888671875L19.267446171875,6.94502888671875Q19.289526171875,6.93278888671875,19.340576171875,6.90483888671875Q20.444326171874998,6.30058888671875,21.076916171875,5.94119888671875ZM23.534876171875,10.78680888671875Q23.534876171875,10.47622888671875,23.315576171875,10.25575888671875L23.314576171875,10.25476888671875L21.299966171875,8.240108886718751Q21.079736171875,8.01990888671875,20.768426171875,8.01990888671875Q20.457086171874998,8.01990888671875,20.235196171875,8.24181888671875Q20.016926171875,8.46216888671875,20.016926171875,8.77217888671875Q20.016926171875,9.08302888671875,20.236866171875,9.30380888671875L22.252896171875,11.31980888671875Q22.473506171875,11.53870888671875,22.783626171875,11.53870888671875Q23.094376171875,11.53870888671875,23.314676171875,11.31840888671875Q23.534876171875,11.09820888671875,23.534876171875,10.78680888671875Z"
                fill-rule="evenodd"
                fill="#FFC300"
                fill-opacity="1"
              />
            </g>
          </g>
        </svg>
        <span>{{ $t('interfacePlatform.interfaceModuleTheme') }}</span>
        <el-select
          v-model="supplierParams.interfaceDataType"
          :placeholder="$t('interfacePlatform.pleaseSelect')"
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </div>
      <div class="menu-content">
        <div
          class="menu-item"
          :class="{ isActive: item === 1 }"
          v-for="item in 20"
        >
          <div class="menu-item-name">{{ item.dataName || '司法研究院' }}</div>
          <div class="menu-item-action">
            <div
              data-v-184aeaba=""
              class="edit-icon"
              @click.stop="editMenu(item)"
            >
              <svg
                data-v-184aeaba=""
                xmlns="http://www.w3.org/2000/svg"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                fill="none"
                version="1.1"
                width="14"
                height="14"
                viewBox="0 0 14 14"
              >
                <defs data-v-184aeaba="">
                  <mask
                    data-v-184aeaba=""
                    id="master_svg0_499_73682"
                    maskUnits="objectBoundingBox"
                    style="mask-type: alpha"
                  >
                    <g data-v-184aeaba="">
                      <rect
                        data-v-184aeaba=""
                        x="0"
                        y="0"
                        width="14"
                        height="14"
                        rx="0"
                        fill="currentColor"
                        fill-opacity="1"
                      ></rect>
                    </g>
                  </mask>
                </defs>
                <g data-v-184aeaba="" mask="url(#master_svg0_499_73682)">
                  <g data-v-184aeaba="">
                    <g data-v-184aeaba="">
                      <rect
                        data-v-184aeaba=""
                        x="0"
                        y="11.4579416513443"
                        width="5.939393997192383"
                        height="1.4848483800888062"
                        rx="0.7424241900444031"
                        fill="currentColor"
                        fill-opacity="0.6000000238418579"
                      ></rect>
                    </g>
                    <g
                      data-v-184aeaba=""
                      transform="matrix(0.7071067690849304,-0.7071067690849304,0.7071067690849304,0.7071067690849304,-4.952927210694362,4.485733126849077)"
                    >
                      <path
                        data-v-184aeaba=""
                        d="M2.9382951259613037,8.22157871723175L12.140415125961304,8.22157871723175C12.648645125961304,8.22157871723175,13.060595125961303,8.633571717231751,13.060595125961303,9.14179171723175L13.060595125961303,12.82263871723175C13.060595125961303,13.33085871723175,12.648645125961304,13.742848717231752,12.140415125961304,13.742848717231752L2.9382951259613037,13.742848717231752L2.9382951259613037,8.22157871723175ZM12.140415125961304,12.82263871723175L3.8585081259613037,12.82263871723175L3.8585081259613037,9.14179071723175L12.140415125961304,9.14179171723175L12.140415125961304,12.82263871723175Z"
                        fill-rule="evenodd"
                        fill="currentColor"
                        fill-opacity="1"
                      ></path>
                    </g>
                    <g
                      data-v-184aeaba=""
                      transform="matrix(0.9789952635765076,-0.20388270914554596,0.20388270914554596,0.9789952635765076,-1.7102932122412131,0.6132693315595432)"
                    >
                      <path
                        data-v-184aeaba=""
                        d="M3.0453940056152344,12.738228289680482L2.1209873446152345,12.663628289680481L2.3099600056152343,11.755678289680482L2.7000310056152346,9.88150828968048L2.9652640056152344,8.607148289680481L3.678582005615234,9.695968289680481L5.179662005615235,11.987248289680482L5.818232005615235,12.96197828968048L4.656732005615234,12.86824828968048L3.0453940056152344,12.738228289680482Z"
                        fill-rule="evenodd"
                        fill="currentColor"
                        fill-opacity="1"
                      ></path>
                    </g>
                    <g
                      data-v-184aeaba=""
                      transform="matrix(0.9789952635765076,-0.20388270914554596,0.20388270914554596,0.9789952635765076,-1.9791168074042336,0.8780362788654337)"
                    >
                      <path
                        data-v-184aeaba=""
                        d="M3.271785020828247,11.685655307960511L4.764975020828247,11.79876530796051L3.613996020828247,10.04420530796051L3.271785020828247,11.685655307960511Z"
                        fill="currentColor"
                        fill-opacity="1"
                      ></path>
                    </g>
                  </g>
                </g>
              </svg>
            </div>
            <div
              data-v-184aeaba=""
              class="close-icon"
              @click.stop="deleteMenu(item)"
            >
              <svg
                data-v-184aeaba=""
                xmlns="http://www.w3.org/2000/svg"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                fill="none"
                version="1.1"
                width="10"
                height="10"
                viewBox="0 0 10 10"
              >
                <defs data-v-184aeaba="">
                  <mask
                    data-v-184aeaba=""
                    id="master_svg0_499_73690"
                    maskUnits="objectBoundingBox"
                    style="mask-type: alpha"
                  >
                    <g data-v-184aeaba="">
                      <rect
                        data-v-184aeaba=""
                        x="0"
                        y="0"
                        width="10"
                        height="10"
                        rx="0"
                        fill="currentColor"
                        fill-opacity="1"
                      ></rect>
                    </g>
                  </mask>
                </defs>
                <g data-v-184aeaba="" mask="url(#master_svg0_499_73690)">
                  <g data-v-184aeaba="">
                    <g
                      data-v-184aeaba=""
                      transform="matrix(0.6907609105110168,0.7230832576751709,-0.6907609105110168,0.7230831980705261,0.40679558681542716,-0.9511963012452895)"
                    >
                      <rect
                        data-v-184aeaba=""
                        x="1.3154727220535278"
                        y="0"
                        width="12.57242202758789"
                        height="1.2572423219680786"
                        rx="0.6286211609840393"
                        fill="currentColor"
                        fill-opacity="1"
                      ></rect>
                    </g>
                    <g
                      data-v-184aeaba=""
                      transform="matrix(-0.6907609105110168,0.7230832576751709,0.6907609105110168,0.7230831980705261,14.68349814781908,-6.279652911767016)"
                    >
                      <rect
                        data-v-184aeaba=""
                        x="8.684550285339355"
                        y="0"
                        width="12.57242202758789"
                        height="1.2572423219680786"
                        rx="0.6286211609840393"
                        fill="currentColor"
                        fill-opacity="1"
                      ></rect>
                    </g>
                  </g>
                </g>
              </svg>
            </div>
          </div>
        </div>
      </div>
      <div class="menu-footer">
        <el-button type="primary" icon="el-icon-plus">{{ $t('interfacePlatform.add') }}</el-button>
      </div>
    </div>
    <div class="scenario-wrapper">
      <div class="scenario-wrapper-search">
        <el-input
          v-model="apiParams.interfaceName"
          :placeholder="$t('interfacePlatform.inputInterfaceName')"
        >
          <span slot="prefix">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              xmlns:xlink="http://www.w3.org/1999/xlink"
              fill="none"
              version="1.1"
              width="14"
              height="14"
              viewBox="0 0 14 14"
            >
              <g>
                <g>
                  <path
                    d="M10.5024,9.67056C10.502,9.67023,10.5017,9.6699,10.5014,9.66957C10.4607,9.62748,10.451,9.56183,10.4873,9.51591C11.2794,8.51482,11.7516,7.24983,11.7516,5.87486C11.7516,2.63017,9.12098,0,5.87578,0C2.63059,0,0,2.63017,0,5.87486C0,9.11866,2.62916,11.7483,5.87314,11.7497C5.87509,11.7497,5.87647,11.7497,5.87841,11.7497C7.20121,11.7513,8.48444,11.3047,9.51957,10.4845C9.56536,10.4482,9.63011,10.4591,9.67135,10.5005C9.67161,10.5008,9.67187,10.501,9.67212,10.5013L13.0002,13.8288C13.1102,13.9388,13.2596,14.0004,13.4152,14C13.5654,13.9996,13.7095,13.9414,13.8177,13.8378C13.8257,13.8301,13.833,13.8228,13.8406,13.8148C14.058,13.5849,14.0511,13.2188,13.8316,12.9987C13.8311,12.9982,13.8308,12.9979,13.8303,12.9974L10.5038,9.67198C10.5032,9.67145,10.5029,9.6711,10.5024,9.67056M5.87543,10.5747C8.47151,10.5747,10.5761,8.47053,10.5761,5.87486C10.5761,3.27918,8.47151,1.17497,5.87543,1.17497C3.27935,1.17497,1.1748,3.27918,1.1748,5.87486C1.1748,8.47053,3.27935,10.5747,5.87543,10.5747Z"
                    fill-rule="evenodd"
                    fill="#3D3D3D"
                    fill-opacity="0.6000000238418579"
                  />
                </g>
              </g>
            </svg>
          </span>
        </el-input>
        <el-button type="primary" icon="el-icon-plus">{{ $t('interfacePlatform.addInterface') }}</el-button>
      </div>
      <div class="scenario-wrapper-content">
        <el-table
          :data="tableData"
          border
          height="calc(100% - 56px)"
          :header-cell-style="tableHeaderColor"
          :row-style="IntelligentExtensionRowStyle"
          style="width: 100%"
        >
          <el-table-column prop="date" :label="$t('interfacePlatform.index')" width="64" align="center" />
          <el-table-column prop="name" :label="$t('interfacePlatform.interfaceName')" />
          <el-table-column prop="name" :label="$t('interfacePlatform.interfaceNo')" />
          <el-table-column prop="name" :label="$t('interfacePlatform.interfaceVersion')" />
          <el-table-column
            prop="address"
            :label="$t('interfacePlatform.interfaceType')"
            width="156"
            align="center"
          >
            <el-tag>{{ $t('interfacePlatform.judicialInterface') }}</el-tag>
          </el-table-column>
          <el-table-column
            prop="date"
            :label="$t('common.createTime')"
            width="156"
            align="center"
          />
          <el-table-column :label="$t('common.operation')" align="center">
            <template slot-scope="{ row }">
              <el-button type="text">{{ $t('interfacePlatform.editInterface') }}</el-button>
              <el-button type="text">{{ $t('interfacePlatform.editParameter') }}</el-button>
              <el-button type="text" class="delete-button">{{ $t('common.delete') }}</el-button>
            </template>
          </el-table-column>
        </el-table>
        <Pagination
          :total="total"
          :current-page="apiParams.pageNum"
          :page-size="apiParams.pageSize"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { Pagination } from '@/Financing/components'
export default {
  name: 'interfaceCenter',
  components: {
    Pagination,
  },
  data() {
    return {
      options: [],
      supplierParams: {
        pageNum: 1,
        pageSize: 10,
        interfaceDataType: 0,
        sourceName: undefined,
      },
      apiParams: {
        pageNum: 1,
        pageSize: 10,
        interfaceName: undefined, //接口名称
        sourceNo: undefined, //供应商编号
      },
      total: 0,
      tableData: [],
    }
  },
  computed: {
    options() {
      return [
        { value: 0, label: this.$t('interfacePlatform.metadata') },
        { value: 1, label: this.$t('interfacePlatform.featureVariable') },
        { value: 2, label: this.$t('interfacePlatform.analysisTarget') },
      ]
    },
  },
  methods: {
    editMenu(item) {
      console.log('编辑', item)
    },
    deleteMenu(item) {
      this.$confirm(this.$t('interfacePlatform.deleteConfirmSimple'), this.$t('common.systemTip'), {
        confirmButtonText: this.$t('common.sure'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning',
      })
        .then(() => {
          this.$message({
            type: 'success',
            message: this.$t('interfacePlatform.deleteSuccess'),
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: this.$t('interfacePlatform.deleteCancelled'),
          })
        })
    },
    handleCurrentChange(num) {
      this.apiParams.pageNum = num
    },
  },
}
</script>

<style lang="less" scoped>
.scenario {
  height: calc(var(--bgvh) - 42px);
  padding: 20px;
  display: flex;

  &-menu {
    width: 280px;
    position: relative;
    border-radius: 6px;
    background: #ffffff;
    overflow-y: auto;

    .menu-title {
      top: 0;
      position: sticky;
      display: flex;
      align-items: center;
      padding: 20px;
      font-weight: 500;
      font-size: 14px;
      color: #ff8f1f;
      line-height: 26px;
      background: #ffffff;

      > span {
        margin-left: 4px;
      }

      ::v-deep .el-select {
        margin-left: 8px;
        width: 124px;

        .el-input__inner {
          height: 40px;
          background: rgba(0, 0, 0, 0.04);
          border-radius: 6px;
          border: none;
        }

        .el-select__caret {
          font-weight: bold;
          color: #1e2135;
        }
      }
    }

    .menu-content {
      padding: 0 20px 88px;

      .menu-item {
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 14px;
        font-weight: 400;
        font-size: 14px;
        color: rgba(0, 0, 0, 0.851);
        border-radius: 6px;
        background: linear-gradient(
          90deg,
          rgba(0, 0, 0, 0.0024) 0%,
          rgba(40, 136, 232, 0.06) 100%
        );

        & + .menu-item {
          margin-top: 10px;
        }

        &-action {
          display: flex;
          align-items: center;

          .edit-icon,
          .close-icon {
            cursor: pointer;
            position: relative;
            opacity: 0;
            z-index: -100;
            transition: opacity 0.3s;
            color: rgba(0, 0, 0, 0.6);
          }

          .close-icon {
            margin-left: 8px;
          }
        }

        &.isActive {
          color: #ffffff;
          background: var(--primary-color);

          .edit-icon,
          .close-icon {
            color: #ffffff;
          }
        }

        &:hover {
          .menu-item-action {
            .edit-icon,
            .close-icon {
              opacity: 1;
              z-index: 6;
            }
          }
        }
      }
    }

    .menu-footer {
      width: 280px;
      bottom: 20px;
      position: fixed;
      padding: 0 20px 20px;

      ::v-deep .el-button--primary {
        width: 100%;
        height: 48px;
        font-size: 14px;
        background: var(--primary-color);
        box-shadow: 0 4px 10px 0 rgba(40, 136, 232, 0.6);
        border-radius: 6px;
        border: 1px solid var(--primary-color);
      }
    }
  }

  &-wrapper {
    margin-left: 20px;
    width: calc(100% - 300px);

    &-search {
      display: flex;
      align-items: center;

      ::v-deep .el-input {
        width: 320px;

        &__inner {
          height: 40px;
          border-radius: 6px;
          border: none;
          font-size: 14px;
        }

        .el-input__prefix {
          > span {
            height: 100%;
            display: flex;
            align-items: center;
          }
        }
      }

      ::v-deep .el-button {
        margin-left: 16px;
        padding: 0 14px;
        height: 40px;
        background: var(--primary-color);
        border-radius: 6px;
        border: none;
      }
    }

    &-content {
      height: calc(100% - 60px);
      margin-top: 20px;
      padding: 20px;
      background: #ffffff;
      border-radius: 6px;

      ::v-deep .el-table {
        .el-table__cell {
          .el-tag {
            font-size: 12px;
            color: var(--primary-color);
            line-height: 20px;
            height: 20px;
            background: rgba(40, 136, 232, 0.1);
            border-radius: 2px;
            border: none;
          }

          .el-button {
            font-size: 12px;
            color: var(--primary-color);

            &.delete-button {
              color: #fa5151;
            }

            &:hover {
              opacity: 0.8;
            }
          }
        }
      }

      ::v-deep .gt-pagination {
        margin: 20px 0 0;
      }
    }
  }
}
</style>
