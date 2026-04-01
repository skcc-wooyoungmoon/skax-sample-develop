import { http } from "@/services/http/client";
import type { ApiResponse } from "@/types/common/api";

interface SignUpRequest {
  email: string;
  password: string;
  username: string;
}

interface LoginRequest {
  email: string;
  password: string;
}

interface TokenData {
  accessToken: string;
}

const unwrap = <T>(response: ApiResponse<T>): T => {
  if (!response.success) {
    throw new Error(response.error?.message ?? "API 요청이 실패했습니다.");
  }
  return response.data;
};

export const authService = {
  async signUp(request: SignUpRequest): Promise<void> {
    const { data } = await http.post<ApiResponse<unknown>>("/api/v1/common/users/signup", request);
    unwrap(data);
  },

  async login(request: LoginRequest): Promise<string> {
    const { data } = await http.post<ApiResponse<TokenData>>(
      "/api/v1/common/users/authenticate",
      request,
    );
    return unwrap(data).accessToken;
  },
};
