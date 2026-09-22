package hk.texasholdem;

import java.util.ArrayList;
import java.util.List;

// 텍사스 홀덤 한 판의 전체 진행을 담당한다.
//
// 이 클래스는 족보를 직접 계산하지 않는다.
// 카드 족보 계산은 HandEvaluator에게 맡긴다.
public class TexasHoldemGame {

    // 이번 게임에서 사용할 카드 52장
    private final Deck deck;

    // 게임에 참여하는 사용자와 컴퓨터
    private final Player user;
    private final Player computer;

    // 사용자와 컴퓨터가 함께 사용하는 공용 카드
    // 플롭 3장 + 턴 1장 + 리버 1장 = 총 5장
    private final List<Card> communityCards;

    // 카드 7장에서 가장 강한 족보를 계산하는 객체
    private final HandEvaluator evaluator;

    // 게임에 필요한 모든 객체를 준비한다.
    public TexasHoldemGame() {

        // Deck 생성자에서 카드 52장을 만들고 섞는다.
        deck = new Deck();

        user = new Player("사용자");
        computer = new Player("컴퓨터");

        communityCards = new ArrayList<>();

        evaluator = new HandEvaluator();
    }

    // 텍사스 홀덤 한 판을 순서대로 진행한다.
    public void start() {

        System.out.println("==============================");
        System.out.println("간소화 텍사스 홀덤을 시작합니다.");
        System.out.println("==============================");

        // 각 플레이어에게 개인 카드 2장 지급
        dealHoleCards();

        System.out.println();
        System.out.println("내 개인 카드: "
                + user.getHoleCards());

        // 컴퓨터 카드는 최종 승부 전까지 공개하지 않는다.
        System.out.println("컴퓨터 개인 카드: [비공개]");

        // 플롭 3장 공개
        revealFlop();

        // 턴 1장 공개
        revealTurn();

        // 리버 1장 공개
        revealRiver();

        // 모든 카드가 공개됐으므로 최종 족보와 승자를 결정한다.
        showdown();
    }

    // 사용자와 컴퓨터에게 번갈아 카드 2장씩 나눠준다.
    private void dealHoleCards() {

        // 반복문 한 번에 사용자와 컴퓨터가 한 장씩 받는다.
        // 두 번 반복하므로 각 플레이어가 2장씩 받는다.
        for (int i = 0; i < 2; i++) {
            user.receiveCard(deck.draw());
            computer.receiveCard(deck.draw());
        }
    }

    // 첫 번째 공용 카드 단계
    // 플롭에서는 공용 카드 3장을 공개한다.
    private void revealFlop() {

        communityCards.add(deck.draw());
        communityCards.add(deck.draw());
        communityCards.add(deck.draw());

        System.out.println();
        System.out.println("[플롭] 공용 카드 3장 공개");
        System.out.println("공용 카드: " + communityCards);
    }

    // 두 번째 공용 카드 단계
    // 턴에서는 공용 카드 1장을 추가한다.
    private void revealTurn() {

        Card turnCard = deck.draw();
        communityCards.add(turnCard);

        System.out.println();
        System.out.println("[턴] 공용 카드 1장 공개: "
                + turnCard);
        System.out.println("공용 카드: " + communityCards);
    }

    // 세 번째 공용 카드 단계
    // 리버에서는 마지막 공용 카드 1장을 추가한다.
    private void revealRiver() {

        Card riverCard = deck.draw();
        communityCards.add(riverCard);

        System.out.println();
        System.out.println("[리버] 공용 카드 1장 공개: "
                + riverCard);
        System.out.println("공용 카드: " + communityCards);
    }

    // 개인 카드 2장과 공용 카드 5장을 합쳐
    // HandEvaluator에 전달할 카드 7장을 만든다.
    private List<Card> createSevenCards(Player player) {

        // 플레이어마다 별도의 7장 리스트를 만든다.
        List<Card> sevenCards = new ArrayList<>();

        // 플레이어의 개인 카드 2장을 추가한다.
        sevenCards.addAll(player.getHoleCards());

        // 모든 플레이어가 공유하는 공용 카드 5장을 추가한다.
        sevenCards.addAll(communityCards);

        return sevenCards;
    }

    // 사용자와 컴퓨터의 최종 족보를 비교한다.
    private void showdown() {

        System.out.println();
        System.out.println("==============================");
        System.out.println("쇼다운");
        System.out.println("==============================");

        // 승부 시점에 컴퓨터의 개인 카드를 공개한다.
        System.out.println("사용자 개인 카드: "
                + user.getHoleCards());
        System.out.println("컴퓨터 개인 카드: "
                + computer.getHoleCards());
        System.out.println("공용 카드: "
                + communityCards);

        // 사용자 개인 카드 2장과 공용 카드 5장을 합친다.
        List<Card> userSevenCards = createSevenCards(user);

        // 컴퓨터 개인 카드 2장과 공용 카드 5장을 합친다.
        List<Card> computerSevenCards = createSevenCards(computer);

        // 각 7장에서 가장 강한 5장 조합을 판정한다.
        HandResult userResult = evaluator.evaluate(userSevenCards);

        HandResult computerResult = evaluator.evaluate(computerSevenCards);

        System.out.println();
        System.out.println("사용자 족보: " + userResult);
        System.out.println("컴퓨터 족보: " + computerResult);

        // 사용자의 결과를 기준으로 컴퓨터의 결과와 비교한다.
        int comparison = userResult.compareTo(computerResult);

        System.out.println();

        if (comparison > 0) {
            System.out.println("최종 결과: 사용자 승리!");
        } else if (comparison < 0) {
            System.out.println("최종 결과: 컴퓨터 승리!");
        } else {
            System.out.println("최종 결과: 무승부!");
        }

        // 개인 카드 4장과 공용 카드 5장을 사용했으므로
        // 덱에는 43장이 남아야 한다.
        System.out.println("덱에 남은 카드: "
                + deck.remainingCards());
    }
}