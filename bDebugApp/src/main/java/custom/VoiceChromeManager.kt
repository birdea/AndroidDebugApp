package com.skt.aidev.nugustb.ui

import com.risewide.bdebugapp.util.BLog
import custom.ChromeBarType
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Created by Won Jong Hoon on 2018. 7. 31..
 * ChromeBar Controller
 */
class VoiceChromeManager{
    val TAG = "VoiceChromeManager"

    private object SingletonHolder{
        val instance = VoiceChromeManager()
    }

    companion object {
        @JvmStatic
        val Instance: VoiceChromeManager by lazy { SingletonHolder.instance }
    }

    private val onAnimatorListenerQueue = ConcurrentLinkedQueue<OnAnimatorListener>()
    private var currentChromeBarType : ChromeBarType = ChromeBarType.CBTNone
    private var chromeBarTypeListener : OnChromeBarTypeListener? = null

    interface OnAnimatorListener{
        fun onAnimationEnd()
    }

    interface OnChromeBarTypeListener{
        fun onAddChromeBarType(barType: ChromeBarType)
    }


    fun addAnimatorListener(listener: OnAnimatorListener, barType: ChromeBarType) {
        BLog.d(TAG, "[addAnimatorListener]: $barType")
        if(isProcessing()){
            BLog.d(TAG,  "[addAnimatorListener] registerDuxCardListener: $barType")
            onAnimatorListenerQueue.add(listener)
            chromeBarTypeListener?.onAddChromeBarType(barType)
        }else{
            listener.onAnimationEnd()
        }

    }

    fun registerChromeBarTypeListener(listener: OnChromeBarTypeListener){
        BLog.d(TAG, "[registerChromeBarTypeListener]")
        chromeBarTypeListener = listener
    }

    fun notify() : Boolean {
        BLog.d(TAG, "[notify]")
        var isNotify = false
        if(isProcessingEnd()){
            BLog.d(TAG, "[notify] processingEnd")
            while (onAnimatorListenerQueue.size > 0){
                onAnimatorListenerQueue.poll()?.onAnimationEnd()
                isNotify = true
            }
        }
        return isNotify
    }


    fun setBarType(barType: ChromeBarType){
        currentChromeBarType = barType
    }

    fun isProcessing(): Boolean{
        BLog.d(TAG, "isProcessing $currentChromeBarType")
        return currentChromeBarType == ChromeBarType.CBTThinking
    }

    fun isProcessingEnd(): Boolean{
        BLog.d(TAG, "isProcessingEnd $currentChromeBarType")
        return currentChromeBarType == ChromeBarType.CBTThinkingEndDUX
                || currentChromeBarType == ChromeBarType.CBTThinkingEndVUX
                || currentChromeBarType == ChromeBarType.CBTThinkingEndERROR
    }

    fun clear(){
        BLog.d(TAG, "[clear]")
        notify()
        currentChromeBarType = ChromeBarType.CBTNone
        onAnimatorListenerQueue.clear()

    }

    fun clearChromeBarTypeListener(){
        BLog.d(TAG, "[clearChromeBarTypeListener]")
        chromeBarTypeListener = null
    }
}