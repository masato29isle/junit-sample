package com.example.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.app.model.AgeGroup;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * 発展課題1: {@link AgeGroupClassifierTest} の境界のテストを、パラメータ化テストで1つにまとめたもの。
 *
 * <p>テストの中身は同じで、入力と期待値だけが違うときに使う。
 */
class AgeGroupClassifierParameterizedTest {

    // @CsvSource の1行が1回分のテストになる。左から順にメソッドの引数（age, expected）に渡される
    @ParameterizedTest(name = "{0}歳は {1}")
    @CsvSource({
        "0, CHILD",
        "12, CHILD",
        "13, ADULT",
        "64, ADULT",
        "65, SENIOR",
        "100, SENIOR"
    })
    void classify_returnsExpectedGroup(int age, AgeGroup expected) {
        AgeGroupClassifier classifier = new AgeGroupClassifier();

        AgeGroup actual = classifier.classify(age);

        assertEquals(expected, actual);
    }
}
