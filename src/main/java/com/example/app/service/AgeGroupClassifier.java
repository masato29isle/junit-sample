package com.example.app.service;

import com.example.app.model.AgeGroup;

/**
 * 年齢から年齢区分を判定するクラス。
 *
 * <p>仕様は次のとおり。
 *
 * <ul>
 *   <li>0〜12歳: {@link AgeGroup#CHILD}
 *   <li>13〜64歳: {@link AgeGroup#ADULT}
 *   <li>65歳以上: {@link AgeGroup#SENIOR}
 *   <li>負の数: {@link IllegalArgumentException} を投げる
 * </ul>
 *
 * <p>DB や他のクラスに依存しない、計算だけのクラス。こういうクラスがいちばんテストしやすい。
 */
public class AgeGroupClassifier {

    public AgeGroup classify(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("年齢は0以上で指定してください age=" + age);
        }
        if (age <= 12) {
            return AgeGroup.CHILD;
        }
        if (age > 65) {
            return AgeGroup.SENIOR;
        }
        return AgeGroup.ADULT;
    }
}
