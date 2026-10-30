# THÀNH VIÊN 1 – "Bộ não": Luật chơi, máy chơi, kiểm thử

> Học xong `0-phan-chung.md` trước. Thời gian: khoảng 3 giờ.
> Vai trò thêm: **người đỡ lời** khi hai bạn gặp câu khó về thuật toán.

## Bạn phụ trách

| Slide | Nội dung |
|---|---|
| 4 | Kiến trúc 3 tầng |
| 5 | Mô hình dữ liệu và xét thắng (8 đường) |
| 6 | Minimax (**slide quan trọng nhất**) |
| 9 | Kiến thức giáo trình và kiểm thử |

**File code:** `domain/Player.kt`, `Board.kt`, `GameState.kt`, `Ai.kt`, `Demo.kt`, `app/src/test/.../GameTest.kt`, `BoardAndAiTest.kt`. Đọc kèm `docs/giai-thich/01-domain-ai.md`.

---

## 1. Mô hình dữ liệu

### Bốn loại lớp (Chương 3.4), thầy rất hay hỏi

| Loại | Trong app | Vì sao chọn |
|---|---|---|
| `enum class` | `Player { X, O }`, `Opponent { HUMAN, EASY, HARD }` | Số giá trị cố định |
| `data class` | `Board`, `GameState` (và `Scores` ở ViewModel) | Chỉ chứa dữ liệu; tự có `==`, `copy`, `toString` |
| `object` | `Ai` | Chỉ cần **một** máy chơi cho cả app, không có trạng thái riêng |
| `companion object` | `Board.SIZE`, `Board.LINES` | Hằng số dùng chung, gọi bằng `Board.SIZE` |

### Enum có hàm
```kotlin
enum class Player {
    X, O;
    fun other(): Player = if (this == X) O else X   // hàm rút gọn
}
```

### Null safety
`List<Player?>`: dấu `?` cho phép `null` = ô trống. `winner()` viết `winningLine()?.let { cells[it[0]] }`: nếu không có đường thắng thì trả `null` luôn, không lỗi.

### Bất biến
`place` **không sửa** bàn cũ mà tạo bàn mới:
```kotlin
fun place(index: Int, player: Player): Board {
    require(cells[index] == null) { "Ô $index đã có quân" }
    return Board(cells.toMutableList().also { it[index] = player })
}
```
Vì sao quan trọng? Minimax thử hàng trăm nghìn nước "trong đầu"; nếu sửa thẳng bàn cờ thì bàn thật bị hỏng.

### Getter tính toán (Ch3.1)
```kotlin
val isOver: Boolean get() = winner != null || board.isFull()
```
Không lưu sẵn mà **tính mỗi lần đọc**, nên luôn đúng với bàn hiện tại.

---

## 2. Xét thắng

```kotlin
fun winningLine(): List<Int>? =
    LINES.firstOrNull { (a, b, c) -> cells[a] != null && cells[a] == cells[b] && cells[b] == cells[c] }
```
- `firstOrNull`: hàm bậc cao, trả phần tử **đầu tiên** thỏa điều kiện hoặc `null`.
- `(a, b, c)`: destructuring một `List` 3 phần tử.
- Phải có `cells[a] != null`, nếu không ba ô trống cũng "bằng nhau".
- Hòa: `winner == null && board.isFull()`.

---

## 3. Minimax (`Ai.kt`) – phải chạy tay được trên bảng

```kotlin
private fun minimax(board: Board, me: Player, turn: Player): Int {
    val winner = board.winner()
    return when {
        winner == me   ->  1 + board.emptyCells().size
        winner != null -> -1 - board.emptyCells().size
        board.isFull() ->  0
        else -> {
            val scores = board.emptyCells().map { minimax(board.place(it, turn), me, turn.other()) }
            if (turn == me) scores.max() else scores.min()
        }
    }
}
```

Ba ý phải nói được:
1. **Điểm dừng** của đệ quy: có người thắng hoặc bàn đầy.
2. **Max/min:** lượt mình chọn max, lượt đối thủ chọn min (giả định đối thủ giỏi nhất).
3. **Cộng số ô trống:** thắng sớm điểm cao hơn; thua muộn đỡ tệ hơn.

### Bảng chạy tay (máy là O, tới lượt O)
```
X | X | .
. | O | .
. | . | .
```
| O đánh ô | Chuyện gì xảy ra | Điểm minimax |
|---|---|---|
| 2 | Chặn X, lại dọa chéo 2-4-6; hai bên chặn nhau → hòa | **0** |
| 3, 5, 6, 7, 8 | X đánh ô 2 thắng ngay, còn 4 ô trống | **-5** (`-1 - 4`) |

