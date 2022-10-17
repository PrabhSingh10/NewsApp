package com.androiddevs.mvvmnewsapp.util

//Sealed Classes define a set of subclasses within it

//A sealed class is implicitly abstract and hence it cannot be instantiated.

//All the subclasses of the sealed class must be defined within the same Kotlin file.
//However, it not necessary to define them within the sealed class, they can be defined
//in any scope where the sealed class is visible.

//It is useful in determining the success or error responses
//and also helps in handling the loading state
sealed class Resource<T>(
    val data : T? = null,   //Data is generic type because the resultant data can be of any type
    val message : String? = null
) {

    class Success<T>(data: T) : Resource<T>(data)
        //Data is not null, because if the response is successful, there will be data
    class Failure<T>(message: String, data: T? = null) : Resource<T>(data, message)
        //Message is not null, because if the response has error, there will be a message and
        //data is null-type because there may or may not be some data
    class Loading<T> : Resource<T>()
        //There is no arg constructor because it represents the loading of response
}