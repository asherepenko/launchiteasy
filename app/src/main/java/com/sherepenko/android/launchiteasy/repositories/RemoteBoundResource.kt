package com.sherepenko.android.launchiteasy.repositories

import androidx.lifecycle.liveData
import com.sherepenko.android.launchiteasy.data.Resource
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

internal abstract class RemoteBoundResource<LocalDataType, RemoteDataType> {

    companion object {
        private const val TAG = "RemoteBoundResource"
    }

    fun asLiveData() =
        liveData(Dispatchers.IO) {
            emit(Resource.loading())

            val localData = getLocalData()
            emit(
                Resource.loading(
                    localData
                )
            )

            Timber.tag(TAG).d("LOADING: Return data from local DB")

            if (shouldFetchRemoteData(localData)) {
                try {
                    Timber.tag(TAG).d("Fetching remote data...")

                    val remoteData = getRemoteData()

                    Timber.tag(TAG).d("Remote data loaded successfully")

                    saveLocally(toLocalData(remoteData))

                    Timber.tag(TAG).d("Remote data stored locally")

                    emit(
                        Resource.success(
                            getLocalData()
                        )
                    )

                    Timber.tag(TAG).i("SUCCESS: Return data from local DB")
                } catch (e: Exception) {
                    emit(
                        Resource.error(
                            e,
                            getLocalData()
                        )
                    )

                    Timber.tag(TAG).e(e, "ERROR: Return data from local DB")
                }
            } else {
                emit(
                    Resource.success(
                        localData
                    )
                )

                Timber.tag(TAG).i("SUCCESS: Return data from local DB without fetching remote")
            }
        }

    protected abstract suspend fun getRemoteData(): RemoteDataType

    protected abstract suspend fun getLocalData(): LocalDataType

    protected abstract suspend fun saveLocally(data: LocalDataType)

    protected abstract suspend fun toLocalData(data: RemoteDataType): LocalDataType

    protected abstract suspend fun shouldFetchRemoteData(data: LocalDataType?): Boolean
}
