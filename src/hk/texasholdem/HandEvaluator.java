package hk.texasholdem;

import java.util.ArrayList;
import java.util.List;

// 카드들을 분석해서 족보 판정 결과인
// HandResult 객체를 만들어 주는 클래스
public class HandEvaluator {

    // 텍사스 홀덤에서 사용할 카드 7장을 받아
    // 그중 가장 강한 카드 5장의 HandResult를 반환한다.
    public HandResult evaluate(List<Card> cards) {

        // 개인 카드 2장 + 공용 카드 5장으로
        // 정확히 7장이 전달돼야 한다.
        if (cards.size() != 7) {
            throw new IllegalArgumentException(
                    "텍사스 홀덤 족보 판정에는 정확히 카드 7장이 필요합니다.");
        }

        // 지금까지 발견한 가장 강한 판정 결과를 저장한다.
        //
        // 아직 어떤 조합도 판정하지 않았으므로
        // 처음에는 null로 둔다.
        HandResult bestResult = null;

        // 7장 중 첫 번째로 버릴 카드의 위치를 선택한다.
        //
        // 마지막 카드는 두 번째 카드로만 선택할 수 있으므로
        // cards.size() - 1 전까지만 반복한다.
        for (int firstExcluded = 0; firstExcluded < cards.size() - 1; firstExcluded++) {

            // 두 번째로 버릴 카드의 위치를 선택한다.
            //
            // 첫 번째 카드보다 뒤에서 시작하면
            // 같은 조합을 중복해서 확인하지 않는다.
            //
            // 예:
            // (0, 1)은 확인하지만 (1, 0)은 확인하지 않는다.
            for (int secondExcluded = firstExcluded + 1; secondExcluded < cards.size(); secondExcluded++) {

                // 현재 선택에서 남길 카드 5장을 저장한다.
                List<Card> fiveCards = new ArrayList<>();

                // 카드 7장을 처음부터 확인한다.
                for (int i = 0; i < cards.size(); i++) {

                    // 버리기로 선택한 두 위치가 아니라면
                    // 카드 5장 목록에 추가한다.
                    if (i != firstExcluded
                            && i != secondExcluded) {

                        fiveCards.add(cards.get(i));
                    }
                }

                // 선택된 카드 5장의 족보를 판정한다.
                HandResult currentResult = evaluateFive(fiveCards);

                // 첫 번째 판정 결과라면 비교 대상이 없으므로
                // 그대로 가장 강한 결과로 저장한다.
                if (bestResult == null) {
                    bestResult = currentResult;
                }

                // 첫 번째 결과가 아니면 기존 최강 결과와 비교한다.
                //
                // compareTo()가 양수를 반환하면
                // currentResult가 bestResult보다 강하다는 뜻이다.
                else if (currentResult.compareTo(bestResult) > 0) {
                    bestResult = currentResult;
                }
            }
        }

        // 21가지 조합을 모두 확인한 뒤
        // 가장 강했던 판정 결과를 반환한다.
        return bestResult;
    }

