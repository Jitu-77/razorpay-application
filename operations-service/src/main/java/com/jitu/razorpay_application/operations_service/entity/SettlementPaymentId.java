package com.jitu.razorpay_application.operations_service.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;


// this is actually the main join table that will create a embedded ID

@Embeddable
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SettlementPaymentId {

    private UUID settlementId;

    private UUID paymentId;
}
