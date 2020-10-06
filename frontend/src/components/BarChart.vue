
<script lang="ts">
// eslint-disable-next-line no-unused-vars
import Vue, { PropType } from "vue";
import { Bar } from "vue-chartjs";

interface ChartData {
  name: string;
  count: number;
}

export default Vue.extend({
  extends: Bar,
  name: "BarChart",
  props: {
    rawData: Array as PropType<ChartData[]>,
    chartLabel: String,
    update: Number
  },
  data: function() {
    return {
      chartdata: {
        labels: this.rawData.map(data => data.name), // ["One", "Two", "Three", "Four"],
        datasets: [
          {
            label: this.chartLabel,
            data: this.rawData.map(data => data.count), // [5, 7, 8, 10],
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
    };
  },
  mounted: function() {
    this.renderChart(this.chartdata, this.options);
  }
});
</script>
