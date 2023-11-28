package com.tdtu.finalproject.utils

class WrongCredentialsException(private var error: String) : Throwable(){
    override val message: String?
        get() = error
}