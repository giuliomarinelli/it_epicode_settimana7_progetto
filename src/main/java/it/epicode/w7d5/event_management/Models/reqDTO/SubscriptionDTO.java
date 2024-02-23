package it.epicode.w7d5.event_management.Models.reqDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SubscriptionDTO
        (
                @NotNull(message = "'userId' is required")
                @Pattern(regexp = "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$",
                        message = "'userId' field is malformed since it doesn't respect the Universal Unique ID pattern"
                )
                String userId
        ) {}
