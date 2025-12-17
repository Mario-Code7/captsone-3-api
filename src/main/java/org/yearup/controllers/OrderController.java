package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.OrderDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Order;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/orders")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class OrderController {
    private OrderDao orderDao;
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;

    @Autowired
    public OrderController(OrderDao orderDao, ShoppingCartDao shoppingCartDao, UserDao userDao) {
        this.orderDao = orderDao;
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    @PostMapping
    public void checkout(Principal principal) {
        try{
            User user = userDao.getByUserName(principal.getName());

            ShoppingCart shoppingCart = shoppingCartDao.getByUserId(user.getId());

            if (shoppingCart.getItems().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shopping cart is empty");
            }

            Order order = new Order();
            order.setUserId(user.getId());
            order.setAddress("771 Ello Street");
            order.setCity("London");
            order.setState("CA");
            order.setZip("45455");

            BigDecimal total = shoppingCart.getTotal();

            int orderId = orderDao.createOrder(order, total);

            for (ShoppingCartItem item : shoppingCart.getItems().values()){
                orderDao.addOrderLineItem(
                        orderId,
                        item.getProduct().getProductId(),
                        item.getQuantity(),
                        item.getProduct().getPrice()
                );
            }
            shoppingCartDao.clearCart(user.getId());
        }catch(Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Checkout failed");
        }
    }


}
