# Bố cục slide báo cáo cuối môn – Cờ Caro BK

> IT4785 – Lập trình di động · Đề tài: **Ứng dụng Kotlin xây dựng game Android "Cờ Caro BK"**
> Thời lượng tối đa **15 phút**: khoảng 12 phút 20 giây trình bày và 2 phút 40 giây hỏi đáp. **11 slide** chính và 3 slide dự phòng.

Mạch trình bày theo đúng một báo cáo đồ án: **Bài toán → Phân tích, thiết kế → Stack → Áp dụng kiến thức → Sản phẩm → Đánh giá → Định hướng.**

## Phân bổ thời gian

| # | Phần | Slide | Người nói | Thời gian | Cộng dồn |
|---|---|---|---|---|---|
| 1 | Mở đầu | Tiêu đề | TV3 | 0:20 | 0:20 |
| 2 | **I. Bài toán** | Bối cảnh và yêu cầu bài toán | TV3 | 1:15 | 1:35 |
| 3 | **II. Giải pháp** | Phân tích, thiết kế: từ yêu cầu đến kiến trúc | TV1 | 1:00 | 2:35 |
| 4 | | Stack công nghệ và lý do chọn | TV2 | 0:50 | 3:25 |
| 5 | **III. Áp dụng kiến thức** | Kotlin (Chương 1–3) | TV1 | 1:50 | 5:15 |
| 6 | | Android (Chương 4–9) | TV2 | 1:30 | 6:45 |
| 7 | | Thuật toán máy chơi: minimax | TV1 | 1:20 | 8:05 |
| 8 | **IV. Sản phẩm** | Game đã xây dựng + demo trực tiếp | TV2 thao tác, TV3 nói | 2:15 | 10:20 |
| 9 | **V. Đánh giá** | Kiểm thử và đối chiếu yêu cầu | TV3 | 0:50 | 11:10 |
| 10 | **VI. Định hướng** | Hướng phát triển | TV1 | 0:50 | 12:00 |
| 11 | Kết | Kết luận, cảm ơn | TV3 | 0:20 | 12:20 |
| – | | Hỏi đáp | Cả nhóm | 2:40 | 15:00 |

Mỗi người nói khoảng 4 phút. **Lố giờ thì cắt theo thứ tự:** slide 4 chỉ nói bảng (bỏ cột lý do) → demo bỏ chế độ hai người → slide 6 chỉ nói cột ViewModel. Slide 2, 5, 7 giữ nguyên.

Trên mỗi slide của phần I–VI, góc trên trái ghi tên phần (ví dụ `III. ÁP DỤNG KIẾN THỨC`) để thầy biết đang ở đâu trong báo cáo.

---

## Slide 1 – Tiêu đề (0:20)

- Nhãn nhỏ: `BÁO CÁO CUỐI MÔN · IT4785 – LẬP TRÌNH DI ĐỘNG`
- Tiêu đề: **Cờ Caro BK**. Dòng phụ: *Ứng dụng Kotlin xây dựng game Android*
- Slogan: *"Ba quân thẳng hàng là qua môn."*
- Icon app (`docs/icon-512.png`) bên phải.
- Chân trang: Nhóm [TV1 · TV2 · TV3] · Giảng viên [Tên] · 03/11/2026.

---

## Slide 2 – I. Bài toán: bối cảnh và yêu cầu (1:15)

**Tiêu đề:** *Xây dựng một game Android hoàn chỉnh bằng Kotlin*

**Khung trên: yêu cầu của môn học**
- Một ứng dụng Android **chạy được**.
- **Áp dụng kiến thức đã học**: Kotlin Chương 1–3 là trọng tâm, kèm phần Android.
- Được dùng AI hỗ trợ, nhưng **nhóm phải hiểu và giải thích được** code.

**Khung dưới: bài toán nhóm chọn, tic-tac-toe 3×3 có máy chơi**

| Mã | Yêu cầu chức năng |
|---|---|
| F1 | Đánh cờ 3×3, X và O luân phiên; xét **thắng** (3 quân thẳng hàng trên 8 đường) và **hòa** (bàn đầy) |
| F2 | Ba chế độ: **máy dễ**, **máy khó**, **hai người** trên cùng máy |
| F3 | Máy khó chơi tối ưu: **không bao giờ thua** |
| F4 | Bảng điểm phiên; Ván mới đổi bên đi trước; Reset |
| F5 | Lưu **thành tích** đấu máy lâu dài; xóa có xác nhận |
| F6 | Giao diện **sáng/tối**, nhớ lựa chọn |

