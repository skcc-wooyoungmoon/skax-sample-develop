package scm.common.biz.user.infrastructure.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserRepositoryMybatis {

    Optional<UserDto> findById(@Param("id") Long id);

    Optional<UserDto> findByEmail(@Param("email") String email);

    int save(UserDto user);

    int update(UserDto user);

    int updateStatus(UserDto user);

    List<UserDto> findAll();

    List<UserDto> findAllWithPageable(@Param("offset") long offset, @Param("pageSize") int pageSize);

    long countAll();

    List<UserDto> findAdminUsersWithPageable(@Param("userIds") List<Long> userIds, @Param("offset") long offset, @Param("pageSize") int pageSize);

    long countAdminUsers(@Param("userIds") List<Long> userIds);
}
