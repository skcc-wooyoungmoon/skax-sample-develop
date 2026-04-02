"use client";

import { useCallback, useEffect, useState } from "react";
import { sampleService } from "@/services/sample/sampleService";
import type { SampleItem } from "@/types/sample";
import styles from "./SampleCrudDashboard.module.css";

interface PanelState {
  items: SampleItem[];
  name: string;
  description: string;
  editId: number | null;
  loading: boolean;
  message: string;
  error: string;
}

const createInitialState = (): PanelState => ({
  items: [],
  name: "",
  description: "",
  editId: null,
  loading: false,
  message: "",
  error: "",
});

const toLocale = (value: string): string =>
  new Date(value).toLocaleString("ko-KR", {
    hour12: false,
  });

export default function SampleCrudDashboard() {
  const [state, setState] = useState<PanelState>(createInitialState);

  const loadItems = useCallback(async (): Promise<void> => {
    setState((prev) => ({ ...prev, loading: true, error: "", message: "" }));
    try {
      const items = await sampleService.findAll();
      setState((prev) => ({ ...prev, items, loading: false }));
    } catch (error) {
      const message = error instanceof Error ? error.message : "조회에 실패했습니다.";
      setState((prev) => ({ ...prev, loading: false, error: message }));
    }
  }, []);

  useEffect(() => {
    void loadItems();
  }, [loadItems]);

  const resetForm = useCallback((): void => {
    setState((prev) => ({
      ...prev,
      name: "",
      description: "",
      editId: null,
    }));
  }, []);

  const handleSubmit = useCallback(async (): Promise<void> => {
    const name = state.name.trim();
    const description = state.description.trim();

    if (!name) {
      setState((prev) => ({
        ...prev,
        error: "이름은 필수입니다.",
        message: "",
      }));
      return;
    }

    try {
      setState((prev) => ({ ...prev, loading: true, error: "", message: "" }));

      if (state.editId === null) {
        await sampleService.create({ name, description });
        setState((prev) => ({
          ...prev,
          loading: false,
          message: "등록이 완료되었습니다.",
        }));
      } else {
        await sampleService.update(state.editId, { name, description });
        setState((prev) => ({
          ...prev,
          loading: false,
          message: "수정이 완료되었습니다.",
        }));
      }

      resetForm();
      await loadItems();
    } catch (error) {
      const message = error instanceof Error ? error.message : "저장에 실패했습니다.";
      setState((prev) => ({ ...prev, loading: false, error: message }));
    }
  }, [loadItems, resetForm, state.description, state.editId, state.name]);

  const handleEdit = useCallback((item: SampleItem): void => {
    setState((prev) => ({
      ...prev,
      name: item.name,
      description: item.description ?? "",
      editId: item.id,
      message: "",
      error: "",
    }));
  }, []);

  const handleDelete = useCallback(async (id: number): Promise<void> => {
    try {
      setState((prev) => ({ ...prev, loading: true, error: "", message: "" }));
      await sampleService.remove(id);
      setState((prev) => ({
        ...prev,
        loading: false,
        message: "삭제가 완료되었습니다.",
      }));
      await loadItems();
    } catch (error) {
      const message = error instanceof Error ? error.message : "삭제에 실패했습니다.";
      setState((prev) => ({ ...prev, loading: false, error: message }));
    }
  }, [loadItems]);

  return (
    <div>
      <header className={styles.pageHeader}>
        <h1 className={styles.title}>SCM 샘플 CRUD 프론트엔드</h1>
        <p className={styles.description}>
          백엔드 샘플(MyBatis) API와 직접 연동되는 프론트엔드 샘플 화면입니다.
        </p>
      </header>

      <div className={styles.grid}>
        <section className={styles.panel}>
          <h2 className={styles.panelTitle}>MyBatis 샘플 항목</h2>

          <div className={styles.formRow}>
            <input
              className={styles.input}
              placeholder="이름 (필수)"
              value={state.name}
              onChange={(event) =>
                setState((prev) => ({ ...prev, name: event.target.value }))
              }
            />
            <input
              className={styles.input}
              placeholder="설명"
              value={state.description}
              onChange={(event) =>
                setState((prev) => ({ ...prev, description: event.target.value }))
              }
            />
          </div>

          <div className={styles.buttonRow}>
            <button
              type="button"
              className={`${styles.button} ${styles.primaryButton}`}
              disabled={state.loading}
              onClick={() => void handleSubmit()}
            >
              {state.editId === null ? "등록" : "수정"}
            </button>
            <button
              type="button"
              className={styles.button}
              disabled={state.loading}
              onClick={() => resetForm()}
            >
              초기화
            </button>
            <button
              type="button"
              className={styles.button}
              disabled={state.loading}
              onClick={() => void loadItems()}
            >
              새로고침
            </button>
          </div>

          {state.message ? <p className={styles.message}>{state.message}</p> : null}
          {state.error ? (
            <p className={`${styles.message} ${styles.error}`}>{state.error}</p>
          ) : null}

          <ul className={styles.list}>
            {state.items.map((item) => (
              <li key={item.id} className={styles.item}>
                <div className={styles.itemHeader}>
                  <p className={styles.itemName}>{item.name}</p>
                  <div className={styles.buttonRow}>
                    <button
                      type="button"
                      className={styles.button}
                      disabled={state.loading}
                      onClick={() => handleEdit(item)}
                    >
                      편집
                    </button>
                    <button
                      type="button"
                      className={styles.button}
                      disabled={state.loading}
                      onClick={() => void handleDelete(item.id)}
                    >
                      삭제
                    </button>
                  </div>
                </div>
                <p className={styles.itemDesc}>{item.description || "-"}</p>
                <p className={styles.itemMeta}>
                  생성일: {toLocale(item.createdDate)} / 수정일: {toLocale(item.lastModifiedDate)}
                </p>
              </li>
            ))}
          </ul>
        </section>
      </div>
    </div>
  );
}
