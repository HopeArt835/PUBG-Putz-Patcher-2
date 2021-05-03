#ifndef KONTOL_H
#define KONTOL_H

#include <jni.h>
#include <string>
#include <cstdlib>
#include <unistd.h>
#include <sys/mman.h>
#include <android/log.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <cerrno>
#include <sys/un.h>
#include <cstring>
#include <string>
#include <cmath>
#include "StructsCommon.h"
#include "Log.h"

bool isESP = false;
//Player
bool isPlayerName = false;
bool isPlayerDist = false;
bool isPlayerHealth = false;
bool isTeamMateShow = false;
bool isPlayerBox = false;
bool isPlayerLine = false;
bool isPlayer360 = false;
bool isNearEnemy= false;
bool isPlayerSkel= false;
float playerTextSize = 15;
//Items
bool isVehicle = false;
bool isLootBox = false;
bool isAirDrop = false;
bool isLootItems = false;
bool isItemName = false;
bool isItemDist = false;
bool  isIgnoreBots = false;
float itemTextSize = 15;

#endif //KONTOL_H
