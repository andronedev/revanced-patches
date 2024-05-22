package app.revanced.patches.geniusscan.misc.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

object CurrentPlanFingerprint : MethodFingerprint(
    returnType = "Ljava/lang/String;",
    accessFlags = AccessFlags.PUBLIC.or(AccessFlags.FINAL),
    parameters = listOf("Lcom/thegrizzlylabs/geniusscan/billing/d;"),
    opcodes = listOf(
        Opcode.CONST_STRING,
        Opcode.INVOKE_STATIC,
        Opcode.SGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT,
        Opcode.AGET,
        Opcode.IF_EQ,
        Opcode.CONST_STRING,
        Opcode.RETURN_OBJECT
    ),
    strings = listOf("basic", "plus", "plusLegacy", "ultra")
)
