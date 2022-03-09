export enum ErrorConstants {
  VALIDATION_ERROR = "VALIDATION_ERROR",
  UNIQUE_ERROR = "UNIQUE_ERROR",
  BAD_REQUEST_ERROR = "BAD_REQUEST_ERROR",
  ACCESS_DENIED_ERROR = "ACCESS_DENIED_ERROR",
  RESOURCE_ALREADY_EXISTS_ERROR = "RESOURCE_ALREADY_EXISTS_ERROR",
  EXISTING_DEPENDENCY_ERROR = "EXISTING_DEPENDENCY_ERROR",
  NOT_FOUND_ERROR = "NOT_FOUND_ERROR",
  WRONG_TIME_EXCEPTION = "WRONG_TIME_EXCEPTION",
  NOT_DELETABLE_ERROR = "NOT_DELETABLE_ERROR",
}

export interface ErrorResponse {
  key: string;
  messages: ErrorMessage[];
}

export interface ErrorMessage {
  message: string;
  fieldName: string;
}

export const isError = (err: ErrorResponse): boolean => {
  return Object.keys(ErrorConstants).includes(err.key);
};

export const isErrorOfType = (
  type: ErrorConstants,
  err: ErrorResponse | null
): boolean => {
  return err?.key === type;
};
