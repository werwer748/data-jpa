package study.datajpa.repository;

//* 실제 클래스기반 프로젝션
public class UsernameOnlyDto {

    private final String username;

    //? 생성자의 파라미터명을 기준으로 값을 채워준다.
    public UsernameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
