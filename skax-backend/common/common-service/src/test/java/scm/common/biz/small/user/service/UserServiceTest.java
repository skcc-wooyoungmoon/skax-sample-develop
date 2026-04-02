package scm.common.biz.small.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import scm.common.app.exception.CustomException;
import scm.common.app.util.JwtUtil;
import scm.common.biz.mock.FakePasswordEncoder;
import scm.common.biz.mock.FakeTokenAllowlistService;
import scm.common.biz.mock.FakeUserRepositoryPort;
import scm.common.biz.user.controller.request.UserCreateRequest;
import scm.common.biz.user.domain.User;
import scm.common.biz.user.domain.UserStatus;
import scm.common.biz.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {

    private UserService userService;
    private final int TOTAL_USER_COUNT = 45;
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        FakeUserRepositoryPort fakeUserRepository = new FakeUserRepositoryPort();
        FakePasswordEncoder fakePasswordEncoder = new FakePasswordEncoder();
        jwtUtil = new JwtUtil("skax-secret-key-skax-secret-key-skax-secret-key", 3600000);

        this.userService = new UserService(
                fakeUserRepository, fakePasswordEncoder, jwtUtil, new FakeTokenAllowlistService());

        for (int i = 1; i <= TOTAL_USER_COUNT; i++) {
            fakeUserRepository.save(User.builder()
                    .id((long) (i))
                    .email("test" + i + "@sk.com")
                    .password(fakePasswordEncoder.encode("password"))
                    .username("홍길동" + i)
                    .status(UserStatus.ACTIVE)
                    .build());
        }
    }

    @Test
    void userCreate_를_이용해_생성한다() throws Exception {
        // given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username("홍길동")
                .email("email@sk.com")
                .password("password")
                .build();

        // when
        User result = userService.signUp(userCreateRequest.toModel());

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(result.getPassword()).isEqualTo("ENC_password");
    }

    // @Test
    void 이미존재하는_사용자_생성시_에러발생() throws Exception {
        // given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username("홍길동")
                .email("test1@sk.com")
                .password("password")
                .build();

        // when & then
        assertThrows(CustomException.class, () -> userService.signUp(userCreateRequest.toModel()));
        // assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EXIST_ELEMENT);
    }

    // @Test
    void 잘못된_정보로_로그인_시도() throws Exception {
        // given
        String email = "test1@sk.com";
        String rawPassword = "passwordXXX";

        // when
        assertThrows(CustomException.class, () -> userService.authenticate(email, rawPassword));
        // assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_MATCHED_PASSWORD);

    }

    // @Test
    void 존재하지_않은_이메일로_로그인_시도() throws Exception {
        // given
        String email = "empty@sk.com";
        String rawPassword = "password";

        // when
        assertThrows(CustomException.class, () -> userService.authenticate(email, rawPassword));
        // assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_ELEMENT);
    }

    @Test
    void 전체_사용자_조회() throws Exception {
        // given

        // when
        List<User> allUsers = userService.findAllUsers();

        // then
        assertThat(allUsers.size()).isEqualTo(TOTAL_USER_COUNT);

    }

    @Test
    void 페이지정보를_이용한_사용자_조회() throws Exception {
        // given
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(0, pageSize);

        // when
        Page<User> users = userService.findAll(pageRequest);

        // then
        assertThat(users.getTotalElements()).isEqualTo(TOTAL_USER_COUNT);
        assertThat(users.getContent().size()).isEqualTo(pageSize);
        assertThat(users.getTotalPages()).isEqualTo(
                TOTAL_USER_COUNT % pageSize == 0 ? TOTAL_USER_COUNT / pageSize : TOTAL_USER_COUNT / pageSize + 1);

    }

    @Test
    void ID로_사용자_조회() throws Exception {
        // given
        Long id = 1L;

        // when
        User user = userService.getById(id);

        // then
        assertThat(user.getId()).isEqualTo(id);

    }

}