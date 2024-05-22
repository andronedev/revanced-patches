package app.revanced.patches.geniusscan.misc.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

object GetRequiresPaidPlanFingerprint : MethodFingerprint(
    returnType = "Z", // Méthode retournant un booléen
    accessFlags = AccessFlags.PUBLIC.or(AccessFlags.FINAL),
    parameters = emptyList(), // Pas de paramètres pour cette méthode
    opcodes = listOf(
        Opcode.IGET_BOOLEAN, // Récupération d'un booléen
        Opcode.RETURN // Retourne le booléen
    ),
    strings = emptyList() // Pas de chaînes spécifiques à rechercher
)
