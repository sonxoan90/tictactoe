# THÀNH VIÊN 3 – Trải nghiệm: Splash, Menu, lưu trữ, sáng/tối

> Học xong `0-phan-chung.md` trước. Thời gian: khoảng 2,5 giờ.

## Bạn phụ trách

| Slide | Nội dung |
|---|---|
| 1–2 | Mở đầu, bài toán và 3 chế độ |
| 8 (một nửa) | Vòng đời và lưu trữ |
| 10 | Kết luận, dùng AI, hạn chế |

**File code:** `TictactoeApp.kt`, `ui/splash/SplashActivity.kt`, `ui/menu/MenuActivity.kt`, `data/GameStats.kt`, `ui/ThemeMode.kt`, `res/layout/activity_menu.xml`, `AndroidManifest.xml`. Đọc kèm `docs/giai-thich/03-menu-luu-tru-giao-dien.md`.

---

## 1. Đường đi của app

```
TictactoeApp.onCreate  (áp dụng sáng/tối đã lưu)
        ↓
SplashActivity  (LAUNCHER, 2 giây, finish())
        ↓ Intent
MenuActivity  ── chọn chế độ ──►  GameActivity (extra "opponent")
        ▲                                │
        └──────── Back / Về menu ────────┘   → Menu.onResume cập nhật thành tích
```

## 2. Intent mang dữ liệu sang màn khác

```kotlin
// GameActivity.kt
companion object {
    fun intent(context: Context, opponent: Opponent) =
        Intent(context, GameActivity::class.java).putExtra(EXTRA_OPPONENT, opponent.name)
}
// MenuActivity.kt
binding.btnHard.setOnClickListener { startActivity(GameActivity.intent(this, Opponent.HARD)) }
```
Bên nhận: `Opponent.valueOf(intent.getStringExtra(EXTRA_OPPONENT) ?: Opponent.HUMAN.name)`; có `?:` phòng trường hợp không có extra.

## 3. SharedPreferences (`GameStats`)

- Kho **khóa–giá trị** nhỏ, lưu trong file riêng của app, còn sau khi tắt app.
- Khóa ghép bằng string template: `"${opponent.name}_win"` → `EASY_win`, `HARD_loss`…
- Đọc: `prefs.getInt(key, 0)` (mặc định 0). Ghi: `prefs.edit().putInt(key, giá trị).apply()`.
- **Chỉ ghi khi đấu máy**; hai người chơi không ghi.
- `apply()` ghi nền, không chặn giao diện; `commit()` ghi ngay và trả `true/false`.

## 4. Vòng đời

| Callback | Ở app |
|---|---|
| `onCreate` | Gắn View Binding, sự kiện nút |
| `onResume` (Menu) | **Đọc lại thành tích**, vì Back từ màn chơi không chạy lại `onCreate` |
| `onDestroy` (Splash) | `handler.removeCallbacksAndMessages(null)`: đóng Splash sớm thì không mở Menu nữa |

## 5. Xóa thành tích có xác nhận

```kotlin
MaterialAlertDialogBuilder(this)
    .setMessage(R.string.reset_confirm)
    .setPositiveButton(R.string.yes) { _, _ -> stats.reset(); showStats() }
    .setNegativeButton(R.string.no, null)
    .show()
```
Thao tác **không hoàn tác được** thì phải hỏi lại. `_` là tham số lambda không dùng.

## 6. Sáng/tối (`ThemeMode`)

- `toggle`: đang tối thì chuyển sáng và ngược lại, lưu vào prefs `settings` khóa `night_mode`, gọi `AppCompatDelegate.setDefaultNightMode`. Android **tự tạo lại Activity** với màu mới.
- `applySaved` gọi trong `TictactoeApp.onCreate`: mở app lần sau vẫn đúng chế độ đã chọn. Chưa chọn thì theo hệ thống.
- Màu lấy từ theme Material 3 (`colorPrimary` cho X, `colorTertiary` cho O), nên đổi chế độ thì quân cờ đổi màu theo.

## 7. Giao diện Menu (`activity_menu.xml`)

- Một `MaterialCardView` chứa 3 dòng chế độ: ô icon (mặt cười / bộ não / hai người), tên, phụ đề ("Khởi động nhẹ nhàng", "Thử thách chiến thuật", "So tài cùng bạn bè"), mũi tên.
- Thẻ "Thành tích của bạn": hàng Máy dễ và Máy khó, mỗi hàng 3 số Thắng / Hòa / Thua.
- Mọi chuỗi nằm trong `strings.xml` (tiếng Việt); kiểu chữ số thành tích tách thành `styles.xml` (`StatNumber`, `StatLabel`).
- `enableEdgeToEdge()` + `setOnApplyWindowInsetsListener`: giao diện tràn viền nhưng không bị thanh trạng thái che.

## 8. Câu hỏi của bạn

1. App có mấy Activity, đi theo thứ tự nào? Activity nào là LAUNCHER?
2. Intent truyền chế độ chơi thế nào? Vì sao truyền `opponent.name` (chuỗi)?
3. SharedPreferences là gì, lưu được kiểu nào? `apply` khác `commit`?
4. Vì sao đọc thành tích ở `onResume`?
5. Vì sao Splash gọi `finish()` và gỡ `Handler` ở `onDestroy`?
6. Vì sao Xóa thành tích phải hỏi xác nhận? Xóa những gì?
7. Đổi sáng/tối hoạt động thế nào, lưu ở đâu? `TictactoeApp` để làm gì?
8. Vì sao các Activity phụ để `exported="false"`?
9. **Câu hạn chế:** app còn thiếu gì? *(Thoát giữa ván thì mất ván; bảng điểm phiên không lưu; chỉ bàn 3×3; chưa có âm thanh; người chơi luôn là X.)*
