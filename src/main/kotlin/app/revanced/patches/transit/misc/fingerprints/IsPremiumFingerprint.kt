package app.revanced.patches.transit.misc.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object IsPremiumFingerprint : MethodFingerprint(
    // this methode is used to find the fingerprint of the method getEnvironmentVariableBooleanValue that is used in the TransitUnlockPatch
    customFingerprint ={ methodDef, classDef ->
        methodDef.name == "getEnvironmentVariableBooleanValue" && methodDef.parameterTypes.size == 1 && methodDef.parameterTypes[0] == "Ljava/lang/String;"
    },
    returnType = "Z",
    parameters = listOf("Ljava/lang/String;"),
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    opcodes = listOf(
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT
    )
)