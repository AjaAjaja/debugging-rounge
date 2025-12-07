package ajaajaja.debugging_rounge.feature.answer.api;

import ajaajaja.debugging_rounge.common.security.annotation.LoginUserId;
import ajaajaja.debugging_rounge.common.util.UriHelper;
import ajaajaja.debugging_rounge.feature.answer.api.dto.AnswerCreateRequest;
import ajaajaja.debugging_rounge.feature.answer.api.dto.AnswerDetailResponse;
import ajaajaja.debugging_rounge.feature.answer.api.dto.AnswerUpdateRequest;
import ajaajaja.debugging_rounge.feature.answer.api.mapper.AnswerMapper;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.CreateAnswerUseCase;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.DeleteAnswerUseCase;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.GetAnswersQuery;
import ajaajaja.debugging_rounge.feature.answer.application.port.in.UpdateAnswerUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Answer", description = "답변 관련 API")
@RestController
@RequiredArgsConstructor

public class AnswerController {

    private final CreateAnswerUseCase createAnswerUseCase;
    private final GetAnswersQuery getAnswersQuery;
    private final UpdateAnswerUseCase updateAnswerUseCase;
    private final DeleteAnswerUseCase deleteAnswerUseCase;
    private final AnswerMapper answerMapper;

    @Operation(summary = "답변 생성", description = "특정 질문에 대한 답변을 생성합니다.", security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "답변 생성 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "질문을 찾을 수 없음", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<Long> createAnswer(
            @Parameter(description = "질문 ID", required = true, example = "1") @PathVariable("questionId") Long questionId,
            @RequestBody @Valid AnswerCreateRequest answerCreateRequest,
            @Parameter(hidden = true) @LoginUserId Long userId
    ) {
        Long answerId = createAnswerUseCase.createAnswer(answerCreateRequest.toDto(questionId, userId));
        return ResponseEntity
                .created(UriHelper.buildCreatedUri(answerId))
                .body(answerId);
    }

    @Operation(summary = "답변 목록 조회", description = "특정 질문에 대한 답변 목록을 페이지네이션하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "질문을 찾을 수 없음", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/questions/{questionId}/answers")
    public ResponseEntity<Page<AnswerDetailResponse>> getAnswersByQuestionId(
            @Parameter(description = "질문 ID", required = true, example = "1") @PathVariable("questionId") Long questionId,
            @Parameter(hidden = true) @LoginUserId(required = false) Long currentUserId,
            @Parameter(hidden = true) Pageable pageable
    ) {
        Page<AnswerDetailDto> answersPage = getAnswersQuery.getAllAnswerByQuestionId(questionId, pageable);

        Page<AnswerDetailResponse> answersResponsePage =
                answersPage.map(dto -> answerMapper.toResponse(dto, currentUserId));

        return ResponseEntity.ok(answersResponsePage);
    }

    @Operation(summary = "답변 수정", description = "답변을 수정합니다. 본인이 작성한 답변만 수정할 수 있습니다.", security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "수정 성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음", content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/answers/{answerId}")
    public ResponseEntity<Void> updateAnswer(
            @Parameter(description = "답변 ID", required = true, example = "1") @PathVariable("answerId")Long answerId,
            @RequestBody @Valid AnswerUpdateRequest answerUpdateRequest,
            @Parameter(hidden = true) @LoginUserId Long loginUserId
    ){
        updateAnswerUseCase.updateAnswer(answerMapper.toDto(answerUpdateRequest, answerId, loginUserId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "답변 삭제", description = "답변을 삭제합니다. 본인이 작성한 답변만 삭제할 수 있습니다.", security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음", content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/answers/{answerId}")
    public ResponseEntity<Void> deleteAnswer(
            @Parameter(description = "답변 ID", required = true, example = "1") @PathVariable("answerId")Long answerId,
            @Parameter(hidden = true) @LoginUserId Long loginUserId
    ){
        deleteAnswerUseCase.deleteAnswer(answerId, loginUserId);

        return ResponseEntity.noContent().build();
    }
}
