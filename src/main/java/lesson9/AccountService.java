package lesson9;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountService {
    private ResultSet resultSet;
    private Connection connection;
    private PreparedStatement preparedStatement;

    public AccountService(Connection connection) {
        this.connection = connection;
    }

    public int withdraw(int accountId,int amount) throws NotEnoughMoneyException, UnknownAccountException, IOException, SQLException {
        String sql = "SELECT * FROM ACCOUNTS WHERE id = ?";
        String sql2 = "UPDATE ACCOUNTS SET amount = ? WHERE id = ?";
        if(!isUserExist(accountId)) {
            throw new UnknownAccountException("Пользователь не найден");
        } else {
            try {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1,accountId);
                resultSet = preparedStatement.executeQuery();
                while(resultSet.next()) {
                    int temp = resultSet.getInt(3);
                    if(temp < amount) {
                        throw new NotEnoughMoneyException("Не хватает средств");
                    } else {
                        preparedStatement = connection.prepareStatement(sql2);
                        preparedStatement.setInt(1,(temp - amount));
                        preparedStatement.setInt(2,accountId);
                        preparedStatement.executeUpdate();
                        System.out.println("C баланса снято " + amount);
                    }
                }
            } catch(SQLException e) {
                e.printStackTrace();
            } finally{
                preparedStatement.close();
            }
        }
        return amount;
    }

    public int balance(int accountId) throws UnknownAccountException, SQLException {
        String sql = "SELECT * FROM ACCOUNTS WHERE id = ?";
        int balance = 0;
        if(!isUserExist(accountId)) {
            throw new UnknownAccountException("Пользователь не найден");
        } else {
            try {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1,accountId);
                resultSet = preparedStatement.executeQuery();
                while(resultSet.next()) {
                    System.out.println("Счет с идентификатором №" + accountId + " имеет сумму на счете " + resultSet.getInt(3));
                    balance = resultSet.getInt(3);
                }
            } catch(SQLException e) {
                e.printStackTrace();
            } finally{
                preparedStatement.close();
            }
        }
        return balance;
    }


    public void deposit(int accountId,int amount) throws NotEnoughMoneyException, UnknownAccountException, SQLException {
        String sql = "SELECT * FROM ACCOUNTS WHERE id = ?";
        String sql2 = "UPDATE ACCOUNTS SET amount = ? WHERE id = ?";
        if(!isUserExist(accountId)) {
            throw new UnknownAccountException("Пользователь не найден");
        } else {
            if(amount <= 0) {
                throw new NotEnoughMoneyException("Некорректная сумма");
            } else {
                try {
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setInt(1,accountId);
                    resultSet = preparedStatement.executeQuery();
                    while(resultSet.next()) {
                        int temp = resultSet.getInt(3);
                        preparedStatement = connection.prepareStatement(sql2);
                        preparedStatement.setInt(1,(temp + amount));
                        preparedStatement.setInt(2,accountId);
                        preparedStatement.executeUpdate();
                        System.out.println("На счет внесено " + amount);
                    }
                } catch(SQLException e) {
                    e.printStackTrace();
                } finally{
                    preparedStatement.close();
                }
            }
        }

    }

    public void transfer(int from,int to,int amount) throws NotEnoughMoneyException, UnknownAccountException, SQLException {
        String sql = "SELECT * FROM ACCOUNTS WHERE id = ?";
        String sql2 = "UPDATE ACCOUNTS SET amount = ? WHERE id = ?";
        if(!(isUserExist(from) || isUserExist(to))) {
            throw new UnknownAccountException("Пользователь не найден");
        } else {
            if(amount <= 0) {
                throw new NotEnoughMoneyException("Некорректная сумма перевода");
            } else {
                try {
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setInt(1,from);
                    resultSet = preparedStatement.executeQuery();
                    while(resultSet.next()) {
                        int temp = resultSet.getInt(3);
                        if(temp < amount) {
                            throw new NotEnoughMoneyException("Недостаточно средств для перевода");
                        } else {
                            try {
                                preparedStatement = connection.prepareStatement(sql);
                                preparedStatement.setInt(1,to);
                                resultSet = preparedStatement.executeQuery();
                                while(resultSet.next()) {
                                    temp = resultSet.getInt(3);
                                    preparedStatement = connection.prepareStatement(sql2);
                                    preparedStatement.setInt(1,(temp + amount));
                                    preparedStatement.setInt(2,to);
                                    preparedStatement.executeUpdate();
                                }
                                preparedStatement = connection.prepareStatement(sql);
                                preparedStatement.setInt(1,from);
                                resultSet = preparedStatement.executeQuery();
                                while(resultSet.next()) {
                                    temp = resultSet.getInt(3);
                                    preparedStatement = connection.prepareStatement(sql2);
                                    preparedStatement.setInt(1,(temp - amount));
                                    preparedStatement.setInt(2,from);
                                    preparedStatement.executeUpdate();
                                }
                                System.out.println("На счет № " + to + " переведено: " + amount);
                            } catch(SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch(SQLException e) {
                    e.printStackTrace();
                } finally{
                   preparedStatement.close();
                }
            }
        }
    }

    boolean isUserExist(int accountId) throws SQLException {
        String sql = "SELECT * FROM ACCOUNTS WHERE id = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,accountId);
        resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }
}


