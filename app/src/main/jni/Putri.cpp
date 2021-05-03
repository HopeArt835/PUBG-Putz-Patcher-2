#include <jni.h>
#include <string>
#include "ESP.h"
#include "Hacks.h"

ESP espOverlay;
int type=1,utype=2;
extern "C" JNIEXPORT void JNICALL
Java_com_tencent_qq_Overlay_DrawOn(JNIEnv *env, jclass , jobject espView, jobject canvas) {
    espOverlay = ESP(env, espView, canvas);
    if (espOverlay.isValid()){
        DrawESP(espOverlay, espOverlay.getWidth(), espOverlay.getHeight());
    }
}
extern "C" JNIEXPORT void JNICALL
Java_com_tencent_qq_Overlay_Close(JNIEnv *,  jobject ) {
   Close();
}
extern "C" JNIEXPORT void JNICALL
Java_com_tencent_qq_FloatLogo_SettingValue(JNIEnv *,  jobject ,jint code,jboolean jboolean1) {
    switch((int)code){
        case 0:
            isESP = jboolean1;
            break;
        case 1:
            isPlayerName = jboolean1;
            break;
        case 2:
            isPlayerHealth = jboolean1;
            break;
        case 3:
            isPlayerDist = jboolean1;
            break;
        case 4:
            isTeamMateShow = jboolean1;
            break;
        case 5:
            isPlayerLine = jboolean1;
            break;
        case 6:
            isPlayerBox = jboolean1;
            break;
        case 7:
            isPlayer360 = jboolean1;
            break;
        case 8:
            isNearEnemy = jboolean1;
            break;
        case 9:
            isItemName = jboolean1;
            break;
        case 10:
            isItemDist = jboolean1;
            break;
        case 11:
            isVehicle = jboolean1;
            break;
        case 12:
            isLootBox = jboolean1;
            break;
        case 13:
            isLootItems = jboolean1;
            break;
        case 14:
            isPlayerSkel = jboolean1;
            break;
        case 15:
            isAirDrop = jboolean1;
            break;
        case 16:
            isIgnoreBots = jboolean1;
            break;

    }
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_tencent_qq_Overlay_getReady(JNIEnv *, jclass ,int typeofgame) {
    int sockCheck=1;
    if (!Create()) {
        perror("Creation failed");
        return false;
    }
    setsockopt(sock,SOL_SOCKET,SO_REUSEADDR,&sockCheck, sizeof(int));
    if (!Bind()) {
        perror("Bind failed");
        return false;
    }

    if (!Listen()) {
        perror("Listen failed");
        return false;
    }
    if (Accept()) {
        SetValue sv{};
        sv.mode=typeofgame;
        sv.type=utype;
        send((void*)&sv,sizeof(sv));
       
        return true;
    }

}


