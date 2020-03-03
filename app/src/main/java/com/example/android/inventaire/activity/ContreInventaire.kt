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
import kotlinx.android.synthetic.main.activity_contre_inventaire.*
import kotlinx.android.synthetic.main.activity_inventory.tInventoryDate
import kotlinx.android.synthetic.main.product_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ContreInventaire : AppCompatActivity() {

        var inventory_idPartie1 = 0
        var inventory_idPartie2 = 0
        var inventory_date : String? =null
        var inventory_locator : String? =null
        var inventory_proprio : String? =null
        var inventory_rep_locator  : String? =null
        var inventory_rep_proprio : String? =null
        var inventory_state : String? =null
        var inventory_total : String? =null
        var inventory_ContreAfaireInventory1 : String? =null
        var inventory_ContreAfaireInventory2 : String? =null
        val sdf = SimpleDateFormat("dd/MM/yy")
        val currentDate = sdf.format(Date())

        var listProduct = ArrayList<Product>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_contre_inventaire)


            //try {
            var bundle:Bundle= intent.extras!!


            inventory_idPartie1=bundle.getInt("inventory_id",0)
            inventory_date=bundle.getString("inventory_date","")
            inventory_proprio=bundle.getString("inventory_proprio","")
            inventory_rep_proprio=bundle.getString("inventory_rep_proprio","")
            inventory_locator=bundle.getString("inventory_locator","")
            inventory_rep_locator=bundle.getString("inventory_rep_locator","")
            inventory_state=bundle.getString("inventory_state","")
            inventory_total= bundle.getString("inventory_total", "")
            inventory_ContreAfaireInventory1 = bundle.getString("inventory_contreAFaire", "")

            tContreInventaireDate.text = currentDate.toString()
            tCollectifLocataire.text = inventory_locator

            // init new inventaire
            InitContreInventaire()






            bBackToInventory.setOnClickListener{
                deleteInventoryProduct()
            }
            bSaveContreInventaire.setOnClickListener{
                saveProductAndInventory()
            }



            LoadQuery()


        }

    // important pour refresh après la modification de product
    override fun onResume() {
        super.onResume()
        LoadQuery()
    }


       fun InitContreInventaire() {

         // Création d'un nouvel inventoryId, copie des products possédant l'inventoryIdPartie1 dans l'inventoryIdPartie2

        var valuesInit = ContentValues()
        var mydbManager = DbManager(this)
        valuesInit.put("inventory_date", currentDate.toString())
        valuesInit.put("inventory_proprio", inventory_proprio)
        valuesInit.put("inventory_rep_proprio", "init")
        valuesInit.put("inventory_locator", inventory_locator)
        valuesInit.put("inventory_rep_locator", "init")
        valuesInit.put("inventory_state", "Partie 2")
        valuesInit.put("inventory_total", "init")
        valuesInit.put("inventory_contreAFaire", "false")

        // récupère l'id pour l'inventaire Partie 2

        mydbManager.InsertInventory(valuesInit)
        // Ressort l'id de la date d'aujourd'hui, prend seulement le dernier
        val projections= arrayOf("inventory_id","inventory_state","inventory_contreAFaire")
        val selectionArgs= arrayOf(currentDate.toString())
        val cursor=mydbManager.QueryInventory(projections,"inventory_date like ?",selectionArgs,"inventory_date")
        if(cursor.moveToLast()){

           do{

               val inventoryState = cursor.getString(cursor.getColumnIndex("inventory_state"))
               val inventoryId = cursor.getInt(cursor.getColumnIndex("inventory_id"))
               val inventoryContreAfaire = cursor.getString(cursor.getColumnIndex("inventory_contreAFaire"))

                    inventory_ContreAfaireInventory2 = inventoryContreAfaire
                    inventory_state = inventoryState
                    inventory_idPartie2 = inventoryId

           }while (cursor.moveToNext())
       }

       // Load les product de l'inventaire partie1 et copie les products avec l'inventaire Partie2
       var newProductValues = ContentValues()
       val projectionsProduct= arrayOf("ID","name","price","quantity", "inventory_id", "sousTotal")
       val selectionArgsProduct= arrayOf(inventory_idPartie1.toString())
       val cursorProduct=mydbManager.QueryProduct(projectionsProduct,"inventory_id like ?",selectionArgsProduct,"name")
       listProduct.clear()
       if(cursorProduct.moveToFirst()){

           do{
               val id=cursorProduct.getInt(cursorProduct.getColumnIndex("ID"))
               val name=cursorProduct.getString(cursorProduct.getColumnIndex("name"))
               val price=cursorProduct.getString(cursorProduct.getColumnIndex("price"))
               val quantity=cursorProduct.getString(cursorProduct.getColumnIndex("quantity"))
               val sousTotal = cursorProduct.getString(cursorProduct.getColumnIndex("sousTotal"))


               newProductValues.put("name", name)
               newProductValues.put("price", price)
               newProductValues.put("quantity", quantity)
               newProductValues.put("sousTotal", sousTotal)
               newProductValues.put("inventory_id", inventory_idPartie2.toString())

               mydbManager.InsertProduct(newProductValues)


               }while (cursorProduct.moveToNext())
           }

    }

        fun LoadQuery(){

        // Load les produit d'inventory_idPartie2

        var dbManager= DbManager(this)
        val projections= arrayOf("ID","name","price","quantity", "inventory_id", "sousTotal")
        val selectionArgs= arrayOf(inventory_idPartie2.toString())
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
        listViewProductContreInventaire.adapter=myProductAdapter


    }

        fun deleteInventoryProduct(){
            var dbManager= DbManager(this)
            var selectionArgs = arrayOf(inventory_idPartie2.toString())
            dbManager.DeleteInventory("inventory_id=?", selectionArgs)

            dbManager.DeleteProduit("inventory_id=?",selectionArgs)
            Toast.makeText(this, "Inventaire supprimé", Toast.LENGTH_LONG).show()

            finish()

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
                var myProduct=listProductsAdpater[p0]
                myView.tProductName.text=myProduct.productName.toString()
                myView.tPrice.text=myProduct.productPrice.toString()
                myView.tQuantity.text=myProduct.productQuantity.toString()

                myView.bDelete.VisibleOrGone(false)


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

        fun GoToUpdateInventory() {
            var intent=  Intent(this,SaveInventory::class.java)
            intent.putExtra("inventory_idPartie1",inventory_idPartie1)
            intent.putExtra("inventory_id",inventory_idPartie2)
            intent.putExtra("inventory_contreAFaire",inventory_ContreAfaireInventory2)
            intent.putExtra("inventory_proprio",inventory_proprio)
            intent.putExtra("inventory_locator",inventory_locator)


            startActivity(intent)
        }

    // longue méthode sa mère
        fun saveProductAndInventory(){


        val mydbManager = DbManager(this)
        var totalDesSousTotaux : Double = 0.0

        // Calcul du Total pour l'inventaire
        val projections2= arrayOf("sousTotal")
        val selectionArgs2= arrayOf(inventory_idPartie2.toString())
        val cursor2=mydbManager.QueryProduct(projections2,"inventory_id like ?",selectionArgs2,"name")
        if(cursor2.moveToFirst()){

            do{
                //  val id=cursor2.getInt(cursor2.getColumnIndex("ID"))

                val sousTotal = cursor2.getString(cursor2.getColumnIndex("sousTotal"))


                totalDesSousTotaux += sousTotal.toString().toDouble()


            }while (cursor2.moveToNext())
        }

        var totalValue = ContentValues()
        totalDesSousTotaux = Math.round(totalDesSousTotaux*100.0)/100.0
        totalValue.put("inventory_total", totalDesSousTotaux.toString())
        var selectionArgs3 = arrayOf(inventory_idPartie2.toString())
        mydbManager.UpdateInventory(totalValue,"inventory_id=?", selectionArgs3)
        GoToUpdateInventory()

    }

        fun View.VisibleOrGone(visible: Boolean) {
        visibility = if(visible) View.VISIBLE else View.GONE
    }


}
