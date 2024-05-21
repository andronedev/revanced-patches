package app.revanced.patches.transit.misc
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.transit.misc.fingerprints.IsPremiumFingerprint

@Patch(
    name = "Pro Features Unlock",
    description = "Unlock all pro features in Transit",
    compatiblePackages = [
        CompatiblePackage("com.thetransitapp.droid", ["5.15.16"]),
    ],
)
@Suppress("unused")
object TransitUnlockPatch :  BytecodePatch(setOf(IsPremiumFingerprint)) {
    override fun execute(context: BytecodeContext) = IsPremiumFingerprint.result?.let { result ->
        val isSubscribedIndex = result.scanResult.patternScanResult!!.startIndex
        result.mutableMethod.replaceInstruction(isSubscribedIndex, "const/4 v0, 0x1")
    } ?: throw IllegalStateException("Fingerprint not found")
}