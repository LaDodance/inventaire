package com.example.android.inventaire.activity

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.example.android.inventaire.R
import com.example.android.inventaire.dbHelper.DbManager
import kotlinx.android.synthetic.main.activity_save_inventory.*
import java.text.SimpleDateFormat
import java.util.*

class SaveInventory : AppCompatActivity() {


    var inventory_id =0
    var inventory_idPartie1 =0
    var inventory_ContreAfaireInventory : String? = null
    var inventory_proprio : String ? = null
    var inventory_locator : String ? = null
    var values = ContentValues()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_inventory)

        try {
            var bundle:Bundle= intent.extras!!
            inventory_id=bundle.getInt("inventory_id",0)
            inventory_idPartie1 = bundle.getInt("inventory_idPartie1",0)
            inventory_ContreAfaireInventory = bundle.getString("inventory_contreAFaire","")
            inventory_proprio = bundle.getString("inventory_proprio", "")
            inventory_locator = bundle.getString("inventory_locator", "")

        }catch (ex:Exception){}



        if (inventory_ContreAfaireInventory == "false" && inventory_proprio != "init" && inventory_locator != "init") {
                switchInterne.VisibleOrGone(false)
            editTextCollectifProprio.setText(inventory_proprio.toString().toEditable())
            editTextCollectifLouant.setText(inventory_locator.toString().toEditable())




        }
        else{

            values.put("inventory_state", "Partie 1")
        }

        val sdf = SimpleDateFormat("dd/MM/yy")
        val currentDate = sdf.format(Date())

        tSaveInventoryDate.text = currentDate.toString()


        var dbManager= DbManager(this)


        // initialise le switch
        switchInterne.setText("Inventaire externe")


        switchInterne.setOnCheckedChangeListener{CompoundButton, onSwitch ->

            if (onSwitch){
                Toast.makeText(this, "activé", Toast.LENGTH_SHORT).show()

                editTextCollectifProprio.addTextChangedListener(object : TextWatcher {

                    override fun afterTextChanged(s: Editable) {
                        editTextCollectifLouant.setText(s.toString().toEditable())}

                    override fun beforeTextChanged(s: CharSequence, start: Int,
                                                   count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence, start: Int,
                                               before: Int, count: Int) {

                    }
                })

               editTextResponsableProprio.addTextChangedListener(object : TextWatcher {

                   override fun afterTextChanged(s: Editable) {
                       editTextResponsablelocation.setText(s.toString().toEditable())}

                   override fun beforeTextChanged(s: CharSequence, start: Int,
                                                  count: Int, after: Int) {
                   }

                   override fun onTextChanged(s: CharSequence, start: Int,
                                              before: Int, count: Int) {

                   }
               })

                // si on appuye sur le switch après encodage
                editTextCollectifLouant.setText(editTextCollectifProprio.text.toString())
                editTextResponsablelocation.setText(editTextResponsableProprio.text.toString())

                editTextCollectifLouant.isEnabled = false
                editTextResponsablelocation.isEnabled = false
                switchInterne.setText("Inventaire interne")

                values.put("inventory_state", "Interne")
            }else
            {

                editTextResponsablelocation.setHint("Nom du collectif")
                editTextCollectifLouant.setHint("Nom")

                values.put("inventory_state", "Partie 1")
                editTextCollectifLouant.isEnabled  = true
                editTextResponsablelocation.isEnabled = true
                Toast.makeText(this, "désactivé", Toast.LENGTH_SHORT).show()
                switchInterne.setText("Inventaire externe")

            }
        }



                // Update de l'inventaire transmis via l'id avec le reste des infos
                bSaveOk.setOnClickListener{

                    var selectionArgs = arrayOf(inventory_id.toString())




                    if (editTextCollectifProprio.text.toString() != ""
                        && editTextResponsableProprio.text.toString() != ""
                        && editTextCollectifLouant.text.toString() != ""
                        && editTextResponsablelocation.text.toString() != ""){



                            values.put("inventory_proprio", editTextCollectifProprio.text.toString())
                            values.put("inventory_rep_proprio", editTextResponsableProprio.text.toString())
                            values.put("inventory_locator", editTextCollectifLouant.text.toString())
                            values.put("inventory_rep_locator", editTextResponsablelocation.text.toString())






                    dbManager.UpdateInventory(values, "inventory_id=?", selectionArgs)
                        Toast.makeText(this, "Inventaire updated", Toast.LENGTH_LONG).show()
                    if (inventory_ContreAfaireInventory == "false"){

                        var  dbManager = DbManager(this)
                        var valuesInventoryPart1 = ContentValues()
                        var selectionArgs = arrayOf(inventory_idPartie1.toString())
                        valuesInventoryPart1.put("inventory_contreAFaire", "false")
                        dbManager.UpdateInventory(valuesInventoryPart1,"inventory_id=?", selectionArgs )

                    }

                        finish()
                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    }
                    else {
                        Toast.makeText(this, "Tous les champs doivent être remplis", Toast.LENGTH_LONG).show()
                    }

                }

                bSaveCancel.setOnClickListener{
                    var selectionArgs = arrayOf(inventory_id.toString())
                    dbManager.DeleteInventory("inventory_id=?", selectionArgs)
                    Toast.makeText(this, "Inventaire supprimé", Toast.LENGTH_LONG).show()
                    finish()
                }



            }


    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
    }

    fun View.VisibleOrGone(visible: Boolean) {
        visibility = if(visible) View.VISIBLE else View.GONE
    }

