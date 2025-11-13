package net.ryzen.paylinksystem.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class JwtUtilsResponseDTO {
    private String token;
    private Date expiryTime;
}