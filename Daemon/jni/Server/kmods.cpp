#include "kmods.h"
#include "socket.h"
#include "StructsCommon.h"
#include "FNames.h"
#include "PUBG.h"

using namespace std;

#define maxplayerCount 30
#define maxvehicleCount 20
#define maxitemsCount 20

enum Mode {
	InitMode = 1,
	ESPMode = 2,
	HackMode = 3,
	StopMode = 4,
};

struct SetValue {
    int mode;
    int type;
    
};
struct Request {
	int Mode;
	int ScreenWidth;
	int ScreenHeight;
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

static const char* vehicles[][2] = {
		{"AirDropPlane", "AirDropPlane"},
		{"BP_VH_Buggy", "Buggy"},
		{"VH_Dacia", "Dacia"},
		{"BP_VH_Tuk", "Tuk Tuk"},
		{"Rony_0", "Rony"},
		{"Mirado_close", "Mirado"},
		{"Mirado_open", "Mirado Open"},
		{"VH_UAZ", "UAZ"},
		{"VH_MiniBus", "MiniBus"},
		{"VH_Scooter", "Scooter"},
		{"VH_MotorcycleCart", "Motorcycle Cart"},
		{"VH_Motorcycle", "Motorcycle"},
		{"VH_Snowbike", "SnowBike"},
		{"VH_Snowmobile", "SnowMobile"},
		{"VH_BRDM", "BRDM"},
		{"LadaNiva_0", "LadaNiva"},
		{"PickUp_0", "PickUp Truck"},
		{"VH_PG117", "BigBoat"},
		{"AquaRail_", "AquaRail"},
};

bool FindVehicleName(const string& object, string& res) {
	for (auto &vehicle : vehicles) {
		if (isContain(object, vehicle[0])) {
			res = string(vehicle[1]);
			return true;
		}
	}
	return false;
}

static const char* impitems[][2] = {

		{"PickUp_BP_Armor_Lv3", "Armor L3"},
		{"PickUp_BP_Helmet_Lv3", "Helmet L3"},
		{"PickUp_BP_Bag_Lv3", "Bag L3"},
		{"BP_MZJ_HD_Pickup", "RedDot"},
		{"BP_MZJ_8X_Pickup", "8x"},
		{"BP_MZJ_6X_Pickup", "6x"},
		{"BP_MZJ_4X_Pickup", "4x"},
		{"BP_Ghillie_", "Ghillie"},
	
};

bool FindItemName(const string& object, string& res) {
	for (auto &items : impitems) {
		if (isContain(object, items[0])) {
			res = string(items[1]);
			return true;
		}
	}
	return false;
}

bool isOutsideSafeZone(Vector2 pos) {
	if (pos.Y < 0) {
		return true;
	}
	if (pos.X > (float)Width) {
		return true;
	}
	if (pos.Y > (float)Height) {
		return true;
	}
	return pos.X < 0;
}

void createDataList(Response& response) {
    response.Success = false;
    response.PlayerCount = 0;
    response.ItemsCount = 0;

    kaddr gworld = getRealOffset(Offsets::GWorld);
    if(gworld == 0){ return; }

    kaddr uworld = getPtr(gworld);
    if(UObject::isInvalid(uworld)){ return; }

    kaddr level = World::getPersistentLevel(uworld);
    if (UObject::isInvalid(level)) { return; }

    kaddr actorList = Level::getActorList(level);
    if (actorList == 0) { return; }

    int nearEnemy = 0;
    int localTeamID = 0;
    uint32 localPKey = 0;
    MinimalViewInfo POV = MinimalViewInfo();

    for (int i = 0; i < Level::getActorsCount(level); i++) {
        kaddr actor = Level::getActor(actorList, i);
        if (UObject::isInvalid(actor)) { continue; }

        string oname = UObject::getName(actor);

        if(isContain(oname, "BP_STExtraPlayerController")){
            localTeamID = PlayerController::getTeamID(actor);
            localPKey = PlayerController::getPlayerKey(actor);
            continue;
        }

        if(isContain(oname, "BP_PlayerCameraManager")){
            POV = PlayerCameraManager::getPOV(actor);
            continue;
        }

        if(localPKey == 0 || POV.FOV == 0){ continue; }

        kaddr RootComp = Actor::getRootComponent(actor);
        if (UObject::isInvalid(RootComp)) { continue; }

        Vector3 Location = SceneComponent::getLocation(RootComp);
        float distance = (Vector3::Distance(Location, POV.Location) / 100.0f);

        if (isContain(oname, "BP_PlayerPawn_") && !isContain(oname, "BP_PlayerPawn_Statue_")) {
            if (response.PlayerCount == maxplayerCount) {
                continue;
            }

            PlayerData* data = &response.Players[response.PlayerCount];

            if(distance > 500) { continue; }

            if(localPKey == Player::getPlayerKey(actor)){ continue;	}

            int teamID = Player::getTeamID(actor);
            if(localTeamID != teamID){
                nearEnemy++;
            }

			kaddr Mesh = Character::getMeshComponent(actor);
			if (UObject::isInvalid(Mesh)) { continue; }

			FTransform c2wTransform = SceneComponent::getComponentToWorld(Mesh);
			FMatrix c2wMatrix = TransformToMatrixWithScale(c2wTransform);

			kaddr BoneArr = SkeletalMeshComponent::getBoneArr(Mesh);
			if (BoneArr == 0) { continue; }

			FString pname = Player::getPlayerName(actor);

			wmemcpy(data->PlayerName, pname.w_str(), pname.Count);
            data->isBot = Player::IsAI(actor);
            data->TeamID = teamID;
            data->Health = Player::getHealth(actor);
            data->Distance = distance;
            data->Body = WorldToScreen(Location, POV, Width, Height);
            data->Root = WorldToScreen(Player::getBoneLoc(c2wMatrix, BoneArr, 0) - Vector3(0,0,7), POV, Width, Height);
            data->Head = WorldToScreen(Player::getBoneLoc(c2wMatrix, BoneArr, 6) + Vector3(0,0,22), POV, Width, Height);
            data->Neck = WorldToScreen(Player::getBoneLoc(c2wMatrix, BoneArr, 6) + Vector3(0,0,10), POV, Width, Height);
            data->Chest = WorldToScreen(Player::getBoneLoc(c2wMatrix, BoneArr, 5), POV, Width, Height);
            data->Pelvis = WorldToScreen(Player::getBoneLoc(c2wMatrix, BoneArr, 2), POV, Width, Height);
            data->LShoulder = WorldToScreen(Player::getBoneLoc(c2wMatrix, BoneArr, 12), POV, Width, Height);
            data->RShoulder = WorldToScreen(Player::getBoneLoc(c2wMatrix, BoneArr, 33), POV, Width, Height);
            data->LElbow = WorldToScreen(Player::getBoneLoc(c2wMatrix, BoneArr, 13), POV, Width, Height);
            data->RElbow = WorldToScreen(Player::getBoneLoc(c2wMatrix, BoneArr, 34), POV, Width, Height);
            data->LWrist = WorldToScreen(Player::getBoneLoc(c2wMatrix, BoneArr, 64), POV, Width, Height);
            data->RWrist = WorldToScreen(Player::getBoneLoc(c2wMatrix, BoneArr, 63), POV, Width, Height);
            data->LThigh = WorldToScreen(Player::getBoneLoc(c2wMatrix, BoneArr, 53), POV, Width, Height);
            data->RThigh = WorldToScreen(Player::getBoneLoc(c2wMatrix, BoneArr, 57), POV, Width, Height);
            data->LKnee = WorldToScreen(Player::getBoneLoc(c2wMatrix, BoneArr, 54), POV, Width, Height);
            data->RKnee = WorldToScreen(Player::getBoneLoc(c2wMatrix, BoneArr, 58), POV, Width, Height);
            data->LAnkle = WorldToScreen(Player::getBoneLoc(c2wMatrix, BoneArr, 55), POV, Width, Height);
            data->RAnkle = WorldToScreen(Player::getBoneLoc(c2wMatrix, BoneArr, 59), POV, Width, Height);

            response.PlayerCount++;
        } else if (isContain(oname, "PlayerDeadInventoryBox")) {
            if (response.ItemsCount == maxitemsCount) {
                continue;
            }

            Vector2 SLoc = WorldToScreen(Location, POV, Width, Height);
			if (isOutsideSafeZone(SLoc)) { continue; }

            ItemData* data = &response.Items[response.ItemsCount];

            if(distance > 500) { continue; }

            strncpy(data->Name, "LootBox", 7);
            data->isVehicle = false;
            data->isLootBox = true;
            data->isAirDrop = false;
            data->isLootItem = false;
            data->Distance = distance;
            data->Location = SLoc;

            response.ItemsCount++;
        } else if(isContain(oname, "AirDropBox")) {
            if (response.ItemsCount == maxitemsCount) {
                continue;
            }

            Vector2 SLoc = WorldToScreen(Location, POV, Width, Height);
            if (isOutsideSafeZone(SLoc)) { continue; }

            if(distance > 1000) { continue; }

            ItemData* data = &response.Items[response.ItemsCount];

            strncpy(data->Name, "AirDrop", 6);
            data->isVehicle = false;
            data->isLootBox = false;
            data->isAirDrop = true;
            data->isLootItem = false;
            data->Distance = distance;
            data->Location = SLoc;

            response.ItemsCount++;
        } else {
            string itemName;
            if(FindVehicleName(oname, itemName)){
                if (response.ItemsCount == maxitemsCount) {
                    continue;
                }

				Vector2 SLoc = WorldToScreen(Location, POV, Width, Height);
				if (isOutsideSafeZone(SLoc)) { continue; }

                if(distance < 10 || distance > 800) { continue; }

                ItemData* data = &response.Items[response.ItemsCount];

                strncpy(data->Name, itemName.data(), itemName.size());
                data->isVehicle = true;
                data->isLootBox = false;
                data->isAirDrop = false;
                data->isLootItem = false;
                data->Distance = distance;
                data->Location = SLoc;

                response.ItemsCount++;
            } else if(FindItemName(oname, itemName)){
                if (response.ItemsCount == maxitemsCount) {
                    continue;
                }

				Vector2 SLoc = WorldToScreen(Location, POV, Width, Height);
				if (isOutsideSafeZone(SLoc)) { continue; }

                if(distance > 800) { continue; }

                ItemData* data = &response.Items[response.ItemsCount];

                strncpy(data->Name, itemName.data(), itemName.size());
                data->isVehicle = false;
                data->isLootBox = false;
                data->isAirDrop = false;
                data->isLootItem = true;
                data->Distance = distance;
                data->Location = SLoc;

                response.ItemsCount++;
            }
        }
    }

    response.Success = true;
    response.NearEnemy = nearEnemy;
    response.MyTeamID = localTeamID;
}
SetValue sv{};
int main(int argc, char*argv[]){
        
    if (!Create()){
        LOGE("[Server] Socket can't create");
        return -1;
    }
    if (!Connect()){
        LOGE("[Server] Socket can't connect");
        return -1;
    }
    LOGI("[Server] Socket connected");
     int no;


    receive((void*)&sv);
    no = sv.mode;    
           
           if (no == 1) {
            Offsets::globalOffsets();
            target_pid = find_pid("com.tencent.ig");
        } else if (no == 2) {
             Offsets::koreaOffsets();
            target_pid = find_pid("com.pubg.krmobile");          
        } else if (no == 3) {
             Offsets::vietnamOffsets();
            target_pid = find_pid("com.vng.pubgmobile");
        } else if (no == 4) {
             Offsets::taiwanOffsets();
            target_pid = find_pid("com.rekoo.pubgm");              
            
        }
        if (Height > Width)
        {
            int t = Width;
            Width = Height;
            Height = t;
        }
        LOGI("Device screen:");
        LOGI("Width : %d", (int) Width);
        LOGI("Height : %d", (int) Height);
        if (target_pid < 0){
            LOGE("[Server] Can't get game pid");
            exit(1);
        }
        libbase = get_module_base("libUE4.so");
        if (libbase < 0){
            LOGE("[Server] Can't get game base");
            exit(1);
        }
    Request request{};
    while ((receive((void*)&request) > 0)){
        
        Width = request.ScreenWidth;
        Height = request.ScreenHeight;
        Response response{};
        response.Success = true;
        response.PlayerCount = 0;
        response.ItemsCount = 0;
        createDataList(response);
        send((void*)&response, sizeof(response));
    }
    Close();
    return 0;
}





