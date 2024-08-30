package study.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
// 이벤트 기반으로 동작한다는 것을 알려준다. - 해당 클래스에 Auditing 기능을 포함시킴
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity extends BaseTimeEntity {

    @CreatedBy // 생성자를 기입해줌.
    @Column(updatable = false) // 수정안되도록 신경써주는 것
    private String createdBy;

    @LastModifiedBy // 수정자를 기입해줌.
    private String lastModifiedBy;
}
