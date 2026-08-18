package com.jitu.razorpay_application.merchant_service.entity;


import com.jitu.razorpay_application.common_lib.entity.BaseEntity;
import com.jitu.razorpay_application.common_lib.enums.BusinessType;
import com.jitu.razorpay_application.common_lib.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "merchant", indexes = {
        @Index(name = "idx_merchant_status", columnList = "status")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Merchant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable=false,length=200)
    private String name;
    @Column(unique=true,nullable=false,length=200)
    private String email;

    @Column(length = 20)
    private String contactNumber;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @Column(length = 20)
    private String businessName;

    @Column(length = 200)
    private String websiteUrl;

    @Column(length = 20,nullable = false)
    @Enumerated(EnumType.STRING)
    private MerchantStatus status = MerchantStatus.PENDING_KYC;

    @Column(length = 20)
    private String gstId;

    @Column(length = 20)
    private String panId;

    @Column(length = 200)
    private String settlementBankAccount;

    @Column(length = 20)
    private String settlementBankIfsc;

    @Column(length = 200)
    private String settlementBankAccountHolderName;

}
