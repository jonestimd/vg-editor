package io.github.jonestimd.vgeditor;

import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class JavaFxThreadingRule implements TestRule {
    private static boolean isFxInitialized;

    @Override
    public Statement apply(Statement statement, Description description) {
        return new JavaFxStatement(statement);
    }

    private static class JavaFxStatement extends Statement {
        private final Statement statement;

        public JavaFxStatement(Statement statement) {
            this.statement = statement;
        }

        private Throwable throwable = null;

        @Override
        public void evaluate() throws Throwable {
            if (!isFxInitialized) {
                startJavaFX();
                isFxInitialized = true;
            }

            final CountDownLatch countDownLatch = new CountDownLatch(1);

            Platform.runLater(() -> {
                try {
                    statement.evaluate();
                } catch (Throwable e) {
                    throwable = e;
                }
                countDownLatch.countDown();
            });
            countDownLatch.await();

            if (throwable != null) throw throwable;
        }

        protected void startJavaFX() throws InterruptedException {
            final CountDownLatch latch = new CountDownLatch(1);

            SwingUtilities.invokeLater(() -> {
                new JFXPanel();
                latch.countDown();
            });

            latch.await();
        }
    }
}