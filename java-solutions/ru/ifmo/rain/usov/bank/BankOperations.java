package ru.ifmo.rain.usov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BankOperations extends Remote {
    String createPerson(String firstName, String lastName, long passport) throws RemoteException;
    String createAccount(long passport, String accountSubId) throws RemoteException;
    boolean isClient(long passport) throws RemoteException;
    LocalPerson getLocal(long passport) throws  RemoteException;
    RemotePerson getRemote(long passport) throws RemoteException;
    String increaseAmount(long passport, String subId, int amount) throws RemoteException;
    String decreaseAmount(long passport, String subId, int amount) throws RemoteException;
}
