package com.example.warehousesystem.Annotation;

import com.example.warehousesystem.entity.User;
import com.example.warehousesystem.repository.UserRepository;
import com.example.warehousesystem.service.UserLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor
public class SystemLogAspect {

    private final UserLogService userLogService;
    private final HttpServletRequest request;
    private final UserRepository userRepository;

    @Around("@annotation(systemLog)")
    public Object logAction(ProceedingJoinPoint joinPoint, SystemLog systemLog) throws Throwable {
        // Thực thi method gốc
        Object result = joinPoint.proceed();

        // Lấy thông tin user từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            currentUser = userRepository.findByUsername(username).orElse(null);
        }

        // Lấy IP
        String ipAddress = extractClientIp();

        // Lấy targetId từ tham số (nếu có)
        Integer targetId = null;
        for (Object arg : joinPoint.getArgs()) {
            try {
                targetId = (Integer) arg.getClass().getMethod("getWarehouseId").invoke(arg);
                if (targetId != null) break;
            } catch (Exception ignored) {}
        }

        // Nếu vẫn chưa có, thử lấy từ result (trường hợp entity có getId)
        if (targetId == null && result != null) {
            try {
                targetId = (Integer) result.getClass().getMethod("getId").invoke(result);
            } catch (Exception ignored) {}
        }

        // Ghi log
        if (currentUser != null) {
            userLogService.saveLog(
                    currentUser,
                    systemLog.action(),
                    systemLog.targetTable(),
                    targetId,
                    ipAddress
            );
        }

        return result;
    }

    private String extractClientIp() {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
