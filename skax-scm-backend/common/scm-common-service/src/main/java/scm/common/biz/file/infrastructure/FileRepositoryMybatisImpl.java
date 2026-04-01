package scm.common.biz.file.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import scm.common.biz.file.domain.FileModel;
import scm.common.biz.file.infrastructure.mybatis.FileDto;
import scm.common.biz.file.infrastructure.mybatis.FileRepositoryMybatis;
import scm.common.biz.file.service.port.FileRepositoryPort;

@Repository
@RequiredArgsConstructor
public class FileRepositoryMybatisImpl implements FileRepositoryPort {

    private final FileRepositoryMybatis fileRepositoryMybatis;

    @Override
    public FileModel save(FileModel fileModel) {
        FileDto row = FileDto.from(fileModel);
        fileRepositoryMybatis.insert(row);
        return fileRepositoryMybatis.findById(row.getId()).map(FileDto::toModel).orElseThrow();
    }

    @Override
    public FileModel findById(Long id) {
        if (id == null) {
            return null;
        }
        return fileRepositoryMybatis.findById(id).map(FileDto::toModel).orElse(null);
    }
}
