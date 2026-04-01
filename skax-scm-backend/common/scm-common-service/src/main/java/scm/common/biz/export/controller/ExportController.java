package scm.common.biz.export.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scm.common.biz.export.controller.response.UserExportResponse;
import scm.common.biz.user.domain.User;
import scm.common.biz.user.service.UserService;
import scm.common.biz.util.FileExportUtil;

import java.io.IOException;
import java.util.List;

/**
 * 사용자 데이터 CSV/Excel보내기 패턴 예시 API. {@link FileExportUtil} 연동.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/common/export")
public class ExportController {

    private final FileExportUtil fileExportUtil;
    private final UserService userService;

    @GetMapping("/users/csv")
    public ResponseEntity<Resource> exportUsersCsv() throws IOException {
        List<User> userList = userService.findAllUsers();
        List<UserExportResponse> users = userList.stream()
                .map(UserExportResponse::fromUser)
                .toList();
        Resource resource = fileExportUtil.exportToCsv(users, UserExportResponse.class);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @GetMapping("/users/excel")
    public ResponseEntity<Resource> exportUsersExcel() throws IOException {
        List<User> userList = userService.findAllUsers();
        List<UserExportResponse> users = userList.stream()
                .map(UserExportResponse::fromUser)
                .toList();
        Resource resource = fileExportUtil.exportToExcel(users, UserExportResponse.class);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }
}
