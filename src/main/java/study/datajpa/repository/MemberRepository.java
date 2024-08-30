package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

//? 사용자 정의 레포지토리를 상속받으면 사용할 수 있음
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

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

    /** Paging api 사용 => 반환타입을 Page로 받는다
     * Pageable의 구현체인 PageRequest만 넘겨받으면 된다. => total을 포함한 페이징 관련 정보를 알아서 뽑아와 준다.
     * ? PageRequest: Pageable 인터페이스를 구현한 클래스
     * ? 반환된 값 체크 시 중요 필드:
     *      * getContent: 조회된 데이터 리스트
     *      * getTotalElements: 전체 데이터 수
     *      * getTotalPages: 전체 페이지 수
     *      * getNumber: 현재 페이지 번호
     *      * isFist: 첫번째 페이지인지 확인 => true: 첫번째 페이지, false: 첫번째 페이지가 아님
     *      * hasNext: 다음 페이지가 있는지 확인 => true: 다음 페이지 있음, false: 다음 페이지 없음
     */
    Page<Member> findByAge(int age, Pageable pageable);

    /**
     * Slice: 인피니트 스크롤 처럼 토탈이 필요없는 페이징 처리에 사용
     * Page와 마찬가지로 Pageable을 구현한 PageRequest를 넘겨받는다.
     * 반환 되는 필드 중 tatal이 필요한 필드들은 제외된다. (getTotalElements, getTotalPages.. 등)
     */
    Slice<Member> findSliceByAge(int age, Pageable pageable);

    /**
     * 카운트 쿼리 최적화 해보기
     * 실 쿼리와 따로 작성할 수 있는 countQuery 옵션을 사용해야 한다.
     * 쿼리를 분리하여 작성하는 것.
     * 무조건 사용하기 보다는 상황에 맞게 필요하다 판단 될 경우 쓰면 된다.
     */
    @Query(
            value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m"
    )
    Page<Member> findCountByAge(int age, Pageable pageable);

    /**
     * 벌크성 수정 쿼리
     * @Modifying을 통해 executeUpdate()를 사용한다.
     * 이 어노테이션을 사용하지 않을 경우 무언가 잘못호출되었다는 에러가 뜸
     * !JPA에서 벌크성 업데이트는 영속성 컨텍스트를 무시하고 바로 DB에 쿼리를 날린다.
     * ! => 코드 실행 중 영속성 컨텍스트와 DB의 데이터가 다를 수 있음
     *
     * clearAutomatically = true: 벌크성 수정 쿼리를 실행한 후 영속성 컨텍스트를 초기화한다.
     *  => 따로 엔티티메니저를 주입받아서 쓰는 수고를 덜 수 있음
     */
    @Modifying(clearAutomatically = true) //? 이 어노테이션을 붙여줘야 JPA가 이 쿼리는 update 쿼리라고 인식한다. - 없을경우 에러
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    /**
     * join fetch를 사용하여 연관된 엔티티를 함께 조회
     */
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    /**
     * 예제를 위해 findAll을 오버라이드 해서 사용
     * => 오버라이드하는 이유? findAll은 JpaRepository에 정의되어 있는 메소드기 떄문에 오버라이드 해야함
     *
     * @EntityGraph: 연관된 엔티티를 함께 조회할 때 사용
     *  => attributePaths에 연관된 엔티티를 지정해준다.
     *  JPQL 없이 객체 그래프를 한번에 엮어서 성능 최적화하여 가져온다.
     *  여러개 쓰는 경우는 attributePaths = {"team", "order", ...}
     */
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //? jpql도 짜고 EntityGraph도 쓰고 싶다면 이렇게 쓸 수 있다.
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //? 메소드명 조회에 EntityGraph를 사용하고 싶다면 이렇게 쓸 수 있다.
//    @EntityGraph(attributePaths = {"team"})
    /**
     * NamedEntityGraph를 사용하여 엔티티에 정의한 관계 조회를 사용할 수 있다.
     */
    @EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(String username);

    /**
     * QueryHint를 사용하여 쿼리 힌트를 제공할 수 있다.
     * @QueryHint(name = "org.hibernate.readOnly", value = "true")
     *  => 읽기 전용 힌트를 제공한다.
     *  => 이렇게 하면 영속성 컨텍스트를 거치지 않고 바로 DB에서 데이터를 가져온다.
     *  => 원본 데이터를 함께 가지고 있으면서 변경감지를 위한 자원을 소모하지 않아도 된다.
     *
     *  무조건 모든 기능에 바른다고해서 성능이 최적화 눈에 띄게 향상되지는 않는다.
     *  필수적으로 필요하다 싶은 곳에서만 잘 쓰는게 좋음
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    /**
     * Jpa가 제공하는 Lock
     * ? Lock? => 데이터베이스에서 데이터를 읽거나 쓸 때 다른 사용자가 접근하지 못하도록 하는 것
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Member findLockByUsername(String username);
}
