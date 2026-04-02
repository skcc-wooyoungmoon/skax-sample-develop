package scm.common.biz.context.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import scm.common.app.context.ContextStorageService;
import scm.common.app.dto.ApiResponse;

@RestController
@RequestMapping("/api/v1/common/context")
@RequiredArgsConstructor
public class ContextController {

    private final ContextStorageService ctxService;

    @PostMapping(value = "/set/{key}")
    public String setContext(@PathVariable String key, @RequestBody Object data) {
        ctxService.set(key, data);
        return "ctx-set-ok";
    }

    @GetMapping(value = "/get/{key}")
    public ApiResponse<?> getContext(@PathVariable String key) {
        return ApiResponse.ok(ctxService.get(key, Object.class));
    }
}
