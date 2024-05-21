package app.revanced.patches.transit.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

/*
    .line 11
    .line 12
    invoke-static {v2}, Lcom/thetransitapp/droid/shared/data/TransitLib;->getEnvironmentVariableBooleanValue(Ljava/lang/String;)Z
    move-result v2
 */
internal object IsPremiumFingerprint : MethodFingerprint(
    customFingerprint ={ methodDef, classDef ->
        classDef.type.endsWith("TransitLib") && methodDef.name == "getEnvironmentVariableBooleanValue"
    },
    returnType = "Z",
    parameters = listOf("Ljava/lang/String;"),
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    opcodes = listOf(
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT
    )
)