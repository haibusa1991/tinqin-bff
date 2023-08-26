package com.tinqin.bff.core.processor.voucher;

import com.tinqin.bff.api.operation.voucher.purchase.PurchaseVoucherOperation;
import com.tinqin.bff.api.operation.voucher.purchase.PurchaseVoucherOperationInput;
import com.tinqin.bff.api.operation.voucher.purchase.PurchaseVoucherResult;
import com.tinqin.bff.persistence.entity.Voucher;
import com.tinqin.bff.persistence.repository.VoucherRepository;
import com.tinqin.storage.restexport.StorageRestExport;
import com.tinqin.zoostore.api.operations.item.getItemById.GetItemByIdResult;
import com.tinqin.zoostore.api.operations.item.getItemByPartialTitle.GetItemByPartialTitleResult;
import com.tinqin.zoostore.restexport.ZooStoreRestExport;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
@Service
@RequiredArgsConstructor
public class PurchaseVoucherOperationProcessor implements PurchaseVoucherOperation {
    private final VoucherRepository voucherRepository;
    private final ZooStoreRestExport zoostoreClient;
    private final StorageRestExport storageClient;
    private final Logger logger;

    //TODO mail trap research

    @Override
    public PurchaseVoucherResult process(PurchaseVoucherOperationInput input) {
        GetItemByPartialTitleResult itemByPartialTitle = zoostoreClient.getItemByPartialTitle("voucher", 10, 1);

        List<UUID> list = input.getItems()
                .stream()
                .filter(e -> itemByPartialTitle.getItems()
                        .stream()
                        .anyMatch(v -> v.getId().toString().contains(e.toString())))
                .toList();

        list.stream()
                .map(this::generateVoucher)
                .forEach(this::sendEmail);

        return PurchaseVoucherResult.builder().build();
    }

    private Voucher generateVoucher(UUID voucherItemId) {
        Double voucherValue = this.storageClient
                .getItemByReferencedItemId(Set.of(voucherItemId.toString()))
                .getItems()
                .stream()
                .findFirst()
                .orElseThrow()
                .getPrice();


        Voucher voucher = Voucher.builder()
                .code(this.getCode(8))
                .value(voucherValue.intValue())
                .validUntil(LocalDateTime.now(Clock.systemUTC()).plusDays(30))
                .build();

        return this.voucherRepository.save(voucher);
    }

    private String getCode(Integer codeLength) {
        Random r = new Random();

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < codeLength; i++) {
            stringBuilder.append((char) (r.nextInt(25) + 65));
        }

        return stringBuilder.toString();

    }

    private void sendEmail(Voucher voucher) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        this.logger.info(String.format("Sending email to '%s' with voucher code '%s' with value %s", name, voucher.getCode(),voucher.getValue()));
    }
}
