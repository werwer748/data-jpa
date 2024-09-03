package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

/**
 * 프로젝션 대상이 되는 인터페이스
 * 엔티티의 일부만 가져올 때 사용
 * 이런식으로 인터페이스만 정의하면 스프링 데이터 JPA가 실제 구현체를 만들어서 반환해 준다.
 *
 * 인터페이스 기반 클로즈 프로젝션
 * 엔티티의 getter 메서드(프로퍼티)를 이용하여 프로젝션
 *
 * 인터페이스 기반 오픈 프로젝션
 * @Value("#{target.username + ' ' + target.age}")
 * => 두 문자를 더해서 반환
 * 엔티티를 통쨰로 가져온 다음 taget에 지정한 정보를 따로 뽑아서 반환하는 것.
 */
public interface UsernameOnly {

    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
}
