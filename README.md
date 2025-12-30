# My Batch Project

簡単な説明
- シンプルなバッチ実行フレームワークと、実行履歴を表示する最小限の Web UI (Spring Boot + Thymeleaf) を提供します。

**Prerequisites**
- Java 17
- Maven

**Quick Start**
1. ビルド（テストをスキップする例）

```bash
mvn -DskipTests package
```

2. jar で起動

```bash
java -jar target/*.jar
```

または開発中は直接実行:

```bash
mvn -DskipTests spring-boot:run
```

ポートを変更する例（8081）:

```bash
mvn -DskipTests spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8081"
# または
SPRING_APPLICATION_JSON='{"server":{"port":8081}}' mvn -DskipTests spring-boot:run
```

**Web UI / API**
- ブラウザ: `http://localhost:8080/jobs` — 実行履歴を表形式で表示します。
- JSON API: `GET /api/job-executions` — 実行履歴を JSON で返します。

関連ソース:
- コントローラ（ページ）: [src/main/java/com/example/batch/controller/JobPageController.java](src/main/java/com/example/batch/controller/JobPageController.java)
- コントローラ（API）: [src/main/java/com/example/batch/controller/JobExecutionController.java](src/main/java/com/example/batch/controller/JobExecutionController.java)
- テンプレート: [src/main/resources/templates/jobs.html](src/main/resources/templates/jobs.html)

**データベース**
- 組み込み H2 を使用しています（ファイル: `./batchdb`）。JDBC URL: `jdbc:h2:./batchdb;AUTO_SERVER=TRUE`。
- 実行履歴テーブル: `job_execution`。テーブル作成・接続は [src/main/java/com/example/batch/DatabaseManager.java](src/main/java/com/example/batch/DatabaseManager.java) にあります。

**バッチの実行**
- 既存の CLI エントリポイントは [src/main/java/com/example/batch/App.java](src/main/java/com/example/batch/App.java) です。
- フレームワークの要点:
  - `JobRunner` が `JobStep` の一覧を実行します。
  - 1ジョブは 1 トランザクションで実行され、監査レコード（`job_execution`）はジョブトランザクションとは別の自動コミット接続で永続化される設計です（ロールバック時にも監査が残ります）。実装: [src/main/java/com/example/batch/JobRunner.java](src/main/java/com/example/batch/JobRunner.java)

**ビルド / テスト**
- ビルド: `mvn package`
- テスト: `mvn test`

**運用メモ**
- 開発実行時のログは `web-run.log` にリダイレクトしていることがあります。
- 起動中のプロセスを停止する例:

```bash
# 起動時に作成した PID ファイルがあれば
kill $(cat web-run.pid) || true
# またはプロセス名で停止
pkill -f 'spring-boot:run' || pkill -f 'java.*WebApplication'
```

**注意事項**
- プロジェクトはローカル開発向けの最小構成です。商用利用時は DB やセキュリティ、例外処理、ログローテーション等の追加対策が必要です。

---
ファイル参照や追加要望があれば教えてください。

**補足（協力ツールについて）**
- 本リポジトリの実装には、GitHub Copilot や GPT による提案を参考にした部分が含まれます。提案をもとに実装・編集を行いましたが、コードの一部は自動生成や補助的な提案に依存しているため、細かな実装意図や振る舞いをまだ把握しきれていない箇所があるかもしれません。
- 動作確認は行っていますが、重要な変更や本番運用前には、コードをよくレビューして理解したうえで調整・テストすることをおすすめします。
