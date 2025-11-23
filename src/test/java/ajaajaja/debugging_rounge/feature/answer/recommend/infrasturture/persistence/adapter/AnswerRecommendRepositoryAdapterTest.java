package ajaajaja.debugging_rounge.feature.answer.recommend.infrasturture.persistence.adapter;

import ajaajaja.debugging_rounge.feature.answer.domain.Answer;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.answer.recommend.domain.AnswerRecommend;
import ajaajaja.debugging_rounge.feature.answer.recommend.domain.RecommendType;
import ajaajaja.debugging_rounge.feature.question.domain.Question;
import ajaajaja.debugging_rounge.support.MysqlJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Import({AnswerRecommendRepositoryAdapter.class, AnswerRecommendDtoMapperImpl.class})
class AnswerRecommendRepositoryAdapterTest extends MysqlJpaTestSupport {

    @Autowired
    AnswerRecommendRepositoryAdapter adapter;

    @Test
    @DisplayName("findRecommendScoreAndMyType 각 답변마다 추천 점수와 내 추천 타입을 불러온다")
    void 답변마다_추천점수와_내_추천타입_불러오기() {

        // given
        Long authorId = 1L;
        Long userId1 = 101L;
        Long userId2 = 102L;
        Long userId3 = 103L;

        Question question = saveQuestion("testTitle", "abcdefghijklmn", authorId);
        Answer answer1 = saveAnswer("testContent1", question.getId(), userId1);
        Answer answer2 = saveAnswer("testContent2", question.getId(), userId2);
        Answer answer3 = saveAnswer("testContent3", question.getId(), userId3);
        Long answer1Id = answer1.getId();
        Long answer2Id = answer2.getId();
        Long answer3Id = answer3.getId();

        List<Long> answerIds = List.of(answer1Id, answer2Id, answer3Id);

        // answer1: +3점
        saveAnswerRecommend(RecommendType.UP, answer1Id, userId1); // user1: Type.UP
        saveAnswerRecommend(RecommendType.UP, answer1Id, userId2);
        saveAnswerRecommend(RecommendType.UP, answer1Id, userId3);

        // answer2: -2점
        saveAnswerRecommend(RecommendType.DOWN, answer2Id, userId1); // user1: Type.DOWN
        saveAnswerRecommend(RecommendType.DOWN, answer2Id, userId2);

        // answer3: 0점
        saveAnswerRecommend(RecommendType.UP, answer3Id, userId1); // user1: Type.NONE
        saveAnswerRecommend(RecommendType.DOWN, answer3Id, userId2);

        // when
        List<AnswerRecommendScoreAndMyRecommendTypeDto> result = adapter.findRecommendScoreAndMyType(answerIds, userId1);

        // then
        assertThat(result).hasSize(3);

        Map<Long, AnswerRecommendScoreAndMyRecommendTypeDto> map =
                result.stream().collect(Collectors.toMap(
                        AnswerRecommendScoreAndMyRecommendTypeDto::answerId,
                        Function.identity()
                ));

        assertThat(map.get(answer1Id).answerRecommendScore()).isEqualTo(3);
        assertThat(map.get(answer1Id).myAnswerRecommendType()).isEqualTo(RecommendType.UP);

        assertThat(map.get(answer2Id).answerRecommendScore()).isEqualTo(-2);
        assertThat(map.get(answer2Id).myAnswerRecommendType()).isEqualTo(RecommendType.DOWN);

        assertThat(map.get(answer3Id).answerRecommendScore()).isEqualTo(0);
        assertThat(map.get(answer3Id).myAnswerRecommendType()).isEqualTo(RecommendType.UP);
    }

    @Nested
    @DisplayName("findRecommendScoreByAnswerId")
    class findRecommendScoreByAnswerId {
        @Test
        @DisplayName("answerId에 따른 추천 점수를 반환한다")
        void 특정_답변의_추천_점수() {

            // given
            Long authorId = 1L;
            Long userId1 = 101L;
            Long userId2 = 102L;
            Long userId3 = 103L;
            Long userId4 = 104L;

            Question question = saveQuestion("testTitle", "abcdefghijklmn", authorId);
            Answer answer = saveAnswer("testContent1", question.getId(), userId1);
            Long answerId = answer.getId();

            // +2점
            saveAnswerRecommend(RecommendType.UP, answerId, userId1); // +1
            saveAnswerRecommend(RecommendType.UP, answerId, userId2); // +1
            saveAnswerRecommend(RecommendType.DOWN, answerId, userId3); // -1
            saveAnswerRecommend(RecommendType.UP, answerId, userId4); // +1

            // when
            Integer result = adapter.findRecommendScoreByAnswerId(answerId);

            // then
            assertThat(result).isEqualTo(2);
        }

