# Cờ Caro BK — Tic-tac-toe 3×3 có máy chơi

Đồ án môn **IT4785 – Lập trình di động**. Game cờ caro 3×3 (tic-tac-toe) trên Android, có 3 chế độ: **đấu máy dễ**, **đấu máy khó** (minimax, không bao giờ thua) và **hai người chơi trên cùng máy**.

> Slogan: *"Ba quân thẳng hàng là qua môn."*
> Phụ đề trên Menu: *"Tư duy chiến thuật · Phiên bản BK"*

---

## 1. Bối cảnh (Claude Code đọc kỹ trước khi viết code)

- Môn học diễn ra 21/10/2026 – 03/11/2026. **Báo cáo trước giáo viên: 03/11/2026, khoảng 15 phút.** Nhóm 2–3 người.
- Yêu cầu của giáo viên: một app Android chạy được, áp dụng kiến thức đã học, được dùng AI hỗ trợ.
- Giáo trình (13 chương): Kotlin cơ bản → Hàm → Lớp & đối tượng → App Android đầu tiên → Layout → Navigation/Fragment → Lifecycle → ViewModel/LiveData → Room/Coroutines → RecyclerView nâng cao → Internet.
- **Cách làm của nhóm:** AI viết code, nhưng **mọi thành viên phải hiểu và trả lời được khi thầy hỏi**. Vì vậy:
  - Code phải **đơn giản, rõ ràng, dễ giải thích**. Không dùng kỹ thuật "khoe".
  - Kiến thức Kotlin Chương 1–3 phải thể hiện rõ, vì đây là phần chấm chính.
  - Được dùng kiến thức chưa dạy ở mức có trong giáo trình (ViewModel/LiveData Ch8, coroutine `viewModelScope` Ch9) và phải giải thích được.
- Chỉ **một máy** (máy trưởng nhóm) chạy Claude Code để build. Cả nhóm đọc code và tài liệu trong `docs/`.

## 2. Stack (đã có trong code; muốn đổi phải hỏi nhóm)

| Hạng mục | Lựa chọn |
|---|---|
| Ngôn ngữ | Kotlin |
| Build | Gradle Kotlin DSL + version catalog (`gradle/libs.versions.toml`), AGP 9.5 |
| SDK | `minSdk 26`, `compileSdk`/`targetSdk 37` |
| Package | `vn.edu.hust.tictactoe` |
| Tên hiển thị | `Cờ Caro BK` |
| Giao diện | **XML Views + ConstraintLayout/LinearLayout + View Binding**, Material Components 3 (MaterialCardView, MaterialButton, MaterialAlertDialog) |
| Màn hình | **Nhiều Activity + Intent**: Splash → Menu → Game |
| Tầng giao diện | **ViewModel (`AndroidViewModel`) + LiveData** cho GameActivity: giữ ván khi Activity bị tạo lại (đổi sáng/tối) |
| Bàn cờ | `GridLayout` 3×3 chứa 9 **`CellView`** (View tự vẽ bằng `Canvas`) |
| Máy chơi | `object Ai`: dễ = thắng ngay nếu được, còn lại ngẫu nhiên; khó = **minimax** |
| Độ trễ nước máy | `viewModelScope.launch { delay(450) }` (coroutine) |
| Lưu trữ | **SharedPreferences**: thành tích đấu máy (`stats`), chế độ sáng/tối (`settings`) |
| Giao diện sáng/tối | `AppCompatDelegate.setDefaultNightMode`, áp dụng trong `TictactoeApp.onCreate` |
| Test | JUnit 4 (`GameTest.kt`, `BoardAndAiTest.kt`) + `Demo.kt` có hàm `main()` |

**Dependency ngoài template:** `lifecycle-viewmodel-ktx`, `lifecycle-livedata-ktx`, `activity-ktx` (cho `by viewModels()`). Muốn thêm gì khác thì hỏi nhóm.

**KHÔNG dùng:** Jetpack Compose, Navigation Component, Fragment, Hilt/Koin/Dagger, Flow, RxJava, thư viện game/animation bên ngoài.

Môi trường: Windows, Android Studio bản mới nhất. Lệnh build `gradlew.bat assembleDebug`, test `gradlew.bat testDebugUnitTest`.

## 3. Luật chơi

- Bàn cờ 3×3, ô đánh số **0..8** theo từng hàng:
  ```
  0 | 1 | 2
  3 | 4 | 5
  6 | 7 | 8
  ```
