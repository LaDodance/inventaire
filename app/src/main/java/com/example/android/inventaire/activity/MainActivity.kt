package com.example.android.inventaire.activity

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.example.android.inventaire.R
import com.example.android.inventaire.dbHelper.DbManager
import com.example.android.inventaire.entity.Product
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.product_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var listProduct=ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Affichage de la date actuelle

        val sdf = SimpleDateFormat("dd/MM/yy")
        val currentDate = sdf.format(Date())

        tInventoryDate.text = currentDate.toString()

        bAddProduit.setOnClickListener{

            var intent=  Intent(this,AddProduct::class.java)
            startActivity(intent)
        }

        bSave.setOnClickListener{
            saveProductAndInventory()
        }

        bHistory.setOnClickListener{
            var intent = Intent( this, History::class.java)
            startActivity(intent)
        }






            //Load from DB
        LoadQuery("init")
    }
    override  fun onResume() {
        super.onResume()
        LoadQuery("init")
    }

    fun LoadQuery(name:String){



        var dbManager= DbManager(this)
        val projections= arrayOf("ID","name","price","quantity", "inventory_id", "sousTotal")
        val selectionArgs= arrayOf(name)
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
        listViewProduct.adapter=myProductAdapter


    }

    inner class  MyProductAdapter: BaseAdapter {
        var listProductsAdpater=ArrayList<Product>()
        var context: Context?=null
        constructor(context: Context, listProductsAdpater:ArrayList<Product>):super(){
            this.listProductsAdpater=listProductsAdpater
            this.context=context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

            var myView=layoutInflater.inflate(R.layout.product_item,null)
            var dbManager=DbManager(this.context!!)
            var myProduct=listProductsAdpater[p0]
            myView.tProductName.text=myProduct.productName.toString()
            myView.tPrice.text=myProduct.productPrice.toString()
            myView.tQuantity.text=myProduct.productQuantity.toString()


            myView.bDelete.setOnClickListener{
                val selectionArgs= arrayOf(myProduct.productID.toString())
                dbManager.DeleteProduit("ID=?",selectionArgs)
                LoadQuery("init")
            }

            myView.bMod.setOnClickListener{
                GoToUpdateProduct(myProduct)
            }
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

    fun GoToUpdateProduct(product:Product){
        var intent=  Intent(this,AddProduct::class.java)
        intent.putExtra("id",product.productID)
        intent.putExtra("name",product.productName)
        intent.putExtra("price",product.productPrice)
        intent.putExtra("quantity", product.productQuantity)
        startActivity(intent)
    }

    fun GoToUpdateInventory(id: Int?) {
        var intent=  Intent(this,SaveInventory::class.java)
        intent.putExtra("inventory_id",id)


        startActivity(intent)
    }

    // longue méthode sa mère
    fun saveProductAndInventory(){
        val mydbManager = DbManager(this)
        var valuesInventory = ContentValues()
        var valuesProduct = ContentValues()
        var inventory_id : Int? = null
        var totalDesSousTotaux : Double = 0.0

        // Création d'un nouveau inventaire, init car can't be null
        valuesInventory.put("inventory_date", tInventoryDate.text.toString())
        valuesInventory.put("inventory_proprio", "init")
        valuesInventory.put("inventory_rep_proprio", "init")
        valuesInventory.put("inventory_locator", "init")
        valuesInventory.put("inventory_rep_locator", "init")
        valuesInventory.put("inventory_state", "init")
        valuesInventory.put("inventory_total", totalDesSousTotaux.toString())
        valuesInventory.put("inventory_contreAFaire", "true")

        mydbManager.InsertInventory(valuesInventory)
        Toast.makeText(this, "nouveau inventaire créé", Toast.LENGTH_SHORT).show()


        // Ressort l'id de la date d'aujourd'hui, prend seulement le dernier
        val projections= arrayOf("inventory_id")
        val selectionArgs= arrayOf(tInventoryDate.text.toString())
        val cursor=mydbManager.QueryInventory(projections,"inventory_date like ?",selectionArgs,"inventory_date")
        if(cursor.moveToLast()){

            do{

                val inventoryId = cursor.getInt(cursor.getColumnIndex("inventory_id"))

                inventory_id = inventoryId

            }while (cursor.moveToNext())
        }


        // Création des produits avec le même inventory_id que l'inventaire créé
        val projections2= arrayOf("ID","name","price","quantity", "inventory_id", "sousTotal")
        val selectionArgs2= arrayOf("init")
        val cursor2=mydbManager.QueryProduct(projections2,"inventory_id like ?",selectionArgs2,"name")
        listProduct.clear()
        if(cursor2.moveToFirst()){

            do{
              //  val id=cursor2.getInt(cursor2.getColumnIndex("ID"))
                val name=cursor2.getString(cursor2.getColumnIndex("name"))
                val price=cursor2.getString(cursor2.getColumnIndex("price"))
                val quantity=cursor2.getString(cursor2.getColumnIndex("quantity"))
                val sousTotal = cursor2.getString(cursor2.getColumnIndex("sousTotal"))

               // valuesProduct.put("ID", id)
                valuesProduct.put("name", name)
                valuesProduct.put("price", price)
                valuesProduct.put("quantity", quantity)
                valuesProduct.put("sousTotal", sousTotal)
                valuesProduct.put("inventory_id", inventory_id)

                totalDesSousTotaux += sousTotal.toString().toDouble()

                mydbManager.InsertProduct(valuesProduct)
               // Toast.makeText(this, " insertion produit", Toast.LENGTH_SHORT).show()

            }while (cursor2.moveToNext())
        }

        var totalValue = ContentValues()
       totalDesSousTotaux = Math.round(totalDesSousTotaux*100.0)/100.0
        totalValue.put("inventory_total", totalDesSousTotaux.toString())
        var selectionArgs3 = arrayOf(inventory_id.toString())
        mydbManager.UpdateInventory(totalValue,"inventory_id=?", selectionArgs3)
        GoToUpdateInventory(inventory_id)

    }

}
