package com.example.hanaro.controller;

import com.example.hanaro.config.swagger.response.Api401Error;
import com.example.hanaro.config.swagger.response.Api403Error;
import com.example.hanaro.config.swagger.response.Api404Error;
import com.example.hanaro.config.swagger.response.Api500ErrorGroup;
import com.example.hanaro.dto.UserResponseDto;
import com.example.hanaro.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - User", description = "관리자 회원 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/user")
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    private final UserService userService;

    @Operation(summary = "회원 목록 조회", description = "모든 회원의 목록을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "회원 목록 조회 성공")
    @Api401Error
    @Api403Error
    @Api500ErrorGroup
    @GetMapping("/list")
    public ResponseEntity<List<UserResponseDto>> getUserList() {
        List<UserResponseDto> userList = userService.getUserList();
        return ResponseEntity.ok(userList);
    }

    @Operation(summary = "회원 삭제", description = "특정 회원을 ID로 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "회원 삭제 성공")
    @Api401Error
    @Api403Error
    @Api404Error
    @Api500ErrorGroup
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}