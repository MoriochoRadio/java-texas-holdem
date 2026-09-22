# Java Texas Hold'em Card Game

Java 수업에서 배운 클래스, 캡슐화, 컬렉션, 예외, `enum`, 인터페이스를 활용해 만드는
베팅 제외 2인용 텍사스 홀덤입니다. 기존 수업 코드는 보존하고 새 패키지
`src/hk/texasholdem`에서 별도로 구현했습니다.

> 이 저장소는 취업 아카데미 Java 과제로 제출한 결과물입니다. 수업 자료와 강사 코드는
> 포함하지 않고, 제가 직접 구현한 `hk.texasholdem` 패키지와 과제 문서만 분리해 담았습니다.
> 작업 과정은 [`PROMPT_LOG.md`](./PROMPT_LOG.md), 직접 잡은 버그와 수정 내역은
> [`CODE_REVIEW.md`](./CODE_REVIEW.md)에 있습니다.

## 구현 범위

- 조커 없는 카드 52장 생성 및 셔플
- 사용자와 컴퓨터의 개인 카드 2장 관리
- 포커 족보 9종 판정
- 같은 족보일 때 페어 숫자와 키커 비교
- 카드 7장 중 가능한 21개 조합을 검사해 최강 5장 선택
- 플롭·턴·리버 공개와 사용자·컴퓨터의 최종 승자 판정

베팅, 칩, 블라인드와 여러 라운드는 첫 구현 범위에서 제외했습니다. 현재는
`TexasHoldemGame`이 카드 배분부터 쇼다운과 승자 발표까지 한 판을 진행합니다.

## 주요 클래스

```text
Card
└─ 카드 한 장의 무늬와 숫자

Deck
└─ 카드 52장 생성, 셔플, 한 장 뽑기

Player
└─ 플레이어 이름과 개인 카드 2장 관리

HandRank
└─ 9가지 족보 이름과 강도

HandEvaluator
└─ 5장 족보 판정 및 7장 중 최강 5장 선택

HandResult
└─ 판정 결과 보관 및 다른 결과와 비교

TexasHoldemGame
└─ 개인 카드 배분, 공용 카드 공개, 쇼다운과 승자 발표

TexasHoldemMain
└─ 게임 객체를 만들고 한 판 시작
```

### 클래스 다이어그램

![클래스 다이어그램](./docs/class-diagram.png)

## 수업 코드 활용

- `D2_Card`, `D2_CardCase`: 카드 객체와 52장 목록이라는 기본 개념을 참고했습니다.
  기존의 랜덤 생성·중복 검사 대신 무늬 4개와 숫자 13개를 이중 반복문으로 정확히 만든
  후 `Collections.shuffle()`로 섞었습니다.
- 마방진 클래스 구조: 한 클래스가 모든 일을 하지 않고 생성, 판정, 실행 책임을
  분리하는 방식을 참고했습니다. 게임 종류가 하나뿐이므로 불필요한 Factory나 상속은
  추가하지 않았습니다.
- 수업의 인터페이스 개념을 `List`/`ArrayList`, `Comparable<HandResult>`에 연결해
  적용했습니다.

---

## 기여도 및 핵심 코드 명세서

### 1) 코드 기여도 구분

**[AI 도움을 받은 영역]**

- 텍사스 홀덤의 간소화 범위와 클래스 다이어그램 초안
- 클래스 구현 순서와 메서드 책임에 대한 설명
- `Collections.shuffle`, `unmodifiableList`, `IllegalStateException`, `enum`,
  `Comparable` 등 처음 접한 문법의 예제와 주석
- 작성한 파일의 읽기 검토, 별도 출력 폴더 컴파일 확인
- 요청 후 Main의 5장 테스트를 7장 테스트로 바꾼 통합 수정 1건

**[본인 직접 구현·검증한 영역]**

- `hk.texasholdem` 패키지와 Java 클래스 생성
- AI 설명을 이해한 뒤 각 클래스를 직접 입력하고 실행
- `Card` 배열 모델링 오류를 발견한 뒤 한 장의 상태로 수정
- 기존 수업 `D2_CardCase`와 비교해 52장 생성 방식을 결정
- `List`와 구현 클래스, 예외, 읽기 전용 리스트의 필요성을 재질문해 이해
- 족보별 `comparisonValues`의 순서를 확인하며 `HandEvaluator` 구현
- 7장 중 두 장을 제외하는 21가지 조합과 `compareTo()` 실행 흐름 검증
- 콘솔 카드 문양 문제와 Main 통합 테스트 결과 확인

