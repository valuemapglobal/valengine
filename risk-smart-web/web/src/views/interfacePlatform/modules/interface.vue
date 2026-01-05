<template>
  <div id="interface"></div>
</template>

<script>
export default {
  props: ['info'],
  data() {
    return {};

  },
  watch: {
    info: {
      handler(n, o) {
        if (n) {
          this.chart()
        }
      },
      immediate: true,

    }
  },
  mounted() {

    window.addEventListener("resize", this.resizeHandler);
  },
  methods: {
    chart() {
      let chartDom = document.getElementById("interface");
      let myChart = this.$echarts.init(chartDom, null, { renderer: "canvas" });
      const bgColors = [
        "rgba(255, 169, 101, 0.4)",
        "rgba(107, 165, 255, 0.4)",
        "rgba(162, 134, 254, 0.4)",
        "rgba(75, 210, 201, 0.4)",
      ];
      const colors = ["#425EE9", "#E36952", "#EB2F96"];
      const bgSeriesData = [];
      const seriesData = [];
      let calc = [this.info.successPercentage, this.info.failurePercentage]
      const values = [this.info.successPercentage, this.info.failurePercentage];
      const names = ["接口调用成功", "接口调用失败"];
      names.forEach((item, index) => {
        bgSeriesData.push({
          name: item,
          value: values[index],
          itemStyle: {
            color: bgColors[index],
          },
        });
        seriesData.push({
          name: item,
          calc: calc[index],
          value: values[index],
          itemStyle: {
            color: colors[index],
          },
        });
      });

      let option = {
        title: {
          text: "累计接口调用",
          left: "50%",
          top: "50%",
          itemGap: 10,
          textStyle: {
            color: "#5E5E5E",
            fontSize: "12",
            fontWeight: 400,
          },
          subtext: this.info.total+'次',
          subtextStyle: {
            color: "#000000",
            fontSize: "16",
            fontWeight: 600,
          },
        },
        series: [
          {
            type: "pie",
            radius: ["45%", "70%"],
            center: ["55%", "55%"],
            labelLine: {
              show: true,
              length2: 70,
              length: 30,
            },
            label: {
              show: true,
              position: "outside",
              distanceToLabelLine: -30,
              rich: {
                name0: {
                  color: "#333333",
                  fontSize: 14,
                  fontWeight: 400,
                  padding: [20, 0, 0, -50],
                },
                name1: {
                  color: "#333333",
                  fontSize: 14,
                  fontWeight: 400,
                  padding: [20, -50, 0, 0],
                },
                name2: {
                  color: "#333333",
                  fontSize: 14,
                  fontWeight: 400,
                  padding: [30, 0, 0, 0],
                },
                value0: {
                  color: "#666666",
                  fontWeight: 400,
                  fontSize: 29,
                  padding: [0, 30, -50, 0],
                },
                value1: {
                  color: "#666666",
                  fontWeight: 400,
                  fontSize: 13,
                  padding: [20, 0, 0, 12],
                },
                value2: {
                  color: "#666666",
                  fontWeight: 400,
                  fontSize: 13,
                  padding: [8, 0, 0, 12],
                  align: "left",
                },
              },
              // formatter: (params) => {
              //   const x = params.dataIndex;
              //   return (
              //     "{name" +
              //     x +
              //     "|" +
              //     params.data.name +
              //     "}" +
              //     "\n" +
              //     params.value +
              //     "%"
              //   );
              // },
              formatter: (params) => {
                console.log('params', params)
                const x = params.dataIndex;
                return (
                  "{name" +
                  x +
                  "|" +
                  params.data.name +
                  "}" +
                  "\n" +
                  params.value +
                  "%"
                );
              },
            },
            data: seriesData,
          },
        ],
      };

      myChart.setOption(option);
      this.myChart = myChart;
    },
    resizeHandler() {
      if (this.myChart) {
        this.myChart.resize();
      }
    },
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.resizeHandler);
  },
};
</script>

<style>
#interface {
  width: 100%;
  height: 660px;
}
</style>
