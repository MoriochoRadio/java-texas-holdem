package hk.texasholdem;

// 텍사스 홀덤에서 사용할 족보 종류를 정의한다.
// enum을 사용하면 아래에 정의된 값만 사용할 수 있다.
//enum은 밖에서 함부로 만들 수 없도록, 데이터와 기능을 꽉 채운 완제품 객체들을 미리 딱 정해진 개수만큼만 만들어두고 골라 쓰게 만든 한정판 객체 세트같은거임
public enum HandRank {
    // enum 상수(족보 이름, 강도, 출력할 한글 이름)
    // 숫자가 높을수록 강한 족보다.
    HIGH_CARD(1, "하이 카드"), // 이 한 줄이 상수 객체 생성임. 괄호 안의 값(1, "하이 카드")이 아래 생성자 메서드의 매개변수로 들어감
    ONE_PAIR(2, "원 페어"),
    TWO_PAIR(3, "투 페어"),
    THREE_OF_A_KIND(4, "트리플"),
    STRAIGHT(5, "스트레이트"),
    FLUSH(6, "플러시"),
    FULL_HOUSE(7, "풀하우스"),
    FOUR_OF_A_KIND(8, "포카드"),
    STRAIGHT_FLUSH(9, "스트레이트 플러시");

    // ================ enum은 그냥 class랑 다름!
    // 생성자와 내부 필드 변수는 오직 맨 위의 상수 객체들을 처음 조립하고 세팅할 때만 쓰이는 내부 부품임
    // ================

    // 각 족보의 강도를 저장한다.
    // 예: HIGH_CARD는 1, STRAIGHT_FLUSH는 9
    private final int strength;

    // 화면에 출력할 한글 족보 이름을 저장한다.
    private final String koreanName;

    // enum의 각 상수가 생성될 때 강도와 한글 이름을 전달받는다.
    //
    // HIGH_CARD(1, "하이 카드")가 만들어질 때:
    // strength에는 1
    // koreanName에는 "하이 카드"가 저장된다.
    HandRank(int strength, String koreanName) {
        this.strength = strength;
        this.koreanName = koreanName;
    }

    // 밑에 pulbic으로 만든건 밖에서 쓸 수 있는거임

    // 족보의 강도를 반환한다.
    public int getStrength() {
        return strength;
    }

    // 화면에 출력할 한글 이름을 반환한다.
    public String getKoreanName() {
        return koreanName;
    }
}
