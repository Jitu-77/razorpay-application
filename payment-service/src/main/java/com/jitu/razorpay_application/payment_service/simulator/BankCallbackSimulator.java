package com.jitu.razorpay_application.payment_service.simulator;

//import com.jitu.RazorPay.common.enums.ChaosMode;
//import com.jitu.RazorPay.common.enums.PaymentStatus;
//import com.jitu.RazorPay.common.uti.RandomizerUtil;
//import com.jitu.RazorPay.payment.entity.Payment;
//import com.jitu.RazorPay.payment.repository.PaymentRepository;
//import com.jitu.RazorPay.payment.service.PaymentService;


import com.jitu.razorpay_application.common_lib.enums.ChaosMode;
import com.jitu.razorpay_application.common_lib.enums.PaymentStatus;
import com.jitu.razorpay_application.common_lib.uti.RandomizerUtil;
import com.jitu.razorpay_application.payment_service.entity.Payment;
import com.jitu.razorpay_application.payment_service.repository.PaymentRepository;
import com.jitu.razorpay_application.payment_service.service.PaymentService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

//@Component
@Slf4j
@RequiredArgsConstructor
public class BankCallbackSimulator {
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final SimulatorConfig simulatorConfig;


    @Scheduled(fixedDelayString = "${payment.simulator.poll-interval-ms:5000}")
    @SchedulerLock(name = "payment-service-bank-callback-simulator", lockAtMostFor = "10s", lockAtLeastFor = "1s")
    public void processCallbacks() {

        LocalDateTime globalWindow = LocalDateTime.now().minusSeconds(1);

        List<Payment> candidates = paymentRepository
                .findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING, globalWindow);

        log.info("Simulating payments for {} payments", candidates.size());

        if (candidates.isEmpty()) return;

        for (Payment payment: candidates) {
            simulateCallback(payment);
        }

    }
    //just a random number generation and calculation based on variable bucket
    // based on this our bank will approve or reject
    private boolean shouldApprove(Payment payment,
                                  SimulatorConfig.MethodSimulatorConfig methodConfig) {
        int bucket = Math.abs(payment.getId().hashCode()) % 100;
        return bucket < methodConfig.getSuccessRate();
    }

    //we are getting continuous payment requests
    // as this is a queue and all are async operations
    //so we are going to replicate the real world scenario bya delay
    private LocalDateTime dueAt(Payment payment,
                                SimulatorConfig.MethodSimulatorConfig methodConfig) {

        int range = methodConfig.getMaxDelaySeconds() - methodConfig.getMinDelaySeconds();
        int delaySeconds = methodConfig.getMinDelaySeconds() + Math.abs(payment.getId().hashCode()) % (range+1);

        if (simulatorConfig.getChaosMode() == ChaosMode.SLOW) {
            delaySeconds *= 2;
        }

        return payment.getCreatedAt().plusSeconds(delaySeconds);
    }

    private void simulateCallback(Payment payment) {
        SimulatorConfig.MethodSimulatorConfig methodConfig = simulatorConfig.configFor(payment.getMethod());

        LocalDateTime dueAt = dueAt(payment, methodConfig);

        if(LocalDateTime.now().isBefore(dueAt)) {
            return;
        }

        ChaosMode chaosMode = simulatorConfig.getChaosMode();

        switch (chaosMode) {
            case SUCCESS -> resolve(payment, true);
            case FAILURE -> resolve(payment, false);
            case TIMEOUT -> {
                log.debug("BankCallback simulator: Payment Timed out");
            }
            case NORMAL, SLOW -> resolve(payment, shouldApprove(payment, methodConfig));
        }
    }

    // this is the banking side payment resolver for testing
    private void resolve(Payment payment, boolean approve) {
        if (approve) {
            String bankRef = "SIM_BANK_REF"+ RandomizerUtil.randomBase64(8);
            //resolveAuthorization responsible for change of state to Authorized or Failed
            paymentService.resolveAuthorization(payment.getId(), true, bankRef, null, null);
        } else {
            paymentService.resolveAuthorization(payment.getId(), false, null, "SIM_BANK_ERROR_CODE", "Simulated Bank Decline");
        }
    }

}
