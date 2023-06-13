package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO myusers (id, firstname, lastname, age) VALUES (?, ?, ?, ?)";
    private static final String updateUserSQL = "UPDATE myusers SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
    private static final String deleteUser = "DELETE FROM myusers WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname = ? AND lastname = ?";
    private static final String findAllUserSQL = "SELECT * FROM myusers";

    public Long createUser(User user) {
        try(Connection connection = CustomDataSource.getInstance().getConnection()) {
            ps = connection.prepareStatement(createUserSQL);
            ps.setLong(1, user.getId());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setInt(4, user.getAge());
            ps.executeUpdate();
            return user.getId();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserById(Long userId) {
        try(Connection connection = CustomDataSource.getInstance().getConnection()) {
            ps = connection.prepareStatement(findUserByIdSQL);
            ps.setLong(1, userId);
            ResultSet set = ps.executeQuery();
            String firstname = set.getString(2);
            String lastname = set.getString(3);
            int age = set.getInt(4);
            return new User(userId, firstname, lastname, age);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserByName(String userName) {
        try(Connection connection = CustomDataSource.getInstance().getConnection()) {
            ps = connection.prepareStatement(findUserByNameSQL);
            String firstname = userName.split(" ")[0];
            String lastname = userName.split(" ")[1];
            ps.setString(1, firstname);
            ps.setString(2, lastname);
            ResultSet set = ps.executeQuery();
            Long id = set.getLong(1);
            int age = set.getInt(4);
            return new User(id, firstname, lastname, age);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> findAllUser() {
        try(Connection connection = CustomDataSource.getInstance().getConnection()) {
            List<User> userList = new ArrayList<>();
            ps = connection.prepareStatement(findAllUserSQL);
            ResultSet set = ps.executeQuery();
            while (set.next()) {
                Long id = set.getLong(1);
                String firstname = set.getString(2);
                String lastname = set.getString(3);
                int age = set.getInt(4);
                userList.add(new User(id, firstname, lastname, age));
            }
            return userList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User updateUser(Long id) {
        try(Connection connection = CustomDataSource.getInstance().getConnection()) {
            ps = connection.prepareStatement(updateUserSQL);
            ps.executeUpdate();
            ResultSet set = ps.executeQuery(findUserByIdSQL);
            String firstname = set.getString(2);
            String lastname = set.getString(3);
            int age = set.getInt(4);
            return new User(id, firstname, lastname, age);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteUser(User user) {
        try(Connection connection = CustomDataSource.getInstance().getConnection()) {
            ps = connection.prepareStatement(deleteUser);
            ps.setLong(1, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
