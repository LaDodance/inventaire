package com.example.android.inventaire.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.core.view.isGone
import com.example.android.inventaire.R
import com.example.android.inventaire.dbHelper.DbManager
import com.example.android.inventaire.entity.Inventory
import com.example.android.inventaire.entity.Product
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_inventory.*
import kotlinx.android.synthetic.main.activity_inventory.tInventoryDate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.history_item.*
import kotlinx.android.synthetic.main.inventory_item.view.*
import kotlinx.android.synthetic.main.product_item.view.*
import kotlinx.android.synthetic.main.product_item.view.tQuantity

class InventoryActivity : AppCompatActivity() {

    var inventory_id = 0
    var inventory_date : String? =null
    var inventory_locator : String? =null
    var inventory_proprio : String? =null
    var inventory_rep_locator  : String? =null
    var inventory_rep_proprio : String? =null
    var inventory_state : String? =null
    var inventory_total : String? =null
    var inventory_ContreAfaire : String? =null


    var listProduct = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)


        //try {
            var bundle:Bundle= intent.extras!!


            inventory_id=bundle.getInt("inventory_id",0)
            inventory_date=bundle.getString("inventory_date","")
            inventory_proprio=bundle.getString("inventory_proprio","")
            inventory_rep_proprio=bundle.getString("inventory_rep_proprio","")
            inventory_locator=bundle.getString("inventory_locator","")
            inventory_rep_locator=bundle.getString("inventory_rep_locator","")
            inventory_state=bundle.getString("inventory_state","")
            inventory_total= bundle.getString("inventory_total", "")
            inventory_ContreAfaire = bundle.getString("inventory_contreAFaire", "")



        var resume = "Inventaire de la location du local " + inventory_proprio.toString() +
                " loué par le collectif " + inventory_locator.toString() + " fait par " +
                inventory_rep_proprio.toString() + " accepté par " + inventory_rep_locator.toString()
            tInventoryDate.text = inventory_date.toString()
            tResume.text = resume
            tStateInventory.text = inventory_state.toString()
            tTotal.text = inventory_total.toString()



        // affichage du "contre inventaire à faire"
       if (inventory_ContreAfaire == "true"){
           tContreAFaire.VisibleOrGone(true)
           bContreInventaire.VisibleOrGone(true)
       }else{
           tContreAFaire.VisibleOrGone(false)
           bContreInventaire.VisibleOrGone(false)
       }


        bBackToHistory.setOnClickListener{
            finish()
        }
        bGoToHome.setOnClickListener{
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        bContreInventaire.setOnClickListener{
            openContreInventaire()
        }

        LoadQuery()
    }

    override fun onResume() {
        super.onResume()
        LoadQuery()
    }

    fun View.VisibleOrGone(visible: Boolean) {
        visibility = if(visible) View.VISIBLE else View.GONE
    }
    fun LoadQuery(){



        var dbManager= DbManager(this)
        val projections= arrayOf("ID","name","price","quantity", "inventory_id", "sousTotal")
        val selectionArgs= arrayOf(inventory_id.toString())
        val cursor=dbManager.QueryProduct(projections,"inventory_id like ?",selectionArgs,"name")
        listProduct.clear()
        if(cursor.moveToFirst()){

            do{
                val id=cursor.getInt(cursor.getColumnIndex("ID"))
                val name=cursor.getString(cursor.getColumnIndex("name"))
                val price=cursor.getString(cursor.getColumnIndex("price"))
                val quantity=cursor.getString(cursor.getColumnIndex("quantity"))
                val inventoryId = cursor.getString(cursor.getColumnIndex("inventory_id"))
                val sousTotal = cursor.getString(cursor.getColumnIndex("sousTotal"))



                listProduct.add(Product(id,name,price,quantity, inventoryId,sousTotal))

            }while (cursor.moveToNext())
        }

        var myProductAdapter= MyProductAdapter(this, listProduct)
        listViewDetail.adapter=myProductAdapter


    }

    inner class  MyProductAdapter: BaseAdapter {
        var listProductsAdpater=ArrayList<Product>()
        var context: Context?=null
        constructor(context: Context, listProductsAdpater:ArrayList<Product>):super(){
            this.listProductsAdpater=listProductsAdpater
            this.context=context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

            var myView=layoutInflater.inflate(R.layout.inventory_item,null)
            var dbManager= DbManager(this.context!!)
            var myProduct=listProductsAdpater[p0]
            myView.tNameProduct.text=myProduct.productName.toString()
            myView.tPriceProduct.text=myProduct.productPrice.toString()
            myView.tQuantityProduct.text=myProduct.productQuantity.toString()
            myView.tSousTotal.text = myProduct.sousTotal.toString()



            return myView
        }




        override fun getItem(p0: Int): Any {
            return listProductsAdpater[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {

            return listProductsAdpater.size

        }
    }

    fun openContreInventaire(){
        var intent = Intent(this,ContreInventaire::class.java)
        intent.putExtra("inventory_id",inventory_id)
        intent.putExtra("inventory_date",inventory_date)
        intent.putExtra("inventory_proprio",inventory_proprio)
        intent.putExtra("inventory_rep_proprio",inventory_rep_proprio)
        intent.putExtra("inventory_locator",inventory_locator)
        intent.putExtra("inventory_rep_locator",inventory_rep_locator)
        intent.putExtra("inventory_state",inventory_state)
        intent.putExtra("inventory_total",inventory_total)
        intent.putExtra("inventory_contreAFaire", inventory_ContreAfaire)

        startActivity(intent)

    }

}
