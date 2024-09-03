package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        /**
         * memberRepository = class jdk.proxy3.$Proxy132
         * Spring Data JPA가 인터페이스에 대한 구현체(프록시객체)를 만들어서 주입해준다.
         */
        System.out.println("memberRepository = " + memberRepository.getClass());
        Member member = new Member("memberA");

        //* JpaRepository에서 save를 기본 제공한다.
        Member savedMember = memberRepository.save(member);

        //* JpaRepository에서 findById를 기본 제공한다. - 기본적으로 Optional로 반환한다.
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertEquals(findMember.getId(), member.getId());
        assertEquals(findMember.getUsername(), member.getUsername());
        assertEquals(findMember, member);
    }

    @Test // 기본 CRUD 테스트
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        // 단건 조회 검증
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        findMember1.setUsername("member!!!!"); // 변경 감지 - 트랜잭션 종료시점에 업데이트 쿼리

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        //* 인터페이스만 작성했는데 기능이 정상 작동 됨.
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertEquals(result.size(), 1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername2("AAA");

        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);

        Member findMember = result.get(0);
        assertEquals(findMember, m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUsernameList();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(List.of("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findListByUsername("ccc");
        System.out.println("aaa = " + aaa.size());
        Member findMember = memberRepository.findMemberByUsername("AAA");
        System.out.println("findMember = " + findMember);
//        Optional<Member> findOptional = memberRepository.findOptionalByUsername("AAA");
//        System.out.println("findOptional = " + findOptional);

    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;

        //? PageRequest로 만든 Pageable 인터페이스만 넘겨주면 된다.
        PageRequest pageRequest = PageRequest.of(
                0, 3, Sort.by(Sort.Direction.DESC, "username")
        );

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //* 이렇게 Dto로 변환해서 응답을 반환하면 된다.
        //* Page 타입의 경우 이대로 반환해도 스프링에서 알아서 json으로 변환해준다.
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        // then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(totalElements).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);

        //? 벌크 연산 후에 영속성 컨텍스트는 초기화해야 함.
        //? 한트랜잭션 안에서는 같은 엔티티메니저를 사용하기 때문에 이런식으로 사용해도 문제가 없음
//        em.flush(); => save를 하면 자동으로 flush가 되기 때문에 필요 없음
//        em.clear(); => 레포지토리에 @Modifying(clearAutomatically = true)를 붙여주면 이것도 필요 없음


        List<Member> result = memberRepository.findListByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        // EntityGraph 어노테이션을 사용해서 한방에 조회해 온다.
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());

            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    public void lock() {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void specBasic() {
        // given
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member m1 = new Member("m1", 10, teamA);
        Member m2 = new Member("m2", 20, teamA);
        memberRepository.save(m1);
        memberRepository.save(m2);

        em.flush();
        em.clear();

        // when
        /**
         * Specification을 구현하여 명세들을 조립해 사용할 수 있다.
         * where(), and(), or(), not() 등을 사용할 수 있다.
         * findAll에 명세를 넘겨주면 조립된 명세대로 쿼리를 날린다.
         *
         * criteria자체가 복잡해서 실무에서 사용성이 떨어진다.
         * 자바코드를 조립해서 사용할 수 있다는건 장점이긴 함.
         */
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test // 쿼리가 동적으로 나가야 할 때 쓸 수 있다.
    public void queryByExample() {
        // given
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member m1 = new Member("m1", 10, teamA);
        Member m2 = new Member("m2", 0, teamA);
        memberRepository.save(m1);
        memberRepository.save(m2);

        em.flush();
        em.clear();

        // when
        /**
         * 엔티티 자체가 검색 조건이 된다. - Probe 생성
         * 엔티티에 age없을시 where age = 0 조건이 걸림 - age는 primitive type이기 때문!
         *
         * 복잡한 죠인에는 사용할 수가 없음... - 한계가 명확하다.(inner join만 가능)
         */
        Member member = new Member("m1");
        Team team = new Team("teamA");
        /**
         * join -> join 확인
         *   team t1_0
         *       on t1_0.team_id=m1_0.team_id
         * where -> where 확인
         *     t1_0.name=?
         *     and m1_0.username=?
         */
        member.setTeam(team); // 연관관계 설정 - join

        // where에 age 조건을 무시한다.
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");

        //* Example: Probe와 ExampleMatcher로 구성 -> 쿼리 생성에 사용한다.
        Example<Member> example = Example.of(member, matcher);

        /**
         * JpaRepository에서 제공하는 findAll에 Example을 넘겨주면 됨. - QueryByExampleExecutor를 넘기는 것
         * Jpa가 기본적으로 제공하는 기능
         */
        List<Member> result = memberRepository.findAll(example);

        assertThat(result.get(0).getUsername()).isEqualTo("m1");
    }

    @Test
    public void projections() {
        // given
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member m1 = new Member("m1", 10, teamA);
        Member m2 = new Member("m2", 0, teamA);
        memberRepository.save(m1);
        memberRepository.save(m2);

        em.flush();
        em.clear();

        // when
        //* 인터페이스 기반 프로젝션
//        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");
        //* 클래스 기반 프로젝션
//        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1");
        //* 클래스 기반 프로젝션 - 동적 프로젝션 사용
//        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1", UsernameOnlyDto.class);

        //* 프로젝션 해오는 타입만 바꿔가면서 사용 - 동적 프로젝션 활용
        List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);

        for (NestedClosedProjections nestedClosedProjections : result) {
            System.out.println("nestedClosedProjections = " + nestedClosedProjections.getUsername());
            System.out.println("nestedClosedProjections = " + nestedClosedProjections.getTeam().getName());
        }
    }

    @Test
    public void nativeQuery() {
        // given
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member m1 = new Member("m1", 10, teamA);
        Member m2 = new Member("m2", 0, teamA);
        memberRepository.save(m1);
        memberRepository.save(m2);

        em.flush();
        em.clear();

        // when
        Page<MemberProjection> result = memberRepository.findByNativeProjection("m1", PageRequest.of(0, 10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection.getId());
            System.out.println("memberProjection = " + memberProjection.getUsername());
            System.out.println("memberProjection = " + memberProjection.getTeamName());
        }
    }
}