package app.revanced.patches.geniusscan.misc

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.geniusscan.misc.fingerprints.CurrentPlanFingerprint
import app.revanced.patches.transit.misc.fingerprints.IsPremiumFingerprint

@Patch(
    name = "Pro Features Unlock",
    description = "Unlock all pro features in Genius Scan",
    compatiblePackages = [
        CompatiblePackage("com.thegrizzlylabs.geniusscan.free", ["7.17.2"]),
    ],
)
@Suppress("unused")
object GeniusscanUnlockPatch : BytecodePatch(setOf(CurrentPlanFingerprint)) {
    override fun execute(context: BytecodeContext) = IsPremiumFingerprint.result?.let { result ->
        // always return "ultra" plan
        result.mutableMethod.replaceInstruction(4, "const/4 p1, 0x4")
    } ?: throw IllegalStateException("Fingerprint not found")
}