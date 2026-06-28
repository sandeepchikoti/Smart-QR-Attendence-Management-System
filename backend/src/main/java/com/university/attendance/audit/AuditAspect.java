package com.university.attendance.audit;

import com.university.attendance.audit.entity.AuditLog;
import com.university.attendance.audit.repository.AuditLogRepository;
import com.university.attendance.user.entity.User;
import com.university.attendance.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @AfterReturning(value = "@annotation(audit)", returning = "result")
    public void logAudit(JoinPoint joinPoint, Audit audit, Object result) {
        String action = audit.action();
        String username = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "anonymousUser";

        User actor = userRepository.findByEmail(username).orElse(null);

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String ipAddress = "0.0.0.0";
        String userAgent = "Unknown";
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            ipAddress = getClientIp(request);
            userAgent = request.getHeader("User-Agent");
        }

        String details = "Method: " + joinPoint.getSignature().toShortString() +
                ", Args: " + Arrays.toString(joinPoint.getArgs());

        AuditLog logRecord = AuditLog.builder()
                .actor(actor)
                .action(action)
                .details(details)
                .ipAddress(ipAddress)
                .userAgent(userAgent != null ? userAgent : "Unknown")
                .build();

        auditLogRepository.save(logRecord);
        log.info("Audit log saved: {} by {}", action, username);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isBlank()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
