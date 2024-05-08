package com.mohaberabi.run.network.source

import com.mohaberabi.core.data.network.constructRoute
import com.mohaberabi.core.data.network.delete
import com.mohaberabi.core.data.network.get
import com.mohaberabi.core.data.network.safeCall
import com.mohaberabi.core.domain.model.RunModel
import com.mohaberabi.core.domain.run.source.RunRemoteDataSource
import com.mohaberabi.core.domain.utils.AppResult
import com.mohaberabi.core.domain.utils.EmptyDataResult
import com.mohaberabi.core.domain.utils.const.EndPoints
import com.mohaberabi.core.domain.utils.error.DataError
import com.mohaberabi.core.domain.utils.map
import com.mohaberabi.run.network.dto.RunModelDto
import com.mohaberabi.run.network.mappers.toRun
import com.mohaberabi.run.network.mappers.toRunRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class KtorRunRemoteDataSource(
    private val httpClient: HttpClient,
) : RunRemoteDataSource {
    override suspend fun getRuns(): AppResult<List<RunModel>, DataError.Network> {
        return httpClient.get<List<RunModelDto>>(
            EndPoints.RUNS,
        ).map { runDtos ->
            runDtos.map {
                it.toRun()
            }
        }
    }

    override suspend fun postRun(
        run: RunModel,
        mapPicture: ByteArray
    ): AppResult<RunModel, DataError.Network> {

        val createRunRequestJson = Json.encodeToString(run.toRunRequest())
        val result = safeCall<RunModelDto> {
            httpClient.submitFormWithBinaryData(
                url = constructRoute(EndPoints.SINGLE_RUN),
                formData = formData {
                    append("MAP_PICTURE", mapPicture, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=mappicture.jpg")
                    })
                    append("RUN_DATA", createRunRequestJson, Headers.build {
                        append(HttpHeaders.ContentType, "text/plain")
                        append(HttpHeaders.ContentDisposition, "form-data; name=\"RUN_DATA\"")
                    })
                }

            ) {

                method = HttpMethod.Post
            }
        }

        return result.map { dto ->
            dto.toRun()
        }

    }

    override suspend fun deleteRun(
        id:
        String
    ): EmptyDataResult<DataError.Network> {

        return httpClient.delete(
            EndPoints.SINGLE_RUN,
            queryParams = mapOf("id" to id)
        )
    }
}