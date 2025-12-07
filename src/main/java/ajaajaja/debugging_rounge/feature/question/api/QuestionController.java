package ajaajaja.debugging_rounge.feature.question.api;

import ajaajaja.debugging_rounge.common.security.annotation.LoginUserId;
import ajaajaja.debugging_rounge.common.util.UriHelper;
import ajaajaja.debugging_rounge.feature.answer.api.mapper.AnswerMapper;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionCreateRequest;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionListResponse;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionUpdateRequest;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionWithAnswerResponse;
import ajaajaja.debugging_rounge.feature.question.api.mapper.QuestionResponseMapper;
import ajaajaja.debugging_rounge.feature.question.api.sort.QuestionOrder;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionCreateDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionListDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionUpdateDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionWithAnswersDto;
import ajaajaja.debugging_rounge.feature.question.application.port.in.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Question", description = "질문 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionController {

    private final CreateQuestionUseCase createQuestionUseCase;
    private final GetQuestionWithAnswersQuery getQuestionWithAnswersQuery;
    private final GetQuestionListWithPreviewQuery getQuestionListWithPreviewQuery;
    private final UpdateQuestionUseCase updateQuestionUseCase;
    private final DeleteQuestionUseCase deleteQuestionUseCase;
    private final QuestionResponseMapper questionResponseMapper;

    @Operation(summary = "질문 생성", description = "새로운 질문을 생성합니다.", security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "질문 생성 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<Long> createQuestion(
            @Parameter(hidden = true) @LoginUserId Long userId,
            @RequestBody @Valid QuestionCreateRequest questionCreateRequest) {

        QuestionCreateDto questionCreateDto =
                QuestionCreateDto.of(questionCreateRequest.title(), questionCreateRequest.content(), userId);
        Long questionId = createQuestionUseCase.createQuestion(questionCreateDto);

        return ResponseEntity
                .created(UriHelper.buildCreatedUri(questionId))
                .body(questionId);
    }

    @Operation(summary = "질문 상세 조회", description = "질문 ID로 질문과 답변 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "질문을 찾을 수 없음", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionWithAnswerResponse> findQuestionWithAnswers(
            @Parameter(description = "질문 ID", required = true, example = "1") @PathVariable("questionId") Long questionId,
            @Parameter(hidden = true) @LoginUserId(required = false) Long loginUserId,
            @Parameter(hidden = true) Pageable answerPageable) {

        QuestionWithAnswersDto questionWithAnswersDto =
                getQuestionWithAnswersQuery.getQuestionWithAnswers(questionId, loginUserId, answerPageable);

        QuestionWithAnswerResponse questionWithAnswerResponse =
                questionResponseMapper.toQuestionWithAnswersResponse(questionWithAnswersDto, loginUserId);

        return ResponseEntity.ok(questionWithAnswerResponse);
    }

    @Operation(summary = "질문 목록 조회", description = "정렬 옵션에 따라 질문 목록을 페이지네이션하여 조회합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json")))
    @GetMapping
    public ResponseEntity<Page<QuestionListResponse>> findQuestionsWithPreview(
            @Parameter(description = "정렬 기준 (LATEST, POPULAR 등)", example = "LATEST")
            @RequestParam(name = "order", defaultValue = "LATEST") QuestionOrder order,
            @Parameter(hidden = true)
            @PageableDefault(sort = {"createdDate", "id"}, direction = Sort.Direction.DESC)
            Pageable pageable, HttpServletRequest request) {
        Page<QuestionListDto> questionListDtos = getQuestionListWithPreviewQuery.getQuestionsWithPreview(pageable, order);
        return ResponseEntity.ok(questionListDtos.map(questionResponseMapper::toQuestionListResponse));
    }

    @Operation(summary = "질문 수정", description = "질문을 수정합니다. 본인이 작성한 질문만 수정할 수 있습니다.", security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "수정 성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "질문을 찾을 수 없음", content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/{questionId}")
    public ResponseEntity<Void> updateQuestion(
            @Parameter(description = "질문 ID", required = true, example = "1") @PathVariable("questionId") Long questionId,
            @RequestBody @Valid QuestionUpdateRequest questionUpdateRequest,
            @Parameter(hidden = true) @LoginUserId Long loginUserId
    ){
        QuestionUpdateDto questionUpdateDto = QuestionUpdateDto.of(
                questionId, questionUpdateRequest.title(), questionUpdateRequest.content(), loginUserId);
        updateQuestionUseCase.updateQuestion(questionUpdateDto);

        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

    @Operation(summary = "질문 삭제", description = "질문을 삭제합니다. 본인이 작성한 질문만 삭제할 수 있습니다.", security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "질문을 찾을 수 없음", content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @Parameter(description = "질문 ID", required = true, example = "1") @PathVariable("questionId") Long questionId,
            @Parameter(hidden = true) @LoginUserId Long loginUserId){
        deleteQuestionUseCase.deleteQuestion(questionId, loginUserId);

        return ResponseEntity.noContent().build();
    }
}