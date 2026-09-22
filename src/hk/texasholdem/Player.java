package hk.texasholdem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {

    // 플레이어의 이름이다.
    // 플레이 도중 이름을 변경할 필요가 없으므로 final로 선언한다.
    private final String name;

    // 플레이어가 받은 개인 카드를 저장한다.
    // 텍사스 홀덤에서 개인 카드는 최대 2장이다.
    private final List<Card> holeCards; // final이 막는 것은 리스트 변수 자체를 다른 리스트로 교체하는 것

    // 생성자
    // Player 객체를 생성할 때 이름을 전달받는다.
    public Player(String name) {
        this.name = name;

        // List는 인터페이스이므로 ArrayList 객체를 만들어 저장한다.
        holeCards = new ArrayList<>();
    }

    // 딜러 또는 게임 클래스로부터 카드 한 장을 받는 메서드
    public void receiveCard(Card card) {

        // 텍사스 홀덤의 개인 카드는 최대 2장이므로
        // 이미 2장이 있다면 카드를 더 받을 수 없게 한다.
        // throw는 정상적으로 처리할 수 없는 문제가 생겼다고 호출한 곳에 알리는 것
        // 컴파일러가 처리를 강제하지 않는 예외라서 try-catch 없이 사용할 수 있음
        if (holeCards.size() >= 2) {
            throw new IllegalStateException(
                    name + "은(는) 개인 카드를 이미 2장 가지고 있습니다.");
        }

        holeCards.add(card);
    }

    // 게터
    // 플레이어 이름을 반환한다.
    public String getName() {
        return name;
    }

    // 플레이어가 가진 개인 카드 목록을 반환한다.
    public List<Card> getHoleCards() {

        // holeCards를 그대로 반환하면 외부에서 add(), remove()를
        // 호출하여 플레이어의 카드를 마음대로 변경할 수 있다.
        //
        // unmodifiableList()는 조회만 가능하고 수정은 불가능한
        // 형태로 감싸서 반환한다.
        return Collections.unmodifiableList(holeCards); // Collections는 Java가 제공하는 컬렉션 도구 클래스
    }

    // 새로운 게임을 시작할 때 기존 개인 카드를 모두 제거한다.
    public void clearCards() {
        holeCards.clear();
    }

    // Player 객체를 출력하면 이름과 개인 카드가 함께 출력되도록 한다.
    @Override
    public String toString() {
        return name + ": " + holeCards;
    }
}