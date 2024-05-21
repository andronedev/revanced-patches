package app.revanced.patches.transit.misc.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

/*
.method public static fetchSubscriptionStatus(ZJ)V
    .locals 20

    .line 1
    new-instance v0, Lcom/thetransitapp/droid/shared/core/service/CppCallbackRef;

    .line 2
    .line 3
    move-wide/from16 v1, p1

    .line 4
    .line 5
    invoke-direct {v0, v1, v2}, Lcom/thetransitapp/droid/shared/core/service/CppCallbackRef;-><init>(J)V

    .line 6
    .line 7
    .line 8
    sget-object v1, Lcom/thetransitapp/droid/shared/data/TransitLib;->n:Lcom/thetransitapp/droid/shared/data/TransitLib;

    .line 9
    .line 10
    const-string v2, "ForceHasActiveRoyaleSubscription"

    .line 11
    .line 12
    invoke-static {v2}, Lcom/thetransitapp/droid/shared/data/TransitLib;->getEnvironmentVariableBooleanValue(Ljava/lang/String;)Z

    .line 13
    .line 14
    .line 15
    move-result v2

    .line 16
    if-eqz v2, :cond_0

    .line 17
    .line 18
    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J

    .line 19
    .line 20
    .line 21
    move-result-wide v11

    .line 22
    const-wide/32 v1, 0x493e0

    .line 23
    .line 24
    .line 25
    add-long/2addr v1, v11

    .line 26
    new-instance v15, Lcom/thetransitapp/droid/shared/model/cpp/royale/RoyaleStoreSubscriptionStatus;

    .line 27
    .line 28
    const/16 v3, 0x2710

    .line 29
    .line 30
    int-to-long v3, v3

    .line 31
    sub-long v9, v11, v3

    .line 32
    .line 33
    const/16 v3, 0x3e8

    .line 34
    .line 35
    int-to-long v3, v3

    .line 36
    div-long v13, v1, v3

    .line 37
    .line 38
    const/4 v7, 0x0

    .line 39
    const/4 v8, 0x1

    .line 40
    const-string v4, "product1234"

    .line 41
    .line 42
    const/4 v5, 0x1

    .line 43
    const/4 v6, 0x1

    .line 44
    const-wide/16 v1, 0x0

    .line 45
    .line 46
    const-wide/16 v17, 0x0

    .line 47
    .line 48
    move-object v3, v15

    .line 49
    move-object/from16 v19, v15

    .line 50
    .line 51
    move-wide v15, v1

    .line 52
    invoke-direct/range {v3 .. v18}, Lcom/thetransitapp/droid/shared/model/cpp/royale/RoyaleStoreSubscriptionStatus;-><init>(Ljava/lang/String;ZZZIJJJJJ)V

    .line 53
    .line 54
    .line 55
    sget-object v1, Lcom/thetransitapp/droid/shared/data/TransitLib;->m:Lcom/google/gson/f;

    .line 56
    .line 57
    move-object/from16 v2, v19

    .line 58
    .line 59
    invoke-virtual {v1, v2}, Lcom/google/gson/f;->f(Ljava/lang/Object;)Ljava/lang/String;

    .line 60
    .line 61
    .line 62
    move-result-object v1

    .line 63
    invoke-static {v0, v1}, Lcom/thetransitapp/droid/shared/data/TransitLib;->sendJsonData(Lcom/thetransitapp/droid/shared/core/service/CppCallbackRef;Ljava/lang/String;)V

    .line 64
    .line 65
    .line 66
    return-void

    .line 67
    :cond_0
    if-eqz v1, :cond_1

    .line 68
    .line 69
    new-instance v1, Lcom/thetransitapp/droid/shared/data/p;

    .line 70
    .line 71
    const/4 v2, 0x0

    .line 72
    invoke-direct {v1, v0, v2}, Lcom/thetransitapp/droid/shared/data/p;-><init>(Lcom/thetransitapp/droid/shared/core/service/CppCallbackRef;I)V

    .line 73
    .line 74
    .line 75
    move/from16 v0, p0

    .line 76
    .line 77
    invoke-static {v1, v0}, Lcom/thetransitapp/droid/shared/core/g;->c(LP6/k;Z)V

    .line 78
    .line 79
    .line 80
    :cond_1
    return-void
.end method
 */
internal object IsPremiumFingerprint : MethodFingerprint(
    // this methode is used to find the fingerprint of the method fetchSubscriptionStatus
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    customFingerprint = { methodDef, _ ->
        methodDef.name == "fetchSubscriptionStatus"
    },
)