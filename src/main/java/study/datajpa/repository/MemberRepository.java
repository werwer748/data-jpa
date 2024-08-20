package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

//? interface 끼리 상속 받을때는 extends 를 사용한다.
public interface MemberRepository extends JpaRepository<Member, Long> {

    //* 유저이름이 같고 나이가 특정나이 이상인 결과를 원함 - 구현체를 만들지 않았다.
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    //? NamedQuery를 사용
//    @Query(name = "Member.findByUsername") - 주석처리해도 엔티티에 지정한 이름과 메소드명이 같으면 정상작동
    //? @Param? - jpql이 명확하게 작성되어 있고 파라미터를 받아서 사용하는 경우 쓰면 됨.
    List<Member> findByUsername2(@Param("username") String username);

    /**
     * @Query - 레포지토리 메소드에 쿼리 직접 정의하기
     * NamedQuery 처럼 애플리케이션 로딩시점에 에러를 잡아준다.
     * (정적쿼리라서 애플리케이션 로딩 시점에 파싱을 해서 SQL을 만들어두기 때문에)
     * 실무에서 많이 사용됨 => 동적 쿼리는 QueryDSL을 사용하여 처리하는게 정신건강에 이로울듯
     */
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    //* 단순 회원 이름 리스트 조회 - 엔티티 내 필드 단일 조회
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    //* DTO로 조회하기
    @Query("select " +
            //? new study.datajpa.dto.MemberDto(...) - 패키지명을 포함한 전체경로를 적어줘야함
            "new study.datajpa.dto.MemberDto(m.id, m.username, t.name) " +
            "from Member m " +
            "join m.team t")
    List<MemberDto> findMemberDto();

    //* 컬렉션 파라미터 바인딩
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    //* 반환타입이 컬렉션인 경우 => 결과가 없으면 빈 컬렉션을 반환(null이 아님이 보장된다)
    List<Member> findListByUsername(String username);

    /**
     * 단건 조회에서 결과가 2건 이상인 경우
     * ! NonUniqueResultException이 발생
     * ! => 이 에러를 프레임워크 에러로 바꿔 줌
     * ! org.springframework.dao.IncorrectResultSizeDataAccessException
     * ? 에러를 트랜스레이션 해주는 이유
     * ?: JPA에 의존하지 않고 다양한 기술에 대응하여 클라이언트(스프링)에 넘겨줄 수 있기 떄문이다.
     */
    //* 반환타입이 단건인 경우 => 조회가 안될경우 결과 null => 순수 JPA에서는 NoResult에러가 터짐
    Member findMemberByUsername(String username);
    //* 반환타입이 Optional인 경우 => 조회 결과가 없을 경우 Optional.empty => orElse 등으로 유연하게 처리 가능
    Optional<Member> findOptionalByUsername(String username);
}
