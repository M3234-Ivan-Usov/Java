package ru.ifmo.rain.usov.bank;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Bank implements BankOperations {
    private final int port;
    private ConcurrentMap<Long, RemotePerson> clients;
    private Map<Date, String> operations;

    public Bank(int port) {
        this.port = port;
        clients = new ConcurrentHashMap<>();
        operations = new TreeMap<>();
    }

    private void log(String message) {
        operations.put(new Date(), message);
        System.out.println(message);
    }

    @Override
    public synchronized String createPerson(String firstName, String lastName, long passport) throws RemoteException {
        String message;
        if (!isClient(passport)) {
            RemotePerson person = new RemotePerson(firstName, lastName, passport, port);
            clients.put(passport, person);
            message = "Client " + firstName + " " + lastName +
                    " with passport " + passport + " is created";
            log(message);
        } else {
            RemotePerson person = getRemote(passport);
            if ((person.getFirstName().equals(firstName)) && (person.getLastName().equals(lastName))) {
                message = person.getFullInfo();
            } else {
                message = "Personal info is incorrect";
            }
        }
        return message;
    }

    @Override
    public synchronized String createAccount(long passport, String accountSubId) {
        String message;
            if (!clients.get(passport).accounts.containsKey(accountSubId)) {
                clients.get(passport).accounts.put(accountSubId, 0);
                message = "Account " + clients.get(passport).getFullId(accountSubId) + " is created";
                log(message);
            } else {
                message = clients.get(passport).getFullId(accountSubId) +
                        " " +clients.get(passport).getBalance(accountSubId);
            }
        return message;
    }

    @Override
    public synchronized boolean isClient(long passport) {
        return clients.containsKey(passport);
    }

    @Override
    public synchronized LocalPerson getLocal(long passport) {
        RemotePerson person = clients.get(passport);
        return (person == null) ? null : new LocalPerson(person);
    }

    @Override
    public synchronized RemotePerson getRemote(long passport) {
        return clients.get(passport);
    }

    @Override
    public synchronized String increaseAmount(long passport, String subId, int amount) {
        RemotePerson person = getRemote(passport);
        String message1 = "";
        if (!person.accounts.containsKey(subId)) {
            message1 = createAccount(passport, subId) + "\n";
        }
        int current = person.accounts.get(subId);
        person.accounts.replace(subId, current + amount);
        String message2 = "Balance on " + person.getFullId(subId) + " is " + person.getBalance(subId);
        log(message2);
        return message1 + message2;
    }

    @Override
    public synchronized String decreaseAmount(long passport, String subId, int amount) {
        RemotePerson person = getRemote(passport);
        if (person.accounts.containsKey(subId)) {
            int current = person.accounts.get(subId);
            if (current < amount) {
                return "Not enough money on account";
            }
            person.accounts.replace(subId, current - amount);
            String message = "Balance on " + person.getFullId(subId) + " is " + person.getBalance(subId);
            log(message);
            return message;
        } else {
            return "No such account";
        }
    }
}
