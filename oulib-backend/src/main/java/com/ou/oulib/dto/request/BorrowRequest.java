package com.ou.oulib.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowRequest {
    String borrowerId;
    @NotNull
    @Min(0)
    Integer borrowDuration;
    List<String> barcodes = new ArrayList<>();
}
