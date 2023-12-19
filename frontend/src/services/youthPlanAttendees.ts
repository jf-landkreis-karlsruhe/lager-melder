import { getData } from "../helper/fetch";
import { withAuthenticationHeader } from "./authentication";

export interface YouthPlanDistribution {
  youthCount: number;
  leaderCount: number;
}
export const getYouthPlanDistribution = (): Promise<YouthPlanDistribution> => {
  return getData<YouthPlanDistribution>(
    `youth-plan-attendees/distribution`,
    withAuthenticationHeader()
  );
};
