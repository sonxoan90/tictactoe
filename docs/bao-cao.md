# Chuẩn bị báo cáo – Cờ Caro BK

Báo cáo trước giáo viên: **03/11/2026, khoảng 15 phút** (khoảng 12 phút trình bày và 3 phút hỏi đáp). Tài liệu gồm: (1) tóm tắt dự án, (2) kịch bản demo, (3) câu hỏi theo từng thành viên, (4) đưa code lên GitHub, (5) danh sách kiểm tra. Bố cục slide nằm ở `docs/slide-bo-cuc.md`.

> Slogan: *"Ba quân thẳng hàng là qua môn."*

---

## 1. Tóm tắt dự án (30 giây mở đầu)

- Game tic-tac-toe 3×3 trên Android, 3 chế độ: **máy dễ**, **máy khó** (minimax, không bao giờ thua), **hai người chơi**.
- Kotlin + XML Views, 3 Activity (Splash → Menu → Game) nối bằng Intent, ViewModel/LiveData, coroutine cho nước đi của máy, SharedPreferences, View tự vẽ có hiệu ứng.
- **Luật chơi và AI nằm trong `domain/`** (Kotlin thuần, không `import android.*`), kiểm thử bằng **19 unit test** JUnit; `Demo.kt` chạy thử máy đấu máy không cần emulator.
- Quy mô: 13 file Kotlin (5 file `domain`, 1 file `data`, 7 file `ui`/app) + 2 file test; 77 chỗ đánh dấu kiến thức `// [KT ChX.Y]` (tìm bằng `grep -r "\[KT" app/src`).
- Tính năng: đánh cờ với hiệu ứng vẽ quân, ô thắng nhấp nháy, thẻ kết quả, bảng điểm phiên, đổi bên đi trước mỗi ván, thành tích đấu máy lưu lâu dài, xóa thành tích có xác nhận, giao diện sáng/tối được ghi nhớ.

Luồng một lượt chạm (ai cũng phải nói được):

```
Chạm ô → CellView → viewModel.onCellClicked(i) → GameState.play(i) [domain]
       → LiveData → GameActivity.observe → render()
       → (lượt máy) coroutine delay(450) → Ai.chooseMove → applyMove
```

---

## 2. Kịch bản demo (khoảng 3 phút video + 30 giây thao tác trực tiếp)

### Chuẩn bị trước (ở nhà)
1. Bật emulator với **Quick Boot**, mở sẵn app một lần.
2. Menu → **Xóa thành tích** để bắt đầu từ 0 (thẻ thành tích toàn số 0, tạo tương phản khi tăng).
3. **Quay sẵn video** toàn bộ kịch bản để dự phòng (thanh công cụ emulator → Screen record).
4. Tập trước một ván **hòa máy khó** (gợi ý: X đánh ô giữa, sau đó luôn chặn) và một ván **thắng máy dễ** (đánh góc, tạo hai đường dọa cùng lúc).

### Các bước

| # | Thao tác | Nói gì | Ai nói |
|---|---|---|---|
| 1 | Mở app: Splash (logo phóng to) → Menu | Ba Activity, Splash `finish()` nên Back ở Menu là thoát | TV3 |
| 2 | Chỉ 3 thẻ chế độ và thẻ thành tích (toàn 0) | Thành tích đấu máy lưu bằng SharedPreferences | TV3 |
| 3 | **Đấu máy dễ**, thắng một ván | Quân được vẽ dần (custom View); đường thắng nhấp nháy, 0,7 giây sau hiện thẻ kết quả | TV2 |
| 4 | **Ván mới**: lần này O (máy) đi trước | Mỗi ván đổi bên đi trước cho công bằng | TV2 |
| 5 | Về menu → thành tích máy dễ: 1 thắng | `onResume` đọc lại thành tích | TV3 |
| 6 | **Đấu máy khó**, cố tình để hở một đường → máy **chặn** | Minimax thử hết mọi nước; không cần viết luật chặn riêng | TV1 |
| 7 | Giữa ván bấm **sáng/tối** | Activity bị tạo lại, **ván vẫn còn** nhờ ViewModel; quân không vẽ lại từ đầu | TV2 |
| 8 | Chơi tới hòa | Tic-tac-toe chơi hoàn hảo thì hòa; máy khó không bao giờ thua | TV1 |
| 9 | **Reset**: điểm phiên về 0 | Reset khác Ván mới; thành tích lâu dài vẫn giữ | TV2 |
| 10 | **Hai người chơi**: đánh nhanh một ván | Dòng trạng thái "Lượt của X/O", không ghi thành tích | TV2 |
| 11 | Menu → **Xóa thành tích** → hộp thoại → Xóa | Thao tác không hoàn tác được phải hỏi lại | TV3 |
| 12 | Android Studio: chạy bộ test (19 test xanh), rồi chạy `main()` trong `Demo.kt` | `domain` test được không cần emulator; `hardAiNeverLoses` thử mọi chuỗi nước của người | TV1 |

### Mẹo và dự phòng
- Emulator đơ: chiếu video quay sẵn và vẫn giải thích như bình thường.
- Nếu thầy bảo "xoay màn hình": màn chơi **khóa dọc** trong Manifest. Nói rõ và demo bằng sáng/tối (cùng cơ chế Activity tạo lại).
- Nước đầu của máy khó trên bàn trống có thể khựng nhẹ (máy duyệt hết khoảng 550 nghìn trạng thái). Nên để **người đi trước** khi demo máy khó.
- Chạy thử kịch bản **ít nhất 2 lần**, bấm giờ.