- Hai quân X và O đánh xen kẽ. Không được đánh vào ô đã có quân, không được đánh khi ván đã xong.
- **Thắng:** ba quân cùng loại trên một trong **8 đường** (3 hàng, 3 cột, 2 đường chéo).
- **Hòa:** bàn đầy mà không ai thắng.
- **Người chơi luôn là X**; khi đấu máy thì máy là O.
- **Ván mới:** đổi bên đi trước (X rồi O rồi X…) cho công bằng. Nếu O đi trước khi đấu máy thì máy đánh nước đầu.
- **Reset:** xóa bàn, đưa bảng điểm của phiên về 0, X đi trước. Thành tích lưu lâu dài **không** bị xóa.

## 4. Máy chơi (`domain/Ai.kt`)

| Mức | Cách chọn nước | Đặc điểm |
|---|---|---|
| Dễ | Nếu có ô giúp thắng ngay → đánh ô đó. Còn lại chọn **ngẫu nhiên** | **Không chặn** người chơi nên người chơi thắng được |
| Khó | **Minimax** duyệt hết mọi nước đi tới cuối ván | **Không bao giờ thua**; người chơi giỏi nhất cũng chỉ hòa |

Điểm minimax theo góc nhìn của máy:
- Máy thắng: `1 + số ô trống` (thắng càng sớm điểm càng cao)
- Máy thua: `-1 - số ô trống` (thua càng muộn càng đỡ)
- Hòa: `0`
- Lượt máy chọn **max**, lượt đối thủ chọn **min**. Nhiều nước cùng điểm cao nhất thì chọn ngẫu nhiên cho đỡ nhàm.

Từ bàn trống, cây trò chơi có **549.946 nút**. Đây là lý do máy khó hơi "nghĩ" lâu ở nước đầu (xem mục 10).

## 5. Màn hình

1. **SplashActivity** (LAUNCHER): logo phóng to (`OvershootInterpolator`), hiện tên game, sau 2 giây chuyển sang Menu và `finish()`.
2. **MenuActivity**: tên game, phụ đề, thẻ **3 chế độ** (icon + tên + phụ đề + mũi tên), thẻ **thành tích** (Thắng/Hòa/Thua cho máy dễ và máy khó), nút **Xóa thành tích** (có hộp thoại xác nhận), nút đổi **sáng/tối**. Cập nhật thành tích trong `onResume`.
3. **GameActivity** (khóa dọc): tiêu đề chế độ, bảng điểm phiên (X · Hòa · O, cột đang đến lượt được tô nền), dòng trạng thái kèm icon quân, lưới 3×3, nút **Ván mới**, **Reset**, **Về menu**, **thẻ kết quả** (cúp/mặt buồn/dấu bằng) hiện sau 0,7 giây để kịp nhìn đường thắng.

Hiệu ứng: quân được **vẽ dần** (X hai nét, O một vòng cung, 260 ms), có **vầng sáng** (`BlurMaskFilter`); ba ô thắng **nhấp nháy** và phóng nhẹ.

## 6. Kiến trúc & cấu trúc thư mục

```
vn.edu.hust.tictactoe
├── TictactoeApp.kt          // Application: áp dụng chế độ sáng/tối đã lưu
├── domain/                  // Kotlin THUẦN — không import android.*; test bằng JUnit
│   ├── Player.kt            // enum Player { X, O } + other(); enum Opponent { HUMAN, EASY, HARD }
│   ├── Board.kt             // data class Board(cells: List<Player?>): place, emptyCells, winningLine, winner, isFull
│   ├── GameState.kt         // data class GameState(board, turn): winner, isDraw, isOver, play(index)
│   ├── Ai.kt                // object Ai: chooseMove(board, me, hard, random), minimax
│   └── Demo.kt              // fun main(): cho máy đấu máy, in bàn cờ ra console
├── data/
│   └── GameStats.kt         // SharedPreferences "stats": thắng/hòa/thua theo Opponent
└── ui/
    ├── ThemeMode.kt         // object: đổi và nhớ sáng/tối (SharedPreferences "settings")
    ├── splash/SplashActivity.kt
    ├── menu/MenuActivity.kt
    └── game/
        ├── GameActivity.kt  // dựng 9 CellView, observe LiveData, vẽ lại, thẻ kết quả
        ├── GameViewModel.kt // LiveData<GameState>, LiveData<Scores>; nước đi của máy; ghi thành tích
        └── CellView.kt      // View tự vẽ: quân X/O, hiệu ứng vẽ dần, nhấp nháy ô thắng
```

Nguyên tắc:
- **Toàn bộ luật chơi và AI nằm trong `domain/`.** Luồng xử lý: chạm ô → `viewModel.onCellClicked(i)` → `GameState.play(i)` → cập nhật LiveData → Activity `observe` và vẽ lại → (nếu tới lượt máy) coroutine chờ 450 ms → `Ai.chooseMove` → `applyMove`.
- **Dữ liệu bất biến:** mỗi nước đi tạo `Board`/`GameState` mới (`place`, `play`); không sửa bàn cũ.
- Nước đi không hợp lệ thì `play` trả về **chính đối tượng cũ**, ViewModel nhận ra bằng `after === before`.

