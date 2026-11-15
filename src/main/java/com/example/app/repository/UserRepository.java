package com.example.app.repository;

import com.example.app.model.User;

/** ユーザー情報の保存と検索を行う窓口（インターフェース）。 */
public interface UserRepository {

    /** 同じ名前のユーザーが登録済みなら true を返す。 */
    boolean existsByName(String name);

    /** ユーザーを保存する。 */
    void save(User user);
}
