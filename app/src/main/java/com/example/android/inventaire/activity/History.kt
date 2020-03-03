package com.example.android.inventaire.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.android.inventaire.R
import com.example.android.inventaire.dbHelper.DbManager
import com.example.android.inventaire.entity.Inventory
import kotlinx.android.synthetic.main.activity_history.*

import kotlinx.android.synthetic.main.history_item.view.*

class History : AppCompatActivity() {

    var listInventory=ArrayList<Inventory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        bHome.setOnClickListener{

            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        LoadQuery("%")
    }

    override fun onResume() {
        super.onResume()
        LoadQuery("%")
    }


    fun LoadQuery(date:String){
        var dbManager= DbManager(this)
        val projections= arrayOf("inventory_id",
            "inventory_date",
            "inventory_proprio",
            "inventory_rep_proprio",
            "inventory_locator",
            "inventory_rep_locator",
            "inventory_state",
            "inventory_total",
            "inventory_contreAFaire")
        val selectionArgs= arrayOf(date)
        val cursor=dbManager.QueryInventory(projections,"inventory_date like ?",selectionArgs,"inventory_id DESC")
        listInventory.clear()
        if(cursor.moveToFirst()){

            do{
                val inventory_id = cursor.getInt(cursor.getColumnIndex("inventory_id"))
                val inventory_date = cursor.getString(cursor.getColumnIndex("inventory_date"))
                val inventory_proprio = cursor.getString(cursor.getColumnIndex("inventory_proprio"))
                val inventory_rep_proprio = cursor.getString(cursor.getColumnIndex("inventory_rep_proprio"))
                val inventory_locator = cursor.getString(cursor.getColumnIndex("inventory_locator"))
                val inventory_rep_locator = cursor.getString(cursor.getColumnIndex("inventory_rep_locator"))
                val inventoryState = cursor.getString(cursor.getColumnIndex("inventory_state"))
                val inventoryTotal = cursor.getString(cursor.getColumnIndex("inventory_total"))
                val inventoryContreAfaire = cursor.getString(cursor.getColumnIndex("inventory_contreAFaire"))

                listInventory.add(Inventory(inventory_id,inventory_date,inventory_proprio,inventory_rep_proprio,inventory_locator,inventory_rep_locator,inventoryState,inventoryTotal, inventoryContreAfaire))


            }while (cursor.moveToNext())
        }

        var myInventoryAdapter = MyInventoryAdapter(this, listInventory)
        listViewHistory.adapter=myInventoryAdapter


    }

    inner class  MyInventoryAdapter: BaseAdapter {
        var listInventoriesAdpater=ArrayList<Inventory>()
        var context: Context?=null
        constructor(context: Context, listInventoriesAdpater:ArrayList<Inventory>):super(){
            this.listInventoriesAdpater=listInventoriesAdpater
            this.context=context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

            var myView=layoutInflater.inflate(R.layout.history_item,null)

            var myInventory=listInventoriesAdpater[p0]
            myView.tDateInventoryHisto.text = myInventory.colDateInventory.toString()
            myView.tLocataire.text=myInventory.colLocator.toString()
            myView.tState.text=myInventory.colStateInventory.toString()


            myView.linearItem.setOnClickListener {


                openInventoryActivity(myInventory)
            }



            return myView
        }




        override fun getItem(p0: Int): Any {
            return listInventoriesAdpater[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {

            return listInventoriesAdpater.size

        }
    }



    fun openInventoryActivity(inventory: Inventory){
        var intent = Intent(this,InventoryActivity::class.java)
        intent.putExtra("inventory_id",inventory.colIdInventory)
        intent.putExtra("inventory_date",inventory.colDateInventory)
        intent.putExtra("inventory_proprio",inventory.colProprio)
        intent.putExtra("inventory_rep_proprio",inventory.colRepProprio)
        intent.putExtra("inventory_locator",inventory.colLocator)
        intent.putExtra("inventory_rep_locator",inventory.colRepLocator)
        intent.putExtra("inventory_state",inventory.colStateInventory)
        intent.putExtra("inventory_total",inventory.colTotalInventory)
        intent.putExtra("inventory_contreAFaire", inventory.colContreAfaire)

        startActivity(intent)

    }
}
