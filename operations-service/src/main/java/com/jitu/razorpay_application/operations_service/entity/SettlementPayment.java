package com.jitu.razorpay_application.operations_service.entity;



//since a table cannot have 2 PK keys hence we need the embeddedId via SettlementPaymentId


import com.jitu.razorpay_application.common_lib.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "settlement_payment")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SettlementPayment extends BaseEntity {

    @EmbeddedId
    private SettlementPaymentId id;

    // by  this it won't create a column called  settlement_id
    // this is just referring to SettlementPaymentId --> settlement_id
    // link wise
    // we can get the settlement_id from here only via this @MapsId
    // @MapsId -- help to map any id of the PK with the ID referred here --   settlement_id

    @MapsId("settlementId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;
}