<template>
  <v-row justify="center">
    <v-col justify="center" align="center" class="sorry my-4">
      <h1 class="sorry-title">
        {{ title }}
      </h1>
      <img :src="imageUrl" width="30%" height="auto" class="mb-4" />
      <p v-if="description" class="desc" v-html="description"></p>
      <v-row justify="center">
        <div class="back-button">
          <router-link :to="ctaUrl" tag="a">{{ ctaLabel }}</router-link>
        </div>
      </v-row>
    </v-col>
  </v-row>
</template>

<script lang="ts">
import { Prop, Vue, Component } from "vue-property-decorator";

@Component({ name: "SorryComponent" })
export default class SorryComponent extends Vue {
  @Prop({ required: true })
  private readonly title!: string;

  @Prop({ required: true })
  private readonly imageUrl!: string;

  @Prop({ required: false })
  private readonly description: string | undefined;

  @Prop({ required: false })
  private readonly ctaUrl: string | undefined;

  @Prop({ required: false })
  private readonly ctaLabel: string | undefined;
}
</script>

<style lang="scss">
.sorry {
  .sorry-title {
    font-size: 2rem;
  }
  .desc {
    text-align: center;
  }
  .back-button {
    margin-top: 1rem;
    padding: 0.5rem;
    color: #1976d2;
  }
}
</style>
