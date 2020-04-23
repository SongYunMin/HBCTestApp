package com.example.hbctestapp;

public class Counter {
    private static int _cnt = 0;

    public static void AddOne() {_cnt++;}
    public static int GetCurrentCout() {return _cnt;}
}
