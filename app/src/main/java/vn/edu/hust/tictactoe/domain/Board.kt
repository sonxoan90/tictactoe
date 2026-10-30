package vn.edu.hust.tictactoe.domain

/** Bàn cờ 3x3, ô đánh số 0..8 theo từng hàng. Bất biến: mỗi nước đi tạo ra một Board mới. */
// [KT Ch3.4] data class tự có ==, copy, toString · [KT Ch1] Player? : ô trống là null · [KT Ch2.2] tham số mặc định = bàn trống
data class Board(val cells: List<Player?> = List(SIZE * SIZE) { null }) {

    fun place(index: Int, player: Player): Board {
        // [KT Ch1] require: báo lỗi ngay nếu đánh vào ô đã có quân
        require(cells[index] == null) { "Ô $index đã có quân" }
        // [KT Ch1.6] List bất biến → chép ra MutableList để sửa bản sao, bàn cũ giữ nguyên
        return Board(cells.toMutableList().also { it[index] = player })
    }

    // [KT Ch2.5] filter giữ lại chỉ số các ô còn trống
    fun emptyCells(): List<Int> = cells.indices.filter { cells[it] == null }

    /** Ba ô tạo thành đường thắng, hoặc null nếu chưa ai thắng. */
    fun winningLine(): List<Int>? =
        // [KT Ch2.4] lambda + firstOrNull: đường thắng đầu tiên hoặc null · destructuring (a, b, c)
        LINES.firstOrNull { (a, b, c) -> cells[a] != null && cells[a] == cells[b] && cells[b] == cells[c] }

    // [KT Ch1] ?.let: chỉ lấy quân khi có đường thắng, không thì trả null
    fun winner(): Player? = winningLine()?.let { cells[it[0]] }

    // [KT Ch2.3] hàm rút gọn · [KT Ch2.5] none: không còn ô nào null
    fun isFull(): Boolean = cells.none { it == null }

    // [KT Ch3.4] companion object: hằng số dùng chung, gọi Board.SIZE
    companion object {
        const val SIZE = 3

        // [KT Ch1.6] danh sách lồng nhau: liệt kê sẵn 8 đường thắng
        private val LINES = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // hàng
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // cột
            listOf(0, 4, 8), listOf(2, 4, 6),                  // chéo
        )
    }
}
