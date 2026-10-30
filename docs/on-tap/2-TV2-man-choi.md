# THÀNH VIÊN 2 – Màn chơi: ViewModel, LiveData, coroutine, View tự vẽ

> Học xong `0-phan-chung.md` trước. Thời gian: khoảng 3 giờ.

## Bạn phụ trách

| Slide | Nội dung |
|---|---|
| 3 | Demo (người thao tác chính) |
| 7 | Luồng một lượt chạm |
| 8 (một nửa) | ViewModel giữ ván khi Activity tạo lại |

**File code:** `ui/game/GameActivity.kt`, `GameViewModel.kt`, `CellView.kt`, `res/layout/activity_game.xml`. Đọc kèm `docs/giai-thich/02-man-choi.md`.

---

## 1. Vì sao cần ViewModel?

Khi **đổi cấu hình** (xoay màn hình, đổi sáng/tối, đổi ngôn ngữ), Android **hủy Activity và tạo lại**. Mọi biến trong Activity mất hết.

`GameViewModel` sống **lâu hơn** Activity: Activity mới lấy lại đúng ViewModel cũ bằng `by viewModels()`.

**Ở app này màn chơi khóa dọc**, nên demo bằng **nút sáng/tối**: bấm giữa ván, Activity tạo lại, bàn cờ vẫn còn.

Giống **bảng điểm của trọng tài**: khán đài (Activity) có thể dựng lại, bảng điểm (ViewModel) vẫn giữ nguyên.

## 2. LiveData

```kotlin
private val _state = MutableLiveData(GameState())   // chỉ ViewModel sửa
val state: LiveData<GameState> = _state            // Activity chỉ đọc

viewModel.state.observe(this) { render(it) }       // trong Activity
```
- `observe(this)`: LiveData biết vòng đời Activity. Chỉ gửi khi Activity đang hiển thị, tự hủy đăng ký khi Activity bị hủy, không rò bộ nhớ.
- Activity **không tự tính** gì về luật chơi; chỉ nhận `GameState` rồi vẽ.

## 3. Máy đi bằng coroutine

```kotlin
job = viewModelScope.launch {
    delay(450)                                       // chờ cho người chơi kịp nhìn
    val move = Ai.chooseMove(_state.value!!.board, Player.O, hard = opponent == Opponent.HARD)
    applyMove(move)
}
```
- `delay` **không chặn** giao diện (khác `Thread.sleep`).
- `viewModelScope` tự hủy coroutine khi ViewModel bị hủy (thoát màn chơi).
- `job?.cancel()` khi bấm **Ván mới**/**Reset**: nước máy đang chờ của ván cũ không rơi vào ván mới.
- `onCellClicked` thoát sớm nếu `isComputerTurn()`: người không chạm được lúc máy đang nghĩ.

## 4. Ván mới, Reset, bảng điểm

| Nút | Làm gì |
|---|---|
| **Ván mới** | Đổi bên đi trước (`starter = starter.other()`); nếu O đi trước khi đấu máy thì máy đánh luôn |
| **Reset** | Điểm phiên về 0, X đi trước; **thành tích lưu lâu dài vẫn giữ** |
| **Về menu** | `finish()` |

`Scores` là `data class`, cập nhật bằng `copy`: `old.copy(x = old.x + 1)` (**named argument**).

## 5. `render(state)`: vẽ lại màn hình

```kotlin
for (i in cells.indices) {
    cell.setMark(player, animate = previous != null && previous.board.cells[i] == null)
    cell.setHighlighted(i in line)
    cell.isEnabled = !state.isOver && player == null
}
```
- Chỉ ô **vừa có quân** mới vẽ dần. Lần đầu sau khi Activity tạo lại, `lastState == null` nên vẽ ngay, không chạy lại hiệu ứng cả bàn.
- Ô thắng nhấp nháy; ô đã có quân hoặc ván đã xong thì bị khóa.
- Cột X hoặc O đang đến lượt được tô nền; dòng trạng thái có icon quân.
- Ván xong: hẹn **700 ms** (`postDelayed`) rồi thẻ kết quả trượt lên, để người chơi kịp thấy đường thắng.

## 6. `CellView`: View tự vẽ (kế thừa, Ch3.2)

```kotlin
class CellView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs)
```
- Ghi đè `onDraw(canvas)`: X là 2 nét `drawLine`, O là vòng cung `drawArc`.
- `ValueAnimator` tăng `progress` 0 → 1 trong 260 ms, mỗi khung gọi `invalidate()` → Android gọi lại `onDraw`.
- Vầng sáng: vẽ thêm một lớp mờ bằng `BlurMaskFilter`.
- `onDetachedFromWindow` hủy animator.
- `contentDescription` là "X", "O" hoặc "Ô trống" để trình đọc màn hình đọc được.

## 7. Kịch bản demo bạn thao tác (khoảng 3 phút)
Xem `docs/bao-cao.md` mục 2. Phần của bạn: chơi máy dễ (thắng), máy khó (hòa, cho thấy máy chặn), đổi sáng/tối giữa ván.

## 8. Câu hỏi của bạn

1. Vì sao dùng ViewModel? Đổi sáng/tối thì chuyện gì xảy ra?
2. LiveData khác biến thường thế nào? `observe(this)` để làm gì?
3. Vì sao có `_state` và `state`?
4. Vì sao dùng coroutine `delay` mà không dùng `Thread.sleep`?
5. Bấm Ván mới khi máy đang nghĩ thì sao?
6. Làm sao người chơi không chạm được lúc máy đang nghĩ?
7. `CellView` vẽ quân thế nào? `invalidate()` để làm gì?
8. Vì sao `GameViewModel` kế thừa `AndroidViewModel`? *(Cần `Application` làm `Context` để mở `GameStats`.)*
9. **Câu khó:** "Sao không thấy hiệu ứng vẽ lại cả bàn khi đổi sáng/tối?" *(So với `lastState`; lần đầu `lastState == null` nên `animate = false`.)*
10. **Câu khó:** "Demo xoay màn hình đi." *(Màn chơi khóa dọc trong Manifest; nhóm demo bằng đổi sáng/tối, cùng cơ chế Activity tạo lại.)*
