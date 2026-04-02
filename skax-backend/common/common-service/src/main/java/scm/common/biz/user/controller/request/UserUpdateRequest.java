package scm.common.biz.user.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import scm.common.biz.user.domain.User;
import scm.common.biz.user.domain.UserRole;
import scm.common.biz.user.domain.UserStatus;

@Getter
@Builder
public class UserUpdateRequest {

    @NotNull(message = "{javax.validation.constraints.NotNull.message}")
    private final String email;
    private final String username;
    private final String password;
    private final UserStatus status;

    public User toModel() {
        return User.builder()
                .email(email)
                .username(username)
                .password(password)
                .status(status)
                .build();
    }
}
