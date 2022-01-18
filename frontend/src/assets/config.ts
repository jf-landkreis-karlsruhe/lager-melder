// export const BASE_URL = "http://10.100.253.43:8081";
export const BASE_URL = "http://localhost:8080";
export const CODE_LENGTH = 8;

/**
 *
 * @param code attendee or event code to test
 * @returns if code matches number of characters and exists of only letters and numbers
 */
export const isValidCode = (code: string) => {
  return new RegExp(/^[A-Za-z0-9]{8}$/g).test(code);
};

// TODO: only for testing
export const isValidTestCode = (code: string) => {
  return new RegExp(/^[A-Za-z0-9-]{8}$/g).test(code);
};
