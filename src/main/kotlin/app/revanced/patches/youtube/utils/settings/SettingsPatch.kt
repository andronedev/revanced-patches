package app.revanced.patches.youtube.utils.settings

import app.revanced.patcher.data.ResourceContext
import app.revanced.patches.shared.elements.StringsElementsUtils.removeStringsElements
import app.revanced.patches.shared.mapping.ResourceMappingPatch
import app.revanced.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.patches.youtube.utils.integrations.IntegrationsPatch
import app.revanced.patches.youtube.utils.resourceid.SharedResourceIdPatch
import app.revanced.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.revanced.patches.youtube.utils.settings.ResourceUtils.addPreferenceFragment
import app.revanced.patches.youtube.utils.settings.ResourceUtils.updatePatchStatus
import app.revanced.patches.youtube.utils.settings.ResourceUtils.updatePatchStatusSettings
import app.revanced.util.ResourceGroup
import app.revanced.util.classLoader
import app.revanced.util.copyResources
import app.revanced.util.copyXmlNode
import app.revanced.util.patch.BaseBytecodePatch
import app.revanced.util.patch.BaseResourcePatch
import org.w3c.dom.Element
import java.io.Closeable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.jar.Manifest

@Suppress("DEPRECATION", "unused")
object SettingsPatch : BaseResourcePatch(
    name = "Settings",
    description = "Applies mandatory patches to implement ReVanced Extended settings into the application.",
    dependencies = setOf(
        IntegrationsPatch::class,
        ResourceMappingPatch::class,
        SharedResourceIdPatch::class,
        SettingsBytecodePatch::class
    ),
    compatiblePackages = COMPATIBLE_PACKAGE,
    requiresIntegrations = true
), Closeable {

    private val THREAD_COUNT = Runtime.getRuntime().availableProcessors()
    private val threadPoolExecutor = Executors.newFixedThreadPool(THREAD_COUNT)

    internal lateinit var contexts: ResourceContext
    internal var upward1831 = false
    internal var upward1834 = false
    internal var upward1839 = false
    internal var upward1842 = false
    internal var upward1849 = false
    internal var upward1902 = false
    internal var upward1909 = false
    internal var upward1912 = false

    override fun execute(context: ResourceContext) {
        contexts = context

        val resourceXmlFile = context["res/values/integers.xml"].readBytes()

        for (threadIndex in 0 until THREAD_COUNT) {
            threadPoolExecutor.execute thread@{
                context.xmlEditor[resourceXmlFile.inputStream()].use { editor ->
                    val resources = editor.file.documentElement.childNodes
                    val resourcesLength = resources.length
                    val jobSize = resourcesLength / THREAD_COUNT

                    val batchStart = jobSize * threadIndex
                    val batchEnd = jobSize * (threadIndex + 1)
                    element@ for (i in batchStart until batchEnd) {
                        if (i >= resourcesLength) return@thread

                        val node = resources.item(i)
                        if (node !is Element) continue

                        if (node.nodeName != "integer" || !node.getAttribute("name")
                                .startsWith("google_play_services_version")
                        ) continue

                        val playServicesVersion = node.textContent.toInt()

                        upward1831 = 233200000 <= playServicesVersion
                        upward1834 = 233500000 <= playServicesVersion
                        upward1839 = 234000000 <= playServicesVersion
                        upward1842 = 234302000 <= playServicesVersion
                        upward1849 = 235000000 <= playServicesVersion
                        upward1902 = 240204000 < playServicesVersion
                        upward1909 = 241002000 <= playServicesVersion
                        upward1912 = 241302000 <= playServicesVersion

                        break
                    }
                }
            }
        }

        threadPoolExecutor
            .also { it.shutdown() }
            .awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)

        /**
         * remove strings duplicated with RVX resources
         *
         * YouTube does not provide translations for these strings.
         * That's why it's been added to RVX resources.
         * This string also exists in RVX resources, so it must be removed to avoid being duplicated.
         */
        context.removeStringsElements(
            arrayOf("values"),
            arrayOf(
                "accessibility_settings_edu_opt_in_text",
                "accessibility_settings_edu_opt_out_text"
            )
        )

        /**
         * copy arrays, strings and preference
         */
        arrayOf(
            "arrays.xml",
            "dimens.xml",
            "strings.xml",
            "styles.xml"
        ).forEach { xmlFile ->
            context.copyXmlNode("youtube/settings/host", "values/$xmlFile", "resources")
        }

        arrayOf(
            ResourceGroup(
                "layout",
                "revanced_settings_preferences_category.xml",
                "revanced_settings_with_toolbar.xml",
            ),
            ResourceGroup(
                "xml",
                "revanced_prefs.xml",
            )
        ).forEach { resourceGroup ->
            context.copyResources("youtube/settings", resourceGroup)
        }

        /**
         * initialize ReVanced Extended Settings
         */
        context.addPreferenceFragment("revanced_extended_settings")

        /**
         * remove ReVanced Extended Settings divider
         */
        arrayOf("Theme.YouTube.Settings", "Theme.YouTube.Settings.Dark").forEach { themeName ->
            context.xmlEditor["res/values/styles.xml"].use { editor ->
                with(editor.file) {
                    val resourcesNode = getElementsByTagName("resources").item(0) as Element

                    val newElement: Element = createElement("item")
                    newElement.setAttribute("name", "android:listDivider")

                    for (i in 0 until resourcesNode.childNodes.length) {
                        val node = resourcesNode.childNodes.item(i) as? Element ?: continue

                        if (node.getAttribute("name") == themeName) {
                            newElement.appendChild(createTextNode("@null"))

                            node.appendChild(newElement)
                        }
                    }
                }
            }
        }

    }

    internal fun addPreference(settingArray: Array<String>) {
        contexts.addPreference(settingArray)
    }

    internal fun updatePatchStatus(patch: BaseResourcePatch) {
        updatePatchStatus(patch.name!!)
    }

    internal fun updatePatchStatus(patch: BaseBytecodePatch) {
        updatePatchStatus(patch.name!!)
    }

    internal fun updatePatchStatus(patchTitle: String) {
        contexts.updatePatchStatus(patchTitle)
    }

    override fun close() {

        // region set ReVanced Patches Version

        val jarManifest = classLoader.getResources("META-INF/MANIFEST.MF")
        while (jarManifest.hasMoreElements())
            contexts.updatePatchStatusSettings(
                "ReVanced Patches",
                Manifest(jarManifest.nextElement().openStream())
                    .mainAttributes
                    .getValue("Version") + ""
            )

        // endregion

        // region set ReVanced Integrations Version

        val buildConfigMutableClass = SettingsBytecodePatch.contexts.findClass { it.sourceFile == "BuildConfig.java" }!!.mutableClass
        val versionNameField = buildConfigMutableClass.fields.single { it.name == "VERSION_NAME" }
        val versionName = versionNameField.initialValue.toString().trim().replace("\"","").replace("&quot;", "")

        contexts.updatePatchStatusSettings(
            "ReVanced Integrations",
            versionName
        )

        // endregion

    }
}