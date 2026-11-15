package com.example.app.service;

import com.example.app.model.User;
import com.example.app.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * 発展課題2: テスト用の偽物の Repository。DB の代わりに、メモリ上のリストに保存する。
 *
 * <p>src/test/java に置いているので、本番のコードには含まれない。
 */
class FakeUserRepository implements UserRepository {

    private final List<User> users = new ArrayList<>();

    @Override
    public boolean existsByName(String name) {
        return users.stream().anyMatch(user -> user.name().equals(name));
    }

    @Override
    public void save(User user) {
        users.add(user);
    }
}
