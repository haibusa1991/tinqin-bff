package com.tinqin.bff.api.operation.cart.getAllCartItems;

import com.tinqin.bff.api.base.Processor;
import com.tinqin.bff.api.operation.auth.loginUser.LoginUserInput;
import com.tinqin.bff.api.operation.auth.loginUser.LoginUserResult;

public interface GetAllCartItemsOperation extends Processor<LoginUserResult, LoginUserInput> {
}
