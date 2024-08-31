package com.ai.aishotclientkotlin.ui.screens.entrance.login.util

class LoginUtils {

    //Mail Değerlerin boş olması 2
    //Email uzunluğunun yetersiz olması 3
    //Uygunsuz Mail Formatı 4
    //Şifre Değerlerin boş olması 5

    fun loginFormatValidation(phoneNum: String, password: String): Int {

        if (phoneNum.trim().isNotEmpty()) {

            if (phoneNum.length == 11) {



                    return if (password.trim().isNotEmpty()) {

                        1

                    } else {


                        5

                    }

            } else {

                return 3

            }

        } else {

            return 2

        }
    }
}