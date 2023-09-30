package com.example;

public class Runner {

    /**
     * this fixes a bug where JavaFX doesn't like being
     * packaged if the main class extends another class
     * https://stackoverflow.com/a/71990054
     * @param args
     */
    public static void main(String[] args) {
        App.main(args);
    }
}
