package com.tinqin.bff.rest.controller;

import com.tinqin.bff.api.operation.voucher.activate.ActivateVoucherInput;
import com.tinqin.bff.api.operation.voucher.activate.ActivateVoucherOperation;
import com.tinqin.bff.api.operation.voucher.activate.ActivateVoucherResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/voucher")
@RequiredArgsConstructor
public class VoucherController {
    private final ActivateVoucherOperation activateVoucher;

    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "Voucher activated successfully.")
    @ApiResponse(responseCode = "400", description = "Voucher is used or expired.")
    @ApiResponse(responseCode = "403", description = "JWT is invalid.")
    @ApiResponse(responseCode = "404", description = "Voucher code does not exist.")
    @Operation(description = "Activates a voucher and puts the sum towards user's credit.",
            summary = "Activates a voucher")
    @PostMapping
    public ResponseEntity<ActivateVoucherResult> activateVoucher(@RequestBody ActivateVoucherInput input) {
        return new ResponseEntity<>(this.activateVoucher.process(input), HttpStatus.OK);
    }
}
