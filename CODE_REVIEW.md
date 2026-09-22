# 🔍 AI 코드 리뷰 & 디버깅 일지

AI가 제시한 구조를 한 번에 복사해 완성하지 않고, 클래스별로 직접 입력하고 실행한 뒤
이해되지 않는 부분을 다시 질문했습니다.

---

## 1) AI 활용 코드의 한계/오류 분석

AI가 초반에 제안한 클래스 다이어그램에는 일반적인 카드게임에서 사용할 수 있는
`Card.equals()`, `Card.hashCode()`, `Deck.reset()`까지 포함되어 있었습니다.
이번 프로젝트는 베팅과 여러 라운드를 제외하고 텍사스 홀덤 한 판만 실행하므로, 현재
코드에서는 이 메서드들을 실제로 호출할 일이 없었습니다.

처음에는 클래스 다이어그램에 나온 메서드를 모두 만들어야 할까 생각했지만,
수업 코드와 현재 게임 흐름을 비교해 보면서 과도하게 복잡해지며 불필요한 기능까지 구현할 필요는 없다고 생각했습니다. 
필요한 기능만 남기고 실제 코드에 맞춰 클래스 다이어그램도 다시 수정했습니다.

**초기 AI 설계**

```java
// 이번 범위에서는 실제 사용 없었던 메서드
public boolean equals(Object obj) { ... }
public int hashCode() { ... }
public void reset() { ... }
```

**최종 적용**

```java
// Card에는 카드 한 장을 표현하고 출력하는 기능만 구현
public String getSuit() { ... }
public int getRank() { ... }
public String toString() { ... }

// Deck에는 한 판에서 사용하는 기능만 구현
public void shuffle() { ... }
public Card draw() { ... }
public int remainingCards() { ... }
```

## 2) Before & After 코드 비교

### 사례 1 — `Card` 객체에 전체 카드 후보를 저장한 모델링

**Before**

```java
private String[] suit = { "◆", "♥", "♣", "♠" };
private int[] rank = { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 };
```

이 코드는 `Card` 객체 하나가 무늬 4개와 숫자 13개를 모두 가지므로 카드 한 장이
아니라 카드 목록을 표현하고 있었습니다.

**After**

```java
private final String suit;
private final int rank;

public Card(String suit, int rank) {
    this.suit = suit;
    this.rank = rank;
}
```

**수정 이유**: `Card`는 특정 카드 한 장, `Deck`은 52장 생성과 보관을 담당하도록
분리했습니다. `final`을 사용해 생성된 카드의 무늬와 숫자가 게임 중에 바뀌지 않도록 했습니다.

---

### 사례 2 — 족보 계산과 승자 발표 책임 분리

**Before 개념**

```java
// 게임 클래스가 카드 숫자와 족보를 모두 직접 비교하는 방식
if (/* 플러시인지 검사 */) {
    // 키커까지 직접 비교
}
```

**After**

```java
HandResult userResult = evaluator.evaluate(userSevenCards);
HandResult computerResult = evaluator.evaluate(computerSevenCards);

int comparison = userResult.compareTo(computerResult);
```

**수정 이유**: `HandEvaluator`는 카드 분석, `HandResult`는 결과 보관 및 동급 족보의
세부 비교, 게임 진행 클래스는 비교 결과를 승자 이름으로 해석하도록 나눴습니다.
수업의 마방진 예제에서 생성 방식과 실행 책임을 클래스로 분리했던 구조를 카드게임에서 활용하려고 해 보았습니다.

---

## 3) 전체 코드 구조 리뷰

### `Card`와 `Deck` — 카드 한 장과 카드 묶음의 구분

처음에는 `Card` 안에 무늬 배열과 숫자 배열을 넣었기 때문에 카드 객체 하나가 가능한
카드 정보를 전부 가지고 있었습니다. 질문하면서 `Card` 객체 하나는 스페이드 A처럼
특정 카드 한 장의 상태만 가지는것이 지금의 게임을 제작할때에 더 맞겠다고 느꼈습니다.
그래서 `Card`에는 `suit`와 `rank`만 남기고, 무늬 4개와 숫자 13개를 조합해 52장을 만드는 일은
`Deck`으로 옮겼습니다.

