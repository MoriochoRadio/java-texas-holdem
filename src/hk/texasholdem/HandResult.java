package hk.texasholdem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Comparable은 자바가 기본으로 제공하는 나랑 같은 종류의 객체와 크기를 비교할 수 있는 능력을 부여하는 인터페이스
//Comparable 뒤의 <HandResult>는 누구랑 비교할 건지 비교 대상의 종류(타입)를 지정한 것
//인터페이스 Comparable<T>를 구현하려면 자바 규칙에 따라 compareTo()라는 승패 판정 메서드를 반드시 오버라이딩해야함

// Comparable<HandResult>를 구현하면
// HandResult 객체끼리 compareTo()로 크기를 비교할 수 있다.
public class HandResult implements Comparable<HandResult> {

    // 판정된 족보 종류
    // 예: ONE_PAIR, STRAIGHT, FULL_HOUSE
    private final HandRank handRank;

    // 같은 족보끼리 비교할 때 사용할 숫자들을
    // 중요한 순서대로 저장한다.
    //
    // 예: 10 원 페어 + A, 8, 4 키커
    // [10, 14, 8, 4]
    private final List<Integer> comparisonValues;

    // 7장 중 실제 족보를 구성하는 가장 강한 카드 5장
    private final List<Card> bestFiveCards;

    // HandEvaluator가 족보 판정을 끝낸 뒤
    // 판정 결과를 전달하여 HandResult 객체를 생성한다.
    public HandResult(
            HandRank handRank,
            List<Integer> comparisonValues,
            List<Card> bestFiveCards) {

        this.handRank = handRank;

        // 전달받은 리스트를 그대로 저장하지 않고
        // 새로운 ArrayList에 복사하여 저장한다.
        //
        // 외부에서 원래 리스트를 수정해도
        // HandResult 내부의 판정 결과가 변하지 않게 하기 위해서다.
        this.comparisonValues = new ArrayList<>(comparisonValues);
        this.bestFiveCards = new ArrayList<>(bestFiveCards);
    }

    public HandRank getHandRank() {
        return handRank;
    }

    // 외부에서는 비교값을 조회만 할 수 있게 한다.
    public List<Integer> getComparisonValues() {
        return Collections.unmodifiableList(comparisonValues);
    }

    // 외부에서는 최종 카드도 조회만 할 수 있게 한다.
    public List<Card> getBestFiveCards() {
        return Collections.unmodifiableList(bestFiveCards);
    }

    // 다른 HandResult와 강도를 비교한다.
    //
    // 양수 반환: 현재 객체가 더 강함
    // 음수 반환: other가 더 강함
    // 0 반환: 두 결과의 강도가 완전히 같음
    @Override
    public int compareTo(HandResult other) {

        // 1단계: 먼저 족보 자체의 강도를 비교한다.
        //
        // 예: TWO_PAIR(3)와 ONE_PAIR(2)를 비교하면
        // TWO_PAIR 쪽이 더 강하다.
        if (this.handRank.getStrength() > other.handRank.getStrength()) {
            return 1;
        }

        if (this.handRank.getStrength() < other.handRank.getStrength()) {
            return -1;
        }

        // 여기까지 왔다면 두 결과의 족보 종류가 같다.
        // 이제 동점 비교용 숫자를 앞에서부터 비교한다.
        //
        // 예:
        // [10, 14, 8, 4]
        // [10, 13, 12, 9]
        //
        // 첫 값 10은 같고, 두 번째 값에서 14 > 13이므로
        // 첫 번째 결과가 더 강하다.
        for (int i = 0; i < comparisonValues.size(); i++) {

            int thisValue = this.comparisonValues.get(i);
            int otherValue = other.comparisonValues.get(i);

            if (thisValue > otherValue) {
                return 1;
            }

            if (thisValue < otherValue) {
                return -1;
            }
        }

        // 족보 등급과 모든 비교값이 같다면 진짜 무승부다.
        return 0;
    }

    // 판정 결과를 확인하기 위한 출력 형식
    @Override
    public String toString() {
        return handRank.getKoreanName()
                + " / 비교값: " + comparisonValues
                + " / 최종 카드: " + bestFiveCards;
    }
}