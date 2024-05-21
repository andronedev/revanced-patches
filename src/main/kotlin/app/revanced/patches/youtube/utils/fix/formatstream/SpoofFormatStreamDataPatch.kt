package app.revanced.patches.youtube.utils.fix.formatstream

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.utils.compatibility.Constants
import app.revanced.patches.youtube.utils.fix.formatstream.fingerprints.FormatStreamModelConstructorFingerprint
import app.revanced.patches.youtube.utils.fix.formatstream.fingerprints.VideoStreamingDataConstructorFingerprint
import app.revanced.patches.youtube.utils.integrations.Constants.MISC_PATH
import app.revanced.patches.youtube.utils.settings.SettingsPatch
import app.revanced.patches.youtube.video.information.VideoInformationPatch
import app.revanced.patches.youtube.video.playerresponse.PlayerResponseMethodHookPatch
import app.revanced.patches.youtube.video.videoid.VideoIdPatch
import app.revanced.util.addFieldAndInstructions
import app.revanced.util.patch.BaseBytecodePatch
import app.revanced.util.resultOrThrow
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

@Suppress("unused")
object SpoofFormatStreamDataPatch : BaseBytecodePatch(
    name = "Spoof format stream data",
    description = "Adds options to spoof format stream data to prevent playback issues.",
    dependencies = setOf(
        PlayerResponseMethodHookPatch::class,
        SettingsPatch::class,
        VideoIdPatch::class,
        VideoInformationPatch::class,
    ),
    compatiblePackages = Constants.COMPATIBLE_PACKAGE,
    fingerprints = setOf(
        FormatStreamModelConstructorFingerprint,
        VideoStreamingDataConstructorFingerprint
    )
) {
    private const val INTEGRATIONS_CLASS_DESCRIPTOR =
        "$MISC_PATH/SpoofFormatStreamDataPatch;"

    override fun execute(context: BytecodeContext) {

        // hook player response video id, to start loading format stream data sooner in the background.
        VideoIdPatch.hookPlayerResponseVideoId("$INTEGRATIONS_CLASS_DESCRIPTOR->newPlayerResponseVideoId(Ljava/lang/String;Z)V")

        // TODO: Check if all instructions need to be spoofed.
        FormatStreamModelConstructorFingerprint.resultOrThrow().let {
            it.mutableMethod.apply {
                val formatStreamDataIndex = it.scanResult.patternScanResult!!.startIndex
                val formatStreamDataReference = getInstruction<ReferenceInstruction>(formatStreamDataIndex).reference
                val formatStreamDataClass = context.findClass((formatStreamDataReference as FieldReference).definingClass)!!.mutableClass

                formatStreamDataClass.methods.find { method -> method.name == "<init>" }
                    ?.apply {
                        val getSmaliInstructions =
                            """
                                if-eqz v0, :ignore
                                iget-object v0, v0, $formatStreamDataReference
                                if-eqz v0, :ignore
                                return-object v0
                                :ignore
                                const-string v0, ""
                                return-object v0
                                """
                        val setSmaliInstructions =
                            """
                                if-eqz p0, :ignore
                                if-eqz v0, :ignore
                                iput-object p0, v0, $formatStreamDataReference
                                :ignore
                                return-void
                                """

                        val integrationMutableClass =
                            context.findClass(INTEGRATIONS_CLASS_DESCRIPTOR)!!.mutableClass

                        integrationMutableClass.addFieldAndInstructions(
                            context,
                            "getFormatStreamData",
                            "formatStreamDataClass",
                            definingClass,
                            getSmaliInstructions,
                            true
                        )
                        integrationMutableClass.addFieldAndInstructions(
                            context,
                            "setFormatStreamData",
                            "formatStreamDataClass",
                            definingClass,
                            setSmaliInstructions,
                            true
                        )
                    } ?: throw PatchException("FormatStreamDataClass not found!")

                hook()
            }
        }

        VideoStreamingDataConstructorFingerprint.resultOrThrow().mutableMethod.hook()

        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "PREFERENCE_CATEGORY: MISC_EXPERIMENTAL_FLAGS",
                "SETTINGS: SPOOF_FORMAT_STREAM_DATA"
            )
        )

        SettingsPatch.updatePatchStatus(this)
    }

    private fun MutableMethod.hook() =
        addInstruction(
            1,
            "invoke-static { }, $INTEGRATIONS_CLASS_DESCRIPTOR->hookFormatStreamData()V"
        )
}