전체 코드는 AI에게 한 번에 생성해 달라고 요청하지 않았습니다. 클래스 하나를 직접
작성하고 질문·실행·리뷰를 반복하는 방식으로 진행했습니다. 

### 2) 내가 직접 설명할 수 있는 핵심 코드

#### `HandEvaluator.evaluate()` — 7장 중 최강 5장 선택

```java
for (int firstExcluded = 0;
        firstExcluded < cards.size() - 1;
        firstExcluded++) {
    for (int secondExcluded = firstExcluded + 1;
            secondExcluded < cards.size();
            secondExcluded++) {

        List<Card> fiveCards = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++) {
            if (i != firstExcluded && i != secondExcluded) {
                fiveCards.add(cards.get(i));
            }
        }

        HandResult currentResult = evaluateFive(fiveCards);

        if (bestResult == null
                || currentResult.compareTo(bestResult) > 0) {
            bestResult = currentResult;
        }
    }
}
```

7장 중 5장을 직접 선택하는 대신 제외할 2장을 선택합니다. 두 번째 인덱스를 첫 번째
인덱스 다음부터 시작해 `(0, 1)`과 `(1, 0)` 같은 중복을 막고 총 21개 조합을 만듭니다.
각 조합은 `evaluateFive()`로 먼저 완전한 `HandResult`가 된 후, 현재 최강 결과와
비교됩니다.

#### `HandResult.compareTo()` — 족보와 키커의 단계적 비교

```java
if (this.handRank.getStrength()
        > other.handRank.getStrength()) {
    return 1;
}
if (this.handRank.getStrength()
        < other.handRank.getStrength()) {
    return -1;
}

for (int i = 0; i < comparisonValues.size(); i++) {
    int thisValue = this.comparisonValues.get(i);
    int otherValue = other.comparisonValues.get(i);

    if (thisValue > otherValue) return 1;
    if (thisValue < otherValue) return -1;
}
return 0;
```

먼저 `HandRank` 강도로 서로 다른 족보의 승패를 결정합니다. 같은 족보일 때만
`comparisonValues`를 중요한 순서부터 비교합니다. 예를 들어 원 페어의 비교값은
`[페어 숫자, 높은 키커, 다음 키커, 마지막 키커]`이고, 모든 값까지 같을 때만
무승부인 `0`을 반환합니다.

## 실행 방법

JDK 17 이상이 필요합니다 (개발·검증 환경: JDK 21).

```bash
javac -encoding UTF-8 -d out src/hk/texasholdem/*.java
java -Dstdout.encoding=UTF-8 -cp out hk.texasholdem.TexasHoldemMain
```

`-encoding` / `-Dstdout.encoding` 옵션은 Windows 콘솔에서 카드 문양(`♠ ♥ ◆ ♣`)과
한글이 깨지지 않게 하기 위한 것입니다. IDE에서 실행할 때는 `hk.texasholdem.TexasHoldemMain`을
main 클래스로 지정하면 됩니다.

### 실행 예시

```text
==============================
간소화 텍사스 홀덤을 시작합니다.
==============================

내 개인 카드: [[◆3], [◆Q]]
컴퓨터 개인 카드: [비공개]

[플롭] 공용 카드 3장 공개
공용 카드: [[◆6], [◆8], [♣3]]

[턴] 공용 카드 1장 공개: [♠7]
공용 카드: [[◆6], [◆8], [♣3], [♠7]]

[리버] 공용 카드 1장 공개: [♣2]
공용 카드: [[◆6], [◆8], [♣3], [♠7], [♣2]]

==============================
쇼다운
==============================
사용자 개인 카드: [[◆3], [◆Q]]
컴퓨터 개인 카드: [[♣4], [♥9]]
공용 카드: [[◆6], [◆8], [♣3], [♠7], [♣2]]

사용자 족보: 원 페어 / 비교값: [3, 12, 8, 7] / 최종 카드: [[◆Q], [◆8], [♠7], [♣3], [◆3]]
컴퓨터 족보: 하이 카드 / 비교값: [9, 8, 7, 6, 4] / 최종 카드: [[♥9], [◆8], [♠7], [◆6], [♣4]]

최종 결과: 사용자 승리!
덱에 남은 카드: 43
```

## 프로젝트 구조

```text
java-texas-holdem/
├── src/hk/texasholdem/     # 구현 코드 8개 클래스
├── docs/class-diagram.png  # 클래스 다이어그램
├── README.md               # 이 문서 (설계·기여도·핵심 코드 설명)
├── PROMPT_LOG.md           # AI 프롬프트 일지
└── CODE_REVIEW.md          # 코드 리뷰 및 수정 이력
```
