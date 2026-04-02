package scm.common.biz.export.controller.response;

import lombok.Builder;
import lombok.Getter;
import scm.common.biz.user.domain.User;
import scm.common.biz.user.domain.UserStatus;
import scm.common.biz.util.FileExportColumn;

import java.time.LocalDateTime;

/**
 * 사용자 목록 CSV/Excel보내기용 DTO. 컬럼 정의는 {@link FileExportColumn}.
 */
@Getter
@Builder
public class UserExportResponse {
    @FileExportColumn(value = "ID", order = 0)
    private Long id;

    @FileExportColumn(value = "이메일", order = 1)
    private String email;

    @FileExportColumn(value = "사용자명", order = 2)
    private String username;

    @FileExportColumn(value = "상태", order = 3)
    private UserStatus status;

    @FileExportColumn(value = "생성일시", order = 4, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @FileExportColumn(value = "수정일시", order = 5, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastModifiedDate;

    public static UserExportResponse fromUser(User user) {
        return UserExportResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .build();
    }
}