| Mã | Yêu cầu phi chức năng |
|---|---|
| N1 | Không mất ván khi Activity bị tạo lại (đổi cấu hình) |
| N2 | Luật chơi **kiểm thử tự động** được, không cần emulator |
| N3 | Code đơn giản, bám giáo trình, giải thích được |
| N4 | Phản hồi trực quan: hiệu ứng quân cờ, máy "suy nghĩ" có độ trễ |

**Lời nói:** "Vì sao chọn tic-tac-toe? Luật đơn giản nên nhóm tập trung được vào **áp dụng kiến thức**, và vẫn có một bài toán thuật toán thật: làm máy chơi không bao giờ thua."

---

## Slide 3 – II. Phân tích, thiết kế (1:00)

**Tiêu đề:** *Từ yêu cầu đến kiến trúc 3 tầng*

**Trái: kiến trúc**, 3 khối xếp dọc, mũi tên một chiều:
```
ui/      Splash · Menu · Game (Activity, ViewModel, CellView)
  ↓            ↘
data/    GameStats (SharedPreferences)
  ↓
domain/  Player · Board · GameState · Ai   ← Kotlin thuần, không import Android
```

**Phải: yêu cầu nằm ở tầng nào**

| Yêu cầu | Giải quyết ở | Thành phần |
|---|---|---|
| F1, F3 | `domain` | `Board`, `GameState`, `Ai` |
| F2, F4, N1, N4 | `ui` | `GameViewModel`, `GameActivity`, `CellView` |
| F5, F6 | `data`, `ui` | `GameStats`, `ThemeMode` |
| N2 | `domain` độc lập | 19 unit test JUnit |

**Câu chốt:** "Luật chơi tách khỏi Android, nên kiểm thử được và đổi giao diện không phải sửa luật."

---

## Slide 4 – II. Stack công nghệ (0:50)

**Tiêu đề:** *Công nghệ sử dụng và lý do chọn*

| Hạng mục | Lựa chọn | Lý do |
|---|---|---|
| Ngôn ngữ | **Kotlin** | Ngôn ngữ của môn học; null safety, data class, lambda |
| Công cụ | Android Studio, Gradle Kotlin DSL, version catalog | Mặc định của template mới |
| SDK | minSdk 26, targetSdk 37 | Phủ hầu hết thiết bị; icon thích ứng có sẵn từ Android 8 |
| Giao diện | **XML Views + View Binding**, Material Components 3 | Theo giáo trình (Ch5); Material 3 có sẵn chế độ sáng/tối |
| Màn hình | 3 **Activity** + **Intent** | Theo giáo trình; đơn giản, không cần Fragment |
| Trạng thái | **ViewModel + LiveData** | Giữ ván khi Activity tạo lại (Ch8) |
| Bất đồng bộ | **Coroutine** (`viewModelScope`) | Máy "nghĩ" mà không chặn giao diện (Ch9) |
| Lưu trữ | **SharedPreferences** | Dữ liệu nhỏ dạng khóa–giá trị; không cần CSDL |
| Kiểm thử | **JUnit 4** | Test `domain` thuần Kotlin |

Dòng dưới, chữ nhỏ: *Không dùng Jetpack Compose, Navigation, Fragment, thư viện ngoài, để bám giáo trình và dễ giải thích.*

---

## Slide 5 – III. Áp dụng kiến thức Kotlin, Chương 1–3 (1:50) ⭐ trọng tâm chấm

**Tiêu đề:** *Kotlin giải quyết bài toán như thế nào*

**Trái: code thật, rút gọn từ `domain/`, có nhãn số**
```kotlin
enum class Player { X, O; fun other() = if (this == X) O else X }      // ①
data class Board(val cells: List<Player?> = List(9) { null }) {        // ② ③ ④
    fun place(i: Int, p: Player) =
        Board(cells.toMutableList().also { it[i] = p })                // ⑤
    fun emptyCells() = cells.indices.filter { cells[it] == null }      // ⑥
    fun winner() = winningLine()?.let { cells[it[0]] }                 // ⑦
}
data class GameState(val board: Board = Board(), val turn: Player = Player.X) {
    val isOver get() = winner != null || board.isFull()                // ⑧
}
object Ai { fun chooseMove(..., random: Random = Random): Int }        // ⑨
```

**Phải: kiến thức → giải quyết vấn đề gì**

