import com.moviles.agrocity.models.ForgotPasswordDTO
import com.moviles.agrocity.models.LoginDTO
import com.moviles.agrocity.models.RegisterDTO
import com.moviles.agrocity.models.User
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

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body dto: ForgotPasswordDTO): Response<Void>


}
