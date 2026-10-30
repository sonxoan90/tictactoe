# Bố cục slide báo cáo – Cờ Caro BK (15 phút)

**Tổng: 11 slide chính + 3 slide dự phòng.** Khoảng 12 phút trình bày và 3 phút hỏi đáp. Cấu trúc giống deck của 2048 BK: Bài toán → Demo → Kiến trúc → Thuật toán → Android → Kiến thức/Kiểm thử → Kết luận.

Nguyên tắc: mỗi slide **một ý chính**; chữ to, ít dòng; code chỉ trích **dưới 12 dòng**. Màu chủ đạo lấy theo app: tím (X) và hồng tím (O) của Material 3. Ảnh chụp màn hình dùng ảnh thật của app.

---

## Phân bổ thời gian

| # | Slide | Người nói | Thời gian | Cộng dồn |
|---|---|---|---|---|
| 1 | Tiêu đề | TV3 | 0:20 | 0:20 |
| 2 | Bài toán và 3 chế độ | TV3 | 0:50 | 1:10 |
| 3 | Demo | TV2 (TV1, TV3 chen lời) | 3:30 | 4:40 |
| 4 | Kiến trúc 3 tầng | TV1 | 0:50 | 5:30 |
| 5 | Mô hình dữ liệu và xét thắng | TV1 | 1:00 | 6:30 |
| 6 | **Minimax** | TV1 | 1:40 | 8:10 |
| 7 | Luồng một lượt chạm | TV2 | 1:10 | 9:20 |
| 8 | Vòng đời, ViewModel, lưu trữ | TV2 + TV3 | 1:00 | 10:20 |
| 9 | Kiến thức giáo trình và kiểm thử | TV1 | 0:50 | 11:10 |
| 10 | Kết luận: dùng AI, hạn chế, hướng phát triển | TV3 | 0:50 | 12:00 |
| 11 | Hỏi đáp | Cả nhóm | 3:00 | 15:00 |

Mỗi người nói khoảng 4 phút. Bị lố thời gian thì rút ngắn demo (bỏ bước hai người chơi) trước, **không cắt slide 6**.

---

## Slide 1 – Tiêu đề (0:20)
- Nhãn nhỏ: `ĐỒ ÁN MÔN IT4785 – LẬP TRÌNH DI ĐỘNG`
- Tiêu đề lớn: **Cờ Caro BK**
- Slogan: *"Ba quân thẳng hàng là qua môn."*
- Dòng cuối: Nhóm: [TV1] · [TV2] · [TV3] | Giảng viên: [Tên] · 03/11/2026
- Hình: logo X/O của app (`ic_logo`) hoặc ảnh màn Splash.

## Slide 2 – Bài toán (0:50)
- Tiêu đề: **Tic-tac-toe 3×3, ba chế độ chơi**
- Trái: bàn cờ 3×3 đánh số ô 0..8, tô một đường thắng.
- Phải: 3 thẻ giống Menu của app:
  - 🙂 **Máy dễ:** thắng ngay nếu được, còn lại ngẫu nhiên
  - 🧠 **Máy khó:** minimax, không bao giờ thua
  - 👥 **Hai người:** so tài trên cùng máy
- Dòng dưới: Thắng = 3 quân thẳng hàng (8 đường) · Hòa = bàn đầy · Thành tích đấu máy được lưu.

## Slide 3 – Demo (3:30)
- Tiêu đề: **Demo: từ Splash tới ván hòa với máy khó**
- Khung lớn: video quay sẵn khoảng 3 phút (hoặc chiếu emulator trực tiếp).
- Cột phải: danh sách bước theo `docs/bao-cao.md` mục 2 (Splash → máy dễ thắng → Ván mới đổi bên → thành tích tăng → máy khó chặn → đổi sáng/tối giữa ván → hòa → Reset → Xóa thành tích).
- Sau video: thao tác trực tiếp 30 giây, **đổi sáng/tối giữa ván** để thầy thấy tận mắt.

## Slide 4 – Kiến trúc (0:50)
- Tiêu đề: **Ba tầng, phụ thuộc một chiều**
- Sơ đồ 3 khối:
  - `ui/`: Splash, Menu, GameActivity, GameViewModel, CellView, ThemeMode
  - `data/`: GameStats (SharedPreferences)
  - `domain/`: Player, Board, GameState, Ai (**Kotlin thuần, không `android.*`**)
- Mũi tên: `ui → data`, `ui → domain`. `domain` không trỏ đi đâu.
- Câu chốt: *"Luật chơi và AI test được bằng JUnit, không cần emulator."*

## Slide 5 – Mô hình dữ liệu và xét thắng (1:00)
- Tiêu đề: **Bàn cờ bất biến, 8 đường thắng**
- Trái: bảng 4 loại lớp: `enum Player { X, O }` · `data class Board(cells: List<Player?>)` · `data class GameState(board, turn)` · `object Ai`.
- Phải: code `LINES` và `winningLine()` (khoảng 8 dòng); hình 8 đường vẽ trên bàn 3×3.
- Chú thích: `null` = ô trống · `place()` trả **Board mới** · `play()` trả chính nó nếu nước không hợp lệ.

