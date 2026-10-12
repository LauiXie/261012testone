package com.example.idcard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * 新增 / 修改身份证记录的请求体。
 *
 * <p>{@code gender}、{@code birthDate} 可省略，省略时由身份证号自动推导。</p>
 */
public record IdCardRequest(

        @NotBlank(message = "姓名不能为空")
        @Size(max = 64, message = "姓名长度不能超过 64")
        String name,

        @NotBlank(message = "身份证号不能为空")
        @Size(max = 32, message = "身份证号长度不能超过 32")
        String idNumber,

        @Size(max = 8, message = "性别长度不能超过 8")
        String gender,

        LocalDate birthDate,

        @Size(max = 255, message = "地址长度不能超过 255")
        String address
) {
}
