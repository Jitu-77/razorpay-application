package com.jitu.razorpay_application.payment_service.gateway.adatpter;

//import com.jitu.RazorPay.common.enums.PaymentMethod;
//import com.jitu.RazorPay.payment.gateway.PaymentAdapter;
//import com.jitu.RazorPay.payment.gateway.dto.PaymentRequest;
//import com.jitu.RazorPay.payment.gateway.dto.PaymentResult;
//import com.jitu.RazorPay.payment.processor.PaymentProcessorRouter;
//import com.jitu.RazorPay.payment.processor.dto.PaymentProcessorRequest;
//import com.jitu.RazorPay.payment.processor.dto.PaymentProcessorResponse;


import com.jitu.razorpay_application.common_lib.enums.PaymentMethod;
import com.jitu.razorpay_application.payment_service.gateway.PaymentAdapter;
import com.jitu.razorpay_application.payment_service.gateway.dto.PaymentRequest;
import com.jitu.razorpay_application.payment_service.gateway.dto.PaymentResult;
import com.jitu.razorpay_application.payment_service.processor.PaymentProcessorRouter;
import com.jitu.razorpay_application.payment_service.processor.dto.PaymentProcessorRequest;
import com.jitu.razorpay_application.payment_service.processor.dto.PaymentProcessorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("UPI") // automatically creates the map as we are passing value like this
@Slf4j
@RequiredArgsConstructor
public class UpiPaymentAdapter implements PaymentAdapter {
    private final PaymentProcessorRouter paymentProcessorRouter;
    @Override
    public PaymentResult initiate(PaymentRequest paymentRequest) {

        log.info("Initiate Payment with UPI Adapter, paymentId: {}", paymentRequest.paymentId());

        try {
            PaymentProcessorRequest paymentProcessorRequest =
                    PaymentProcessorRequest.nonCard(
                            paymentRequest.paymentId(),
                            paymentRequest.amount(),
                            PaymentMethod.UPI,
                            paymentRequest.methodDetails()
                    );

            PaymentProcessorResponse paymentProcessorResponse = paymentProcessorRouter.charge(
                    paymentProcessorRequest);


            return switch (paymentProcessorResponse) {
                case PaymentProcessorResponse.Failure failure ->
                        new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());

                case PaymentProcessorResponse.Pending pending ->
                        new PaymentResult.Pending(pending.processorReference());

                case PaymentProcessorResponse.Success success -> new PaymentResult.Success(success.bankReference());

            };
        } catch (Exception e) {
            log.warn("UPI failed, paymentId: {}", paymentRequest.paymentId());
            return new PaymentResult.Failure("UPI_FAILED", e.getMessage());
        }
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("UPI_REF");
    }
}
