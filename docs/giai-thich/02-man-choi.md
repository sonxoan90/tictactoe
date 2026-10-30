# Phần 2: Màn chơi (`GameActivity`, `GameViewModel`, `CellView`)

## Làm gì
Hiển thị bàn cờ 3×3, nhận chạm, cho máy đánh, hiện bảng điểm phiên và thẻ kết quả. Ván không mất khi Activity bị tạo lại.

## File
| File | Vai trò |
|---|---|
| `GameViewModel.kt` | Giữ `LiveData<GameState>` và `LiveData<Scores>`; `onCellClicked`, `newRound`, `resetAll`; cho máy đi bằng coroutine; ghi thành tích |
| `GameActivity.kt` | Dựng 9 `CellView` trong `GridLayout`, `observe` LiveData, `render`, thẻ kết quả |
| `CellView.kt` | View tự vẽ quân X/O bằng `Canvas`, hiệu ứng vẽ dần và nhấp nháy ô thắng |
| `activity_game.xml` | Tiêu đề, bảng điểm, dòng trạng thái, lưới, nút, lớp phủ kết quả |

## Luồng chính
1. Menu mở màn chơi bằng `GameActivity.intent(this, Opponent.HARD)` (extra `"opponent"`).
2. `onCreate`: đọc extra → `viewModel.start(opponent)` (chỉ chạy lần đầu nhờ cờ `started`) → `buildBoard()` tạo 9 ô bằng `repeat(9)` → `observe`.
3. Chạm ô → `viewModel.onCellClicked(i)` → `applyMove(i)` → `_state.value = after`.
4. `observe` gọi `render(state)`: với mỗi ô gọi `setMark`, `setHighlighted` (ô thắng), đổi nền, khóa ô đã có quân; tô nền cột của bên đang đến lượt; cập nhật dòng trạng thái.
5. Tới lượt máy: `viewModelScope.launch { delay(450); Ai.chooseMove(...); applyMove(move) }`.
6. Ván xong: `recordResult` cộng `Scores` (dùng `copy`) và ghi `GameStats` khi đấu máy. Activity hẹn 700 ms rồi hiện thẻ kết quả trượt lên (`ViewPropertyAnimator`).

## `CellView`: View tự vẽ
- Kế thừa `View`, ghi đè `onDraw(canvas)` và `onSizeChanged` (độ dày nét theo kích thước ô).
- X: hai đường `drawLine`, nét thứ nhất vẽ trong nửa đầu `progress`, nét thứ hai trong nửa sau. O: `drawArc` từ 0 tới `360° × progress`.
- `ValueAnimator` chạy `progress` từ 0 → 1 trong 260 ms, mỗi khung gọi `invalidate()` để vẽ lại.
- Vầng sáng: vẽ thêm một lần với `BlurMaskFilter` (cần `LAYER_TYPE_SOFTWARE`).
- Ô thắng: `ValueAnimator` lặp vô hạn (`REVERSE`) làm sáng và phóng 8%.
- `onDetachedFromWindow` hủy animator để không rò bộ nhớ.

## Chi tiết đáng nói
- Chỉ ô **vừa đổi** so với `lastState` mới được vẽ dần, nên khi Activity tạo lại (đổi sáng/tối) bàn cờ hiện ngay, không "vẽ lại từ đầu".
- `isComputerTurn()` chặn người chơi chạm khi máy đang nghĩ.
- `job?.cancel()` trong `newRound`/`resetAll` hủy nước máy đang chờ.
- `onDestroy` gỡ `showResultRunnable` để không hiện thẻ kết quả lên Activity đã hủy.

## 5 câu thầy có thể hỏi
1. **Vì sao dùng ViewModel?** Activity bị hủy và tạo lại khi đổi cấu hình (ở đây là đổi sáng/tối); ViewModel sống tiếp nên ván không mất.
2. **Vì sao có `_state` (Mutable, private) và `state` (LiveData, public)?** Chỉ ViewModel được sửa; Activity chỉ được đọc và quan sát.
3. **`observe(this)` truyền `this` để làm gì?** LiveData biết vòng đời Activity: chỉ gửi khi Activity đang hiển thị, tự hủy đăng ký khi Activity bị hủy.
4. **Vì sao dùng coroutine `delay` mà không dùng `Thread.sleep`?** `sleep` chặn luồng giao diện làm app đứng; `delay` chỉ tạm dừng coroutine. `viewModelScope` tự hủy coroutine khi ViewModel bị hủy.
5. **CellView vẽ quân thế nào?** Kế thừa `View`, vẽ trong `onDraw` bằng `Canvas.drawLine`/`drawArc`; `ValueAnimator` tăng dần `progress` và gọi `invalidate()` để vẽ lại từng khung.
