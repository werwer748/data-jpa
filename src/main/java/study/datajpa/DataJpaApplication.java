package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
/**
 * @EnableJpaAuditing:
 * JPA Auditing 활성화 - 순수 JPA, Spring Data JPA 모두 사용
 * 생성시 수정관련 정보를 null로 두고 싶다면 (modifyOnCreate = false) 옵션을 사용
 */
@EnableJpaAuditing
public class DataJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataJpaApplication.class, args);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
//        return new AuditorAware<String>() {
//            @Override
//            public Optional<String> getCurrentAuditor() {
//                return Optional.empty();
//            }
//        }; // 람다식으로 변경 => 메소드가 하나면 이런식으로 작성이 가능.
        /**
         * 지금은 예제이기 떄문에 랜덤한 값을 넣고 있음
         * 실무에서는 SecurityContextHolder.getContext() ... 을 사용해서
         * 로그인한 유저 정보를 가져와 사용하면 됨
         */
        return () -> Optional.of(UUID.randomUUID().toString());
    }

}
