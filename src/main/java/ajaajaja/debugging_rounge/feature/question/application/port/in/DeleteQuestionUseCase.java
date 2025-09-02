package ajaajaja.debugging_rounge.feature.question.application.port.in;

public interface DeleteQuestionUseCase {
    void deleteQuestion(Long questionId, Long loginUserId);
}
