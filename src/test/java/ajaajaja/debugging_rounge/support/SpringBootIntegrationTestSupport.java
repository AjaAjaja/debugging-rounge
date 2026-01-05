package ajaajaja.debugging_rounge.support;

import ajaajaja.debugging_rounge.common.image.application.port.out.DeleteImageFromS3Port;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.transaction.support.TransactionTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

@SpringBootTest
@Import(SpringBootIntegrationTestSupport.MockConfig.class)
// 일반적인 DB에 비해 Testcontainers의 짧은 타임아웃 (5분)을 가지고 있음
// 따라서 5분 이상 소요되는 테스트는 실패할 수 있음
// 따라서 AFTER_CLASS 모드로 설정하여 테스트 클래스 후 Spring Context 재시작
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public abstract class SpringBootIntegrationTestSupport extends BaseTestSupport {

    @TestConfiguration
    static class MockConfig {
        // @Primary: S3 삭제 Port를 Mock으로 대체 (실제 AWS 호출 방지)
        @Bean
        @Primary
        public DeleteImageFromS3Port deleteImageFromS3Port() {
            return mock(DeleteImageFromS3Port.class);
        }
    }

    @Autowired
    protected DeleteImageFromS3Port deleteImageFromS3Port;
    
    @Autowired
    protected TransactionTemplate transactionTemplate;

    @AfterEach
    void cleanUpAfterTest() {
        // 1. Mock 초기화 (다음 테스트를 위한 호출 이력 제거)
        reset(deleteImageFromS3Port);
        
        // 2. DB 정리 (ON DELETE CASCADE로 연관 데이터 자동 삭제)
        // @Transactional 사용 시 자동 롤백되기 때문에 AFTER_COMMIT 작동을 위해 수동 정리
        try {
            answerImageJpaRepository.deleteAll();
            questionImageJpaRepository.deleteAll();
            answerJpaRepository.deleteAll();
            questionJpaRepository.deleteAll();
            userJpaRepository.deleteAll();
        } catch (Exception e) {
            System.out.println("DB 정리 중 에러 무시: " + e.getMessage());
        }
    }
}