        @Test
        @DisplayName("다른 답변 추천이 있어도 본인 답변 추천 점수만 계산한다")
        void 다른_답변_따로_계산() {
            // given
            Long authorId = 1L;
            Long userId1 = 101L;
            Long userId2 = 102L;
            Long userId3 = 103L;
            Long userId4 = 104L;

            Question question = saveQuestion("testTitle", "abcdefghijklmn", authorId);
            Answer answer = saveAnswer("testContent1", question.getId(), userId1);
            Answer anotherAnswer = saveAnswer("testContent2", question.getId(), userId1);
            Long answerId = answer.getId();
            Long anotherAnswerId = anotherAnswer.getId();

            // +2점
            saveAnswerRecommend(RecommendType.UP, answerId, userId1); // +1
            saveAnswerRecommend(RecommendType.UP, answerId, userId2); // +1
            saveAnswerRecommend(RecommendType.DOWN, answerId, userId3); // -1
            saveAnswerRecommend(RecommendType.UP, answerId, userId4); // +1

            // 아래 점수는 포함하지 않는다.
            saveAnswerRecommend(RecommendType.UP, anotherAnswerId, userId2); // +1

            // when
            Integer result = adapter.findRecommendScoreByAnswerId(answerId);

            // then
            assertThat(result).isEqualTo(2);
        }

        @Test
        @DisplayName("따로 추천이 없다면 null이 아닌 0점을 반환한다")
        void null_이_아닌_0점반환() {
            // given
            Long authorId = 1L;
            Long userId1 = 101L;
            Long userId2 = 102L;
            Long userId3 = 103L;
            Long userId4 = 104L;

            Question question = saveQuestion("testTitle", "abcdefghijklmn", authorId);
            Answer answer = saveAnswer("testContent1", question.getId(), userId1);
            Long answerId = answer.getId();

            // when
            Integer result = adapter.findRecommendScoreByAnswerId(answerId);

            // then
            assertThat(result).isZero();
        }
    }

    @Nested
    @DisplayName("upsert")
    class upsert{
        @Test
        @DisplayName("처음 호출이면 insert")
        void 첫호출은_insert() {

            // given
            Long authorId = 1L;
            Long userId = 101L;

            Question question = saveQuestion("testTitle", "abcdefghijklmn", authorId);
            Answer answer = saveAnswer("testContent1", question.getId(), userId);
            Long answerId = answer.getId();
            RecommendType recommendType = RecommendType.DOWN;

            // when
            adapter.upsert(answerId, userId, recommendType.name());

            // then
            Optional<AnswerRecommend> optionalResult = adapter.findByAnswerIdAndUserID(answerId, userId);

            assertThat(optionalResult).isPresent();
            AnswerRecommend result = optionalResult.get();

            assertThat(result.getAnswerId()).isEqualTo(answerId);
            assertThat(result.getUserId()).isEqualTo(userId);
            assertThat(result.getType()).isEqualTo(recommendType);
        }

        @Test
        @DisplayName("두 번째 호출은 update")
        void 두번째_호출은_update() {

            // given
            Long authorId = 1L;
            Long userId = 101L;

            Question question = saveQuestion("testTitle", "abcdefghijklmn", authorId);
            Answer answer = saveAnswer("testContent1", question.getId(), userId);
            Long answerId = answer.getId();

            saveAnswerRecommend(RecommendType.UP, answerId, userId);

            // when
            adapter.upsert(answerId, userId, RecommendType.DOWN.name());
            adapter.upsert(answerId, userId, RecommendType.UP.name()); // 최종 추천 타입 = UP

            // then
            Optional<AnswerRecommend> optionalResult = adapter.findByAnswerIdAndUserID(answerId, userId);

            assertThat(optionalResult.isPresent()).isTrue();
            AnswerRecommend result = optionalResult.get();

            assertThat(result.getAnswerId()).isEqualTo(answerId);
            assertThat(result.getUserId()).isEqualTo(userId);
            assertThat(result.getType()).isEqualTo(RecommendType.UP);
        }
    }
}