    // 정확히 카드 5장을 받아 족보를 판정한다.
    //
    // 카드 5장을 분석하여 9가지 족보 중 하나를 판정한다.
    public HandResult evaluateFive(List<Card> cards) {

        // 이 메서드는 정확히 카드 5장만 판정한다.
        if (cards.size() != 5) {
            throw new IllegalArgumentException(
                    "족보 판정에는 정확히 카드 5장이 필요합니다.");
        }

        // 원본 리스트의 순서를 바꾸지 않기 위해 복사본을 만든다.
        List<Card> sortedCards = new ArrayList<>(cards);

        // 숫자가 높은 카드부터 정렬한다.
        sortByRankDescending(sortedCards);

        // 카드 5장의 무늬가 모두 같은지 확인한다.
        boolean flush = isFlush(sortedCards);

        // 스트레이트라면 가장 높은 숫자를 반환하고,
        // 스트레이트가 아니라면 0을 반환한다.
        int straightHighRank = getStraightHighRank(sortedCards);

        // 가장 높은 숫자가 0보다 크다면 스트레이트다.
        boolean straight = straightHighRank > 0;

        // 카드 숫자별 개수를 저장할 배열이다.
        //
        // 카드 숫자는 2~14이므로 배열 길이를 15로 만든다.
        // 인덱스 0과 1은 사용하지 않는다.
        int[] rankCounts = new int[15];

        // 카드 5장을 확인하면서 해당 숫자의 개수를 1씩 증가시킨다.
        //
        // 예: 숫자 10 카드가 두 번 나오면
        // rankCounts[10]의 최종 값은 2가 된다.
        for (Card card : sortedCards) {
            int rank = card.getRank();
            rankCounts[rank]++;
        }

        // 같은 숫자 4장이 발견되면 해당 숫자를 저장한다.
        // 발견되지 않은 상태를 나타내기 위해 처음에는 0으로 둔다.
        int fourRank = 0;

        // 같은 숫자 3장이 발견되면 해당 숫자를 저장한다.
        int threeRank = 0;

        // 같은 숫자 2장이 여러 종류 나올 수 있으므로 List로 저장한다.
        //
        // 원 페어면 숫자 하나가 저장되고,
        // 투 페어면 숫자 두 개가 저장된다.
        List<Integer> pairRanks = new ArrayList<>();

        // 높은 숫자부터 확인한다.
        //
        // 투 페어일 때 높은 페어가 pairRanks의
        // 앞쪽에 저장되게 하기 위해 14부터 2까지 내려간다.
        for (int rank = 14; rank >= 2; rank--) {

            if (rankCounts[rank] == 4) {
                fourRank = rank;
            } else if (rankCounts[rank] == 3) {
                threeRank = rank;
            } else if (rankCounts[rank] == 2) {
                pairRanks.add(rank);
            }
        }

        // 같은 족보끼리 비교할 숫자를 저장한다.
        List<Integer> comparisonValues = new ArrayList<>();

        // ==================================================
        // 1. 스트레이트 플러시 판정
        // ==================================================
        //
        // 스트레이트이면서 플러시인 경우다.
        //
        // 같은 스트레이트 플러시끼리는
        // 가장 높은 카드 숫자 하나만 비교하면 된다.
        //
        // 예:
        // 9, 8, 7, 6, 5 → 비교값 [9]
        // A, 5, 4, 3, 2 → 비교값 [5]
        if (straight && flush) {

            comparisonValues.add(straightHighRank);

            return new HandResult(
                    HandRank.STRAIGHT_FLUSH,
                    comparisonValues,
                    sortedCards);
        }

        // ==================================================
        // 1. 포카드 판정
        // ==================================================
        //
        // 비교 순서:
        // [포카드 숫자, 남은 키커]
        //
        // 예: 5 포카드 + A
        // [5, 14]
        if (fourRank > 0) {

            comparisonValues.add(fourRank);

            // 포카드가 아닌 나머지 카드 한 장이 키커다.
            for (Card card : sortedCards) {
                if (card.getRank() != fourRank) {
                    comparisonValues.add(card.getRank());
                }
            }

            return new HandResult(
                    HandRank.FOUR_OF_A_KIND,
                    comparisonValues,
                    sortedCards);
        }

        // ==================================================
        // 2. 풀하우스 판정
        // ==================================================
        //
        // 트리플 1개와 페어 1개가 함께 있으면 풀하우스다.
        //
        // 비교 순서:
        // [트리플 숫자, 페어 숫자]
        //
        // 예: Q 트리플 + 8 페어
        // [12, 8]
        if (threeRank > 0 && pairRanks.size() == 1) {

            comparisonValues.add(threeRank);
            comparisonValues.add(pairRanks.get(0));

            return new HandResult(
                    HandRank.FULL_HOUSE,
                    comparisonValues,
                    sortedCards);
        }

        // ==================================================
        // 4. 플러시 판정
        // ==================================================
        //
        // 무늬가 모두 같고 숫자는 연속되지 않은 경우다.
        //
        // 플러시끼리는 높은 카드부터 차례대로 비교한다.
        //
        // 예: A, J, 9, 7, 3
        // 비교값: [14, 11, 9, 7, 3]
        if (flush) {

            for (Card card : sortedCards) {
                comparisonValues.add(card.getRank());
            }

            return new HandResult(
                    HandRank.FLUSH,
                    comparisonValues,
                    sortedCards);
        }

        // ==================================================
        // 5. 스트레이트 판정
        // ==================================================
        //
        // 숫자가 연속되고 무늬는 모두 같지 않은 경우다.
        //
        // 스트레이트끼리는 가장 높은 숫자 하나만 비교한다.
        //
        // 예:
        // 10, 9, 8, 7, 6 → 비교값 [10]
        // A, 5, 4, 3, 2 → 비교값 [5]
        if (straight) {

            comparisonValues.add(straightHighRank);

            return new HandResult(
                    HandRank.STRAIGHT,
                    comparisonValues,
                    sortedCards);
        }

        // ==================================================
        // 3. 트리플 판정
        // ==================================================
        //
        // 비교 순서:
        // [트리플 숫자, 높은 키커, 낮은 키커]
        //
        // 예: 9 트리플 + A, 4
        // [9, 14, 4]
        if (threeRank > 0) {

            comparisonValues.add(threeRank);

            // 트리플이 아닌 카드들을 높은 순서대로 추가한다.
            // sortedCards가 이미 내림차순이므로 별도 정렬이 필요 없다.
            for (Card card : sortedCards) {
                if (card.getRank() != threeRank) {
                    comparisonValues.add(card.getRank());
                }
            }

            return new HandResult(
                    HandRank.THREE_OF_A_KIND,
                    comparisonValues,
                    sortedCards);
        }

        // ==================================================
        // 4. 투 페어 판정
        // ==================================================
        //
        // pairRanks에는 높은 페어부터 저장되어 있다.
        //
        // 비교 순서:
        // [높은 페어, 낮은 페어, 키커]
        //
        // 예: K 페어 + 7 페어 + A
        // [13, 7, 14]
        if (pairRanks.size() == 2) {

            int highPairRank = pairRanks.get(0);
            int lowPairRank = pairRanks.get(1);

            comparisonValues.add(highPairRank);
            comparisonValues.add(lowPairRank);

            // 두 페어에 포함되지 않은 카드 한 장이 키커다.
            for (Card card : sortedCards) {
                if (card.getRank() != highPairRank
                        && card.getRank() != lowPairRank) {

                    comparisonValues.add(card.getRank());
                }
            }

            return new HandResult(
                    HandRank.TWO_PAIR,
                    comparisonValues,
                    sortedCards);
        }

        // ==================================================
        // 5. 원 페어 판정
        // ==================================================
        //
        // 비교 순서:
        // [페어 숫자, 키커1, 키커2, 키커3]
        //
        // 예: 10 페어 + A, 8, 4
        // [10, 14, 8, 4]
        if (pairRanks.size() == 1) {

            int pairRank = pairRanks.get(0);

            comparisonValues.add(pairRank);

            // 페어가 아닌 카드들을 높은 순서대로 추가한다.
            for (Card card : sortedCards) {
                if (card.getRank() != pairRank) {
                    comparisonValues.add(card.getRank());
                }
            }

            return new HandResult(
                    HandRank.ONE_PAIR,
                    comparisonValues,
                    sortedCards);
        }

        // ==================================================
        // 6. 하이 카드 판정
        // ==================================================
        //
        // 위 조건에 모두 해당하지 않으면 현재 단계에서는
        // 하이 카드로 판정한다.
        //
        // 비교값에는 카드 숫자 5개를 높은 순서대로 넣는다.
        for (Card card : sortedCards) {
            comparisonValues.add(card.getRank());
        }

        return new HandResult(
                HandRank.HIGH_CARD,
                comparisonValues,
                sortedCards);
    }

