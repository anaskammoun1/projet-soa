package com.soutenance.security.audit;

import com.soutenance.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(String eventType, String username, Role role, boolean success, String details) {
        auditLogRepository.save(AuditLog.builder()
                .eventType(eventType)
                .username(username)
                .role(role)
                .success(success)
                .details(details)
                .build());
    }
}
