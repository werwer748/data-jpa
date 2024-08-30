package study.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
// 이벤트 기반으로 동작한다는 것을 알려준다. - 해당 클래스에 Auditing 기능을 포함시킴
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

    // 제공되는 어노테이션을 사용한다.
    @CreatedDate // 생성일자를 기입해줌.
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate // 수정일자를 기입해줌.
    private LocalDateTime lastModifiedDate;

}
