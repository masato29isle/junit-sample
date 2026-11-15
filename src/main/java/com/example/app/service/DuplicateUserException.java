package com.example.app.service;

/** 同じ名前のユーザーが登録済みのときに投げる例外。 */
public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException(String name) {
        super("同じ名前のユーザーが登録済みです name=" + name);
    }
}
