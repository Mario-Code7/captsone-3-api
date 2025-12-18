# E-Commerce API and Site

This Capstone 3 project is a Spring Boot API for a simple e-commerce application. It allows users to manage products, shopping carts, profiles, and orders.

---

## **Features**

### 1. **Products**
- Retrieve product details by ID.
- Products have attributes like name, price, category, description, stock, image, and featured status.
- Implemented in `MySqlProductDao` and exposed via a `ProductController`.

### 2. **Shopping Cart**
- Users can add products to their cart.
- Update quantities of items in the cart.
- Clear the cart completely.
- Each user has their own cart linked by `user_id`.
- Implemented in `ShoppingCartController` and `MySqlShoppingCartDao`.

#### **Endpoints**
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/cart` | Get current user's shopping cart |
| POST | `/cart/{productId}` | Add a product to the cart |
| PUT | `/cart/products/{productId}` | Update quantity of a product in the cart |
| DELETE | `/cart` | Clear the shopping cart |

---

### 3. **User Profiles**
- Create, read, and update user profile information.
- Fields: `first_name`, `last_name`, `phone`, `email`, `address`, `city`, `state`, `zip`.
- Implemented in `MySqlProfileDao` and `ProfileDao`.

#### **Endpoints**
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/profile/{userId}` | Get profile for a user |
| POST | `/profile` | Create a new profile |
| PUT | `/profile/{userId}` | Update existing profile |

**Fixes Applied:**
- Fixed the SQL update query to include all profile fields (`address`, `city`, `state`, `zip`).
- Null-safety added for database operations.

---

### 4. **Orders / Checkout**
- Convert a user's shopping cart into an order.
- Each order includes shipping details and items from the cart.
- Order totals are calculated from the shopping cart.
- Implemented in `OrderController` and `MySqlOrderDao`.

#### **Endpoints**
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/orders` | Checkout and create a new order from current user's cart |

**Fixes Applied:**
- Null-safety for profile fields used in the order.
- Shipping amount defaults to 0 if null.
- 500 errors during checkout resolved.

---

## **Database Tables**

### Products
- `product_id`, `name`, `price`, `category_id`, `description`, `sub_category`, `stock`, `image_url`, `featured`

### Shopping Cart
- `shopping_cart_items`: `user_id`, `product_id`, `quantity`

### Profiles
- `profiles`: `user_id`, `first_name`, `last_name`, `phone`, `email`, `address`, `city`, `state`, `zip`

### Orders
- `orders`: `order_id`, `user_id`, `date`, `address`, `city`, `state`, `zip`, `shipping_amount`
- `order_line_items`: `order_line_item_id`, `order_id`, `product_id`, `sales_price`, `quantity`, `discount`

---

## **Testing via Insomnia**

### Shopping Cart
1. **GET Cart**
   - URL: `http://localhost:8080/cart`
   - Method: GET
   - Auth: Logged in user

2. **Add Product**
   - URL: `http://localhost:8080/cart/{productId}`
   - Method: POST
   - Auth: Logged in user
   - Body: None

3. **Update Quantity**
   - URL: `http://localhost:8080/cart/products/{productId}`
   - Method: PUT
   - Body (JSON): `{ "quantity": 3 }`

4. **Clear Cart**
   - URL: `http://localhost:8080/cart`
   - Method: DELETE

### Orders / Checkout
- URL: `http://localhost:8080/orders`
- Method: POST
- Body: None
- Auth: Logged in user
- On success: shopping cart is cleared and a new order is created.

---

## **Setup Instructions**
1. Clone the repository.
2. Set up a MySQL database and update `application.properties` with credentials.
3. Run SQL scripts to create the required tables: `products`, `profiles`, `shopping_cart_items`, `orders`, `order_line_items`.
4. Build and run the Spring Boot application.
5. Test endpoints via **Insomnia** or another REST client.
6. Make sure you are logged in (Spring Security is enabled).

---

## **Notes**
- All endpoints require authentication.
- Null safety checks added to prevent SQL errors.
- All exceptions return meaningful HTTP status codes:
  - 400: Bad request (e.g., empty cart, missing profile)
  - 401: Unauthorized (not logged in)
  - 404: Not found (e.g., product not found)
  - 500: Internal server error

## **Screenshots**
- <img width="1920" height="978" alt="Screenshot 2025-12-17 151140" src="https://github.com/user-attachments/assets/ff0bc562-4d09-4e93-a328-ee16d4a2d2e5" />

- <img width="1920" height="975" alt="Screenshot 2025-12-17 151204" src="https://github.com/user-attachments/assets/f8f4978d-6a40-401a-8a40-3cea82f69153" />

- <img width="1920" height="991" alt="Screenshot 2025-12-17 153213" src="https://github.com/user-attachments/assets/a8129e2a-7661-4a0c-bfe8-a4fba6f9967d" />

- <img width="521" height="981" alt="Screenshot 2025-12-17 153249" src="https://github.com/user-attachments/assets/5c6d7720-e9a6-4a33-93a3-c5435cda0cbd" />

- <img width="1920" height="1081" alt="Screenshot 2025-12-18 135738" src="https://github.com/user-attachments/assets/a02795dd-0436-441c-899e-bde65fc937ef" />

- <img width="1920" height="1011" alt="Screenshot 2025-12-18 153200" src="https://github.com/user-attachments/assets/1ad9f8af-4783-4f7a-a3fc-e15d3fa49aa5" />







