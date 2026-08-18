package com.trbear9.ui.viewmodel

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat

//val inputs = Inputs()
var cam: Boolean = false
var gps: Boolean = false
var perm: ActivityResultLauncher<Array<String>> = object: ActivityResultLauncher<Array<String>>(){
    override val contract: ActivityResultContract<Array<String>, *>
        get() = TODO("Not yet implemented")

    override fun launch(
        input: Array<String>,
        options: ActivityOptionsCompat?
    ) {
    }

    override fun unregister() {
    }
}