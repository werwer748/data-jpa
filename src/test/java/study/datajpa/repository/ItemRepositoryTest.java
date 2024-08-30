package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test // 엔티티의 id에 @GeneratedValue를 사용하면 persist전에는 id가 생성되지 않기 때문에 식별자가 null로 넘어가게 된다.
    public void save() {
//        Item item = new Item();
//        //? persist전에는 id가 생성되지 않기 때문에 식별자가 null로 넘어가게 된다.
//        itemRepository.save(item);
    }

    @Test // 엔티티의 id에 @GeneratedValue를 사용지 않는다면?
    public void saveNoGId() {
        Item item = new Item("A"); // pk에 값이 있는걸로 인식되서 persist를 호출하지 않는다.
        /**
         * savetldp id를 A로 만든 엔티티를 넘겼기 때문에 merge를 호출하게 된다.
         * merge는 DB에서 pk를 찾아오기 위해 select를 호출
         * DB에 A가 없기 때문에 새로 데이터를 집어 넣음
         *
         * merge는 save or update 느낌의 기능을 제공하는것 같지만 애매한 부분이 있다.
         * 업데이트를 해주지만 데이터를 강제로 갈아끼우기 때문에 좋지않음(조회도 다시하기도 하고)
         * 그래서 merge를 쓰기보다는 persist를 사용하는 것이 좋다.
         * 데이터 변경시에는 변경감지를 사용할 것.
         * 영속성 컨텍스트 내에서의 조작이 필요한 경우는 merge가 필요하지만 대부분의 경우 필요가 없음
         */
        itemRepository.save(item);
    }
}