package app.revanced.patches.flightradar24.misc
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.flightradar24.misc.fingerprints.GetMaxAlertsFingerprint

@Patch(
    name = "Max Alerts Patch",
    description = "Allow unlimited alerts",
    compatiblePackages = [
        CompatiblePackage("com.flightradar24free"),
    ],
)
@Suppress("unused")
object Flightradar24MaxAlertsPatch :  BytecodePatch(setOf(GetMaxAlertsFingerprint)) {
    override fun execute(context: BytecodeContext) = GetMaxAlertsFingerprint.result?.let { result ->
        // always return 100
        result.mutableMethod.addInstruction(0, "const/16 v0, 0x64")
    } ?: throw IllegalStateException("Fingerprint not found")
}