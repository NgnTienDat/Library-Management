package com.ou.oulib.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO phản hồi VerifyBarcodeResponse")
public class VerifyBarcodeResponse {
    @Schema(description = "Barcode bản sao sách", example = "BC-0001")
    String barcode;

    @Schema(description = "Nhan đề sách tương ứng với barcode", example = "Clean Code")
    String bookTitle;
}
