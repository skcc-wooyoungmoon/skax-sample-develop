package scm.common.biz.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CSV/Excel 컬럼 매핑을 위한 어노테이션
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FileExportColumn {
    /**
     * CSV 헤더명 또는 Excel 컬럼명
     */
    String value();
    
    /**
     * 컬럼 순서 (0부터 시작)
     */
    int order() default -1;
    
    /**
     * 필수 여부
     */
    boolean required() default false;
    
    /**
     * 날짜 포맷 (날짜 타입일 경우)
     */
    String dateFormat() default "yyyy-MM-dd";
}
