# Cờ Caro BK

Game cờ caro 3×3 (tic-tac-toe) cho Android. Chơi với máy ở hai mức độ hoặc chơi cùng bạn trên một thiết bị, giao diện Material 3 có chế độ sáng/tối.

## Tính năng

**Chế độ chơi**
- **Đấu máy dễ:** máy thắng ngay nếu có nước thắng, còn lại đi ngẫu nhiên.
- **Đấu máy khó:** máy dùng thuật toán minimax nên không bao giờ thua.
- **Hai người chơi:** hai người luân phiên đánh trên cùng một máy.

**Trong ván chơi**
- Thẻ điểm Bạn / Hòa / Máy, cột của bên đang đến lượt được làm nổi.
- Chip báo lượt kèm icon quân cờ ("Lượt của bạn", "Máy đang nghĩ…").
- Quân X/O tự vẽ bằng Canvas: vẽ dần khi đánh, có vầng sáng; ô thuộc đường thắng nhấp nháy.
- Thẻ kết quả khi ván kết thúc (thắng, thua, hòa) kèm tỉ số, có nút "Ván mới" và "Về menu".
- "Ván mới" đổi bên đi trước mỗi ván cho công bằng; "Reset" xóa bàn và đưa bảng điểm về 0.
- Ván đang chơi được giữ nguyên khi xoay màn hình.

**Khác**
- Thành tích thắng / hòa / thua khi đấu máy được lưu lại sau khi tắt app, có nút xóa.
- Đổi giao diện sáng/tối bằng một nút, lựa chọn được nhớ lại.
- Màn khởi động có hiệu ứng logo.

## Thuật toán máy khó

Máy thử mọi nước đi có thể và giả lập toàn bộ ván cho đến khi kết thúc, giả định đối thủ cũng chơi tối ưu:

- Lượt của máy chọn nước có điểm cao nhất, lượt của đối thủ chọn nước có điểm thấp nhất.
- Điểm ván kết thúc: máy thắng `+1 + số ô trống` (thắng càng sớm càng cao), máy thua `-1 - số ô trống`, hòa `0`.
- Khi nhiều nước cùng điểm, máy chọn ngẫu nhiên để các ván không lặp lại.

Cờ 3×3 chỉ có tối đa 9! = 362.880 chuỗi nước đi nên không cần cắt tỉa. Unit test duyệt mọi chuỗi nước đi của người chơi để kiểm tra máy khó không thua.

## Công nghệ

| Hạng mục | Lựa chọn |
|---|---|
| Ngôn ngữ | Kotlin |
| Giao diện | XML Views, ConstraintLayout, ViewBinding, Material Components (Material 3) |
| Kiến trúc | ViewModel + LiveData cho màn chơi; logic game tách riêng trong `domain/` |
| Lưu trữ | SharedPreferences |
| Build | Gradle Kotlin DSL, version catalog |
| SDK | `minSdk 26`, `targetSdk 37` |
| Test | JUnit 4 |

## Cấu trúc mã nguồn

```
app/src/main/java/vn/edu/hust/tictactoe/
├── TictactoeApp.kt            áp dụng giao diện sáng/tối đã lưu khi khởi động
├── domain/                    logic thuần Kotlin, không phụ thuộc Android
│   ├── Player.kt              quân X/O và loại đối thủ
│   ├── Board.kt               bàn cờ 3x3 bất biến, tìm đường thắng
│   ├── GameState.kt           bàn cờ + lượt đi, kết quả ván
│   └── Ai.kt                  máy dễ và máy khó (minimax)
├── data/
│   └── GameStats.kt           lưu thành tích bằng SharedPreferences
└── ui/
    ├── ThemeMode.kt           đổi và nhớ giao diện sáng/tối
    ├── splash/SplashActivity.kt
    ├── menu/MenuActivity.kt
    └── game/
        ├── GameActivity.kt    vẽ giao diện từ dữ liệu của ViewModel
        ├── GameViewModel.kt   giữ ván, lượt máy, bảng điểm
        └── CellView.kt        ô cờ tự vẽ (nét vẽ dần, vầng sáng, nhấp nháy)
```

## Chạy dự án

Yêu cầu: Android Studio bản mới (hỗ trợ AGP 9.x) và JDK 11 trở lên.

1. Mở thư mục dự án bằng Android Studio và chờ Gradle đồng bộ.
2. Chọn emulator hoặc thiết bị thật (Android 8.0 trở lên) rồi bấm **Run**.

Hoặc dùng dòng lệnh:

```bash
./gradlew installDebug        # build và cài bản debug lên thiết bị đang kết nối
./gradlew testDebugUnitTest   # chạy unit test
```

Trên Windows dùng `gradlew.bat` thay cho `./gradlew`.
