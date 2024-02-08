package com.example.homework_memoapplication_sjk

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework_memoapplication_sjk.databinding.ActivityMainBinding
import com.example.homework_memoapplication_sjk.databinding.MemoRawBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var addMemoContract: ActivityResultContracts.StartActivityForResult
    private lateinit var addMemoLauncher: ActivityResultLauncher<Intent>

    private val memoList: MutableList<Memo> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAddMemoContract()
        initToolbar()
        initRecyclerView()
    }

    // 메모 추가 Activity Launcher 를 설정한다.
    private fun initAddMemoContract() {
        addMemoContract = ActivityResultContracts.StartActivityForResult()
        addMemoLauncher = registerForActivityResult(addMemoContract) {
            if (it.resultCode == RESULT_OK) {
                val memo: Memo? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    it.data?.getParcelableExtra("memo", Memo::class.java)
                else
                    it.data?.getParcelableExtra("memo")

                addMemo(memo)
            }
        }
    }

    // Toolbar 를 설정한다.
    private fun initToolbar() {
        binding.apply {
            homeToolbar.setOnMenuItemClickListener {
                if (it.itemId == R.id.main_menu_add) {
                    val addMemoIntent = Intent(this@MainActivity, AddMemoActivity::class.java)
                    addMemoLauncher.launch(addMemoIntent)
                }
                return@setOnMenuItemClickListener false
            }
        }
    }

    // RecyclerView 를 설정한다.
    private fun initRecyclerView() {
        binding.apply {
            with(homeMemoList) {
                adapter = MemoRecyclerAdapter()
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }
    }

    // 메모를 추가하고 RecyclerView 갱신
    private fun addMemo(memo: Memo?) {
        if (memo == null)
            return

        memoList.add(memo)
        binding.homeMemoList.adapter?.notifyDataSetChanged()
    }

    inner class MemoRecyclerAdapter: RecyclerView.Adapter<MemoRecyclerAdapter.MemoViewHolder>() {
        inner class MemoViewHolder(val binding: MemoRawBinding): RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
            val memoBinding = MemoRawBinding.inflate(layoutInflater)
            return MemoViewHolder(memoBinding)
        }

        override fun getItemCount(): Int {
            return memoList.size
        }

        override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
            holder.binding.apply {
                memoRawTitle.text = memoList[position].title
                memoRawDate.text = memoList[position].date
            }
        }
    }
}