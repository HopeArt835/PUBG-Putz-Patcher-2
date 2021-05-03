#ifndef FNAMES_H
#define FNAMES_H

#include "kmods.h"

using namespace std;

map<int, string> gnameCache;

bool isCached(uint32 data, string& res) {
	for (map<int, string>::iterator itr = gnameCache.begin(); itr != gnameCache.end(); ++itr) {
		if(itr->first == data){
			res = itr->second;
			return true;
		}
	}
	return false;
}

string getUEString(kaddr address) {
	unsigned int MAX_SIZE = 100;

	string uestring(ReadStr(address, MAX_SIZE));
	uestring.shrink_to_fit();

	return uestring;
}

string GetFNameFromID(uint32 index) {
	string cached;
	if(isCached(index, cached)){
		return cached;
	}

	kaddr TNameEntryArray = getPtr(getRealOffset(Offsets::GNames));

	kaddr FNameEntryArr = getPtr(TNameEntryArray + ((index / 0x4000) * Offsets::PointerSize));
	kaddr FNameEntry = getPtr(FNameEntryArr + ((index % 0x4000) * Offsets::PointerSize));

	string str = getUEString(FNameEntry + Offsets::FNameEntryToNameString);
	gnameCache.insert(pair<int, string>(index, str));

	return str;
}

#endif