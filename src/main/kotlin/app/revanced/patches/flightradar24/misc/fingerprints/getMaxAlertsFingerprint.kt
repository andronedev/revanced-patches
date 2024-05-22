package app.revanced.patches.transit.misc.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object getMaxAlertsFingerprint : MethodFingerprint(
    // this methode is used to find the fingerprint of the method getUserAlertsMax
    returnType = "I",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    strings = listOf("userAlertsMax"),
    opcodes = listOf(
        Opcode.IGET,
        Opcode.RETURN
    )

)