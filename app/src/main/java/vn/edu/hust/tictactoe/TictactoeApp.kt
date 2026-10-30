package vn.edu.hust.tictactoe

import android.app.Application
import vn.edu.hust.tictactoe.ui.ThemeMode

// [KT Ch3.2] kế thừa Application: chạy một lần trước mọi Activity (khai báo trong Manifest)
class TictactoeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ThemeMode.applySaved(this)
    }
}
