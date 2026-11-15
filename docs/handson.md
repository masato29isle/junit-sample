# ハンズオン: JUnit でユニットテストを書く

所要時間の目安: 約 15 分（発展課題を除く）

## このハンズオンでやること

年齢から年齢区分（CHILD / ADULT / SENIOR）を判定するクラスに対して、JUnit 5 でテストを書きます。
このクラスには **バグが1つ** 入っています。テストを書き足していくと途中でテストが失敗する（赤になる）ので、失敗メッセージを読んでバグを直し、テストを成功（緑）に戻します。

完了すると次の状態になります。

- テストコードの基本の形（準備 → 実行 → 検証）を読んで、自分でも1本書ける
- テストが失敗したとき、メッセージから「期待した値」と「実際の値」を読み取れる
- 「どの値でテストするか」を、境界（区分が切り替わる値）を目印に選べる

Spring の知識は不要です。テストは素の Java と JUnit 5 だけで書きます。

## 前提環境

作業を始める前に、以下を確認してください。

| 項目 | 要件 | 確認コマンド |
| --- | --- | --- |
| JDK | 17 以上 | `java -version` |
| ビルドツール | Maven 3.9.x | `mvn -v` |
| Git | 任意のバージョン | `git --version` |
| エディタ | IntelliJ IDEA / VS Code など任意 | - |
| ネットワーク | Maven Central に接続できること（依存のダウンロード用） | - |

