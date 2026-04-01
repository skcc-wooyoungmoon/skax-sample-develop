package scm.common.biz.file.service.port;

import scm.common.biz.file.domain.FileModel;

public interface FileRepositoryPort {
    FileModel save(FileModel fileModel);
    FileModel findById(Long id);
}
