package scm.common.biz.common.infrastructure.spy;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

import java.util.Locale;

/**
 * p6spy 로그 포맷 (Hibernate 의존 없이 JDBC/MyBatis용).
 */
public class CustomP6spySqlFormat implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        sql = formatSql(category, sql);
        return sql + ";\nExecution Time: " + elapsed + " ms";
    }

    private String formatSql(String category, String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return sql;
        }
        if (Category.STATEMENT.getName().equals(category)) {
            String trimmed = sql.trim().toLowerCase(Locale.ROOT);
            if (trimmed.startsWith("create") || trimmed.startsWith("alter") || trimmed.startsWith("comment")) {
                return sql;
            }
        }
        return sql;
    }
}
