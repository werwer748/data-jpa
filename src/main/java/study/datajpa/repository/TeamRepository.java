package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Team;

/**
 * JpaRepository 를 상속받게되면
 * Spring Data Jpa가 자동으로 구현체를 만들어준다.
 * findBy, save, delete 등 굉장히 많은 기능을 제공함.
 */
public interface TeamRepository extends JpaRepository<Team, Long> {
}
