package backend.academy.scrapper;

import backend.academy.scrapper.dto.GitHubIssueResponse;
import backend.academy.scrapper.dto.GitHubPullRequestResponse;
import backend.academy.scrapper.dto.GitHubUser;
import backend.academy.scrapper.dto.StackOverflowAnswerResponse;
import backend.academy.scrapper.dto.StackOverflowCommentResponse;
import backend.academy.scrapper.dto.StackOverflowUser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты на превью пользовательских сообщений в зависимости от типа обновления")
class LinkCheckSchedulerMessageFormatTest {

    private final LinkCheckScheduler scheduler = new LinkCheckScheduler(
        null, null, null, null, null
    );

    @Test
    @Disabled("Проверка на корректное форматирование Issue")
    void shouldFormatGitHubIssueMessage() {
        // Arrange
        GitHubIssueResponse issue = new GitHubIssueResponse(
            123L,
            "Test issue",
            new GitHubUser("testUser"),
            "2023-01-01T00:00:00Z",
            "This is a test issue body",
            "open",
            "https://github.com/test/repo/issues/123",
            null
        );

        // Act
        String message = scheduler.formatGitHubMessage(
            "Issue",
            issue.title(),
            issue.user().login(),
            issue.updatedAt(),
            issue.body(),
            issue.state(),
            issue.htmlUrl()
        );

        // Assert
        assertThat(message).isEqualTo(
            "[Issue] Test issue\n" +
            "Автор: testUser\n" +
            "Дата: 2023-01-01T00:00:00Z\n" +
            "Статус: open\n" +
            "Ссылка: https://github.com/test/repo/issues/123\n" +
            "Описание: This is a test issue body"
        );
    }

    @Test
    @DisplayName("Проверка на корректное форматирование Pull Request")
    void shouldFormatGitHubPullRequestMessage() {
        // Arrange
        GitHubPullRequestResponse pr = new GitHubPullRequestResponse(
            456L,
            "Feature PR",
            new GitHubUser("pruser"),
            "2023-01-02T00:00:00Z",
            "This is a test PR description",
            "open",
            "https://github.com/test/repo/pull/456"
        );

        // Act
        String message = scheduler.formatGitHubMessage(
            "Pull Request",
            pr.title(),
            pr.user().login(),
            pr.updatedAt(),
            pr.body(),
            pr.state(),
            pr.htmlUrl()
        );

        // Assert
        assertThat(message).isEqualTo(
            "[Pull Request] Feature PR\n" +
            "Автор: pruser\n" +
            "Дата: 2023-01-02T00:00:00Z\n" +
            "Статус: open\n" +
            "Ссылка: https://github.com/test/repo/pull/456\n" +
            "Описание: This is a test PR description"
        );
    }

    @Test
    @DisplayName("Проверка на корректное форматирование ответа Stack Overflow")
    void shouldFormatStackOverflowAnswerMessage() {
        // Arrange
        StackOverflowAnswerResponse answer = new StackOverflowAnswerResponse(
            "Here is how you can test this functionality",
            new StackOverflowUser("answerUser"),
            "2023-01-03T00:00:00Z",
            "How to test?"
        );

        // Act
        String message = scheduler.formatStackOverflowMessage(
            "Ответ",
            answer.questionTitle(),
            answer.owner().displayName(),
            answer.creationDate(),
            answer.body()
        );

        // Assert
        assertThat(message).isEqualTo(
            "[Ответ] Вопрос: How to test?\n" +
            "Автор: answerUser\n" +
            "Дата: 2023-01-03T00:00:00Z\n" +
            "Превью: Here is how you can test this functionality"
        );
    }

    @Test
    @DisplayName("Проверка на корректное форматирование комментария Stack Overflow")
    void shouldFormatStackOverflowCommentMessage() {
        // Arrange
        StackOverflowCommentResponse comment = new StackOverflowCommentResponse(
            "Have you tried this approach?",
            new StackOverflowUser("commentuser"),
            "2023-01-04T00:00:00Z",
            "Testing question"
        );

        // Act
        String message = scheduler.formatStackOverflowMessage(
            "Комментарий",
            comment.questionTitle(),
            comment.owner().displayName(),
            comment.creationDate(),
            comment.body()
        );

        // Assert
        assertThat(message).isEqualTo(
            "[Комментарий] Вопрос: Testing question\n" +
            "Автор: commentuser\n" +
            "Дата: 2023-01-04T00:00:00Z\n" +
            "Превью: Have you tried this approach?"
        );
    }

    @Test
    @DisplayName("Проверка на обрезание длинного тела сообщения")
    void shouldTruncateLongMessageBody() {
        // Arrange
        String longBody = "This is a very long message body that should be truncated " +
            "to 200 characters maximum length as per the implementation. " +
            "Let's make sure it works correctly by providing a string that is " +
            "definitely longer than 200 characters in total length.";

        // Act
        String message = scheduler.formatGitHubMessage(
            "Issue",
            "Test",
            "user",
            Instant.now().toString(),
            longBody,
            "open",
            "http://example.com"
        );

        // Assert
        assertThat(message).contains(longBody.substring(0, 200));
        assertThat(message).doesNotContain(longBody.substring(201));
    }

    @Test
    @DisplayName("Проверка на форматирование некорректного тела сообщения")
    void shouldHandleNullFieldsInMessageFormatting() {
        // Act
        String message = scheduler.formatGitHubMessage(
            "Issue",
            null,
            null,
            null,
            null,
            null,
            null
        );

        // Assert
        assertThat(message).contains("[Issue] null");
        assertThat(message).contains("Автор: null");
        assertThat(message).contains("Без описания");
    }
}
