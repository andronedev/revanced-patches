package app.revanced.patches.flightradar24.ad
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.flightradar24.ad.fingerprints.AdEnabledFingerprint
/*
.method public isAdvertsEnabled()Z
    .locals 2

    .line 1
    const-string v0, "enabled"

    .line 2
    .line 3
    iget-object v1, p0, Lcom/flightradar24free/models/account/UserFeatures;->adverts:Ljava/lang/String;

    .line 4
    .line 5
    invoke-virtual {v0, v1}, Ljava/lang/String;->equalsIgnoreCase(Ljava/lang/String;)Z

    .line 6
    .line 7
    .line 8
    move-result v0

    .line 9
    return v0
.end method
 */
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
        // always return false
        result.mutableMethod.replaceInstruction(3, "const/4 v0, 0x0")
    } ?: throw IllegalStateException("Fingerprint not found")
}