package vn.edu.hust.tictactoe.ui

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

/** Đổi giao diện sáng/tối và nhớ lựa chọn bằng SharedPreferences. */
// [KT Ch3.4] object: tiện ích dùng chung, không cần tạo đối tượng
object ThemeMode {

    // [KT Ch1] const val: hằng số lúc biên dịch
    private const val KEY = "night_mode"

    /** Gọi một lần khi app khởi động. Chưa chọn thì theo hệ thống. */
    fun applySaved(context: Context) {
        val mode = prefs(context).getInt(KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun isDark(context: Context): Boolean =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES

    fun toggle(context: Context) {
        // [KT Ch1.5] if là biểu thức: đang tối thì chuyển sáng và ngược lại
        val mode = if (isDark(context)) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES
        prefs(context).edit().putInt(KEY, mode).apply()
        // [KT Ch7] đổi cấu hình: Android hủy và tạo lại Activity
        AppCompatDelegate.setDefaultNightMode(mode) // Activity tự tạo lại với giao diện mới
    }

    private fun prefs(context: Context) = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
}
