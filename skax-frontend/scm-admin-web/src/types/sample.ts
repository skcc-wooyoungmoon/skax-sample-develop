export interface SampleItem {
  id: number;
  name: string;
  description: string | null;
  createdDate: string;
  lastModifiedDate: string;
}

export interface SampleItemRequest {
  name: string;
  description?: string;
}

// backend sample API는 MyBatis 기반만 사용합니다.
