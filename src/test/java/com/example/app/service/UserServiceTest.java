package com.example.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.app.model.AgeGroup;
import com.example.app.model.User;
import org.junit.jupiter.api.Test;

/**
 * 発展課題2: {@link UserService} のテスト。
 *
 * <p>UserService は Repository（DB）に依存している。本物の代わりに、手書きの偽物を new で渡してテストする。
 */
class UserServiceTest {

    @Test
    void register_newUser_savesUserWithAgeGroup() {
        // 準備: 偽物の Repository を渡して UserService を組み立てる。依存のない AgeGroupClassifier は本物でよい
        FakeUserRepository repository = new FakeUserRepository();
        UserService service = new UserService(repository, new AgeGroupClassifier());

        // 実行
        User user = service.register("alice", 30);

        // 検証: 戻り値と、Repository に保存されたこと
        assertEquals("alice", user.name());
        assertEquals(AgeGroup.ADULT, user.ageGroup());
        assertTrue(repository.existsByName("alice"));
    }

    @Test
    void register_duplicateName_throwsException() {
        FakeUserRepository repository = new FakeUserRepository();
        UserService service = new UserService(repository, new AgeGroupClassifier());
        service.register("alice", 30);

        assertThrows(DuplicateUserException.class, () -> service.register("alice", 70));
    }
}
