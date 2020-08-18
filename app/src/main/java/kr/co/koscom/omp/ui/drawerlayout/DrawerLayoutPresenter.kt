package kr.co.koscom.omp.ui.drawerlayout

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kr.co.koscom.omp.ui.drawerlayout.contract.DrawerLayoutContract

class DrawerLayoutPresenter(private val view: DrawerLayoutContract.View?): DrawerLayoutContract.Presenter {
    internal val tag = this.javaClass.simpleName


    private val disposable = CompositeDisposable()

    private val activity: AppCompatActivity
        get() = (view as AppCompatActivity)

    private val context: Context
        get() = (view as Context)


    override fun detachView() {
        disposable.clear()
    }



}