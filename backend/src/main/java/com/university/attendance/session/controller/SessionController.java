package com.university.attendance.session.controller;

import com.university.attendance.common.ApiResponse;
import com.university.attendance.session.dto.SessionResponse;
import com.university.attendance.session.dto.StartSessionRequest;
import com.university.attendance.session.dto.TokenResponse;
import com.university.attendance.session.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<SessionResponse>> startSession(
            @Valid @RequestBody StartSessionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        SessionResponse response = sessionService.startSession(request.timetableId(), userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/{id}/token")
    public ResponseEntity<ApiResponse<TokenResponse>> getActiveToken(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        TokenResponse response = sessionService.getActiveToken(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<ApiResponse<SessionResponse>> endSession(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        SessionResponse response = sessionService.endSession(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
