package com.jitu.razorpay_application.operations_service.settlement;

//import com.jitu.RazorPay.common.entity.Money;
//import com.jitu.RazorPay.operations.settlement.dto.BankTransferResult;

import com.jitu.razorpay_application.common_lib.entity.Money;
import com.jitu.razorpay_application.operations_service.settlement.dto.BankTransferResult;

import java.util.UUID;

public interface BankTransferProcessor {

    BankTransferResult initiate(UUID settlementId, UUID merchantId, Money amount,
                                String bankAccount, String ifsc);
}
