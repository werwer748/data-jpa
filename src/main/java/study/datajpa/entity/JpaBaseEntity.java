package study.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 해당 클래스를 상속받는 엔티티 클래스에게 매핑 정보를 상속해준다.
public abstract class JpaBaseEntity {

    @Column(updatable = false) // 생성 이후 업데이트 되지 않음
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    /**
     * persist 하기 전에 실행
     */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now;
        this.updateDate = now;
    }

    /**
     * update 하기 전에 실행
     */
    @PreUpdate
    public void preUpdate() {
        this.updateDate = LocalDateTime.now();
    }
}
