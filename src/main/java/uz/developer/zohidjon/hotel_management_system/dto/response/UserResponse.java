package uz.developer.zohidjon.hotel_management_system.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String avatarUrl;
    private String role;
    private boolean enabled;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
