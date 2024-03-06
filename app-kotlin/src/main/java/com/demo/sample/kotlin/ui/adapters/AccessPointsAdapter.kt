package com.demo.sample.kotlin.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brivo.sdk.BrivoLog
import com.brivo.sdk.BrivoSDKInitializationException
import com.brivo.sdk.access.BrivoSDKAccess
import com.brivo.sdk.enums.AccessPointCommunicationState
import com.brivo.sdk.enums.DoorType
import com.brivo.sdk.interfaces.IOnCommunicateWithAccessPointListener
import com.brivo.sdk.model.BrivoResult
import com.brivo.sdk.onair.model.BrivoAccessPoint
import com.demo.sample.kotlin.R
import com.demo.sample.kotlin.ui.adapters.AccessPointsAdapter.SiteViewHolder

class AccessPointsAdapter(
    private val context: Context,
    private val passId: String,
    private val mDataset: ArrayList<BrivoAccessPoint>,
    private val onItemClickListener: AdapterItemClickListener
) : RecyclerView.Adapter<SiteViewHolder>() {

    inner class SiteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvSiteName: TextView
        var tvStatus: TextView
        var ivDoorType: ImageView
        var ivUnlock: ImageView

        init {
            view.setOnClickListener { view1: View ->
                onItemClickListener.onItemClick(
                    view1,
                    adapterPosition
                )
            }
            tvSiteName = view.findViewById(R.id.tv_siteName)
            tvStatus = view.findViewById(R.id.tvStatus)
            ivDoorType = view.findViewById(R.id.iv_doorType)
            ivUnlock = view.findViewById(R.id.ivUnlock)
            ivUnlock.setOnClickListener {
                ivUnlock.setImageResource(R.drawable.lock)
                tvStatus.text = context.getString(R.string.brivo_sample_door_unlocking)
                val accessPoint = mDataset[adapterPosition]
                try {
                    BrivoSDKAccess.getInstance()
                        .unlockAccessPoint(passId, accessPoint.id, null, object :
                            IOnCommunicateWithAccessPointListener {
                            override fun onResult(result: BrivoResult) {
                                when (result.communicationState) {
                                    AccessPointCommunicationState.SUCCESS -> {
                                        ivUnlock.setImageResource(R.drawable.lock_open)
                                        tvStatus.text =
                                            context.getString(R.string.brivo_sample_door_unlocked)
                                    }
                                    AccessPointCommunicationState.FAILED -> {
                                        tvStatus.text =
                                            context.getString(R.string.brivo_sample_door_unlocked_failed)
                                    }
                                    else -> {}
                                }
                            }
                        })
                } catch (e: BrivoSDKInitializationException) {
                    e.printStackTrace()
                    tvStatus.text = context.getString(R.string.brivo_sample_door_unlocked_failed)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiteViewHolder {
        val rlSiteItemContainer: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_access_point, parent, false)
        return SiteViewHolder(rlSiteItemContainer)
    }

    override fun onBindViewHolder(holder: SiteViewHolder, position: Int) {
        val brivoAccessPoint = mDataset[position]
        holder.ivUnlock.visibility = View.INVISIBLE
        holder.tvStatus.visibility = View.INVISIBLE
        holder.tvSiteName.text = brivoAccessPoint.name

        when (brivoAccessPoint.doorType) {
            DoorType.INTERNET -> {
                holder.ivDoorType.setImageResource(R.drawable.ic_net)
                holder.ivUnlock.visibility = View.VISIBLE
                holder.tvStatus.visibility = View.VISIBLE
            }
            DoorType.WAVELYNX -> {
                holder.ivDoorType.setImageResource(R.drawable.ic_brivo)
            }
            DoorType.ALLEGION -> {
                holder.ivDoorType.setImageResource(R.drawable.ic_engage)
            }
            DoorType.UNKNOWN -> {
                BrivoLog.e("Invalid door")
            }
        }
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    interface AdapterItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}