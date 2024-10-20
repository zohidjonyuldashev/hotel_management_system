package uz.developer.zohidjon.hotel_management_system.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class RoomRequest {
    private String roomType;
    private BigDecimal roomPrice;
}
