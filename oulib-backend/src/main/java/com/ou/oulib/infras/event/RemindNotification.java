package com.ou.oulib.infras.event;

import lombok.Builder;

import java.time.Instant;

@Builder
public record  RemindNotification(
        String userId,
        String fullName,
        String bookBarcode,
        String bookTitle,
        Instant createdAt
) {}