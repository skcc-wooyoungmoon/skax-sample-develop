package scm.common.biz.sample.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import scm.common.app.dto.ApiResponse;
import scm.common.biz.sample.controller.port.SampleServicePort;
import scm.common.biz.sample.controller.request.SampleItemRequest;
import scm.common.biz.sample.controller.response.SampleItemResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/common/sample/mybatis-items")
public class SampleRestController {

    private final SampleServicePort sampleServicePort;

    @GetMapping
    public ApiResponse<List<SampleItemResponse>> findAll() {
        List<SampleItemResponse> result = sampleServicePort.findAll().stream()
                .map(SampleItemResponse::from)
                .toList();
        return ApiResponse.ok(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<SampleItemResponse> findById(@PathVariable Long id) {
        return ApiResponse.ok(SampleItemResponse.from(sampleServicePort.findById(id)));
    }

    @PostMapping
    public ApiResponse<SampleItemResponse> create(@Valid @RequestBody SampleItemRequest request) {
        return ApiResponse.ok(SampleItemResponse.from(sampleServicePort.create(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<SampleItemResponse> update(@PathVariable Long id,
            @Valid @RequestBody SampleItemRequest request) {
        return ApiResponse.ok(SampleItemResponse.from(sampleServicePort.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        sampleServicePort.delete(id);
        return ApiResponse.ok(null);
    }
}
