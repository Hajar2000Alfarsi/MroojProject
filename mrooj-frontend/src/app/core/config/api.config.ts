/**
 * Central API configuration.
 * For local development Angular runs on 4200 and Spring Boot on 8080.
 * Change only this file (or serve through a reverse proxy) for deployment.
 */
const host = window.location.hostname;

export const SERVER_BASE_URL = `http://${host}:8080`;
export const API_BASE_URL = `${SERVER_BASE_URL}/api`;
