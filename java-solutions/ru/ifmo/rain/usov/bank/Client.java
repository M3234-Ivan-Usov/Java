package ru.ifmo.rain.usov.bank;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void confirm(String message) {
        System.out.println(message);
    }

    public static void main(String[] args) {
        if ((args.length < 3) || (args.length > 5)) {
            System.out.println("Invalid args. Correct usage:");
            System.out.println("firstname lastname passport [accountId] [money]");
            return;
        }
        long passport = Long.parseLong(args[2]);
        try {
            final Registry registry = LocateRegistry.getRegistry(1090);
            BankOperations sberbank = (BankOperations) registry.lookup("//localhost/bank");
            if (args.length == 3) {
                confirm(sberbank.createPerson(args[0], args[1], passport));
            }
            if (args.length == 4) {
                if(!sberbank.isClient(passport)) {
                    confirm(sberbank.createPerson(args[0], args[1], passport));
                }
                confirm(sberbank.createAccount(passport, args[3]));
            }
            if (args.length == 5) {
                if (!sberbank.isClient(passport)) {
                    confirm(sberbank.createPerson(args[0], args[1], passport));
                }
                int amount = Integer.parseInt(args[4]);
                if (amount >= 0) {
                    confirm(sberbank.increaseAmount(passport, args[3], amount));
                } else {
                    confirm(sberbank.decreaseAmount(passport, args[3], -amount));
                }
            }
        } catch (NotBoundException | RemoteException e) {
            System.out.println(e.getMessage());
        }
    }
}
