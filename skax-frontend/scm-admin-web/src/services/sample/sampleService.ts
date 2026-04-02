import { http } from "@/services/http/client";
import type { ApiResponse } from "@/types/common/api";
import type { SampleItem, SampleItemRequest } from "@/types/sample";

const basePath = "/api/v1/common/sample/mybatis-items";

const unwrap = <T>(response: ApiResponse<T>): T => {
  if (!response.success) {
    throw new Error(response.error?.message ?? "API 요청이 실패했습니다.");
  }
  return response.data;
};

export const sampleService = {
  async findAll(): Promise<SampleItem[]> {
    const { data } = await http.get<ApiResponse<SampleItem[]>>(basePath);
    return unwrap(data);
  },

  async create(request: SampleItemRequest): Promise<SampleItem> {
    const { data } = await http.post<ApiResponse<SampleItem>>(basePath, request);
    return unwrap(data);
  },

  async update(id: number, request: SampleItemRequest): Promise<SampleItem> {
    const { data } = await http.put<ApiResponse<SampleItem>>(
      `${basePath}/${id}`,
      request,
    );
    return unwrap(data);
  },

  async remove(id: number): Promise<void> {
    await http.delete<ApiResponse<null>>(`${basePath}/${id}`);
  },
};
