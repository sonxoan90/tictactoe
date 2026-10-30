# Phần 1: Luật chơi và máy chơi (`domain/`)

## Làm gì
Toàn bộ luật tic-tac-toe và hai mức máy chơi, viết bằng **Kotlin thuần** (không `import android.*`), nên test được bằng JUnit.

## File
| File | Vai trò |
|---|---|
| `Player.kt` | `enum class Player { X, O }` với `other()`; `enum class Opponent { HUMAN, EASY, HARD }` |
| `Board.kt` | `data class Board(cells: List<Player?>)`: 9 ô, `null` là ô trống. `place`, `emptyCells`, `winningLine`, `winner`, `isFull`; `companion object` chứa `SIZE = 3` và 8 đường `LINES` |
| `GameState.kt` | `data class GameState(board, turn)`: `winner`, `winningLine`, `isDraw`, `isOver` (getter tính toán), `play(index)` |
| `Ai.kt` | `object Ai`: `chooseMove(board, me, hard, random)` và `minimax` |

## Hàm quan trọng
**`Board.place(index, player)`**: kiểm tra ô trống bằng `require`, copy danh sách bằng `toMutableList()`, đặt quân, trả về **Board mới**. Bàn cũ giữ nguyên, nên minimax "thử" được hàng trăm nghìn nước mà không làm hỏng bàn thật.

**`Board.winningLine()`**: duyệt 8 đường, `firstOrNull { (a, b, c) -> ... }` trả đường đầu tiên có 3 ô cùng quân (khác `null`), hoặc `null`.

**`GameState.play(index)`**: nếu ván xong hoặc ô đã có quân thì trả **chính nó** (`this`); ngược lại trả `GameState(board.place(index, turn), turn.other())`.

**`Ai.chooseMove`**:
- Dễ: `empty.firstOrNull { board.place(it, me).winner() == me } ?: empty.random(random)`.
- Khó: tính `minimax` cho từng ô trống, lấy điểm lớn nhất, chọn ngẫu nhiên trong các ô đạt điểm đó.

**`Ai.minimax(board, me, turn)`**: thắng `1 + ô trống`, thua `-1 - ô trống`, hòa `0`; còn lại gọi đệ quy cho mọi ô trống, lượt `me` lấy `max`, lượt đối thủ lấy `min`.

### Chạy tay: vì sao máy khó biết chặn
Máy là O, tới lượt O:
```
X | X | .        ô 2 trống
. | O | .
. | . | .
```
- Nếu O đánh **bất kỳ ô nào khác ô 2**, lượt sau X đánh ô 2 và thắng → nhánh đó có điểm **âm**.
- Nếu O đánh **ô 2**, chính O lại dọa đường chéo 2-4-6; X phải chặn ô 6, rồi hai bên chặn nhau tới khi bàn đầy → hòa, điểm **0**.
- Điểm minimax tính thật: **ô 2 → 0**, các ô 3, 5, 6, 7, 8 → **-5** (thua ngay lượt sau, còn 4 ô trống: `-1 - 4`).
- `max` chọn ô 2. Máy "biết chặn" mà không cần viết luật chặn riêng.

## Test (19 test) và Demo
- `GameTest.kt` (5): `xWinsOnRow`, `drawWhenFullWithoutWinner`, `cannotPlayOnTakenCellOrAfterGameOver`, `easyAiTakesWinningMove`, `hardAiNeverLoses` (người X thử **mọi** chuỗi nước, máy không thua).
- `BoardAndAiTest.kt` (14): thắng theo cột/chéo, đủ 8 đường, bất biến của `place`, máy khó chặn, ưu tiên thắng hơn chặn, đáp góc bằng cạnh, máy đi trước vẫn không thua, hai máy khó luôn hòa, máy dễ lặp lại được với cùng seed.
- `Demo.kt`: `main()` in từng nước của máy khó đấu máy khó, rồi thống kê 100 ván máy dễ đấu máy khó.
 Chạy: `gradlew.bat testDebugUnitTest` hoặc chuột phải thư mục `test` → Run.

## 5 câu thầy có thể hỏi
1. **Kiểm tra thắng thế nào?** Liệt kê sẵn 8 đường (3 hàng, 3 cột, 2 chéo), xem có đường nào 3 ô cùng quân và không trống không.
2. **Vì sao `cells` là `List<Player?>`?** Mỗi ô có 3 khả năng: X, O hoặc trống; trống biểu diễn bằng `null`, Kotlin bắt kiểm tra null nên không quên trường hợp ô trống.
3. **Vì sao `Board` bất biến?** Mỗi nước tạo bàn mới, nên minimax thử nước thoải mái; `data class` cho so sánh `==` và test dễ.
4. **Minimax là gì? Vì sao máy khó không thua?** Thử mọi nước tới cuối ván, máy chọn max, giả định đối thủ chọn min. Tic-tac-toe chơi hoàn hảo thì hòa, nên máy luôn tìm được nước ít nhất là hòa.
5. **Vì sao cộng số ô trống vào điểm?** Để thắng sớm có điểm cao hơn thắng muộn; máy không "dây dưa" khi đã có nước thắng.
