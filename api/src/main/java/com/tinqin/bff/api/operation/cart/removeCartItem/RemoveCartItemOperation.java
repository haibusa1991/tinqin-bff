package com.tinqin.bff.api.operation.cart.removeCartItem;

import com.tinqin.bff.api.base.Processor;
import com.tinqin.bff.api.operation.auth.loginUser.LoginUserInput;
import com.tinqin.bff.api.operation.auth.loginUser.LoginUserResult;

public interface RemoveCartItemOperation extends Processor<LoginUserResult, LoginUserInput> {
}
