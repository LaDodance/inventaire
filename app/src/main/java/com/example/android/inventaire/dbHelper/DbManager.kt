package com.example.android.inventaire.dbHelper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast

class DbManager {

    val dbName="Product"
    val dbTableProduct="product_table"
    val colProductID="ID"
    val colname="name"
    val colprice="price"
    val colquantity="quantity"
    val colSousTotal="sousTotal"


    val dbTableInventory="inventory_table"
    val colIdInventory="inventory_id"
    val colDateInventory="inventory_date"
    val colProprio="inventory_proprio"
    val colRepProprio ="inventory_rep_proprio"
    val colLocator="inventory_locator"
    val colRepLocator="inventory_rep_locator"
    val colStateInventory = "inventory_state"
    val colTotalInventory = "inventory_total"
    val colContreAfaire = "inventory_contreAFaire"

    val dbVersion=1
    //CREATE TABLE IF NOT EXISTS MyNotes (ID INTEGER PRIMARY KEY,title TEXT, description TEXT);"
    // /!\ espaces devant et TEXT important
    val sqlCreateTableProduct="CREATE TABLE IF NOT EXISTS "+
            dbTableProduct +" ("+
            colProductID +" INTEGER PRIMARY KEY,"+
            colname + " TEXT, "+
            colprice +" TEXT, " +
            colquantity + " TEXT, " +
            colSousTotal + " TEXT, " +
            colIdInventory + " TEXT);"
    val sqlCreateTableInventory="CREATE TABLE IF NOT EXISTS " +
            dbTableInventory + " ("+
            colIdInventory + " INTEGER PRIMARY KEY, "+
            colDateInventory + " TEXT, "+
            colProprio + " TEXT, " +
            colLocator + " TEXT, " +
            colRepProprio + " TEXT, " +
            colRepLocator + " TEXT, " +
            colTotalInventory + " TEXT, " +
            colContreAfaire + " TEXT, " +
            colStateInventory + " TEXT);"

    var sqlDB: SQLiteDatabase?=null

    constructor(context: Context){
        var db=DatabaseHelperProduct(context)
        sqlDB=db.writableDatabase

    }


    inner class  DatabaseHelperProduct: SQLiteOpenHelper {
        var context: Context?=null
        constructor(context: Context):super(context,dbName,null,dbVersion){
            this.context=context
        }
        override fun onCreate(p0: SQLiteDatabase?) {
            p0!!.execSQL(sqlCreateTableProduct)
            p0!!.execSQL(sqlCreateTableInventory)
            Toast.makeText(this.context," database is created", Toast.LENGTH_LONG).show()

        }

        override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
            p0!!.execSQL("Drop table IF EXISTS $dbTableProduct")
        }

    }

    // Methode sur la table Product

    fun InsertProduct(values: ContentValues):Long{

        val ID= sqlDB!!.insert(dbTableProduct,"",values)
        return ID
    }



    fun  QueryProduct(projection:Array<String>, selection:String, selectionArgs:Array<String>, sorOrder:String): Cursor {

        val qb= SQLiteQueryBuilder()
        qb.tables=dbTableProduct
        val cursor=qb.query(sqlDB,projection,selection,selectionArgs,null,null,sorOrder)
        return cursor
    }

    fun DeleteProduit(selection:String, selectionArgs:Array<String>):Int{

        val count=sqlDB!!.delete(dbTableProduct,selection,selectionArgs)
        return  count
    }

    fun UpdateProduct(values: ContentValues, selection:String, selectionargs:Array<String>):Int{

        val count=sqlDB!!.update(dbTableProduct,values,selection,selectionargs)
        return count
    }

    // Methode sur la table Inventory

    fun InsertInventory(values: ContentValues):Long{

        val ID= sqlDB!!.insert(dbTableInventory,"",values)
        return ID
    }

    fun  QueryInventory(projection:Array<String>, selection:String, selectionArgs:Array<String>, sorOrder:String): Cursor {

        val qb= SQLiteQueryBuilder()
        qb.tables=dbTableInventory
        val cursor=qb.query(sqlDB,projection,selection,selectionArgs,null,null,sorOrder)
        return cursor
    }

    fun DeleteInventory(selection:String, selectionArgs:Array<String>):Int{

        val count=sqlDB!!.delete(dbTableInventory,selection,selectionArgs)
        return  count
    }

    fun UpdateInventory(values: ContentValues, selection:String, selectionargs:Array<String>):Int{

        val count=sqlDB!!.update(dbTableInventory,values,selection,selectionargs)
        return count
    }

    // Jointure en SQLITE
    /*
    fun getJoinedInfo(lookingFor: String): Cursor? {
        Log.d(TAG, "DB: looking up info")
        val cursor: Cursor
        val query: String
        query = "SELECT " +
                " a.fieldName1," +
                " a.fieldName2," +
                " b.fieldName3," +
                " c.fieldName4" +
                " FROM tableName1 a" +
                "     INNER JOIN tableName2 b ON a.someKeyInA = b.someKeyInB" +
                "     INNER JOIN tableName3 c ON a.someOtherKeyInA = c.someKeyInC" +
                " WHERE a.FieldName ='" + lookingFor + "'"
        Log.d(TAG, "DB: query = \n" + query.replace(", ", ",\n  "))
        cursor = db.rawQuery(query, null)
        Log.d(TAG, "DB: query complete")
        return cursor
    }*/
}