→ `chooseMove` chọn ô 2. Không cần viết luật "chặn" riêng; minimax tự suy ra.

### `chooseMove`
```kotlin
if (!hard) {
    val winning = empty.firstOrNull { board.place(it, me).winner() == me }
    return winning ?: empty.random(random)          // toán tử Elvis
}
val scored = empty.map { it to minimax(board.place(it, me), me, me.other()) }  // Pair
val best = scored.maxOf { it.second }
return scored.filter { it.second == best }.random(random).first
```
- `random: Random = Random`: **tham số mặc định**; test có thể truyền `Random(seed)` để kết quả lặp lại được.
- Nhiều nước cùng điểm cao nhất thì chọn ngẫu nhiên, nên máy khó không đi giống hệt nhau mọi ván.

### Vì sao không cần alpha-beta?
Cây từ bàn trống có **549.946 nút**, nhỏ, duyệt hết được. Alpha-beta chỉ cần với cờ lớn hơn (ví dụ caro 15×15, cờ vua).

---

## 4. Kiểm thử

| Test | Kiểm tra |
|---|---|
| `xWinsOnRow` | X đủ một hàng thì `winner == X` |
| `drawWhenFullWithoutWinner` | Bàn đầy không ai thắng → `isDraw` |
| `cannotPlayOnTakenCellOrAfterGameOver` | Đánh ô có quân hoặc sau khi xong → trạng thái không đổi |
| `easyAiTakesWinningMove` | Máy dễ có nước thắng thì đánh |
| `hardAiNeverLoses` | Người X thử **mọi chuỗi nước đi có thể** (duyệt hết), máy khó không thua ván nào |

**File `BoardAndAiTest.kt` (14 test bổ sung):** thắng theo cột, hai đường chéo, đủ 8 đường; lượt đi xen kẽ; `place` không sửa bàn cũ và báo lỗi khi ô đã có quân; máy khó chặn (`XX./.O./...` → ô 2), ưu tiên thắng hơn chặn, đáp góc bằng cạnh, máy đi trước vẫn không thua, hai máy khó luôn hòa; máy dễ cùng seed thì cùng nước.

**`Demo.kt`:** bấm mũi tên xanh cạnh `fun main()` để xem máy khó đấu máy khó (luôn hòa) và kết quả 100 ván máy dễ đấu máy khó (seed cố định: 0 thắng · 20 hòa · 80 thua). Dùng khi thầy hỏi "chứng minh máy khó không thua".

Chạy: chuột phải `app/src/test` → Run Tests, hoặc `gradlew.bat testDebugUnitTest`.

---

## 5. Nếu thầy yêu cầu sửa ngay trên lớp

| Yêu cầu | Sửa ở đâu |
|---|---|
| "Cho máy dễ biết chặn" | `chooseMove`: sau `winning`, thêm `empty.firstOrNull { board.place(it, me.other()).winner() == me.other() }` |
| "Máy khó luôn đánh ô giữa nếu trống" | đầu `chooseMove`: `if (hard && 4 in empty) return 4` |
| "Đổi bàn 4×4" | Phải sửa `SIZE` **và** danh sách `LINES` (và minimax sẽ chậm hơn rất nhiều) |

---

## 6. Câu hỏi của bạn (phải trả lời trôi chảy)

1. Có mấy đường thắng, kiểm tra thế nào?
2. Vì sao ô trống là `null`? Null safety giúp gì?
3. Vì sao `Board` bất biến? Liên quan gì minimax?
4. Minimax là gì? Điểm dừng là gì? Vì sao máy khó không thua?
5. Vì sao cộng số ô trống vào điểm?
6. `object Ai` khác `class Ai` thế nào?
7. Vì sao `chooseMove` có tham số `random`?
8. `enum` và `data class` khác nhau ở đâu?
9. **Câu khó:** "Máy khó đi trước trên bàn trống thì có chậm không?" *(Phải duyệt cả cây khoảng 550 nghìn nút, hiện chạy trên luồng chính nên máy yếu có thể khựng nhẹ. Cách sửa: bàn trống thì đánh ngay ô giữa/góc, hoặc tính ở `Dispatchers.Default`.)*

## 7. Bài tập tự làm
- Chạy tay minimax cho bàn `X . . / . O . / . . X`, O đi. *(Gợi ý: O không được đánh góc, phải đánh cạnh.)*
- Đọc test `hardAiAnswersCornersWithAnEdge` và giải thích vì sao O đánh góc thì thua.
- Tìm tất cả thẻ của mình: `grep -rn "\[KT" app/src/main/java/vn/edu/hust/tictactoe/domain`.
