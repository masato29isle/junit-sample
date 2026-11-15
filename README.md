# junit-sample

社内勉強会「JUnit でユニットテストを書く」のハンズオン用サンプルプロジェクトです。
Controller / Service / Repository の3層構造のユーザー登録処理で、年齢から年齢区分（CHILD / ADULT / SENIOR）を判定するクラスに **バグが1つ** 入っています。
テストを書いてそのバグを見つけ、直すところまでを体験します。

各クラスには Spring の `@RestController` / `@Service` / `@Repository` を付けていますが、
**Spring Boot ではないのでアプリとしては起動しません**。業務コードと同じ見た目にするための目印です。
テストは Spring を使わず、素の Java と JUnit 5 だけで書きます。

## ハンズオン手順

手順は [docs/handson.md](docs/handson.md) を参照してください。

## ブランチ

| ブランチ | 内容 |
| --- | --- |
| `main` | 開始状態。テストは1本だけで、`AgeGroupClassifier` にバグが1つ入っている |
| `solution` | 完成状態。本編のテスト（代表値、境界値、異常系）とバグの修正に加え、発展課題1（`@ParameterizedTest`）と発展課題2（手書きの偽 Repository を使った `UserService` のテスト）まで入れたもの |

ハンズオンでは `main` から始めます。詰まったときは `solution` ブランチを参照してください。

```bash
# 開始状態との差分を確認する
git diff main solution

# 特定のファイルだけ完成状態を取り出す（例: テストクラス）
git checkout solution -- src/test/java/com/example/app/service/AgeGroupClassifierTest.java
```

## 前提環境

- JDK 17 以上（`java -version` で確認）
- Maven 3.9.x（`mvn -v` で確認）
- 任意: IntelliJ IDEA や VS Code などのエディタ

## 始め方

```bash
git clone https://github.com/masato29isle/junit-sample.git
cd junit-sample
mvn test
```

`Tests run: 1, Failures: 0, Errors: 0, Skipped: 0` と `BUILD SUCCESS` が出れば準備完了です。
依存ライブラリのダウンロードに時間がかかるため、勉強会の前に一度実行しておいてください。

## 構成

```
pom.xml                                      Maven の設定（JUnit 5）
docs/handson.md                              ハンズオン手順
src/main/java/com/example/app
├── controller/UserController.java           @RestController  リクエストを受け付ける
├── service/UserService.java                 @Service         ユーザー登録（重複チェック、年齢区分の判定、保存）
├── service/AgeGroupClassifier.java          年齢から年齢区分を判定する。今日のテスト対象
├── service/DuplicateUserException.java      重複時の例外
├── repository/UserRepository.java           保存と検索の窓口（インターフェース）
├── repository/DbUserRepository.java         @Repository      本番用の実装という想定。DB がないので呼ぶと失敗する
├── model/User.java                          登録されたユーザー（record）
└── model/AgeGroup.java                      年齢区分（enum）
src/test/java/com/example/app
└── service/AgeGroupClassifierTest.java      テストクラス。ここにテストを書き足す
```

ビルド成果物（`target/`）や IDE の設定ファイルは `.gitignore` で除外しています。
