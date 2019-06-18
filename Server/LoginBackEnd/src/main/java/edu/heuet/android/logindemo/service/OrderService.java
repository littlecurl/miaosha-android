package edu.heuet.android.logindemo.service;

import edu.heuet.android.logindemo.error.BusinessException;
import edu.heuet.android.logindemo.service.model.OrderModel;

public interface OrderService {
    OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BusinessException;
}
