package study.datajpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class Team extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;

    private String name;

    /**
     * Team과 Member는 일대다 관계
     * 컬렉션 필드를 만든다.
     * @OneToMany(mappedBy = "team")
     * : 일대다 연관관계에서 해당 어노테이션을 사용
     * mappedBy : Member에서 자신을 가지는 필드를 설정
     * => 연관관계의 주인을 설정하는 것.
     * => 외래키가 없는쪽에 설정하는것이 권장됨.
     */
    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
