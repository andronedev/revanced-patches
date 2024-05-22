package app.revanced.patches.geniusscan.misc

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.geniusscan.misc.fingerprints.CurrentPlanFingerprint
import com.android.tools.smali.dexlib2.Opcode

@Patch(
    name = "Pro Features Unlock",
    description = "Unlock all pro features in Genius Scan",
    compatiblePackages = [
        CompatiblePackage("com.thegrizzlylabs.geniusscan.free", ["7.17.2"]),
    ],
)
@Suppress("unused")
object GeniusscanUnlockPatch : BytecodePatch(setOf(CurrentPlanFingerprint)) {
    override fun execute(context: BytecodeContext) = CurrentPlanFingerprint.result?.let { result ->
        // always return "ultra" plan
        val instructions = result.mutableMethod.implementation!!.instructions
        val targetInstructionIndex = instructions.indexOfFirst { it.opcode == Opcode.CONST_STRING && it.toString().contains("basic") }

        if (targetInstructionIndex != -1) {
            // Replace the instruction at the found index to return "ultra"
            result.mutableMethod.replaceInstruction(targetInstructionIndex, "const-string p1, \"ultra\"")
            // Adjust the return statement if necessary
            result.mutableMethod.replaceInstruction(targetInstructionIndex + 1, "return-object p1")
        } else {
            throw IllegalStateException("Target instruction not found")
        }
    } ?: throw IllegalStateException("Fingerprint not found")
}