## Slide 6 – Minimax (1:40) ⭐ slide quan trọng nhất
- Tiêu đề: **Máy khó: thử hết mọi nước, không bao giờ thua**
- Trái: code `minimax` (khoảng 11 dòng), đánh số ① điểm dừng ② đệ quy ③ max/min.
- Phải: chạy tay bàn `X X . / . O . / . . .` (O đi):

  | O đánh ô | Kết quả | Điểm |
  |---|---|---|
  | **2** | chặn, rồi hòa | **0** |
  | 3, 5, 6, 7, 8 | X thắng ngay | -5 |

- Dòng dưới: Thắng `1 + ô trống` (thắng sớm điểm cao) · Thua `-1 - ô trống` · Hòa `0` · Cây từ bàn trống: **549.946 nút**, duyệt hết, không cần alpha-beta.
- Câu chốt: *"Máy không có luật 'chặn' nào; nó tự suy ra vì mọi nước khác đều dẫn tới thua."*

## Slide 7 – Luồng một lượt chạm (1:10)
- Tiêu đề: **Từ ngón tay tới bàn cờ mới**
- Sơ đồ 6 bước nằm ngang (giống slide "luồng một lượt vuốt" của 2048 BK):
  1. `CellView` nhận chạm
  2. `GameViewModel.onCellClicked(i)` (bỏ qua nếu là lượt máy)
  3. `GameState.play(i)` trong `domain`
  4. `_state.value = after` (LiveData)
  5. `GameActivity.observe → render()`
  6. Lượt máy: `viewModelScope.launch { delay(450); Ai.chooseMove(...) }`
- Góc phải: hình nhỏ quân X đang được vẽ dần (`ValueAnimator` + `onDraw`).

## Slide 8 – Vòng đời và lưu trữ (1:00)
- Tiêu đề: **Không mất ván, không mất thành tích**
- Trái (TV2): **ViewModel** sống qua lúc Activity tạo lại (đổi sáng/tối); `job?.cancel()` khi Ván mới; `lastState` để không vẽ lại hiệu ứng.
- Phải (TV3): **SharedPreferences**
  - `stats`: `EASY_win`, `HARD_draw`… (string template `"${opponent.name}_win"`)
  - `settings`: `night_mode`
  - Menu đọc lại trong `onResume`; Xóa thành tích có hộp thoại xác nhận.
- Dải dưới: Splash → Menu → Game (Intent extra `"opponent"`).

## Slide 9 – Kiến thức và kiểm thử (0:50)
- Tiêu đề: **Kiến thức giáo trình đã áp dụng**
- Trái: bảng ngắn Ch1 → Ch9 (null safety, `when`, lambda/`filter`/`firstOrNull`, tham số mặc định, data class/enum/object, kế thừa `View`, Intent, vòng đời, ViewModel/LiveData, coroutine). Ghi số lớn: **77 thẻ** `// [KT ChX.Y]` trong code.
- Phải: số lớn **19** unit test JUnit (2 file), nhóm theo: xét thắng · bất biến · máy chơi; nhấn mạnh `hardAiNeverLoses` (thử mọi chuỗi nước của người) và `Demo.kt` (100 ván máy dễ đấu máy khó: máy khó không thua ván nào).

## Slide 10 – Kết luận (0:50)
- Tiêu đề: **Dùng AI, hạn chế và hướng phát triển**
- Ba cột:
  - **Cách dùng AI:** nhóm phân tích, mô tả yêu cầu · Claude Code viết code · nhóm chạy thử, duyệt, chia nhau hiểu từng phần.
  - **Hạn chế:** thoát giữa ván thì mất ván · chỉ 3×3 · người luôn là X · máy khó tính trên luồng chính.
  - **Hướng phát triển:** caro 15×15 + alpha-beta · mức trung bình · lưu ván · âm thanh.

## Slide 11 – Hỏi đáp
- **Cảm ơn Thầy và các bạn đã lắng nghe**
- Slogan, link GitHub, mã QR (nếu có).

---

## Slide dự phòng (chỉ mở khi bị hỏi)

| # | Tiêu đề | Nội dung |
|---|---|---|
| 12 | `chooseMove`: máy dễ và máy khó | Code `chooseMove` đầy đủ; toán tử `?:`, `Pair` (`it to score`), `maxOf`, chọn ngẫu nhiên trong các nước tốt nhất |
| 13 | `CellView`: View tự vẽ | `onDraw` với `drawLine`/`drawArc`, `ValueAnimator`, `BlurMaskFilter`, `invalidate()` |
| 14 | Các tình huống đặc biệt | Bảng: chạm ô có quân · chạm khi máy nghĩ · Ván mới lúc máy nghĩ · đổi sáng/tối giữa ván · hai người không ghi thành tích |

---

## Ảnh chụp cần chuẩn bị
1. Splash (logo)
2. Menu, sáng và tối (thẻ 3 chế độ + thẻ thành tích)
3. Màn chơi giữa ván, có cột "đang đến lượt" được tô
4. Đường thắng đang nhấp nháy
5. Thẻ kết quả: cúp (thắng), dấu bằng (hòa), mặt buồn (thua)
6. Hộp thoại "Xóa toàn bộ thành tích đấu với máy?"
7. Cửa sổ Run của Android Studio: 19 test xanh; console của `Demo.kt`
