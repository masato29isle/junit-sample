package com.example.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.app.model.AgeGroup;
import org.junit.jupiter.api.Test;

/** {@link AgeGroupClassifier} のテスト。 */
class AgeGroupClassifierTest {

    @Test
    void classify_age30_returnsAdult() {
        // 準備: テスト対象を用意する
        AgeGroupClassifier classifier = new AgeGroupClassifier();

        // 実行: テストしたいメソッドを呼ぶ
        AgeGroup actual = classifier.classify(30);

        // 検証: 結果が期待どおりか確かめる（期待値, 実際の値 の順）
        assertEquals(AgeGroup.ADULT, actual);
    }

    // TODO(Step 2): 10歳は CHILD になることを確かめるテストを書く

    // TODO(Step 3): 境界（12歳と13歳、64歳と65歳）のテストを書く

    // TODO(Step 4): 負の数を渡すと IllegalArgumentException になることを確かめるテストを書く
}
