package pt.isec.amov.a2018020733.trabalhopratico1

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.isec.amov.a2018020733.trabalhopratico1.databinding.SingleplayerBinding
import pt.isec.amov.a2018020733.trabalhopratico1.databinding.Top5Binding
import pt.isec.amov.a2018020733.trabalhopratico1.models.COLLECTION_FIELD_POINTS
import pt.isec.amov.a2018020733.trabalhopratico1.models.COLLECTION_FIELD_TIME_PLAYED
import pt.isec.amov.a2018020733.trabalhopratico1.models.COLLECTION_PATH

class Top5Activity : AppCompatActivity() {

    private lateinit var binding: Top5Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = Top5Binding.inflate(layoutInflater)
        setContentView(binding.root)

        initialize()
    }

    private fun initialize() {
        val db = Firebase.firestore
        val allScores = ArrayList<Pair<String, Pair<Int, Int>>>()

        db.collection(COLLECTION_PATH)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    allScores.add(
                        Pair(
                            document.id,
                            Pair(
                                document.data[COLLECTION_FIELD_POINTS].toString().toInt(),
                                document.data[COLLECTION_FIELD_TIME_PLAYED].toString().toInt()
                            )
                        )
                    )
                    Log.d("TAG", "${document.id} => ${document.data}")
                }
                val sortedList = allScores.sortedByDescending { it.second.first }

                if (sortedList.size > 0)
                    binding.tvTop1.text =
                        getString(
                            R.string.user
                        ) + sortedList[0].first + "\n " + getString(
                            R.string.points
                        ) + " " + sortedList[0].second.first + " " + getString(
                            R.string.time_played
                        ) + " " + sortedList[0].second.second

                if (sortedList.size > 1)
                    binding.tvTop2.text =
                        getString(
                            R.string.user
                        ) + sortedList[1].first + "\n " + getString(
                            R.string.points
                        ) + " " + sortedList[1].second.first + " " + getString(
                            R.string.time_played
                        ) + " " + sortedList[1].second.second

                if (sortedList.size > 2)
                    binding.tvTop3.text =
                        getString(
                            R.string.user
                        ) + sortedList[2].first + "\n " + getString(
                            R.string.points
                        ) + " " + sortedList[2].second.first + " " + getString(
                            R.string.time_played
                        ) + " " + sortedList[2].second.second

                if (sortedList.size > 3)
                    binding.tvTop4.text =
                        getString(
                            R.string.user
                        ) + sortedList[3].first + "\n " + getString(
                            R.string.points
                        ) + " " + sortedList[3].second.first + " " + getString(
                            R.string.time_played
                        ) + " " + sortedList[3].second.second

                if (sortedList.size > 4)
                    binding.tvTop5.text =
                        getString(
                            R.string.user
                        ) + sortedList[4].first + "\n " + getString(
                            R.string.points
                        ) + " " + sortedList[4].second.first + " " + getString(
                            R.string.time_played
                        ) + " " + sortedList[4].second.second

            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }
}