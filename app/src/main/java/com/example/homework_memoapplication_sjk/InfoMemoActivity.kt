package com.example.homework_memoapplication_sjk

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.homework_memoapplication_sjk.databinding.InfoMemoActivityBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class InfoMemoActivity: AppCompatActivity() {

    private lateinit var binding: InfoMemoActivityBinding

    // 현재 메모
    private lateinit var memo: Memo

    // 보기 모드, 수정 모드
    private var mode: InfoMode = InfoMode.INFO

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = InfoMemoActivityBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initCurrentMemo()
        initToolbar()
        initView()
        initBackPressDispatcher()
    }

    // 홈 화면에서 memo 를 받아온다.
    private fun initCurrentMemo() {
        val memo: Memo? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra("memo", Memo::class.java)
        else
            intent.getParcelableExtra("memo")

        memo?.let {
            this.memo = memo
        } ?: Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_LONG).show()

        if (!this::memo.isInitialized)
            finish()
    }

    // Toolbar 를 설정한다.
    private fun initToolbar() {
        binding.apply {
            with(infoMemoToolbar) {
                // 뒤로가기 버튼을 눌렀을 시 설정
                setNavigationOnClickListener {
                    when (mode) {
                        InfoMode.INFO -> {
                            showApplyMemoDialog()
                        }
                        InfoMode.EDIT -> {
                            setMemoEditable(false)
                            applyMemo()
                        }
                    }
                }

                // 메뉴를 눌렀을 시 설정
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        // 수정 메뉴
                        R.id.info_memo_edit -> {
                            setMemoEditable(true)
                            return@setOnMenuItemClickListener true
                        }

                        // 삭제 버튼
                        R.id.info_memo_delete -> {
                            showDeleteDialog()
                            return@setOnMenuItemClickListener true
                        }

                        // 수정 완료 버튼
                        R.id.edit_memo_done -> {
                            setMemoEditable(false)
                            applyMemo()
                            return@setOnMenuItemClickListener true
                        }
                        else -> return@setOnMenuItemClickListener false
                    }
                }
            }
        }
    }

    // 메모 내용을 View에 적용한다.
    private fun initView() {
        binding.apply {
            infoMemoTitleInputEdit.setText(memo.title)
            infoMemoDateInputEdit.setText(memo.date)
            infoMemoContentInputEdit.setText(memo.content)
        }
    }

    // 메모를 수정 (불)가능하게 만든다.
    private fun setMemoEditable(isEditable: Boolean) {
        binding.apply {
            infoMemoTitleInputEdit.isEnabled = isEditable
            infoMemoContentInputEdit.isEnabled = isEditable

            infoMemoDateInput.isVisible = !isEditable

            if (isEditable) {
                mode = InfoMode.EDIT
                infoMemoToolbar.menu.clear()
                infoMemoToolbar.inflateMenu(R.menu.edit_memo_menu)
                Util.showSoftKey(infoMemoTitleInputEdit, this@InfoMemoActivity)
            } else {
                mode = InfoMode.INFO
                infoMemoToolbar.menu.clear()
                infoMemoToolbar.inflateMenu(R.menu.info_memo_menu)
                Util.hideSoftInput(this@InfoMemoActivity)
            }
        }
    }

    // 수정한 메모를 적용한다.
    private fun applyMemo() {
        binding.apply {
            val title = infoMemoTitleInputEdit.text.toString()
            val content = infoMemoContentInputEdit.text.toString()

            memo = memo.copy(title = title, content = content)
        }
    }

    // 메모 삭제 다이얼로그를 띄운다.
    private fun showDeleteDialog() {
        val dialog = MaterialAlertDialogBuilder(this).apply {
            title = "메모 삭제"
            setMessage("${memo.title}\n메모를 삭제합니다.")

            setPositiveButton("삭제") { _, _ ->
                intent.putExtra("deleted", true)
                setResult(RESULT_OK, intent)
                finish()
            }

            setNegativeButton("취소") { _, _ ->
                intent.putExtra("deleted", false)
            }
        }
        dialog.show()
    }

    // 수정한 메모가 원본과 다르면
    // 실제로 적용할 것인지 묻는 다이얼로그를 띄운다.
    private fun showApplyMemoDialog() {
        val originalMemo: Memo? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra("memo", Memo::class.java)
        else
            intent.getParcelableExtra("memo")

        originalMemo?.let { before ->
            if (before != memo) {
                val dialog = MaterialAlertDialogBuilder(this).apply {
                    title = "메모 적용"
                    setMessage("메모를 적용하고 홈 화면으로 돌아갑니다.")
                    setPositiveButton("확인") { _, _ ->
                        intent.putExtra("memo", memo)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    setNegativeButton("취소") { d, _ ->
                        d.dismiss()
                    }
                }
                dialog.show()
            } else {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }

    // 뒤로가기 버튼도 Toolbar의 뒤로가기 액션과 동일하게 만든다.
    private fun initBackPressDispatcher() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showApplyMemoDialog()
            }
        })
    }

}

enum class InfoMode {
    INFO, EDIT
}