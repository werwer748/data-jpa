package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository // 해당 어노테이션을 달아줘야 스프링 빈으로 등록이 된다.
public class MemberJpaRepository {

    // 엔티티 메니저 주입
    @PersistenceContext
    private EntityManager em;

    /**
     * 따로 업데이트 메서드가 없는 이유?
     * JPA는 변경 감지라는 기능으로 데이터를 바꾼다.
     * 엔티티 메니저를 통회 값을 조회해오고 트랜잭션을 커밋할 때
     * 변경된 값이 있는지 확인하고 업데이트 쿼리를 날린다.
     */
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        // 전체 조회의 경우 JPQL을 이용해 처리 - jpql은 엔티티 객체를 대상으로 쿼리를 날림
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findByUsernameAndAgeGreaterThen(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age", Member.class)
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    //? 엔티티나 orm.xml에 정의한 NamedQuery를 사용하는 방법
    public List<Member> findByUsername(String username) {
        return em.createNamedQuery("Member.findByUsername2", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    //* 페이징과 정렬
    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc", Member.class)
                .setParameter("age", age)
                .setFirstResult(offset) // 몇번째부터
                .setMaxResults(limit) // 몇개
                .getResultList();
    }

    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    //* 벌크성 수정 쿼리 - executeUpdate
    public int bulkAgePlus(int age) {
        return em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
    }
}
