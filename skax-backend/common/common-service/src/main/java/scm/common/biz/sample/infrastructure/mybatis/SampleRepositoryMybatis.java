package scm.common.biz.sample.infrastructure.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SampleRepositoryMybatis {

    List<SampleItemDto> findAll();

    Optional<SampleItemDto> findById(Long id);

    void insert(SampleItemDto dto);

    int update(@Param("id") Long id, @Param("name") String name, @Param("description") String description);

    int delete(Long id);
}
