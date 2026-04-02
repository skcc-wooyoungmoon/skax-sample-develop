package scm.common.biz.file.infrastructure.mybatis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import scm.common.biz.file.domain.FileModel;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {

    private Long id;
    private String orgName;
    private String encName;
    private String dirPath;
    private long size;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public static FileDto from(FileModel model) {
        return FileDto.builder()
                .id(model.getId() == 0 ? null : model.getId())
                .orgName(model.getOrgName())
                .encName(model.getEncName())
                .dirPath(model.getDirPath())
                .size(model.getSize())
                .createdDate(model.getCreatedDate())
                .lastModifiedDate(model.getLastModifiedDate())
                .build();
    }

    public FileModel toModel() {
        return FileModel.builder()
                .id(id != null ? id : 0L)
                .orgName(orgName)
                .encName(encName)
                .dirPath(dirPath)
                .size(size)
                .createdDate(createdDate)
                .lastModifiedDate(lastModifiedDate)
                .build();
    }
}
