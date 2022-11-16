package com.vimdevs.nannyapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DiapersAdapterRv(val context: Context, val diapersList: ArrayList<DiapersDataModule>) :
    RecyclerView.Adapter<DiapersAdapterRv.Holder>() {

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(diapersList[position], context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.diaper_item_rv, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return diapersList.size
    }

    inner class Holder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val fecha = view?.findViewById<TextView>(R.id.fechaDiaper)
        val tipoDiaper = view?.findViewById<TextView>(R.id.tipoDiaper)
        val colorDiaper = view?.findViewById<TextView>(R.id.colorDiaper)

        fun bind(diaper: DiapersDataModule, context: Context) {
            fecha?.text = diaper.date
            tipoDiaper?.text = diaper.type
            colorDiaper?.text = diaper.color
        }
    }
}