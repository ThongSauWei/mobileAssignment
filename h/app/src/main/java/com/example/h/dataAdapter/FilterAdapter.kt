import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.h.R

class FilterAdapter(private val list: List<String>) : RecyclerView.Adapter<FilterAdapter.FilterHolder>() {

    private var selectedItem: String? = null

    class FilterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btn: Button = itemView.findViewById(R.id.btnFilterFilterHolder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.filter_holder, parent, false)
        return FilterHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FilterHolder, position: Int) {
        val currentItem = list[position]
        holder.btn.text = currentItem

        // Set background color based on selection
        if (currentItem == selectedItem) {
            holder.btn.setBackgroundColor(Color.parseColor("#FF9C26"))
        }


        // Set click listener for each item
        holder.btn.setOnClickListener {
            // Set the selected item
            selectedItem = currentItem
            notifyDataSetChanged() // Notify adapter of data change
        }
    }

    // Method to get the selected value
    fun getSelectedValue(): String? {
        return selectedItem
    }
}
