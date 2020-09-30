
<script lang="ts">
// eslint-disable-next-line no-unused-vars
import { PropType } from "vue";
import { Bar } from "vue-chartjs";

interface ChartData {
  name: string;
  count: number;
}

export default {
  extends: Bar,
  name: "BarChart",
  props: {
    rawData: Array as PropType<ChartData[]>,
    chartLabel: String
  },
  data: () => ({
    chartdata: {
      labels: ["One", "Two", "Three", "Four"],
      datasets: [
        {
          label: this.chartLabel,
          data: [5, 7, 8, 10],
          backgroundColor: "#f87979"
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        yAxes: [
          {
            ticks: {
              beginAtZero: true
            }
          }
        ]
      }
    }
  }),

  mounted() {
    this.renderChart(this.chartdata, this.options);
  }
};
</script>
