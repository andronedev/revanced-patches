package app.revanced.patches.geniusscan.misc.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

object CurrentPlanFingerprint : MethodFingerprint(
    // this methode is used to find the fingerprint of the that returns the current plan
    strings = listOf("basic", "plus", "plusLegacy", "ultra"),
    )