package ru.ifmo.rain.usov.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RemotePerson extends UnicastRemoteObject implements Account, PersonalInfo {
    private String firstName;
    private String lastName;
    private long passport;
    ConcurrentMap<String, Integer> accounts;

    RemotePerson(String firstName, String lastName, long passport, int port) throws RemoteException {
        super(port);
        this.firstName = firstName;
        this.lastName = lastName;
        this.passport = passport;
        this.accounts = new ConcurrentHashMap<>();
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public long getPassport() {
        return passport;
    }


    @Override
    public String getFullId(String subId) {
        return passport + ":" + subId;
    }

    @Override
    public int getBalance(String subId) {
        return accounts.getOrDefault(subId, 0);
    }

    @Override
    public String getFullInfo() {
        if (accounts.keySet().size() == 0) {
            return "Does not hold any account";
        }
        StringBuilder message = new StringBuilder();
        TreeSet<String> ordered = new TreeSet<>(accounts.keySet());
        for (String subId : ordered) {
            message.append(getFullId(subId)).append(" ").append(getBalance(subId)).append("\n");
        }
        return message.toString();
    }
}

