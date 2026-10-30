# Tổng quan Cờ Caro BK – Tài liệu ôn tập cho cả nhóm

> Đọc file này trước, rồi đọc file riêng của phần mình (`01`, `02`, `03`) và file ôn tập trong `docs/on-tap/`.

---

## 1. App làm gì (nói trong 30 giây)

**Cờ Caro BK** là game tic-tac-toe 3×3 trên Android. Hai bên X và O đánh xen kẽ, ai có **ba quân thẳng hàng** (hàng, cột hoặc chéo) trước thì thắng; bàn đầy mà chưa ai thắng thì hòa.

Có 3 chế độ:
- **Đấu máy dễ:** máy thắng ngay nếu được, còn lại đi ngẫu nhiên.
- **Đấu máy khó:** máy dùng thuật toán **minimax**, **không bao giờ thua**.
- **Hai người chơi** trên cùng một máy.

Điểm cộng: quân cờ được **vẽ dần** bằng View tự vẽ, ô thắng nhấp nháy, có thẻ kết quả, **thành tích đấu máy được lưu lại**, có **giao diện sáng/tối**.

> Slogan: *"Ba quân thẳng hàng là qua môn."*

---

## 2. Kiến trúc 3 tầng

| Tầng | Giống như | Làm gì | File |
|---|---|---|---|
| **domain** | Trọng tài + kỳ thủ | Luật chơi, xét thắng/hòa, máy chọn nước | `Player.kt`, `Board.kt`, `GameState.kt`, `Ai.kt` |
| **data** | Sổ ghi chép | Lưu thành tích thắng/hòa/thua | `GameStats.kt` |
| **ui** | Bàn cờ + người phục vụ | Nhận chạm, vẽ màn hình, chuyển màn | `SplashActivity`, `MenuActivity`, `GameActivity`, `GameViewModel`, `CellView`, `ThemeMode` |

**Quy tắc:** `domain` **không import `android.*`**. Nó chỉ nhận bàn cờ và trả ra bàn cờ mới, nên test được bằng JUnit mà không cần emulator.

```
ui  ──►  data
 │
 └──►  domain   (không phụ thuộc ai)
```

### Bản đồ file

| File | Dòng quan trọng |
|---|---|
| `domain/Player.kt` | `enum class Player { X, O }`, `fun other()`; `enum class Opponent { HUMAN, EASY, HARD }` |
| `domain/Board.kt` | `data class Board(val cells: List<Player?>)`, `place`, `emptyCells`, `winningLine`, `winner`, `isFull`, `LINES` |
| `domain/GameState.kt` | `data class GameState(board, turn)`, `isOver`, `isDraw`, `play(index)` |
| `domain/Ai.kt` | `object Ai`: `chooseMove`, `minimax` |
| `data/GameStats.kt` | SharedPreferences `"stats"`, khóa `EASY_win`, `HARD_loss`… |
| `ui/game/GameViewModel.kt` | `LiveData<GameState>`, `LiveData<Scores>`, `onCellClicked`, `newRound`, `resetAll`, nước máy |
| `ui/game/GameActivity.kt` | dựng 9 ô, `render(state)`, thẻ kết quả |
| `ui/game/CellView.kt` | `onDraw` vẽ X/O, `ValueAnimator` vẽ dần và nhấp nháy |
| `ui/menu/MenuActivity.kt` | 3 chế độ, thành tích, xóa thành tích |
| `ui/splash/SplashActivity.kt` | logo phóng to, 2 giây sau sang Menu |
| `ui/ThemeMode.kt` | đổi và nhớ sáng/tối |
| `TictactoeApp.kt` | áp dụng sáng/tối khi mở app |

---

## 3. Đầu vào – Xử lý – Đầu ra

| Đầu vào | Xử lý | Đầu ra |
|---|---|---|
| Chạm ô số `i` (0..8) | `GameState.play(i)`: đặt quân, đổi lượt | Bàn cờ mới, trạng thái (đang chơi / thắng / hòa) |
| Tới lượt máy | `Ai.chooseMove(board, O, hard)` | Ô máy chọn |
| Ván kết thúc | `recordResult` | Bảng điểm phiên + thành tích lưu lâu dài |

---

## 4. Một lượt chạm – AI CŨNG PHẢI NÓI ĐƯỢC

```
1. Người chơi chạm ô số 4
2. CellView.setOnClickListener  → viewModel.onCellClicked(4)
3. GameViewModel                 đang là lượt máy? → bỏ qua. Không thì applyMove(4)
4. GameState.play(4) [domain]    ô trống và ván chưa xong → GameState mới, đổi lượt
5. GameViewModel                 _state.value = after  (LiveData báo "có thay đổi")
6. GameActivity.observe          render(state): vẽ quân, tô ô thắng, đổi dòng trạng thái
7. Nếu tới lượt máy              coroutine: delay(450) → Ai.chooseMove → applyMove (lặp lại bước 4–6)
8. Nếu ván xong                  recordResult: cộng điểm phiên, ghi GameStats (khi đấu máy)
```