| # | Kiến thức (chương) | Giải quyết |
|---|---|---|
| ① | enum có hàm (3.4), hàm rút gọn (2.3) | Chỉ có 2 quân; đổi lượt bằng `other()` |
| ② | data class (3.4) | So sánh bàn cờ bằng `==`, dễ test |
| ③ | null safety (1) | Ô trống là `null`, compiler buộc xử lý |
| ④ | tham số mặc định (2.2) | Tạo bàn trống bằng `Board()` |
| ⑤ | List bất biến (1.6) | Mỗi nước tạo bàn mới, minimax thử nước không làm hỏng bàn thật |
| ⑥ | lambda, `filter` (2.4, 2.5) | Liệt kê ô trống trong 1 dòng |
| ⑦ | `?.let` (1) | Có đường thắng mới lấy quân |
| ⑧ | getter tính toán (3.1) | Trạng thái ván luôn khớp với bàn |
| ⑨ | `object` (3.4), tham số mặc định | Một máy chơi duy nhất; test truyền `Random(seed)` |

Chân trang: *77 chỗ được đánh dấu `// [KT ChX.Y]` trong code · tìm bằng `grep -r "\[KT" app/src`.*

---

## Slide 6 – III. Áp dụng kiến thức Android, Chương 4–9 (1:30)

**Tiêu đề:** *Android: màn hình, vòng đời, trạng thái, lưu trữ*

Bốn ô, mỗi ô gồm kiến thức, áp dụng ở đâu và một ảnh nhỏ:

| Activity, Intent, Layout (Ch4–6) | Vòng đời (Ch7) |
|---|---|
| Splash → Menu → Game; truyền chế độ bằng `putExtra("opponent")` · Layout XML + View Binding + Material 3 | Menu đọc lại thành tích ở `onResume` · Splash `finish()` và gỡ `Handler` ở `onDestroy` |
| **ViewModel + LiveData (Ch8)** | **Coroutine + lưu trữ (Ch9)** |
| `GameViewModel` giữ ván khi đổi sáng/tối · `_state` private, `state` public · Activity chỉ `observe` rồi vẽ | `viewModelScope.launch { delay(450) }` cho máy đi · SharedPreferences: `EASY_win`, `HARD_draw`, `night_mode` |

Dải dưới: **Custom View `CellView`**: kế thừa `View`, vẽ X/O bằng `Canvas`, `ValueAnimator` vẽ dần và làm ô thắng nhấp nháy.

---

## Slide 7 – III. Thuật toán máy chơi: minimax (1:20)

**Tiêu đề:** *Máy khó: thử hết mọi nước, không bao giờ thua*

- **Trái:** hàm `minimax` (11 dòng), đánh dấu ① điểm dừng: thắng `1 + ô trống` / thua `-1 - ô trống` / hòa `0`; ② đệ quy cho từng ô trống; ③ lượt máy chọn max, lượt người chọn min.
- **Phải:** ví dụ bàn `X X . / . O . / . . .`, máy là O: **ô 2 → 0 điểm** (chặn), các ô khác **→ -5** (thua ngay). Máy tự biết chặn mà không cần viết luật chặn.
- **Dưới:** so sánh hai mức
  - Máy dễ: thắng ngay nếu được, còn lại đi ngẫu nhiên.
  - Máy khó: minimax duyệt cả cây (549.946 nút từ bàn trống).

---

## Slide 8 – IV. Sản phẩm: game đã xây dựng (2:15)

**Tiêu đề:** *Cờ Caro BK*

**Trên slide:** dải 5 ảnh chụp màn hình có chú thích:
1. Splash: logo phóng to
2. Menu: thẻ 3 chế độ, thẻ thành tích, nút sáng/tối
3. Màn chơi: bảng điểm, cột đang đến lượt được tô, quân vẽ dần
4. Kết thúc: đường thắng nhấp nháy, thẻ kết quả (cúp / dấu bằng / mặt buồn)
5. Giao diện tối

Sau đó **chuyển sang emulator, demo trực tiếp khoảng 1 phút 30 giây** (có video dự phòng):

| Bước | Thao tác | Thể hiện yêu cầu |
|---|---|---|
| 1 | Máy khó: để hở một đường → máy chặn | F2, F3 |
| 2 | Giữa ván bấm sáng/tối → ván vẫn còn | F6, N1 |
| 3 | Chơi tới hòa → thẻ kết quả → Về menu: thành tích máy khó tăng | F1, F5 |
| 4 | Hai người chơi, đánh nhanh 1 ván | F2, F4 |

---

## Slide 9 – V. Đánh giá: kiểm thử và đối chiếu yêu cầu (0:50)

