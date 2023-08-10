package com.tinqin.bff.core.processor.voucher;

import com.tinqin.bff.api.operation.voucher.activate.ActivateVoucherInput;
import com.tinqin.bff.api.operation.voucher.activate.ActivateVoucherOperation;
import com.tinqin.bff.api.operation.voucher.activate.ActivateVoucherResult;
import com.tinqin.bff.core.exception.UserNotFoundException;
import com.tinqin.bff.core.exception.VoucherExpiredException;
import com.tinqin.bff.core.exception.VoucherIsExhaustedException;
import com.tinqin.bff.core.exception.VoucherNotFoundException;
import com.tinqin.bff.persistence.entity.User;
import com.tinqin.bff.persistence.entity.Voucher;
import com.tinqin.bff.persistence.repository.UserRepository;
import com.tinqin.bff.persistence.repository.VoucherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivateVoucherOperationProcessor implements ActivateVoucherOperation {
    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;

    @Override
    public ActivateVoucherResult process(ActivateVoucherInput input) {
        String voucherCode = input.getVoucherCode();
        Voucher voucher = this.validateCode(voucherCode);

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException(userEmail));

        voucher.setIsExhausted(true);
        this.voucherRepository.save(voucher);

        user.setCredit(user.getCredit().add(BigDecimal.valueOf(voucher.getValue())));
        User persistedUser = this.userRepository.save(user);

        return ActivateVoucherResult.builder()
                .userId(persistedUser.getId())
                .credit(persistedUser.getCredit().doubleValue())
                .build();
    }

    private Voucher validateCode(String voucherCode) {
        Optional<Voucher> voucherOptional = this.voucherRepository.findByCode(voucherCode);

        if (voucherOptional.isEmpty()) {
            throw new VoucherNotFoundException(voucherCode);
        }

        Voucher voucher = voucherOptional.get();

        if (voucher.getIsExhausted()) {
            throw new VoucherIsExhaustedException(voucherCode);
        }

        if (voucher.getValidUntil().isBefore(LocalDateTime.now(Clock.systemUTC()))) {
            throw new VoucherExpiredException(voucherCode);
        }

        return voucher;
    }
}
