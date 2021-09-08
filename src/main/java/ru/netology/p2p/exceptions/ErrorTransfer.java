package ru.netology.p2p.exceptions;

public class ErrorTransfer extends  RuntimeException {
    public ErrorTransfer(String msg) {
        super(msg);
    }
}