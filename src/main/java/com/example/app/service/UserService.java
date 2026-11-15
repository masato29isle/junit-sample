package com.example.app.service;

import com.example.app.model.AgeGroup;
import com.example.app.model.User;
import com.example.app.repository.UserRepository;
import org.springframework.stereotype.Service;

/** ユーザーに関する業務ロジックを担うクラス。 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AgeGroupClassifier ageGroupClassifier;

    // 使う部品（依存）はコンストラクタで外から受け取る。テストでは偽物を渡せる（発展課題2）
    public UserService(UserRepository userRepository, AgeGroupClassifier ageGroupClassifier) {
        this.userRepository = userRepository;
        this.ageGroupClassifier = ageGroupClassifier;
    }

    /**
     * ユーザーを登録する。
     *
     * @return 登録したユーザー（年齢区分を判定済み）
     * @throws DuplicateUserException 同じ名前のユーザーが登録済みのとき
     */
    public User register(String name, int age) {
        if (userRepository.existsByName(name)) {
            throw new DuplicateUserException(name);
        }
        AgeGroup ageGroup = ageGroupClassifier.classify(age);
        User user = new User(name, age, ageGroup);
        userRepository.save(user);
        return user;
    }
}
