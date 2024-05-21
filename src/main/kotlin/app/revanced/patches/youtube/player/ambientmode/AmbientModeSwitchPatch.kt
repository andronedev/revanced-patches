package app.revanced.patches.youtube.player.ambientmode

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patches.youtube.player.ambientmode.fingerprints.AmbientModeInFullscreenFingerprint
import app.revanced.patches.youtube.player.ambientmode.fingerprints.PowerSaveModeFingerprint
import app.revanced.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.patches.youtube.utils.integrations.Constants.PLAYER_CLASS_DESCRIPTOR
import app.revanced.patches.youtube.utils.settings.SettingsPatch
import app.revanced.util.getTargetIndexWithMethodReferenceNameReversed
import app.revanced.util.literalInstructionBooleanHook
import app.revanced.util.patch.BaseBytecodePatch
import app.revanced.util.resultOrThrow
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Suppress("unused")
object AmbientModeSwitchPatch : BaseBytecodePatch(
    name = "Ambient mode control",
    description = "Adds an option to bypass the restrictions of ambient mode or disable it completely.",
    dependencies = setOf(SettingsPatch::class),
    compatiblePackages = COMPATIBLE_PACKAGE,
    fingerprints = setOf(
        AmbientModeInFullscreenFingerprint,
        PowerSaveModeFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {

        // region patch for bypass ambient mode restrictions

        PowerSaveModeFingerprint.resultOrThrow().let {
            it.mutableMethod.apply {
                val powerSaveModePrimaryIndex = getTargetIndexWithMethodReferenceNameReversed("isPowerSaveMode")
                val powerSaveModeSecondaryIndex = getTargetIndexWithMethodReferenceNameReversed(powerSaveModePrimaryIndex - 1, "isPowerSaveMode")

                arrayOf(
                    powerSaveModePrimaryIndex,
                    powerSaveModeSecondaryIndex
                ).forEach { index ->
                    val register = getInstruction<OneRegisterInstruction>(index + 1).registerA

                    addInstructions(
                        index + 2, """
                            invoke-static {v$register}, $PLAYER_CLASS_DESCRIPTOR->bypassAmbientModeRestrictions(Z)Z
                            move-result v$register
                            """
                    )
                }
            }
        }

        // endregion

        // region patch for disable ambient mode in fullscreen

        AmbientModeInFullscreenFingerprint.literalInstructionBooleanHook(
            45389368,
            "$PLAYER_CLASS_DESCRIPTOR->disableAmbientModeInFullscreen()Z"
        )

        // endregion

        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "PREFERENCE_SCREEN: PLAYER",
                "SETTINGS: AMBIENT_MODE_CONTROLS"
            )
        )

        SettingsPatch.updatePatchStatus(this)
    }
}