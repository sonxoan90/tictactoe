package vn.edu.hust.tictactoe.domain

/** Hai quân cờ. */
// [KT Ch3.4] enum class: chỉ có đúng 2 quân X và O
enum class Player {
    X, O;

    // [KT Ch2.3] hàm rút gọn một biểu thức; [KT Ch1.5] if là biểu thức
    fun other(): Player = if (this == X) O else X
}

/** Đối thủ của người chơi (luôn là X): người thứ hai, máy dễ hoặc máy khó. */
// [KT Ch3.4] enum class: 3 loại đối thủ, truyền qua Intent bằng tên (name)
enum class Opponent { HUMAN, EASY, HARD }
