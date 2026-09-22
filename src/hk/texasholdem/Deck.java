package hk.texasholdem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    // 사용할 카드 무늬 4개
    private static final String[] SUITS = { "◆", "♥", "♣", "♠" };

    // 아직 뽑지 않은 카드들을 보관할 리스트. 크게 먼저 List로 잡아뒀다.
    // List는 리스트가 갖춰야 할 기능을 정의한 인터페이스
    // List는 인터페이스이므로 직접 객체를 만들 수 없음
    private List<Card> cards; // <Card> -> Card 타입만 저장할 수 있다

    // 생성자
    public Deck() {
        cards = new ArrayList<>(); // ArrayList로 생성한다.
        // ArrayList는 List 인터페이스에 정의된 기능을 실제로 구현한 클래스
        initializeDeck(); // 초기화 메서드를 불러와서 List에 카드를 추가한다.
        shuffle(); // List를 섞는다.
    }

    // 무늬 4개 × 숫자 13개 = 카드 52장 생성하는 메서드
    private void initializeDeck() {
        for (String suit : SUITS) { // 향상된 for문으로 무늬배열을 돌면서
            for (int rank = 2; rank <= 14; rank++) { // 랭크 2~14까지 무늬마다
                cards.add(new Card(suit, rank)); // Card 객체를 생성해서 List에 추가한다.
            }
        }
    }

    // 카드 순서를 무작위로 섞는 메서드
    public void shuffle() { // Collections는 리스트 작업에 유용한 기능을 모아놓은 Java의 도구 클래스
        // shuffle()은 리스트 내부 요소의 순서를 무작위로 섞어줌
        Collections.shuffle(cards); // List에 저장된 카드의 순서를 무작위로 섞는다.
        // shuffle()이 새로운 리스트를 반환하는 것이 아니라, 전달받은 기존 리스트 자체의 순서를 바꾼다
    }

    // 카드 한 장 뽑는 메서드
    public Card draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("덱에 남아 있는 카드가 없습니다.");
        }

        return cards.remove(cards.size() - 1);
    }

    // 남아 있는 카드 수
    public int remainingCards() {
        return cards.size();
    }
}