package com.vimdevs.nannyapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodsAdapterRv(val context: Context, val foodsList: ArrayList<FoodsDataModule>) :
    RecyclerView.Adapter<FoodsAdapterRv.Holder>() {

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(foodsList[position], context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.food_item_rv, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return foodsList.size
    }

    inner class Holder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val fecha = view?.findViewById<TextView>(R.id.fechaFoodItem)
        val tipoLeche = view?.findViewById<TextView>(R.id.milkFoodItem)
        val cantidadLeche = view?.findViewById<TextView>(R.id.quantityFoodItem)

        fun bind(food: FoodsDataModule, context: Context) {
            fecha?.text = food.date
            tipoLeche?.text = food.milkType
            cantidadLeche?.text = food.quantity
        }
    }
}