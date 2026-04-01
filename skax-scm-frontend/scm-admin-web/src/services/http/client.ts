import axios, { AxiosError } from "axios";
import { tokenStorage } from "@/services/auth/tokenStorage";
import type { ApiResponse } from "@/types/common/api";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8081";

export const http = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

http.interceptors.request.use((config) => {
  const token = tokenStorage.getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

http.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiResponse<unknown>>) => {
    const status = error.response?.status;
    if (status === 401 && typeof window !== "undefined") {
      window.dispatchEvent(new Event("auth:unauthorized"));
    }

    const message =
      error.response?.data?.error?.message ??
      error.message ??
      "요청 처리 중 오류가 발생했습니다.";

    return Promise.reject(new Error(message));
  },
);
