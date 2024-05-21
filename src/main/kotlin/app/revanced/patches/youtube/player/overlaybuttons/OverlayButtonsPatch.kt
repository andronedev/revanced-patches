package app.revanced.patches.youtube.player.overlaybuttons

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.options.PatchOption.PatchExtensions.booleanPatchOption
import app.revanced.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.patches.youtube.utils.fix.fullscreen.FullscreenButtonViewStubPatch
import app.revanced.patches.youtube.utils.fix.suggestedvideoendscreen.SuggestedVideoEndScreenPatch
import app.revanced.patches.youtube.utils.integrations.Constants.OVERLAY_BUTTONS_PATH
import app.revanced.patches.youtube.utils.playercontrols.PlayerControlsPatch
import app.revanced.patches.youtube.utils.settings.SettingsPatch
import app.revanced.patches.youtube.video.information.VideoInformationPatch
import app.revanced.util.ResourceGroup
import app.revanced.util.copyResources
import app.revanced.util.copyXmlNode
import app.revanced.util.doRecursively
import app.revanced.util.patch.BaseResourcePatch
import org.w3c.dom.Element

@Suppress("DEPRECATION", "unused")
object OverlayButtonsPatch : BaseResourcePatch(
    name = "Overlay buttons",
    description = "Adds an option to display overlay buttons in the video player.",
    dependencies = setOf(
        FullscreenButtonViewStubPatch::class,
        PlayerControlsPatch::class,
        SettingsPatch::class,
        SuggestedVideoEndScreenPatch::class,
        OverlayButtonsBytecodePatch::class,
        VideoInformationPatch::class
    ),
    compatiblePackages = COMPATIBLE_PACKAGE
) {
    private val OutlineIcon by booleanPatchOption(
        key = "OutlineIcon",
        default = false,
        title = "Outline icons",
        description = "Apply the outline icon",
        required = true
    )

    private val WiderBottomPadding by booleanPatchOption(
        key = "WiderBottomPadding",
        default = false,
        title = "Wider bottom padding",
        description = "Apply wider bottom padding. Click effect may not be shown in the correct position."
    )

    override fun execute(context: ResourceContext) {

        /**
         * Inject hook
         */
        arrayOf(
            "AlwaysRepeat;",
            "CopyVideoUrl;",
            "CopyVideoUrlTimestamp;",
            "ExternalDownload;",
            "SpeedDialog;"
        ).forEach { className ->
            PlayerControlsPatch.hookOverlayButtons("$OVERLAY_BUTTONS_PATH/$className")
        }

        /**
         * Copy resources
         */
        arrayOf(
            ResourceGroup(
                "drawable",
                "playlist_repeat_button.xml",
                "playlist_shuffle_button.xml",
                "revanced_repeat_icon.xml"
            )
        ).forEach { resourceGroup ->
            context.copyResources("youtube/overlaybuttons/shared", resourceGroup)
        }

        if (OutlineIcon == true) {
            arrayOf(
                ResourceGroup(
                    "drawable-xxhdpi",
                    "ic_fullscreen_vertical_button.png",
                    "quantum_ic_fullscreen_exit_grey600_24.png",
                    "quantum_ic_fullscreen_exit_white_24.png",
                    "quantum_ic_fullscreen_grey600_24.png",
                    "quantum_ic_fullscreen_white_24.png",
                    "revanced_copy_icon.png",
                    "revanced_copy_icon_with_time.png",
                    "revanced_download_icon.png",
                    "revanced_speed_icon.png",
                    "yt_fill_arrow_repeat_white_24.png",
                    "yt_outline_arrow_repeat_1_white_24.png",
                    "yt_outline_arrow_shuffle_1_white_24.png",
                    "yt_outline_screen_full_exit_white_24.png",
                    "yt_outline_screen_full_white_24.png"
                )
            ).forEach { resourceGroup ->
                context.copyResources("youtube/overlaybuttons/outline", resourceGroup)
            }
        } else {
            arrayOf(
                ResourceGroup(
                    "drawable-xxhdpi",
                    "ic_fullscreen_vertical_button.png",
                    "ic_vr.png",
                    "quantum_ic_fullscreen_exit_grey600_24.png",
                    "quantum_ic_fullscreen_exit_white_24.png",
                    "quantum_ic_fullscreen_grey600_24.png",
                    "quantum_ic_fullscreen_white_24.png",
                    "revanced_copy_icon.png",
                    "revanced_copy_icon_with_time.png",
                    "revanced_download_icon.png",
                    "revanced_speed_icon.png",
                    "yt_fill_arrow_repeat_white_24.png",
                    "yt_outline_arrow_repeat_1_white_24.png",
                    "yt_outline_arrow_shuffle_1_white_24.png",
                    "yt_outline_screen_full_exit_white_24.png",
                    "yt_outline_screen_full_white_24.png",
                    "yt_outline_screen_vertical_vd_theme_24.png"
                )
            ).forEach { resourceGroup ->
                context.copyResources("youtube/overlaybuttons/default", resourceGroup)
            }
        }

        /**
         * Merge xml nodes from the host to their real xml files
         */
        context.copyXmlNode(
            "youtube/overlaybuttons/shared/host",
            "layout/youtube_controls_bottom_ui_container.xml",
            "android.support.constraint.ConstraintLayout"
        )

        val fullscreenButtonId = if (SettingsPatch.upward1909)
            "youtube_controls_fullscreen_button_stub"
        else
            "fullscreen_button"

        val bottomPadding = if (WiderBottomPadding == true) "31.0dip" else "22.0dip"
        context.xmlEditor["res/layout/youtube_controls_bottom_ui_container.xml"].use { editor ->
            editor.file.doRecursively loop@{
                if (it !is Element) return@loop

                // Change the relationship between buttons
                it.getAttributeNode("yt:layout_constraintRight_toLeftOf")?.let { attribute ->
                    if (attribute.textContent == "@id/fullscreen_button") {
                        attribute.textContent = "@+id/speed_dialog_button"
                    }
                }

                it.getAttributeNode("android:id")?.let { attribute ->
                    // Adjust Fullscreen Button size and padding
                    arrayOf(
                        "speed_dialog_button",
                        "copy_video_url_button",
                        "copy_video_url_timestamp_button",
                        "always_repeat_button",
                        "external_download_button",
                        fullscreenButtonId
                    ).forEach { targetId ->
                        if (attribute.textContent.endsWith(targetId)) {
                            arrayOf(
                                "0.0dip" to arrayOf("paddingLeft", "paddingRight"),
                                bottomPadding to arrayOf("paddingBottom"),
                                "48.0dip" to arrayOf("layout_height", "layout_width")
                            ).forEach { (replace, attributes) ->
                                attributes.forEach { name ->
                                    it.getAttributeNode("android:$name")?.textContent = replace
                                }
                            }
                        }
                    }
                }

                if (WiderBottomPadding == false) {
                    // Adjust TimeBar and Chapter bottom padding
                    arrayOf(
                        "@id/time_bar_chapter_title" to "14.0dip",
                        "@id/timestamps_container" to "12.0dip"
                    ).forEach { (id, replace) ->
                        it.getAttributeNode("android:id")?.let { attribute ->
                            if (attribute.textContent == id) {
                                it.getAttributeNode("android:paddingBottom").textContent = replace
                            }
                        }
                    }
                }
            }
        }

        arrayOf(
            "youtube_controls_cf_fullscreen_button.xml",
            "youtube_controls_fullscreen_button.xml"
        ).forEach { xmlFile ->
            val targetXml = context["res"].resolve("layout").resolve(xmlFile)
            if (targetXml.exists()) {
                context.xmlEditor["res/layout/$xmlFile"].use { editor ->
                    editor.file.doRecursively loop@{
                        if (it !is Element) return@loop

                        it.getAttributeNode("android:id")?.let { attribute ->
                            // Adjust Fullscreen Button size and padding
                            if (attribute.textContent.endsWith("fullscreen_button")) {
                                arrayOf(
                                    "0.0dip" to arrayOf("paddingLeft", "paddingRight"),
                                    bottomPadding to arrayOf("paddingBottom"),
                                    "48.0dip" to arrayOf("layout_height", "layout_width")
                                ).forEach { (replace, attributes) ->
                                    attributes.forEach { name ->
                                        it.getAttributeNode("android:$name")?.textContent = replace
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "PREFERENCE_SCREEN: PLAYER",
                "PREFERENCE_SCREENS: PLAYER_BUTTONS",
                "SETTINGS: OVERLAY_BUTTONS"
            )
        )

        SettingsPatch.updatePatchStatus(this)
    }
}