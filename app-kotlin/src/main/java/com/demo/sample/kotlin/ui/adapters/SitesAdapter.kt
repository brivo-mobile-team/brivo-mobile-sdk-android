package com.demo.sample.kotlin.ui.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import com.brivo.sdk.onair.model.BrivoOnairPass
import com.brivo.sdk.onair.model.BrivoSite

import com.demo.sample.kotlin.R

import java.util.LinkedHashMap

class SitesAdapter(
    private val context: Context,
    private val listDataGroup: List<String>,
    private val listDataChild: LinkedHashMap<String, BrivoOnairPass>
) : BaseExpandableListAdapter() {

    override fun getChild(groupPosition: Int, childPosititon: Int): Any {
        return listDataChild[listDataGroup[groupPosition]]!!.sites[childPosititon]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int) = childPosition.toLong()

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val site = getChild(groupPosition, childPosition) as BrivoSite
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_site, parent, false)

        val textViewChild: TextView = view.findViewById(R.id.tv_siteName)
        textViewChild.text = site.siteName

        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return listDataChild[listDataGroup[groupPosition]]!!.sites.size
    }

    override fun getGroup(groupPosition: Int) = listDataGroup[groupPosition]

    override fun getGroupCount() = listDataGroup.size

    override fun getGroupId(groupPosition: Int) = groupPosition.toLong()

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val headerTitle = getGroup(groupPosition)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_site, parent, false)

        view.findViewById<TextView>(R.id.tv_siteName).apply {
            setTypeface(null, Typeface.BOLD)
            text = headerTitle
        }

        (parent as ExpandableListView).expandGroup(groupPosition)
        return view
    }

    override fun hasStableIds() = false

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true
}