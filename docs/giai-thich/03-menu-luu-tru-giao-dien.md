# Phần 3: Splash, Menu, lưu trữ và giao diện sáng/tối

## Làm gì
Màn khởi động, menu chọn chế độ, hiển thị và xóa thành tích, đổi giao diện sáng/tối và nhớ lựa chọn.

## File
| File | Vai trò |
|---|---|
| `TictactoeApp.kt` | Lớp `Application` (khai báo `android:name=".TictactoeApp"` trong Manifest), gọi `ThemeMode.applySaved` khi app khởi động |
| `ui/splash/SplashActivity.kt` | Activity LAUNCHER: logo phóng to (`OvershootInterpolator`), chữ hiện dần, 2 giây sau mở Menu rồi `finish()` |
| `ui/menu/MenuActivity.kt` | Gắn sự kiện 3 thẻ chế độ, hiện thành tích, hộp thoại xóa thành tích, nút sáng/tối |
| `data/GameStats.kt` | SharedPreferences `"stats"`: `wins/draws/losses(opponent)`, `addWin/addDraw/addLoss`, `reset` |
| `ui/ThemeMode.kt` | `object`: `applySaved`, `isDark`, `toggle`; lưu `night_mode` trong prefs `"settings"` |
| `res/layout/activity_menu.xml` | Thẻ `MaterialCardView` chứa 3 dòng chế độ (icon, tên, phụ đề, mũi tên) và thẻ thành tích |

## Luồng
- **Mở app:** `TictactoeApp.onCreate` (áp dụng sáng/tối) → `SplashActivity` → `MenuActivity`. Splash gọi `finish()` nên bấm Back ở Menu thì thoát app, không quay lại Splash.
- **Chọn chế độ:** `startActivity(GameActivity.intent(this, Opponent.EASY))`. Intent mang tên enum dạng chuỗi; màn chơi đọc lại bằng `Opponent.valueOf(...)`.
- **Quay về Menu:** `onResume` gọi `showStats()` để cập nhật số thắng/hòa/thua vừa thay đổi.
- **Xóa thành tích:** `MaterialAlertDialogBuilder` hỏi "Xóa toàn bộ thành tích đấu với máy?" → Xóa → `stats.reset()` → hiện lại.
- **Sáng/tối:** `ThemeMode.toggle` lưu lựa chọn rồi `AppCompatDelegate.setDefaultNightMode(...)`; Android tự tạo lại Activity với theme mới. Icon nút đổi giữa mặt trăng và mặt trời.

## `GameStats`: cách đặt tên khóa
```kotlin
fun wins(opponent: Opponent) = prefs.getInt("${opponent.name}_win", 0)
private fun increase(key: String) {
    prefs.edit().putInt(key, prefs.getInt(key, 0) + 1).apply()
}
```
Một hàm chung cho cả máy dễ và máy khó nhờ **string template** ghép tên enum. Giá trị mặc định `0` khi chưa có khóa.

## 5 câu thầy có thể hỏi
1. **SharedPreferences là gì, lưu được gì?** Kho cặp khóa–giá trị nhỏ (Int, String, Boolean…) lưu trong file XML riêng của app; hợp với thành tích và cài đặt.
2. **`apply()` khác `commit()`?** `apply` ghi nền, không trả kết quả, không chặn giao diện; `commit` ghi ngay và trả `Boolean`.
3. **Vì sao cập nhật thành tích ở `onResume` mà không ở `onCreate`?** Khi Back từ màn chơi, Menu chỉ chạy `onRestart → onStart → onResume`, không chạy lại `onCreate`.
4. **Vì sao Splash gọi `finish()`?** Để bấm Back ở Menu không quay lại Splash; `onDestroy` gỡ `Handler` để không mở Menu khi Splash đã bị đóng.
5. **Vì sao các Activity phụ để `exported="false"`?** Chỉ app mình được mở chúng; riêng Splash `exported="true"` vì là màn LAUNCHER.
