import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.h.Home
import com.example.h.MainActivity
import com.example.h.R

class FilterPost : Fragment() {

    //private val filterViewModel: FilterViewModel by activityViewModels()

    var selectedCategory: String? = null
    var selectedLearningStyle: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_filter_post, container, false)

        (activity as MainActivity).setToolbar(R.layout.toolbar_with_profile)

        val recyclerViewCategory: RecyclerView = view.findViewById(R.id.recyclerViewCategoryFilterPost)
        val recyclerViewLearningStyle: RecyclerView = view.findViewById(R.id.recyclerViewLearningStyleFilterPost)

        // Set adapters for RecyclerViews
        recyclerViewCategory.adapter = FilterAdapter(resources.getStringArray(R.array.category).toList())
        recyclerViewLearningStyle.adapter = FilterAdapter(resources.getStringArray(R.array.learning_style).toList())

        val btnFilter: AppCompatButton = view.findViewById(R.id.btnFilterFilterPost)
        btnFilter.setOnClickListener {
            // Get selected category
            val selectedCategory = (recyclerViewCategory.adapter as? FilterAdapter)?.getSelectedValue()
            // Get selected learning style
            val selectedLearningStyle = (recyclerViewLearningStyle.adapter as? FilterAdapter)?.getSelectedValue()

            // Retrieve existing arguments bundle
//            val args = arguments ?: Bundle()
//
//            // Update the existing arguments bundle
//            args.putString("selectedCategory", selectedCategory)
//            args.putString("selectedLearningStyle", selectedLearningStyle)
//
//            // Set the updated arguments bundle
//            arguments = args
//
//            // Navigate back to Home fragment
//            activity?.onBackPressed()


            val fragment = Home()
            val bundle = Bundle()
            if (selectedCategory != null) {
                bundle.putString("selectedCategory", selectedCategory)
            }
            if (selectedLearningStyle != null) {
                bundle.putString("selectedLearningStyle", selectedLearningStyle)
            }

            fragment.arguments = bundle
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }


        val btnExitFilterPost = view.findViewById<ImageView>(R.id.btnExitFilterPost)
        btnExitFilterPost.setOnClickListener {
            activity?.onBackPressed()
        }

        return view
    }
}
