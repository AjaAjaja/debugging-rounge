package ajaajaja.debugging_rounge.feature.question.application;

import ajaajaja.debugging_rounge.feature.question.domain.Question;
import ajaajaja.debugging_rounge.feature.user.domain.model.User;
import ajaajaja.debugging_rounge.support.SpringBootIntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@DisplayName("질문 삭제 통합 테스트 - S3 이미지 비동기 삭제")
class QuestionDeleteIntegrationTest extends SpringBootIntegrationTestSupport {

    @Autowired
    QuestionFacade questionFacade;

    @Nested
    @DisplayName("이미지가 있는 질문 삭제")
    class DeleteQuestionWithImages {

        @Test
        @DisplayName("트랜잭션 커밋 후 S3 이미지 비동기 삭제가 실행된다")
        void deleteQuestion_WithImages_ShouldTriggerAsyncS3Delete() {
            // given
            User author = createUser("author@test.com");
            Question question = createQuestion("테스트 질문", "내용", author.getId());
            List<String> imageUrls = List.of(
                    "https://s3.amazonaws.com/bucket/images/uuid1.jpg",
                    "https://s3.amazonaws.com/bucket/images/uuid2.jpg",
                    "https://s3.amazonaws.com/bucket/images/uuid3.jpg"
            );
            saveQuestionImages(question.getId(), imageUrls);

            // when
            questionFacade.deleteQuestion(question.getId(), author.getId());

            // then
            // 1. DB에서 질문이 삭제되었는지 확인
            assertThat(questionJpaRepository.findById(question.getId())).isEmpty();

            // 2. CASCADE로 이미지도 삭제되었는지 확인
            assertThat(questionImageJpaRepository.findByQuestionIdOrderByDisplayOrderAsc(question.getId())).isEmpty();

            // 3. 비동기로 S3 삭제가 호출되었는지 확인 (최대 5초 대기)
            await().atMost(5, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        verify(deleteImageFromS3Port, times(1))
                                .deleteImagesAsync(argThat(urls ->
                                        urls != null &&
                                        urls.size() == 3 &&
                                        urls.containsAll(imageUrls)
                                ));
                    });
        }

        @Test
        @DisplayName("이미지가 없는 질문 삭제 시 S3 삭제가 호출되지 않는다")
        void deleteQuestion_WithoutImages_ShouldNotTriggerS3Delete() {
            // given
            User author = createUser("author@test.com");
            Question question = createQuestion("이미지 없는 질문", "내용", author.getId());

            // when
            questionFacade.deleteQuestion(question.getId(), author.getId());

            // then
            assertThat(questionJpaRepository.findById(question.getId())).isEmpty();

            // S3 삭제가 호출되지 않음
            await().pollDelay(1, TimeUnit.SECONDS)
                    .atMost(2, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        verify(deleteImageFromS3Port, never()).deleteImagesAsync(anyList());
                    });
        }

        @Test
        @DisplayName("여러 이미지가 있는 질문 삭제 시 모든 이미지 URL이 전달된다")
        void deleteQuestion_WithMultipleImages_ShouldPassAllUrls() {
            // given
            User author = createUser("author@test.com");
            Question question = createQuestion("다중 이미지 질문", "내용", author.getId());
            List<String> imageUrls = List.of(
                    "https://s3.amazonaws.com/bucket/images/img1.jpg",
                    "https://s3.amazonaws.com/bucket/images/img2.jpg",
                    "https://s3.amazonaws.com/bucket/images/img3.jpg",
                    "https://s3.amazonaws.com/bucket/images/img4.jpg",
                    "https://s3.amazonaws.com/bucket/images/img5.jpg"
            );
            saveQuestionImages(question.getId(), imageUrls);

            // when
            questionFacade.deleteQuestion(question.getId(), author.getId());

            // then
            await().atMost(5, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        verify(deleteImageFromS3Port).deleteImagesAsync(argThat(urls ->
                                urls != null && urls.size() == 5
                        ));
                    });
        }
    }

    @Nested
    @DisplayName("트랜잭션 롤백 시나리오")
    class TransactionRollbackScenario {

        @Test
        @DisplayName("트랜잭션 롤백 시 S3 삭제 이벤트가 발생하지 않는다")
        void whenTransactionRollback_ShouldNotTriggerS3Delete() {
            // given
            User author = createUser("author@test.com");
            Question question = createQuestion("롤백 테스트", "내용", author.getId());
            List<String> imageUrls = List.of(
                    "https://s3.amazonaws.com/bucket/images/test.jpg"
            );
            saveQuestionImages(question.getId(), imageUrls);

            // when - 트랜잭션 내에서 예외 발생 시뮬레이션
            try {
                transactionTemplate.execute(status -> {
                    questionJpaRepository.deleteById(question.getId());
                    // 강제로 예외 발생 (CASCADE 실행 전)
                    throw new RuntimeException("트랜잭션 롤백 발생!");
                });
            } catch (RuntimeException e) {
                // 예외 무시
            }

            // then
            // 1. 트랜잭션이 롤백되어 질문이 여전히 존재
            assertThat(questionJpaRepository.findById(question.getId())).isPresent();

            // 2. S3 삭제 이벤트가 발생하지 않음
            await().pollDelay(1, TimeUnit.SECONDS)
                    .atMost(2, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        verify(deleteImageFromS3Port, never()).deleteImagesAsync(anyList());
                    });
        }
    }

    @Nested
    @DisplayName("비동기 실행 검증")
    class AsyncExecutionVerification {

        @Test
        @DisplayName("S3 삭제가 별도 스레드에서 비동기로 실행된다")
        void s3Delete_ShouldRunAsynchronously() {
            // given
            User author = createUser("author@test.com");
            Question question = createQuestion("비동기 테스트", "내용", author.getId());
            List<String> imageUrls = List.of("https://s3.amazonaws.com/bucket/images/async.jpg");
            saveQuestionImages(question.getId(), imageUrls);

            // Mock이 호출된 스레드 이름을 캡처
            doAnswer(invocation -> {
                String threadName = Thread.currentThread().getName();
                System.out.println("S3 삭제 실행 스레드: " + threadName);
                assertThat(threadName).startsWith("async-s3-");
                return null;
            }).when(deleteImageFromS3Port).deleteImagesAsync(anyList());

            // when
            String mainThreadName = Thread.currentThread().getName();
            System.out.println("메인 스레드: " + mainThreadName);
            questionFacade.deleteQuestion(question.getId(), author.getId());

            // then
            await().atMost(5, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        verify(deleteImageFromS3Port).deleteImagesAsync(anyList());
                    });
        }
    }

}

