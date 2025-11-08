package ajaajaja.debugging_rounge.feature.answer.application.port.out;

public interface DeleteAnswerPort {
    void deleteById(Long id);

    void deleteAllByQuestionId(Long questionId);
}
