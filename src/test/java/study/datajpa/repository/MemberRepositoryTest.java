package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

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
}