`Deck`의 필드는 `List<Card>`로 선언했지만 실제 객체는 `new ArrayList<>()`로
만들었습니다. 이 부분도 처음에는 서로 다른 자료형처럼 보여 헷갈렸습니다. 수업에서
인터페이스 타입으로 변수를 선언했던 방식과 비교하면서, `List`는 사용할 기능을 정하고
`ArrayList`는 그 기능을 실제로 구현하는 클래스라는 점을 이해했습니다.
`Collections.shuffle(cards)`는 직접 난수와 중복 검사를 작성하는 대신, 완성된 카드
52장의 순서만 섞는 역할을 합니다.

### `Player` — 개인 카드 규칙과 내부 데이터 보호

`Player`는 이름과 개인 카드 2장만 관리합니다. 카드가 이미 2장인데 또 받으려고 하면
그냥 무시할 수도 있지만, 그러면 잘못된 게임 진행을 발견하기 어렵습니다. 그래서
`receiveCard()`에서 `IllegalStateException`을 발생시켜 현재 객체 상태로는 그 동작을
수행할 수 없다는 사실을 바로 알리도록 했습니다. 이 예외가 `RuntimeException` 계열이라
`try-catch` 작성이 강제되지 않는다는 것도 확인했습니다.

처음에는 `getHoleCards()`가 내부 리스트를 그대로 반환해도 된다고 생각했습니다.
하지만 외부에서 `player.getHoleCards().add(...)`를 실행하면 `receiveCard()`를 거치지
않고 카드 3장을 넣을 수 있습니다. 이를 막기 위해 `Collections.unmodifiableList()`로
조회만 가능한 형태를 반환했습니다. 이 부분을 통해 getter가 있다고 해서 내부 데이터를
무조건 그대로 공개해야 하는 것은 아니라는 점을 알게 되었습니다.

### `HandRank`, `HandEvaluator`, `HandResult` — 판정과 결과 비교의 분리

가장 헷갈렸던 부분은 `HandEvaluator`와 `HandResult`의 차이였습니다. 처음에는
`HandResult`가 직접 카드를 분석하거나, `compareTo()`를 호출할 때마다 카드 정보를 다시
받아야 한다고 생각했습니다.

실제 흐름을 따라가 보니 `HandEvaluator`가 카드 5장을 분석해 족보 종류,
`comparisonValues`, 최강 카드 5장을 모두 계산한 다음 완성된 `HandResult`를 만듭니다.
따라서 `HandResult`는 분석기 보다는 판정이 끝난 결과지 입니다.

`HandRank`는 족보 이름과 강도를 관리합니다. `HandResult.compareTo()`는 먼저
`HandRank`의 강도를 비교하고, 족보가 같을 때만 `comparisonValues`를 앞에서부터
비교합니다. 예를 들어 원 페어라면 `[페어 숫자, 높은 키커, 다음 키커, 마지막 키커]`
순으로 들어갑니다. 그래서 `comparisonValues`는 항상 확인하는 점수가 아니라 같은
족보가 나왔을 때 펼쳐 보는 동점 비교 기준이라고 이해했습니다.

7장 중 최강 5장을 찾을 때는 뽑을 5장을 고르는 대신 제외할 2장을 고릅니다. 두 개의
반복문에서 제외할 인덱스 두 개를 선택하면 총 21가지 조합이 만들어집니다. 각 조합은
먼저 `evaluateFive()`를 통해 완성된 `HandResult`가 된 뒤, 현재의 `bestResult`와
`compareTo()`로 비교됩니다.

### `TexasHoldemGame`과 `TexasHoldemMain` — 계산값을 실제 승패로 해석

`TexasHoldemGame`은 족보를 직접 계산하지 않습니다. 덱과 플레이어를 준비하고 개인
카드 배분, 공용카드가 공개되는 순서인(플롭, 턴, 리버)를 순서대로 진행한 다음, 각 플레이어의 카드 7장을 `HandEvaluator`에 전달합니다.

