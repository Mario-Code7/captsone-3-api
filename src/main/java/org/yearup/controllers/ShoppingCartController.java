package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

// convert this class to a REST controller
// only logged in users should have access to these actions
@RestController
@RequestMapping("/cart")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController {
    // a shopping cart requires
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;
    private final ProductDao productDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    // each method in this controller requires a Principal object as a parameter
    @GetMapping
    public ShoppingCart getCart(Principal principal) {
        try {
            // get the currently logged in username
            User user = getCurrentUser(principal);
            // find database user by userId

            return shoppingCartDao.getByUserId(user.getId());

            // use the shoppingcartDao to get all items in the cart and return the cart
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
    @PostMapping("/products/{productId}")
    public ShoppingCart addProduct(@PathVariable int productId, Principal principal) {
        try{
            User user = getCurrentUser(principal);
            Product product = productDao.getById(productId);

            if(product == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Oops... our bad.");
            }

            if (shoppingCartDao.containsProduct(user.getId(), productId)) {
                shoppingCartDao.plusQuantity(user.getId(), productId, 1);
            } else {
                shoppingCartDao.addProduct(user.getId(), productId, 1);
            }
            return shoppingCartDao.getByUserId(user.getId());
        }catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
    @PutMapping("/products/{productId}")
    public ShoppingCart updateProduct(@PathVariable int productId, @RequestBody ShoppingCartItem shoppingCartItem, Principal principal) {
        try {
            User user = getCurrentUser(principal);

            if (!shoppingCartDao.containsProduct(user.getId(), productId)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
            shoppingCartDao.updateQuantity(user.getId(), productId, shoppingCartItem.getQuantity());

            return shoppingCartDao.getByUserId(user.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @DeleteMapping
    public ShoppingCart clearCart(Principal principal) {
        try {
            User user = getCurrentUser(principal);
            shoppingCartDao.clearCart(user.getId());

            return shoppingCartDao.getByUserId(user.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added


    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated


    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart

    private User getCurrentUser(Principal principal) {
        String username = principal.getName();
        User user = userDao.getByUserName(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        return user;
    }

}
