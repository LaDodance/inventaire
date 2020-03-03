package com.example.android.inventaire.entity

class Product {

    var productID:Int?=null
    var productName:String?=null
    var productPrice:String?=null
    var productQuantity:String?=null
    var inventory_id:String? =null
    var sousTotal : String? = null

    constructor(productID:Int,productName:String,productPrice:String,productQuantity:String, inventory_id:String, sousTotal:String){
        this.productID=productID
        this.productName=productName
        this.productPrice=productPrice
        this.productQuantity=productQuantity
        this.inventory_id= inventory_id
        this.sousTotal = sousTotal
    }
}