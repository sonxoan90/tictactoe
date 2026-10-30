package vn.edu.hust.tictactoe.data

import android.content.Context
import vn.edu.hust.tictactoe.domain.Opponent

/** Thành tích khi đấu với máy, lưu bằng SharedPreferences. */
// [KT Ch3.1] lớp nhận Context qua constructor
class GameStats(context: Context) {

    // [KT Android] SharedPreferences: kho khóa–giá trị, còn sau khi tắt app
    private val prefs = context.getSharedPreferences("stats", Context.MODE_PRIVATE)

    // [KT Ch1] string template ghép tên khóa, ví dụ EASY_win · [KT Ch2.3] hàm rút gọn
    fun wins(opponent: Opponent) = prefs.getInt("${opponent.name}_win", 0)
    fun losses(opponent: Opponent) = prefs.getInt("${opponent.name}_loss", 0)
    fun draws(opponent: Opponent) = prefs.getInt("${opponent.name}_draw", 0)

    fun addWin(opponent: Opponent) = increase("${opponent.name}_win")
    fun addLoss(opponent: Opponent) = increase("${opponent.name}_loss")
    fun addDraw(opponent: Opponent) = increase("${opponent.name}_draw")

    // [KT Android] clear() xóa hết, apply() ghi nền không chặn giao diện
    fun reset() = prefs.edit().clear().apply()

    private fun increase(key: String) {
        prefs.edit().putInt(key, prefs.getInt(key, 0) + 1).apply()
    }
}
