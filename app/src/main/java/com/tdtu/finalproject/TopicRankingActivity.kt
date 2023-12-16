package com.tdtu.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tdtu.finalproject.adapter.RankingAdapter
import com.tdtu.finalproject.databinding.ActivityTopicRankingBinding
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.Utils
import com.tdtu.finalproject.viewmodel.RankingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class TopicRankingActivity : BaseActivity() {
    private lateinit var binding: ActivityTopicRankingBinding
    private lateinit var dataRepository: DataRepository
    private lateinit var rankingViewModel: RankingViewModel
    private lateinit var topic: Topic
    private lateinit var rankingAdapter: RankingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicRankingBinding.inflate(layoutInflater)
        rankingViewModel = ViewModelProvider(this)[RankingViewModel::class.java]
        dataRepository = DataRepository.getInstance()
        topic = intent.getParcelableExtra("topic")!!
        getTopicRanking()
        binding.returnBtn.setOnClickListener {
            finish()
        }
        rankingViewModel.getStatistics().observe(this){
            rankingAdapter = RankingAdapter(this, it, R.layout.ranking_item_layout)
            binding.topicRankingRecyclerView.setHasFixedSize(true)
            binding.topicRankingRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)
            binding.topicRankingRecyclerView.adapter = rankingAdapter
        }

        binding.criteriaBtn.setOnClickListener {
            val popupMenu = PopupMenu(this, binding.criteriaBtn)
            popupMenu.menuInflater.inflate(R.menu.sorting_criteria_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.sortByLearningCount -> {
                        rankingViewModel.sortByLearningCount()
                        true
                    }
                    R.id.sortByProgress -> {
                        rankingViewModel.sortByProgress()
                        true
                    }
                    R.id.sortByTimeLearned ->{
                        rankingViewModel.sortByTimeLearned()
                        true
                    }
                    R.id.sortByVocabularyLearned ->{
                        rankingViewModel.sortByVocabularyLearned()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        setContentView(binding.root)
    }
    private fun getTopicRanking() {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            val job = async {
                dataRepository.getLearningStatisticsForTopic(topic.id!!).thenAcceptAsync {
                    if(it.isEmpty()){
                        runOnUiThread {
                            binding.emptyRankingLayout.visibility = android.view.View.VISIBLE
                            binding.topicRankingRecyclerView.visibility = android.view.View.GONE
                        }
                    }
                    else{
                        runOnUiThread {
                            binding.emptyRankingLayout.visibility = android.view.View.GONE
                            binding.topicRankingRecyclerView.visibility = android.view.View.VISIBLE
                            rankingViewModel.setStatisticsList(it)
                            rankingViewModel.setOriginalStatisticList(it)
                        }
                    }
                }.exceptionally {
                    runOnUiThread {
                        Utils.showDialog(Gravity.CENTER, it.message!!.toString(), this@TopicRankingActivity)
                    }
                    null
                }
            }
            job.await()
        }.start()
    }
}