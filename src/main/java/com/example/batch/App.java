package com.example.batch;

public class App {
    public static void main(String[] args) {
        System.out.println("バッチ処理のテスト実行");

        // JobRunner / JobStep の簡易例
        JobRunner runner = new JobRunner();
        runner.run();
    }
}

class JobRunner {
    public void run() {
        System.out.println("JobRunner: バッチ開始");
        JobStep step1 = new JobStep("ステップ1");
        step1.execute();
        JobStep step2 = new JobStep("ステップ2");
        step2.execute();
        System.out.println("JobRunner: バッチ終了");
    }
}

class JobStep {
    private String name;

    public JobStep(String name) {
        this.name = name;
    }

    public void execute() {
        System.out.println("JobStep [" + name + "] 実行中...");
        // ここに処理を追加していく
    }
}
