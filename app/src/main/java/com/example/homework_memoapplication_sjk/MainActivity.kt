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

    // 메모 추가 Activity 관련 프로퍼티
    private lateinit var addMemoContract: ActivityResultContracts.StartActivityForResult
    private lateinit var addMemoLauncher: ActivityResultLauncher<Intent>

    // 메모 보기 및 숮어 Activity 관련 프로퍼티
    private lateinit var infoMemoContract: ActivityResultContracts.StartActivityForResult
    private lateinit var infoMemoLauncher: ActivityResultLauncher<Intent>

    // 실제 보여질 메모 List
    private val memoList: MutableList<Memo> = mutableListOf()

    // 메모 수정할 때 사용할 인덱스 번호
    private var selectedMemoPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAddMemoContract()
        initInfoMemoContract()
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

    // 메모 보기 및 수정 Activity Launcher 를 설정한다.
    private fun initInfoMemoContract() {
        infoMemoContract = ActivityResultContracts.StartActivityForResult()
        infoMemoLauncher = registerForActivityResult(infoMemoContract) {
            if (it.resultCode == RESULT_OK) {
                val isDeleted = it.data?.getBooleanExtra("deleted", false) ?: false
                if (isDeleted) {
                    memoList.removeAt(selectedMemoPosition)
                    binding.homeMemoList.adapter?.notifyItemRemoved(selectedMemoPosition)
                    return@registerForActivityResult
                }

                val modifiedMemo: Memo? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    it.data?.getParcelableExtra("memo", Memo::class.java)
                else
                    it.data?.getParcelableExtra("memo")

                modifiedMemo?.let { after ->
                    memoList[selectedMemoPosition] = after
                    binding.homeMemoList.adapter?.notifyItemChanged(selectedMemoPosition)
                }
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

    inner class MemoRecyclerAdapter : RecyclerView.Adapter<MemoRecyclerAdapter.MemoViewHolder>() {
        inner class MemoViewHolder(val binding: MemoRawBinding) :
            RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.layoutParams =
                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

                binding.memoRawCard.setOnClickListener {
                    selectedMemoPosition = adapterPosition
                    val intent = Intent(this@MainActivity, InfoMemoActivity::class.java).apply {
                        putExtra("memo", memoList[adapterPosition])
                    }
                    infoMemoLauncher.launch(intent)
                }
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