package org.yearup.data.mysql;


import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.Order;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {
    public MySqlOrderDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int createOrder(Order order, BigDecimal total) {
        String query = """
                INSERT INTO orders (user_id, date, address, city, state, zip, shipping_amount)
                VALUES (?, NOW(), ?, ?, ?, ?, ?)
                """;

            try(Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, order.getUserId());
                preparedStatement.setString(2, order.getAddress());
                preparedStatement.setString(3, order.getCity());
                preparedStatement.setString(4, order.getState());
                preparedStatement.setString(5, order.getZip());
                preparedStatement.setBigDecimal(6, order.getShippingAmount());

                preparedStatement.executeUpdate();

                ResultSet keys = preparedStatement.getGeneratedKeys();
                if(keys.next()){
                    return keys.getInt(1);
                }
                throw new RuntimeException("Order could not be created");
            }catch(SQLException e){
                throw new RuntimeException(e);
            }
    }

    @Override
    public void addOrderLineItem(int orderId, int productId, int quantity, BigDecimal price) {
        String query = """
                INSERT INTO order_line_items (order_id, product_id, quantity, sale_price, discount)
                VALUES (?, ?, ?, ?, 0)
                """;

        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, orderId);
            preparedStatement.setInt(2, productId);
            preparedStatement.setInt(3, quantity);
            preparedStatement.setBigDecimal(4, price);

            preparedStatement.executeUpdate();

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}
