import com.moviles.agrocity.models.Garden
import com.moviles.agrocity.models.LoginDTO
import com.moviles.agrocity.models.RegisterDTO
import com.moviles.agrocity.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/acceso/Login")
    suspend fun loginUser(@Body loginDTO: LoginDTO): Response<Map<String, Any>>

    @POST("api/acceso/Registrarse")
    suspend fun registerUser(@Body registerDTO: RegisterDTO): Response<Map<String, Any>>

    @GET("api/users")
    suspend fun getUsers(): Response<List<User>>

    @POST("api/users")
    suspend fun addUser(@Body userDto: RegisterDTO): Response<User>

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Int?, @Body userDto: User): Response<User>

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int?): Response<Unit>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<User>



    //Garden
    @GET("api/garden")
    suspend fun getGardens(): List<Garden>


    @POST("api/garden")
    @Multipart
    suspend fun addGarden(
        @Part("Name") name: RequestBody?,
        @Part("Description") description: RequestBody?,
        @Part("Schedule") schedule: RequestBody?,
        @Part("Professor") professor: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Garden



    @PUT("api/garden/{id}")
    suspend fun updateGarden(@Path("id") id: Int, @Body garden: Garden): Garden

    @DELETE("api/garden/{id}")
    suspend fun deleteGarden(@Path("id") id: Int): Response<Unit>


    @GET("api/garden/{id}")
    suspend fun getGardenById(@Path("id") id: Int): Garden

}
