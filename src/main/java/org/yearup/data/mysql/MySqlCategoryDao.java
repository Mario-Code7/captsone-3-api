package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {
    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();

        String query = """
        SELECT *
        FROM categories
        """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet row = statement.executeQuery())
        {
            while (row.next())
            {
                categories.add(mapRow(row));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return categories;
    }

    @Override
    public Category getById(int categoryId)
    { String query = """
            SELECT category_id, name, description
            FROM categories
            WHERE category_id = ?
            """;

        try(Connection connection = getConnection();
        PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {


            preparedStatement.setInt(1, categoryId);
            try (ResultSet row = preparedStatement.executeQuery()){
                if (row.next()){
                    return mapRow(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // get category by id
        return null;
    }

    @Override
    public Category create(Category category)
    { String query = """
        INSERT INTO categories (name, description)
        VALUES (?, ?)
        """;

        try(Connection connection = getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(query,  PreparedStatement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.executeUpdate();

            try(ResultSet keys = preparedStatement.getGeneratedKeys()){
                if (keys.next()){
                    category.setCategoryId(keys.getInt(1));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        // create a new category
        return category;
    }

    @Override
    public void update(int categoryId, Category category)
    { String query = """
            UPDATE categories
            SET name=?, description=?
            WHERE category_id=?
            """;

        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);{

                preparedStatement.setString(1, category.getName());
                preparedStatement.setString(2, category.getDescription());
                preparedStatement.setInt(3, categoryId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // update category
    }

    @Override
    public void delete(int categoryId)
    { String query = """
            DELETE FROM categories
            WHERE category_id=?
            """;

        try(Connection connection = getConnection()) {
        PreparedStatement preparedStatement = connection.prepareStatement(query); {

                preparedStatement.setInt(1, categoryId);
                preparedStatement.executeUpdate();
            }
            }catch(SQLException e){
            throw new RuntimeException(e);
        }
        // delete category
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }
    }
