package hk.texasholdem;

public class Card {

    // 이 카드 한 장의 무늬
    private final String suit;

    // 이 카드 한 장의 숫자: 2~14
    private final int rank;

    // 생성자
    public Card(String suit, int rank) {
        this.suit = suit;
        this.rank = rank;
    }

    // 게터
    public String getSuit() {
        return suit;
    }

    public int getRank() {
        return rank;
    }

    // toString을 오버라이딩해서 11~14는 JQKA로 바꿔주도록 한다.
    @Override
    public String toString() {
        String displayRank;

        switch (rank) {
            case 11:
                displayRank = "J";
                break;
            case 12:
                displayRank = "Q";
                break;
            case 13:
                displayRank = "K";
                break;
            case 14:
                displayRank = "A";
                break;
            default:
                displayRank = String.valueOf(rank);
        }

        return "[" + suit + displayRank + "]";
    }
}
