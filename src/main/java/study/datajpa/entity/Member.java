package study.datajpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자를 protected로 설정
@ToString(of = {"id", "username", "age"}) // 연관관계 필드는 주의해서 사용할 것(무한루프 발생)
@NamedQuery(
        name = "Member.findByUsername2",
        query = "select m from Member m where m.username = :username"
)
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
public class Member extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id") // 필드와 매칭되는 컬럼명을 지정해줄 수 있다.
    private Long id;

    private String username;
    private int age;

    /**
     * 맴버와 팀은 다대일 관계
     * @ManyToOne : 다대일 관계에서 사용
     * @JoinColumn(name = "team_id") : 외래키를 가지는 컬럼
     *
     * LAZY(지연로딩)을 사용해야하는 이유:
     * 모든 연관관계는 기본적으로 다 지연로딩으로 세팅해야 하는데
     * 즉시 로딩으로 걸려있는 경우 성능 최적화하기가 어렵기 떄문이다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team teamA) {
        this.username = username;
        this.age = age;
        if (teamA != null) {
            changeTeam(teamA);
        }
    }

    // 연관관계 편의 메서드
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
