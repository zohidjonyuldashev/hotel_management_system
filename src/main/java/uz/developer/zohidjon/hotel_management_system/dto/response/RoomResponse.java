package uz.developer.zohidjon.hotel_management_system.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {
    private String id;
    private String roomType;
    private BigDecimal roomPrice;
    private boolean isBooked;
    private String photo_url;
}