## 7. Kiến thức Kotlin/Android xuất hiện trong code

Quy ước (giống BK2048): mỗi chỗ minh họa thêm comment tiếng Việt có thẻ `// [KT ChX.Y]` để `grep -r "\[KT" app/src` ra làm slide. Code đã gắn **63 dòng chú thích, 77 thẻ** (một dòng có thể ghi nhiều thẻ). Thẻ `[KT Android]` đánh dấu kiến thức Android ngoài giáo trình (SharedPreferences, custom View, animation).

| Kiến thức | Dùng ở đâu |
|---|---|
| val/var, null safety `Player?`, `?:`, `?.let` (Ch1) | `Board.cells: List<Player?>`, `winner()`, `intent.getStringExtra(...) ?: ...` |
| `if`/`when`, ranges, vòng lặp (Ch1.5) | `minimax` (`when`), `render` (`for (i in cells.indices)`), `repeat(9)` trong `buildBoard` |
| List, MutableList (Ch1.6) | `cells.toMutableList().also { ... }` trong `place`; `LINES` |
| Hàm, tham số mặc định, named arguments (Ch2.2) | `chooseMove(..., random: Random = Random)`, `GameState(turn = starter)`, `setMark(player, animate = ...)` |
| Hàm rút gọn (Ch2.3) | `fun isFull() = cells.none { it == null }`, `fun other() = if (...) O else X` |
| Lambda, hàm bậc cao (Ch2.4) | `setOnClickListener { ... }`, `observe(this) { render(it) }`, `firstOrNull { ... }` |
| `filter`/`map`/`maxOf`/`none`/`random` (Ch2.5) | `emptyCells()`, `chooseMove` |
| Destructuring (Ch2) | `LINES.firstOrNull { (a, b, c) -> ... }` |
| Lớp, constructor, property, getter tính toán (Ch3.1) | `GameState.winner get() = ...`, `GameStats(context)` |
| Kế thừa (Ch3.2) | `CellView : View`, `GameViewModel : AndroidViewModel`, `TictactoeApp : Application` |
| data class, enum, object, companion object (Ch3.4) | `Board`, `GameState`, `Scores`; `Player`, `Opponent`; `Ai`, `ThemeMode`; `Board.SIZE`, `GameActivity.intent(...)` |
| Đệ quy | `minimax` gọi lại chính nó |
| Tổ chức package (Ch3.5) | `domain / data / ui` |
| Activity, resources, sự kiện (Ch4) | 3 Activity, `strings.xml`, `drawable/ic_*.xml` |
| Layout, View Binding (Ch5) | `activity_menu.xml`, `activity_game.xml`, `ActivityGameBinding` |
| Intent + extra (Ch6) | `GameActivity.intent(context, opponent)` → `putExtra("opponent", ...)` |
| Vòng đời Activity (Ch7) | `MenuActivity.onResume` cập nhật thành tích; `onDestroy` gỡ `Runnable`/`Handler` |
| ViewModel, LiveData (Ch8) | `GameViewModel`; đổi sáng/tối (Activity bị tạo lại) mà không mất ván |
| Coroutine (Ch9) | `viewModelScope.launch { delay(450) ... }`, `job?.cancel()` |
| Custom View, Canvas, animation | `CellView.onDraw`, `ValueAnimator`, `ViewPropertyAnimator` (thẻ kết quả, splash) |

## 8. Test (`app/src/test/.../domain/`)

Có **19 test, pass hết** trong 2 file:

**`GameTest.kt` (5 test ban đầu)**

| Test | Kiểm tra |
|---|---|
| `xWinsOnRow` | X đủ một hàng thì thắng, `winningLine = [0,1,2]` |
| `drawWhenFullWithoutWinner` | Bàn đầy, không ai thắng → hòa |
| `cannotPlayOnTakenCellOrAfterGameOver` | Đánh ô có quân hoặc khi ván đã xong → `play` trả chính nó |
| `easyAiTakesWinningMove` | Máy dễ thấy nước thắng ngay thì đánh |
| `hardAiNeverLoses` | Người X thử **mọi chuỗi nước đi có thể**, máy khó (O) không thua ván nào |

**`BoardAndAiTest.kt` (14 test bổ sung)**

