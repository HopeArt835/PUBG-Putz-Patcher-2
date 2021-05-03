#ifndef STRUCTSCOMM_H
#define STRUCTSCOMM_H

#include "Vector3.h"
#include "Vector2.h"
#include "Rect.h"

using namespace std;

#define maxplayerCount 30
#define maxitemsCount 40

enum Mode {
	InitMode = 1,
	ESPMode = 2,
	HackMode = 3,
	StopMode = 4,
	MemMode = 5,
};

struct Request {
	int Mode;
	int ScreenWidth;
	int ScreenHeight;
	int HackNum;
};
struct SetValue {
    int mode;
    int type;
};
struct PlayerData {
	wchar_t PlayerName[30];
	bool isBot;
	int TeamID;
	float Health;
	float Distance;
	Vector2 Body;
	Vector2 Root;
	Vector2 Head;
	Vector2 Neck;
	Vector2 Chest;
	Vector2 Pelvis;
	Vector2 LShoulder;
	Vector2 RShoulder;
	Vector2 LElbow;
	Vector2 RElbow;
	Vector2 LWrist;
	Vector2 RWrist;
	Vector2 LThigh;
	Vector2 RThigh;
	Vector2 LKnee;
	Vector2 RKnee;
	Vector2 LAnkle;
	Vector2 RAnkle;
};

struct ItemData {
	char Name[20];
	bool isVehicle;
	bool isLootBox;
	bool isAirDrop;
	bool isLootItem;
	float Distance;
	Vector2 Location;
};

struct Response {
	bool Success;
	int NearEnemy;
	int MyTeamID;
	int PlayerCount;
	int ItemsCount;
	PlayerData Players[maxplayerCount];
	ItemData Items[maxitemsCount];
};




#endif
