package ru.netology.p2p.exceptions;

public class ErrorInputData extends RuntimeException {
    public ErrorInputData(String msg) {
        super(msg);
    }
}