**Tiêu đề:** *Kết quả đạt được*

**Trái: kiểm thử**
- **19 unit test JUnit, pass hết**: xét thắng và hòa (7) · lượt đi và bất biến (4) · máy chơi (8).
- `hardAiNeverLoses`: người chơi thử **mọi** chuỗi nước đi, máy không thua lần nào.
- `Demo.kt`: 100 ván máy dễ đấu máy khó → máy khó thắng 80, hòa 20, **thua 0**.

**Phải: đối chiếu yêu cầu**

| Yêu cầu | Kết quả |
|---|---|
| F1–F6 | ✅ Đạt |
| N1 Không mất ván | ✅ Khi Activity tạo lại · ⚠️ Thoát app giữa ván thì mất |
| N2 Kiểm thử tự động | ✅ 19 test |
| N3 Bám giáo trình | ✅ 77 chỗ đánh dấu kiến thức |
| N4 Phản hồi trực quan | ✅ · ⚠️ Nước đầu của máy khó trên bàn trống có thể khựng nhẹ |

Dòng dưới: *Cách dùng AI: nhóm phân tích yêu cầu → Claude Code viết code → nhóm chạy test, đọc giải thích, chia nhau hiểu từng tầng.*

---

## Slide 10 – VI. Định hướng phát triển (0:50)

**Tiêu đề:** *Hướng phát triển*

Lộ trình 3 chặng, mỗi chặng ghi kiến thức sẽ dùng:

| Ngắn hạn: hoàn thiện | Trung hạn: mở rộng game | Dài hạn: kết nối |
|---|---|---|
| Lưu ván đang chơi khi thoát app | **Caro 15×15, luật 5 quân** | **Chơi online** 2 máy (Ch13 Internet / Firebase) |
| Mức **Trung bình** (minimax đôi khi đi sai) | Máy chơi bằng **alpha-beta + hàm đánh giá** | Bảng xếp hạng |
| Cho người chọn quân X/O, ai đi trước | **Lịch sử ván** bằng Room (Ch9), xem lại từng nước | Đăng nhập, đồng bộ thành tích |
| Âm thanh, rung khi thắng | Danh sách lịch sử bằng RecyclerView (Ch5, Ch10) | |
| Tính nước máy ở luồng nền (`Dispatchers.Default`) | | |

**Lời nói:** nối với kiến trúc. "Nhờ tách `domain`, mở rộng lên 15×15 chủ yếu sửa `Board` và `Ai`; giao diện gần như giữ nguyên."

---

## Slide 11 – Kết luận (0:20)

- Ba ý chốt:
  1. Hoàn thành game Android đáp ứng F1–F6.
  2. Áp dụng Kotlin Ch1–3 và Android Ch4–9 vào bài toán thật.
  3. Có kiểm thử tự động và thuật toán máy chơi tối ưu.
- **Cảm ơn thầy và các bạn đã lắng nghe** · link GitHub · *mời thầy thử hòa máy khó*.

---

## Hỏi đáp (2:40)

- Để sẵn emulator ở màn **Đấu máy khó**.
- Phân công trả lời: yêu cầu, lưu trữ, Intent, giao diện → TV3 · ViewModel, coroutine, vòng đời, CellView → TV2 · Kotlin, minimax, test, kiến trúc → TV1.
- Câu hỏi luyện tập: `docs/bao-cao.md` mục 3 và `docs/on-tap/`.

## Slide dự phòng

| # | Nội dung | Mở khi thầy hỏi |
|---|---|---|
| A | `chooseMove` đầy đủ (máy dễ và máy khó) | "Máy dễ chọn nước thế nào?" |
| B | Luồng một lượt chạm: CellView → ViewModel → `GameState.play` → LiveData → render → lượt máy | "Bấm một ô thì code chạy thế nào?" |
| C | Bảng tình huống đặc biệt: chạm ô có quân, chạm lúc máy nghĩ, Ván mới lúc máy nghĩ, đổi sáng/tối giữa ván | "Người chơi bấm lung tung thì sao?" |

## Ảnh và tài liệu cần chuẩn bị

1. Icon app: `docs/icon-512.png` (slide 1)
2. 5 ảnh màn hình: Splash, Menu, giữa ván, kết thúc, giao diện tối (slide 8)
3. Ảnh bàn `X X . / . O .` chụp từ app (slide 7)
4. Ảnh 19 test xanh và console `Demo.kt` (slide 9)
5. Video demo 1 phút 30 giây để dự phòng (slide 8)
