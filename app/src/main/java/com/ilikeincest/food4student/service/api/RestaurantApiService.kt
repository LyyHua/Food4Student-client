package com.ilikeincest.food4student.service.api

import com.here.sdk.core.GeoCoordinates
import com.ilikeincest.food4student.dto.RatingDto
import com.ilikeincest.food4student.dto.RestaurantDetailDto
import com.ilikeincest.food4student.dto.RegisterRestaurantOwnerDto
import com.ilikeincest.food4student.model.Restaurant
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RestaurantApiService {
    @GET("restaurants")
    suspend fun getRestaurants(
        @Query("Latitude") latitude: Double,
        @Query("Longitude") longitude: Double,
        @Query("PageNumber") pageNumber: Int,
        @Query("PageSize") pageSize: Int
    ) : Response<List<Restaurant>>
    @GET("restaurants/{id}")
    suspend fun getRestaurantById(
        @Path("id") id: String
    ) : Response<Restaurant>
    @PUT("restaurants")
    suspend fun updateRestaurant(
        @Body restaurant : RegisterRestaurantOwnerDto
    ) : Response<Restaurant>
    @DELETE("restaurants")
    suspend fun deleteRestaurant() : Response<Unit>
    // Rating
    @GET("restaurants/{id}/ratings")
    suspend fun restaurantRating(
        @Path("id") id: String
    ) : Response<List<RatingDto>>
    @GET("restaurants/owned")
    suspend fun getOwnedRestaurants() : Response<Restaurant>
}