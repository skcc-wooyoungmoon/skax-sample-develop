package scm.common.biz.sample.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SampleItemRequest {

    @NotBlank
    private String name;

    private String description;
}
