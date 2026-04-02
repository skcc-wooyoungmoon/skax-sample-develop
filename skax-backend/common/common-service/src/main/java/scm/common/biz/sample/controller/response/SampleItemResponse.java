package scm.common.biz.sample.controller.response;

import lombok.Builder;
import lombok.Getter;
import scm.common.biz.sample.domain.SampleItem;

import java.time.LocalDateTime;

@Getter
@Builder
public class SampleItemResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public static SampleItemResponse from(SampleItem item) {
        return SampleItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .createdDate(item.getCreatedDate())
                .lastModifiedDate(item.getLastModifiedDate())
                .build();
    }
}
