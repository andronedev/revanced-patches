package app.revanced.patches.transit

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch

@Patch(
    name = "Transit",
    description = "Unlock Pro",
    compatiblePackages = [
        CompatiblePackage("com.thetransitapp.droid", ["5.15.16"]),
    ],
)
@Suppress("unused")
object TransitUnlockPatch : BytecodePatch(emptySet()) {
    override fun execute(context: BytecodeContext) {
        IsPremiumFingerprint.resolve(context,YukaUserConstructorFingerprint.result!!.classDef)
        val method = IsPremiumFingerprint.result!!.mutableMethod
        method.addInstructions(
            0,
            """
                const/4 v0, 0x1
                return v0
            """
        )
    }
}
