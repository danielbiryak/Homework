package projectFiles.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import projectFiles.dao.exception.DaoException;
import projectFiles.dao.UserDao;
import projectFiles.entity.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    private static final String USER_FIELD_FULL = "id, username, role, mailcontact, login, password";
    private static final String USER_FIELD = "username, role, mailcontact, login, password";
    private static final String SELECT_ALL = "select " + USER_FIELD_FULL + " from \"User\"";
    private static final String SELECT_BY_ID = "select " + USER_FIELD_FULL + " from \"User\" where id = ?";
    private static final String SELECT_BY_USERNAME = "select " + USER_FIELD_FULL + " from \"User\" where login = ?";
    private static final String SELECT_BY_EMAIL = "select " + USER_FIELD_FULL + " from \"User\" where mailcontact = ?";
    private static final String INSERT_SQL = "insert into \"User\"(" + USER_FIELD + ") values(?,?,?,?,?)";
    private static final String DELETE_SQL = "delete from \"User\" where id = ?";

    private DataSource dataSource;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Integer create(User user) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            Integer generatedKey = 0;
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getRole().toString());
            preparedStatement.setString(3, user.getMailContact());
            preparedStatement.setString(4, user.getLogin());
            preparedStatement.setString(5, user.getPassword());

            preparedStatement.execute();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                generatedKey = resultSet.getInt(1);
            }
            return generatedKey;
        } catch (SQLException e) {
            throw new DaoException();
        }
    }

    @Override
    public void delete(User user) throws DaoException {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)
        ) {
            preparedStatement.setInt(1, user.getId());

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new DaoException();
        }
    }

    @Override
    public List<User> findAll() throws DaoException {
        List<User> users = new ArrayList<>();
        try (
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SELECT_ALL)
        ) {
            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("role"),
                        resultSet.getString("mailcontact"),
                        resultSet.getString("login"),
                        resultSet.getString("password")
                );
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new DaoException();
        }
    }

    @Override
    public User getById(Integer id) throws DaoException {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID)
        ) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("role"),
                        resultSet.getString("mailcontact"),
                        resultSet.getString("login"),
                        resultSet.getString("password")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new DaoException();
        }
    }

    @Override
    public User getByLogin(String login) throws DaoException {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_USERNAME)
        ) {
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new User(resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("role"),
                        resultSet.getString("mailcontact"),
                        resultSet.getString("login"),
                        resultSet.getString("password"));
            }
            return null;
        } catch (SQLException e) {
            throw new DaoException();
        }
    }

    @Override
    public User getByEmail(String email) throws DaoException {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_EMAIL)
        ) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new User(resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("role"),
                        resultSet.getString("mailcontact"),
                        resultSet.getString("login"),
                        resultSet.getString("password"));
            }
            return null;
        } catch (SQLException e) {
            throw new DaoException();
        }
    }
}