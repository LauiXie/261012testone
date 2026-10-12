package com.example.idcard.dto;

import com.example.idcard.entity.IdCard;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 身份证记录返回体。
 */
public record IdCardResponse(
        Long id,
        String name,
        String idNumber,
        String gender,
        LocalDate birthDate,
        String address,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime updatedAt
) {

    public static IdCardResponse from(IdCard entity) {
        return new IdCardResponse(
                entity.getId(),
                entity.getName(),
                entity.getIdNumber(),
                entity.getGender(),
                entity.getBirthDate(),
                entity.getAddress(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
