package study.datajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//? 모종의 이유로 GeneratedValue를 사용하지 못할 경우 Persistable을 사용하여 식별자 구분로직을 직접 구현할 수 있다.
public class Item implements Persistable<String> {

    @Id
//    @GeneratedValue
    private String id;

    @CreatedDate // JPA이벤트라서 데이터가 등록될 때 값이 생성됨
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() { // entityInformation.isNew(entity)
        // 값이 있냐 없냐의 판단을 이걸 통해서 객체 구분을 할 수 있다.
        return createdDate == null;
    }
}
