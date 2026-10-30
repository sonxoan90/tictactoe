# PHẦN CHUNG – Cả nhóm đều phải nắm

> Thời gian học: khoảng 1 giờ, học **cùng nhau**.
> Mục tiêu: bất kỳ ai trong nhóm cũng trả lời được các câu ở mục 7.

---

## 1. App của nhóm là gì? (nói trong 30 giây)

**Cờ Caro BK** là game tic-tac-toe 3×3 chạy trên Android.
- Hai bên **X** và **O** đánh xen kẽ vào 9 ô.
- Ai có **ba quân thẳng hàng** (hàng ngang, cột dọc hoặc đường chéo) trước là **thắng**.
- Bàn đầy mà chưa ai thắng là **hòa**.
- Ba chế độ: **máy dễ**, **máy khó** (không bao giờ thua) và **hai người chơi**.
- Thành tích đấu máy được lưu lại; có giao diện sáng/tối.

Slogan: *"Ba quân thẳng hàng là qua môn."*

---

## 2. Code chia làm 3 phần

Hình dung app như một **giải đấu cờ**:

| Tầng | Giống như | Làm gì | Thư mục |
|---|---|---|---|
| **domain** | **Trọng tài và kỳ thủ máy** | Luật chơi, xét thắng/hòa, máy chọn nước | `domain/` |
| **data** | **Sổ ghi thành tích** | Lưu số ván thắng/hòa/thua | `data/` |
| **ui** | **Bàn cờ và khán đài** | Nhận chạm, vẽ quân, chuyển màn | `ui/` |

**Quy tắc quan trọng nhất:** trọng tài (`domain`) không biết gì về màn hình. Nó chỉ nhận "bàn cờ + ô vừa chạm" và trả ra "bàn cờ mới". Nhờ vậy luật chơi và máy chơi được **kiểm thử tự động** mà không cần mở emulator.

> **Câu nói mẫu:** "Code chia 3 tầng: `domain` chứa luật chơi và AI bằng Kotlin thuần, `data` lưu thành tích bằng SharedPreferences, `ui` lo giao diện. `domain` không phụ thuộc Android nên test được bằng JUnit."

---

## 3. Bàn cờ được biểu diễn thế nào?

9 ô đánh số từ 0 đến 8, đọc từ trái sang phải, trên xuống dưới:
```
0 | 1 | 2
3 | 4 | 5
6 | 7 | 8
```
Trong code là một **danh sách 9 phần tử** `List<Player?>`: mỗi phần tử là `X`, `O` hoặc `null` (ô trống).

Ví dụ bàn `X . O / . X . / . . .` là `[X, null, O, null, X, null, null, null, null]`.

---

## 4. Chuyện gì xảy ra khi chạm một ô? (AI CŨNG PHẢI NÓI ĐƯỢC)

Giống **chạy tiếp sức**:

```
1. Ngón tay chạm ô số 4
2. CellView          "có người chạm tôi" → viewModel.onCellClicked(4)
3. GameViewModel     "có đang là lượt máy không?" Không → chuyển cho luật chơi
4. GameState.play(4) đặt quân, đổi lượt → trả về trạng thái MỚI
5. GameViewModel     cập nhật LiveData: "bàn cờ đổi rồi!"
6. GameActivity      đang lắng nghe → vẽ lại 9 ô và dòng "Lượt của…"
7. Tới lượt máy?     chờ 0,45 giây → Ai chọn ô → quay lại bước 4
```

> **Câu nói mẫu:** "Chạm ô thì ViewModel gọi `play` trong `domain`, được trạng thái mới thì gán vào LiveData; Activity đang `observe` nên tự vẽ lại. Nếu tới lượt máy, ViewModel chờ 450 mili giây rồi hỏi `Ai` chọn ô."

---

## 5. Xét thắng và máy khó (AI CŨNG PHẢI GIẢI THÍCH ĐƯỢC)

**Xét thắng:** bàn 3×3 chỉ có **8 đường**: 3 hàng, 3 cột, 2 chéo. Code liệt kê sẵn 8 đường đó, rồi xem có đường nào **cả 3 ô cùng một quân** (và không trống) không.

**Máy khó – minimax**, giống **đánh cờ trong đầu**:
- Trước mỗi nước, máy tự hỏi: "Nếu mình đánh ô này, đối thủ giỏi nhất sẽ đáp lại thế nào? Rồi mình đáp lại thế nào?…" cho tới hết ván.
- Kết thúc: **máy thắng = điểm dương**, **máy thua = điểm âm**, **hòa = 0**.
- Tới lượt máy thì chọn điểm **cao nhất** (max); tới lượt người thì giả định người chọn điểm **thấp nhất** cho máy (min).
- Vì thử **hết mọi khả năng**, máy không bao giờ đi vào nước thua. Tic-tac-toe nếu hai bên chơi hoàn hảo thì **luôn hòa**, nên người chơi giỏi nhất cũng chỉ hòa được máy khó.

**Máy dễ:** có nước thắng ngay thì đánh, còn lại **chọn bừa**. Máy dễ không biết chặn nên người chơi thắng được.

**Tự luyện:** máy là O, tới lượt O. Bàn `X X . / . O . / . . .`. Máy khó đánh ô nào? *(Ô 2: chặn X. Các ô khác đều thua ngay lượt sau.)*

---

## 6. Nhóm dùng AI thế nào? (trả lời trung thực)

> "Nhóm phân tích bài toán, chọn công nghệ theo giáo trình và mô tả yêu cầu. Claude Code viết code theo từng bước. Sau mỗi bước nhóm chạy thử, đọc tài liệu giải thích và chia nhau phụ trách hiểu từng phần."

**Không được** nói là tự viết toàn bộ.

---

## 7. Câu hỏi chung (ai cũng phải trả lời được)

| Câu hỏi | Trả lời ngắn |
|---|---|
| App làm gì? | Tic-tac-toe 3×3, 3 chế độ, lưu thành tích, sáng/tối |
| Code chia thế nào? | 3 tầng: `domain` luật chơi + AI, `data` lưu trữ, `ui` giao diện |
| Chạm một ô thì chuyện gì xảy ra? | CellView → ViewModel → `GameState.play` → LiveData → Activity vẽ lại |
| Có mấy đường thắng? | 8 |
| Vì sao máy khó không thua? | Minimax thử hết mọi nước tới cuối ván; chơi hoàn hảo thì hòa |
| Máy dễ khác máy khó? | Dễ: thắng ngay nếu được, còn lại ngẫu nhiên. Khó: minimax |
| Thành tích lưu ở đâu? | SharedPreferences |
| Vì sao không dùng Jetpack Compose? | Giáo trình dạy XML Views, nhóm làm theo giáo trình |
| Nhóm dùng AI thế nào? | Như mục 6 |

---

## 8. Mỗi người tự làm được

- [ ] Mở project bằng Android Studio, bấm Run, app chạy trên emulator.
- [ ] Chơi đủ 3 chế độ; thắng được máy dễ; hòa được máy khó.
- [ ] Đổi sáng/tối **giữa ván** và thấy ván không mất.
- [ ] Chạy bộ test (19 test xanh) và chạy `main()` trong `domain/Demo.kt`.
- [ ] Chạy tay ví dụ minimax ở mục 5 trên giấy.
