package ru.ifmo.rain.usov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Account extends Remote {
    String getFullId(String subId) throws RemoteException;
    int getBalance(String subId) throws RemoteException;
    String getFullInfo() throws RemoteException;

}
