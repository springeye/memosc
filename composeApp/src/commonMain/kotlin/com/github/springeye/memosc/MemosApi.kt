package com.github.springeye.memosc
import com.github.springeye.memosc.model.Memo
import com.github.springeye.memosc.model.MemosRowStatus
import com.github.springeye.memosc.model.MemosVisibility
import com.github.springeye.memosc.model.Resource
import com.github.springeye.memosc.model.Status
import com.github.springeye.memosc.model.User
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import kotlinx.serialization.Serializable

@Serializable
data class SignInInput(
    val email: String,
    var username: String,
    val password: String,
    val remember:Boolean=true
)
@Serializable
data class CreateMemoInput(
    val content: String,
    val visibility: MemosVisibility? = null,
    val resourceIdList: List<Long>? = null
)
@Serializable
data class UpdateMemoOrganizerInput(
    val pinned: Boolean
)
@Serializable
data class UpdateTagInput(
    val name: String
)
@Serializable
data class DeleteTagInput(
    val name: String
)
@Serializable
data class PatchMemoInput(
    val id: Long,
    val createdTs: Long? = null,
    val rowStatus: MemosRowStatus? = null,
    val content: String? = null,
    val visibility: MemosVisibility? = null,
    val resourceIdList: List<Long>? = null,
)

interface MemosApi {
    @POST("api/v1/auth/signin")
    suspend fun signIn(@Body body: SignInInput):Response<Unit>

    @POST("api/v1/auth/signout")
    suspend fun logout()

    @GET("api/v1/user/me")
    suspend fun me(): User


    /**
     * /api/v1/memo
     * GET
     * Summary
     * Get a list of memos matching optional filters
     *
     * Parameters
     * Name	Located in	Description	Required	Schema
     * creatorId	query	Creator ID	No	integer
     * creatorUsername	query	Creator username	No	string
     * rowStatus	query	Row status	No	string
     * pinned	query	Pinned	No	boolean
     * tag	query	Search for tag. Do not append #	No	string
     * content	query	Search for content	No	string
     * limit	query	Limit	No	integer
     * offset	query	Offset	No	integer
     */
    @GET("api/v1/memo")
    suspend fun listMemo(
        @Query("creatorId") creatorId: Long? = null,
        @Query("creatorUsername") creatorUsername: String? = null,
        @Query("pinned") pinned: Boolean? = null,
        @Query("tag") tag: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("rowStatus") rowStatus: MemosRowStatus? = null,
        @Query("visibility") visibility: MemosVisibility? = null,
        @Query("content") content: String? = null
    ): List<Memo>

    @POST("api/v1/memo")
    suspend fun createMemo(@Body body: CreateMemoInput): Memo

    @GET("api/v1/tag")
    suspend fun getTags(@Query("creatorId") creatorId: Long? = null): List<String>

    @POST("api/v1/tag")
    suspend fun updateTag(@Body body: UpdateTagInput): String

    @POST("api/v1/tag/delete")
    suspend fun deleteTag(@Body body: DeleteTagInput): Unit

    @POST("api/v1/memo/{id}/organizer")
    suspend fun updateMemoOrganizer(@Path("id") memoId: Long, @Body body: UpdateMemoOrganizerInput): Memo

    @PATCH("api/v1/memo/{id}")
    suspend fun patchMemo(@Path("id") memoId: Long, @Body body: PatchMemoInput): Memo

    @DELETE("api/v1/memo/{id}")
    suspend fun deleteMemo(@Path("id") memoId: Long): Unit

    @GET("api/v1/resource")
    suspend fun getResources(): List<Resource>

    @POST("api/v1/resource/blob")
    suspend fun uploadResource(@Body map: MultiPartFormDataContent): Resource

    @DELETE("api/v1/resource/{id}")
    suspend fun deleteResource(@Path("id") resourceId: Long): Unit

    @GET("api/v1/status")
    suspend fun status(): Status

    /**
     * Used to generate the heatmap
     * @return Memo createdTs list
     */
    @GET("api/v1/memo/stats")
    suspend fun stats(@Query("creatorId") creatorId: Long?=null, @Query("creatorUsername") creatorUsername: String? = null): List<Long>
    @GET("api/v1/memo/all")
    suspend fun listAllMemo(
        @Query("pinned") pinned: Boolean? = null,
        @Query("tag") tag: String? = null,
        @Query("visibility") visibility: MemosVisibility? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): List<Memo>
    @GET
    suspend fun get(@Url url: String):Map<String,Any>
}