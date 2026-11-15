package com.example.app.model;

/** 登録されたユーザー。 */
public record User(String name, int age, AgeGroup ageGroup) {
}
