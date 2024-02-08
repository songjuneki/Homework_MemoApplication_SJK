package com.example.homework_memoapplication_sjk

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.homework_memoapplication_sjk.databinding.AddMemoActivityBinding
import java.util.Calendar
import java.util.TimeZone

class AddMemoActivity: AppCompatActivity() {

    private lateinit var binding: AddMemoActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = AddMemoActivityBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolbar()
        Util.showSoftKey(binding.addMemoTitleInputEdit, this)
    }

    private fun initToolbar() {
        binding.apply {
            with(addMemoToolbar) {
                setOnMenuItemClickListener {
                    if (it.itemId == R.id.add_memo_menu_done) {
                        checkInput()?.let { memo ->
                            intent.putExtra("memo", memo)
                            setResult(RESULT_OK, intent)
                            finish()
                        } ?: Toast.makeText(this@AddMemoActivity, "제목 및 내용을 입력해주세요", Toast.LENGTH_SHORT).show()

                        return@setOnMenuItemClickListener true
                    }
                    return@setOnMenuItemClickListener false
                }

                setNavigationOnClickListener {
                    setResult(RESULT_CANCELED)
                    finish()
                }

                setOnClickListener {
                    Util.hideSoftInput(this@AddMemoActivity)
                }
            }
        }
    }

    private fun checkInput(): Memo? {
        binding.apply {
            val title = addMemoTitleInputEdit.text.toString()
            val content = addMemoContentInputEdit.text.toString()

            if (title.isBlank() || content.isBlank())
                return null

            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)+1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val min = calendar.get(Calendar.MINUTE)
            val sec = calendar.get(Calendar.SECOND)

            val date = "${year}.${String.format("%02d", month)}.${String.format("%02d", day)} ${String.format("%02d", hour)}:${String.format("%02d", min)}:${String.format("%02d", sec)}"

            return Memo(title, content, date)
        }
    }
}