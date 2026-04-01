package scm.common.biz.user.infrastructure.mybatis;

import lombok.*;
import scm.common.biz.user.domain.User;
import scm.common.biz.user.domain.UserStatus;

import java.time.LocalDateTime;

/**
 * MyBatis User Dto
 */

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private String password;
    private String username;
    private UserStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;


    public static UserDto from (User user){
        if (user == null) {
            return null;
        }
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .username(user.getUsername())
                .status(user.getStatus())
                .createdDate(user.getCreatedDate())
                .lastModifiedDate(user.getLastModifiedDate())
                .build();
        return userDto;
    }

    public User toModel() {
        User model = User.builder()
                .id(id)
                .email(email)
                .password(password)
                .username(username)
                .status(status)
                .createdDate(createdDate)
                .lastModifiedDate(lastModifiedDate)
                .build();
        return model;
    }

}
