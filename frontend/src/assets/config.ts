export const BASE_URL = process.env.VUE_APP_BACKEND_URL || `${window.location.origin}/api`;
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