サンプルプロジェクトは GitHub の [masato29isle/junit-sample](https://github.com/masato29isle/junit-sample) にあります。
事前に clone して、次のコマンドでテストが1本成功することを確認しておいてください。

```bash
git clone https://github.com/masato29isle/junit-sample.git
cd junit-sample
mvn test
```

**期待される結果**: `Tests run: 1, Failures: 0, Errors: 0, Skipped: 0` と `BUILD SUCCESS` が出る

`main` ブランチが開始状態、`solution` ブランチが完成状態です。途中で詰まったら、完成状態のファイルを取り出して見比べてください。

```bash
# 開始状態との差分を確認する
git diff main solution

# 特定のファイルだけ完成状態を取り出す（例: テストクラス）
git checkout solution -- src/test/java/com/example/app/service/AgeGroupClassifierTest.java
```

> このハンズオンでは API キーや外部サービス、DB は使いません。

## サンプルの構成

ユーザー登録を行う3層構造のサンプルです。今日テストするのは、その中の `AgeGroupClassifier` です。

```
UserController.register()          @RestController  リクエストを受け付ける
 └─ UserService.register()         @Service         重複チェックをして、年齢区分を判定し、保存する
     ├─ AgeGroupClassifier         年齢から年齢区分を判定する ← 今日のテスト対象
     └─ UserRepository             保存と検索の窓口。本物は DB が必要（発展課題2で扱う）
```

`AgeGroupClassifier.classify(int age)` の仕様は次のとおりです。

| 年齢 | 結果 |
| --- | --- |
| 0〜12歳 | `CHILD` |
| 13〜64歳 | `ADULT` |
| 65歳以上 | `SENIOR` |
| 負の数 | `IllegalArgumentException` を投げる |

`AgeGroupClassifier` は DB にも他のクラスにも依存せず、渡された値から結果を計算するだけのクラスです。こういうクラスがいちばんテストしやすいので、最初の1本に向いています。
Spring のアノテーションは「そのクラスの役割を示す目印」です。Spring Boot として起動しないので、DI や設定の知識は不要です。

テストに関係するファイルは2つです。

| ファイル | 役割 |
| --- | --- |
| `src/main/java/com/example/app/service/AgeGroupClassifier.java` | テストされる側（プロダクトコード） |
| `src/test/java/com/example/app/service/AgeGroupClassifierTest.java` | テストする側（テストコード） |

Maven では、プロダクトコードを `src/main/java`、テストコードを `src/test/java` に置きます。パッケージを同じにして、クラス名の末尾に `Test` を付けるのが慣習です。

## 手順

### Step 1: テストを実行して、テストコードを読む（3分）

まず開始状態のままテストを実行します。

```bash
mvn test
```

**期待される結果**: テストが1本実行されて成功する

```
[INFO] Running com.example.app.service.AgeGroupClassifierTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.029 s -- in com.example.app.service.AgeGroupClassifierTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

次に `src/test/java/com/example/app/service/AgeGroupClassifierTest.java` を開いて、すでに書いてある1本を読みます。

```java
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
```

読むポイントは3つです。

- **`@Test` を付けたメソッド1つが、テスト1本** です。`main` メソッドは要りません。JUnit が `@Test` の付いたメソッドを見つけて実行します
- 中身は **準備 → 実行 → 検証** の3段です。「30歳を渡したら（準備・実行）、ADULT が返る（検証）」と読めます
- `assertEquals(期待値, 実際の値)` は、2つが等しければ何も起きず（成功）、違えばテストを失敗させます

メソッド名は `対象_条件_期待する結果` の形にしています（`classify` に `age30` を渡すと `returnsAdult`）。名前だけで何を確かめているテストかが分かるようにするためです。

`@DisplayName` は、テストに付ける表示名です。IntelliJ IDEA や VS Code でテストを実行すると、結果の一覧にメソッド名の代わりにこの名前が出ます。
`mvn test` のコンソール出力には表示名ではなくメソッド名が出るので、このハンズオンで書き足すテストには付けなくて構いません。

**期待される結果**: 「このテストは何を確かめているか」を一言で説明できる

### Step 2: テストを1本書く（4分）

「10歳は CHILD になる」ことを確かめるテストを書きます。
`AgeGroupClassifierTest.java` の `// TODO(Step 2)` のコメントの下に、次のメソッドを書き足してください。

```java
    @Test
    void classify_age10_returnsChild() {
        AgeGroupClassifier classifier = new AgeGroupClassifier();

        AgeGroup actual = classifier.classify(10);

        assertEquals(AgeGroup.CHILD, actual);
    }
```

すでにある1本をコピーして、メソッド名、渡す値、期待値の3箇所を変えるのが早道です。

```bash
mvn test
```

**期待される結果**: `Tests run: 2, Failures: 0, Errors: 0, Skipped: 0` と `BUILD SUCCESS` が出る（実行数が 1 から 2 に増える）

実行数が増えていない場合は、`@Test` を付け忘れていないか確認してください。
IntelliJ IDEA や VS Code では、テストメソッドの左に出る緑の実行ボタンからも実行できます。

### Step 3: 境界のテストを書いて、バグを見つける（6分）

ここまでの 30歳 と 10歳 は、区分の真ん中あたりの値です。
バグが入りやすいのは **区分が切り替わる境界** です（`<` と `<=` の書き間違いなど）。仕様の表から、切り替わる前後の値を拾います。

| 境界 | テストする値 |
| --- | --- |
| CHILD と ADULT の境界 | 12歳は `CHILD`、13歳は `ADULT` |
| ADULT と SENIOR の境界 | 64歳は `ADULT`、65歳は `SENIOR` |

`// TODO(Step 3)` のコメントの下に、4本のテストを書き足します。

```java
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
```

実行と検証を1行にまとめて書いています。Step 2 の書き方と意味は同じです。

```bash
mvn test
```

**期待される結果**: 6本中1本が失敗し、`BUILD FAILURE` になる

```
[ERROR] Tests run: 6, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 0.047 s <<< FAILURE! -- in com.example.app.service.AgeGroupClassifierTest
[ERROR] com.example.app.service.AgeGroupClassifierTest.classify_age65_returnsSenior -- Time elapsed: 0.005 s <<< FAILURE!
org.opentest4j.AssertionFailedError: expected: <SENIOR> but was: <ADULT>
	at org.junit.jupiter.api.AssertionFailureBuilder.build(AssertionFailureBuilder.java:151)
	...
	at com.example.app.service.AgeGroupClassifierTest.classify_age65_returnsSenior(AgeGroupClassifierTest.java:61)
	...

[ERROR] Failures:
[ERROR]   AgeGroupClassifierTest.classify_age65_returnsSenior:61 expected: <SENIOR> but was: <ADULT>
[INFO]
[ERROR] Tests run: 6, Failures: 1, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD FAILURE
```

（行番号の `61` は、テストを書いた位置によって変わります。）

出力は長いですが、読むのは `Failures:` の下の **1行** だけで足ります。

```
AgeGroupClassifierTest.classify_age65_returnsSenior:61 expected: <SENIOR> but was: <ADULT>
```

- **どのテストが**: `classify_age65_returnsSenior`（65歳を渡すテスト）
- **期待した値**: `expected: <SENIOR>`
- **実際の値**: `but was: <ADULT>`

つまり「65歳は SENIOR のはずなのに、ADULT が返ってきた」です。仕様は「65歳以上は SENIOR」なので、テストが正しく、プロダクトコードが間違っています。

`src/main/java/com/example/app/service/AgeGroupClassifier.java` を開いて、原因を探して直してください。

直す前:

```java
        if (age > 65) {
            return AgeGroup.SENIOR;
        }
```

直した後:

```java
        if (age >= 65) {
            return AgeGroup.SENIOR;
        }
```

`>` は「65 より大きい」なので、65歳ちょうどが SENIOR に入っていませんでした。

```bash
mvn test
```

**期待される結果**: `Tests run: 6, Failures: 0, Errors: 0, Skipped: 0` と `BUILD SUCCESS` が出る

ここで押さえておきたい点です。

- 30歳 と 10歳 のテストだけでは、このバグは見つかりませんでした。**テストは「どの値を選ぶか」で価値が決まります**
- 直した後にもう一度全部のテストを流すことで、「65歳を直したせいで 64歳 が壊れた」といった巻き添えがないことも同時に確かめられています

### Step 4: 例外が投げられることをテストする（2分）

最後に異常系です。仕様の「負の数は `IllegalArgumentException` を投げる」を確かめます。
`// TODO(Step 4)` のコメントの下に、次のメソッドを書き足してください。

```java
    @Test
    void classify_negativeAge_throwsException() {
        AgeGroupClassifier classifier = new AgeGroupClassifier();

        assertThrows(IllegalArgumentException.class, () -> classifier.classify(-1));
    }
```

ファイル先頭の import に、次の1行も足します。

```java
import static org.junit.jupiter.api.Assertions.assertThrows;
```

`assertThrows(期待する例外の型, 実行する処理)` は、処理を実行して **その例外が投げられたら成功**、投げられなければ失敗になります。
`() -> classifier.classify(-1)` はラムダ式で、「この処理を、今ではなく `assertThrows` の中で実行してほしい」と処理そのものを渡す書き方です。

```bash
mvn test
```

**期待される結果**: `Tests run: 7, Failures: 0, Errors: 0, Skipped: 0` と `BUILD SUCCESS` が出る

余裕があれば、`-1` を `0` に変えて実行してみてください。0歳は例外にならないので、次のように失敗します。確認したら `-1` に戻します。

```
Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.
```

本編はここまでです。`solution` ブランチと差分を取ると、完成状態と見比べられます（`solution` には発展課題の分も入っています）。

```bash
git diff main solution
```

### 発展課題1: @ParameterizedTest で境界のテストをまとめる

本編が早く終わった人向けです。
Step 3 で書いた4本は、渡す値と期待値が違うだけで中身は同じでした。**パラメータ化テスト** を使うと、テストメソッドを1つにして、値の組だけを並べて書けます。

`src/test/java/com/example/app/service/` に `AgeGroupClassifierParameterizedTest.java` を新しく作り、次の内容を書きます。

```java
package com.example.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.app.model.AgeGroup;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
```

- `@Test` の代わりに `@ParameterizedTest` を付けます。`@CsvSource` の1行ごとに、テストメソッドが1回ずつ実行されます
- `"65, SENIOR"` の文字列は、引数の型（`int` と `AgeGroup`）に合わせて JUnit が変換します
- `name = "{0}歳は {1}"` は、IDE のテスト結果や失敗時のメッセージに出る表示名です（`{0}` が1番目の引数）

```bash
mvn test
```

**期待される結果**: `AgeGroupClassifierParameterizedTest` が `Tests run: 6` で成功する（`@CsvSource` の6行が6回分として数えられる）

仕様の表とテストの値の並びが同じ形になるので、「仕様のどこをテストしたか」が一目で分かるのが利点です。
境界を1つ足したいときも、1行足すだけで済みます。

### 発展課題2: 手書きの偽 Repository を渡して UserService をテストする

`AgeGroupClassifier` は何にも依存していないので、`new` してすぐテストできました。
では、Repository（DB）に依存している `UserService` はどうでしょうか。まず、本物の Repository を渡すテストを書いてみます。

`src/test/java/com/example/app/service/` に `UserServiceTest.java` を作ります。

```java
package com.example.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.app.model.AgeGroup;
import com.example.app.model.User;
import com.example.app.repository.DbUserRepository;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    @Test
    void register_newUser_savesUserWithAgeGroup() {
        UserService service = new UserService(new DbUserRepository(), new AgeGroupClassifier());

        User user = service.register("alice", 30);

        assertEquals(AgeGroup.ADULT, user.ageGroup());
    }
}
```

```bash
mvn test
```

**期待される結果**: `UserServiceTest` が失敗する（`Failures` ではなく `Errors` に数えられる）

```
[ERROR] Errors:
[ERROR]   UserServiceTest.register_newUser_savesUserWithAgeGroup:16 » IllegalState DB に接続できません（このサンプルには DB がありません）
```

`UserService` のロジックを確かめたいだけなのに、DB がないとテストが動きません。
（`Failures` は「検証で値が違った」、`Errors` は「検証にたどり着く前に想定外の例外が起きた」という違いです。）

そこで、**DB の代わりにメモリ上で動く偽物の Repository** を自分で書いて渡します。
`UserService` は Repository をコンストラクタで外から受け取る作りになっているので、テストのときだけ偽物に差し替えられます。

同じディレクトリに `FakeUserRepository.java` を作ります。

```java
package com.example.app.service;

import com.example.app.model.User;
import com.example.app.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;

/** テスト用の偽物の Repository。DB の代わりに、メモリ上のリストに保存する。 */
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
```

`UserServiceTest.java` を、偽物を渡す形に書き換えます。

```java
package com.example.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.app.model.AgeGroup;
import com.example.app.model.User;
import org.junit.jupiter.api.Test;

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
```

```bash
mvn test
```

**期待される結果**: `UserServiceTest` が `Tests run: 2` で成功する。DB がなくても、「重複していたら例外」「年齢区分を判定して保存する」というロジックを確かめられた

ここで見せたいのは、次の2点です。

- **依存を外から渡せる作りになっていると、テストしやすい**。もし `UserService` の中で `new DbUserRepository()` していたら、偽物に差し替える方法がありません
- 偽物にするのは DB のように「テストで動かしにくいもの」だけです。`AgeGroupClassifier` のように依存のないクラスは、本物をそのまま使います

この課題は次回以降の予告でもあります。

- 今日は `new UserService(repository, ...)` と **自分の手で** 部品を渡しました。この受け渡しをフレームワークが代わりに行う仕組みが、Spring の DI です
- 今日は偽物のクラスを **自分で書きました**。Repository のメソッドが増えると手書きは大変になります。偽物を自動で作ってくれるライブラリが Mockito です

## トラブルシューティング

| 症状 | 原因 | 対処 |
| --- | --- | --- |
| テストを書き足したのに `Tests run` の数が増えない | メソッドに `@Test` を付け忘れている | `@Test` を付ける。付いていないメソッドは実行されない |
| 新しく作ったテストクラスが実行されない | クラス名が `Test` で終わっていない | `mvn test` は既定で `Test` で始まるか `Test` / `Tests` / `TestCase` で終わるクラスだけを実行する。クラス名とファイル名を `〜Test` にする |
| `cannot find symbol` で `assertThrows` や `assertEquals` が見つからない | `import static` を書き忘れている | `import static org.junit.jupiter.api.Assertions.assertThrows;` のように足す |
| `package org.junit.jupiter.api does not exist` | テストクラスを `src/main/java` に作っている | `src/test/java` の下に移す。JUnit は `src/test/java` のコードからだけ使える設定になっている |
| `expected` と `but was` が逆に見える | `assertEquals` の引数の順番が逆 | `assertEquals(期待値, 実際の値)` の順にする。逆でも成功・失敗の判定は変わらないが、失敗メッセージが読みにくくなる |
| Step 3 でテストが失敗しない | 65歳のテストを書いていない、または先にプロダクトコードを直している | `classify_age65_returnsSenior` があるか確認する。失敗を見たい場合は `git checkout main -- src/main` でプロダクトコードを開始状態に戻す |
| 1つのテストクラスだけ実行したい | - | `mvn test -Dtest=AgeGroupClassifierTest`。PowerShell では `"-Dtest=AgeGroupClassifierTest"` のように引用符で囲む |
| `BUILD FAILURE` だが `Tests run` の行が出ない | テストの実行前にコンパイルエラーで止まっている | `[ERROR]` の行にファイル名と行番号が出るので、その行を直す |
| 日本語が文字化けする（Windows） | コンソールの文字コードが UTF-8 でない | 実行前に `chcp 65001` を実行する（**要確認**。直らない場合は Windows Terminal を使う） |

## 片付け

なし。

## 参考資料

- [JUnit 5 User Guide](https://docs.junit.org/5.14.4/overview.html): 公式ドキュメント。「Writing Tests」の章に `@Test`、アサーション、`@ParameterizedTest` の説明がある
- [JUnit 5 User Guide: Parameterized Classes and Tests](https://docs.junit.org/5.14.4/writing-tests/parameterized-classes-and-tests.html): `@CsvSource` 以外の値の渡し方（`@ValueSource`、`@MethodSource` など）の一覧
- [Maven Surefire Plugin: Inclusions and Exclusions of Tests](https://maven.apache.org/surefire/maven-surefire-plugin/examples/inclusion-exclusion.html): `mvn test` がどのクラスをテストとして実行するかの決まり
