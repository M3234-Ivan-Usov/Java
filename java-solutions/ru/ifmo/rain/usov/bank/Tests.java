package ru.ifmo.rain.usov.bank;

import org.junit.*;

import static org.junit.Assert.*;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runners.MethodSorters;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Tests {
    private static BankOperations testBank;

    @BeforeClass
    public static void runServer() {
        System.out.println("---Starting test---");
        try {
            testBank = new Bank(0);
            final Registry registry = LocateRegistry.createRegistry(1090);
            Remote stub = UnicastRemoteObject.exportObject(testBank, 0);
            registry.bind("//localhost/bank", stub);
        } catch (RemoteException | AlreadyBoundException e) {
            System.out.println("Failed to launch a test");
        }
    }

    @AfterClass
    public static void end() {
        System.out.println("---End testing---");
    }

    @Test
    public void test01_addPerson() throws RemoteException {
        Client.main(new String[]{"Ivan", "Ivanov", "1111"});
        RemotePerson testPerson = testBank.getRemote(1111);
        assertNotNull(testPerson);
        assertEquals("Ivan", testPerson.getFirstName());
        assertEquals("Ivanov", testPerson.getLastName());
        assertEquals(1111, testPerson.getPassport());
        assertEquals(testPerson.accounts.keySet().size(), 0);
        assertEquals("Does not hold any account", testPerson.getFullInfo());
    }

    @Test
    public void test02_addPersonWithTheSameName() throws RemoteException {
        Client.main(new String[]{"Ivan", "Ivanov", "1234"});
        RemotePerson testPerson = testBank.getRemote(1234);
        assertNotNull(testPerson);
        assertEquals("Ivan", testPerson.getFirstName());
        assertEquals("Ivanov", testPerson.getLastName());
        assertEquals(1234, testPerson.getPassport());
        assertEquals(0, testPerson.accounts.keySet().size());
        assertEquals("Does not hold any account", testPerson.getFullInfo());
    }

    @Test
    public void test03_createZeroAccountForNewUser() throws RemoteException {
        Client.main(new String[]{"Petrov", "Petr", "1000", "account1"});
        RemotePerson testPerson = testBank.getRemote(1000);
        assertNotNull(testPerson);
        assertEquals(1, testPerson.accounts.keySet().size());
        assertTrue(testPerson.accounts.containsKey("account1"));
        int amount = testPerson.accounts.get("account1");
        assertEquals(0, amount);
    }

    @Test
    public void test04_createZeroAccountForExistingUser() throws RemoteException {
        Client.main(new String[]{"Petrov", "Petr", "1000", "account2"});
        RemotePerson testPerson = testBank.getRemote(1000);
        assertNotNull(testPerson);
        assertEquals(2, testPerson.accounts.keySet().size());
        assertTrue(testPerson.accounts.containsKey("account1"));
        assertTrue(testPerson.accounts.containsKey("account2"));
        int amount1 = testPerson.accounts.get("account1");
        int amount2 = testPerson.accounts.get("account2");
        assertEquals(0 ,amount1);
        assertEquals(0, amount2);
    }

    @Test
    public void test05_createNonZeroAccount() throws RemoteException {
        Client.main(new String[]{"Petrov", "Petr", "1000", "account3", "200"});
        RemotePerson testPerson = testBank.getRemote(1000);
        assertNotNull(testPerson);
        assertEquals(3, testPerson.accounts.keySet().size());
        assertTrue(testPerson.accounts.containsKey("account3"));
        int amount = testPerson.accounts.get("account3");
        assertEquals(200, amount);
    }

    @Test
    public void test06_increaseAmount() throws RemoteException {
        Client.main(new String[]{"Petrov", "Petr", "1000", "account3", "500"});
        RemotePerson testPerson = testBank.getRemote(1000);
        assertNotNull(testPerson);
        assertEquals(3, testPerson.accounts.keySet().size());
        assertTrue(testPerson.accounts.containsKey("account3"));
        int amount = testPerson.accounts.get("account3");
        assertEquals(700, amount);
    }

    @Test
    public void test07_decreaseAmountOK() throws RemoteException {
        Client.main(new String[]{"Petrov", "Petr", "1000", "account3", "-400"});
        RemotePerson testPerson = testBank.getRemote(1000);
        assertNotNull(testPerson);
        assertEquals(3, testPerson.accounts.keySet().size());
        assertTrue(testPerson.accounts.containsKey("account3"));
        int amount = testPerson.accounts.get("account3");
        assertEquals(300, amount);
    }

    @Test
    public void test08_decreaseAmountFailed() throws RemoteException {
        Client.main(new String[]{"Petrov", "Petr", "1000", "account3", "-1000"});
        RemotePerson testPerson = testBank.getRemote(1000);
        assertNotNull(testPerson);
        assertEquals(3, testPerson.accounts.keySet().size());
        assertTrue(testPerson.accounts.containsKey("account3"));
        int amount = testPerson.accounts.get("account3");
        assertEquals(300, amount);
    }

    @Test
    public void test09_isClientCheck() throws RemoteException {
        assertTrue(testBank.isClient(1111));
        assertFalse(testBank.isClient(0));
    }

    @Test
    public void test10_accountsCheck() throws RemoteException {
        Client.main(new String[]{"Petrov", "Petr", "1000"});
        RemotePerson testPerson = testBank.getRemote(1000);
        assertNotNull(testPerson);
        assertEquals("1000:account1 0\n1000:account2 0\n1000:account3 300\n", testPerson.getFullInfo());
    }

    @Test
    public void test11_addExistingPassport() throws RemoteException {
        assertEquals("Personal info is incorrect",
                testBank.createPerson("NoName", "NoSurName", 1111));
    }

    @Test
    public void test12_multiIncrease() throws InterruptedException, RemoteException {
        Runnable task = () -> Client.main(new String[]{"Multi", "Thread", "3333", "multiAccount", "1"});
        testBank.createPerson("Multi", "Thread", 3333);
        testBank.createAccount(3333, "multiAccount");
        assertEquals(0, testBank.getRemote(3333).getBalance("account"));
        List<Thread> incrementors = new LinkedList<>();
        for (int i = 1; i <= 200; i++) {
            incrementors.add(new Thread(task));
        }
        incrementors.forEach(Thread::start);
        for (int i = 1; i <= 200; i++) {
            incrementors.get(i - 1).join();
        }
        assertEquals(200, testBank.getRemote(3333).getBalance("multiAccount"));
    }


    public static void main(String[] args) {
        final Result result = new JUnitCore().run(Tests.class);
        if (result.wasSuccessful()) {
            System.out.println("---Success---");
        } else {
            for (Failure fail : result.getFailures()) {
                System.out.println(String.format("%s :: %s", fail.getDescription().getMethodName(), fail.getMessage()));
            }
        }
        System.exit(0);
    }
}
