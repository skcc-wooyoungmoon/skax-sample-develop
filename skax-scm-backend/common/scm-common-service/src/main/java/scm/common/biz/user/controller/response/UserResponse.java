package scm.common.biz.user.controller.response;

import lombok.Builder;
import lombok.Getter;
import scm.common.biz.user.domain.User;
import scm.common.biz.user.domain.UserStatus;

@Getter
@Builder
public class UserResponse {

    // 응답값으로 필요한 정보만 세팅
    private Long id;
    private String email;
    private String username;
    private UserStatus status;


    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .build();
    }
}
