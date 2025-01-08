package com.example.group4planninggame.managers;

public class CrudSingleton {
    private static CrudSingleton instance;
    private Crud crud;

    private CrudSingleton() {
        crud = new Crud();
    }

    public static synchronized CrudSingleton getInstance() {
        if (instance == null) {
            instance = new CrudSingleton();
        }
        return instance;
    }

    public Crud getCrud() {
        return crud;
    }
}
