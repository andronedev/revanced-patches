package app.revanced.patches.flightradar24.misc
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
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
        // always return 50
        result.mutableMethod.replaceInstruction(0,
            """
            const/16 v0, 0x32
            return v0
            """.trimIndent())
    } ?: throw IllegalStateException("Fingerprint not found")
}