> **Câu nói mẫu:** "Chạm ô thì Activity gọi ViewModel. ViewModel gọi `play` trong `domain` để tạo trạng thái mới rồi gán vào LiveData. Activity đang `observe` nên tự vẽ lại. Nếu tới lượt máy, ViewModel chạy một coroutine chờ 450 ms rồi hỏi `Ai` chọn ô và đi tiếp đúng con đường đó."

---

## 5. Xét thắng: 8 đường (`Board.kt`)

```kotlin
private val LINES = listOf(
    listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // hàng
    listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // cột
    listOf(0, 4, 8), listOf(2, 4, 6),                  // chéo
)

fun winningLine(): List<Int>? =
    LINES.firstOrNull { (a, b, c) -> cells[a] != null && cells[a] == cells[b] && cells[b] == cells[c] }
```

- Bàn 3×3 chỉ có **đúng 8 đường thắng**, nên liệt kê sẵn là cách đơn giản và dễ kiểm tra nhất.
- `(a, b, c)` là **destructuring**: tách 3 phần tử của một đường.
- `cells[a] != null` để ba ô **trống** không bị tính là "thẳng hàng".
- `winner()` lấy quân ở ô đầu của đường thắng; `isFull()` kiểm tra hết ô trống; **hòa** = không ai thắng và bàn đầy.

---

## 6. Máy khó: minimax (`Ai.kt`) – điểm nhấn kỹ thuật

Ý tưởng: máy **thử hết mọi nước đi tới cuối ván**, giả định đối thủ cũng chơi giỏi nhất.

```kotlin
private fun minimax(board: Board, me: Player, turn: Player): Int {
    val winner = board.winner()
    return when {
        winner == me   ->  1 + board.emptyCells().size   // thắng: càng sớm càng tốt
        winner != null -> -1 - board.emptyCells().size   // thua: càng muộn càng đỡ
        board.isFull() ->  0                             // hòa
        else -> {
            val scores = board.emptyCells().map { minimax(board.place(it, turn), me, turn.other()) }
            if (turn == me) scores.max() else scores.min()  // máy chọn max, đối thủ chọn min
        }
    }
}
```

- **Đệ quy:** mỗi lần gọi là "giả sử đánh ô này, rồi tới lượt bên kia".
- **max/min:** tới lượt máy thì máy chọn nước điểm **cao nhất**; tới lượt người thì giả định người chọn nước làm máy **tệ nhất**.
- **Vì sao không bao giờ thua?** Tic-tac-toe là trò chơi đã "giải xong": nếu cả hai chơi hoàn hảo thì luôn hòa. Minimax duyệt **toàn bộ** cây nên luôn tìm được nước ít nhất là hòa.
- **Cộng số ô trống:** để máy ưu tiên **thắng sớm** thay vì "dạo chơi" rồi mới thắng.
- `chooseMove` gọi `minimax` cho từng ô trống; nhiều ô cùng điểm cao nhất thì chọn **ngẫu nhiên** cho đỡ nhàm.
- Bàn trống có **549.946** trạng thái trong cây. Số này đủ nhỏ để duyệt hết mà không cần cắt tỉa alpha-beta (cả bộ test, gồm nhiều ván máy khó, chạy khoảng 0,2 giây). Trên điện thoại yếu, nước đầu trên bàn trống có thể khựng nhẹ (xem `CLAUDE.md` mục 10).

**Máy dễ:** `empty.firstOrNull { board.place(it, me).winner() == me } ?: empty.random(random)`: có nước thắng ngay thì đánh, còn lại ngẫu nhiên (**không chặn**).

---

## 7. Tầng giao diện: ViewModel, LiveData, coroutine

- `GameViewModel` giữ `_state: MutableLiveData<GameState>` (private, sửa được) và `state: LiveData<GameState>` (public, chỉ đọc). Tương tự cho `Scores`.
- Activity bị **hủy và tạo lại** (ở app này là khi **đổi sáng/tối**, vì màn chơi đã khóa dọc) → ViewModel **vẫn còn**, ván không mất.
- `start(opponent)` có cờ `started` để không khởi tạo lại khi Activity tạo lại.
- Nước của máy chạy trong `viewModelScope.launch { delay(450); ... }`. Coroutine tự hủy khi ViewModel bị hủy; `job?.cancel()` khi bấm Ván mới/Reset để nước máy cũ không "rơi" vào ván mới.
- **Ván mới** đổi bên đi trước (`starter = starter.other()`); **Reset** đưa điểm phiên về 0 và cho X đi trước.

