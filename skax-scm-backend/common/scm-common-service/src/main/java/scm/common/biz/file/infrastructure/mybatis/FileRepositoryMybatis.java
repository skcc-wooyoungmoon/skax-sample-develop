package scm.common.biz.file.infrastructure.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface FileRepositoryMybatis {

    int insert(FileDto row);

    Optional<FileDto> findById(@Param("id") long id);
}

