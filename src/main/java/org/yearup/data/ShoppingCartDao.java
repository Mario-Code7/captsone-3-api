package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
    boolean containsProduct(int userId, int productId);

    void addProduct(int userId, int productId, int quantity);

    void plusQuantity(int userId, int productId, int quantity);

    void updateQuantity(int userId, int productId, int quantity);

    void clearCart(int userId);
}
