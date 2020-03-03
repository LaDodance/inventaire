package com.example.android.inventaire.entity

class Inventory {

    var colIdInventory : Int ? = null
    var colDateInventory : String? = null
    var colProprio: String? = null
    var colRepProprio : String? = null
    var colLocator: String? = null
    var colRepLocator: String? = null
    var colStateInventory: String? = null
    var colTotalInventory :String?= null
    var colContreAfaire : String? = null

    constructor(colIdInventory : Int,colDateInventory : String, colProprio: String, colRepProprio : String, colLocator: String, colRepLocator: String, colStateInventory: String,colTotalInventory:String, colContreAfaire:String ){

        this.colIdInventory = colIdInventory
        this.colDateInventory = colDateInventory
        this.colProprio = colProprio
        this.colRepProprio = colRepProprio
        this.colLocator = colLocator
        this.colRepLocator = colRepLocator
        this.colStateInventory = colStateInventory
        this.colTotalInventory = colTotalInventory
        this.colContreAfaire = colContreAfaire
    }
}