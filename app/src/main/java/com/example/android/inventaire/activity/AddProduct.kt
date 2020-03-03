package com.example.android.inventaire.activity

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android.inventaire.R
import com.example.android.inventaire.dbHelper.DbManager
import kotlinx.android.synthetic.main.activity_new_product.*

class AddProduct : AppCompatActivity() {


    var id=0
    var sousTotal : Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)




        try{
            var bundle:Bundle= intent.extras!!
            id=bundle.getInt("id",0)
            if(id!=0) {
                editTextNameNewProduct.setText(bundle.getString("name") )
                editTextPriceNewProduct.setText(bundle.getString("price") )
                editTextQuantityNewProduct.setText(bundle.getString("quantity") )
                tTitreAjoutOuMod.text= "Modification du produit"

            }
            else{

            }
        }catch (ex:Exception){}


        val mydbManager = DbManager(this)
        var values = ContentValues()
        bNewProduct.setOnClickListener{

            if (editTextNameNewProduct.text.toString() != "" && editTextPriceNewProduct.text.toString() != "" && editTextQuantityNewProduct.text.toString() != "") {
                values.put("name", editTextNameNewProduct.text.toString())
                values.put("price", editTextPriceNewProduct.text.toString())
                values.put("quantity", editTextQuantityNewProduct.text.toString())

                sousTotal = editTextPriceNewProduct.text.toString().toDouble() * editTextQuantityNewProduct.text.toString().toDouble()
                sousTotal = Math.round(sousTotal*100.0)/100.0
                values.put("sousTotal", sousTotal.toString())

                if (id == 0) {
                    // init seulement si on rajoute un produit
                    // A priori quand on fait un contre, on ne rajoute pas de produit on modifie juste donc ça passe
                    values.put("inventory_id", "init")
                    val ID = mydbManager.InsertProduct(values)
                    if (ID > 0) {
                        Toast.makeText(this, " Produit ajouté", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this, " Il y a une couille ", Toast.LENGTH_LONG).show()
                    }
                } else {
                    var selectionArs = arrayOf(id.toString())
                    val ID = mydbManager.UpdateProduct(values, "ID=?", selectionArs)
                    if (ID > 0) {
                        Toast.makeText(this, " Produit modifié", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Il y a une couille", Toast.LENGTH_LONG).show()
                    }
                }
            }
            else {
                Toast.makeText(this,"Tous les champs doivent être renseignés", Toast.LENGTH_LONG).show()

            }


        }
        buttonCancelProduct.setOnClickListener{

            finish()
        }

    }
}