---

## 8. Lưu trữ: SharedPreferences

| File prefs | Khóa | Giá trị |
|---|---|---|
| `stats` | `EASY_win`, `EASY_draw`, `EASY_loss`, `HARD_win`, `HARD_draw`, `HARD_loss` | số ván (`Int`) |
| `settings` | `night_mode` | `MODE_NIGHT_YES` / `NO` / `FOLLOW_SYSTEM` |

- Tên khóa ghép bằng **string template**: `"${opponent.name}_win"`.
- `apply()` ghi bất đồng bộ, không chặn giao diện.
- **Xóa thành tích:** `prefs.edit().clear().apply()`, có hộp thoại xác nhận.
- Menu đọc lại thành tích trong `onResume`, vì sau khi chơi xong bấm Back thì Menu **không** chạy lại `onCreate`.

---

## 9. Bản đồ kiến thức giáo trình

Xem bảng đầy đủ ở mục 7 của `CLAUDE.md`. Những chỗ dễ chỉ trên lớp nhất:

| Kiến thức | Mở file | Chỉ vào |
|---|---|---|
| enum | `Player.kt` | `enum class Player { X, O }` |
| data class + bất biến | `Board.kt` | `place` trả `Board` mới |
| null safety | `Board.kt` | `List<Player?>`, `winningLine()?.let { ... }` |
| lambda, `filter`, `firstOrNull` | `Board.kt` | `emptyCells`, `winningLine` |
| object | `Ai.kt` | `object Ai` |
| tham số mặc định | `Ai.kt` | `random: Random = Random` |
| kế thừa | `CellView.kt` | `class CellView ... : View(...)` |
| Intent + extra | `GameActivity.kt` | `companion object { fun intent(...) }` |
| ViewModel/LiveData | `GameViewModel.kt` | `_state`, `state` |

---

## 10. Các tình huống đặc biệt (thầy hay hỏi)

| Tình huống | Code xử lý |
|---|---|
| Chạm ô đã có quân | `play` trả `this`, ViewModel thấy `after === before` thì không làm gì |
| Chạm khi đang là lượt máy | `onCellClicked` thoát sớm vì `isComputerTurn()` |
| Chạm sau khi ván xong | `play` trả `this` vì `isOver`; ô cũng bị `isEnabled = false` |
| Bấm Ván mới khi máy đang "nghĩ" | `job?.cancel()` |
| Đổi sáng/tối giữa ván | Activity tạo lại, ViewModel giữ ván; `lastState == null` nên không vẽ lại hiệu ứng |
| Thoát app giữa ván | Ván **không** được lưu (hạn chế); thành tích chỉ ghi khi ván xong |
| Hai người chơi | Không ghi thành tích lâu dài |

---

## 11. Tự kiểm tra (che cột phải, tự trả lời)

| Câu hỏi | Trả lời |
|---|---|
| Có mấy đường thắng? | 8: 3 hàng, 3 cột, 2 chéo |
| Hòa khi nào? | Bàn đầy và không ai thắng |
| Vì sao `Board` là `data class` bất biến? | Tự có `==`/`copy`; mỗi nước tạo bàn mới, dễ test, minimax thử nước mà không làm hỏng bàn thật |
| Máy dễ khác máy khó? | Dễ: thắng ngay nếu được, còn lại ngẫu nhiên. Khó: minimax duyệt hết, không thua |
| Vì sao máy khó không thua? | Duyệt toàn bộ cây, giả định đối thủ chơi tốt nhất; tic-tac-toe chơi hoàn hảo thì hòa |
| Một lượt chạm đi qua đâu? | CellView → ViewModel → `GameState.play` → LiveData → Activity vẽ lại |
| Vì sao máy chờ 450 ms? | Để người chơi kịp thấy nước của mình; dùng coroutine `delay` không chặn giao diện |
| Thành tích lưu ở đâu? | SharedPreferences `stats`, khóa `EASY_win`… |

---

## 12. Lộ trình ôn (khoảng 2–3 giờ mỗi người)

1. Đọc file này (30 phút).
2. Đọc `docs/on-tap/0-phan-chung.md` và làm phần tự luyện (30 phút).
3. Đọc file `docs/on-tap/` của mình và file `giai-thich` tương ứng, mở code đối chiếu (1 giờ).
4. Chạy app, chơi đủ 3 chế độ, đổi sáng/tối giữa ván, xóa thành tích (15 phút).
5. Tập trả lời câu hỏi của mình trong `docs/bao-cao.md` mục 3 (30 phút).
