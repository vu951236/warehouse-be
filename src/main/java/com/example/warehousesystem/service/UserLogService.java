package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.UserLogRequest;
import com.example.warehousesystem.dto.response.UserLogResponse;
import com.example.warehousesystem.entity.User;
import com.example.warehousesystem.entity.UserLog;
import com.example.warehousesystem.repository.UserLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserLogService {

    private final UserLogRepository userLogRepository;

    public List<UserLogResponse> getUserLogs(UserLogRequest request) {
        LocalDateTime startOfDay = request.getDate().atStartOfDay();
        LocalDateTime endOfDay = request.getDate().atTime(23, 59, 59);

        List<UserLog> logs = userLogRepository.findLogsByWarehouseAndDateRange(
                request.getWarehouseId(),
                startOfDay,
                endOfDay
        );

        return logs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    public List<UserLogResponse> getAllUserLogs(Integer warehouseId) {
        List<UserLog> logs = userLogRepository.findByWarehouse(warehouseId);

        return logs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void saveLog(User user, String action, String targetTable, Integer targetId, String ipAddress) {
        UserLog log = UserLog.builder()
                .user(user)
                .action(action)
                .targetTable(targetTable)
                .targetId(targetId)
                .timestamp(LocalDateTime.now())
                .ipAddress(ipAddress)
                .note(null)
                .build();
        userLogRepository.save(log);
    }

    private UserLogResponse mapToResponse(UserLog log) {
        return UserLogResponse.builder()
                .userName(log.getUser().getFullName() != null && !log.getUser().getFullName().isBlank()
                        ? log.getUser().getFullName()
                        : log.getUser().getUsername())
                .action(log.getAction())
                .targetTable(log.getTargetTable())
                .timestamp(log.getTimestamp().toString())
                .build();
    }
}
