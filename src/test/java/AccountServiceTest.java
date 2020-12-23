import lesson9.AccountService;
import lesson9.UnknownAccountException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AccountServiceTest {
    private Connection connection;
    AccountService accountService;

    @Before
    public void init() {
        try {
            connection = DriverManager.getConnection("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM './Database.sql'\\;RUNSCRIPT FROM './FactureForBase.sql'");
            accountService = new AccountService(connection);
        } catch(SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @After
    public void destroy() throws SQLException {
        connection.close();
    }

    @Test
    public void test_connection_is_not_null() {
        Assert.assertNotNull(connection);
    }

    @Test
    public void testWithDraw() throws UnknownAccountException, SQLException, IOException {
        int accountId = 1;
        int amount = 150;
        int balanceBefore = accountService.balance(accountId);
        accountService.withdraw(accountId,amount);
        int balanceAfter = accountService.balance(accountId);
        Assert.assertEquals(balanceAfter,balanceBefore - amount);
    }

    @Test
    public void testDeposit() throws SQLException, UnknownAccountException {
        int accountId = 2;
        int amount = 99;
        int balanceBefore = accountService.balance(accountId);
        accountService.deposit(accountId,amount);
        int balanceAfter = accountService.balance(accountId);
        Assert.assertEquals(balanceAfter,balanceBefore + amount);
    }

    @Test
    public void testTransfer() throws SQLException, UnknownAccountException {
        int accountTo = 3;
        int accountFrom = 5;
        int amount = 50;
        int balanceToBefore = accountService.balance(accountTo);
        int balanceFromBefore = accountService.balance(accountFrom);
        accountService.transfer(accountFrom,accountTo,amount);
        int balanceToAfter = accountService.balance(accountTo);
        int balanceFromAfter = accountService.balance(accountFrom);
        Assert.assertEquals(balanceFromAfter,balanceFromBefore - amount);
        Assert.assertEquals(balanceToAfter,balanceToBefore + amount);
    }
}


