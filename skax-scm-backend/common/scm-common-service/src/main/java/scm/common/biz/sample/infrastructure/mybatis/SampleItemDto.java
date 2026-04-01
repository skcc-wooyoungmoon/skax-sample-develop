package scm.common.biz.sample.infrastructure.mybatis;

import lombok.Getter;
import lombok.Setter;
import scm.common.biz.sample.domain.SampleItem;

import java.time.LocalDateTime;

@Getter
@Setter
public class SampleItemDto {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public static SampleItemDto from(SampleItem item) {
        SampleItemDto dto = new SampleItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setCreatedDate(item.getCreatedDate());
        dto.setLastModifiedDate(item.getLastModifiedDate());
        return dto;
    }

    public SampleItem toModel() {
        return SampleItem.builder()
                .id(id)
                .name(name)
                .description(description)
                .createdDate(createdDate)
                .lastModifiedDate(lastModifiedDate)
                .build();
    }
}
