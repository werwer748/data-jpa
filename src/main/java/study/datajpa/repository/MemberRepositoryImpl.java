package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import java.util.List;

/**
 * 구현체 이름을 정할 때 기존에는
 * 상속받을 레포지토리 명 + Impl이 규칙 이었으나(예: MemberRepositoryImpl)
 * 현재(스프링 데이터 JPA 2.x 부터)는 사용자 정의 인터페이스명 + Impl 방식도 지원함
 *  => MemberRepositoryCustomImpl 도 사용이 가능하다는 얘기
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
