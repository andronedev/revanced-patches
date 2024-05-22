package app.revanced.patches.flightradar24.ad.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

internal object AdEnabledFingerprint : MethodFingerprint(
    returnType = "Z",
    customFingerprint = { methodDef, _ ->
        methodDef.name == "isAdvertsEnabled"
    },
    strings = setOf("enabled"),
)