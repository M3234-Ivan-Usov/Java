package ru.ifmo.rain.usov.bank;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    public static void main(String[] args) {
        try {
            final BankOperations sberbank = new Bank(0);
            final Registry registry = LocateRegistry.createRegistry(1090);
            Remote stub = UnicastRemoteObject.exportObject(sberbank, 0);
            registry.bind("//localhost/bank", stub);
            System.out.println("Server is ready");
        } catch (RemoteException | AlreadyBoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
