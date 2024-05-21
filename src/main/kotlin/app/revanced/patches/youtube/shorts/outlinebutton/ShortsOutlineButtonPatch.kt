package app.revanced.patches.youtube.shorts.outlinebutton

import app.revanced.patcher.data.ResourceContext
import app.revanced.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.patches.youtube.utils.settings.SettingsPatch
import app.revanced.util.ResourceGroup
import app.revanced.util.copyResources
import app.revanced.util.patch.BaseResourcePatch

@Suppress("unused")
object ShortsOutlineButtonPatch : BaseResourcePatch(
    name = "Shorts outline button",
    description = "Apply the outline icon to the action button of the Shorts player.",
    dependencies = setOf(SettingsPatch::class),
    compatiblePackages = COMPATIBLE_PACKAGE
) {
    override fun execute(context: ResourceContext) {

        arrayOf(
            "xxxhdpi",
            "xxhdpi",
            "xhdpi",
            "hdpi",
            "mdpi"
        ).forEach { dpi ->
            context.copyResources(
                "youtube/shorts/outline",
                ResourceGroup(
                    "drawable-$dpi",
                    "ic_remix_filled_white_24.webp",
                    "ic_remix_filled_white_shadowed.webp",
                    "ic_right_comment_shadowed.webp",
                    "ic_right_dislike_off_shadowed.webp",
                    "ic_right_dislike_on_32c.webp",
                    "ic_right_dislike_on_shadowed.webp",
                    "ic_right_like_off_shadowed.webp",
                    "ic_right_like_on_32c.webp",
                    "ic_right_like_on_shadowed.webp",
                    "ic_right_share_shadowed.webp"
                )
            )
        }

        arrayOf(
            // Shorts outline icons for older versions of YouTube
            ResourceGroup(
                "drawable",
                "ic_right_comment_32c.xml",
                "ic_right_dislike_off_32c.xml",
                "ic_right_like_off_32c.xml",
                "ic_right_share_32c.xml"
            )
        ).forEach { resourceGroup ->
            context.copyResources("youtube/shorts/outline", resourceGroup)
        }

        SettingsPatch.updatePatchStatus(this)

    }
}