package com.tinqin.bff.api.operation.cart.emptyCart;

import com.tinqin.bff.api.base.Processor;
import com.tinqin.bff.api.operation.auth.loginUser.LoginUserInput;
import com.tinqin.bff.api.operation.auth.loginUser.LoginUserResult;

public interface EmptyCartOperation extends Processor<LoginUserResult, LoginUserInput> {
}
