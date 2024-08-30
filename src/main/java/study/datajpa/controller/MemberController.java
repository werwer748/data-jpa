package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    /**
     * 스프링 데이터 JPA가 제공하는 도메인 클래스 컨버터
     * <p>
     * 파라피터로 pk를 받을 때 대상 엔티티를 바로 받을 수 있다.
     * 스프링 부트를 쓰기 때문에 기본적으로 동작함
     */
    @GetMapping("/members2/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    /**
     * Pageable 인터페이스의 구현체로 스프링 데이터 JPA의 기능이 있는데
     * Spring Boot가 그걸 자동으로 세팅해준다.
     * 컨트롤러에서 파라미터가 바인딩 될떄 Pageable이 있으면 PageRequest 객체를 생성 값을 채워서 인젝션 해준다.
     *
     * members?page=0&size=3&sort=id,desc <= 이런식으로 요청하면 조건에 맞춰 리턴해준다.
     */
    @GetMapping("/members")
    public Page<MemberDto> list(
            // Pageable 기본값을 해당 컨트롤러에만 적용할 때 - 글로벌 설정보다 우선시 됨.
            @PageableDefault(size = 5) Pageable pageable
    ) {
        // findAll에 pageable을 넘기면 페이징 처리가 된다.(어떤 쿼리라도 - PagingAndSortingRepository가 동작)
        Page<Member> page = memberRepository.findAll(pageable);

        int pageNum = 1;

        // 페이지를 0번이 아닌 1번 부터 받고싶다면? - Pageable을 구현한 PageRequest를 사용하면 된다.
        PageRequest pageRequest = PageRequest.of(pageNum - 1, 10);// <= 1번 페이지, 10개씩
//        Page<Member> page2 = memberRepository.findAll(pageRequest); // pageRequest를 넘긴다.

        // Page는 DTO로 변환해서 반환하는 것이 좋다.
        // 반환 데이터에 페이지 관련 값들이 모두 있기 떄문에 이대로 프론트에서 사용하기에도 좋음
        // 반환도 Page보다 필요한 데이터를 뽑아서 서비스에 맞는 클래스로 만들어 반환하는것도 좋은 방법이다.
        return page.map(MemberDto::new);
    }

//    @PostConstruct
//    public void init() {
//        for (int i = 0; i < 100; i++) {
//            memberRepository.save(new Member("user" + i, i));
//        }
//    }
}