Evaluator가 반환한 사용자와 컴퓨터의 `HandResult`를 비교하면 양수, 음수, 0이
나옵니다. 게임 클래스는 이 숫자를 각각 사용자 승리, 컴퓨터 승리, 무승부로 해석해
출력합니다. 즉 비교 기준은 `HandResult`에 있고, 그 결과를 게임 상황에 맞는 문장으로
바꾸는 기능은 `TexasHoldemGame`에 있습니다.

`TexasHoldemMain`은 처음처럼 테스트 카드를 직접 만들거나 족보를 판정하지 않습니다.
게임 객체를 만든 뒤 `start()`만 호출합니다. 마방진 수업의 실행 클래스처럼 프로그램의
시작점만 담당하도록 정리했습니다.

### 클래스가 연결되는 전체 흐름

```text
TexasHoldemMain
→ TexasHoldemGame.start()
→ Deck에서 사용자와 컴퓨터에게 개인 카드 2장씩 배분
→ 공용 카드 5장 공개(플롭 3장 → 턴 1장 → 리버 1장)
→ 각 플레이어의 개인 카드 2장과 공용 카드 5장을 합쳐 7장 구성
→ HandEvaluator.evaluate()가 21가지 5장 조합을 검사
→ 사용자와 컴퓨터의 최강 HandResult 반환
→ userResult.compareTo(computerResult)
→ 양수는 사용자 승리, 음수는 컴퓨터 승리, 0은 무승부로 발표
```

처음에는 클래스가 많아져 오히려 복잡해 보였지만, 각 클래스가 담당하는 질문을 하나씩
정리하니 관계를 이해하기 쉬워졌습니다.

- 카드는 무엇인가? → `Card`
- 카드 52장은 누가 관리하는가? → `Deck`
- 개인 카드 2장은 누가 보관하는가? → `Player`
- 카드 조합의 족보는 누가 계산하는가? → `HandEvaluator`
- 계산된 결과와 동점 기준은 어디에 있는가? → `HandResult`, `HandRank`
- 게임 순서와 최종 발표는 누가 담당하는가? → `TexasHoldemGame`
- 프로그램은 어디에서 시작하는가? → `TexasHoldemMain`

이렇게 책임을 나누었기 때문에 족보 판정 부분을 확인할 때 게임 출력 코드까지 한꺼번에
볼 필요가 없고, 각 클래스를 따로 따라가며 이해하고 검증할 수 있었습니다.

### 클래스 다이어그램을 활용하며 느낀 점

처음부터 코드를 바로 작성했다면 어떤 클래스부터 만들어야 하는지, 다른 클래스와
어떻게 연결해야 하는지 더 많이 헷갈렸을 것 같습니다. 먼저 AI의 도움을 받아 클래스
다이어그램의 초안을 만들고, 그 그림에서 클래스별 필드와 메서드, 클래스 사이의 관계를
확인한 뒤 구현 순서를 잡으니 전체 흐름을 이해하는 데 도움이 되었습니다.

특히 `HandEvaluator`와 `HandResult`의 역할이 헷갈렸을 때 클래스 다이어그램을 다시
보면서, Evaluator가 Result를 만들어 반환하고 Game이 그 결과를 사용한다는 방향을
확인할 수 있었습니다. 코드를 한 줄씩 볼 때는 세부 문법에 집중하게 되지만, 다이어그램은
현재 작성하는 클래스가 전체 게임에서 어디에 위치하는지를 보여 주는 지도처럼 사용할 수
있었습니다.

AI가 만든 초기 다이어그램을 먼저 보면서 코드를 AI와 함께 물어봐 배우며 구현하였고,
실제 구현에서 사용하지 않는 메서드는 제외하고, 클래스 이름과 메서드가 바뀌면 현재 코드에 맞게 다이어그램도 다시 수정했습니다. 이를 통해 클래스 다이어그램은 구현 전에
큰 구조를 이해하고 만들면서 설계를 점검하도록 활용하는 것이 좋다고 느꼈습니다.
