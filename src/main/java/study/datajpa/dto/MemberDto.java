package study.datajpa.dto;

import lombok.Data;
import study.datajpa.entity.Member;

@Data
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    /**
     * 깨알 팁
     * 엔티티가 Dto를 보는건 좋지 않지만(같은 패키지 내라면 괜찮을 수 도 있음)
     * Dto가 엔티티를 보는 것은 괜찮다.
     * 그래서 Dto에 엔티티를 파라미터로 받는 생성자를 만들어도 된다.
     * */
    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
    }
}