    // 카드 5장의 무늬가 모두 같은지 확인한다.
    private boolean isFlush(List<Card> cards) {

        // 첫 번째 카드의 무늬를 비교 기준으로 잡는다.
        String firstSuit = cards.get(0).getSuit();

        // 두 번째 카드부터 첫 번째 카드의 무늬와 비교한다.
        for (int i = 1; i < cards.size(); i++) {

            // String은 ==가 아니라 equals()로 내용을 비교한다.
            if (!firstSuit.equals(cards.get(i).getSuit())) {

                // 무늬가 다른 카드가 하나라도 발견되면
                // 플러시가 아니므로 즉시 false를 반환한다.
                return false;
            }
        }

        // 반복문이 끝날 때까지 다른 무늬가 없었으므로
        // 카드 5장의 무늬가 모두 같다.
        return true;
    }

    // 스트레이트라면 가장 높은 카드 숫자를 반환한다.
    // 스트레이트가 아니라면 0을 반환한다.
    private int getStraightHighRank(List<Card> sortedCards) {

        // 일반적인 스트레이트인지 먼저 확인한다.
        //
        // 정렬된 숫자가:
        // 10, 9, 8, 7, 6
        //
        // 앞 숫자 - 1 == 뒤 숫자 관계인지 확인한다.
        boolean normalStraight = true;

        for (int i = 0; i < sortedCards.size() - 1; i++) {

            int currentRank = sortedCards.get(i).getRank();
            int nextRank = sortedCards.get(i + 1).getRank();

            // 숫자가 정확히 1씩 감소하지 않는다면
            // 일반적인 스트레이트가 아니다.
            if (currentRank - 1 != nextRank) {
                normalStraight = false;
                break;
            }
        }

        if (normalStraight) {

            // 내림차순으로 정렬했으므로
            // 첫 번째 카드가 스트레이트의 가장 높은 카드다.
            return sortedCards.get(0).getRank();
        }

        // A, 5, 4, 3, 2는 특별한 스트레이트다.
        //
        // 일반적으로 A는 14로 사용하지만 이 조합에서만
        // 숫자 1처럼 취급한다. 이를 휠 스트레이트라고 한다.
        boolean aceLowStraight = sortedCards.get(0).getRank() == 14
                && sortedCards.get(1).getRank() == 5
                && sortedCards.get(2).getRank() == 4
                && sortedCards.get(3).getRank() == 3
                && sortedCards.get(4).getRank() == 2;

        if (aceLowStraight) {

            // A를 가장 낮은 카드로 사용하므로
            // 이 스트레이트의 최고 숫자는 5다.
            return 5;
        }

        // 일반 스트레이트도 아니고
        // A, 5, 4, 3, 2도 아니므로 스트레이트가 아니다.
        return 0;
    }

    // 전달받은 카드 리스트를 숫자가 높은 순서로 정렬한다.
    //
    // 예:
    // 정렬 전: [3, A, 7, J, 9]
    // 정렬 후: [A, J, 9, 7, 3]
    private void sortByRankDescending(List<Card> cards) {

        // i는 현재 높은 카드를 배치할 위치다.
        for (int i = 0; i < cards.size() - 1; i++) {

            // i 뒤에 있는 카드들을 하나씩 확인한다.
            for (int j = i + 1; j < cards.size(); j++) {

                // i 위치의 카드보다 j 위치의 카드가 더 크다면
                // 두 카드의 위치를 서로 바꾼다.
                if (cards.get(i).getRank() < cards.get(j).getRank()) {

                    // 교환하기 전에 i 위치의 카드를 임시 저장한다.
                    Card temp = cards.get(i);

                    // j의 높은 카드를 i 위치로 옮긴다.
                    cards.set(i, cards.get(j));

                    // 임시 저장한 카드를 j 위치로 옮긴다.
                    cards.set(j, temp);
                }
            }
        }
    }

}
