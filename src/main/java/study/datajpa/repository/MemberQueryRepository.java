package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;

//* 임의의 레포지토리를 만들어서 사용하기
//* 다른 레포지토리들과 같이 인젝션 받아서 사용하면 됨.
@Repository // 해당 어노테이션을 달아줘야 스프링 빈으로 등록이 된다.
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final EntityManager em;

    public List<Member> search() {
        return em.createQuery("select m.id, m.username, t.name " +
                "from Member m " +
                "join m.team t", Member.class)
                .getResultList();
    }
}