---

## 3. Câu hỏi thầy có thể hỏi

Mỗi thành viên đọc file `docs/on-tap/` và `docs/giai-thich/` của phần mình. Đáp án chi tiết nằm trong các file đó.

### Thành viên 1 – Luật chơi và AI (`01-domain-ai.md`, `on-tap/1-TV1-logic-ai.md`)
1. Kiểm tra thắng thế nào? Vì sao liệt kê sẵn 8 đường?
2. Vì sao ô trống là `null`? `List<Player?>` nghĩa là gì?
3. Vì sao `Board` bất biến? Liên quan gì tới minimax?
4. Minimax hoạt động thế nào? Điểm dừng là gì? Vì sao máy khó không bao giờ thua?
5. Vì sao điểm thắng là `1 + số ô trống`?
6. Máy dễ chọn nước thế nào? Vì sao người thắng được máy dễ?
7. `object`, `enum`, `data class`, `companion object` dùng ở đâu, vì sao?
8. Vì sao `chooseMove` nhận tham số `random`?
9. **Câu khó:** vì sao không dùng alpha-beta? Máy khó đi trước có chậm không?

### Thành viên 2 – Màn chơi (`02-man-choi.md`, `on-tap/2-TV2-man-choi.md`)
1. Vì sao dùng ViewModel? Đổi sáng/tối giữa ván thì chuyện gì xảy ra?
2. LiveData là gì? `observe(this)` truyền `this` để làm gì? Vì sao có cặp `_state`/`state`?
3. Máy "nghĩ" 450 ms bằng cách nào? Vì sao không dùng `Thread.sleep`?
4. Bấm Ván mới khi máy đang nghĩ thì sao? *(`job?.cancel()`.)*
5. Làm sao chặn người chạm khi đang là lượt máy?
6. `CellView` vẽ quân và tạo hiệu ứng thế nào? `invalidate()` để làm gì?
7. Ván mới khác Reset thế nào?
8. **Câu khó:** vì sao đổi sáng/tối không chạy lại hiệu ứng vẽ cả bàn?

### Thành viên 3 – Menu, lưu trữ, giao diện (`03-menu-luu-tru-giao-dien.md`, `on-tap/3-TV3-menu-giao-dien.md`)
1. App có mấy Activity, đi theo thứ tự nào, cái nào là LAUNCHER?
2. Intent truyền chế độ chơi thế nào?
3. SharedPreferences lưu thành tích thế nào? Tên khóa là gì? `apply` khác `commit`?
4. Vì sao đọc thành tích ở `onResume`?
5. Vì sao Xóa thành tích phải hỏi xác nhận?
6. Sáng/tối hoạt động thế nào và được nhớ ở đâu? `TictactoeApp` để làm gì?
7. Vì sao Splash gọi `finish()`?

### Câu hỏi chung
1. Vì sao dùng XML Views mà không dùng Jetpack Compose? *(Theo giáo trình; nhiều Activity + Intent.)*
2. Nhóm dùng AI thế nào? *(Trả lời trung thực, xem `on-tap/0-phan-chung.md` mục 6.)*
3. Chỉ một chỗ minh họa Kotlin Chương 2 trong code. *(`Board.emptyCells`: `filter`; `winningLine`: `firstOrNull` + destructuring.)*
4. Hạn chế và hướng phát triển? *(Thoát giữa ván thì mất ván; chỉ 3×3; người luôn là X; chưa có âm thanh; máy khó tính trên luồng chính. Hướng phát triển: caro 15×15 với alpha-beta, mức "trung bình", lưu ván, chọn quân.)*
5. Chạy được trên máy thật không? *(minSdk 26; nhóm chủ yếu chạy emulator, nên nói rõ.)*

---

## 4. Đưa code lên GitHub

Thư mục `Tictactoe` **chưa có git**. Mở Terminal của Android Studio ở thư mục gốc project:

```bash
git init
```
```bash
git add .
```
```bash
git commit -m "Cờ Caro BK: game, AI minimax, menu, tài liệu"
```

Tạo repo rỗng trên github.com (**không** tích README/.gitignore/license), rồi:

```bash
git remote add origin https://github.com/<tai-khoan>/co-caro-bk.git
```
```bash
git branch -M main
```
```bash
git push -u origin main
```

`.gitignore` đã bỏ qua `local.properties`, `build/`, `.gradle`. Nên thêm `.kotlin/` vào `.gitignore` trước khi commit. Các bạn clone về, mở bằng Android Studio, chờ Gradle sync, bấm Run.

---

## 5. Danh sách kiểm tra trước giờ báo cáo

- [ ] Cả nhóm đọc `docs/on-tap/0-phan-chung.md`; mỗi người đọc file riêng của mình.
- [ ] Ai cũng nói được luồng một lượt chạm và vì sao máy khó không thua.
- [x] Đã gắn thẻ `// [KT ChX.Y]` vào code (77 thẻ) và thêm test (19 test).
- [ ] Build lại trong Android Studio: `gradlew.bat assembleDebug` và `gradlew.bat testDebugUnitTest` đều thành công.
- [ ] Emulator bật Quick Boot, đã chạy thử kịch bản 2 lần, đã bấm giờ.
- [ ] Đã **Xóa thành tích** trước giờ demo.
- [ ] Có video quay sẵn để dự phòng.
- [ ] Code đã lên GitHub, link đã điền vào slide cuối.
- [ ] Slide theo `docs/slide-bo-cuc.md`.
