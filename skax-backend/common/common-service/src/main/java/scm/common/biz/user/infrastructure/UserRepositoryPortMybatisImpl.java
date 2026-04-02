package scm.common.biz.user.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import scm.common.biz.user.domain.User;
import scm.common.biz.user.infrastructure.mybatis.UserDto;
import scm.common.biz.user.infrastructure.mybatis.UserRepositoryMybatis;
import scm.common.biz.user.service.port.UserRepositoryPort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryPortMybatisImpl implements UserRepositoryPort {

    private final UserRepositoryMybatis userRepositoryMybatis;

    @Override
    public Optional<User> findById(Long id) {
        return userRepositoryMybatis.findById(id).map(UserDto::toModel);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepositoryMybatis.findByEmail(email).map(UserDto::toModel);
    }

    @Override
    @Transactional
    public User save(User user) {
        UserDto dto = UserDto.from(user);
        LocalDateTime now = LocalDateTime.now();
        if (dto.getCreatedDate() == null) {
            dto.setCreatedDate(now);
        }
        dto.setLastModifiedDate(now);

        if (user.getId() == null) {
            int n = userRepositoryMybatis.save(dto);
            if (n == 0) {
                return null;
            }
        } else {
            userRepositoryMybatis.update(dto);
        }
        return userRepositoryMybatis.findById(dto.getId()).map(UserDto::toModel).orElse(null);
    }

    @Override
    public List<User> findAll() {
        return userRepositoryMybatis.findAll().stream().map(UserDto::toModel).toList();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        List<UserDto> userDtos = userRepositoryMybatis.findAllWithPageable(
                pageable.getOffset(),
                pageable.getPageSize()
        );
        long totalCount = userRepositoryMybatis.countAll();
        List<User> users = userDtos.stream().map(UserDto::toModel).toList();
        return new PageImpl<>(users, pageable, totalCount);
    }

    @Override
    public Page<User> findAdminUsers(Pageable pageable, List<Long> userIds) {
        // role/userrole 샘플 패키지를 제거했으므로 admin 필터는 비활성화
        return findAll(pageable);
    }

    @Override
    @Transactional
    public User updateStatus(User user) {
        UserDto dto = UserDto.from(user);
        dto.setLastModifiedDate(LocalDateTime.now());
        userRepositoryMybatis.updateStatus(dto);
        return userRepositoryMybatis.findByEmail(user.getEmail()).map(UserDto::toModel)
                .orElse(null);
    }
}
