package ru.ifmo.rain.usov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PersonalInfo extends Remote {
    String getFirstName() throws RemoteException;
    String getLastName() throws RemoteException;
    long getPassport() throws RemoteException;
}
