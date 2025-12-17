package org.yearup.data;




import org.yearup.models.Order;

import java.math.BigDecimal;

public interface OrderDao {
    int createOrder(Order order, BigDecimal total);

    void addOrderLineItem(int orderId, int productId, int quantity, BigDecimal price);
}
