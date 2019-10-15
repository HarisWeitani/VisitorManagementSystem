package com.hw.vms.visitormanagementsystem.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.hw.vms.visitormanagementsystem.DataSet.DataHost

class HostAdapter(internal var context: Context,internal var layoutResource : Int,internal var hostList : List<DataHost> ) :
    ArrayAdapter<DataHost>(context,layoutResource,hostList) {

    var resList = ArrayList<DataHost>(hostList)
    var suggestions = ArrayList<DataHost>()

    override fun getCount(): Int = hostList.size

    override fun getItem(position: Int): DataHost? = hostList[position]

    override fun getItemId(position: Int): Long = hostList[position].host_id!!.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context).inflate(layoutResource, parent, false) as TextView
        view.text = "${hostList[position].host_name}"
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as DataHost).host_name.toString()
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: Filter.FilterResults) {
                clear()
                addAll(filterResults.values as List<DataHost>)
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
                val filterResults = FilterResults()
                val suggest = ArrayList<DataHost>()

                if(constraint == null || constraint.isEmpty() ){
                    suggest.addAll(resList)
                }else{
                    val filterPattern = constraint.toString().toLowerCase().trim()
                    for (item in resList){
                        if(item.host_name!!.toLowerCase().contains(filterPattern)){
                            suggest.add(item)
                        }
                    }
                }

                filterResults.values = suggest
                filterResults.count = suggest.size

                return filterResults
            }

           /* override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
                if (constraint != null) {
                    suggestions.clear()
                    for (people in resList) {
                        if (people.host_name!!.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            suggestions.add(people)
                        }
                    }
                    val filterResults = Filter.FilterResults()
                    filterResults.values = suggestions
                    filterResults.count = suggestions.size
                    return filterResults
                } else {
                    return Filter.FilterResults()
                }
            }

            override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
                val filterList = results.values as ArrayList<DataHost>
                if (results != null && results.count > 0) {
                    clear()
                    for (people in filterList) {
                        add(people)
                        notifyDataSetChanged()
                    }
                }
            }*/
        }
    }
/*
    var nameFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence {
            return (resultValue as People).getName()
        }

        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            if (constraint != null) {
                suggestions.clear()
                for (people in tempItems) {
                    if (people.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(people)
                    }
                }
                val filterResults = Filter.FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size()
                return filterResults
            } else {
                return Filter.FilterResults()
            }
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            val filterList = results.values as ArrayList<People>
            if (results != null && results.count > 0) {
                clear()
                for (people in filterList) {
                    add(people)
                    notifyDataSetChanged()
                }
            }
        }
    }*/
}