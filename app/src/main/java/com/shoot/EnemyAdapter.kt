package com.shoot

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.nearby.Nearby
import kotlinx.android.synthetic.main.enemy.view.*


class EnemyAdapter(var enemies : ArrayList<EnemyInformation>, var playFragment: PlayFragment) : RecyclerView.Adapter<EnemyAdapter.EnemyViewHolder>() {

    class EnemyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var name : Button
        init {
            name = itemView.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnemyViewHolder {
        if (viewType == 0) return EnemyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.header, parent, false))
        else return EnemyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.enemy, parent, false))
    }

    override fun getItemCount() = enemies.size

    override fun onBindViewHolder(holder: EnemyViewHolder, position: Int) {
        val enemyInformation = enemies.get(position)
        holder.name.text = enemyInformation.name
        if (!enemyInformation.isTitle()) holder.itemView.name.setOnClickListener {
            playFragment.connect(enemyInformation.endPointID)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val enemyInformation = enemies.get(position)
        if (enemyInformation.isTitle()) return 0
        else return 1
    }
}