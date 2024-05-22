package app.revanced.patches.flightradar24.misc.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

internal object GetMaxAlertsFingerprint : MethodFingerprint(
    returnType = "I",
    customFingerprint = { methodDef, _ ->
        methodDef.name == "getUserAlertsMax"
    },
    opcodes = listOf(
        Opcode.IGET,
        Opcode.RETURN
    )

)