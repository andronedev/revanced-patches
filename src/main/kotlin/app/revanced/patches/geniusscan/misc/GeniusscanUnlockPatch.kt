package app.revanced.patches.geniusscan.misc

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.geniusscan.misc.fingerprints.GetRequiresPaidPlanFingerprint
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.ClassDef

@Patch(
    name = "Pro Features Unlock",
    description = "Unlock all pro features in Genius Scan",
    compatiblePackages = [
        CompatiblePackage("com.thegrizzlylabs.geniusscan.free", ["7.17.2"]),
    ],
)
@Suppress("unused")
object GeniusscanUnlockPatch : BytecodePatch(setOf(GetRequiresPaidPlanFingerprint)) {
    override fun execute(context: BytecodeContext) {
        val classes = context.classes

        // Résoudre le fingerprint manuellement
        for (classDef in classes) {
            for (method in classDef.methods) {
                if (GetRequiresPaidPlanFingerprint.resolve(context, method, classDef)) {
                    val result = GetRequiresPaidPlanFingerprint.result
                    result?.let {
                        // always return false for requiresPaidPlan
                        val instructions = it.mutableMethod.implementation!!.instructions
                        val returnInstructionIndex = instructions.indexOfFirst { it.opcode == Opcode.RETURN }

                        if (returnInstructionIndex != -1) {
                            // Replace the instruction before the return to always return false
                            it.mutableMethod.replaceInstruction(returnInstructionIndex - 1, "const/4 v0, 0x0")
                            it.mutableMethod.replaceInstruction(returnInstructionIndex, "return v0")
                            return
                        } else {
                            throw IllegalStateException("Return instruction not found")
                        }
                    }
                }
            }
        }

        throw IllegalStateException("Fingerprint not found")
    }
}
