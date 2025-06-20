import com.moviles.agrocity.models.Comment
import com.moviles.agrocity.models.Garden
import com.moviles.agrocity.models.GardenResponse
import com.moviles.agrocity.models.LoginDTO
import com.moviles.agrocity.models.PestDto
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
    @GET("api/Garden")
    suspend fun getGardens(): List<Garden>


    @GET("api/Garden/garden/{userId}")
    suspend fun getGardensByUserId(@Path("userId") id: Int): List<Garden>





    @GET("api/Garden/{gardenId}")
    suspend fun getGardenById(@Path("gardenId") id: Int): GardenResponse

    @Multipart
    @POST("api/Garden")
    suspend fun addGarden(
        @Part("UserId") userId: RequestBody,
        @Part("Name") name: RequestBody,
        @Part("Description") description: RequestBody,
        @Part("CreatedAt") createdAt: RequestBody,
        @Part file: MultipartBody.Part?
    ): Garden

    @Multipart
    @PUT("api/Garden/{gardenId}")
    suspend fun updateGarden(
        @Path("gardenId") id: Int,
        @Part("UserId") userId: RequestBody,
        @Part("Name") name: RequestBody,
        @Part("Description") description: RequestBody,
        @Part("CreatedAt") createdAt: RequestBody,
        @Part file: MultipartBody.Part?
    ): Garden


    @DELETE("api/Garden/{gardenId}")
    suspend fun deleteGarden(@Path("gardenId") id: Int): Response<Unit>











    @GET("api/pest")
    suspend fun getAllPests(): Response<List<PestDto>>

    @GET("api/Pest/External")
    suspend fun getExternalPests(): Response<List<PestDto>>

    // Endpoints de comentarios
    @GET("api/comments")
    suspend fun getAllComments(): Response<List<Comment>>

    @GET("api/comments/{id}")
    suspend fun getCommentById(@Path("id") id: Int): Response<Comment>

    @POST("api/comments")
    suspend fun createComment(@Body comment: Comment): Response<Comment>

    @PUT("api/comments/{id}")
    suspend fun updateComment(@Path("id") id: Int, @Body comment: Comment): Response<Comment>

    @DELETE("api/comments/{id}")
    suspend fun deleteComment(@Path("id") id: Int): Response<Unit>

    @GET("api/comments/garden/{gardenId}")
    suspend fun getCommentsByPublication(@Path("gardenId") gardenId: Int): Response<List<Comment>>
}
