package ru.ifmo.rain.usov.bank;

import java.io.Serializable;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocalPerson implements Serializable, PersonalInfo, Account {
    private String firstName;
    private String lastName;
    private long passport;
    private ConcurrentMap<String, Integer> accounts;


    public LocalPerson(RemotePerson person) {
        firstName = person.getFirstName();
        lastName = person.getLastName();
        passport = person.getPassport();
        accounts = new ConcurrentHashMap<>();
        for (String subId : person.accounts.keySet()) {
            this.accounts.put(subId, person.accounts.get(subId));
        }
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