| Nhóm | Test |
|---|---|
| Xét thắng | `xWinsOnColumn`, `xWinsOnMainDiagonal`, `oWinsOnAntiDiagonal`, `allEightLinesAreDetected`, `emptyBoardHasNoWinnerAndIsNotOver` |
| Lượt đi, bất biến | `turnAlternatesBetweenXAndO`, `placeDoesNotChangeOriginalBoard`, `placeOnTakenCellThrows` |
| Máy chơi | `hardAiBlocksOpponent` (bàn `XX./.O./...` → ô 2), `hardAiPrefersWinningOverBlocking`, `hardAiAnswersCornersWithAnEdge`, `hardAiNeverLosesWhenComputerStarts`, `twoHardAisAlwaysDraw`, `easyAiPicksEmptyCellAndIsReproducibleWithSeed` |

**`domain/Demo.kt`**: hàm `main()` cho máy khó đấu máy khó (in từng nước, luôn hòa), rồi cho máy dễ đấu máy khó 100 ván. Với seed cố định kết quả là: máy dễ thắng 0, hòa 20, máy khó thắng 80. Chạy bằng mũi tên xanh cạnh `fun main()`.

## 9. Kế hoạch còn lại

**Quy tắc:** sửa xong phải `gradlew.bat assembleDebug` và `gradlew.bat testDebugUnitTest` thành công, commit với message rõ ràng, cập nhật file trong `docs/giai-thich/` nếu đổi logic.

| Việc | Ưu tiên | Ghi chú |
|---|---|---|
| ~~Gắn thẻ `// [KT ChX.Y]`~~ | Xong | 77 thẻ |
| ~~Thêm test, `Demo.kt`~~ | Xong | 19 test |
| Build lại trong Android Studio để xác nhận | **Cao** | `gradlew.bat assembleDebug testDebugUnitTest` |
| Máy khó đánh nước đầu nhanh hơn (mục 10) | Trung bình | |
| Push GitHub, các bạn clone về chạy được | Cao | Xem `docs/bao-cao.md` |
| Lưu ván đang chơi khi thoát app | Thấp | Hiện chỉ lưu thành tích |

## 10. Các điểm kỹ thuật cần biết (thầy hay hỏi)

- **GameActivity khóa dọc** (`screenOrientation="portrait"`), nên khi demo ViewModel **đổi sáng/tối** thay cho xoay màn hình: `setDefaultNightMode` làm Activity bị hủy và tạo lại, ván vẫn còn.
- **Không vẽ lại hiệu ứng khi Activity tạo lại:** `render` chỉ cho `animate = true` với ô vừa đổi so với `lastState`; lần đầu `lastState == null` nên vẽ ngay.
- **Máy không đi khi chưa tới lượt:** `onCellClicked` bỏ qua chạm nếu `isComputerTurn()`; `newRound`/`resetAll` gọi `job?.cancel()` để hủy nước máy đang chờ.
- **Máy khó, nước đầu:** khi máy đi trước trên bàn trống, `minimax` duyệt cả cây (~550 nghìn nút) và hiện chạy trên **luồng chính** (`viewModelScope` mặc định `Dispatchers.Main`). Trên máy yếu có thể khựng nhẹ. Cách sửa đơn giản: nếu bàn trống thì đánh ngay ô giữa hoặc góc; hoặc tính trong `withContext(Dispatchers.Default)`.
- **Thành tích chỉ ghi khi đấu máy** (`if (opponent == Opponent.HUMAN) return`). Bảng điểm trên màn chơi (`Scores`) chỉ là của phiên, mất khi thoát màn.

## 11. Phân công

- **Thành viên 1 – Logic & AI:** `domain/` + `GameTest`. Trình bày luật thắng/hòa (8 đường), bất biến, **minimax**, Kotlin Ch1–3.
- **Thành viên 2 – Màn chơi:** `GameActivity`, `GameViewModel`, `CellView`. Trình bày luồng chạm → ViewModel → domain → LiveData, coroutine nước máy, custom View và hiệu ứng.
- **Thành viên 3 – Menu, lưu trữ, giao diện:** `SplashActivity`, `MenuActivity`, `GameStats`, `ThemeMode`, layout XML. Trình bày Intent, SharedPreferences, vòng đời `onResume`, sáng/tối, hộp thoại xác nhận.

**Ai cũng phải giải thích được:** cách kiểm tra thắng bằng 8 đường, luồng một lượt chạm, và vì sao máy khó không bao giờ thua.

## 12. Lịch

| Ngày | Việc |
|---|---|
| 30/10 | Hoàn thiện Menu dạng thẻ, viết tài liệu `docs/` |
| 30/10 (tối) | Gắn thẻ `[KT]`, thêm 14 test và `Demo.kt` (xong) |
| 31/10 | Push GitHub; cả nhóm đọc `docs/on-tap/` |
| 01/11 | Làm slide theo `docs/slide-bo-cuc.md`, quay video demo |
| 02/11 | Tập thuyết trình 15 phút, bấm giờ, tập trả lời câu hỏi |
| 03/11 | Báo cáo |
