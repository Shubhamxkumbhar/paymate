package com.paymate.paymate.module.auth.dto;

import com.paymate.paymate.module.auth.domain.KycStatus;
import com.paymate.paymate.module.auth.domain.Role;
import com.paymate.paymate.module.auth.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO representing a user's public profile.
 *
 * <p>Carefully omits sensitive fields from {@link User}:
 * passwordHash is never included in any response.
 * Only fields safe to return to the authenticated user are included.</p>
 *
 * @author Shubham Kumbhar
 */
@Getter
@Builder
public class UserResponse {

    private UUID id;
    private String email;
    private String phone;
    private String fullName;
    private Role role;
    private KycStatus kycStatus;
    private boolean isActive;
    private LocalDateTime createdAt;

    /**
     * Factory method to create a UserResponse from a User entity.
     *
     * <p>This is the only place where User → UserResponse conversion
     * happens, keeping mapping logic in one place.</p>
     *
     * @param user the entity to convert
     * @return a safe DTO representation of the user
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .fullName(user.getFullName())
                .role(user.getRole())
                .kycStatus(user.getKycStatus())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}