package custom

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import com.airbnb.lottie.LottieAnimationView
import com.risewide.bdebugapp.R
import com.risewide.bdebugapp.util.BLog
import com.skt.aidev.nugustb.ui.VoiceChromeManager
import java.util.concurrent.ConcurrentLinkedQueue


/**
 * Created by 1001026 on 2018. 8. 6..
 * apply lottie
 */

class ChromeBarView : LottieAnimationView, VoiceChromeManager.OnChromeBarTypeListener{

    private val TAG = "ChromeBarView"

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?):super(context, attributeSet){
        enableMergePathsForKitKatAndAbove(true)
        addAnimatorListener(animationListener)
        VoiceChromeManager.Instance.registerChromeBarTypeListener(this)
    }

    inner class VoiceChromeInfo(val barType: ChromeBarType, val resId: Int?, val isLoop: Boolean = false)

    private val voiceChromeQueue = ConcurrentLinkedQueue<VoiceChromeInfo>()
    private var resId: Int? = null
    private var currentChromeBarType: ChromeBarType = ChromeBarType.CBTNone
    private var currentVoiceChromeInfo: VoiceChromeInfo? = null
    private val animationListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {
            BLog.d(TAG, "[onAnimationStart]: $currentChromeBarType")
        }

        override fun onAnimationEnd(animation: Animator?) {
            BLog.d(TAG,"[onAnimationEnd]: $currentChromeBarType")

            if(voiceChromeQueue.size == 0) { // 애니매이션이 모두 완료 후 다음 task 진행
                if(VoiceChromeManager.Instance.notify()){
                    BLog.d(TAG, "notifyChromeBarListener")
                    return
                }
            }

            if(voiceChromeQueue.size > 0){
                BLog.d(TAG,"[onAnimationEnd]: playNextAnimation")
                playNextAnimation()
            }else{
                currentVoiceChromeInfo?.apply {
                    if(isLoop){
                        BLog.d(TAG,"[onAnimationEnd]: loop: $currentChromeBarType")
                        playAnimation()
                    }
                }
            }
        }

        override fun onAnimationCancel(animation: Animator?) {
            BLog.d(TAG,"[onAnimationCancel]")
        }

        override fun onAnimationRepeat(animation: Animator?) {
            BLog.d(TAG,"[onAnimationRepeat]")
        }
    }


    private fun isLoop(barType: ChromeBarType) : Boolean{
        return when(barType){
            ChromeBarType.CBTReady,
            ChromeBarType.CBTListening,
            ChromeBarType.CBTThinking,
            ChromeBarType.CBTSpeaking,
            ChromeBarType.CBTError -> true
            else -> false
        }
    }

    private fun getResId(barType: ChromeBarType) : Int?{
        return when(barType) {
            ChromeBarType.CBTReady -> R.raw.lp
            ChromeBarType.CBTListening -> R.raw.la
            ChromeBarType.CBTSpeaking -> R.raw.sp
            ChromeBarType.CBTThinking -> R.raw.pc
            ChromeBarType.CBTError -> R.raw.er
            ChromeBarType.CBTThinkingEndDUX -> R.raw.sp_none
            ChromeBarType.CBTThinkingEndVUX -> R.raw.sp_start
            ChromeBarType.CBTThinkingEndERROR -> R.raw.esp_start
            else -> null
        }
    }

    fun setBarType(barType: ChromeBarType){
        BLog.d(TAG, "[setBarType]: $barType")
        resId = getResId(barType)
        if(null == resId){
            setCurrentBarType(barType)
            return
        }

        val voiceChromeInfo = VoiceChromeInfo(barType, resId, isLoop(barType))
        when(barType){
            ChromeBarType.CBTReady,
            ChromeBarType.CBTListening ->{
                if(currentChromeBarType == barType){
                    return
                }
                voiceChromeQueue.clear() // voiceChromeQueue 초기화
                playAnimation(voiceChromeInfo)
                BLog.d(TAG, "playAnimation")
            }
            ChromeBarType.CBTThinking ->{
                playAnimation(voiceChromeInfo)
            }
            ChromeBarType.CBTSpeaking,
            ChromeBarType.CBTError ->{
                insertQueue(voiceChromeInfo, true)
            }
            else ->{
                insertQueue(voiceChromeInfo, false)
            }
        }
    }

    private fun insertQueue(voiceChromeInfo: VoiceChromeInfo, isClear: Boolean){
        if(isClear){
            voiceChromeQueue.clear()
        }

        if(isAnimating){
            BLog.d(TAG, "insert queue: ${voiceChromeInfo.barType}")
            voiceChromeQueue.add(voiceChromeInfo)
        }else{
            BLog.d(TAG, "playAnimation: ${voiceChromeInfo.barType}")
            playAnimation(voiceChromeInfo)
        }
    }

    private fun setCurrentBarType(barType: ChromeBarType){
        currentChromeBarType = barType
        VoiceChromeManager.Instance.setBarType(currentChromeBarType)
    }

    fun getBarType(): ChromeBarType{
        return currentChromeBarType
    }

    private fun playAnimation(voiceChromeInfo: VoiceChromeInfo){
        if(null != voiceChromeInfo.resId){
            setAnimation(voiceChromeInfo.resId)
            currentVoiceChromeInfo = voiceChromeInfo
            setCurrentBarType(voiceChromeInfo.barType)
            playAnimation()
        }
    }

    private fun playNextAnimation(){
        BLog.d(TAG, "[playNextAnimation]")
        val voiceChromeInfo = voiceChromeQueue.poll()
        if(voiceChromeInfo != null){
            playAnimation(voiceChromeInfo)
        }
    }

    fun setLevel(level: Int){
        //nothing
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        BLog.d(TAG, "[onDetachedFromWindow]")
        VoiceChromeManager.Instance.clear()
        // Voice Chrome hide 될 경우 간헐적으로 Voice Chrome 애니메이션이 종료 되지 않는 이슈가 있어
        // Voice Chrome 닫힐 경우 애니메이션 cancel 하는 코드 추가
        cancelAnimation()
        // Voice Chrome 닫히면 애이메이션 정보 초기화
        currentVoiceChromeInfo = null
        resId = null
    }

    override fun onAddChromeBarType(barType: ChromeBarType) {
        setBarType(barType)
    }
}
