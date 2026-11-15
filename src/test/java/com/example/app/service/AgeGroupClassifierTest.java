package com.example.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.app.model.AgeGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** {@link AgeGroupClassifier} のテスト。 */
class AgeGroupClassifierTest {

    @Test
    @DisplayName("30歳は ADULT になる")
    void classify_age30_returnsAdult() {
        // 準備: テスト対象を用意する
        AgeGroupClassifier classifier = new AgeGroupClassifier();

        // 実行: テストしたいメソッドを呼ぶ
        AgeGroup actual = classifier.classify(30);

        // 検証: 結果が期待どおりか確かめる（期待値, 実際の値 の順）
        assertEquals(AgeGroup.ADULT, actual);
    }

    // Step 2: 代表的な値のテスト
    @Test
    void classify_age10_returnsChild() {
        AgeGroupClassifier classifier = new AgeGroupClassifier();

        AgeGroup actual = classifier.classify(10);

        assertEquals(AgeGroup.CHILD, actual);
    }

    // Step 3: 境界のテスト。区分が切り替わる前後の値を確かめる
    @Test
    void classify_age12_returnsChild() {
        AgeGroupClassifier classifier = new AgeGroupClassifier();

        assertEquals(AgeGroup.CHILD, classifier.classify(12));
    }

    @Test
    void classify_age13_returnsAdult() {
        AgeGroupClassifier classifier = new AgeGroupClassifier();

        assertEquals(AgeGroup.ADULT, classifier.classify(13));
    }

    @Test
    void classify_age64_returnsAdult() {
        AgeGroupClassifier classifier = new AgeGroupClassifier();

        assertEquals(AgeGroup.ADULT, classifier.classify(64));
    }

    @Test
    void classify_age65_returnsSenior() {
        AgeGroupClassifier classifier = new AgeGroupClassifier();

        assertEquals(AgeGroup.SENIOR, classifier.classify(65));
    }

    // Step 4: 異常系のテスト。例外が投げられることを確かめる
    @Test
    void classify_negativeAge_throwsException() {
        AgeGroupClassifier classifier = new AgeGroupClassifier();

        assertThrows(IllegalArgumentException.class, () -> classifier.classify(-1));
    }
}
