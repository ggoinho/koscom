package kr.co.koscom.omp.data.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import kr.co.koscom.omp.data.ServiceFactory
import kr.co.koscom.omp.data.model.Request
import kr.co.koscom.omp.data.model.Response

class UpdateViewModel : ViewModel() {

    val updateService = ServiceFactory.getUpdateService()

    fun updateVersion(device: String?): Flowable<Response> {

        var request = Request()
        request.DEVICE = device

        return updateService.updateVersion(request)
    }

}