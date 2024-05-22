package app.revanced.patches.flightradar24.ad
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.flightradar24.ad.fingerprints.AdEnabledFingerprint

@Patch(
    name = "Disable Ads",
    description = "Disable ads in Flightradar24",
    compatiblePackages = [
        CompatiblePackage("com.flightradar24free"),
    ],
)
@Suppress("unused")
object Flightradar24AdPatch :  BytecodePatch(setOf(AdEnabledFingerprint)) {
    override fun execute(context: BytecodeContext) = AdEnabledFingerprint.result?.let { result ->
        result.mutableMethod.replaceInstruction(
            0,
            """
            const/4 v0, 0x1
            return v0
        """
        )
    } ?: throw IllegalStateException("Fingerprint not found")
}