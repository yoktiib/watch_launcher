/*
 * Copyright (C) 2017 codeestX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package moe.codeest.rxsocketclient

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import moe.codeest.rxsocketclient.meta.DataWrapper
import moe.codeest.rxsocketclient.meta.SocketConfig
import java.net.InetSocketAddress
import java.net.Socket
import moe.codeest.rxsocketclient.meta.SocketState
import org.jetbrains.anko.doAsync
import java.io.DataInputStream
import java.io.IOException
import java.net.SocketException


/**
 * @author: Est <codeest.dev@gmail.com>
 * @date: 2017/7/9
 * @description:
 */

class SocketObservable(val mConfig: SocketConfig, val mSocket: Socket) : Observable<DataWrapper>() {

    var detectUserListener: OnDetectUserListener? = null
    //val mReadThread: ReadThread = ReadThread()
    lateinit var observerWrapper: SocketObserver
    var mHeartBeatRef: Disposable? = null

    override fun subscribeActual(observer: Observer<in DataWrapper>?) {
        observerWrapper = SocketObserver(observer)
        observer?.onSubscribe(observerWrapper)

        Thread(Runnable {
            try {
                defaultSleep = mConfig.delayReadBuffer!!
                maxSleep = mConfig.maxDelayReadBuffer!!
                increaseSleep = mConfig.increaseSleep!!
                isDelay = false
                mSocket.keepAlive = true
                mSocket.connect(InetSocketAddress(mConfig.mIp, mConfig.mPort
                        ?: 1080), mConfig.mTimeout ?: 0)
                observer?.onNext(DataWrapper(SocketState.OPEN, ByteArray(0)))
                //mReadThread.start()
                socketRunAlways()
            } catch (e: IOException) {
                println(e.toString())
                observer?.onNext(DataWrapper(SocketState.CLOSE, ByteArray(0)))
            }
        }).start()
    }

    fun setHeartBeatRef(ref: Disposable) {
        mHeartBeatRef = ref
    }

    fun close() {
        observerWrapper.dispose()
    }

    inner class SocketObserver(private val observer: Observer<in DataWrapper>?) : Disposable {

        fun onNext(data: ByteArray) {
            if (mSocket.isConnected) {
                observer?.onNext(DataWrapper(SocketState.CONNECTING, data))
            }
        }

        fun onNext(dataWrapper: DataWrapper) {
            if (mSocket.isConnected) {
                observer?.onNext(dataWrapper)
            }
        }

        override fun dispose() {
            //mReadThread.interrupt()
            mHeartBeatRef?.dispose()
            mSocket.close()
            observer?.onNext(DataWrapper(SocketState.CLOSE, ByteArray(0)))
        }

        override fun isDisposed(): Boolean {
            return mSocket.isConnected
        }
    }

    private var defaultSleep: Long = 2000
    private var maxSleep: Long = 15000
    private var increaseSleep: Long = 1000
    private var timeSleep: Long = defaultSleep
    private var isMaxInterval: Boolean = false
    private var isDelay: Boolean = false
    fun updateTimeSleep(defaultSleep: Long, isDelay: Boolean) {
        this.defaultSleep = defaultSleep
        this.timeSleep = defaultSleep
        this.isDelay = isDelay
        periodicTask.run()
    }

    fun socketRunAlways() {
        doAsync {
            try {
                while (mSocket.isConnected)
                    periodicTask.run()
            } catch (e: SocketException) {
                observerWrapper.onNext(DataWrapper(SocketState.CLOSE, ByteArray(0)))
            }
        }
    }

    var periodicTask = Runnable {
        try {
            println(" Log Socket : $timeSleep")
            val input = DataInputStream(mSocket.getInputStream())
            val buffer = ByteArray(input.available())
            if (buffer.isNotEmpty()) {
                timeSleep = defaultSleep
                input.read(buffer)
                observerWrapper.onNext(buffer)
            }
            if (isDelay) {
                if (isMaxInterval) {
                    if (timeSleep <= defaultSleep)
                        isMaxInterval = false
                    timeSleep -= increaseSleep
                } else {
                    if (timeSleep >= maxSleep)
                        isMaxInterval = true
                    timeSleep += increaseSleep
                }
            } else {
                timeSleep = defaultSleep
            }
            if (timeSleep >= 1000)
                Thread.sleep(timeSleep)
        } catch (e: InterruptedException) {
        }
    }

    /*inner class ReadThread : Thread() {
        override fun run() {
            super.run()
            try {
                //sch.scheduleAtFixedRate(periodicTask, 0, 5, TimeUnit.SECONDS)
                while (!mReadThread.isInterrupted && mSocket.isConnected) {
                    periodicTask.run()
                }
            } catch (e: SocketException) {
                observerWrapper.onNext(DataWrapper(SocketState.CLOSE, ByteArray(0)))
            }
        }

        // And yet another
        var periodicTask = Runnable {
            try {
                //println(" Log Socket : " + periodicTask)
                println(" Log Socket : ${mLocalConfig.delayReadBuffer}")
                val input = DataInputStream(mSocket.getInputStream())
                val buffer = ByteArray(input.available())
                if (buffer.isNotEmpty()) {
                    timeSleep = defaultSleep
                    input.read(buffer)
                    observerWrapper.onNext(buffer)
                }
               if (isDelay) {
                if (isMaxInterval) {
                    if (timeSleep <= defaultSleep)
                        isMaxInterval = false
                    timeSleep -= increaseSleep
                } else {
                    if (timeSleep >= maxSleep)
                        isMaxInterval = true
                    timeSleep += increaseSleep
                }
            } else {
                timeSleep = defaultSleep
            }
            if (timeSleep >= 1000)
                Thread.sleep(timeSleep)
            } catch (e: InterruptedException) {
            }
        }
    }*/

}