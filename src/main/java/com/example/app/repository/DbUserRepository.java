package com.example.app.repository;

import com.example.app.model.User;
import org.springframework.stereotype.Repository;

/**
 * DB に保存する本番用の実装（という想定のクラス）。
 *
 * <p>このサンプルには DB がないので、呼ぶと必ず失敗する。「本物の Repository は DB がないと動かない」ことを表している。
 */
@Repository
public class DbUserRepository implements UserRepository {

    @Override
    public boolean existsByName(String name) {
        throw new IllegalStateException("DB に接続できません（このサンプルには DB がありません）");
    }

    @Override
    public void save(User user) {
        throw new IllegalStateException("DB に接続できません（このサンプルには DB がありません）");
    }
}
