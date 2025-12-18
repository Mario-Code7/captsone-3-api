package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.OrderDao;
import org.yearup.data.ProfileDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.*;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/orders")
@PreAuthorize("isAuthenticated()")
public class OrderController {
    private final OrderDao orderDao;
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;
    private final ProfileDao profileDao;

    @Autowired
    public OrderController(OrderDao orderDao, ShoppingCartDao shoppingCartDao, UserDao userDao, ProfileDao profileDao) {
        this.orderDao = orderDao;
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.profileDao = profileDao;
    }

    @PostMapping("")
    public void checkout(Principal principal) {
        try{
            User user = userDao.getByUserName(principal.getName());

            ShoppingCart shoppingCart = shoppingCartDao.getByUserId(user.getId());

            if (shoppingCart.getItems().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shopping cart is empty");
            }

            Order order = new Order();
            order.setUserId(user.getId());

            order.setShippingAmount(BigDecimal.ZERO);

            Profile profile = profileDao.getUserById(user.getId());
            if (profile == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile not found");
            }

            order.setAddress(profile.getAddress());
            order.setCity(profile.getCity());
            order.setState(profile.getState());
            order.setZip(profile.getZip());

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
