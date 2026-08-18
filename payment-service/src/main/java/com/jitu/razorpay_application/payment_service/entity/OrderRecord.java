package com.jitu.razorpay_application.payment_service.entity;

//import com.jitu.RazorPay.common.entity.BaseEntity;
//import com.jitu.RazorPay.common.entity.Money;
//import com.jitu.RazorPay.common.enums.OrderStatus;


import com.jitu.razorpay_application.common_lib.entity.BaseEntity;
import com.jitu.razorpay_application.common_lib.entity.Money;
import com.jitu.razorpay_application.common_lib.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "order_record" , indexes = {
        @Index(name = "idx_order_id_merchant_id", columnList = "id, merchant_id"),
        @Index(name = "idx_order_merchant_id", columnList = "merchant_id") //use case for find all orders by merchant
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRecord extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "customer_id")
    private UUID customerId;

    @Embedded
    private Money amount;

    @Column(length = 100)
    private String receipt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus orderStatus = OrderStatus.CREATED;

    @Column(nullable = false)
    @Builder.Default
    private Integer attempts = 0;

    @JdbcTypeCode((SqlTypes.JSON))
    // If we dont store this we will get t  he whole data type as string blob
    //JSON converted to a MAP properties
    @Column(columnDefinition="jsonb")
    //we need <jackson-databind> to convert the JSON to java obj vice versa
    // this helps the Hibernate to support to JSONB
    private Map<String, Object>  notes;

    @Column(nullable = false)
    private LocalDateTime expiresAt;    
}
